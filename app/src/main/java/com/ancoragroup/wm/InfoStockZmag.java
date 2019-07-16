package com.ancoragroup.wm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TableRow;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class InfoStockZmag extends Griglia {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        impostaBottoneDocumenta(bottoneAzione1);
        impostaBottoneSelezioneMultipleRighe(bottoneAzione2, "Tr. In");
        impostaBottoneSelezioneMultipleRighe(bottoneAzione3, "Tr. Da");
        impostaBottoneSelezioneSingolaRiga(bottoneAvanti, (menuItem.equals(Global.INFO_STOCK_UBICAZIONE) ? "In.St.\nMatr" : "In.St.\nUbic"));
    }
    @Override
    public void impostaTuttiCampi() {
        super.impostaTuttiCampi();

        setCampo("WERKS");
        setCampo("LGNUM");
        setCampo("LGTYP", "Cat");
        setCampo("LGPLA");

        // Tolti perchè per ora non sono gestiti, quando verranno gestiti togliere il commento
        /*
        setCampo("CASSONI");
        setCampo("AREE");
        */
        setCampo("CASSONI");

        setCampo("LOG");
        setCampo("MATNR");

        setCampo("GESME", "Giac");
        setCampo("VERME", "Disp");
        setCampo("PLNBEZ");
        setCampo("AUFNR", 3);
        setCampo("ZCODMAT", 5);
        setCampo("LGORT");
        setCampo("ENMNG", "OpPr");
        setCampo("TEXT", 4);
        //campi meno utili
        setCampo("SOBKZ", "TpStk");
        setCampo("SONUM");
        setCampo("AUSME", "Pre");
        setCampo("EINME", "Ver");
        setCampo("LSONR", "StockSp");
        setCampo("PRVBE");
        setCampo("MENGE");
        setCampo("FTMENGE");
        setCampo("FTOFMEN");
        setCampo("BDMNG", "OpAp");
        setCampo("RLOG");
        setCampo("RTEXT");
        setCampo("LOG_QTY");
        setCampo("MBLNR");
        setCampo("MJAHR");
        setCampo("MEINS");
        setCampo("NAME1", 5);
        setCampo("PLNBEZ", "Mat. Odp");
        setCampo("KTEXT", 4);
        setCampo("EDATU", 2);
        setCampo("WENUM");
        setCampo("BKTXT");

        setCampo("REFNT");
        setCampo("REFNR");
        setCampo("NLTYP");
        setCampo("NLPLA");
        setCampo("VLTYP");
        setCampo("VLPLA");
        setCampo("VSOLM_C");
        setCampo("ALTME", Global.getLabel("ALTME"), true);
        setCampo("MAKTX", 8);
        setCampo("ZZCID");
        setCampo("ZZTBPRI");
        setCampo("SPACE2", "                                                          ");
    }

    @Override
    public void impostaCampiGriglia() {
        super.impostaCampiGriglia();

        // Campi prima riga
        if (menuItem.equals(Global.INFO_STOCK) || menuItem.equals(Global.INFO_STOCK_ODP)) {
            aggiungiCampoRiga(1, "LGTYP");
            aggiungiCampoRiga(1, "LGPLA");

            // Tolti perchè per ora non sono gestiti, quando verranno gestiti togliere il commento
            aggiungiCampoRiga(1, "CASSONI");
            //aggiungiCampoRiga(1, "AREE");
            aggiungiCampoRiga(1, "GESME");
            aggiungiCampoRiga(1, "VERME");
            aggiungiCampoRiga(1, "EINME");
            aggiungiCampoRiga(1, "AUSME");
            aggiungiCampoRiga(1, "BDMNG");
            aggiungiCampoRiga(1, "ENMNG");

        } else if (menuItem.equals(Global.INFO_STOCK_MATERIALE)) {
            aggiungiCampoRiga(1, "LGTYP");
            aggiungiCampoRiga(1, "LGPLA");
            aggiungiCampoRiga(1, "CASSONI");
            //aggiungiCampoRiga(1, "AREE");
            aggiungiCampoRiga(1, "GESME");
            aggiungiCampoRiga(1, "VERME");
            aggiungiCampoRiga(1, "EINME");
            aggiungiCampoRiga(1, "AUSME");
            aggiungiCampoRiga(1, "BDMNG");
            aggiungiCampoRiga(1, "ENMNG");
        } else if (menuItem.equals(Global.INFO_STOCK_UBICAZIONE)) {
            aggiungiCampoRiga(1, "MATNR");
            // Tolti perchè per ora non sono gestiti, quando verranno gestiti togliere il commento
            aggiungiCampoRiga(1, "CASSONI");
            /*

            aggiungiCampoRiga(1, "LOG");;
            */

            aggiungiCampoRiga(1, "GESME");
            aggiungiCampoRiga(1, "VERME");
            aggiungiCampoRiga(1, "EINME");
            aggiungiCampoRiga(1, "AUSME");
            aggiungiCampoRiga(1, "BDMNG");   // ODP APERTI
            aggiungiCampoRiga(1, "ENMNG");   // ODP PRELEVATI

        } else {
            aggiungiCampoRiga(1, "MATNR");
            // Tolti perchè per ora non sono gestiti, quando verranno gestiti togliere il commento
            aggiungiCampoRiga(1, "CASSONI");
            /*
            aggiungiCampoRiga(1, "AREE");
            */
            aggiungiCampoRiga(1, "LOG");

            aggiungiCampoRiga(1, "GESME");
            aggiungiCampoRiga(1, "VERME");
            aggiungiCampoRiga(1, "EINME");
            aggiungiCampoRiga(1, "AUSME");
            aggiungiCampoRiga(1, "BDMNG");  // ODP APERTI
            aggiungiCampoRiga(1, "ENMNG");  // ODP PRELEVATI
        }



        aggiungiCampoRiga(1, "SOBKZ");
        aggiungiCampoRiga(1, "LSONR");
        aggiungiCampoRiga(1, "WENUM");
        aggiungiCampoRiga(1, "SPACE2");


        // RIGA 2
        aggiungiCampoRiga(2, "MEINS");
        aggiungiCampoRiga(2, "LGORT");
        aggiungiCampoRiga(2, "MATNR");
        aggiungiCampoRiga(2, "MAKTX");

        // RIGA 3
        aggiungiCampoRiga(3, "LOG");
        aggiungiCampoRiga(3, "BKTXT");

        if (menuItem.equals(Global.INFO_STOCK) || menuItem.equals(Global.INFO_STOCK_ODP)) {
            aggiungiCampoRiga(3, "PLNBEZ");
            aggiungiCampoRiga(3, "AUFNR");
            aggiungiCampoRiga(3, "ZCODMAT");
            aggiungiCampoRiga(3, "KTEXT");
        } else if (menuItem.equals(Global.INFO_STOCK_MATERIALE)) {
            aggiungiCampoRiga(3, "PLNBEZ");
            aggiungiCampoRiga(3, "AUFNR");
            aggiungiCampoRiga(3, "ZCODMAT");
            aggiungiCampoRiga(3, "KTEXT");
        } else if (menuItem.equals(Global.INFO_STOCK_UBICAZIONE)) {
            aggiungiCampoRiga(3, "PLNBEZ");
            aggiungiCampoRiga(3, "AUFNR");
            aggiungiCampoRiga(3, "ZCODMAT");
            aggiungiCampoRiga(3, "KTEXT");
        }

        // RIGA 4
        aggiungiCampoRiga(4,"EDATU");
        aggiungiCampoRiga(4,"WENUM");
        aggiungiCampoRiga(4,"NAME1");
        aggiungiCampoRiga(4,"TEXT");
    }

    @Override
    public String getUrl(int ciclo) {
        String url = "";
        url = Global.serverURL + Global.zmagXML;
        String tipoubic = myIntent.getStringExtra("Tipo + ubic");

        String tubic = "";
        String ubic = "";
        if (tipoubic != null && !tipoubic.equals("")) tubic = tipoubic.substring(0, 3);
        if (tipoubic != null && !tipoubic.equals("")) ubic = tipoubic.substring(3);

        if (tipoubic != null) { Log.d("URL: openInfoStockUbic", tipoubic);}
        else {Log.d("URL: openInfoStockUbic", "null");}

        url = url.replaceAll("#cod#", myIntent.getStringExtra("Materiale"));
        url = url.replaceAll("#desc#", myIntent.getStringExtra("Descrizione"));
        url = url.replaceAll("#todp#", myIntent.getStringExtra("Tipo ordine"));
        url = url.replaceAll("#odp#", myIntent.getStringExtra("Ordine"));
        url = url.replaceAll("#div#", myIntent.getStringExtra("Divisione"));
        url = url.replaceAll("#mag#", myIntent.getStringExtra("Magazzino"));
        url = url.replaceAll("#nmag#", myIntent.getStringExtra("Numero Mag."));

        String parStock = "";
        if (myIntent.getStringExtra("Stock/Odp").equals("STOCK")) parStock = "X";

        url = url.replaceAll("#stock#", parStock );
        url = url.replaceAll("#tubic#", tubic);
        url = url.replaceAll("#ubic#", ubic);
        url = url.replaceAll("#cassone#", myIntent.getStringExtra("HU"));
        Log.d("URLListaInfoStock", url);

        return url;
    }

    // TR.IN
    @Override
    public void bottoneAzione2SingolaRiga(int sel_riga) {
        Intent intent = new Intent(this, PrendiParametri.class);
        intent.putExtra("titolo", titolo);
        intent.putExtra("info2", "Trasferisci in");
        intent.putExtra("Bem", myIntent.getStringExtra("Bem"));



        String aCassone = StringUtils.defaultString(listaValori.get(sel_riga).get("CASSONI"));

        ArrayList<HashMap<String, String>> listaValoriNext = new ArrayList<>();

        listaValoriNext.add(Global.putListaValori("Materiale",listaValori.get(sel_riga).get("MATNR"),"READONLY"));
        listaValoriNext.add(Global.putListaValori("Descrizione",listaValori.get(sel_riga).get("MAKTX"),"READONLY"));
        listaValoriNext.add(Global.putListaValori("Stock",Global.zapZero(listaValori.get(sel_riga).get("GESME")),"READONLY"));

        listaValoriNext.add(Global.putListaValori("Prov.", listaValori.get(sel_riga).get("LGORT") + " - " + listaValori.get(sel_riga).get("LGTYP") + listaValori.get(sel_riga).get("LGPLA"), "READONLY"));
        listaValoriNext.add(Global.putListaValori("HU Prov.", aCassone, "READONLY"));   //NLENR

        listaValoriNext.add(Global.putListaValori("Dest.", "", ""));   //NLTYP + NLPLA
        listaValoriNext.add(Global.putListaValori("HU Dest.", "", ""));   //NLENR
        listaValoriNext.add(Global.putListaValori("Quantita", Global.zapZero(listaValori.get(sel_riga).get("VERME")), "NUM"));
        intent.putExtra("listaValori", listaValoriNext);

        intent.putExtra("menuItem", Global.INFO_STOCK + "TR.IN");

        intent.putExtra("MOV262", Global.I_MOV262);

        intent.putExtra("matnr", listaValori.get(sel_riga).get("MATNR"));

        intent.putExtra("MEINS", listaValori.get(sel_riga).get("MEINS"));

        Log.d("TESTINTENTMEINS", listaValori.get(sel_riga).get("MEINS"));

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

    // TR.DA
    @Override
    public void bottoneAzione3SingolaRiga(int sel_riga) {
        Intent intent = new Intent(this, PrendiParametri.class);
        intent.putExtra("titolo", titolo);
        intent.putExtra("info2", "Trasferisci da");
        intent.putExtra("Bem", myIntent.getStringExtra("Bem"));

        ArrayList<HashMap<String, String>> listaValoriNext = new ArrayList<>();

        String daCassone = StringUtils.defaultString(listaValori.get(sel_riga).get("CASSONI"));
        listaValoriNext.add(Global.putListaValori("Materiale",listaValori.get(sel_riga).get("MATNR"),"READONLY"));
        listaValoriNext.add(Global.putListaValori("Descrizione",listaValori.get(sel_riga).get("MAKTX"),"READONLY"));
        listaValoriNext.add(Global.putListaValori("Stock",Global.zapZero(listaValori.get(sel_riga).get("GESME")),"READONLY"));
        listaValoriNext.add(Global.putListaValori("Prov.", "", ""));

        listaValoriNext.add(Global.putListaValori("HU Prov.", "", ""));   //NLENR

        listaValoriNext.add(Global.putListaValori("Dest.", listaValori.get(sel_riga).get("LGORT") + " - " + listaValori.get(sel_riga).get("LGTYP") + listaValori.get(sel_riga).get("LGPLA"), "READONLY"));
        listaValoriNext.add(Global.putListaValori("HU Dest.", daCassone, "READONLY"));   //NLENR

        listaValoriNext.add(Global.putListaValori("Quantita", Global.zapZero(listaValori.get(sel_riga).get("VERME")), "NUM"));
        intent.putExtra("listaValori", listaValoriNext);

        intent.putExtra("menuItem", Global.INFO_STOCK + "TR.DA");

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
    public void bottoneAvantiSingolaRiga (int sel_riga) {
        if (sel_riga!=-1) {
            if (menuItem.equals(Global.INFO_STOCK_UBICAZIONE)) {
                openInfoStockMatr(true, listaValori.get(sel_riga));
            } else {
                openInfoStockUbic(true, listaValori.get(sel_riga));
            }
        } else {
            Global.alert(this, "Riga non selezionata");
        }
    }

    protected void onResume() {
        super.onResume();

    }
}
