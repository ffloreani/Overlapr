package xyz.filipfloreani.overlapr.graphing;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.realm.implementation.RealmLineDataSet;
import com.github.mikephil.charting.highlight.Highlight;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmResults;
import xyz.filipfloreani.overlapr.HomeActivity;
import xyz.filipfloreani.overlapr.R;
import xyz.filipfloreani.overlapr.model.RealmChartModel;
import xyz.filipfloreani.overlapr.model.RealmHighlightsModel;
import xyz.filipfloreani.overlapr.model.RealmPointModel;
import xyz.filipfloreani.overlapr.utils.GeneralUtils;

import static xyz.filipfloreani.overlapr.HomeActivity.SHARED_PREF_HOME_ACTIVITY;

public class GraphingActivity extends AppCompatActivity {

    private static final String TAG = GraphingActivity.class.getSimpleName();

    private Realm realm;
    private RealmAsyncTask writeTransaction = null;

    private LineChart lineChart;
    private Button highlightButton;
    private TextView startPointTextView;
    private TextView endPointTextView;

    private boolean isGraphShown = false;

    private String chartUuid = null;
    private boolean wasStartMarked = false;

    private float startX;
    private float startY;
    private float endX;
    private float endY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pafgraphing);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        realm = Realm.getDefaultInstance();

        // Line chart view
        lineChart = (LineChart) findViewById(R.id.chart);
        lineChart.setHardwareAccelerationEnabled(true);
        lineChart.setScaleYEnabled(false);

        // Highlighting button & text views
        highlightButton = (Button) findViewById(R.id.highlight_button);
        startPointTextView = (TextView) findViewById(R.id.start_point_value);
        endPointTextView = (TextView) findViewById(R.id.end_point_value);

        highlightButton.setVisibility(View.GONE);
        highlightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHighlightValue();
            }
        });

        chartUuid = getUUIDFromSharedPrefs();
        setTitle(getChartTitle(chartUuid));
        createChart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (writeTransaction != null && !writeTransaction.isCancelled()) {
            writeTransaction.cancel();
        }
        realm.close();
    }

    private String getUUIDFromSharedPrefs() {
        SharedPreferences sp = getSharedPreferences(SHARED_PREF_HOME_ACTIVITY, MODE_PRIVATE);
        return sp.getString(HomeActivity.SHARED_PREF_CHART_UUID, null);
    }

    private void showHighlightValue() {
        if (!isGraphShown) {
            AlertDialog.Builder builder = GeneralUtils.buildWatchOutDialog(this);
            builder.setMessage(R.string.no_graph_error).show();
            return;
        }

        Highlight[] highlighted = lineChart.getHighlighted();

        if (highlighted != null && highlighted.length > 0) {
            if (!wasStartMarked) {
                // Remember the starting point
                startX = highlighted[0].getX();
                startY = highlighted[0].getY();

                wasStartMarked = true;

                startPointTextView.setText("Start: (" + startX + ", " + startY + ")");
            } else {
                // Remember the end point & insert/update the model into Realm
                endX = highlighted[0].getX();
                endY = highlighted[0].getY();

                wasStartMarked = false;

                if (startX >= endX) {
                    Toast.makeText(this, R.string.start_end_warning, Toast.LENGTH_SHORT).show();
                    startPointTextView.setText("");
                    return;
                }

                endPointTextView.setText("End: (" + endX + ", " + endY + ")");

                writeHighlightsToRealm(startX, startY, endX, endY);
            }
        } else {
            Toast.makeText(this, R.string.position_highlighter, Toast.LENGTH_SHORT).show();
        }
    }

    private void createChart() {
        if (chartUuid != null) {
            // Read points from Realm and show the chart
            if (isChartHighlighted()) {
                setDataToHighlightedChart();
            } else {
                setDataToChart();
            }
        }
    }

    private boolean isChartHighlighted() {
        return realm.where(RealmHighlightsModel.class).equalTo("parentChart.uuid", chartUuid).findFirst() != null;
    }

    private void setDataToHighlightedChart() {
        RealmHighlightsModel highlight = realm.where(RealmHighlightsModel.class).equalTo("parentChart.uuid", chartUuid).findFirst();
        float highlightStartXCoor = highlight.getStartPoint().getxCoor();
        float highlightEndXCoor = highlight.getEndPoint().getxCoor();

        // Get separate realm result lists
        RealmResults<RealmPointModel> pointsLeftOfHighlight = realm.where(RealmPointModel.class).equalTo("chart.uuid", chartUuid).lessThanOrEqualTo("xCoor", highlightStartXCoor).findAll();
        RealmResults<RealmPointModel> highlightedPoints = realm.where(RealmPointModel.class).equalTo("chart.uuid", chartUuid).between("xCoor", highlightStartXCoor, highlightEndXCoor).findAll();
        RealmResults<RealmPointModel> pointsRightOfHighlight = realm.where(RealmPointModel.class).equalTo("chart.uuid", chartUuid).greaterThanOrEqualTo("xCoor", highlightEndXCoor).findAll();

        // Create custom non-highlight sets
        RealmLineDataSet<RealmPointModel> dataSetLeft = createCustomDataSet(pointsLeftOfHighlight, false);
        RealmLineDataSet<RealmPointModel> dataSetRight = createCustomDataSet(pointsRightOfHighlight, false);

        // Create custom highlight set
        RealmLineDataSet<RealmPointModel> dataSetHighlight = createCustomDataSet(highlightedPoints, true);

        RealmLineDataSet[] dataArray = new RealmLineDataSet[3];
        dataArray[0] = dataSetLeft;
        dataArray[1] = dataSetHighlight;
        dataArray[2] = dataSetRight;

        configureChart(new LineData(dataArray));
    }

    private void setDataToChart() {
        RealmResults<RealmPointModel> points = getAllPointsForChartUUID(chartUuid);
        RealmLineDataSet<RealmPointModel> realmDataSet = createCustomDataSet(points, false);

        configureChart(new LineData(realmDataSet));
    }

    private RealmLineDataSet<RealmPointModel> createCustomDataSet(RealmResults<RealmPointModel> points, boolean isHighlightSet) {
        RealmLineDataSet<RealmPointModel> dataSet = new RealmLineDataSet<>(points, "xCoor", "yCoor");

        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setColor(isHighlightSet ? R.color.highlightChartColor : R.color.regularChartColor);
        dataSet.setDrawCircles(false);

        Drawable gradientDrawable = ContextCompat.getDrawable(this, isHighlightSet ? R.drawable.fade_red : R.drawable.fade_green);
        dataSet.setFillDrawable(gradientDrawable);
        dataSet.setDrawFilled(true);

        dataSet.setDrawHorizontalHighlightIndicator(false);
        dataSet.setHighLightColor(R.color.highlighterColor);

        return dataSet;
    }

    private void configureChart(LineData lineData) {
        // Set up X-axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // Set up Y-axis
        YAxis yAxis = lineChart.getAxisRight();
        yAxis.setEnabled(false);

        lineChart.getLegend().setEnabled(false);

        // Set up description
        Description desc = new Description();
        desc.setText(getString(R.string.overlap_reads));
        lineChart.setDescription(desc);

        lineChart.setData(lineData);

        lineChart.invalidate();

        isGraphShown = true;
        highlightButton.setVisibility(View.VISIBLE);
    }

    private String getChartTitle(String chartUuid) {
        return realm.where(RealmChartModel.class).equalTo("uuid", chartUuid).findFirst().getTitle();
    }

    /**
     * For a given chart UUID, returns all it's points from Realm.
     *
     * @param chartUuid The chart UUID to get a chart points for
     * @return RealmResults containing all points for the chart with the given UUID
     */
    private RealmResults<RealmPointModel> getAllPointsForChartUUID(String chartUuid) {
        return realm.where(RealmPointModel.class).equalTo("chart.uuid", chartUuid).findAll();
    }

    private void writeHighlightsToRealm(final float startX, final float startY, final float endX, final float endY) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                RealmHighlightsModel highlightsModel = bgRealm.where(RealmHighlightsModel.class)
                        .equalTo("parentChart.uuid", chartUuid)
                        .findFirst();

                RealmPointModel startPoint = bgRealm.where(RealmPointModel.class)
                        .equalTo("xCoor", startX)
                        .equalTo("yCoor", startY)
                        .equalTo("chart.uuid", chartUuid).findFirst();

                RealmPointModel endPoint = bgRealm.where(RealmPointModel.class)
                        .equalTo("xCoor", endX)
                        .equalTo("yCoor", endY)
                        .equalTo("chart.uuid", chartUuid).findFirst();

                RealmChartModel parentChart = bgRealm.where(RealmChartModel.class)
                        .equalTo("uuid", chartUuid)
                        .findFirst();

                if (highlightsModel != null) {
                    highlightsModel.setStartPoint(startPoint);
                    highlightsModel.setEndPoint(endPoint);
                    bgRealm.copyToRealmOrUpdate(highlightsModel);
                } else {
                    highlightsModel = bgRealm.createObject(RealmHighlightsModel.class, UUID.randomUUID().toString());
                    highlightsModel.setStartPoint(startPoint);
                    highlightsModel.setEndPoint(endPoint);
                    highlightsModel.setParentChart(parentChart);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Highlights successfully copied to Realm!");
                Toast.makeText(GraphingActivity.this, R.string.highlight_complete, Toast.LENGTH_SHORT).show();
                createChart();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.d(TAG, "Failed to copy highlight to realm");
                Toast.makeText(GraphingActivity.this, R.string.highlight_error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
