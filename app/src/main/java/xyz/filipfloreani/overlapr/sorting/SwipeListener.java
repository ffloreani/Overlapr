package xyz.filipfloreani.overlapr.sorting;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * A custom implementation of OnTouchListener used to
 * detect swipe actions from the user, recognize the direction of
 * them and call methods accordingly.
 *
 * Created by filipfloreani on 30/04/2017.
 */
public class SwipeListener implements OnTouchListener {

    private static final int MIN_DISTANCE = 250;

    private float downX, downY, upX, upY;
    private OnSwipeEvent onSwipeEvent;

    public SwipeListener(View v) {
        v.setOnTouchListener(this);
    }

    /**
     * Sets the given OnSwipeEvent as the swipe event action.
     *
     * @param swipeEvent Swipe event to set
     */
    public void setOnSwipeEvent(OnSwipeEvent swipeEvent) {
        onSwipeEvent = swipeEvent;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                return true;
            case MotionEvent.ACTION_UP:
                upX = event.getX();
                upY = event.getY();

                float deltaX = upX - downX;
                float deltaY = upY - downY;

                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    if (deltaX > 0) {
                        // Swipe left to right
                        onSwipeEvent.swipeEventDetected(v, SwipeDirectionEnum.LEFT_TO_RIGHT);
                    } else {
                        // Swipe right to left
                        onSwipeEvent.swipeEventDetected(v, SwipeDirectionEnum.RIGHT_TO_LEFT);
                    }

                    return true;
                } else if (Math.abs(deltaY) > MIN_DISTANCE) {
                    if (deltaY > 0) {
                        // Swipe top to bottom
                        onSwipeEvent.swipeEventDetected(v, SwipeDirectionEnum.TOP_TO_BOTTOM);
                    } else {
                        // Swipe bottom to top
                        onSwipeEvent.swipeEventDetected(v, SwipeDirectionEnum.BOTTOM_TO_TOP);
                    }

                    return true;
                } else {
                    return false; // The swipe wasn't long enough
                }
        }

        return false;
    }

    /**
     * Contains all recognized swiping directions.
     */
    public enum SwipeDirectionEnum {
        RIGHT_TO_LEFT,
        LEFT_TO_RIGHT,
        TOP_TO_BOTTOM,
        BOTTOM_TO_TOP
    }
}
