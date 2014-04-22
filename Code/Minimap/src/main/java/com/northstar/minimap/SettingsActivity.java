package com.northstar.minimap;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;

public class SettingsActivity extends PreferenceActivity {
    public static final String KEY_PREF_BEACON_RANGE = "pref_beacon_range";
    public static final String KEY_PREF_PROXIMITY_ZONE = "pref_proximity_zone";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new SettingsFragment()).commit();
    }

    public static class SettingsFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(KEY_PREF_BEACON_RANGE)) {
                Log.d("BT-PREF", "BEACON RANGE " + sharedPreferences.getString(key, ""));

//                Preference connectionPref = findPreference(key);
//                connectionPref.setSummary(sharedPreferences.getString(key, ""));
            } else if (key.equals(KEY_PREF_PROXIMITY_ZONE)) {
                Log.d("BT-PREF", "PROXIMITY RANGE " + sharedPreferences.getString(key, ""));
            }
        }
    }
}