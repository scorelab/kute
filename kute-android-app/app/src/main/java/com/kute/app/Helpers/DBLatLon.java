package com.kute.app.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Dilushi on 7/28/2016.
 */

public class DBLatLon extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "LatLonDB.db";
    public static final String TABLE_NAME = "LatLon";
    public static final String LATITUDES = "Latitudes";
    public static final String LONGITUDES = "Longitudes";
    public static final String TIME = "Time";
    public static boolean isRecord = false;

    public DBLatLon(Context context){
        super(context, DATABASE_NAME, null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " +  TABLE_NAME +
                        "(id integer primary key AUTOINCREMENT, " +
                        LATITUDES + " text, " +
                        LONGITUDES + " text, " +
                        TIME + " text);"
        );

    }
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {/**/}

    public void addRecord(double lat, double lon) {

        if(!isRecord){
            return;
        }

        SQLiteDatabase db = getWritableDatabase();
        ContentValues insertValues = new ContentValues();
        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();

        insertValues.put(LATITUDES, lat);
        insertValues.put(LONGITUDES, lon);
        insertValues.put(TIME, ts);

        db.insert(TABLE_NAME, null, insertValues);
    }

    public ArrayList<String> fetchAllPoints() {

        ArrayList<String> coordinates = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from " + TABLE_NAME, null);
        res.moveToFirst();

        while(!res.isAfterLast()){
            coordinates.add(res.getString(res.getColumnIndex("id")));
            coordinates.add(res.getString(res.getColumnIndex(TIME)));
            coordinates.add(res.getString(res.getColumnIndex(LATITUDES)));
            coordinates.add(res.getString(res.getColumnIndex(LONGITUDES)));
            res.moveToNext();
        }
        res.close();
        db.close();
        return coordinates;
    }
}