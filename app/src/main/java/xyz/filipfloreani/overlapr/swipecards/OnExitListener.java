package xyz.filipfloreani.overlapr.swipecards;

import android.view.View;

import xyz.filipfloreani.overlapr.swipecards.internal.Direction;


/**
 *
 */
public interface OnExitListener {
    void onExit(View view, @Direction int direction);
}