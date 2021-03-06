package xyz.filipfloreani.overlapr.model;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import xyz.filipfloreani.overlapr.sorting.SortingOptions;

/**
 * Realm model of a single chart. Contains a title, creation date, a list of point objects and a sorting option.
 * <p>
 * Created by filipfloreani on 03/04/2017.
 */
public class RealmChartModel extends RealmObject {

    @PrimaryKey
    private String uuid = UUID.randomUUID().toString();
    private String title;
    private Date creationDate;
    private RealmList<RealmPointModel> points;
    private int sortingOption = SortingOptions.UNSORTED.ordinal();

    public RealmChartModel() {
    }

    public RealmChartModel(String title, Date creationDate) {
        this.title = title;
        this.creationDate = creationDate;
    }

    public RealmChartModel(String title, Date creationDate, RealmList<RealmPointModel> points) {
        this.title = title;
        this.creationDate = creationDate;
        this.points = points;
    }

    public RealmChartModel(String title, Date creationDate, RealmList<RealmPointModel> points, SortingOptions sortingOption) {
        this.title = title;
        this.creationDate = creationDate;
        this.points = points;
        this.sortingOption = sortingOption.ordinal();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public RealmList<RealmPointModel> getPoints() {
        return points;
    }

    public void setPoints(RealmList<RealmPointModel> points) {
        this.points = points;
    }

    public void addPoint(RealmPointModel point) {
        if (points == null) points = new RealmList<>();
        points.add(point);
    }

    public SortingOptions getSortingOption() {
        return SortingOptions.get(sortingOption);
    }

    public void setSortingOption(SortingOptions sortingOption) {
        this.sortingOption = sortingOption.ordinal();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RealmChartModel that = (RealmChartModel) o;

        if (!uuid.equals(that.uuid)) return false;
        return title.equals(that.title);

    }

    @Override
    public int hashCode() {
        int result = uuid.hashCode();
        result = 31 * result + title.hashCode();
        return result;
    }
}
