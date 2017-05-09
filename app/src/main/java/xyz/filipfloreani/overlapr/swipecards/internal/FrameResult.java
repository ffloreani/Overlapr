package xyz.filipfloreani.overlapr.swipecards.internal;

import static java.lang.Math.abs;
import static xyz.filipfloreani.overlapr.swipecards.internal.Direction.CENTER;
import static xyz.filipfloreani.overlapr.swipecards.internal.Direction.DOWN;
import static xyz.filipfloreani.overlapr.swipecards.internal.Direction.LEFT;
import static xyz.filipfloreani.overlapr.swipecards.internal.Direction.RIGHT;
import static xyz.filipfloreani.overlapr.swipecards.internal.Direction.UP;
import static xyz.filipfloreani.overlapr.swipecards.internal.EndEvent.EXIT;
import static xyz.filipfloreani.overlapr.swipecards.internal.EndEvent.RECENTER;

/**
 * The resulting details for the view when the user moves up or cancels the drag events.
 */
class FrameResult {

    private static final FrameResult CLICK = new FrameResult(EndEvent.CLICK, CENTER);
    private final int endEventYpe;
    private final int direction;

    /**
     * Creates a new instance for the given scrolling progress
     *
     * @param scrollProgress the progress of scrolling for the view.
     * @return a new instance of {@link FrameResult}
     */
    static FrameResult fromScrollProgress(ScrollProgress scrollProgress) {

        if (abs(scrollProgress.progress - 1) == 0) {
            switch (scrollProgress.direction) {
                case DOWN:
                    return new FrameResult(EXIT, DOWN);
                case LEFT:
                    return new FrameResult(EXIT, LEFT);
                case RIGHT:
                    return new FrameResult(EXIT, RIGHT);
                case UP:
                    return new FrameResult(EXIT, UP);
            }
        }

        return new FrameResult(RECENTER, CENTER);
    }

    /**
     * Creates a instance related to a click event.
     *
     * @return a new instance of {@link FrameResult}
     */
    static FrameResult click() {
        return CLICK;
    }

    private FrameResult(@EndEvent int endEventYpe, @Direction int direction) {
        this.endEventYpe = endEventYpe;
        this.direction = direction;
    }

    @EndEvent
    int getEndEvent() {
        return endEventYpe;
    }

    @Direction
    int getDirection() {
        return direction;
    }

}
