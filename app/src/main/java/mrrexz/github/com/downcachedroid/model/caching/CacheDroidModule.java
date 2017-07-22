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
    private static final String TAG = CacheDroidModule.class.getName();
    private LruCache<String, Pair<byte[], BaseDownFileModule>> lruCache = new LruCache<String, Pair<byte[], BaseDownFileModule>>(getDefaultLruCacheSize()) {
        @Override
        protected void entryRemoved(boolean evicted, String key, Pair<byte[], BaseDownFileModule> oldValue, Pair<byte[], BaseDownFileModule> newValue) {
            super.entryRemoved(evicted, key, oldValue, newValue);
            if (evicted) {
                Log.d(TAG, "Cache element Evicted");
                dataUpdateListener.cacheElemRemoved(key);
            }
        }
    };

    public Set<BaseDownFileModule> supportedDownTypes;
    DataUpdateListener dataUpdateListener;

    @Inject
    public CacheDroidModule(Set<BaseDownFileModule> supportedDownTypes) {
        this.supportedDownTypes = supportedDownTypes;
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

    public void setDataUpdateListener(DataUpdateListener dataUpdateListener) {
        this.dataUpdateListener = dataUpdateListener;
        Log.d("CacheDroidModule", "Listener SET!");
    }


    public void insertToCache(String key, byte[] is, BaseDownFileModule downFileType){
        synchronized(lruCache) {
            dataUpdateListener.cacheElemAdded(key);
            lruCache.put(key, new Pair<>(is, downFileType));
        }
    }

    @Provides
    public byte[] getDataFromCache(String key)  {
        try {
            synchronized (lruCache) {
                return lruCache.get(key).first;
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Provides
    public BaseDownFileModule getTypeFromCache(String key) {
        try {
            synchronized (lruCache) {
                return lruCache.get(key).second;
            }
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

    public void resizeCache(int maxSize){
        synchronized (lruCache) {
            lruCache.resize(maxSize);
        }
    }

    public static int getDefaultLruCacheSize() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() );
        final int cacheSize = maxMemory / 8;
        Log.d("Default Memory size", Integer.toString(cacheSize));
        return cacheSize;
    }



}
