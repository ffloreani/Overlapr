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
import xyz.filipfloreani.overlapr.model.RealmPointModel;
import xyz.filipfloreani.overlapr.utils.GeneralUtils;

import static xyz.filipfloreani.overlapr.HomeActivity.EXTRA_PAF_PATH;
import static xyz.filipfloreani.overlapr.HomeActivity.SHARED_PREF_HOME_ACTIVITY;

public class PAFGraphingActivity extends AppCompatActivity {

    private Realm realm;
    private RealmAsyncTask writeTransaction = null;

    private LineChart lineChart;
    private Button highlightButton;
    private TextView highlightTextView;

    private Uri filePath;
    private boolean isGraphShown = false;
    private String graphTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pafgraphing);

        realm = Realm.getDefaultInstance();

        getGraphTitle();
        setTitle(graphTitle);

        lineChart = (LineChart) findViewById(R.id.chart);
        lineChart.setHardwareAccelerationEnabled(true);
        lineChart.setScaleYEnabled(false);

        highlightButton = (Button) findViewById(R.id.highlight_button);
        highlightTextView = (TextView) findViewById(R.id.highlight_value);

        // Read PAF path from intent
        filePath = getIntent().getParcelableExtra(EXTRA_PAF_PATH);

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
            builder.setMessage("You must have a graph drawn in order to show the highlight value.");
            builder.show();
        }

        Highlight[] highlighted = lineChart.getHighlighted();
        if (highlighted.length > 0) {
            highlightTextView.setText("Highlighted: (" + highlighted[0].getX() + ", " + highlighted[0].getY() + ")");
        }
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

        writeToRealm(chartEntries);
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
    private void writeToRealm(final List<Entry> dataSet) {
        writeTransaction = realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmChartModel chartModel = new RealmChartModel(graphTitle, GeneralUtils.getUTCNow());
                RealmList<RealmPointModel> pointModels = GeneralUtils.entriesToChartPoints(dataSet, chartModel);

                chartModel.setPoints(pointModels);

                realm.copyToRealm(pointModels);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(PAFGraphingActivity.this.getLocalClassName(), "Chart successfully copied to Realm!");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.d(PAFGraphingActivity.this.getLocalClassName(), "Chart copying failed.");
                error.printStackTrace();
            }
        });
    }
}
