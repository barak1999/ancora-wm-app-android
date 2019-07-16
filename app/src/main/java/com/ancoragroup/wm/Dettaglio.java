package com.ancoragroup.wm;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.HashMap;

public class Dettaglio extends OmarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        bottoneIndietro.setText("OK");
        bottoneAvanti.setVisibility(View.INVISIBLE);


        listaValori = (ArrayList<HashMap<String, String>>) myIntent.getSerializableExtra("listaValori");

        grigliaViste.setNumColumns(2);

        OmarAdapter omarAdapter = new OmarAdapter(Dettaglio.this, listaValori, R.layout.omar_riga_visualizzazione, da, a);//Create object and set the parameters for simpleAdapter
        grigliaViste.setAdapter(omarAdapter);//sets the adapter for listView

    }

    private long lastClickTime = 0;
    // se vado avanti imposto tutti valori scelt
    @Override
    public void bottoneAvantiClick (View v) {
        // Evito il doppio TAP
        if (Global.BUTTON_WAIT_FEATURE) {
            if (SystemClock.elapsedRealtime() - lastClickTime < Global.BUTTON_CLICK_MS_WAIT) {
                return;
            }

            Log.d("CLIC", "Click " + lastClickTime);

            lastClickTime = SystemClock.elapsedRealtime();
        }
        customBackPressed();
    }
}
