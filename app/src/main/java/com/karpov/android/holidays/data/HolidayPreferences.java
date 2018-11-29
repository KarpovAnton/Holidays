package com.karpov.android.holidays.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.karpov.android.holidays.R;

import java.util.ArrayList;
import java.util.List;


public final class HolidayPreferences {

    /**
     * Return list of countries which currently set in Preferences
     *
     * @param context used to access SharedPreferences
     *
     * @return List of selected countries
     */
    public static List<String> getListOfCountries(Context context) {

        List<String> selectedCountries = new ArrayList<String>();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        boolean USisSelected = sp.getBoolean(context.getString(R.string.pref_US_selected_key),
                context.getResources().getBoolean(R.bool.pref_US_selected_by_default));
        if (USisSelected) {
            selectedCountries.add(context.getString(R.string.US_ISO_format));
        }

        boolean RUisSelected = sp.getBoolean(context.getString(R.string.pref_RU_selected_key),
                context.getResources().getBoolean(R.bool.pref_RU_selected_by_default));
        if (RUisSelected) {
            selectedCountries.add(context.getString(R.string.RU_ISO_format));
        }

        boolean CNisSelected = sp.getBoolean(context.getString(R.string.pref_CN_selected_key),
                context.getResources().getBoolean(R.bool.pref_CN_selected_by_default));
        if (CNisSelected) {
            selectedCountries.add(context.getString(R.string.CN_ISO_format));
        }

        boolean DEisSelected = sp.getBoolean(context.getString(R.string.pref_DE_selected_key),
                context.getResources().getBoolean(R.bool.pref_DE_selected_by_default));
        if (DEisSelected) {
            selectedCountries.add(context.getString(R.string.DE_ISO_format));
        }

        boolean ESisSelected = sp.getBoolean(context.getString(R.string.pref_ES_selected_key),
                context.getResources().getBoolean(R.bool.pref_ES_selected_by_default));
        if (ESisSelected) {
            selectedCountries.add(context.getString(R.string.ES_ISO_format));
        }

        boolean FRisSelected = sp.getBoolean(context.getString(R.string.pref_FR_selected_key),
                context.getResources().getBoolean(R.bool.pref_FR_selected_by_default));
        if (FRisSelected) {
            selectedCountries.add(context.getString(R.string.FR_ISO_format));
        }

        boolean JPisSelected = sp.getBoolean(context.getString(R.string.pref_JP_selected_key),
                context.getResources().getBoolean(R.bool.pref_JP_selected_by_default));
        if (JPisSelected) {
            selectedCountries.add(context.getString(R.string.JP_ISO_format));
        }

        return selectedCountries;
    }

    /**
     * Returns true if the user prefers to see notifications, false otherwise.
     *
     * @param context Used to access SharedPreferences
     * @return true if the user prefers to see notifications, false otherwise
     */
    public static boolean areNotificationsEnabled(Context context) {

        String displayNotificationsKey = context.getString(R.string.pref_enable_notifications_key);

        boolean shouldDisplayNotificationsByDefault = context
                .getResources()
                .getBoolean(R.bool.pref_notifications_by_default);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        boolean shouldDisplayNotifications = sp
                .getBoolean(displayNotificationsKey, shouldDisplayNotificationsByDefault);

        return shouldDisplayNotifications;
    }
}
