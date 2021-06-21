package com.dji.uavapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Gallery;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DB_Name="APP.db";
    public static final int DB_VERSION=1;
    public static final String UserTAB="user";
    public static final String FlightTAB="flight";
    public static final String GalleryTAB="gallery";
    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DB_Name, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CreateUserTableSQL="create table if not exists "+UserTAB+" ("+
                "userid INTEGER PRIMARY KEY NOT NULL,"+
                "nickname CHARACTER(20),"+
                "flightsumdistance INTEGER,"+
                "flightsumtime INTEGER);";
        String CreateFlightTableSQL="create table if not exists "+FlightTAB+" ("+
                "flightid INTEGER PRIMARY KEY NOT NULL,"+
                "flightstarttime TEXT,"+
                "flightendtime TEXT,"+
                "path TEXT,"+
                "pathmedium CHARACTER(20)"+
                ");";
        String CreateGalleryTableSQL="create table if not exists "+ GalleryTAB+" ("+
                "galleryid INTEGER PRIMARY KEY NOT NULL,"+
                "size INTEGER,"+
                "location CHARACTER(20),"+
                "time TEXT"+
                ");";

        db.execSQL(CreateUserTableSQL);
        db.execSQL(CreateFlightTableSQL);
        db.execSQL(CreateGalleryTableSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
