package com.pavchishin.skladhelper;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

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

    ArrayList<String> numList;
    int scanCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        setNoActionBar();

        helper = new DBHelper(this);

        qDock = findViewById(R.id.title_quant_number);
        qPlace = findViewById(R.id.title_place_number);
        qScanned = findViewById(R.id.title_scan_number);
        qScanned.setText(String.valueOf(scanCount));
        qDifference = findViewById(R.id.title_difference_number);

        scanField = findViewById(R.id.scanner);

        status = findViewById(R.id.im_status);

        centerLayout = findViewById(R.id.center_layout);
        rightLayout = findViewById(R.id.right_layout);

        numList = new ArrayList<>();

        fillLeftDisplay();
        fillCentralDisplay();
        qPlace.setText(String.valueOf(numList.size()));

        scanField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNoActionBar();
                String barcode = scanField.getText().toString();
                checkBarcode(barcode);
            }
        });

    }

    private void checkBarcode(String barcode) {

        for (String num : numList) {
            if (barcode.contains(num)){
                status.setBackgroundResource(R.drawable.ok_im);
                scanCount++;
                qScanned.setText(String.valueOf(scanCount));
                qDifference.setText(String.valueOf(Integer.parseInt(qPlace.getText().toString()) - scanCount));
                scanField.setText("");
                numList.remove(num);
                showParts(num);
                centerLayout.removeAllViews();
                removeFromDB(num);
                fillCentralDisplay();
                break;
            } else {
                status.setBackgroundResource(R.drawable.not_ok_im);
                scanField.setText("");
            }
        }

    }

    private void removeFromDB(String num) {
        database = helper.getWritableDatabase();
        String whereClause = DBHelper.PLACE_PLACE_NUMBER + " =?";
        database.delete(DBHelper.TABLE_PLACES, whereClause, new String[]{num});
        Log.d(TAG, "Number " + num + " delete");
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

        c.moveToFirst();
            do {
                final String number = c.getString(c.getColumnIndex(DBHelper.PLACE_PLACE_NUMBER));
                final Button button = new Button(this);
                button.setText(number);
                centerLayout.addView(button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showParts(number);
                    }
                });

                numList.add(number);

            } while(c.moveToNext());
        c.close();
    }

    private void showParts(final String number) {
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
