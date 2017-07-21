package mrrexz.github.com.downcachedroid.model.caching;

import android.support.v4.util.LruCache;
import android.support.v4.util.Pair;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import mrrexz.github.com.downcachedroid.model.downfiles.BaseDownFile;

/**
 * Created by antho on 7/19/2017.
 */

public class CacheDroid {
    static LruCache<String, Pair<byte[], BaseDownFile>> lruCache = new LruCache<String, Pair<byte[], BaseDownFile>>(getDefaultLruCacheSize());
    public static Set<BaseDownFile> supportedDownTypes = new HashSet<>();
    public synchronized static void insertToCache(String key, byte[] is, BaseDownFile downFileType){
        lruCache.put(key, new Pair<>(is, downFileType));
    }

    public synchronized static byte[] getDataFromCache(String key)  {
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

    public static int getDefaultLruCacheSize() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        return cacheSize;
    }



}
