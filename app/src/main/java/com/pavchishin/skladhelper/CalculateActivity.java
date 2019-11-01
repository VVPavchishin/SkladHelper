package com.pavchishin.skladhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

public class CalculateActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener {

    private static final String TAG = "--->>";
    private static final String EMPTY = "";

    TextView txtNumber, txtDate, namePart,
            artikulPart, locationPart, quantityPart,
            quantityDoc, quantityReal, difference,
            titleDoc, titleReal, titleDifference;

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

        /* Количественные поля для ввода */
        quantityDoc = findViewById(R.id.txt_doc_quantity);
        quantityReal = findViewById(R.id.txt_real_quantity);
        difference = findViewById(R.id.txt_difference);

        /* Количественные названия полей для ввода */
        titleDoc = findViewById(R.id.doc_quantity);
        titleDoc.setVisibility(View.INVISIBLE);
        titleReal = findViewById(R.id.real_quantity);
        titleReal.setVisibility(View.INVISIBLE);
        titleDifference = findViewById(R.id.plus_mines);
        titleDifference.setVisibility(View.INVISIBLE);

        plusOne = findViewById(R.id.box_plus_one);
        plusOne.setVisibility(View.INVISIBLE);
        plusOne.setChecked(true);
        changeLocation = findViewById(R.id.box_location);
        changeLocation.setVisibility(View.INVISIBLE);


