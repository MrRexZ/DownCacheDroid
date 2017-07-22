package mrrexz.github.com.downcachedroid.helper;

/**
 * Created by antho on 7/22/2017.
 */

public interface DataUpdateListener {
    void cacheElemAdded(String url);
    void cacheElemRemoved(String url);
}
