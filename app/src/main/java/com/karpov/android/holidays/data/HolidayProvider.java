package com.karpov.android.holidays.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class HolidayProvider extends ContentProvider {

    /*
     * These constant will be used to match URIs with the data they are looking for.
     */
    public static final int CODE_HOLIDAYS = 100;
    public static final int CODE_HOLIDAY_WITH_NAME = 101;

    /*
     * The URI Matcher used by this content provider.
     */
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private HolidayDbHelper mOpenHelper;

    /**
     * Creates the UriMatcher that will match each URI to the CODE_HOLIDAYS and
     * CODE_HOLIDAY_WITH_NAME constants defined above.
     *
     * @return A UriMatcher that correctly matches the constants.
     */
    public static UriMatcher buildUriMatcher() {

        /*
         * The code passed into the constructor of UriMatcher here represents the code to
         * return for the root URI.
         */
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = HolidayContract.CONTENT_AUTHORITY;

        /* This URI is content://com.karpov.android.holidays/holidays/ */
        matcher.addURI(authority, HolidayContract.PATH_HOLIDAYS, CODE_HOLIDAYS);

        /* This URI is content://com.karpov.android.holidays/holidays/holidaysname */
        matcher.addURI(authority, HolidayContract.PATH_HOLIDAYS + "/*", CODE_HOLIDAY_WITH_NAME);

        return matcher;
    }

    /**
     * Initialize our content provider on startup.
     *
     * @return true if the provider was successfully loaded, false otherwise
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new HolidayDbHelper(getContext());
        return true;
    }

    /**
     * Handles requests to insert a set of new rows.
     *
     * @param uri    The content:// URI of the insertion request.
     * @param values An array of sets of column_name/value pairs to add to the database.
     *
     * @return The number of values that were inserted.
     */
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {

            case CODE_HOLIDAYS:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(HolidayContract.HolidayEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    /**
     * Handles query requests from clients. Use this method to query for all
     * of holidays data and to query for holiday with name.
     *
     * @param uri           The URI to query
     * @param projection    The list of columns to put into the cursor. If null, all columns are
     *                      included.
     * @param selection     A selection criteria to apply when filtering rows. If null, then all
     *                      rows are included.
     * @param selectionArgs Include ?s in selection, which will be replaced by
     *                      the values from selectionArgs, in order that they appear in the
     *                      selection.
     * @param sortOrder     How the rows in the cursor should be sorted.
     * @return A Cursor containing the results of the query.
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Cursor cursor;

        switch (sUriMatcher.match(uri)) {

            /*
             * In this case, return a cursor that contains one row of holidays data.
             */
            case CODE_HOLIDAY_WITH_NAME: {

                String nameHolidayString = uri.getLastPathSegment();

                String[] selectionArguments = new String[]{nameHolidayString};

                cursor = mOpenHelper.getReadableDatabase().query(
                        HolidayContract.HolidayEntry.TABLE_NAME,
                        projection,
                        HolidayContract.HolidayEntry.COLUMN_NAME + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            }

            /*
             * In this case, return a cursor that contains every row of holidays data.
             */
            case CODE_HOLIDAYS: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        HolidayContract.HolidayEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Deletes data at a given URI with optional arguments for more fine tuned deletions.
     *
     * @param uri           The full URI to query
     * @param selection     An optional restriction to apply to rows when deleting.
     * @param selectionArgs Used in conjunction with the selection statement
     * @return The number of rows deleted
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        /* Number of rows deleted to be returned. */
        int numRowsDeleted;

        switch (sUriMatcher.match(uri)) {

            case CODE_HOLIDAYS:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        HolidayContract.HolidayEntry.TABLE_NAME,
                        selection,
                        selectionArgs);

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        /* If we actually deleted any rows, notify that a change has occurred to this URI */
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new RuntimeException("Not implementing update in Holidays");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        throw new RuntimeException("Not implementing insert in Holidays. Use bulkInsert instead");
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("Not implementing getType in Holidays.");
    }
}
