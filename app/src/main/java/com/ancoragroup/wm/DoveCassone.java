package com.ancoragroup.wm;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class DoveCassone extends Griglia {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        impostaBottoneInvisibile(bottoneAzione1);
        impostaBottoneInvisibile(bottoneAvanti);
        impostaBottoneSelezioneSingolaRiga(bottoneAzione2, "Verifica\nCassone");
        impostaBottoneSelezioneSingolaRiga(bottoneAzione3, "Sposta\nCassone");
    }

    @Override
    public void impostaTuttiCampi() {
        super.impostaTuttiCampi();

        setCampo("EV_EXIDV2");
        //setCampo("EV_EXIDV");
        setCampo("EV_LGTYP");
        setCampo("EV_LGPLA");
        setCampo("EV_LGNUM");
        setCampo("EV_AUFNR");

        campoOrdinamento = 1;
    }

    @Override
    public void impostaCampiGriglia() {
        super.impostaCampiGriglia();
        aggiungiCampoRiga(1, "EV_EXIDV2");
        //aggiungiCampoRiga(1, "EV_EXIDV");
        aggiungiCampoRiga(1, "EV_LGTYP");
        aggiungiCampoRiga(1, "EV_LGPLA");
        aggiungiCampoRiga(1, "EV_LGNUM");
        aggiungiCampoRiga(1, "EV_AUFNR");

    }

    @Override
    public void aggiungiCampiCalcolati(int ciclo) { }


    @Override
    public String getUrl(int ciclo) {
        String url = "";
        url = Global.serverURL + Global.CASSONI_LEGGI_UBICAZIONE;

        url = url.replaceAll("#LENUM#", myIntent.getStringExtra("HU"));
        url = url.replaceAll("#IV_AUFNR#", myIntent.getStringExtra("Odp"));

        Log.d("URL", url);

        return url;
    }


    @Override
    public void bottoneAzione2SingolaRiga (int sel_riga) {
        Intent intent = new Intent(this, VerificaCassoni.class);
        intent.putExtra("titolo", "3 Verifica Cassone" );
        intent.putExtra("menuItem", myIntent.getStringExtra("HU"));
        intent.putExtra("HU", myIntent.getStringExtra("HU"));

        startActivity(intent);
    }

    @Override
    public void bottoneAzione3SingolaRiga (int sel_riga) {
        ArrayList<HashMap<String, String>> temp = new ArrayList<>();
        temp.add(Global.putListaValori("HU", myIntent.getStringExtra("HU"), "", true));
        temp.add(Global.putListaValori("Tipo + ubic", "", "", true));

        Intent intent = intent = new Intent(this, PrendiParametri.class);
        intent.putExtra("titolo", "2 Sposta Cassone" );
        intent.putExtra("menuItem", "a");
        intent.putExtra("listaValori", temp);

        startActivity(intent);
    }

    @Override
    public void bottoneAvantiSingolaRiga (int sel_riga) {
        Intent intent = new Intent(this, Esito.class);

        ArrayList<HashMap<String, String>> listaValoriNext = new ArrayList<>();

        listaValoriNext.add(Global.putListaValori("A Ubic",listaValori.get(sel_riga).get("Destinazione"),"READONLY"));

        intent.putExtra("listaValori", listaValoriNext );
        startActivity(intent);
    }

    @Override
    public void bottoneAvantiClickLong (View v) {
        AlertDialog dialog = new AlertDialog.Builder(v.getContext())

                .setTitle("Sei sicuro di voler procedere per TUTTI gli elementi selezionati?")
                .setMessage("HU")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        faiTutti();
                    }
                })
                .setNegativeButton("Annulla", null)
                .create();
        dialog.show();
    }

    public void faiTutti() {
        for (int sel_riga_temp = 0; sel_riga_temp < tabella.getChildCount(); sel_riga_temp++) {
            View child = tabella.getChildAt(sel_riga_temp);

            if (child instanceof TableRow) {
                TableRow row = (TableRow) child;

                for (int t = 0; t < row.getChildCount(); t++) {
                    View view = row.getChildAt(t);

                    if (view instanceof CheckBox) {
                        CheckBox cb = (CheckBox) view;
                        int sel_riga = sel_riga_temp / RIGHE;
                        if (cb.isChecked()) {
                            String url = Global.serverURL + Global.consumaCassone;

                            Log.d("URL", url);

                            LinkedHashMap<String, String> campiTest = new LinkedHashMap<>();
                            campiTest.put("MSGTX", "MSGTX");
                            ArrayList<HashMap<String, String>> listaValoriTemp = Global.getValoriXML(url, campiTest);
                        }
                    }
                }
            }
        }
        Global.disabledItem = true;
        onResume();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
