package com.example.oegod.criminalintent;

import android.util.Log;

import java.util.Comparator;

/**
 * Created by oegod on 02.09.2017.
 */


class CrimeComparator implements Comparator<Crime> {
    static int SORT_TYPE_POSITION = 1;
    static int SORT_TYPE_POLICE = 2;
    static int SORT_TYPE_DATE = 3;
    private int mSortType;

    CrimeComparator(int sortType) {
        mSortType = sortType;
    }

    @Override
    public int compare(Crime o1, Crime o2) {
        if (mSortType == SORT_TYPE_POSITION) {
            return o1.getPosition() - o2.getPosition();
        } else {
            return Boolean.valueOf(o2.isNeedPolice()).compareTo(o1.isNeedPolice());
        }
    }
}
