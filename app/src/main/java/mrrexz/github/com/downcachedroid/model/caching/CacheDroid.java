package mrrexz.github.com.downcachedroid.model.caching;

import android.support.v4.util.LruCache;
import android.support.v4.util.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

import mrrexz.github.com.downcachedroid.model.downfiles.BaseDownFile;

/**
 * Created by antho on 7/19/2017.
 */

public class CacheDroid {
    static LruCache<String, Pair<InputStream, BaseDownFile>> lruCache;
    static ConcurrentHashMap<String, BaseDownFile> downFileTypeMap = new ConcurrentHashMap<>();
    public CacheDroid(int cacheSize) {
        lruCache = new LruCache<String, Pair<InputStream, BaseDownFile>>(cacheSize);
    }

    public synchronized static void insertToCache(String key, InputStream data, BaseDownFile downFileType){
        lruCache.put(key, new Pair<>(data, downFileType));
    }

    public synchronized static InputStream getDataFromCache(String key)  {
        try {
            return lruCache.get(key).first;
        } catch (Exception e) {
            return null;
        }
    }

    public synchronized static BaseDownFile getTypeFromCache(String key) {
        try {
            return lruCache.get(key).second;
        } catch (Exception e) {
            return null;
        }
    }

    public synchronized static Object getConvertedDataFromCache(String key)  {
        try {
            return getTypeFromCache(key).get(key);
        } catch (Exception e) {
            return null;
        }
    }

    public synchronized static void resizeCache(int maxSize){
        lruCache.resize(maxSize);
    }



}
