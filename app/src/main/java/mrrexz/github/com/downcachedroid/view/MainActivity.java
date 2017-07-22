package mrrexz.github.com.downcachedroid.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import mrrexz.github.com.downcachedroid.R;
import mrrexz.github.com.downcachedroid.controller.DaggerDownCacheApp;
import mrrexz.github.com.downcachedroid.controller.download.DownloadProcDroid;
import mrrexz.github.com.downcachedroid.helper.DataUpdateListener;
import mrrexz.github.com.downcachedroid.helper.GenericCallback;
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
        //DownloadProcDroid.cacheWebContents(testString);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recylerview_photos);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        PhotosRecyclerViewAdapter photosRecyclerViewAdapter = new PhotosRecyclerViewAdapter(new ArrayList<>(), downCacheApp.getDownloadProcInstance().cacheDroidModule);
        recyclerView.setAdapter(photosRecyclerViewAdapter);
        DataUpdateListener dataUpdateListener = new DataUpdateListener() {
            @Override
            public void cacheUpdated(String url) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        photosRecyclerViewAdapter.add(photosRecyclerViewAdapter.getItemCount(), url);
                        Log.d("BITMAP", "Successfully added to recylcerview");
                    }
                });
            }
        };
        downCacheApp.getDownloadProcInstance().cacheDroidModule.setDataUpdateListener(dataUpdateListener);

        Log.d("BITMAP Main", "Preparing...");
        try {
            downCacheApp.getDownloadProcInstance().getWebResLinks(testString, (urls) -> {
                downCacheApp.getDownloadProcInstance().cache(urls);
                Log.d("BITMAP Main", "Started!!");


            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
