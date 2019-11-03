package com.pavchishin.skladhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.pavchishin.skladhelper.MainActivity.TAG;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_PARTS = "spare_parts";

    static final String TABLE_PARTS = "parts";

    private static final String PART_ID = "_id";
    static final String PART_BARCODE = "barcode";
    static final String PART_ARTIKUL = "artikul";
    static final String PART_NAME = "name";
    static final String PART_PLACE = "place";
    static final String PART_QUANTITY_DOC = "quantity_doc";
    static final String PART_QUANTITY_REAL = "quantity_real";

    static final String TABLE_PLACES = "places";

    static final String PLACE_DOCNAME = "name_dok";
    static final String PLACE_ARTIKUL_PART = "artikul_part";
    static final String PLACE_NAME_PART = "name_part";
    static final String PLACE_QUANTITY_PART = "quantity_part";
    static final String PLACE_PRICE_PART = "price_part";
    static final String PLACE_PLACE_NUMBER = "place_number";

    public DBHelper(Context context) {
        super(context, DATABASE_PARTS, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "<--- Create Parts database --->");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARTS);
        db.execSQL("create table " + TABLE_PARTS + " ("
                + PART_ID + " integer primary key,"
                + PART_BARCODE + " text,"
                + PART_ARTIKUL + " text,"
                + PART_NAME + " text,"
                + PART_PLACE + " text,"
                + PART_QUANTITY_DOC + " integer,"
                + PART_QUANTITY_REAL + " integer" + ");");

        Log.d(TAG, "<--- Create Place database --->");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACES);
        db.execSQL("create table " + TABLE_PLACES + " ("
                + PLACE_DOCNAME + " TEXT,"
                + PLACE_ARTIKUL_PART + " TEXT,"
                + PLACE_NAME_PART + " TEXT,"
                + PLACE_QUANTITY_PART + " INTEGER,"
                + PLACE_PRICE_PART + " REAL,"
                + PLACE_PLACE_NUMBER + " TEXT" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public void onDelete(SQLiteDatabase db, String tableName){
        Log.d(TAG, "<--- Delete database --->");
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
    }
}
