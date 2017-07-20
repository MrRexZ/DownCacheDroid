package mrrexz.github.com.downcachedroid.view;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.constraint.solver.Cache;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import mrrexz.github.com.downcachedroid.R;
import mrrexz.github.com.downcachedroid.model.caching.CacheDroid;
import mrrexz.github.com.downcachedroid.controller.download.DownloadProcDroid;
import mrrexz.github.com.downcachedroid.model.downfiles.BaseDownFile;
import mrrexz.github.com.downcachedroid.model.downfiles.DownImageFile;
import okhttp3.Call;

public class MainActivity extends AppCompatActivity {

    final String testString = "http://pastebin.com/raw/wgkJgazE";
    Set<BaseDownFile> supportedDownTypes = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final int memClass = ((ActivityManager) this.getApplicationContext().getSystemService(
                Context.ACTIVITY_SERVICE)).getMemoryClass();
        final int cacheSize = 1024 * 1024 * memClass / 8;
        System.out.println("CacheSize: "+cacheSize);
        supportedDownTypes.add(
                new DownImageFile()
        );

        try {
            Call pageRequest = DownloadProcDroid.getWebResLinks(testString, (urls) -> {
                DownloadProcDroid.process(
                        urls,
                        supportedDownTypes);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        ImageView image = (ImageView) findViewById(R.id.imageView1);
        String test = "https://images.unsplash.com/profile-1464495186405-68089dcd96c3?ixlib=rb-0.3.5";
        ListView listViewPhotos = (ListView) findViewById(R.id.list_photos);
//        while (CacheDroid.getDataFromCache(test) == null) {
//        }
//        image.setImageBitmap((Bitmap) CacheDroid.getConvertedDataFromCache(test));

    }



}
