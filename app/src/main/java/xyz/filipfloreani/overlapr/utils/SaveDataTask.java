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
import xyz.filipfloreani.overlapr.model.RealmChartModel;
import xyz.filipfloreani.overlapr.model.RealmHighlightsModel;
import xyz.filipfloreani.overlapr.sync.HttpOperations;

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
            RealmResults<RealmChartModel> sortedCharts = realm.where(RealmChartModel.class).notEqualTo("sortingOption", 4).findAll();

            File outputFile = writeToFile(highlights, sortedCharts);
            if (outputFile != null) {
                HttpOperations.postFile(outputFile, context);
            }
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
     * <p>
     * 'Chart UUID' 'Chart title' '(Start point X, start point Y)' '(End point X, end point Y)'
     *
     * @param highlights   The highlights to write to the output file
     * @param sortedCharts Collection of charts that have been sorted and are to be written to the output file
     * @throws IOException Thrown in case of BufferedReader failure
     */
    private File writeToFile(RealmResults<RealmHighlightsModel> highlights, RealmResults<RealmChartModel> sortedCharts) throws IOException {
        if (highlights.isEmpty()) return null;

        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return null;
        }

        File outputDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Overlapr results/");
        if (!outputDir.exists()) {
            if (!outputDir.mkdirs()) Log.d(TAG, "Results directory not created");
        }

        File outputFile = new File(outputDir + File.separator + "Output " + GeneralUtils.getUTCNowAsTimestamp() + ".txt");
        if (!outputFile.createNewFile()) {
            return null;
        }

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile)))) {
            for (RealmHighlightsModel highlight : highlights) {
                String chartTitle = highlight.getStartPoint().getChart().getTitle();
                String startPoint = "(" + Math.round(highlight.getStartPoint().getxCoor() * 6) + ", " + Math.round(highlight.getStartPoint().getyCoor()) + ")";
                String endPoint = "(" + Math.round(highlight.getEndPoint().getxCoor() * 6) + ", " + Math.round(highlight.getEndPoint().getyCoor()) + ")";

                bw.write(chartTitle + " " + startPoint + " " + endPoint);
                bw.newLine();
            }
            bw.newLine();

            for (RealmChartModel chart : sortedCharts) {
                String chartTitle = chart.getTitle();
                String sortingOption = chart.getSortingOption().toString();

                bw.write(chartTitle + " " + sortingOption);
                bw.newLine();
            }

            bw.flush();
        }

        return outputFile;
    }
}
