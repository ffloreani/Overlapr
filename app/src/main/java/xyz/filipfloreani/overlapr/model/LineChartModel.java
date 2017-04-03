package xyz.filipfloreani.overlapr.model;

import android.provider.BaseColumns;

import com.github.mikephil.charting.data.LineDataSet;

import java.util.Date;

/**
 * Created by filipfloreani on 03/04/2017.
 */

public class LineChartModel {

    private String title;
    private Date creationDate;
    private LineDataSet lineDataSet;

    public LineChartModel(String title, Date creationDate, LineDataSet lineDataSet) {
        this.title = title;
        this.creationDate = creationDate;
        this.lineDataSet = lineDataSet;
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

    public LineDataSet getLineDataSet() {
        return lineDataSet;
    }

    public void setLineDataSet(LineDataSet lineDataSet) {
        this.lineDataSet = lineDataSet;
    }

    public static class LineChartEntry implements BaseColumns {
        public static final String TABLE_NAME = "line_chart";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_CREATION_DATE = "creation_date";
        public static final String COLUMN_NAME_CHART_DATA = "chart_data";
    }
}
