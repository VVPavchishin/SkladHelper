package com.pavchishin.skladhelper;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

import static com.pavchishin.skladhelper.MainActivity.PARTS_FOLDER;

public class CalculateActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener {

    private static final String TAG = ">>";
    private static final String EMPTY = "";
    private static final String FILE_INPUT = "INPUT.txt";

    TextView txtNumber, txtDate, namePart,
            artikulPart, locationPart, quantityPart,
            quantityDoc, quantityReal, difference,
            titleDoc, titleReal, titleDifference;

    EditText scanner;
    CheckBox plusOne, changeLocation;

    Button saveFile, exit;
    ImageButton back;

    Context context = this;

    DBHelper helper;
    SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setNoActionBar();


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

        /* Выгрузка в файл */
        saveFile = findViewById(R.id.btn_upload);
        saveFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataFromDB();
            }
        });

        /* Выход */
        exit = findViewById(R.id.btn_exit);
        exit.setVisibility(View.INVISIBLE);

        /*Возврат*/
        back = findViewById(R.id.b_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalculateActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        scanner = findViewById(R.id.edt_barcode);
        scanner.setHintTextColor(Color.GREEN);
        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNoActionBar();
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
                    setEmptyFields();
                }

            }
        });

        helper = new DBHelper(context);
        db = helper.getWritableDatabase();

        Intent intent = getIntent();
        String numberDoc = intent.getStringExtra("NUMBER");
        String dateDoc = intent.getStringExtra("DATA");
        if (numberDoc != null) {
            txtNumber.setTextColor(Color.GREEN);
            txtNumber.setText(numberDoc.substring(5));
        }
        txtDate.setText(dateDoc);
        txtDate.setTextColor(Color.GREEN);
    }

    private void setEmptyFields() {
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
        titleDoc.setVisibility(View.INVISIBLE);
        titleReal.setVisibility(View.INVISIBLE);
        titleDifference.setVisibility(View.INVISIBLE);
    }

    private void getDataFromDB() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.d(TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
            return;
        }
        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + PARTS_FOLDER);
        File sdFile = new File(folder, FILE_INPUT);
        if (sdFile.exists()){
            sdFile.delete();
            Log.d(TAG, sdFile.getName() + " удален!");
        }
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_PARTS, null);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
            bw.write(txtNumber.getText().toString() + "|" + txtDate.getText().toString() + "|" + 1 +"|" + 1 + "|" + "\n");

            while (cursor.moveToNext()) {
                int barcodeIndex = cursor.getColumnIndex(DBHelper.PART_BARCODE);
                int artikulIndex = cursor.getColumnIndex(DBHelper.PART_ARTIKUL);
                int nameIndex = cursor.getColumnIndex(DBHelper.PART_NAME);
                int placeIndex = cursor.getColumnIndex(DBHelper.PART_PLACE);
                int docIndex = cursor.getColumnIndex(DBHelper.PART_QUANTITY_DOC);
                int realIndex = cursor.getColumnIndex(DBHelper.PART_QUANTITY_REAL);

                    bw.write(cursor.getString(barcodeIndex) + "|"
                            + cursor.getString(artikulIndex) + "|"
                            + cursor.getString(nameIndex) + "|"
                            + cursor.getString(placeIndex) + "|"
                            + cursor.getInt(docIndex) + "|"
                            + cursor.getInt(realIndex) + "|" + "\n");
                    Log.d(TAG, cursor.getString(barcodeIndex) + "|"
                            + cursor.getString(artikulIndex) + "|"
                            + cursor.getString(nameIndex) + "|"
                            + cursor.getString(placeIndex) + "|"
                            + cursor.getInt(docIndex) + "|"
                            + cursor.getInt(realIndex) + "|");
            }

            bw.close();
            Log.d(TAG, "Файл записан на SD: " + sdFile.getAbsolutePath());
            saveFile.setTextColor(Color.GREEN);
            saveFile.setText("Данні вигружено в файл");
            saveFile.setEnabled(false);
            setEmptyFields();
            namePart.setText(EMPTY);
            scanner.setInputType(InputType.TYPE_CLASS_TEXT);
            scanner.setTextSize(18);
            scanner.setText("Сканування завершено!");
            exit.setVisibility(View.VISIBLE);
            new DBHelper(context).onDelete(context, DBHelper.TABLE_PARTS);
            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setNoActionBar();
                    Intent intent = new Intent(CalculateActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        cursor.close();
    }

    private void setLocation(final String pName) {
        changeLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setNoActionBar();
                if (isChecked){
                    locationPart.setBackgroundColor(Color.DKGRAY);
                    locationPart.setEnabled(true);
                    locationPart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setNoActionBar();
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
            }
            @Override
            public void afterTextChanged(Editable s) {}
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
                setNoActionBar();
                locationPart.setText(locationName.getText().toString());
                updateLocationDb(pName, locationName.getText().toString());
                setNoActionBar();
                changeLocation.setChecked(false);
                locationDialog.dismiss();
            }
        });


        locationDialog.show();
    }

    private void updateLocationDb(String artikul, String location) {
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.PART_PLACE, location);
        db.update(DBHelper.TABLE_PARTS, cv, DBHelper.PART_ARTIKUL + " = ?",
                new String[] { artikul });
    }

    private void setQuantity(final String pName) {
        plusOne.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setNoActionBar();
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
            quantityPart.setText(String.valueOf(quantityRealValue + 1));
            updateQuantity(artikul, addOne);
            return quantityRealValue + 1;
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

    public void setNoActionBar() {
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
