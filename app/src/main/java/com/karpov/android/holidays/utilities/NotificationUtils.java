package com.karpov.android.holidays.utilities;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

import com.karpov.android.holidays.R;
import com.karpov.android.holidays.data.HolidayContract;

import java.util.Calendar;


public class NotificationUtils {

    /*
     * The columns of data that we use for handle notifications.
     */
    public static final String[] HOLIDAYS_NOTIFICATION_PROJECTION = {
            HolidayContract.HolidayEntry.COLUMN_COUNTRY,
            HolidayContract.HolidayEntry.COLUMN_NAME,
            HolidayContract.HolidayEntry.COLUMN_DATE
    };

    /*
     * Store the indices of the values in the array of Strings above to more quickly be able to
     * access the data from our query. If the order of the Strings above changes, these indices
     * must be adjusted to match the order of the Strings.
     */
    public static final int INDEX_HOLIDAY_COUNTRY = 0;
    public static final int INDEX_HOLIDAY_NAME = 1;
    public static final int INDEX_HOLIDAY_DATE = 2;


    private static final String HOLIDAY_NOTIFICATION_CHANNEL_ID = "holiday_notification_channel";
    private static final int HOLIDAYS_NOTIFICATION_ID = 124;

    /* Used for holiday notifications pending intent */
    private static final int HOLIDAYS_NOTIFICATION_PENDING_INTENT_ID = 156;
    /* Used for broadcast alarm receiver pending intent */
    public static final int ALARM_RECEIVER_PENDING_INTENT_ID = 174;

    /**
     * Schedules a daily repeating notification celebrated holiday today
     * using {@link AlarmManager}  with {@link AlarmReceiver}.
     *
     * @param context Android context
     */
    public static void scheduleNotifications(Context context) {

        Calendar calendar = Calendar.getInstance();
        /* Set check at 09:00 */
        calendar.set(Calendar.HOUR_OF_DAY, 9);

        Intent alarmIntent = new Intent(context, AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                ALARM_RECEIVER_PENDING_INTENT_ID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    /**
     * Broadcast Receiver for sync query to database and notify user about celebrated holiday.
     */
    public static class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Uri dailyCheckQueryUri = HolidayContract.HolidayEntry.CONTENT_URI;

            Cursor cursor = context.getContentResolver().query(
                    dailyCheckQueryUri,
                    HOLIDAYS_NOTIFICATION_PROJECTION,
                    null,
                    null,
                    null);

            String todayString = HolidaysUtils.getTodayFormatString();

            while (cursor.moveToNext()) {

                String date = cursor.getString(INDEX_HOLIDAY_DATE);

                if (todayString.equals(date)) {

                    String name = cursor.getString(INDEX_HOLIDAY_NAME);
                    notifyUserOfHolidayToday(context, name);
                }
            }

            cursor.close();
        }
    }

    /**
     * Constructs and displays a notification for celebrating in a particular country.
     *
     * @param context Context used to query our ContentProvider and use various Utility methods
     * @param holidayName Name of celebrated holiday
     */
    public static void notifyUserOfHolidayToday(Context context, String holidayName) {

        /* Build the URI for today's holiday using name */
        Uri todayHolidayUri = HolidayContract.HolidayEntry.buildHolidayUriWithName(holidayName);

        Cursor todayHolidayCursor = context.getContentResolver().query(
                todayHolidayUri,
                HOLIDAYS_NOTIFICATION_PROJECTION,
                null,
                null,
                null);

        /*
         * If todayHolidayCursor is empty, moveToFirst will return false. If our cursor is not
         * empty, we want to show the notification.
         */
        if (todayHolidayCursor.moveToFirst()) {

            String country = todayHolidayCursor.getString(INDEX_HOLIDAY_COUNTRY);
            String name = todayHolidayCursor.getString(INDEX_HOLIDAY_NAME);

            int flagImageId = HolidaysUtils.getFlagResourceId(context, country);

            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);

            /* Create notification channel for Android Oreo and higher */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(
                        HOLIDAY_NOTIFICATION_CHANNEL_ID,
                        context.getString(R.string.main_notification_channel_name),
                        NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(mChannel);
            }

            /*
             * Use notificationCompat Builder to provide a context and specify a color for the
             * notification, small icon, the title for the notification, and finally
             * the text of the notification, which in our case in a summary of holiday
             */
            NotificationCompat.Builder notificationBuilder = new NotificationCompat
                    .Builder(context, HOLIDAY_NOTIFICATION_CHANNEL_ID)
                    .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                    .setSmallIcon(flagImageId)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(name.concat(" celebrated today!"))
                    .setAutoCancel(true);

            Intent openInWebIntent = new Intent(Intent.ACTION_WEB_SEARCH);
            openInWebIntent.putExtra(SearchManager.QUERY, holidayName);

            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
            taskStackBuilder.addNextIntentWithParentStack(openInWebIntent);
            PendingIntent resultPendingIntent = taskStackBuilder.getPendingIntent(
                    HOLIDAYS_NOTIFICATION_PENDING_INTENT_ID,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            notificationBuilder.setContentIntent(resultPendingIntent);

            notificationManager.notify(HOLIDAYS_NOTIFICATION_ID, notificationBuilder.build());
        }

        todayHolidayCursor.close();
    }

}
