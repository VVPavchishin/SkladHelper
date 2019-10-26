package com.pavchishin.skladhelper;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.RequiresApi;

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

public class CalculateTask extends AsyncTask<Void, Void, String[]> {

    public static final String REDEX = "\\|";

    private String fileName;
    private DBHelper helper;
    private Context context;

    public CalculateTask(Context context, String fileName) {
        this.fileName = fileName;
        this.context = context;
    }

    @Override
    protected void onPostExecute(String[] str) {
        super.onPostExecute(str);
        Log.d(TAG, "Для номера документа и даты " + str[0] + " " + str[1]);
        Intent intent = new Intent(context, CalculateActivity.class);
        intent.putExtra("NUMBER", str[0]);
        intent.putExtra("DATA", str[1]);
        context.startActivity(intent);
    }

    @Override
    protected String[] doInBackground(Void... voids) {
        helper = new DBHelper(context);
        String[] mass = new String[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            mass = readFile(fileName);
        }
        return mass;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String[] readFile(String fileName) {
        File workDirPath = new File(Environment.getExternalStorageDirectory() + File.separator + PARTS_FOLDER);
        File file = new File(workDirPath, fileName);
        SQLiteDatabase database = helper.getWritableDatabase();
        String[] numberDate = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(REDEX);
                if (data.length > 3) {
                    ContentValues values = new ContentValues();
                    values.put(PART_BARCODE, data[0]);
                    values.put(PART_ARTIKUL, data[1]);
                    values.put(PART_NAME, data[2]);
                    values.put(PART_PLACE,data[3]);
                    values.put(PART_QUANTITY_DOC, Integer.parseInt(data[4]));
                    values.put(PART_QUANTITY_REAL, 0);
                    database.insert(DBHelper.TABLE_PARTS, null, values);
                } else {
                    numberDate = new String[]{data[0], data[1]};
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return numberDate;
    }
}
