package xyz.filipfloreani.overlapr.swipecards;

import android.database.DataSetObserver;

/**
 *
 */
class RequestLayoutObserver extends DataSetObserver {

    private final SwipeAdapterView adapterView;

    RequestLayoutObserver(SwipeAdapterView adapterView) {
        this.adapterView = adapterView;
    }

    @Override
    public void onChanged() {
        adapterView.requestLayout();
    }

    @Override
    public void onInvalidated() {
        adapterView.requestLayout();
    }

}
