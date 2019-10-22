package com.pavchishin.skladhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.pavchishin.skladhelper.MainActivity.TAG;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_PARTS = "spare_parts";

    public static final String TABLE_PARTS = "parts";

    public static final String PART_ID = "_id";
    public static final String PART_BARCODE = "barcode";
    public static final String PART_ARTIKUL = "artikul";
    public static final String PART_NAME = "name";
    public static final String PART_PLACE = "place";
    public static final String PART_QUANTITY_DOC = "quantity_doc";
    public static final String PART_QUANTITY_REAL = "quantity_real";





    public DBHelper(Context context) {
        super(context, DATABASE_PARTS, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "<--- onCreate database --->");
        db.execSQL("create table " + TABLE_PARTS + " ("
                + PART_ID + " integer primary key autoincrement,"
                + PART_BARCODE + " text,"
                + PART_ARTIKUL + " text,"
                + PART_NAME + " text,"
                + PART_PLACE + " text,"
                + PART_QUANTITY_DOC + " integer,"
                + PART_QUANTITY_REAL + " integer" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
