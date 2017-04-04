package xyz.filipfloreani.overlapr.db;

import android.content.Context;
import android.database.Cursor;

import java.util.List;
import java.util.Vector;

import xyz.filipfloreani.overlapr.model.LineChartModel;
import xyz.filipfloreani.overlapr.utils.GeneralUtils;

/**
 * Created by filipfloreani on 04/04/2017.
 */

public class LineChartRepository extends Repository {

    public static List<LineChartModel> getAllModels(Context context) {
        List<LineChartModel> modelList = new Vector<>();

        getDatabase(context);

        Cursor c = null;
        try {
            c = db.rawQuery(SQLExpression.SELECT_ALL_GRAPH, null);

            while (c.moveToNext()) {
                modelList.add(parseCursorRow(c));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return modelList;
    }

    public static LineChartModel getModelById(Context context, int id) {
        getDatabase(context);

        LineChartModel model = new LineChartModel();
        Cursor c = null;
        try {
            c = db.rawQuery(SQLExpression.SELECT_GRAPH_WHERE_ID + id, null);
            if (c.moveToNext()) {
                model = parseCursorRow(c);
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return model;
    }

    public static LineChartModel getModelByTitle(Context context, String title) {
        getDatabase(context);

        LineChartModel model = new LineChartModel();
        Cursor c = null;
        try {
            c = db.rawQuery(SQLExpression.SELECT_GRAPH_WHERE_TITLE + title, null);
            if (c.moveToNext()) {
                model = parseCursorRow(c);
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return model;
    }

    private static LineChartModel parseCursorRow(Cursor c) {
        LineChartModel model = new LineChartModel();

        int titleIndex = c.getColumnIndex(LineChartModel.LineChartEntry.COLUMN_NAME_TITLE);
        int creationDateIndex = c.getColumnIndex(LineChartModel.LineChartEntry.COLUMN_NAME_CREATION_DATE);
        int lineDataIndex = c.getColumnIndex(LineChartModel.LineChartEntry.COLUMN_NAME_CHART_DATA);

        if (titleIndex > -1)
            model.setTitle(c.getString(titleIndex));
        if (creationDateIndex > -1)
            model.setCreationDate(GeneralUtils.fromTimestamp(c.getLong(creationDateIndex)));
        if (lineDataIndex > -1)
            model.setLineDataSet(LineChartModel.fromJson(c.getString(lineDataIndex)));

        return model;
    }
}
