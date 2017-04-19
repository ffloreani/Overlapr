package xyz.filipfloreani.overlapr.graphing;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;

import com.github.mikephil.charting.data.Entry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by filipfloreani on 28/03/2017.
 */

public class ParserTask extends AsyncTask<Uri, Void, List<Entry>> {

    private static final String REGEX_GROUP_PAF = "(\\d+)";

    private ProgressDialog dialog;
    private PAFGraphingActivity activity;

    public ParserTask(PAFGraphingActivity activity) {
        dialog = new ProgressDialog(activity);
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        dialog.setMessage("Parsing selected PAF file...");
        dialog.setIndeterminate(true);
        dialog.show();
    }

    // Parse PAF to some array or list, extracting only
    // information that is important for graphing.
    @Override
    protected List<Entry> doInBackground(Uri... params) {
        List<Entry> chartEntries = new Vector<>();

        final Pattern pattern = Pattern.compile(REGEX_GROUP_PAF);
        final File pafFile = new File(params[0].getPath());

        if (pafFile.exists() && pafFile.isFile()) {
            try {
                FileInputStream fio = new FileInputStream(pafFile);
                BufferedReader reader = new BufferedReader(new InputStreamReader(fio));

                String line;
                int i = 1;
                while ((line = reader.readLine()) != null) {
                    float graphingValue = 0;

                    final Matcher matcher = pattern.matcher(line);
                    int groupCounter = 1;
                    while (matcher.find()) {
                        if (groupCounter == 9) { // Adding only 9th number group to the entry list
                            graphingValue = Float.parseFloat(matcher.group(1));
                            break;
                        }
                        groupCounter++;
                    }

                    Entry entry = new Entry(i++, graphingValue);
                    chartEntries.add(entry);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return chartEntries;
    }

    @Override
    protected void onPostExecute(List<Entry> chartEntries) {
        super.onPostExecute(chartEntries);
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        activity.setDataToChart(chartEntries);
    }
}