package mrrexz.github.com.downcachedroid.model.downfiles;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

import mrrexz.github.com.downcachedroid.helper.BitmapHelper;
import mrrexz.github.com.downcachedroid.model.caching.CacheDroid;
import mrrexz.github.com.downcachedroid.controller.download.DownloadProcDroid;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by antho on 7/19/2017.
 */

public class ImageDownFile extends BaseDownFile {

    public ImageDownFile() {
        super("image");
    }

    @Override
    public Object get(String url) {
        byte[] inStream = CacheDroid.getDataFromCache(url);
        if (inStream == null) return null;
        Bitmap bitmap = BitmapHelper.decodeSampledBitmapFromStream(url, new Rect(10,10,10,10), 100, 100);
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
                InputStream is = response.body().byteStream();
                byte[] bytesData = IOUtils.toByteArray(is);
                Log.d("Downloaded File :", url);
                CacheDroid.insertToCache(url, bytesData, ImageDownFile.this);
            }
        });
        urlCalls.put(url, call);
    }

}
