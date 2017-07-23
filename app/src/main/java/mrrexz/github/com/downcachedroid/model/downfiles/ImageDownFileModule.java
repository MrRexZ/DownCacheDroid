package mrrexz.github.com.downcachedroid.model.downfiles;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.BiFunction;
import java.util.function.Function;

import mrrexz.github.com.downcachedroid.helper.BitmapHelper;
import mrrexz.github.com.downcachedroid.helper.GenericCallback;
import mrrexz.github.com.downcachedroid.model.caching.CacheDroidModule;
import mrrexz.github.com.downcachedroid.controller.download.DownloadProcDroid;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by antho on 7/19/2017.
 */

public class ImageDownFileModule extends BaseDownFileModule {

    public ImageDownFileModule() {
        super("image");
    }

    @Override
    public Object getConvertedData(Object data) {
        if (data == null) return null;
        return (Bitmap) data;
//        return BitmapHelper.decodeSampledBitmapFromBytes(data, new Rect(10, 10, 10, 10), 250, 250).get();
    }

    @Override
    public void download(Function<BaseDownFileModule, Call> standardDownload) throws IOException {
        Call call = standardDownload.apply(ImageDownFileModule.this);
    }

    @Override
    public Object convertProc(byte[] networkInput) {
        return BitmapHelper.decodeSampledBitmapFromBytes(networkInput, null, 150, 150);
    }
}
