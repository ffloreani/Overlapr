package xyz.filipfloreani.overlapr.sync;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xyz.filipfloreani.overlapr.utils.GeneralUtils;

/**
 * Created by filipfloreani on 19/04/2017.
 */

public class HttpOperations {

    private static final String TAG = "HttpOperations";

    private static final String SERVER_URL = "https://overlapr.filipfloreani.xyz";
    private static final String SERVER_GET = "/api/charts";
    private static final String SERVER_POST = "/api/results";

    private static Handler handler = new Handler(Looper.getMainLooper());
    private static OkHttpClient httpClient = new OkHttpClient();

    public static void getFile(final Context context) {
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
                if (!response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.code() == 404 && response.body().string().equals("No more charts")) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "No charts to download", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                    throw new IOException("Response code " + response);
                }

                if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    return;
                }

                File outputDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Overlapr chart files/");
                if (!outputDir.exists()) {
                    if (!outputDir.mkdirs()) Log.d(TAG, "Charts directory not created");
                }

                Headers respHeaders = response.headers();
                String contentDisp = respHeaders.get("Content-Disposition");
                String fileName = contentDisp != null ? contentDisp.substring(contentDisp.indexOf("\"") + 1, contentDisp.lastIndexOf("\"")) : GeneralUtils.getUTCNow().toString() + ".txt";

                File outputFile = new File(outputDir + File.separator + "Chart " + fileName);
                if (!outputFile.createNewFile()) {
                    return;
                }

                try (BufferedReader br = new BufferedReader(new InputStreamReader(response.body().byteStream()));
                     BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile)))) {

                    String line;
                    while ((line = br.readLine()) != null) {
                        bw.write(line);
                        bw.newLine();
                    }
                    bw.flush();
                }
            }
        });
    }

    public static void postFile(File file, final Context context) throws IOException {
        final MediaType MEDIA_TYPE_TEXT = MediaType.parse("text/plain");

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
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

                if (response.body() != null) {
                    final String responseBody = response.body().string();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, responseBody, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