        scanner = findViewById(R.id.edt_barcode);
        scanner.setHintTextColor(Color.WHITE);
        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String scanText = scanner.getText().toString();
                if(checkDatabase(scanText)){
                    plusOne.setVisibility(View.VISIBLE);
                    changeLocation.setVisibility(View.VISIBLE);
                    titleDoc.setVisibility(View.VISIBLE);
                    titleReal.setVisibility(View.VISIBLE);
                    titleDifference.setVisibility(View.VISIBLE);
                    namePart.setTextColor(Color.WHITE);
                    scanner.setText(EMPTY);
                    scanner.setHint(EMPTY);
                    String pName = artikulPart.getText().toString();
                    setQuantity(pName);
                    setLocation(pName);
                } else {
                    namePart.setTextColor(Color.RED);
                    namePart.setText("Данна запчастина вiдсутня в списку.");
                    scanner.setText(EMPTY);
                    scanner.setHint(EMPTY);
                    artikulPart.setText(EMPTY);
                    locationPart.setText(EMPTY);
                    quantityDoc.setText(EMPTY);
                    quantityReal.setText(EMPTY);
                    difference.setText(EMPTY);
                    quantityPart.setText(EMPTY);
                    plusOne.setVisibility(View.INVISIBLE);
                    changeLocation.setVisibility(View.INVISIBLE);
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

    private void setLocation(final String pName) {
        changeLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    locationPart.setBackgroundColor(Color.DKGRAY);
                    locationPart.setEnabled(true);
                    locationPart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showLocationDialog(pName);
                        }
                    });
                } else {
                    locationPart.setBackgroundColor(Color.BLACK);
                    locationPart.setEnabled(false);
                }
            }
        });
    }

    private void showLocationDialog(final String pName) {
        final Dialog locationDialog = new Dialog(CalculateActivity.this);
        locationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        locationDialog.setContentView(R.layout.dialog_location);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(locationDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));
        }

        final EditText locationName = locationDialog.findViewById(R.id.edt_location);
        locationName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Log.d(TAG, s + " " + start + " " + before + " " + count);
               String location = "";
               location = location + s;
               if (location.length() == 3) {
                   location = location + "-";
                   locationName.setText(location);
                   locationName.setSelection(4);
               }
                if (location.length() == 6) {
                    location = location + "-";
                    locationName.setText(location);
                    locationName.setSelection(7);
                }


                Log.d(TAG, location);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Log.d(TAG, String.valueOf(s));
                locationPart.setText(String.valueOf(s));
            }
        });

        Button cancel = locationDialog.findViewById(R.id.btn_cancel_loc);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNoActionBar();
                changeLocation.setChecked(false);
                locationDialog.dismiss();
            }
        });
        Button save = locationDialog.findViewById(R.id.btn_set_location);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLocationDb(pName);
                setNoActionBar();
                changeLocation.setChecked(false);
                locationDialog.dismiss();
            }
        });


        locationDialog.show();
    }

    private void updateLocationDb(String pName) {

    }

    private void setQuantity(final String pName) {
        plusOne.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    quantityPart.setBackgroundColor(Color.DKGRAY);
                    quantityPart.setEnabled(true);
                    quantityPart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showQuantityDialog(pName);
                        }
                    });
                } else {
                    quantityPart.setBackgroundColor(Color.BLACK);
                    quantityPart.setEnabled(false);
                }
            }
        });
    }
    private boolean checkDatabase(String scanText) {
        boolean flag;
        String subScan = scanText.replace(" ", "");
        String scanValue;
        int scanLength = subScan.length();
        if (scanLength == 13){
            scanValue = subScan.substring(0, 12);
        } else if (scanLength == 11){
            scanValue = subScan.substring(0, 10);
        } else {
            scanValue = subScan;
        }
        Log.d(TAG, "Scan length " + scanLength + " barcode " + scanValue );

        String query = "SELECT * FROM " + DBHelper.TABLE_PARTS
                + " WHERE " + DBHelper.PART_BARCODE + " LIKE " + "'" + scanValue + "'" + ";";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()){
            String artikulValue = cursor.getString(cursor.getColumnIndex(DBHelper.PART_ARTIKUL));
            artikulPart.setText(artikulValue);
            String nameValue = cursor.getString(cursor.getColumnIndex(DBHelper.PART_NAME));
            namePart.setText(nameValue);
            String locationValue = cursor.getString(cursor.getColumnIndex(DBHelper.PART_PLACE));
            locationPart.setText(locationValue);
            int quantityDocValue = cursor.getInt(cursor.getColumnIndex(DBHelper.PART_QUANTITY_DOC));
            quantityDoc.setText(String.valueOf(quantityDocValue));
            int quantityRealValue = cursor.getInt(cursor.getColumnIndex(DBHelper.PART_QUANTITY_REAL));
            int qnt = checkAndSet(artikulValue, quantityRealValue);
            Log.d(TAG, qnt + "");
            quantityReal.setText(String.valueOf(qnt));
            difference.setText(String.valueOf(quantityDocValue - qnt));
            Log.d(TAG, "Артикул " + artikulValue + " Имя " + nameValue + " Количество " + quantityDocValue);
            flag = true;
        } else {
            Log.d(TAG, "Barcode not found " + scanValue);
            flag = false;
        }
        cursor.close();
        return flag;
    }

    private int checkAndSet(String artikul, int quantityRealValue) {
            int addOne = 1;
            quantityPart.setText(String.valueOf(quantityRealValue));
            updateQuantity(artikul, addOne);
            return quantityRealValue;
    }

    private void showQuantityDialog(final String pName) {
        final Dialog dialog = new Dialog(CalculateActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));
        }
        Button add = dialog.findViewById(R.id.btn_add);
        Button cancel = dialog.findViewById(R.id.btn_cancel);
        final NumberPicker np = dialog.findViewById(R.id.numberPicker);
        np.setMaxValue(100);
        np.setMinValue(0);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);
        add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                int docVal = Integer.parseInt(String.valueOf(quantityDoc.getText()));
                int oldValue = Integer.parseInt(String.valueOf(quantityPart.getText()));
                int addQuantity = np.getValue();
                quantityPart.setText(String.valueOf(oldValue + addQuantity));
                updateQuantity(pName, addQuantity);
                quantityPart.setBackgroundColor(Color.parseColor("#161516"));
                quantityReal.setText(String.valueOf(oldValue + addQuantity));
                difference.setText(String.valueOf(docVal - (oldValue + addQuantity)));
                plusOne.setChecked(true);
                setNoActionBar();
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plusOne.setChecked(true);
                setNoActionBar();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void updateQuantity(String artikul , int newValue) {

        db.execSQL("UPDATE " + DBHelper.TABLE_PARTS + " SET " +
                DBHelper.PART_QUANTITY_REAL + " = " + DBHelper.PART_QUANTITY_REAL + "+" + newValue + " WHERE "
                + DBHelper.PART_ARTIKUL + "=?", new String[]{artikul});
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

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {}
}
