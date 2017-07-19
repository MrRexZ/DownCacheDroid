package mrrexz.github.com.downcachedroid.view;

import android.app.ActivityManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import mrrexz.github.com.downcachedroid.R;
import mrrexz.github.com.downcachedroid.controller.caching.CacheDroid;
import mrrexz.github.com.downcachedroid.controller.download.DownloadProcDroid;
import mrrexz.github.com.downcachedroid.model.BaseDownFile;
import mrrexz.github.com.downcachedroid.model.DownImageFile;
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
        CacheDroid cacheDroid = new CacheDroid(cacheSize);

        supportedDownTypes.add(
                new DownImageFile(this, cacheDroid)
        );

        try {
            Call pageRequest = DownloadProcDroid.getWebResLinks(testString, (urls) -> {
                DownloadProcDroid.process(urls,
                        supportedDownTypes);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
