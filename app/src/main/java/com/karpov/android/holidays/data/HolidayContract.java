package com.karpov.android.holidays.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the holidays database.
 */
public class HolidayContract {

    public static final String CONTENT_AUTHORITY = "com.karpov.android.holidays";

    /* Use CONTENT_AUTHORITY to create the base of all URI's. */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /* Possible path. */
    public static final String PATH_HOLIDAYS = "holidays";

    /*
     * Inner class that defines the table contents of the holidays table.
     * Each entry in the table represents a single holiday.
     */
    public static final class HolidayEntry implements BaseColumns {

        /* The base CONTENT_URI used to access the Holiday table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_HOLIDAYS)
                .build();

        /* Name of database table for holidays */
        public static final String TABLE_NAME = "holidays";

        /* Country in which holiday */
        public static final String COLUMN_COUNTRY = "country";

        /* Name of the holiday */
        public static final String COLUMN_NAME = "name";

        /* Date of the holiday */
        public static final String COLUMN_DATE = "date";


        /**
         * Builds a URI that adds the holiday name to the end of the content URI path.
         *
         * @param name Holiday`s name
         * @return Uri to query details about a single holiday entry
         */
        public static Uri buildHolidayUriWithName(String name) {
            return CONTENT_URI.buildUpon()
                    .appendPath(name)
                    .build();
        }
    }
}
