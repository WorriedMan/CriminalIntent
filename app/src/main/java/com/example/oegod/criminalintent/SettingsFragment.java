package com.example.oegod.criminalintent;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by oegod on 29.09.2017.
 */

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }
}
