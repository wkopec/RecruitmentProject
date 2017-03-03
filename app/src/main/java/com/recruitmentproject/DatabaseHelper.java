package com.recruitmentproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.DecimalFormat;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Locations.db";
    public static final String TABLE_NAME = "locations_lable";
    Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(id INTEGER PRIMARY KEY, name TEXT, avatar TEXT, latitude REAL, longitude REAL, time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP DATABASE IF EXISTS " + TABLE_NAME);
    }

    public boolean insertData(int id, String name, String avatar, double latitude, double longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", id);
        contentValues.put("name", name);
        contentValues.put("avatar", avatar);
        contentValues.put("latitude", latitude);
        contentValues.put("longitude", longitude);

        if (db.insert(TABLE_NAME, null, contentValues) == -1) {
            updatePosition(id);
            return false;
        }
        else {
            return true;
        }
    }

    public void updatePosition(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_NAME + " SET time = CURRENT_TIMESTAMP WHERE id = " + id);
    }

    public SQLiteCursor getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY time DESC";
        SQLiteCursor cursor = (SQLiteCursor) db.rawQuery(query, null);
        return cursor;
    }

    public SQLiteCursor getLocationData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE id = " + id;
        SQLiteCursor cursor = (SQLiteCursor) db.rawQuery(query, null);
        return cursor;
    }

}
