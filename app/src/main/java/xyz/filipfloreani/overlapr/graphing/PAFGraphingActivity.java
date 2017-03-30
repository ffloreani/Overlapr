package xyz.filipfloreani.overlapr.graphing;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import xyz.filipfloreani.overlapr.R;

import static xyz.filipfloreani.overlapr.HomeActivity.EXTRA_PAF_PATH;

public class PAFGraphingActivity extends AppCompatActivity {

    private Uri filePath;
    private LineChart lineChart;
    private Button highlightButton;
    private TextView highlightTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pafgraphing);

        lineChart = (LineChart) findViewById(R.id.chart);
        lineChart.setHardwareAccelerationEnabled(true);

        highlightButton = (Button) findViewById(R.id.highlight_button);
        highlightTextView = (TextView) findViewById(R.id.highlight_value);

        // Read PAF path from intent
        filePath = getIntent().getParcelableExtra(EXTRA_PAF_PATH);

        

        parsePAFFile();
    }

    private void parsePAFFile() {
        new ParserTask(this).execute(filePath);
    }

    public void setDataToChart(List<Entry> chartEntries) {
        LineData lineData = new LineData(createLineDataSet(chartEntries));

        // Set up x-axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // Set up description
        Description desc = new Description();
        desc.setText("Overlaping reads");
        lineChart.setDescription(desc);

        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    private LineDataSet createLineDataSet(List<Entry> chartEntries) {
        LineDataSet dataSet = new LineDataSet(chartEntries, "Matches");
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setColor(Color.parseColor("#4CAF50"));
        dataSet.setDrawCircles(false);
        setupHighlighters(dataSet);

        return dataSet;
    }

    private void setupHighlighters(LineDataSet dataSet) {
        Highlight highlightLeft = new Highlight(100f, 0, 0);
        Highlight highlightRight = new Highlight(200f, 0, 0);

        dataSet.setDrawHorizontalHighlightIndicator(false);
        dataSet.setHighLightColor(Color.parseColor("#C62828"));
    }
}
