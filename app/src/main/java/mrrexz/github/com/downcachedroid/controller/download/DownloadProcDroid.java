package mrrexz.github.com.downcachedroid.controller.download;

import android.util.Log;
import android.util.Patterns;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;

import javax.inject.Inject;
import javax.inject.Singleton;

import mrrexz.github.com.downcachedroid.helper.GenericCallback;
import mrrexz.github.com.downcachedroid.model.caching.CacheDroidModule;
import mrrexz.github.com.downcachedroid.model.downfiles.BaseDownFileModule;
import mrrexz.github.com.downcachedroid.model.downfiles.ImageDownFileModule;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by antho on 7/20/2017.
 */

@Singleton
public class DownloadProcDroid {

    private String TAG = "DownloadProcDroid";
    static OkHttpClient client = createOkHttpClient();
    static OkHttpClient downClient = createOkHttpClient();
    private static OkHttpClient createOkHttpClient() {
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(70);
        dispatcher.setMaxRequestsPerHost(20);
        return new OkHttpClient.Builder()
                .dispatcher(dispatcher)
                .connectionPool(new ConnectionPool(20 ,15000, TimeUnit.MILLISECONDS))
                .build();
    }
    public final CacheDroidModule cacheDroidModule;


    @Inject
    public DownloadProcDroid(CacheDroidModule cacheDroidModule) {
        this.cacheDroidModule = cacheDroidModule;
    }


//    public Call cacheWebContents(String url) {
//        try {
//            Call cacheWebContentsCall = getWebResLinks(url, this::cache);
//            return cacheWebContentsCall;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    public Call getWebResLinks(String url, GenericCallback<List<String>> successCallback) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, IOException e) {

            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String pageRes = response.body().string();
                successCallback.onValue(extractLinks(pageRes));
            }
        });

        return call;
    }

    public Call asyncGetUrlMimeType(final String url, GenericCallback<MediaType> successCallback) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, IOException e) {

            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                MediaType contentType = MediaType.parse(response.header("Content-Type"));
                successCallback.onValue(contentType);
            }
        });
        return call;
    }

    public void asyncGetUrlsWithMimeType(String targetMime, String[] urls, GenericCallback<String> successCallback){
        for (int i = 0 ; i< urls.length ; i++) {
            asyncGetUrlWithMimeType(targetMime, urls[i], successCallback);
        }
    }

    public void asyncGetUrlWithMimeType(String targetMime, String url, GenericCallback<String> successCallback) {
        asyncGetUrlMimeType(url, new GenericCallback<MediaType>() {
            @Override
            public void onValue(MediaType value) throws IOException {
                if (value.type().equals(targetMime)){
                    successCallback.onValue(url);
                }
            }
        });
    }

    public MediaType syncIdentifyMime(String url) throws ExecutionException, InterruptedException {
        Callable<MediaType> callable = new Callable<MediaType>() {
            @Override
            public MediaType call() throws Exception {
                    Request request = new Request.Builder()
                            .url(url)
                            .build();
                    Response response = client.newCall(request).execute();
                    MediaType contentType = MediaType.parse(response.header("Content-Type"));
                    return contentType;
                }

        };

        FutureTask<MediaType> futureTask = new FutureTask<MediaType>(callable);
        Thread thread = new Thread(futureTask);
        return futureTask.get();
    }

    public List<String> retrieveUrlsWithMimeType(String targetMIME, String[] urls) throws ExecutionException, InterruptedException {

        ExecutorService executor = Executors.newCachedThreadPool();
        Callable<List<String>> callable = new Callable<List<String>>() {
            @Override
            public List<String> call() throws Exception {
                List<String> filteredURL = new ArrayList<>();
                for (int i = 0 ; i< urls.length ; i++) {
                    Request request = new Request.Builder()
                            .url(urls[i])
                            .build();
                    Response response = client.newCall(request).execute();
                    MediaType contentType = MediaType.parse(response.header("Content-Type"));
                    if (contentType.type().equals(targetMIME))
                        filteredURL.add(urls[i]);

                }

                return filteredURL;
            }
        };
        Future<List<String>> future =  executor.submit(callable);
        return future.get();
    }

    private List<String> extractLinks(String text) {
        List<String> urlLinks = new ArrayList<String>();
        Matcher urlMatcher = Patterns.WEB_URL.matcher(text);
        while (urlMatcher.find()) {
            String url = urlMatcher.group();
            urlLinks.add(url);
        }
        return urlLinks;
    }

    public void cache(List<String> urls) {
        Map<String, BaseDownFileModule> mimeDownObjMap = new ConcurrentHashMap<>();
        cacheDroidModule.supportedDownTypes.stream().forEach(supportedType -> {
            mimeDownObjMap.put(supportedType.MIME, supportedType);
        });

        urls.forEach( url -> {
            asyncGetUrlMimeType(url, new GenericCallback<MediaType>() {
                @Override
                public void onValue(MediaType mediaType) throws IOException {
                    BaseDownFileModule downObjType = mimeDownObjMap.get(mediaType.type());
                    if (downObjType != null) {
                        byte[] cachedVal = cacheDroidModule.getDataFromCache(url);
                        if (cachedVal == null) {
                            downObjType.download(standardDownload(), url);
                        }
                    }
                }
            });
        });
    }



    public BiFunction<String, BaseDownFileModule, Call> standardDownload() {
        return (String url, BaseDownFileModule fileType) -> {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Call call = downClient.newCall(request);
            call.enqueue(normalSuccessCallback(url, fileType));
            return call;
        };
    }

    Callback normalSuccessCallback(String url, BaseDownFileModule fileType) {
        return new Callback() {
            @Override
            public void onFailure(final Call call, IOException e) {
                Log.d(TAG, "On Failure : Redownloading..."  + e.getMessage());
                call.clone().enqueue(normalSuccessCallback(url, fileType));
            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                try {
                    byte[] bytesData = response.body().bytes();
                    Log.d(TAG, url);
                    cacheDroidModule.insertToCache(url, bytesData, fileType);
                    //afterCache.onValue(url);
                }
                catch (IOException e) {
                    Log.d(TAG, " IOException : Redownloading...");
                    call.clone().enqueue(normalSuccessCallback(url, fileType));
                }

            }
        };
    }


}
