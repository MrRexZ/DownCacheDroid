package mrrexz.github.com.downcachedroid.model.caching;

import android.support.v4.util.LruCache;
import android.support.v4.util.Pair;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
public class CacheDroidModule extends LruCache<String, Pair<Object, BaseDownFileModule>> {
    private static final String TAG = CacheDroidModule.class.getName();

    public Set<BaseDownFileModule> supportedDownTypes;
    DataUpdateListener dataUpdateListener;

    @Inject
    public CacheDroidModule(Set<BaseDownFileModule> supportedDownTypes) {
        super(getDefaultLruCacheSize());
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


    public synchronized void insertToCache(String key, Object is, BaseDownFileModule downFileType){
        dataUpdateListener.cacheElemAdded(key);
        put(key, new Pair<>(is, downFileType));
    }

    public synchronized Object getDataFromCache(String key)  {
        try {
            return (Object) get(key).first;
        } catch (Exception e) {
            return null;
        }
    }

    @Provides
    public synchronized BaseDownFileModule getTypeFromCache(String key) {
        try {
            return get(key).second;
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

    public synchronized void resizeCache(int maxSize){
        resize(maxSize);
    }

    public static int getDefaultLruCacheSize() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        Log.d("Default Memory size", Integer.toString(cacheSize));
        return cacheSize;
    }

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
        try {
            return objToByte((Object)value.first).length / 1024;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }


     static byte[] objToByte(Object javaObj) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream objStream = new ObjectOutputStream(byteStream);
        objStream.writeObject(javaObj);
        return byteStream.toByteArray();
    }
}
