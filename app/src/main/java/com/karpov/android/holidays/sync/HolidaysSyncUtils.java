package com.karpov.android.holidays.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.karpov.android.holidays.HolidaysActivity;
import com.karpov.android.holidays.data.HolidayContract;

public class HolidaysSyncUtils {

    private static boolean sInitialized;

    /**
     * Perform initialization once per app lifetime. If initialization has already been
     * performed, we have nothing to do in this method. If an immediate sync is required,
     * this method will take care of making sure that sync occurs.
     *
     * @param context Context that will be passed to other methods and used to access the
     *                ContentResolver
     */
    synchronized public static void initialize(@NonNull final Context context) {

        if (sInitialized) return;

        sInitialized = true;

        Thread checkForEmpty = new Thread(new Runnable() {
            @Override
            public void run() {

                /* URI for every row in our holidays table*/
                Uri holidaysQueryUri = HolidayContract.HolidayEntry.CONTENT_URI;


                /* Here, we perform the query to check to see if we have any holidays data */
                Cursor cursor = context.getContentResolver().query(
                        holidaysQueryUri,
                        HolidaysActivity.MAIN_HOLIDAYS_PROJECTION,
                        null,
                        null,
                        null);

                /*
                 * If cursor null OR empty, we need to sync immediately to
                 * be able to display data to the user, else set our static variable
                 * to true for show holidays data.
                 */
                if (null == cursor || cursor.getCount() == 0) {
                    startImmediateSync(context);
                } else {
                    HolidaysSyncTask.sLoadingIsComplete = true;
                }

                cursor.close();
            }
        });

        checkForEmpty.start();
    }

    /**
     * Helper method to perform a sync immediately using an IntentService for asynchronous
     * execution.
     *
     * @param context The Context used to start the IntentService for the sync.
     */
    public static void startImmediateSync(@NonNull final Context context) {
        Intent intentToSyncImmediately = new Intent(context, HolidaysSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }
}
