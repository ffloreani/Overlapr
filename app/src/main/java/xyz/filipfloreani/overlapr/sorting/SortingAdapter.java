package xyz.filipfloreani.overlapr.sorting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.github.mikephil.charting.charts.LineChart;

import java.util.List;

import io.realm.RealmResults;
import xyz.filipfloreani.overlapr.R;
import xyz.filipfloreani.overlapr.model.RealmChartModel;
import xyz.filipfloreani.overlapr.model.RealmPointModel;

/**
 * Created by filipfloreani on 01/05/2017.
 */

class SortingAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private List<RealmChartModel> charts = null;

    public SortingAdapter(Context context, List<RealmChartModel> charts) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.charts = charts;
    }

    @Override
    public int getCount() {
        if (charts != null) {
            return charts.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (charts != null) {
            return charts.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.sorting_card, parent, false);
        }

        RealmChartModel chart = charts.get(position);
        if (chart != null) {
//            ((LineChart) convertView.findViewById(R.id.card_chart));
        }

        return convertView;
    }
}
