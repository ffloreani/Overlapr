package xyz.filipfloreani.overlapr.swipecards;

import android.view.View;

import xyz.filipfloreani.overlapr.swipecards.internal.Direction;

/**
 *
 */
public interface OnScrollListener {
    void onScroll(View view, float scrollProgressPercent, @Direction int direction);
}
