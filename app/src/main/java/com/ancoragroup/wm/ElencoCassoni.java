package com.ancoragroup.wm;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ElencoCassoni extends Griglia {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        impostaBottoneInvisibile(bottoneAzione1);

        boolean fromModula = "true".equals(getIntent().getStringExtra("fromModula"));
        // Se vengo da modula, tolgo "SPOSTA"
        if (fromModula) {
            impostaBottoneInvisibile(bottoneAzione2);
        } else {
            impostaBottoneSelezioneSingolaRiga(bottoneAzione2, "Sposta HU");
        }
        impostaBottoneInvisibile(bottoneAzione3);
        impostaBottoneSelezioneMultipleRighe(bottoneAvanti, "Contenuto HU");

        campoOrdinamento = 1;
    }

    @Override
    public void impostaTuttiCampi() {
        super.impostaTuttiCampi();

       setCampo("EXIDV2");
       setCampo("AUFNR");
       setCampo("MATNR");
       setCampo("VEMNG");
       setCampo("VEMNG");
       setCampo("SOBKZ");
       setCampo("SONUM");
       setCampo("EXIDV");
       setCampo("LGTYP", "Tipo");
       setCampo("LGPLA");
       setCampo("LGNUM");

    }

    @Override
    public void impostaCampiGriglia() {
        super.impostaCampiGriglia();
/*
        aggiungiCampoRiga(1, "EXIDV2");
        aggiungiCampoRiga(1, "AUFNR");
        aggiungiCampoRiga(1, "MATNR");
        aggiungiCampoRiga(1, "VEMNG");
        aggiungiCampoRiga(1, "VEMNG");
        aggiungiCampoRiga(1, "SOBKZ");
        aggiungiCampoRiga(1, "SONUM");
        aggiungiCampoRiga(1, "EXIDV");
        aggiungiCampoRiga(1, "LGTYP");
        aggiungiCampoRiga(1, "LGPLA");
        aggiungiCampoRiga(1, "LGNUM");
        */
        /*
        aggiungiCampoRiga(1, "LGTYP");
        aggiungiCampoRiga(1, "LGPLA");
        aggiungiCampoRiga(1, "EXIDV2");
        aggiungiCampoRiga(1, "ALGPLA")
        */

        aggiungiCampoRiga(1,"EXIDV2");

        //aggiungiCampoRiga(1,"MATNR");
        aggiungiCampoRiga(1,"LGTYP");
        aggiungiCampoRiga(1,"LGPLA");

        aggiungiCampoRiga(1,"AUFNR");
    }

    @Override
    public void aggiungiCampiCalcolati(int ciclo) { }

    @Override
    public String getUrl(int ciclo) {
        String url = "";
        url = Global.serverURL + Global.CASSONI_GET_LIST;

        // Se ho specificato ODP ci metto uno 0 davanti altrimenti non trova niente
        String odp = myIntent.getStringExtra("Odp");
        if (!odp.equals("")) {
            odp = "0" + odp;
        }

        url = url.replace("#IT_EXIDV2#", myIntent.getStringExtra("HU"));
        url = url.replace("#IT_MATNR#", myIntent.getStringExtra("Materiale"));
        url = url.replace("#IT_AUFNR#", odp);

        Log.d("URLCASSONI", url);


        return url;
    }

    @Override
    public void bottoneAvantiSingolaRiga (int sel_riga) {
        Intent intent = new Intent(this, VerificaCassoni.class);
        intent.putExtra("titolo", "Verifica HU");
        intent.putExtra("info2", listaValori.get(sel_riga).get("EXIDV2").trim());
        intent.putExtra("HU",  listaValori.get(sel_riga).get("EXIDV2").trim());

        startActivity(intent);
    }

    @Override
    public void bottoneAzione2SingolaRiga (int sel_riga) {
        if (sel_riga!=-1) {
            Intent intent = new Intent(this, PrendiParametri.class);
            intent.putExtra("titolo", "2 Sposta HU");
            intent.putExtra("info2", listaValori.get(sel_riga).get("EXIDV2").trim());
            intent.putExtra("HU",  listaValori.get(sel_riga).get("EXIDV2").trim());
            intent.putExtra("menuItem", Global.SPOSTACASSONE);
            ArrayList<HashMap<String, String>> temp = new ArrayList<>();
            temp.add(Global.putListaValori("HU", listaValori.get(sel_riga).get("EXIDV2").trim(), "", true));
            temp.add(Global.putListaValori("Tipo + ubic", "", "", true));
            intent.putExtra("listaValori",  temp);

            startActivity(intent);
        } else {
            Global.alert(this, "Riga non selezionata");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

}
