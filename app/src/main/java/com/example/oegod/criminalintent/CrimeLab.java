package com.example.oegod.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.example.oegod.criminalintent.database.CrimeBaseHelper;
import com.example.oegod.criminalintent.database.CrimeDbSchema;
import com.example.oegod.criminalintent.database.CursorCrimeWrapper;
import com.example.oegod.criminalintent.socket.ConnectionWorker;
import com.oegodf.crime.CrimeBase;
import com.oegodf.crime.CrimesMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by oegod on 21.08.2017.
 */

public class CrimeLab {
    private static volatile CrimeLab sInstance;
    private static ConnectionWorker sSocketWorker;
    private CrimesMap<Crime> mCrimes;
    private SQLiteDatabase mDatabase;

    enum Connection {
        SOCKET,
        SQLITE,
        HTTP;
    }

    static Connection sConnection = Connection.SOCKET;

    static CrimeLab get(Context context) {
        CrimeLab localInstance = get();
        localInstance.loadDatabase(context);
        return localInstance;
    }

    static CrimeLab get(ConnectionWorker worker) {
        CrimeLab localInstance = get();
        if (sConnection == Connection.SOCKET) {
            sSocketWorker = worker;
        } else {
            sSocketWorker = null;
        }
        return localInstance;
    }

    static CrimeLab get() {
        CrimeLab localInstance = sInstance;
        if (localInstance == null) {
            synchronized (CrimeLab.class) {
                localInstance = sInstance;
                if (localInstance == null) {
                    sInstance = localInstance = new CrimeLab();
                }
            }
        }
        return localInstance;
    }

    private void loadDatabase(Context context) {
        if (sConnection == Connection.SQLITE) {
            if (mDatabase == null) {
                mDatabase = new CrimeBaseHelper(context)
                        .getWritableDatabase();
                mCrimes = loadCrimes();
                recountCrimes();
            }
        } else {
            if (mDatabase != null) {
                mDatabase.close();
                mDatabase = null;
            }
        }
    }

    private CrimeLab() {
        mCrimes = new CrimesMap<>();
    }

    private CrimesMap<Crime> loadCrimes() {
        CrimesMap<Crime> crimes = new CrimesMap<>();
        if (sConnection == Connection.SQLITE) {
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
        switch (sConnection) {
            case SQLITE:
                ContentValues values = getContentValues(c);
                mDatabase.insert(CrimeDbSchema.CrimeTable.NAME, null, values);
                break;
            case SOCKET:
                sSocketWorker.sendCommand("ADD", c);
                break;
        }
    }

    public void deleteCrime(Crime c) {
        if (sConnection == Connection.SQLITE) {
            mDatabase.delete(CrimeDbSchema.CrimeTable.NAME, CrimeDbSchema.CrimeTable.Cols.UUID + " = ?", new String[]{c.getId().toString()});
        } else {

        }
        mCrimes.remove(c.getId());
    }

    void setCrimes(CrimesMap<Crime> crimes) {
        mCrimes = crimes;
    }

    CrimesMap<Crime> getCrimes() {
        return getCrimes(false);
    }

    CrimesMap<Crime> getCrimes(boolean newCrime) {
        if (newCrime) {
            Crime crime = new Crime(UUID.randomUUID(), true);
            crime.setPosition(mCrimes.size());
            mCrimes.put(crime.getId(), crime);
        }
        return mCrimes;
    }

    void updateCrime(Crime crime) {
        if (sConnection == Connection.SQLITE) {
            String uuidString = crime.getId().toString();
            ContentValues values = getContentValues(crime);
            mDatabase.update(CrimeDbSchema.CrimeTable.NAME, values, CrimeDbSchema.CrimeTable.Cols.UUID + " = ?", new String[]{uuidString});
        } else if (sConnection == Connection.SOCKET) {
            sSocketWorker.sendCommand("UPDATS",crime);
        }
    }

    Crime getCrime(UUID id) {
        return (Crime) mCrimes.get(id);
    }

    public void invalidateCrimes() {
        if (mCrimes != null) {
            for (Map.Entry<UUID, CrimeBase> e : mCrimes.entrySet()) {
                Crime crime = (Crime) e.getValue();
                if (Objects.equals(crime.getTitle(), "")) {
                    deleteCrime(crime);
                } else {
                    crime.setNewCrime(false);
                }
            }
            recountCrimes();
        }
    }

    public void recountCrimes() {
        ArrayList<Crime> tempList = mCrimes.getCrimesList(Crime.class);
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
