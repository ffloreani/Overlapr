package xyz.filipfloreani.overlapr.model;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Model of a highlighted section of a chart. Contains
 * the start point and end point of the section, both of type RealmPointModel.
 * <p>
 * Created by filipfloreani on 03/05/2017.
 */
public class RealmHighlightsModel extends RealmObject {

    @PrimaryKey
    private String uuid = UUID.randomUUID().toString();
    private RealmPointModel startPoint;
    private RealmPointModel endPoint;

    public RealmHighlightsModel() {
    }

    public RealmHighlightsModel(RealmPointModel startPoint, RealmPointModel endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public RealmPointModel getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(RealmPointModel startPoint) {
        this.startPoint = startPoint;
    }

    public RealmPointModel getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(RealmPointModel endPoint) {
        this.endPoint = endPoint;
    }
}
