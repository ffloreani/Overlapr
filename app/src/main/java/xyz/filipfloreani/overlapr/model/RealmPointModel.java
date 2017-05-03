package xyz.filipfloreani.overlapr.model;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * A Realm model of a single point of a chart, containing it's x and y coordinates.
 * <p>
 * Created by filipfloreani on 01/05/2017.
 */
public class RealmPointModel extends RealmObject {

    @PrimaryKey
    private String uuid = UUID.randomUUID().toString();
    private float xCoor;
    private float yCoor;
    private RealmChartModel chart;

    public RealmPointModel() {
    }

    public RealmPointModel(float xCoor, float yCoor, RealmChartModel chart) {
        this.xCoor = xCoor;
        this.yCoor = yCoor;
        this.chart = chart;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public float getxCoor() {
        return xCoor;
    }

    public void setxCoor(float xCoor) {
        this.xCoor = xCoor;
    }

    public float getyCoor() {
        return yCoor;
    }

    public void setyCoor(float yCoor) {
        this.yCoor = yCoor;
    }

    public RealmChartModel getChart() {
        return chart;
    }

    public void setChart(RealmChartModel chart) {
        this.chart = chart;
    }
}
