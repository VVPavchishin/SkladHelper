package com.pavchishin.skladhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
        setContentView(R.layout.activity_main);

        btnPlaceCalculate = findViewById(R.id.btn_add_place);
        btnPlaceCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkFile(PLACE_FOLDER)){
                    new PlaceTask(context).execute();
                }
            }
        });

        btnPartsCalculate = findViewById(R.id.btn_add_parts);
        btnPartsCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkFile(PARTS_FOLDER))
                    new CalculateTask(context, fileName).execute();
            }
        });

        btnInvent = findViewById(R.id.btn_invent);
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
        String[] listInFolder = folder.list();
        if (listInFolder.length > 0){
            fileName = listInFolder[0];
            Log.d(TAG, "Имя файла: " + fileName);

            return true;
        } else {
            Toast.makeText(context, "Добавте файл в папку " + folderName, Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}