package mrrexz.github.com.downcachedroid.view.screen;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import mrrexz.github.com.downcachedroid.R;
import mrrexz.github.com.downcachedroid.controller.DaggerDownCacheApp;
import mrrexz.github.com.downcachedroid.controller.DownCacheApp;
import mrrexz.github.com.downcachedroid.helper.DataUpdateListener;
import mrrexz.github.com.downcachedroid.model.caching.CacheDroidModule;
import mrrexz.github.com.downcachedroid.model.downfiles.BaseDownFileModule;
import mrrexz.github.com.downcachedroid.model.downfiles.ImageDownFileModule;
import mrrexz.github.com.downcachedroid.view.adapter.PhotosRecyclerViewAdapter;
import mrrexz.github.com.downcachedroid.view.helper.SpacesItemDecoration;

public class MainActivity extends AppCompatActivity {
    final String testString = "http://pastebin.com/raw/wgkJgazE";
    private final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Set<BaseDownFileModule> setSample = new HashSet<BaseDownFileModule>();
        setSample.add(new ImageDownFileModule());
        DownCacheApp downCacheApp = DaggerDownCacheApp.builder().cacheDroidModule(new CacheDroidModule(setSample)).build();
        final int memClass = ((ActivityManager) this.getApplicationContext().getSystemService(
                Context.ACTIVITY_SERVICE)).getMemoryClass();
        final int cacheSize = 1024 * memClass / 8;
        downCacheApp.getDownloadControllerInstance().cacheDroidModule.resizeCache(cacheSize);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recylerview_photos);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.addItemDecoration(new SpacesItemDecoration(20));
        PhotosRecyclerViewAdapter photosRecyclerViewAdapter = new PhotosRecyclerViewAdapter(new ArrayList<>(), downCacheApp.getDownloadControllerInstance());
        recyclerView.setAdapter(photosRecyclerViewAdapter);

        DataUpdateListener dataUpdateListener = new DataUpdateListener() {
            @Override
            public void cacheElemAdded(String url) {
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        photosRecyclerViewAdapter.add(photosRecyclerViewAdapter.getItemCount(), url);
                        Log.d(TAG, "URL successfully added to recylcerview");
                    }
                });
            }

            @Override
            public void cacheElemRemoved(String url) {
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        photosRecyclerViewAdapter.remove(url);
                        Log.d(TAG, "Bitmap removed from Recyclerview");
                    }
                });
            }

        };
        downCacheApp.getDownloadControllerInstance().cacheDroidModule.setDataUpdateListener(dataUpdateListener);
        downCacheApp.getDownloadControllerInstance().cacheWebContents(testString);
        Button failedDownloadButton = (Button) findViewById(R.id.redownloadFailedButton);
        failedDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downCacheApp.getDownloadControllerInstance().asyncRedownloadFailedAll();
            }
        });
    }



}
