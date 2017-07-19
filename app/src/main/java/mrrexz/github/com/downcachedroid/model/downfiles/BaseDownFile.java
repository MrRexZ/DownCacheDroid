package mrrexz.github.com.downcachedroid.model.downfiles;

import android.app.Activity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import mrrexz.github.com.downcachedroid.model.caching.CacheDroid;
import okhttp3.Call;

/**
 * Created by antho on 7/19/2017.
 */

public abstract class BaseDownFile {

    Activity activity;
    Map<String, Call> urlCalls = new HashMap<>();
    public final String MIME;

    public BaseDownFile(String mime) {
        MIME = mime;
    }

    public abstract Object get(String url) ;
    public abstract void download(String url) throws IOException ;
}
