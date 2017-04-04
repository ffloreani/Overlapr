package xyz.filipfloreani.overlapr.db;

import xyz.filipfloreani.overlapr.model.LineChartModel;

/**
 * Created by filipfloreani on 03/04/2017.
 */
public final class SQLExpression {
    public static final String CREATE_GRAPH_TABLE =
            "CREATE TABLE " + LineChartModel.LineChartEntry.TABLE_NAME + " (" +
                    LineChartModel.LineChartEntry._ID + " INTEGER PRIMARY KEY," +
                    LineChartModel.LineChartEntry.COLUMN_NAME_TITLE + "TEXT," +
                    LineChartModel.LineChartEntry.COLUMN_NAME_CREATION_DATE + " INTEGER," +
                    LineChartModel.LineChartEntry.COLUMN_NAME_CHART_DATA + " TEXT)";

    public static final String DELETE_GRAPH_TABLE =
            "DROP TABLE IF EXISTS " + LineChartModel.LineChartEntry.TABLE_NAME;

    public static final String SELECT_ALL_GRAPH =
            "SELECT * FROM " + LineChartModel.LineChartEntry.TABLE_NAME;

    public static final String SELECT_GRAPH_WHERE_ID =
            "SELECT * FROM " + LineChartModel.LineChartEntry.TABLE_NAME +
                    " WHERE " + LineChartModel.LineChartEntry._ID + " = ";

    public static final String SELECT_GRAPH_WHERE_TITLE =
            "SELECT * FROM " + LineChartModel.LineChartEntry.TABLE_NAME +
                    " WHERE " + LineChartModel.LineChartEntry.COLUMN_NAME_TITLE + " = ";
}
