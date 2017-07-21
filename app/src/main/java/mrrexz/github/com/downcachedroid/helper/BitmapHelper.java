package mrrexz.github.com.downcachedroid.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import mrrexz.github.com.downcachedroid.model.caching.CacheDroid;

/**
 * Created by antho on 7/20/2017.
 */

public class BitmapHelper {

    static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromStream(String key_url, Rect outPadding,
                                                       int reqWidth, int reqHeight) {


        byte[] bytesImage = CacheDroid.getDataFromCache(key_url);
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytesImage, 0, bytesImage.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Log.d("BITMAP Helper", "New Stream input stream");
        return BitmapFactory.decodeByteArray(bytesImage, 0, bytesImage.length, options);
    }
}
