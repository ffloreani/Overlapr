package xyz.filipfloreani.overlapr.graphing;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import io.realm.Realm;
import xyz.filipfloreani.overlapr.HomeActivity;
import xyz.filipfloreani.overlapr.model.RealmChartModel;
import xyz.filipfloreani.overlapr.model.RealmPointModel;
import xyz.filipfloreani.overlapr.utils.GeneralUtils;

/**
 * Created by filipfloreani on 15/05/2017.
 */
public class ChartsParserTask extends AsyncTask<Uri, Void, Void> {

    private static final String TAG = "ChartsParserTask";

    private ProgressDialog dialog;
    private HomeActivity activity;

    public ChartsParserTask(HomeActivity activity) {
        dialog = new ProgressDialog(activity);
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setMessage("Parsing charts to Realm...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                ChartsParserTask.this.cancel(true);
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                ChartsParserTask.this.cancel(true);
            }
        });
        dialog.show();
    }

    @Override
    protected Void doInBackground(Uri... params) {

        final File pafFile = new File(params[0].getPath());

        if (pafFile.exists() && pafFile.isFile()) {
            try (Realm realm = Realm.getDefaultInstance()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(pafFile)));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Create a RealmChartModel
                    String chartID = "Chart " + line.substring(0, line.indexOf(' '));
                    Log.d(TAG, "Creating chart: " + chartID);
                    RealmChartModel chartModel = new RealmChartModel(chartID, GeneralUtils.getUTCNow());

                    // Read each value and create a RealmPointModel for each one
                    int pointIndex = 1;
                    line = line.substring(line.indexOf(' ') + 1);
                    String split[] = line.split("\\s+");
                    for (int i = 0; i < split.length; i += 3) {
                        RealmPointModel pointModel = new RealmPointModel(pointIndex++, Float.parseFloat(split[i]), chartModel);
                        chartModel.addPoint(pointModel);
                    }

                    Log.d(TAG, "Saving to Realm...");
                    // Save the new chart to Realm
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(chartModel);
                    realm.commitTransaction();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
