package com.example.oegod.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.oegod.criminalintent.database.CrimeBaseHelper;
import com.example.oegod.criminalintent.database.CrimeDbSchema;
import com.example.oegod.criminalintent.database.CursorCrimeWrapper;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

/**
 * Created by oegod on 21.08.2017.
 */

public class CrimeLab {
    private static CrimeLab sCrimeLab;

    private CrimesMap mCrimes;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context) {
        mContext = context;
        mDatabase = new CrimeBaseHelper(mContext)
                .getWritableDatabase();
        mCrimes = loadCrimes();
        recountCrimes();
    }

    private CrimesMap loadCrimes() {
        CrimesMap crimes = new CrimesMap();
        CursorCrimeWrapper cursor = queryCrimes(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Crime crime = cursor.getCrime();
                crimes.put(crime.getId(), crime);
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return crimes;
    }

    private CursorCrimeWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CrimeDbSchema.CrimeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new CursorCrimeWrapper(cursor);
    }

    void addCrime(Crime c) {
        ContentValues values = getContentValues(c);
        mDatabase.insert(CrimeDbSchema.CrimeTable.NAME, null, values);
    }

    public void deleteCrime(Crime c) {
        mDatabase.delete(CrimeDbSchema.CrimeTable.NAME, CrimeDbSchema.CrimeTable.Cols.UUID + " = ?", new String[]{c.getId().toString()});
        mCrimes.remove(c.getId());
    }

    CrimesMap getCrimes() {
        return getCrimes(false);
    }

    CrimesMap getCrimes(boolean newCrime) {
        if (newCrime) {
            Crime crime = new Crime(UUID.randomUUID(), true);
            crime.setPosition(mCrimes.size());
            mCrimes.put(crime.getId(), crime);
        }
        return mCrimes;
    }

    public void updateCrime(Crime crime) {
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);

        mDatabase.update(CrimeDbSchema.CrimeTable.NAME, values, CrimeDbSchema.CrimeTable.Cols.UUID + " = ?", new String[]{uuidString});
    }

    Crime getCrime(UUID id) {
        return mCrimes.get(id);
    }

    public void invalidateCrimes() {
        for (Map.Entry<UUID, Crime> e : mCrimes.entrySet()) {
            Crime crime = e.getValue();
            if (Objects.equals(crime.getTitle(), "")) {
                deleteCrime(crime);
            } else {
                crime.setNewCrime(false);
            }
        }
        recountCrimes();
    }

    public void recountCrimes() {
        ArrayList<Crime> tempList = new ArrayList<>(mCrimes.values());
        Collections.sort(tempList, new CrimeComparator(CrimeComparator.SORT_TYPE_POSITION));
        int index = 0;
        for (Crime crime :
                tempList) {
            crime.setPosition(index++);

        }
    }

    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(CrimeDbSchema.CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeDbSchema.CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeDbSchema.CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeDbSchema.CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeDbSchema.CrimeTable.Cols.POLICE, crime.isNeedPolice() ? 1 : 0);
        values.put(CrimeDbSchema.CrimeTable.Cols.SUSPECT, crime.getSuspect());
        return values;
    }
}
