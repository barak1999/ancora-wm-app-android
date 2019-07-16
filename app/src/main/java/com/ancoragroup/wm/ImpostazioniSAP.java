package com.ancoragroup.wm;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

public class ImpostazioniSAP extends OmarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        bottoneIndietro.setText("ANNULLA");
        bottoneAvanti.setText("SALVA");

        titolo = "ImpostazioniSAP";

        // permette di tenere visibile il campo in edit
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        OmarAdapter omarAdapter = new OmarAdapter(ImpostazioniSAP.this, Global.impostazioniSAP, R.layout.omar_riga_modifica, da, a);//Create object and set the parameters for simpleAdapter

        grigliaViste.setAdapter(omarAdapter);//sets the adapter for listView

    }

    private long lastClickTime = 0;
    // se vado avanti imposto tutti valori scelti
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
        // Salva i valori globali
        for (int t = 0; t< grigliaViste.getCount(); t++) {
            //Log.d("TITOLO:", getTitolo(t));
            //Log.d("TITOLO:", getDescrizione(t));
            Global.setImpostazioniSAP(getTitolo(t), getDescrizione(t));
        }
        // torna all'activity che l'ha chiamato
        customBackPressed();

    }
}
