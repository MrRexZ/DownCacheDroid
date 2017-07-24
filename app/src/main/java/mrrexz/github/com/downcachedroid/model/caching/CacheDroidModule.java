package mrrexz.github.com.downcachedroid.model.caching;

import android.support.v4.util.LruCache;
import android.support.v4.util.Pair;
import android.util.Log;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import mrrexz.github.com.downcachedroid.helper.DataUpdateListener;
import mrrexz.github.com.downcachedroid.model.downfiles.BaseDownFileModule;

/**
 * Created by antho on 7/19/2017.
 */

@Module
@Singleton
public class CacheDroidModule {
    private static final String TAG = CacheDroidModule.class.getName();
    DataUpdateListener dataUpdateListener;
    private LruCache<String, Pair<Object, BaseDownFileModule>> lruCache = new LruCache<String, Pair<Object, BaseDownFileModule>>(getDefaultLruCacheSize()) {
        @Override
        protected void entryRemoved(boolean evicted, String key, Pair<Object, BaseDownFileModule> oldValue, Pair<Object, BaseDownFileModule> newValue) {
            super.entryRemoved(evicted, key, oldValue, newValue);
            if (evicted) {
                Log.d(TAG, "Cache element Evicted");
                dataUpdateListener.cacheElemRemoved(key);
            }
        }

        @Override
        protected int sizeOf(String key, Pair<Object, BaseDownFileModule> value) {
            if (value != null) {
                return value.second.determineSizeInCache(value.first) / 1024;
            } else {
                return 0;
            }
        }
    };
    private Set<BaseDownFileModule> supportedDownTypes;

    @Inject
    public CacheDroidModule(Set<BaseDownFileModule> supportedDownTypes) {
        this.supportedDownTypes = supportedDownTypes;
    }

    public static int getDefaultLruCacheSize() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        Log.d("Default Memory size", Integer.toString(cacheSize));
        return cacheSize;
    }

    @Provides
    CacheDroidModule provideCacheDroidModuleInstance() {
        return this;
    }

    public void addNewSupportedType(BaseDownFileModule baseDownFileModule) {
        synchronized (supportedDownTypes) {
            supportedDownTypes.add(baseDownFileModule);
        }
    }

    public synchronized Set<BaseDownFileModule> getAllSupportedTypes() {
        return supportedDownTypes;
    }

    public void setDataUpdateListener(DataUpdateListener dataUpdateListener) {
        this.dataUpdateListener = dataUpdateListener;
        Log.d(TAG, "Listener set!");
    }

    public synchronized void insertToCache(String key, Object data, BaseDownFileModule downFileType){
        lruCache.put(key, new Pair<>(data, downFileType));
        dataUpdateListener.cacheElemAdded(key);
    }

    private synchronized Object getDataFromCache(String key) {
        try {
            return (Object) lruCache.get(key).first;
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
    public Object getConvertedDataFromCache(String key)  {
        try {
            return getTypeFromCache(key).getConvertedData(getDataFromCache(key));
        } catch (Exception e) {
            return null;
        }
    }

    public synchronized Set<String> getAllKeys() {
        return lruCache.snapshot().keySet();
    }

    public synchronized void resizeCache(int maxSize){
        lruCache.resize(maxSize);
    }

}
