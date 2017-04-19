package xyz.filipfloreani.overlapr.network;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by filipfloreani on 19/04/2017.
 */

public class HttpOperations {

    private static OkHttpClient httpClient = new OkHttpClient();

    public static void get() throws IOException {
        Request request = new Request.Builder()
                .url("http://vast-garden-64251.herokuapp.com/overlapr")
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful())
                    throw new IOException("Unknown response code " + response);

                try (InputStream byteStream = response.body().byteStream()) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(byteStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        Log.d("HTTP GET", line); // TODO Do something useful with this...
                    }
                }
            }
        });
    }
}
