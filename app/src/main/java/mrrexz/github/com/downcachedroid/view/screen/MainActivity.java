package mrrexz.github.com.downcachedroid.view.screen;

import android.app.ActivityManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import mrrexz.github.com.downcachedroid.R;
import mrrexz.github.com.downcachedroid.controller.DaggerDownCacheApp;
import mrrexz.github.com.downcachedroid.helper.DataUpdateListener;
import mrrexz.github.com.downcachedroid.helper.GenericCallback;
import mrrexz.github.com.downcachedroid.view.adapter.PhotosRecyclerViewAdapter;
import mrrexz.github.com.downcachedroid.view.helper.SpacesItemDecoration;
import mrrexz.github.com.downcachedroid.model.caching.CacheDroidModule;
import mrrexz.github.com.downcachedroid.model.downfiles.BaseDownFileModule;
import mrrexz.github.com.downcachedroid.model.downfiles.ImageDownFileModule;
import mrrexz.github.com.downcachedroid.controller.DownCacheApp;

public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.getName();
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

        final int cacheSize = 1024 * memClass / 8;
        downCacheApp.getDownloadProcInstance().cacheDroidModule.resizeCache(cacheSize);
        Log.d("Memory size : ", Integer.toString(cacheSize));
        //DownloadProcDroid.cacheWebContents(testString);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recylerview_photos);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.addItemDecoration(new SpacesItemDecoration(20));
//        recyclerView.addItemDecoration(new DividerItemDecoration(this,
//                DividerItemDecoration.VERTICAL));
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        PhotosRecyclerViewAdapter photosRecyclerViewAdapter = new PhotosRecyclerViewAdapter(new ArrayList<>(), downCacheApp.getDownloadProcInstance());
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
        downCacheApp.getDownloadProcInstance().cacheDroidModule.setDataUpdateListener(dataUpdateListener);

        Log.d("BITMAP Main", "Preparing...");
        try {
            downCacheApp.getDownloadProcInstance().getWebResLinks(testString, (urls) -> {
                downCacheApp.getDownloadProcInstance().downloadAndCache(urls, new GenericCallback<String>() {
                    @Override
                    public void onValue(String value) throws IOException {
                        //Do nothing..
                    }
                });
                Log.d(TAG, "Started downloading bitmap!!");

            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        Button failedDownloadButton = (Button) findViewById(R.id.redownloadFailedButton);
        failedDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downCacheApp.getDownloadProcInstance().asyncRedownloadFailedAll(new GenericCallback<String>() {
                    @Override
                    public void onValue(String value) throws IOException {
                        Log.d(TAG, "Redownload successfull : " + value);
                        dataUpdateListener.cacheElemAdded(value);
                    }
                });
            }
        });
    }



}
