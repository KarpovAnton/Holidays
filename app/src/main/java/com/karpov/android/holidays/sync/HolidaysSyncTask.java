package com.karpov.android.holidays.sync;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.karpov.android.holidays.R;
import com.karpov.android.holidays.data.HolidayContract;
import com.karpov.android.holidays.data.HolidayPreferences;
import com.karpov.android.holidays.utilities.HolidaysJsonUtils;
import com.karpov.android.holidays.utilities.NetworkUtils;
import com.karpov.android.holidays.utilities.NotificationUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class HolidaysSyncTask {

    public static boolean sLoadingIsComplete;

    /**
     * Performs the network request for updated holidays, parses the JSON from that request, and
     * inserts the new holiday information into our ContentProvider. Launch daily check holidays
     * in database on celebrating today if notifications not disabled in the preferences screen.
     *
     * @param context Used to access utility methods and the ContentResolver
     */
    synchronized public static void syncHolidays(Context context) {

        try {

            sLoadingIsComplete = false;

            /* Get a handle on the ContentResolver to delete and insert data */
            ContentResolver holidaysContentResolver = context.getContentResolver();

            /* Try connection to server */
            if (getJsonHolidaysResponse(context.getString(R.string.US_ISO_format)).isEmpty()) {
                Toast.makeText(context, "Problem with network connection", Toast.LENGTH_SHORT).show();
                return;
            }

            /* Delete old holidays data because we don't need to keep multiple data */
            holidaysContentResolver.delete(
                    HolidayContract.HolidayEntry.CONTENT_URI,
                    null,
                    null);

            /*
             * For each selected country in the preferences screen retrieve the JSON,
             * parse the JSON into a list of holidays values and insert our new data
             * into Holidays's ContentProvider
             */
            List<String> selectedCountries = HolidayPreferences.getListOfCountries(context);

            for (String country : selectedCountries) {

                String jsonHolidaysResponse = getJsonHolidaysResponse(country);

                ContentValues[] holidayValues = HolidaysJsonUtils
                        .getHolidaysContentValuesFromJson(jsonHolidaysResponse, country);

                if (holidayValues != null && holidayValues.length != 0) {

                    holidaysContentResolver.bulkInsert(
                            HolidayContract.HolidayEntry.CONTENT_URI,
                            holidayValues);
                }
            }

            /* When all list of countries is loaded set static variable to true*/
            sLoadingIsComplete = true;

            /* Schedule notifications if the user wants them shown */
            if (HolidayPreferences.areNotificationsEnabled(context)) {

                NotificationUtils.scheduleNotifications(context);

            } else {

                /* In other case we should turn off notifications by cancelling AlarmManager */
                Intent alarmIntent = new Intent(context, NotificationUtils.AlarmReceiver.class);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                        NotificationUtils.ALARM_RECEIVER_PENDING_INTENT_ID,
                        alarmIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);
            }

        } catch (Exception e) {
            /* Server probably invalid */
            e.printStackTrace();
        }

    }

    /**
     * Helper method for get simple JSON string.
     *
     * @param country Country for request.
     * @return JSON string witch contains response.
     * @throws IOException
     */
    private static String getJsonHolidaysResponse(String country) throws IOException {

        URL holidaysRequestUrl = NetworkUtils.buildUrl(country);
        return NetworkUtils.getResponseFromHttpUrl(holidaysRequestUrl);
    }
}
