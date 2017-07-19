package mrrexz.github.com.downcachedroid;

import android.app.Activity;
import android.util.Log;
import android.util.Patterns;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.http2.Header;

/**
 * Created by antho on 7/20/2017.
 */

public class DownloadProcDroid {

    static OkHttpClient client = new OkHttpClient();
    static String[] urlLinks;

    static Call getWebResLinks(String url, final Activity activity) throws IOException {

        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String res = response.body().string();
                urlLinks = extractLinks(res);
                Log.d("RES", res);
            }
        });

        return call;
    }

    static Call analyzeMimeType(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                MediaType contentType = MediaType.parse(response.header("Content-Type"));
                Log.d("HOLA", contentType.toString());
            }
        });

        return call;
    }

    public static String[] extractLinks(String text) {
        List<String> urlLinks = new ArrayList<String>();
        Matcher urlMatcher = Patterns.WEB_URL.matcher(text);
        while (urlMatcher.find()) {
            String url = urlMatcher.group();
            analyzeMimeType(url);
            urlLinks.add(url);
        }
        return urlLinks.toArray(new String[urlLinks.size()]);
    }
}
