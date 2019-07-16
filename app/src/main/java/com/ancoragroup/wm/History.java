package com.ancoragroup.wm;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;

public class History extends Griglia {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        impostaBottoneInvisibile(bottoneAzione1);
        impostaBottoneInvisibile(bottoneAzione3);
        impostaBottoneInvisibile(bottoneAvanti);
        // preordinato per data
        orderByDesc = true;
        campoOrdinamento = 3;

    }
    @Override
    public void impostaTuttiCampi() {
        super.impostaTuttiCampi();

        setCampo("EQUNR","Ubic. Dest");
        //
        setCampo("DATUM", "UMA", true);
        setCampo("SERNR", true, Color.BLUE);
        setCampo("MATNR", true, Color.RED);
        setCampo("MAKTX", 3);

        setCampo("ERNAM");
        setCampo("LTXA1", "Gruppo", 2);
        setCampo("FULLNAME");

    }
    @Override
    public void impostaCampiGriglia() {
        super.impostaCampiGriglia();

        aggiungiCampoRiga(1,"SERNR");
        aggiungiCampoRiga(1,"EQUNR");
        aggiungiCampoRiga(1,"DATUM");

        aggiungiCampoRiga(2, "MATNR");
        aggiungiCampoRiga(2, "MAKTX");

        aggiungiCampoRiga(3, "ERNAM");
        aggiungiCampoRiga(3, "LTXA1");
        aggiungiCampoRiga(3, "FULLNAME");

    }

    @Override
    public void aggiungiCampiCalcolati(int ciclo) {
        if (listaValoriTemp.size()>0) {
            HashMap<String, String> item =  listaValoriTemp.get(0);
            info3 = item.get("RFID"+0);
        }
    }

    @Override
    public String getUrl(int ciclo) {
        String url = "";
        url = Global.serverURL + Global.leggiCronologiaCodice;

        url = url.replaceAll("#IV_SERNR#", getIntent().getStringExtra("RFID"+0));


        Log.d("URL", url);

        return url;
    }


}
