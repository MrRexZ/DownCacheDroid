package mrrexz.github.com.downcachedroid;

import android.app.ActivityManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    final String testString = "http://pastebin.com/raw/wgkJgazE";
    OkHttpClient client = new OkHttpClient();
    Set<BaseDownFile> supportedDownTypes = new HashSet<>();
    String[] urlLinks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final int memClass = ((ActivityManager) this.getApplicationContext().getSystemService(
                Context.ACTIVITY_SERVICE)).getMemoryClass();
        final int cacheSize = 1024 * 1024 * memClass / 8;
        CacheDroid cacheDroid = new CacheDroid(cacheSize);

        supportedDownTypes.add(
                new DownImageFile(this, cacheDroid)
        );

        try {
            Call pageRequest = this.call(testString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String[] extractLinks(String text) {
        List<String> urlLinks = new ArrayList<String>();
        Matcher urlMatcher = Patterns.WEB_URL.matcher(text);
        while (urlMatcher.find()) {
            String url = urlMatcher.group();
            urlLinks.add(url);
        }
        return urlLinks.toArray(new String[urlLinks.size()]);
    }

    Call call(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, IOException e) {
                runOnUiThread(new Runnable() {
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
}
