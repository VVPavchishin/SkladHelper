package com.pavchishin.skladhelper;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class PlaceTask extends AsyncTask<Void, Void, Void> {

    Context context;

    public PlaceTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        context.startActivity(new Intent(context, PlaceActivity.class));
        super.onPostExecute(aVoid);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        return null;
    }
}
