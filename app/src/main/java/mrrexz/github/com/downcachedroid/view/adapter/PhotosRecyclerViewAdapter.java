package mrrexz.github.com.downcachedroid.view.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.support.v7.widget.RecyclerView;
import mrrexz.github.com.downcachedroid.R;
import mrrexz.github.com.downcachedroid.controller.download.DownloadProcDroid;
import mrrexz.github.com.downcachedroid.view.helper.BitmapWorkerTask;

/**
 * Created by antho on 7/20/2017.
 */


import java.util.List;

public class PhotosRecyclerViewAdapter extends RecyclerView.Adapter<PhotosRecyclerViewAdapter.ViewHolder> {
    private List<String> itemsData;
    private DownloadProcDroid downloadProcDroid;

    public PhotosRecyclerViewAdapter(List<String> itemsData, DownloadProcDroid downloadProcDroid) {
        this.itemsData = itemsData;
        this.downloadProcDroid = downloadProcDroid;
    }

    public synchronized void add(int position, String item) {
        itemsData.add(position, item);
        notifyItemInserted(position);
    }

    public synchronized void remove(String item) {
        int remove_index = itemsData.indexOf(item);
        itemsData.remove(remove_index);
        notifyItemRemoved(remove_index);
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
        Bitmap bitmap = (Bitmap)downloadProcDroid.cacheDroidModule.getDataFromCache(key_url);
        if ( bitmap != null ) {
            viewHolder.imgViewIcon.setImageBitmap(bitmap);
        }
        else {
            if (downloadProcDroid.downloadInProgress(key_url)) {
                BitmapWorkerTask bitmapWorkerTask = new BitmapWorkerTask(viewHolder.imgViewIcon, downloadProcDroid);
                bitmapWorkerTask.execute(key_url);
            }
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
