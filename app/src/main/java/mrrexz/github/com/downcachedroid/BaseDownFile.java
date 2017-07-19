package mrrexz.github.com.downcachedroid;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by antho on 7/19/2017.
 */

public abstract class BaseDownFile {

    Context context;
    Activity activity;
    CacheDroid cacheDroid;
    Map<String, Call> urlCalls = new HashMap<>();

    public BaseDownFile(Context ctx, Activity act, CacheDroid cDroid){
        context = ctx;
        activity = act;
        cacheDroid = cDroid;
    }

    public abstract Object get(String url);
}
