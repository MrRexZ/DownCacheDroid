package mrrexz.github.com.downcachedroid.controller;

import javax.inject.Singleton;

import dagger.Component;
import mrrexz.github.com.downcachedroid.controller.download.DownloadProcDroid;
import mrrexz.github.com.downcachedroid.model.caching.CacheDroidModule;
import mrrexz.github.com.downcachedroid.view.MainActivity;

/**
 * Created by antho on 7/21/2017.
 */

@Component(modules = { CacheDroidModule.class })
@Singleton
public interface DownCacheApp {
    DownloadProcDroid getDownloadProcInstance();
}



