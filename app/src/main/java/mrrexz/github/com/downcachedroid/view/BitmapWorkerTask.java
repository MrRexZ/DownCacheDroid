package mrrexz.github.com.downcachedroid.view;

/**
 * Created by antho on 7/20/2017.
 */

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import mrrexz.github.com.downcachedroid.helper.AsyncDrawable;
import mrrexz.github.com.downcachedroid.helper.BitmapHelper;
import mrrexz.github.com.downcachedroid.model.caching.CacheDroidModule;

public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
    public static final String TAG = BitmapWorkerTask.class.getName();

    private String key_url;

    private final WeakReference<ImageView> imageViewReference;
    private CacheDroidModule cacheDroidModule;
    public BitmapWorkerTask(ImageView imageView, CacheDroidModule cacheDroidModule) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<>(imageView);
        this.cacheDroidModule = cacheDroidModule;
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(String... params) {
        key_url = params[0];
        while (cacheDroidModule.getDataFromCache(key_url) == null) {}
        return decodeBitmapFromCache(key_url);
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        Log.d("OnPostExecute", "Done");
        if (isCancelled()) {
            bitmap = null;
        }

        if (bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
            imageView.setImageBitmap(bitmap);
//            if (this == bitmapWorkerTask && imageView != null) {
//                imageView.setImageBitmap(bitmap);
//            }
        }
    }

    public Bitmap decodeBitmapFromCache(String key_url) {
        while (cacheDroidModule.getDataFromCache(key_url) == null){}
        Log.d("BITMAP WorkerTask", "Decoding...");
        return BitmapHelper.decodeSampledBitmapFromBytes(cacheDroidModule.getDataFromCache(key_url), new Rect(10,10,10,10), 150, 150);
    }

    public static void cancelWork(ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
        if (bitmapWorkerTask != null) {
            bitmapWorkerTask.cancel(true);
        }
    }

    public static boolean cancelPotentialWork(String key_url, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            if (!bitmapWorkerTask.key_url.equals(key_url)) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }
}