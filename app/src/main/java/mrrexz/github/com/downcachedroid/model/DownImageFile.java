package mrrexz.github.com.downcachedroid.model;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

import mrrexz.github.com.downcachedroid.controller.caching.CacheDroid;
import mrrexz.github.com.downcachedroid.controller.download.DownloadProcDroid;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by antho on 7/19/2017.
 */

public class DownImageFile extends BaseDownFile {
    OkHttpClient client = new OkHttpClient();

    public DownImageFile(Activity act, CacheDroid cacheDroid) {
        super(act, cacheDroid, "image");
    }

    void download(String url) throws IOException {

        DownloadProcDroid.analyzeMimeType(url, (mediaType) -> {
            if (mediaType.type().equals(MIME)){
                Call call = call(url);
                urlCalls.put(url, call);
            }
        });

    }

    Call call(final String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                InputStream resStream = response.body().byteStream();
                cacheDroid.insertToCache(url, resStream);
            }
        });

        return call;
    }

    @Override
    public Object get(String url){
        InputStream inStream = cacheDroid.getFromCache(url);
        Bitmap bitmap = BitmapFactory.decodeStream(inStream);
        return bitmap;
    }

}
