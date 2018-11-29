package com.karpov.android.holidays;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

/**
 * The SettingsFragment serves as the display for all of the user's settings.
 */
public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static boolean sPreferenceChanged;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        /* Add main preferences, defined in the XML file */
        addPreferencesFromResource(R.xml.pref_main);
    }

    @Override
    public void onStop() {
        super.onStop();
        /* unregister the preference change listener */
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        /* register the preference change listener */
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        sPreferenceChanged = true;
    }
}
