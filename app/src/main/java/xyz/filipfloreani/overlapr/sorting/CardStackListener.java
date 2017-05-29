package xyz.filipfloreani.overlapr.sorting;

import android.util.Log;

import com.wenchao.cardstack.DefaultStackEventListener;

/**
 * Created by filipfloreani on 20/05/2017.
 */

public class CardStackListener extends DefaultStackEventListener {

    private static final String TAG = "CardStackListener";

    private SortingActivity sortingActivity;

    public CardStackListener(int detectionThreshold, SortingActivity sortingActivity) {
        super(detectionThreshold);
        this.sortingActivity = sortingActivity;
    }

    @Override
    public void discarded(int mIndex, int direction) {
        switch (direction) {
            case 0: // DIRECTION_TOP_LEFT
                Log.d(TAG, "EXIT TOP LEFT");
                sortingActivity.onExitTopLeft(mIndex);
                break;
            case 1: // DIRECTION_TOP_RIGHT
                Log.d(TAG, "EXIT TOP RIGHT");
                sortingActivity.onExitTopRight(mIndex);
                break;
            case 2: // DIRECTION_BOTTOM_LEFT
                Log.d(TAG, "EXIT BOTTOM LEFT");
                sortingActivity.onExitBottomLeft(mIndex);
                break;
            case 3: // DIRECTION_BOTTOM_RIGHT
                Log.d(TAG, "EXIT BOTTOM RIGHT");
                sortingActivity.onExitBottomRight(mIndex);
                break;
        }
    }
}
