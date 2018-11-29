package com.karpov.android.holidays.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.karpov.android.holidays.data.HolidayContract.HolidayEntry;

/**
 * Manages a local database for holiday data.
 */
public class HolidayDbHelper extends SQLiteOpenHelper {

    /* Name of the database. */
    private static final String DATABASE_NAME = "holidays.db";

    /* Database version. */
    private static final int DATABASE_VERSION = 3;

    public HolidayDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time.
     *
     * @param sqLiteDatabase The database.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        /*
         * This String will contain a simple SQL statement that will create a table that will
         * cache our holiday data.
         */
        final String SQL_CREATE_HOLIDAYS_TABLE =
                "CREATE TABLE " + HolidayEntry.TABLE_NAME + " (" +
                        HolidayEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        HolidayEntry.COLUMN_COUNTRY + " TEXT NOT NULL, " +
                        HolidayEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                        HolidayEntry.COLUMN_DATE + " TEXT NOT NULL);";

        /* Execute the SQL statement. */
        sqLiteDatabase.execSQL(SQL_CREATE_HOLIDAYS_TABLE);
    }

    /**
     * This database is only a cache for online data, so its upgrade policy is simply to discard
     * the data and call through to onCreate to recreate the table.
     *
     * @param sqLiteDatabase Database that is being upgraded
     * @param oldVersion     The old database version
     * @param newVersion     The new database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + HolidayEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
