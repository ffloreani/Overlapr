package xyz.filipfloreani.overlapr.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import io.realm.Realm;
import io.realm.RealmResults;
import xyz.filipfloreani.overlapr.model.RealmHighlightsModel;

/**
 * Created by filipfloreani on 16/05/2017.
 */

public class SaveDataTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "SaveDataTask";

    private int id = 1;
    private Context context;
    private NotificationManager notifyManager;
    private NotificationCompat.Builder notifyBuilder;


    public SaveDataTask(Context context) {
        this.context = context;

        notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifyBuilder = new NotificationCompat.Builder(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        notifyBuilder.setContentTitle("Overlapr").setContentText("Saving results").setSmallIcon(android.R.drawable.stat_notify_sync).setProgress(0, 0, true);
        notifyManager.notify(id, notifyBuilder.build());
    }

    @Override
    protected Void doInBackground(Void... params) {
        try (Realm realm = Realm.getDefaultInstance()) {
            RealmResults<RealmHighlightsModel> highlights = realm.where(RealmHighlightsModel.class).findAll();
            writeHighlightsToFile(highlights);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (notifyManager != null) {
            notifyManager.cancel(id);
        }
    }

    /**
     * Creates a new file and writes the given highlights into it in the following form:
     *
     * 'Chart UUID' 'Chart title' '(Start point X, start point Y)' '(End point X, end point Y)'
     *
     * @param highlights The highlights to write to the output file
     * @throws IOException Thrown in case of BufferedReader failure
     */
    private void writeHighlightsToFile(RealmResults<RealmHighlightsModel> highlights) throws IOException {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return;
        }

        File outputDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Overlapr highlights output/");
        if (!outputDir.exists()) {
            if (!outputDir.mkdirs()) Log.d(TAG, "Highlights directory not created");
        }

        File outputFile = new File(outputDir + File.separator + "Output " + GeneralUtils.getUTCNowAsTimestamp() + ".txt");
        if (!outputFile.createNewFile()) {
            return;
        }

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile)));

        for (RealmHighlightsModel highlight : highlights) {
            String chartUuid = highlight.getStartPoint().getChart().getUuid();
            String chartTitle = highlight.getStartPoint().getChart().getTitle();
            String startPoint = "(" + highlight.getStartPoint().getxCoor() + ", " + highlight.getStartPoint().getyCoor() + ")";
            String endPoint = "(" + highlight.getEndPoint().getxCoor() + ", " + highlight.getEndPoint().getyCoor() + ")";

            bw.write(chartUuid + " " + chartTitle + " " + startPoint + " " + endPoint);
            bw.newLine();
        }

        bw.flush();
    }
}
