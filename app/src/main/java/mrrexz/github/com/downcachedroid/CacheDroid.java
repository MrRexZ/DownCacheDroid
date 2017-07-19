package mrrexz.github.com.downcachedroid;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import java.io.InputStream;

/**
 * Created by antho on 7/19/2017.
 */

public class CacheDroid {

    LruCache<String, InputStream> lruCache;
    public CacheDroid(int cacheSize) {
        lruCache = new LruCache<String, InputStream>(cacheSize);
    }

    public void insertToCache(String key, InputStream value){
        synchronized (lruCache){
            lruCache.put(key, value);
        }
    }

    public InputStream getFromCache(String key) {
        synchronized (lruCache){
            return lruCache.get(key);
        }
    }
}
