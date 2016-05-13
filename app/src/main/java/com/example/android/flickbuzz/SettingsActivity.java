package com.example.android.flickbuzz;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

/**
 * Created by XCEED on 10-05-2016.
 */
public class SettingsActivity extends PreferenceActivity
        implements OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        addPreferencesFromResource(R.xml.pref_moviefragment);


    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (sharedPreferences instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) sharedPreferences;
            int prefIndex = listPreference.findIndexOfValue(key);
            if (prefIndex >= 0) {
                listPreference.setSummary(listPreference.getEntries()[prefIndex]);
            }

        }

    }
}
