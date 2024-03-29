package com.pavchishin.skladhelper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    public static final String PLACE_FOLDER = "Place Calculate";
    public static final String PARTS_FOLDER = "Parts Calculate";
    public static final String INVENT_FOLDER = "Invent";

    public static final String TAG = "-->>";

    public Context context = this;

    String fileName;

    Button btnPlaceCalculate;
    Button btnPartsCalculate;
    Button btnInvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNoActionBar();
        setContentView(R.layout.activity_main);

        btnPlaceCalculate = findViewById(R.id.btn_add_place);
        btnPlaceCalculate.setTextColor(Color.GREEN);
        btnPlaceCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkFile(PLACE_FOLDER) && !(new DBHelper(context)
                        .doesTableExist(new DBHelper(context).getWritableDatabase(), DBHelper.TABLE_PLACES))){
                    new PlaceTask(context).execute();
                } else {
                    startActivity(new Intent(MainActivity.this, PlaceActivity.class));
                }
            }
        });

        btnPartsCalculate = findViewById(R.id.btn_add_parts);
        btnPartsCalculate.setTextColor(Color.GREEN);
        btnPartsCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkFile(PARTS_FOLDER))
                    new CalculateTask(context, fileName).execute();
            }
        });

        btnInvent = findViewById(R.id.btn_invent);
        btnInvent.setTextColor(Color.GREEN);
        btnInvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkFile(INVENT_FOLDER)){
                    new InventTask(context).execute();
                }
            }
        });
    }

    public boolean checkFile(String folderName){
        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + folderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String[] fileInFolder = folder.list();
        assert fileInFolder != null;
        if (fileInFolder.length > 0){
            fileName = fileInFolder[0];
            return true;
        } else {
            Toast.makeText(context, "Добавте файл в папку " + folderName, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void setNoActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
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
