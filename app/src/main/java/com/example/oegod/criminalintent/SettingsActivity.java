package com.example.oegod.criminalintent;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by oegod on 29.09.2017.
 */

public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
