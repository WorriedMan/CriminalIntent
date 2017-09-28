package com.example.oegod.criminalintent;

import android.content.Context;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by oegod on 21.08.2017.
 */

public class Crime {
    private int mPosition;
    private UUID mId;
    private String mTitle;
    private Calendar mCalendar;
    private boolean mSolved;
    private boolean mNeedPolice;
    private String mSuspect;

    public void setNewCrime(boolean newCrime) {
        mNewCrime = newCrime;
    }

    private boolean mNewCrime;

    public boolean isNeedPolice() {
        return mNeedPolice;
    }

    public void setNeedPolice(boolean needPolice) {
        mNeedPolice = needPolice;
    }

    public Crime(UUID id) {
        this(id, false);
    }

    public Crime(UUID id, boolean newCrime) {
        mId = id;
        mCalendar = Calendar.getInstance();
        mNewCrime = newCrime;
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
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

    public Date getDate() {
        return mCalendar.getTime();
    }

    public void setDate(Date date) {
        mCalendar.setTime(date);
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public boolean isNewCrime() {
        return mNewCrime;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }
}
