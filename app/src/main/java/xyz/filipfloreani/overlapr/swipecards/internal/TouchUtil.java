package xyz.filipfloreani.overlapr.swipecards.internal;

import android.graphics.PointF;

import static java.lang.Math.abs;

/**
 * Utility class related to touch events.
 */
enum TouchUtil {
    ;

    /**
     * The sensitivity of the the card movements.
     */
    private static final double ON_CLICK_PIXEL_SENSITIVITY = 4.0;

    /**
     * Detects if the touch movement was small enough that we can ignore.
     *
     * @see #ON_CLICK_PIXEL_SENSITIVITY
     */
    static boolean minorMovement(PointF touchPosition, PointF lastTouchPosition) {
        return abs(touchPosition.x - lastTouchPosition.x) < ON_CLICK_PIXEL_SENSITIVITY &&
                abs(touchPosition.y - lastTouchPosition.y) < ON_CLICK_PIXEL_SENSITIVITY;
    }
}