package xyz.filipfloreani.overlapr.adapter;

import android.view.View;

import xyz.filipfloreani.overlapr.model.RealmChartModel;

/**
 * Created by filipfloreani on 03/05/2017.
 */

public interface OnHistoryItemClickListener {
    void onItemClick(View v, int position);
    boolean onItemLongClick(View v, RealmChartModel chartModel);
}
