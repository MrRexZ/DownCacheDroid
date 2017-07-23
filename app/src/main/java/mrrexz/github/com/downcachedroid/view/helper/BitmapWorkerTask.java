package mrrexz.github.com.downcachedroid.view.helper;

/**
 * Created by antho on 7/20/2017.
 */

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import mrrexz.github.com.downcachedroid.controller.download.DownloadProcDroid;
import mrrexz.github.com.downcachedroid.helper.AsyncDrawable;

public class BitmapWorkerTask extends AsyncTask<String, Void, WeakReference<Bitmap>> {
    public static final String TAG = BitmapWorkerTask.class.getName();

    private String key_url;

    private final WeakReference<ImageView> imageViewReference;
    private DownloadProcDroid downloadProcDroid;
    public BitmapWorkerTask(ImageView imageView, DownloadProcDroid downloadProcDroid) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<>(imageView);
        this.downloadProcDroid = downloadProcDroid;
    }

    // Decode image in background.
    @Override
    protected WeakReference<Bitmap> doInBackground(String... params) {
        key_url = params[0];

        while(downloadProcDroid.cacheDroidModule.getDataFromCache(key_url) == null) {
            if (!downloadProcDroid.downloadInProgress(key_url)) {
                //TODO: return default bitmap image
                return null;
            }
        }

        return decodeBitmapFromCache(key_url);
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(WeakReference<Bitmap> wrbitmap) {
        Log.d("OnPostExecute", "Done");
        if (!isCancelled()) {
            if (wrbitmap.get() != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(wrbitmap.get());
                }
            }
        }
    }

    public WeakReference<Bitmap> decodeBitmapFromCache(String key_url) {
        while (downloadProcDroid.cacheDroidModule.getDataFromCache(key_url) == null){}
        Log.d("BITMAP WorkerTask", "Decoding...");
        return new WeakReference<Bitmap>((Bitmap) downloadProcDroid.cacheDroidModule.getDataFromCache(key_url));
        //return BitmapHelper.decodeSampledBitmapFromBytes(downloadProcDroid.cacheDroidModule.getDataFromCache(key_url), new Rect(10,10,10,10), 250, 250);
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