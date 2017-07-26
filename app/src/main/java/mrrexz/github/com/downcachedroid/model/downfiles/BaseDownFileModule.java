package mrrexz.github.com.downcachedroid.model.downfiles;

/**
 * Created by antho on 7/19/2017.
 */

public abstract class BaseDownFileModule {

    public final String MIME;

    public BaseDownFileModule(String mime) {
        MIME = mime;
    }

    public abstract Object getConvertedData(Object data);
    public abstract Object convertDownloadedData(byte[] networkInput);
    public abstract int determineSizeInCache(Object data);

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
