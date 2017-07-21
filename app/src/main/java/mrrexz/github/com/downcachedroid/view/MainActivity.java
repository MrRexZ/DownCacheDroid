package mrrexz.github.com.downcachedroid.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import mrrexz.github.com.downcachedroid.R;
import mrrexz.github.com.downcachedroid.controller.download.DownloadProcDroid;
import mrrexz.github.com.downcachedroid.helper.GenericCallback;
import mrrexz.github.com.downcachedroid.model.caching.CacheDroid;
import mrrexz.github.com.downcachedroid.model.downfiles.ImageDownFile;
import okhttp3.MediaType;

public class MainActivity extends AppCompatActivity {

    final String testString = "http://pastebin.com/raw/wgkJgazE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CacheDroid.supportedDownTypes.add(new ImageDownFile());
        //DownloadProcDroid.cacheWebContents(testString);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recylerview_photos);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        PhotosRecyclerViewAdapter photosRecyclerViewAdapter = new PhotosRecyclerViewAdapter(new ArrayList<>());
        recyclerView.setAdapter(photosRecyclerViewAdapter);

        Log.d("BITMAP Main", "Preparing...");
        try {
            DownloadProcDroid.getWebResLinks(testString, (urls) -> {
               //List<String> filteredUrls = DownloadProcDroid.retrieveUrlsWithMimeType(new ImageDownFile().MIME, urls.toArray(new String[urls.size()]));

                DownloadProcDroid.cache(urls);
                DownloadProcDroid.asyncGetUrlsWithMimeType(new ImageDownFile().MIME, urls.toArray(new String[urls.size()]), new GenericCallback<String>() {
                    @Override
                    public void onValue(String url) throws IOException {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                photosRecyclerViewAdapter.add(photosRecyclerViewAdapter.getItemCount(), url);
                                Log.d("BITMAP", "Successfully added to recylcerview");
                            }
                        });
                    }
                });
                Log.d("BITMAP Main", "Started!!");


            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
