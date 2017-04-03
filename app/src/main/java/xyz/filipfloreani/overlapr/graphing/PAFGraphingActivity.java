package xyz.filipfloreani.overlapr.graphing;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import xyz.filipfloreani.overlapr.utils.GeneralUtils;
import xyz.filipfloreani.overlapr.R;

import static xyz.filipfloreani.overlapr.HomeActivity.EXTRA_PAF_PATH;

public class PAFGraphingActivity extends AppCompatActivity {

    private LineChart lineChart;
    private Button highlightButton;
    private TextView highlightTextView;

    private Uri filePath;
    private boolean isGraphShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pafgraphing);

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

        parsePAFFile();
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

    private void parsePAFFile() {
        new ParserTask(this).execute(filePath);
    }

    public void setDataToChart(List<Entry> chartEntries) {
        LineData lineData = new LineData(createLineDataSet(chartEntries));

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
    }

    private LineDataSet createLineDataSet(List<Entry> chartEntries) {
        LineDataSet dataSet = new LineDataSet(chartEntries, "Matches");
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setColor(Color.parseColor("#4CAF50"));
        dataSet.setDrawCircles(false);

        dataSet.setDrawHorizontalHighlightIndicator(false);
        dataSet.setHighLightColor(Color.parseColor("#C62828"));

        return dataSet;
    }

}