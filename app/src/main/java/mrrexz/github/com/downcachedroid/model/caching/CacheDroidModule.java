package mrrexz.github.com.downcachedroid.model.caching;

import android.support.v4.util.LruCache;
import android.support.v4.util.Pair;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import mrrexz.github.com.downcachedroid.helper.DataUpdateListener;
import mrrexz.github.com.downcachedroid.model.downfiles.BaseDownFileModule;
import mrrexz.github.com.downcachedroid.model.downfiles.ImageDownFileModule;

/**
 * Created by antho on 7/19/2017.
 */

@Module
@Singleton
public class CacheDroidModule {

    LruCache<String, Pair<byte[], BaseDownFileModule>> lruCache = new LruCache<String, Pair<byte[], BaseDownFileModule>>(getDefaultLruCacheSize());
    public Set<BaseDownFileModule> supportedDownTypes;
    DataUpdateListener dataUpdateListener;

    @Inject
    public CacheDroidModule(Set<BaseDownFileModule> supportedDownTypes) {
        this.supportedDownTypes = supportedDownTypes;
    }

    @Provides
    CacheDroidModule provideCacheDroidModuleInstance() {
        Set<BaseDownFileModule> tempSet =new HashSet<>();
        tempSet.add(new ImageDownFileModule());
        return new CacheDroidModule(tempSet);
    }

    public void setDataUpdateListener(DataUpdateListener dataUpdateListener) {
        this.dataUpdateListener = dataUpdateListener;
        Log.d("CacheDroidModule", "Listener SET!");
    }


    public synchronized void insertToCache(String key, byte[] is, BaseDownFileModule downFileType){
        dataUpdateListener.cacheUpdated(key);
        lruCache.put(key, new Pair<>(is, downFileType));
    }

    @Provides
    public synchronized byte[] getDataFromCache(String key)  {
        try {
            return lruCache.get(key).first;
        } catch (Exception e) {
            return null;
        }
    }

    @Provides
    public synchronized BaseDownFileModule getTypeFromCache(String key) {
        try {
            return lruCache.get(key).second;
        } catch (Exception e) {
            return null;
        }
    }

    @Provides
    public synchronized Object getConvertedDataFromCache(String key)  {
        try {
            return getTypeFromCache(key).getConvertedData(getDataFromCache(key));
        } catch (Exception e) {
            return null;
        }
    }

    public synchronized void resizeCache(int maxSize){
        lruCache.resize(maxSize);
    }

    public static int getDefaultLruCacheSize() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        return cacheSize;
    }



}
