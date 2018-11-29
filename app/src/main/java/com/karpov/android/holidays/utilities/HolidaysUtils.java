package com.karpov.android.holidays.utilities;

import android.content.Context;
import android.util.Log;

import com.karpov.android.holidays.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class HolidaysUtils {

    private static final String TAG = HolidaysUtils.class.getSimpleName();

    /**
     * Helper method to provide the icon resource id
     *
     * @param context Context to use for resource localization
     * @param country Country for get flag
     *
     * @return resource id for the corresponding icon. -1 if no relation is found.
     */
    public static int getFlagResourceId(Context context, String country) {

        if (country.equals(context.getString(R.string.US_ISO_format))) {
            return R.drawable.usa;
        } else if (country.equals(context.getString(R.string.RU_ISO_format))) {
            return R.drawable.russian_federation;
        } else if (country.equals(context.getString(R.string.CN_ISO_format))) {
            return R.drawable.china;
        } else if (country.equals(context.getString(R.string.DE_ISO_format))) {
            return R.drawable.germany;
        } else if (country.equals(context.getString(R.string.ES_ISO_format))) {
            return R.drawable.spain;
        } else if (country.equals(context.getString(R.string.FR_ISO_format))) {
            return R.drawable.france;
        } else if (country.equals(context.getString(R.string.JP_ISO_format))) {
            return R.drawable.japan;
        }

        Log.e(TAG, "Unknown Country: " + country);
        return -1;
    }

    /**
     * This method returns today's date represented in format witch can compare to database dates.
     * Use for daily check notifications.
     *
     * @return The string today`s date
     */
    public static String getTodayFormatString() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("2017-MM-dd");

        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();

        return simpleDateFormat.format(now);
    }

    /**
     * Helper method to convert the database representation of the date into format to display
     * to users.
     *
     * @param defaultDateString The date represented in database format
     *
     * @return A user-friendly representation of the date such as "May 9"
     */
    public static String getFriendlyDateString(String defaultDateString) {

        SimpleDateFormat defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {

            Date date = defaultDateFormat.parse(defaultDateString);
            SimpleDateFormat friendlyDateFormat = new SimpleDateFormat("LLL dd");
            return friendlyDateFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return defaultDateString;
    }
}
