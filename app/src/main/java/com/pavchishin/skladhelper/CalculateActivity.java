package com.pavchishin.skladhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class CalculateActivity extends AppCompatActivity {

    private static final String TAG = "--->>";
    private static final String EMPTY = "";

    TextView txtNumber, txtDate, namePart,
            artikulPart, locationPart, quantityPart,
            quantityDoc, quantityReal, difference;

    EditText scanner;
    CheckBox plusOne, changeLocation;

    DBHelper helper;
    SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNoActionBar();
        setContentView(R.layout.activity_calculate);

        txtNumber = findViewById(R.id.txt_number_doc);
        txtDate = findViewById(R.id.txt_date_doc);

        namePart = findViewById(R.id.txt_part_name);
        artikulPart = findViewById(R.id.txt_artikul);
        locationPart = findViewById(R.id.txt_location);
        quantityPart = findViewById(R.id.txt_quantity);

        quantityDoc = findViewById(R.id.txt_doc_quantity);
        quantityReal = findViewById(R.id.txt_real_quantity);
        difference = findViewById(R.id.txt_difference);

        plusOne = findViewById(R.id.box_plus_one);

        changeLocation = findViewById(R.id.box_location);


        scanner = findViewById(R.id.edt_barcode);
        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String scanText = scanner.getText().toString();
                if(checkDatabase(scanText)){
                    namePart.setTextColor(Color.WHITE);
                    scanner.setText(EMPTY);
                } else {
                    namePart.setTextColor(Color.RED);
                    namePart.setText("Данна запчастина вiдсутня в списку.");
                    scanner.setText(EMPTY);
                    artikulPart.setText(EMPTY);
                    locationPart.setText(EMPTY);
                    quantityDoc.setText(EMPTY);
                    quantityReal.setText(EMPTY);
                    difference.setText(EMPTY);
                }
            }
        });

        helper = new DBHelper(this);
        db = helper.getWritableDatabase();

        Intent intent = getIntent();
        String numberDoc = intent.getStringExtra("NUMBER");
        String dateDoc = intent.getStringExtra("DATA");
        if (numberDoc != null) {
            txtNumber.setText(numberDoc.substring(5));
        }
        txtDate.setText(dateDoc);
    }

    private boolean checkDatabase(String scanText) {
        boolean flag = false;
        String query = "SELECT " + DBHelper.PART_BARCODE + " FROM " + DBHelper.TABLE_PARTS + ";";
        Cursor cursor = db.rawQuery(query, null);
        Log.d(TAG, "<--- " + scanText + " --->");
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(DBHelper.PART_BARCODE));
            if (scanText.contains(name)){
                flag = true;
                Log.d(TAG, "<--- " + name + " --->");
                fillDisplay(name);
                break;
            } else {
                flag = false;
            }
        }
        cursor.close();
        return flag;
    }

    private void fillDisplay(String name) {
        String queryAll = "SELECT * FROM " + DBHelper.TABLE_PARTS +
                " WHERE " + DBHelper.PART_BARCODE + " LIKE " + "'" + name + "%" + "'" + ";";
        Cursor partCursor = db.rawQuery(queryAll, null);
        while (partCursor.moveToNext()) {
            String artikulValue = partCursor.getString(partCursor.getColumnIndex(DBHelper.PART_ARTIKUL));
            artikulPart.setText(artikulValue);
            String nameValue = partCursor.getString(partCursor.getColumnIndex(DBHelper.PART_NAME));
            namePart.setText(nameValue);
            String locationValue = partCursor.getString(partCursor.getColumnIndex(DBHelper.PART_PLACE));
            locationPart.setText(locationValue);
            int quantityDocValue = partCursor.getInt(partCursor.getColumnIndex(DBHelper.PART_QUANTITY_DOC));
            quantityDoc.setText(String.valueOf(quantityDocValue));
            int quantityRealValue = partCursor.getInt(partCursor.getColumnIndex(DBHelper.PART_QUANTITY_REAL));
            quantityReal.setText(String.valueOf(quantityRealValue));
            difference.setText(String.valueOf(quantityDocValue - quantityRealValue));
            Log.d(TAG, "Артикул " + artikulValue + " Имя " + nameValue + " Количество " + quantityDocValue);
            break;
        }
        partCursor.close();
    }

    private void setNoActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
