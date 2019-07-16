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

public class DistintaMacchina extends Griglia {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        impostaBottoneInvisibile(bottoneAzione1);
        impostaBottoneSelezioneSingolaRiga(bottoneAzione2, "Aggiungi Seriali");
        impostaBottoneInvisibile(bottoneAzione3);
        impostaBottoneSelezioneMultipleRighe(bottoneAvanti, "Cancella seriali");

        // preordinato per data
        campoOrdinamento = 1;
    }

    @Override
    public void impostaTuttiCampi() {
        super.impostaTuttiCampi();

        setCampo("MATNR");
        setCampo("SERNR");
        setCampo("SPACE1", "                                                          ");
        setCampo("SPACE2", "");

    }
    @Override
    public void impostaCampiGriglia() {
        super.impostaCampiGriglia();

        aggiungiCampoRiga(1, "MATNR");
        aggiungiCampoRiga(1, "SERNR");
        aggiungiCampoRiga(1, "SPACE2");

    }

    public String getUrlTest(String serialeFiglio) {
        String url = "";
        url = Global.serverURL + Global.elabSeriali;

        url = url.replaceAll("#I_FUNCT#", "WHERE_USED");
        url = url.replaceAll("#I_SERNR#", "" );
        url = url.replaceAll("#I_SERNR_INF#", serialeFiglio);

        Log.d("URL", url);

        return url;
    }

    @Override
    public String getUrl(int ciclo) {
        String seriale = myIntent.getStringExtra("Macchina");
        // se ho il seriale del figlio recupero il seriale padre
        String serialeFiglio = myIntent.getStringExtra("Testa/Mandrino");

        if (serialeFiglio!=null && !serialeFiglio.equalsIgnoreCase("")) {
            LinkedHashMap<String, String> campiTest = new LinkedHashMap<>();
            //campiTest.put("E_QTA", "E_QTA");
            campiTest.put("MATNR", "MATNR");
            campiTest.put("SERNR", "SERNR");
            ArrayList<HashMap<String, String>> listaValoriTemp = Global.getValoriXML(getUrlTest(serialeFiglio), campiTest);
            if (listaValoriTemp!=null) {
                seriale = listaValoriTemp.get(0).get("SERNR");
            }
        }

        String url = "";
        url = Global.serverURL + Global.elabSeriali;

        url = url.replaceAll("#I_FUNCT#", "GET_LIST");
        url = url.replaceAll("#I_SERNR#", seriale );
        url = url.replaceAll("#I_SERNR_INF#", "");

        info2 = seriale;
        info3 = serialeFiglio;

        Log.d("URL", url);

        return url;
    }


    @Override
    public void bottoneAzione2Click (View v) {
        Intent intent = new Intent();
        intent = new Intent(DistintaMacchina.this, PrendiParametri.class);
        intent.putExtra("titolo", "Aggiungi Seriali");
        intent.putExtra("menuItem", menuItem + "PASSO2");
        intent.putExtra("Macchina", myIntent.getStringExtra("Macchina"));
        ArrayList<HashMap<String, String>> temp = new ArrayList<>();
        for (int i=1; i<=24; i++) {
            temp.add(Global.putListaValori("RFID"+i, "", ""));
        }
        intent.putExtra("listaValori", temp);
        startActivity(intent);
    }

    @Override
    public void bottoneAvantiSingolaRiga (int sel_riga) {
        Intent intent = new Intent();
        intent = new Intent(DistintaMacchina.this, Esito.class);
        String url = Global.serverURL + Global.elabSeriali;

        url = url.replaceAll("#I_FUNCT#", "DELETE_SINGLE");
        url = url.replaceAll("#I_SERNR#",  myIntent.getStringExtra("Macchina") );
        url = url.replaceAll("#I_SERNR_INF#", listaValori.get(sel_riga).get("SERNR"));

        Log.d("URL",url);

        intent.putExtra("url", url);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
