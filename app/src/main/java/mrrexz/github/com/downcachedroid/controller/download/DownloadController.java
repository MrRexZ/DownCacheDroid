package mrrexz.github.com.downcachedroid.controller.download;

import android.util.Log;
import android.util.Patterns;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;

import javax.inject.Inject;
import javax.inject.Singleton;

import mrrexz.github.com.downcachedroid.helper.GenericCallback;
import mrrexz.github.com.downcachedroid.model.caching.CacheDroidModule;
import mrrexz.github.com.downcachedroid.model.downfiles.BaseDownFileModule;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by antho on 7/20/2017.
 */

@Singleton
public class DownloadController {

    static OkHttpClient client = new OkHttpClient();
    public final CacheDroidModule cacheDroidModule;
    private String TAG = "DownloadController";
    private ConcurrentHashMap<String, Call> activeDownloadCall = new ConcurrentHashMap<>();
    private Set<String> failedDownloads = ConcurrentHashMap.newKeySet();
    private Set<String> webPagesVisited = ConcurrentHashMap.newKeySet();

    @Inject
    public DownloadController(CacheDroidModule cacheDroidModule) {
        this.cacheDroidModule = cacheDroidModule;
    }

    public Call cacheWebContents(String url) {
        try {
            Call cacheWebContentsCall = getWebResLinks(url, (urls) -> {
                downloadAndCache(urls);
            });
            return cacheWebContentsCall;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Call getWebResLinks(String url, GenericCallback<List<String>> successCallback) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();


        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, IOException e) {
                Log.d(TAG, "Web pages content fetch failure!");
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String pageRes = response.body().string();
                addVisitedWebPage(url);
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

        Log.w(TAG, "Request BEFORE " + client.dispatcher().queuedCallsCount());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, IOException e) {
                Log.e(TAG, "Failure download : " + e.getMessage());
                failedDownloads.add(url);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                MediaType contentType = MediaType.parse(response.header("Content-Type"));
                byte[] result = response.body().bytes();
                successCallback.onValue(contentType);
            }
        });
        return call;
    }


    public void asyncGetUrlsWithMimeType(String targetMime, String[] urls, GenericCallback<String> successCallback) {
        for (int i = 0; i < urls.length; i++) {
            asyncGetUrlWithMimeType(targetMime, urls[i], successCallback);
        }
    }

    public void asyncGetUrlWithMimeType(String targetMime, String url, GenericCallback<String> successCallback) {
        asyncGetUrlMimeType(url, new GenericCallback<MediaType>() {
            @Override
            public void onValue(MediaType value) throws IOException {
                if (value.type().equals(targetMime)) {
                    successCallback.onValue(url);
                }
            }
        });
    }

    public MediaType syncIdentifyMime(String url) throws ExecutionException, InterruptedException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            MediaType contentType = MediaType.parse(response.header("Content-Type"));
            return contentType;
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "MimeType cannot be identified in the HTTP header!");
        return null;
    }

    public List<String> retrieveUrlsWithMimeType(String targetMIME, String[] urls) throws ExecutionException, InterruptedException {

        ExecutorService executor = Executors.newCachedThreadPool();
        Callable<List<String>> callable = new Callable<List<String>>() {
            @Override
            public List<String> call() throws Exception {
                List<String> filteredURL = new ArrayList<>();
                for (int i = 0; i < urls.length; i++) {
                    Request request = new Request.Builder()
                            .url(urls[i])
                            .build();
                    Response response = client.newCall(request).execute();
                    MediaType contentType = MediaType.parse(response.header("Content-Type"));
                    if (contentType.type().equals(targetMIME)) {
                        filteredURL.add(urls[i]);
                    }
                }

                return filteredURL;
            }
        };
        Future<List<String>> future = executor.submit(callable);
        return future.get();
    }

    private List<String> extractLinks(String text) {
        Set<String> urlLinks = new HashSet<String>();
        Matcher urlMatcher = Patterns.WEB_URL.matcher(text);
        while (urlMatcher.find()) {
            String url = urlMatcher.group();
            if (!urlLinks.contains(url)) {
                urlLinks.add(url);
            }
        }
        return new ArrayList<String>(urlLinks);
    }

    public void downloadAndCache(List<String> urls) {
        for (String url : urls) {
            Log.d(TAG, "starting to download...");
            Call downloadCall = asyncDownload(url);
            if (downloadCall != null) {
                activeDownloadCall.put(url, downloadCall);
            }

        }
    }

    public boolean downloadInProgress(String url) {
        return activeDownloadCall.get(url) != null;
    }

    public Call asyncDownload(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);
        call.enqueue(standardCache(url));
        return call;

    }

    private Callback standardCache(String url) {
        return new Callback() {
            @Override
            public void onFailure(final Call call, IOException e) {
                Log.e(TAG, "Download Failure " + e.getMessage());
                activeDownloadCall.remove(url);
                failedDownloads.add(url);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                try {
                    Log.d(TAG, "QUEUE SIZE : " + client.dispatcher().queuedCallsCount());
                    BaseDownFileModule downObjType = cacheDroidModule.getSupportedTypeWrapper(response.body().contentType().type());
                    byte[] bytesData = response.body().bytes();
                    if (downObjType != null) {
                        Object cachedVal = cacheDroidModule.getConvertedDataFromCache(url);
                        if (cachedVal == null) {
                            Object convertedType = downObjType.convertDownloadedData(bytesData);
                            Log.d(TAG, "Download successful : " + url);
                            cacheDroidModule.insertToCache(url, convertedType, downObjType);
                            if (failedDownloads.contains(url)) {
                                Log.d(TAG, "Previous failed download is downloaded successfully");
                                failedDownloads.remove(url);
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, " Response not converted : " + e.getMessage());
                    failedDownloads.add(url);
                } finally {
                    activeDownloadCall.remove(url);
                }

            }
        };
    }

    public ConcurrentHashMap<String, Call> asyncRedownloadFailedAll() {
        List<String> failedDownloadUrls = new ArrayList<>(failedDownloads);
        ConcurrentHashMap<String, Call> redownloadCall = new ConcurrentHashMap<>();
        failedDownloadUrls.forEach(failedDownloadUrl -> {
            redownloadCall.put(failedDownloadUrl, asyncDownload(failedDownloadUrl));
            Log.d(TAG, "Redownloading : " + failedDownloadUrl);
        });
        return redownloadCall;
    }


    public ConcurrentHashMap<String, Call> asyncRedownloadAll(GenericCallback<List<String>> successCallback) {
        List<String> visitedWebPages = getAllVisitedWebPages();
        ConcurrentHashMap<String, Call> redownloadAllCall = new ConcurrentHashMap<>();
        visitedWebPages.forEach(visitedWebPage -> {
            try {
                redownloadAllCall.put(visitedWebPage, getWebResLinks(visitedWebPage, successCallback));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return redownloadAllCall;
    }


    public void addVisitedWebPage(String url) {
        if (!webPagesVisited.contains(url)) {
            webPagesVisited.add(url);
        }
    }

    public void removeVisitedWebPage(String url) {
        if (webPagesVisited.contains(url)) {
            webPagesVisited.remove(url);
        }
    }

    public void cancelDownload(String url) {
        if (activeDownloadCall.contains(url)) {
            activeDownloadCall.get(url).cancel();
        }
    }

    public List<String> getAllVisitedWebPages() {
        return new ArrayList<String>(webPagesVisited);
    }


}
