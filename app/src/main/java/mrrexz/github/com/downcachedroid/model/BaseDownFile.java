package mrrexz.github.com.downcachedroid.model;

import android.app.Activity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import mrrexz.github.com.downcachedroid.controller.caching.CacheDroid;
import okhttp3.Call;

/**
 * Created by antho on 7/19/2017.
 */

public abstract class BaseDownFile {

    Activity activity;
    CacheDroid cacheDroid;
    Map<String, Call> urlCalls = new HashMap<>();
    final String MIME;

    public BaseDownFile(Activity act, CacheDroid cDroid, String mime) {
        activity = act;
        cacheDroid = cDroid;
        MIME = mime;

    }

    abstract Object get(String url);
    abstract void download(String url) throws IOException ;
}
