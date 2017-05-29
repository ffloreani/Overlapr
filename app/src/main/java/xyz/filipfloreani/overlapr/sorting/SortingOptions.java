package xyz.filipfloreani.overlapr.sorting;

/**
 * Created by filipfloreani on 16/05/2017.
 */

public enum SortingOptions {
    REGULAR("Regular"),
    CHIMERIC("Chimeric"),
    REPEAT("Repeat"),
    LOW_QUALITY("Low quality"),
    UNSORTED("Unsorted");

    private String name;

    SortingOptions(String name) {
        this.name = name;
    }

    public static SortingOptions get(int position) {
        switch (position) {
            case 0:
                return REGULAR;
            case 1:
                return CHIMERIC;
            case 2:
                return REPEAT;
            case 3:
                return LOW_QUALITY;
            case 4:
            default:
                return UNSORTED;
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
