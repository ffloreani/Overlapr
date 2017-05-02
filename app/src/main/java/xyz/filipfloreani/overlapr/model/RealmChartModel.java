package xyz.filipfloreani.overlapr.model;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Realm model of a single chart. Contains a title, creation date and a list of point objects.
 * <p>
 * Created by filipfloreani on 03/04/2017.
 */
public class RealmChartModel extends RealmObject {

    @PrimaryKey
    private String uuid = UUID.randomUUID().toString();
    private String title;
    private Date creationDate;
    private RealmList<RealmPointModel> points;

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
}
