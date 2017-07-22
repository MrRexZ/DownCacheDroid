package mrrexz.github.com.downcachedroid.view;

import android.app.ActivityManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import mrrexz.github.com.downcachedroid.R;
import mrrexz.github.com.downcachedroid.controller.DaggerDownCacheApp;
import mrrexz.github.com.downcachedroid.helper.DataUpdateListener;
import mrrexz.github.com.downcachedroid.model.caching.CacheDroidModule;
import mrrexz.github.com.downcachedroid.model.downfiles.BaseDownFileModule;
import mrrexz.github.com.downcachedroid.model.downfiles.ImageDownFileModule;
import mrrexz.github.com.downcachedroid.controller.DownCacheApp;

public class MainActivity extends AppCompatActivity {

    final String testString = "http://pastebin.com/raw/wgkJgazE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Set<BaseDownFileModule> setSample = new HashSet<BaseDownFileModule>();
        setSample.add(new ImageDownFileModule());
        DownCacheApp downCacheApp = DaggerDownCacheApp.builder().cacheDroidModule(new CacheDroidModule(setSample)).build();
        downCacheApp.injectCache(setSample);
        /** Default proportion of available heap to use for the cache */
        final int DEFAULT_CACHE_SIZE_PROPORTION = 18;

        final int memClass = ((ActivityManager) this.getApplicationContext().getSystemService(
                Context.ACTIVITY_SERVICE)).getMemoryClass();

        final int cacheSize = 1024 * 1024 * memClass / 8;
        downCacheApp.getDownloadProcInstance().cacheDroidModule.resizeCache(cacheSize);
        Log.d("Memory size : ", Integer.toString(cacheSize));
        //DownloadProcDroid.cacheWebContents(testString);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recylerview_photos);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        PhotosRecyclerViewAdapter photosRecyclerViewAdapter = new PhotosRecyclerViewAdapter(new ArrayList<>(), downCacheApp.getDownloadProcInstance());
        recyclerView.setAdapter(photosRecyclerViewAdapter);

        DataUpdateListener dataUpdateListener = new DataUpdateListener() {
            @Override
            public void cacheElemAdded(String url) {
                photosRecyclerViewAdapter.add(photosRecyclerViewAdapter.getItemCount(), url);
                Log.d("BITMAP", "URL successfully added to recylcerview");
            }

            @Override
            public void cacheElemRemoved(String url) {
                photosRecyclerViewAdapter.remove(url);
                Log.d("BITMAP", "Bitmap removed from Recyclerview");
            }

        };
        downCacheApp.getDownloadProcInstance().cacheDroidModule.setDataUpdateListener(dataUpdateListener);

        Log.d("BITMAP Main", "Preparing...");
        try {
            downCacheApp.getDownloadProcInstance().getWebResLinks(testString, (urls) -> {
                downCacheApp.getDownloadProcInstance().downloadAndCache(urls);
                Log.d("BITMAP Main", "Started!!");

            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
