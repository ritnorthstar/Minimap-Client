package com.northstar.minimap;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;

public class SettingsActivity extends PreferenceActivity {
    public static final String KEY_PREF_BEACON_RANGE = "pref_beacon_range";
    public static final String KEY_PREF_PROXIMITY_ZONE = "pref_proximity_zone";
    public static final String KEY_PREF_PROXIMITY_ZONE_RANGE = "pref_proximity_zone_range";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new SettingsFragment()).commit();
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}