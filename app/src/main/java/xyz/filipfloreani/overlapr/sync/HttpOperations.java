package xyz.filipfloreani.overlapr.sync;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by filipfloreani on 19/04/2017.
 */

public class HttpOperations {

    private static final String SERVER_URL = "http://overlapr-server.herokuapp.com";
    private static final String SERVER_GET = "/api/analysis";
    private static final String SERVER_POST = "/api/results";

    private static OkHttpClient httpClient = new OkHttpClient();

    public static void get() throws IOException {
        Request request = new Request.Builder()
                .url(SERVER_URL + SERVER_GET)
                .build();

        // Async call
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
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

    public static void postFile(File file) throws IOException {
        final MediaType MEDIA_TYPE_TEXT = MediaType.parse("text/plain");

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title", file.getName())
                .addFormDataPart("file", file.getName(), RequestBody.create(MEDIA_TYPE_TEXT, file))
                .build();

        Request request = new Request.Builder()
                .url(SERVER_URL + SERVER_POST)
                .post(requestBody)
                .build();

        // Async call
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful())
                    throw new IOException("Unknown response code " + response);

                Log.d("HTTP POST", response.body().string());
            }
        });
    }
}
