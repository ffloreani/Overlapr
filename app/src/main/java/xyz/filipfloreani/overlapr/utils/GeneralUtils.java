package xyz.filipfloreani.overlapr.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.github.mikephil.charting.data.Entry;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import io.realm.RealmList;
import xyz.filipfloreani.overlapr.model.RealmChartModel;
import xyz.filipfloreani.overlapr.model.RealmPointModel;

/**
 * Created by filipfloreani on 30/03/2017.
 */

public final class GeneralUtils {

    public static AlertDialog.Builder buildWatchOutDialog(Context context) {
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(context);
        adBuilder.setTitle("Watch out")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                });

        return adBuilder;
    }

    public static Date fromTimestamp(long seconds) {
        if (seconds == -1) {
            return null;
        }
        return new Date(seconds * 1000);
    }

    public static long toTimestamp(Date date) {
        if (date == null) {
            return 0;
        }
        return date.getTime() / 1000;
    }

    public static Date getUTCNow() {
        Calendar calendar = Calendar.getInstance();
        int localOffset = TimeZone.getDefault().getOffset(
                calendar.getTimeInMillis());
        calendar.add(Calendar.MILLISECOND, -localOffset);
        return calendar.getTime();
    }

    public static long getUTCNowAsTimestamp() {
        return toTimestamp(getUTCNow());
    }

    public static RealmList<RealmPointModel> entriesToChartPoints(List<Entry> dataSet, RealmChartModel chart) {
        RealmList<RealmPointModel> realmPoints = new RealmList<>();
        for (Entry entry : dataSet) {
            RealmPointModel realmPoint =
                    new RealmPointModel(floatToShort(entry.getX()), floatToShort(entry.getY()), chart);
            realmPoints.add(realmPoint);
        }

        return realmPoints;
    }

    private static short floatToShort(float num) {
        if (num < Short.MIN_VALUE) {
            return Short.MIN_VALUE;
        }
        if (num > Short.MAX_VALUE) {
            return Short.MAX_VALUE;
        }
        return (short) Math.round(num);
    }
}
