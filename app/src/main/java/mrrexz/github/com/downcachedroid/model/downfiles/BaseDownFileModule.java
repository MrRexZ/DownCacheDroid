package mrrexz.github.com.downcachedroid.model.downfiles;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import okhttp3.Call;
import okhttp3.Callback;

/**
 * Created by antho on 7/19/2017.
 */

public abstract class BaseDownFileModule {

    public final String MIME;

    public BaseDownFileModule(String mime) {
        MIME = mime;
    }

    public abstract Object getConvertedData(Object data);
    public abstract Object convertProc(byte[] networkInput);
    public abstract void download(Function<BaseDownFileModule, Call> standardDownloadLogic) throws IOException;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseDownFileModule)) return false;

        BaseDownFileModule that = (BaseDownFileModule) o;

        return MIME.equals(that.MIME);

    }

    @Override
    public int hashCode() {
        return MIME.hashCode();
    }

}
