package mrrexz.github.com.downcachedroid.model.downfiles;

import android.graphics.Bitmap;

import mrrexz.github.com.downcachedroid.helper.BitmapHelper;

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
    }

    @Override
    public Object convertDownloadedData(byte[] networkInput) {
        return BitmapHelper.decodeSampledBitmapFromBytes(networkInput, null, 150, 150);
    }

    @Override
    public int determineSizeInCache(Object data) {
        if (data == null){
            return 0;
        }
        Bitmap bitmap = (Bitmap) data;
        return bitmap.getByteCount();
    }
}
