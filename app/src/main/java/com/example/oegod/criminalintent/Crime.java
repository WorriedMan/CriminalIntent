package com.example.oegod.criminalintent;

import android.content.Context;

import com.oegodf.crime.CrimeBase;

import java.text.DateFormat;
import java.util.UUID;

/**
 * Created by oegod on 03.10.2017.
 */

public class Crime extends CrimeBase {
    public Crime(UUID uuid, boolean b) {
        super(uuid, b);
    }

    public Crime(UUID uuid) {
        super(uuid);
    }

    public Crime(Crime crime) {
        super(crime);
    }

    public Crime(CrimeBase crime) {
        super(crime);
    }

    public String getDateString(Context context) {
//        SimpleDateFormat sdf = new SimpleDateFormat("EEEE d MMMM HH:mm:ss yyyy", new Locale("RU"));
        DateFormat df = android.text.format.DateFormat.getMediumDateFormat(context);
        return df.format(mCalendar.getTime());
    }

    public String getTimeString(Context context) {
//        SimpleDateFormat sdf = new SimpleDateFormat("EEEE d MMMM HH:mm:ss yyyy", new Locale("RU"));
        DateFormat df = android.text.format.DateFormat.getTimeFormat(context);
        return df.format(mCalendar.getTime());
    }
}
