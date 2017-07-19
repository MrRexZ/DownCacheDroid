package mrrexz.github.com.downcachedroid.helper;

import java.io.IOException;

/**
 * Created by antho on 7/20/2017.
 */

public interface GenericCallback<T> {
    void onValue(T value) throws IOException;
}
