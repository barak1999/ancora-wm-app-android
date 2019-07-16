package com.ancoragroup.wm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TableRow;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class MessaInUbicazioneDaBem extends Griglia {
    private String qta = "";
    private String materiale = "";
    private String tubic = "";
    private String ubic = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        impostaBottoneDocumenta(bottoneAzione1);
        impostaBottoneInvisibile(bottoneAzione2);
        impostaBottoneInvisibile(bottoneAzione3);
        impostaBottoneSelezioneMultipleRighe(bottoneAzione2, "TRASF.");
    }

    @Override
    public void preparaDati() {

    }
    @Override
    public void impostaTuttiCampi() {
        super.impostaTuttiCampi();

        setCampo("WERKS");
        setCampo("LGNUM");
        setCampo("LGTYP", "Cat");
        setCampo("LGPLA");
        setCampo("LOG", "St");
        setCampo("MATNR");
        setCampo("MAKTX");
        setCampo("VERME");
        setCampo("PLNBEZ");
        setCampo("AUFNR");
        setCampo("ZCODMAT");
        setCampo("LGORT", "Magazzino");
        setCampo("ENMNG","QPrelevata");
        setCampo("TEXT","Analisi");

        setCampo("SOBKZ");
        setCampo("SONUM");
        setCampo("EINME");
        setCampo("AUSME");
        setCampo("LSONR");
        setCampo("PRVBE");
        setCampo("MENGE");
        setCampo("FTMENGE");
        setCampo("FTOFMEN");
        setCampo("BDMNG", "QAperta");
        setCampo("RLOG");
        setCampo("RTEXT");
        setCampo("LOG_QTY");
        setCampo("MBLNR");
        setCampo("MJAHR");

        setCampo("RIMANENTI", "Quantita", 1,true);
    }
    @Override
    public void impostaCampiGriglia() {
        super.impostaCampiGriglia();

        aggiungiCampoRiga(1, "RIMANENTI");
        aggiungiCampoRiga(1, "LOG");
        aggiungiCampoRiga(1, "PLNBEZ");
        aggiungiCampoRiga(1, "LGTYP");
        aggiungiCampoRiga(1, "LGPLA");
        //aggiungiCampoRiga(1, "LOG");//, "St");
        aggiungiCampoRiga(1, "BDMNG");
        aggiungiCampoRiga(1, "AUFNR");
        aggiungiCampoRiga(1, "LGORT");
        aggiungiCampoRiga(1, "ENMNG");
        aggiungiCampoRiga(1, "TEXT");
        aggiungiCampoRiga(1, "ZCODMAT");
    }
    public String getUrlTest() {
        String url = "";
        url = Global.serverURL + Global.checkMatDocUXML;

        String bem = myIntent.getStringExtra("Bem");

        String esercizio = "X";
        String num_doc = "X";
        String pos_doc = "X";

        if (bem.length()>13) {
            esercizio = bem.substring(0, 4);
            num_doc = bem.substring(4, 14);
            pos_doc = bem.substring(14);
        }


        url = url.replaceAll("#I_LGNUM#", Global.getImpostazoniSAP("Numero Magazzino"));
        url = url.replaceAll("#I_MJAHR#", esercizio);
        url = url.replaceAll("#I_MBLNR#", num_doc);
        url = url.replaceAll("#I_ZEILE#", pos_doc);

        Log.d("URL", url);

        return url;
    }

    @Override
    public String getUrl(int ciclo) {
        // prepara i dati
        LinkedHashMap<String, String> campiTest = new LinkedHashMap<>();
        campiTest.put("E_QTA", "E_QTA");
        campiTest.put("E_MATNR", "E_MATNR");
        campiTest.put("E_LGTYP", "E_LGTYP");
        campiTest.put("E_LGPLA", "E_LGPLA");
        ArrayList<HashMap<String, String>> listaValoriTemp = Global.getValoriXML(getUrlTest(), campiTest);

        if ("ERRORE".equals(listaValoriTemp.get(0).get("Titolo"))) {

            Log.d("URL", listaValoriTemp.get(0).get("Descrizione"));
            materiale = "99999999";
        } else {
            qta = listaValoriTemp.get(0).get("E_QTA");
            materiale = listaValoriTemp.get(0).get("E_MATNR");
            tubic = listaValoriTemp.get(0).get("E_LGTYP");
            ubic = listaValoriTemp.get(0).get("E_LGPLA");
            titolo = "Manc.BEM: " + myIntent.getStringExtra("Bem");

            updateToolBar();
        }

        String url = "";

        url = Global.serverURL + Global.zmagXML;
        url = url.replaceAll("#cod#", materiale);
        url = url.replaceAll("#desc#", "");
        url = url.replaceAll("#todp#", "");
        url = url.replaceAll("#odp#", "");
        url = url.replaceAll("#div#", Global.getImpostazoniSAP("Divisione"));
        url = url.replaceAll("#mag#", "");
        url = url.replaceAll("#namg#", "");
        url = url.replaceAll("#stock#", "");
        url = url.replaceAll("#tubic#", "");
        url = url.replaceAll("#ubic#", "");
        Log.d("URL", url);
        return url;
    }

    @Override
    public void aggiungiCampiCalcolati(int ciclo) {
        // aggiunge il campo calcolato RIMANENTI
        for (int t = 0; t < listaValori.size(); t++) {
            HashMap<String, String> item =  listaValori.get(t);

            float q_aperta = Global.getFloat(item.get("MENGE"));
            float q_disp = Global.getFloat(item.get("VERME"));
            float q_inprel = Global.getFloat(item.get("EINME"));

            item.put("RIMANENTI",String.valueOf(q_aperta - q_disp - q_inprel));
        }
    }
    @Override
    public void bottoneAvantiSingolaRiga (int sel_riga) {
        String tipoProv = listaValori.get(sel_riga).get("LGTYP");
        String ubicazProv = listaValori.get(sel_riga).get("LGPLA");
        String descrizione = listaValori.get(sel_riga).get("MAKTX");

        Intent intent = new Intent(this, PrendiParametri.class);
        intent.putExtra("menuItem", Global.MESSA_IN_UBICAZIONE_DA_BEM + "PASSO2");
        intent.putExtra("titolo", titolo);
        intent.putExtra("info2", "Quantità da trasferire");
        intent.putExtra("Bem",myIntent.getStringExtra("Bem"));
        intent.putExtra("Quantita", listaValori.get(sel_riga).get("I_QTA"));

        //Log.d("I_QTA", listaValori.get(sel_riga).get("I_QTA"));

        ArrayList<HashMap<String, String>> listaValoreTEMP = new ArrayList<>();

        listaValoreTEMP.add(listaValori.get(7));

        String aCassone = StringUtils.defaultString(listaValori.get(sel_riga).get("CASSONI"));
        ArrayList<HashMap<String, String>> listaValoriNext = new ArrayList<>();

        listaValoriNext.add(Global.putListaValori("Materiale",listaValori.get(sel_riga).get("MATNR") + " - " + listaValori.get(sel_riga).get("PLNBEZ"),"READONLY"));
        listaValoriNext.add(Global.putListaValori("Descrizione", descrizione,"READONLY"));
        listaValoriNext.add(Global.putListaValori("Stock", Global.zapZero(listaValori.get(sel_riga).get("MENGE")),"READONLY"));

        String qtaTot = Global.zapZero(listaValori.get(sel_riga).get("MENGE"));
        String qtaPrelevata = Global.zapZero(listaValori.get(sel_riga).get("VERME"));

        int qtaRimastaInt = Integer.valueOf(qtaTot) - Integer.valueOf(qtaPrelevata);

        String qtaRimasta = qtaRimastaInt + "";
        listaValoriNext.add(Global.putListaValori("Qta. Possibile" , qtaRimasta, "READONLY"));


        listaValoriNext.add(Global.putListaValori("Prov.", listaValori.get(sel_riga).get("LGORT") + " - " + listaValori.get(sel_riga).get("LGTYP") + listaValori.get(sel_riga).get("LGPLA"), "READONLY"));
        listaValoriNext.add(Global.putListaValori("Quantita", qtaRimasta ,"NUM"));
        listaValoriNext.add(Global.putListaValori("Dest.","",""));

        intent.putExtra("listaValori", listaValoriNext );

        intent.putExtra("NLTYP",listaValori.get(sel_riga).get("LGTYP"));
        intent.putExtra("NLPLA", listaValori.get(sel_riga).get("LGPLA"));

        startActivity(intent);

    }


}

