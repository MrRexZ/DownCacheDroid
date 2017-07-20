package mrrexz.github.com.downcachedroid.view;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.support.constraint.solver.Cache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.io.InputStream;

import mrrexz.github.com.downcachedroid.R;
import mrrexz.github.com.downcachedroid.helper.BitmapHelper;
import mrrexz.github.com.downcachedroid.model.caching.CacheDroid;

/**
 * Created by antho on 7/20/2017.
 */

public class PhotosListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] urls;

    public PhotosListAdapter(Activity context,
                      String[] urls) {
        super(context, R.layout.list_single, urls);
        this.context = context;
        this.urls = urls;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single, null, true);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.downed_img);
        InputStream imageStream = CacheDroid.getDataFromCache(urls[position]);
        if ( imageStream != null ) {
            imageView.setImageBitmap(BitmapHelper.decodeSampledBitmapFromStream(imageStream, new Rect(100, 100, 100, 100), 350, 350));
        }
        else {
             BitmapWorkerTask bitmapWorkerTask = new BitmapWorkerTask(imageView);
                bitmapWorkerTask.execute(urls[position]);
        }
        return rowView;
    }
}
