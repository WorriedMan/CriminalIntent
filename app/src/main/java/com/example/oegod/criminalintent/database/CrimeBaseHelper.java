package com.example.oegod.criminalintent.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.oegod.criminalintent.database.CrimeDbSchema.*;
import static com.example.oegod.criminalintent.database.CrimeDbSchema.CrimeTable.*;

/**
 * Created by oegod on 11.09.2017.
 */

public class CrimeBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "CrimeBase.db";

    public CrimeBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format("CREATE TABLE %s (_id INTEGER PRIMARY KEY AUTOINCREMENT, %s, %s, %s, %s, %s, %s);", NAME, Cols.UUID, Cols.TITLE, Cols.DATE, Cols.SOLVED, Cols.POLICE, Cols.SUSPECT));

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
