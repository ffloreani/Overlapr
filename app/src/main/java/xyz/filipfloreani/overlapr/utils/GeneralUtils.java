package xyz.filipfloreani.overlapr.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

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
}
