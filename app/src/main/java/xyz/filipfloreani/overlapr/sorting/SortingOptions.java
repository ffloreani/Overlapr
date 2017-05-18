package xyz.filipfloreani.overlapr.sorting;

/**
 * Created by filipfloreani on 16/05/2017.
 */

public enum SortingOptions {
    REGULAR,
    CHYMERIC,
    REPEAT,
    LOW_QUALITY,
    UNSORTED;

    public static SortingOptions get(int position) {
        switch (position) {
            case 0:
                return REGULAR;
            case 1:
                return CHYMERIC;
            case 2:
                return REPEAT;
            case 3:
                return LOW_QUALITY;
            case 4:
            default:
                return UNSORTED;
        }
    }
}
