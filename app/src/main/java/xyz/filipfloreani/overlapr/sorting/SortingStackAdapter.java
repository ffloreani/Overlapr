package xyz.filipfloreani.overlapr.sorting;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.realm.implementation.RealmLineDataSet;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import xyz.filipfloreani.overlapr.R;
import xyz.filipfloreani.overlapr.model.RealmChartModel;
import xyz.filipfloreani.overlapr.model.RealmPointModel;

/**
 * Created by filipfloreani on 20/05/2017.
 */

class SortingStackAdapter extends ArrayAdapter<RealmChartModel> {

    private Realm realm;

    private LayoutInflater layoutInflater;

    public SortingStackAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<RealmChartModel> objects, Realm realm) {
        super(context, resource, objects);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.realm = realm;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.sorting_card, parent, false);
        }

        RealmChartModel chart = getItem(position);
        if (chart != null) {
            RealmLineDataSet<RealmPointModel> realmDataSet = getRealmDataSet(chart);
            LineChart lineChart = (LineChart) convertView.findViewById(R.id.card_chart);

            setDataSetToChart(realmDataSet, lineChart);
        }

        return convertView;
    }

    /**
     * For a given RealmChartModel, queries Realm for all points that are assigned to it and
     * returns them as a RealmLineDataSet
     *
     * @param chart RealmChartModel for which to find chart points
     * @return RealmLineDataSet containing points to plot on the chart
     */
    private RealmLineDataSet<RealmPointModel> getRealmDataSet(RealmChartModel chart) {
        RealmResults<RealmPointModel> realmPoints = realm.where(RealmPointModel.class).equalTo("chart.uuid", chart.getUuid()).findAll();
        return new RealmLineDataSet<>(realmPoints, "xCoor", "yCoor");
    }

    /**
     * Sets the given data set to the given chart, after which it invalidates the chart.
     *
     * @param realmDataSet Data set to be drawn on the chart
     * @param lineChart    The chart that the data set is to be drawn on
     */
    private void setDataSetToChart(RealmLineDataSet<RealmPointModel> realmDataSet, LineChart lineChart) {
        customizeDataSet(realmDataSet);

        LineData lineData = new LineData(realmDataSet);

        lineData.setHighlightEnabled(false);

        // Set up X-axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // Set up Y-axis
        YAxis yAxis = lineChart.getAxisRight();
        yAxis.setEnabled(false);

        lineChart.setScaleEnabled(false);

        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    private void customizeDataSet(RealmLineDataSet<RealmPointModel> realmDataSet) {
        realmDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        realmDataSet.setColor(Color.parseColor("#3C9F40"));
        realmDataSet.setDrawCircles(false);

        Drawable gradientDrawable = ContextCompat.getDrawable(getContext(), R.drawable.fade_green);
        realmDataSet.setFillDrawable(gradientDrawable);
        realmDataSet.setDrawFilled(true);
    }
}