package mrrexz.github.com.downcachedroid.view;

import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.support.v7.widget.RecyclerView;
import mrrexz.github.com.downcachedroid.R;
import mrrexz.github.com.downcachedroid.helper.BitmapHelper;
import mrrexz.github.com.downcachedroid.model.caching.CacheDroid;

/**
 * Created by antho on 7/20/2017.
 */


import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class PhotosRecyclerViewAdapter extends RecyclerView.Adapter<PhotosRecyclerViewAdapter.ViewHolder> {
    private List<String> itemsData;

    public PhotosRecyclerViewAdapter(List<String> itemsData) {
        this.itemsData = itemsData;
    }

    public synchronized void add(int position, String item) {
        itemsData.add(position, item);
        notifyItemInserted(position);
    }

    public synchronized void remove(int position) {
        itemsData.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public PhotosRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                   int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_single, null);

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        String key_url = itemsData.get(position);
        byte[] imageStream = CacheDroid.getDataFromCache(key_url);
        if ( imageStream != null ) {
            viewHolder.imgViewIcon.setImageBitmap(BitmapHelper.decodeSampledBitmapFromStream(key_url, new Rect(100, 100, 100, 100), 350, 350));
        }
        else {
            BitmapWorkerTask bitmapWorkerTask = new BitmapWorkerTask(viewHolder.imgViewIcon);
            bitmapWorkerTask.execute(key_url);
        }

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgViewIcon;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            imgViewIcon = (ImageView) itemLayoutView.findViewById(R.id.downed_img);
        }
    }


    @Override
    public int getItemCount() {
        return itemsData.size();
    }
}
