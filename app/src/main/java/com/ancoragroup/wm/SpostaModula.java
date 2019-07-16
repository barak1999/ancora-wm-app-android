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

public class SpostaModula extends Griglia {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        impostaBottoneInvisibile(bottoneAzione1);
        impostaBottoneInvisibile(bottoneAzione2);
        impostaBottoneInvisibile(bottoneAzione2);
        impostaBottoneSelezioneMultipleRighe(bottoneAvanti, "AVANTI");
    }

    @Override
    public void impostaTuttiCampi() {
        super.impostaTuttiCampi();

        setCampo("WERKS");
        setCampo("LGNUM");
        setCampo("LGTYP", "Tip");
        setCampo("LGPLA","Ubic");
        setCampo("LENUM","HU");
        setCampo("CASSONI");
        setCampo("LOG", "St");
        setCampo("MATNR");
        setCampo("MAKTX",10);
        setCampo("GESME");
        setCampo("VERME", "Disp");
        setCampo("PLNBEZ");
        setCampo("AUFNR");
        setCampo("ZCODMAT", 5);
        setCampo("LGORT", "Magazzino");
        setCampo("ENMNG","OpPr");
        setCampo("TEXT","Analisi",4);


        setCampo("SOBKZ","TpStk");
        setCampo("SONUM");
        setCampo("AUSME", "Pre");
        setCampo("EINME", "Ver");
        setCampo("LSONR", "StockSP");
        setCampo("PRVBE");
        setCampo("MENGE");
        setCampo("FTMENGE");
        setCampo("FTOFMEN");
        setCampo("BDMNG","OpAp");
        setCampo("RLOG");
        setCampo("RTEXT");
        setCampo("LOG_QTY");
        setCampo("MBLNR");
        setCampo("MJAHR");
        setCampo("MEINS","UM",1, true);
        setCampo("NAME1", 5);
        setCampo("PLNBEZ", "Mat.Odp.");
        setCampo("KTEXT","Descr.Mat.Odp",4);
        setCampo("EDATU",2);
        setCampo("WENUM","NrEM");
        setCampo("BKTXT");

        setCampo("SPACE2", "                                                          ");

    }
    @Override
    public void impostaCampiGriglia() {
        super.impostaCampiGriglia();

        aggiungiCampoRiga(1, "LGTYP");//, "Tip");
        aggiungiCampoRiga(1, "LGPLA");// "Ubic");
        aggiungiCampoRiga(1, "HU");// "Cassone");
        aggiungiCampoRiga(1, "MATNR");// "Materiale");
        aggiungiCampoRiga(1, "LOG");// "St");

        aggiungiCampoRiga(1, "GESME");// "Giac");
        aggiungiCampoRiga(1, "VERME");// "Disp");
        aggiungiCampoRiga(1, "EINME");// "Ver");
        aggiungiCampoRiga(1, "AUSME");// "Pre");
        aggiungiCampoRiga(1, "BDMNG");// "OpAp");   // ODP APERTI
        aggiungiCampoRiga(1, "ENMNG");// "OpPr");   // ODP PRELEVATI

        aggiungiCampoRiga(1, "SOBKZ");// "TpStk");
        aggiungiCampoRiga(1, "LSONR");// "StockSP");
        aggiungiCampoRiga(1, "WENUM");// "NrEM");
        aggiungiCampoRiga(1, "SPACE2");

        aggiungiCampoRiga(2, "MEINS");// new FieldItem("UM",1));
        aggiungiCampoRiga(2, "LGORT");// new FieldItem("Magazzino",1));
        aggiungiCampoRiga(2, "MATNR");// new FieldItem("Materiale",1));
        aggiungiCampoRiga(2, "MAKTX");// new FieldItem("Descrizione",10));

        aggiungiCampoRiga(3, "LOG");// new FieldItem("St", 1));
        aggiungiCampoRiga(3, "BKTXT");// new FieldItem("Zona",1));
        aggiungiCampoRiga(3, "PLNBEZ");// new FieldItem("Mat.Odp.",1));
        aggiungiCampoRiga(3, "AUFNR");// new FieldItem("Ordine",1));
        aggiungiCampoRiga(3, "ZCODMAT");// new FieldItem("Mat. Cliente",5));
        aggiungiCampoRiga(3, "KTEXT");// new FieldItem("Descr.Mat.Odp",4));

        aggiungiCampoRiga(4, "EDATU");// new FieldItem("DataEM",2));
        aggiungiCampoRiga(4, "WENUM");// new FieldItem("NrEM",1));
        aggiungiCampoRiga(4, "NAME1");// new FieldItem("Cli/For", 5));
        aggiungiCampoRiga(4, "TEXT");// new FieldItem("Analisi",4));

    }
    @Override
    public void aggiungiCampiCalcolati(int ciclo) {

    }

    @Override
    public String getUrl(int ciclo) {
        String url = "";
        String posizione = myIntent.getStringExtra("Posizione");
        url = Global.serverURL + Global.leggiCassoniModula;
        url = url.replaceAll("#POSIZIONE#", posizione);

        Log.d("URL", url);

        return url;
    }


    @Override
    public void bottoneAvantiSingolaRiga (int sel_riga) {
        Intent intent = new Intent(this, PrendiParametri.class);
        intent.putExtra("titolo", titolo);
        intent.putExtra("info2", "Trasferisci in");
        intent.putExtra("Bem", myIntent.getStringExtra("Bem"));

        ArrayList<HashMap<String, String>> listaValoriNext = new ArrayList<>();

        listaValoriNext.add(Global.putListaValori("Materiale",listaValori.get(sel_riga).get("MATNR"),"READONLY"));
        listaValoriNext.add(Global.putListaValori("Descrizione",listaValori.get(sel_riga).get("MAKTX"),"READONLY"));
        listaValoriNext.add(Global.putListaValori("Stock",Global.zapZero(listaValori.get(sel_riga).get("GESME")),"READONLY"));

        listaValoriNext.add(Global.putListaValori("Provenienza", listaValori.get(sel_riga).get("LGORT") + " - " + listaValori.get(sel_riga).get("LGTYP") + listaValori.get(sel_riga).get("LGPLA"), "READONLY"));
        listaValoriNext.add(Global.putListaValori("Destinazione", listaValori.get(sel_riga).get("LGTYP") + listaValori.get(sel_riga).get("LGPLA"), "READONLY"));   //NLTYP + NLPLA
        listaValoriNext.add(Global.putListaValori("HU", "", ""));   //NLENR
        listaValoriNext.add(Global.putListaValori("Quantita", Global.zapZero(listaValori.get(sel_riga).get("VERME")), "READONLY"));
        intent.putExtra("listaValori", listaValoriNext);

        intent.putExtra("menuItem", Global.INFO_STOCK + "TR.IN");

        intent.putExtra("MOV262", Global.I_MOV262);

        intent.putExtra("matnr", listaValori.get(sel_riga).get("MATNR"));

        intent.putExtra("MEINS", listaValori.get(sel_riga).get("MEINS"));
        intent.putExtra("SOBKZ", listaValori.get(sel_riga).get("SOBKZ"));
        intent.putExtra("SONUM", listaValori.get(sel_riga).get("SONUM"));
        intent.putExtra("AUFNR", listaValori.get(sel_riga).get("AUFNR"));
        intent.putExtra("MATNR", listaValori.get(sel_riga).get("MATNR"));


        intent.putExtra("WERKS", listaValori.get(sel_riga).get("WERKS"));
        intent.putExtra("LGNUM", listaValori.get(sel_riga).get("LGNUM"));
        intent.putExtra("LGORT", listaValori.get(sel_riga).get("LGORT"));

        intent.putExtra("LGTYP", listaValori.get(sel_riga).get("LGTYP"));
        intent.putExtra("LGPLA", listaValori.get(sel_riga).get("LGPLA"));

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
