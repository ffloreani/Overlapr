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
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
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
    public View getView(int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.sorting_card, parent, false);
        }

        RealmChartModel chart = getItem(position);
        if (chart != null) {
            RealmLineDataSet<RealmPointModel> realmDataSet = getRealmDataSet(chart);

            LineChart lineChart = (LineChart) convertView.findViewById(R.id.card_chart);
            TextView cardTitle = (TextView) convertView.findViewById(R.id.card_title);

            cardTitle.setText(chart.getTitle());
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

        // Disable X-axis labels & grid axis
        lineChart.getXAxis().setDrawLabels(false);
        lineChart.getXAxis().setDrawGridLines(false);
        // Disable right Y-axis
        lineChart.getAxisRight().setEnabled(false);
        // Disable legend
        lineChart.getLegend().setEnabled(false);
        // Remove description
        Description emptyDesc = new Description();
        emptyDesc.setText("");
        lineChart.setDescription(emptyDesc);

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
