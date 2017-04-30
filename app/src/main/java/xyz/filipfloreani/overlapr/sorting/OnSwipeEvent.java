package xyz.filipfloreani.overlapr.sorting;

import android.view.View;

/**
 * Created by filipfloreani on 30/04/2017.
 */
public interface OnSwipeEvent {
    void swipeEventDetected(View v, SwipeListener.SwipeDirectionEnum swipeDirection);
}
