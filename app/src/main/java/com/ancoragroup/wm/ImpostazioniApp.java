package com.ancoragroup.wm;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class ImpostazioniApp extends OmarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        bottoneIndietro.setText("ANNULLA");
        bottoneIndietro.setTextSize(12);
        bottoneAvanti.setText("SALVA");

        titolo = "ImpostazioniApp";

        // permette di tenere visibile il campo in edit
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        listaValori = Global.getImpostazioniAppHash();
        OmarAdapter omarAdapter = new OmarAdapter(ImpostazioniApp.this, listaValori , R.layout.omar_riga_modifica, da, a);

        grigliaViste.setAdapter(omarAdapter);//sets the adapter for listView

    }


    // se vado avanti imposto tutti valori scelti
    @Override
    public void bottoneAvantiClick (View v) {

        // Salva i valori globali
        for (int t = 0; t< grigliaViste.getCount(); t++) {
            Global.setImpostazioniApp(getTitolo(t), getDescrizione(t));
        }
        // torna all'activity che l'ha chiamato
        customBackPressed();
    }
}
