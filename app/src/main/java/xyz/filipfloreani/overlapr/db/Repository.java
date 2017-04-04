package xyz.filipfloreani.overlapr.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

/**
 * Created by filipfloreani on 03/04/2017.
 */
public abstract class Repository {

    protected static SQLiteDatabase db;

    public static void startTransaction(Context context) {
        getDatabase(context).beginTransactionNonExclusive();
    }

    public static void commitTransaction() {
        if (db != null) {
            db.setTransactionSuccessful();
        }
    }

    public static void endTransaction() {
        if (db != null) {
            db.endTransaction();
        }
    }

    public static boolean isInTransaction() {
        return db.inTransaction();
    }

    public static synchronized SQLiteDatabase getDatabase(Context context) {
        if (db == null || !db.isOpen()) {
            SQLiteHelper helper = new SQLiteHelper(context);
            db = helper.getWritableDatabase();
        }

        return db;
    }

    public static long updateSpecificRecord(String table, ContentValues values, String whereClause, String[] whereArgs) {
        return db.update(table, values, whereClause, whereArgs);
    }

    public static long insertRecord(String table, ContentValues values) {
        long rowId = 0;
        try {
            rowId = db.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        return rowId;
    }

    public static long upsertRecord(String table, ContentValues values) {
        long rowId = values.getAsInteger("Id");
        try {
            db.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            updateSpecificRecord(table, values, "Id = ?", new String[]{String.valueOf(rowId)});
        } catch (SQLiteException e) {
            e.printStackTrace();
            return 0;
        }

        return rowId;
    }

    public static long deleteRecord(String table, String where, String[] args) {
        long rowId = 0;
        try {
            rowId = db.delete(table, where, args);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        return rowId;
    }
}
