package com.example.oegod.criminalintent;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * Created by oegod on 06.09.2017.
 */

public class DateFragmentActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new DatePickerFragment();
    }
}
