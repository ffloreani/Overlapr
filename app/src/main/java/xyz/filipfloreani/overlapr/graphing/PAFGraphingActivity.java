package xyz.filipfloreani.overlapr.graphing;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmList;
import xyz.filipfloreani.overlapr.HomeActivity;
import xyz.filipfloreani.overlapr.R;
import xyz.filipfloreani.overlapr.model.RealmChartModel;
import xyz.filipfloreani.overlapr.model.RealmHighlightsModel;
import xyz.filipfloreani.overlapr.model.RealmPointModel;
import xyz.filipfloreani.overlapr.utils.GeneralUtils;

import static xyz.filipfloreani.overlapr.HomeActivity.EXTRA_PAF_PATH;
import static xyz.filipfloreani.overlapr.HomeActivity.SHARED_PREF_HOME_ACTIVITY;

public class PAFGraphingActivity extends AppCompatActivity {

    private static final String TAG = PAFGraphingActivity.class.getSimpleName();

    private Realm realm;
    private RealmAsyncTask writeTransaction = null;

    private LineChart lineChart;
    private Button highlightButton;
    private TextView startPointTextView;
    private TextView endPointTextView;

    private Uri filePath;
    private boolean isGraphShown = false;
    private String graphTitle;

    private RealmChartModel chartModel = null;
    private RealmHighlightsModel highlightsModel = new RealmHighlightsModel();
    private boolean wasStartMarked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pafgraphing);

        realm = Realm.getDefaultInstance();

        getGraphTitle();
        setTitle(graphTitle);

        // Line chart view
        lineChart = (LineChart) findViewById(R.id.chart);
        lineChart.setHardwareAccelerationEnabled(true);
        lineChart.setScaleYEnabled(false);

        // Highlighting button & text views
        highlightButton = (Button) findViewById(R.id.highlight_button);
        startPointTextView = (TextView) findViewById(R.id.start_point_value);
        endPointTextView = (TextView) findViewById(R.id.end_point_value);

        // Read PAF path from intent
        filePath = getIntent().getParcelableExtra(EXTRA_PAF_PATH);

        highlightButton.setVisibility(View.GONE);
        highlightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHighlightValue();
            }
        });

        // Parse the file at the received path
        parsePAFFile();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (writeTransaction != null && !writeTransaction.isCancelled()) {
            writeTransaction.cancel();
        }
        realm.close();
    }

    /**
     * Gets the title for this graph from the HomeActivity SharedPreferences storage.
     */
    private void getGraphTitle() {
        SharedPreferences preferences = getSharedPreferences(SHARED_PREF_HOME_ACTIVITY, MODE_PRIVATE);
        graphTitle = preferences.getString(HomeActivity.SHARED_PREF_GRAPH_TITLE, "Overlapr graph " + GeneralUtils.getUTCNow());
    }

    private void showHighlightValue() {
        if (!isGraphShown) {
            AlertDialog.Builder builder = GeneralUtils.buildWatchOutDialog(this);
            builder.setMessage("You must have a graph drawn in order to highlight a value.").show();
            return;
        }

        Highlight[] highlighted = lineChart.getHighlighted();
        if (highlighted != null && highlighted.length > 0) {
            if (!wasStartMarked) {
                // Remember the starting point
                RealmPointModel startPoint = getPointFromHighlight(highlighted[0]);
                highlightsModel.setStartPoint(startPoint);
                wasStartMarked = true;

                startPointTextView.setText("Start: (" + startPoint.getxCoor() + ", " + startPoint.getyCoor() + ")");
            } else {
                // Remember the end point & insert/update the model into Realm
                RealmPointModel endPoint = getPointFromHighlight(highlighted[0]);
                highlightsModel.setEndPoint(endPoint);
                wasStartMarked = false;

                endPointTextView.setText("End: (" + endPoint.getxCoor() + ", " + endPoint.getyCoor() + ")");

                writeHighlightsToRealm();
            }
        } else {
            Toast.makeText(this, "Position the highlighter first!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * For a given highlight, returns a matching point in the current chart.
     *
     * @param highlight The highlight to get a point from
     * @return RealmPointModel matching the highlighted point
     */
    private RealmPointModel getPointFromHighlight(Highlight highlight) {
        return realm.where(RealmPointModel.class)
                .equalTo("xCoor", highlight.getX())
                .equalTo("yCoor", highlight.getY())
                .equalTo("chart.uuid", chartModel.getUuid()).findFirst();
    }

    /**
     * Starts a new ParserTask with the received file path
     */
    private void parsePAFFile() {
        new ParserTask(this).execute(filePath);
    }

    public void setDataToChart(List<Entry> chartEntries) {
        LineDataSet dataSet = createLineDataSet(chartEntries);
        LineData lineData = new LineData(dataSet);

        // Set up X-axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // Set up Y-axis
        YAxis yAxis = lineChart.getAxisRight();
        yAxis.setEnabled(false);

        // Set up description
        Description desc = new Description();
        desc.setText("Overlaping reads");
        lineChart.setDescription(desc);

        lineChart.setData(lineData);
        lineChart.invalidate();

        isGraphShown = true;
        highlightButton.setVisibility(View.VISIBLE);

        writeChartToRealm(chartEntries);
    }

    private LineDataSet createLineDataSet(List<Entry> chartEntries) {
        LineDataSet dataSet = new LineDataSet(chartEntries, "Matches");
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setColor(Color.parseColor("#3C9F40"));
        dataSet.setDrawCircles(false);

        Drawable gradientDrawable = ContextCompat.getDrawable(this, R.drawable.fade_green);
        dataSet.setFillDrawable(gradientDrawable);
        dataSet.setDrawFilled(true);

        dataSet.setDrawHorizontalHighlightIndicator(false);
        dataSet.setHighLightColor(Color.parseColor("#C62828"));

        return dataSet;
    }

    /**
     * Write the given dataset into the Realm DB.
     *
     * @param dataSet The dataset to be written to the database
     */
    private void writeChartToRealm(final List<Entry> dataSet) {
        writeTransaction = realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                chartModel = new RealmChartModel(graphTitle, GeneralUtils.getUTCNow());
                RealmList<RealmPointModel> pointModels = GeneralUtils.entriesToChartPoints(dataSet, chartModel);

                chartModel.setPoints(pointModels);

                realm.copyToRealm(pointModels);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Chart successfully copied to Realm!");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.d(TAG, "Chart copying failed.");
                error.printStackTrace();
            }
        });
    }

    private void writeHighlightsToRealm() {
        if (highlightsModel != null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm bgRealm) {
                    // TODO Look this over, it's creating multiple charts when changing highlights for a single chart
                    bgRealm.copyToRealmOrUpdate(highlightsModel);

                    Log.d(TAG, "Highlights successfully copied to Realm!");
                    Toast.makeText(PAFGraphingActivity.this, "Highlighting complete!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
