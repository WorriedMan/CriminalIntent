package com.example.oegod.criminalintent.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.oegod.criminalintent.Crime;

import java.util.Date;
import java.util.UUID;

import static com.example.oegod.criminalintent.database.CrimeDbSchema.*;

/**
 * Created by oegod on 12.09.2017.
 */

public class CursorCrimeWrapper extends CursorWrapper {
    public CursorCrimeWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        int position = getInt(getColumnIndex(CrimeTable.Cols.ID));
        String uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        Long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        int needPolice = getInt(getColumnIndex(CrimeTable.Cols.POLICE));
        String suspect = getString(getColumnIndex(CrimeTable.Cols.SUSPECT));

        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved != 0);
        crime.setNeedPolice(needPolice != 0);
        crime.setPosition(position);
        crime.setSuspect(suspect);
        return crime;
    }
}
