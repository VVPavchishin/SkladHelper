package com.pavchishin.skladhelper;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.pavchishin.skladhelper.DBHelper.PART_ARTIKUL;
import static com.pavchishin.skladhelper.DBHelper.PART_BARCODE;
import static com.pavchishin.skladhelper.DBHelper.PART_NAME;
import static com.pavchishin.skladhelper.DBHelper.PART_PLACE;
import static com.pavchishin.skladhelper.DBHelper.PART_QUANTITY_DOC;
import static com.pavchishin.skladhelper.DBHelper.PART_QUANTITY_REAL;
import static com.pavchishin.skladhelper.MainActivity.PARTS_FOLDER;
import static com.pavchishin.skladhelper.MainActivity.TAG;

public class CalculateTask extends AsyncTask<Void, String, Void> {
    private String fileName;
    private DBHelper helper;
    private  Context context;

    public CalculateTask(Context context, String fileName) {
        this.fileName = fileName;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Intent intent = new Intent(context, CalculateActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        helper = new DBHelper(context);
        readFile(fileName);
        return null;
    }

    private void readFile(String fileName) {
        File workDirPath = new File(Environment.getExternalStorageDirectory() + File.separator + PARTS_FOLDER);
        File file = new File(workDirPath, fileName);
        SQLiteDatabase database = helper.getWritableDatabase();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Log.d(TAG, line);
                String[] data = line.split("\\|");
                if (data.length > 3) {
                    ContentValues values = new ContentValues();
                    values.put(PART_BARCODE, data[0]);
                    values.put(PART_ARTIKUL, data[1]);
                    values.put(PART_NAME, data[2]);
                    values.put(PART_PLACE,data[3]);
                    values.put(PART_QUANTITY_DOC, Integer.parseInt(data[4]));
                    values.put(PART_QUANTITY_REAL, 0);
                    database.insert(DBHelper.TABLE_PARTS, null, values);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
