package mrrexz.github.com.downcachedroid.model.downfiles;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import mrrexz.github.com.downcachedroid.model.caching.CacheDroid;
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

    public DownImageFile() {
        super("image");
    }

    @Override
    public Object get(String url) {
        InputStream inStream = CacheDroid.getDataFromCache(url);
        if (inStream == null) return null;
        Bitmap bitmap = BitmapFactory.decodeStream(inStream);
        return bitmap;
    }

    @Override
    public void download(String url) throws IOException {
        Call call = DownloadProcDroid.standardDownload(url, new Callback() {
            @Override
            public void onFailure(final Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                InputStream resStream = response.body().byteStream();
                Log.d("DownedFiles", url);
                CacheDroid.insertToCache(url, resStream, DownImageFile.this);
            }
        });
        urlCalls.put(url, call);
    }

}
