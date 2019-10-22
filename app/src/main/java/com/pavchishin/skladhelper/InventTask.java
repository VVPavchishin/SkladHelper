package com.pavchishin.skladhelper;

import android.content.Context;
import android.os.AsyncTask;

public class InventTask extends AsyncTask<Void, Void, Void> {

    private Context context;


    public InventTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        return null;
    }
}
