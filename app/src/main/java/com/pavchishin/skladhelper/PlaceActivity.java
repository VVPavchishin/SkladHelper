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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import static com.pavchishin.skladhelper.PlaceTask.QDOCK;
import static com.pavchishin.skladhelper.MainActivity.TAG;

public class PlaceActivity extends AppCompatActivity {

    LinearLayout centerLayout;
    LinearLayout rightLayout;

    TextView qDock, qPlace, qScanned, qDifference;
    EditText scanField;
    ImageView status;

    DBHelper helper;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        setNoActionBar();

        helper = new DBHelper(this);

        qDock = findViewById(R.id.title_quant_number);
        qPlace = findViewById(R.id.title_place_number);
        qScanned = findViewById(R.id.title_scan_number);
        qScanned.setText(String.valueOf(0));
        qDifference = findViewById(R.id.title_difference_number);

        scanField = findViewById(R.id.scanner);

        status = findViewById(R.id.im_status);

        centerLayout = findViewById(R.id.center_layout);
        rightLayout = findViewById(R.id.right_layout);

        fillLeftDisplay();
        fillCentralDisplay();

    }

    private void fillCentralDisplay() {
        getDataFromDB();
    }

    private void fillLeftDisplay() {
        int val = getIntent().getIntExtra(QDOCK, 0);
        qDock.setText(String.valueOf(val));
    }

    private void getDataFromDB() {
        database = helper.getWritableDatabase();
        Cursor c = database.rawQuery("SELECT DISTINCT " + DBHelper.PLACE_PLACE_NUMBER
                                        + " FROM " + DBHelper.TABLE_PLACES, null);
        int count = 0;

        c.moveToFirst();
            do {
                String number = c.getString(c.getColumnIndex(DBHelper.PLACE_PLACE_NUMBER));
                Button button = new Button(this);
                button.setText(number);
                centerLayout.addView(button);
                showParts(number, button);
                count++;

            } while(c.moveToNext());
        c.close();
        qPlace.setText(String.valueOf(count));
        qDifference.setText(String.valueOf(count - Integer.parseInt(qScanned.getText().toString())));
    }

    private void showParts(final String number, Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rightLayout.removeAllViews();
                database = helper.getWritableDatabase();
                Cursor c = database.rawQuery("SELECT DISTINCT " + DBHelper.PLACE_ARTIKUL_PART + "," + DBHelper.PLACE_NAME_PART + "," +
                                        DBHelper.PLACE_QUANTITY_PART + " FROM " +  DBHelper.TABLE_PLACES +
                                        " WHERE " + DBHelper.PLACE_PLACE_NUMBER + " = ?", new String[]{number});
                c.moveToFirst();
                do {
                    String art = c.getString(c.getColumnIndex(DBHelper.PLACE_ARTIKUL_PART));
                    String name = c.getString(c.getColumnIndex(DBHelper.PLACE_NAME_PART));
                    String quant = c.getString(c.getColumnIndex(DBHelper.PLACE_QUANTITY_PART));

                    Log.d(TAG, "Aртикул " + art + " Имя " + name + " Количество " + quant);

                    LinearLayout layout = new LinearLayout(PlaceActivity.this);
                    layout.setOrientation(LinearLayout.HORIZONTAL);
                    TextView atrTxt = new TextView(PlaceActivity.this);
                    atrTxt.setTextColor(Color.WHITE);
                    atrTxt.setText(art.trim());
                    atrTxt.setMaxLines(1);
                    atrTxt.setLayoutParams(new LinearLayout.LayoutParams(0,
                            LinearLayout.LayoutParams.WRAP_CONTENT, 3f));
                    TextView nameTxt = new TextView(PlaceActivity.this);
                    nameTxt.setTextColor(Color.WHITE);
                    nameTxt.setMaxLines(1);
                    nameTxt.setLayoutParams(new LinearLayout.LayoutParams(0,
                            LinearLayout.LayoutParams.WRAP_CONTENT, 4.3f));
                    nameTxt.setText(name.trim());
                    TextView quantTxt = new TextView(PlaceActivity.this);
                    quantTxt.setTextColor(Color.WHITE);
                    quantTxt.setText(quant.trim());
                    quantTxt.setLayoutParams(new LinearLayout.LayoutParams(0,
                            LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                    layout.addView(atrTxt);
                    layout.addView(nameTxt);
                    layout.addView(quantTxt);
                    rightLayout.addView(layout);


                } while(c.moveToNext());
                c.close();
            }
        });
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
}
