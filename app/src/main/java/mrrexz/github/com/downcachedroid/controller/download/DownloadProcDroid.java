package mrrexz.github.com.downcachedroid.controller.download;

import android.support.v4.util.Pair;
import android.util.Patterns;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

import mrrexz.github.com.downcachedroid.helper.GenericCallback;
import mrrexz.github.com.downcachedroid.model.caching.CacheDroid;
import mrrexz.github.com.downcachedroid.model.downfiles.BaseDownFile;
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

public class DownloadProcDroid {

    static OkHttpClient client = createOkHttpClient();

    private static OkHttpClient createOkHttpClient() {
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(100);
        dispatcher.setMaxRequestsPerHost(40);
        return new OkHttpClient.Builder()
                .dispatcher(dispatcher)
                .connectionPool(new ConnectionPool(40 ,15000, TimeUnit.MILLISECONDS))
                .build();

    }
    public static Call getWebResLinks(String url, GenericCallback<List<String>> successCallback) throws IOException {
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

    public static Call analyzeMimeType(final String url, GenericCallback<MediaType> successCallback) {
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

    private static List<String> extractLinks(String text) {
        List<String> urlLinks = new ArrayList<String>();
        Matcher urlMatcher = Patterns.WEB_URL.matcher(text);
        while (urlMatcher.find()) {
            String url = urlMatcher.group();
            urlLinks.add(url);
        }
        return urlLinks;
    }

    public static void process(List<String> urls, Set<BaseDownFile> supportedDownTypes) {
        Map<String, BaseDownFile> mimeDownObjMap = new ConcurrentHashMap<>();
        supportedDownTypes.stream().parallel().forEach(supportedType -> {
            mimeDownObjMap.put(supportedType.MIME, supportedType);
        });
        urls.forEach( url -> {
            DownloadProcDroid.analyzeMimeType(url, new GenericCallback<MediaType>() {
                @Override
                public void onValue(MediaType mediaType) throws IOException {
                    BaseDownFile downObjType = mimeDownObjMap.get(mediaType.type());
                    if (downObjType != null) {
                        InputStream cachedVal = CacheDroid.getDataFromCache(url);
                        if (cachedVal == null) {
                            downObjType.download(url);
                        }
                    }
                }
            });
        });
    }

    public static Call standardDownload(String url, Callback callback)  {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);

        return call;
    }
}
