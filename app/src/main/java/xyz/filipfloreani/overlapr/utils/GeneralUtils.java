package xyz.filipfloreani.overlapr.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by filipfloreani on 30/03/2017.
 */

public class GeneralUtils {

    public static AlertDialog.Builder buildWatchOutDialog(Context context) {
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(context);
        adBuilder.setTitle("Watch out")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                });

        return adBuilder;
    }
}
