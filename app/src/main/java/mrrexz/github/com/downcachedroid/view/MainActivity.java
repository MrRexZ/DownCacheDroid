package mrrexz.github.com.downcachedroid.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mrrexz.github.com.downcachedroid.R;
import mrrexz.github.com.downcachedroid.controller.download.DownloadProcDroid;
import mrrexz.github.com.downcachedroid.model.caching.CacheDroid;
import mrrexz.github.com.downcachedroid.model.downfiles.BaseDownFile;
import mrrexz.github.com.downcachedroid.model.downfiles.DownImageFile;
import okhttp3.Call;

public class MainActivity extends AppCompatActivity {

    final String testString = "http://pastebin.com/raw/wgkJgazE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CacheDroid.supportedDownTypes.add(new DownImageFile());
        DownloadProcDroid.cacheWebContents(testString);
    }



}
