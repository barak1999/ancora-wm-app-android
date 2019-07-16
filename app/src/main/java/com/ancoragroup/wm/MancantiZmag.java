package com.ancoragroup.wm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MancantiZmag extends Griglia {
    private String qta = "";
    private String materiale = "";
    private String tubic = "";
    private String ubic = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        impostaBottoneDocumenta(bottoneAzione1);
        impostaBottoneSelezioneMultipleRighe(bottoneAvanti, "TRASF.");
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
        setCampo("LGPLA", "Ubic");
        setCampo("LOG", "St");
        setCampo("MATNR");
        setCampo("MAKTX");
        setCampo("VERME");
        setCampo("PLNBEZ");
        setCampo("AUFNR");
        setCampo("ZCODMAT");
        setCampo("LGORT", "Magazzino");
        setCampo("ENMNG", "QPrelevata");
        setCampo("TEXT", "Analisi");
        //campi meno utili
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

        setCampo("RIMANENTI", "Qta");
    }
    @Override
    public void impostaCampiGriglia() {
        super.impostaCampiGriglia();

        aggiungiCampoRiga(1, "RIMANENTI");
        aggiungiCampoRiga(1, "LOG");
        aggiungiCampoRiga(1, "LGTYP");
        aggiungiCampoRiga(1, "LGPLA");
        aggiungiCampoRiga(1, "PLNBEZ");
        aggiungiCampoRiga(1, "ZCODMAT");
        aggiungiCampoRiga(1, "AUFNR");
        aggiungiCampoRiga(1, "BDMNG");
        aggiungiCampoRiga(1, "LGORT");
        aggiungiCampoRiga(1, "ENMNG");
        aggiungiCampoRiga(1, "TEXT");
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
            info2 =" Qta: " + qta  + " - " + materiale + " - " + tubic + ubic;
        }

        String url = "";

        url = Global.serverURL + Global.zmagXML;
        url = url.replaceAll("#cod#", materiale);
        url = url.replaceAll("#desc#", "");
        url = url.replaceAll("#todp#", "");
        url = url.replaceAll("#odp#", "");
        url = url.replaceAll("#div#", Global.getImpostazoniSAP("Divisione"));
        url = url.replaceAll("#mag#", "");
        url = url.replaceAll("#nmag#", "");
        url = url.replaceAll("#stock#", "");
        url = url.replaceAll("#tubic#", "");
        url = url.replaceAll("#ubic#", "");
        url = url.replaceAll("#cassone#", "");

        Log.d("URLMancantiZmag", url);
        return url;
    }

    @Override
    public void aggiungiCampiCalcolati(int ciclo) {
        // aggiunge il campo calcolato RIMANENTI
        for (int t = 0; t < listaValoriTemp.size(); t++) {
            HashMap<String, String> item =  listaValoriTemp.get(t);

            float q_aperta = Global.getFloat(item.get("MENGE"));
            float q_disp = Global.getFloat(item.get("VERME"));
            float q_inprel = Global.getFloat(item.get("EINME"));

            item.put("RIMANENTI",String.valueOf(q_aperta - q_disp - q_inprel));
        }
    }


    @Override
    public boolean saltaValori (HashMap<String, String> item) {
        return !(item.get("LOG").equals("@5C@") || item.get("LOG").equals("@05@"));
    }

    private String gqta = qta;
    @Override
    public void bottoneAvantiClickStart() {
        gqta = qta;
    }

    @Override
    public void bottoneAvantiClickEnd() { }

    @Override
    public void bottoneAvantiSingolaRiga (int sel_riga) {
        String tipoDest = listaValori.get(sel_riga).get("LGTYP");
        String ubicDest = listaValori.get(sel_riga).get("LGPLA");
        String descrizione = listaValori.get(sel_riga).get("MAKTX");
        String rimanenti = listaValori.get(sel_riga).get("RIMANENTI");

        Intent intent = new Intent(MancantiZmag.this, PrendiParametri.class);
        intent.putExtra("menuItem", Global.MANCANTI_DA_ZMAG + "PASSO2");
        intent.putExtra("titolo",titolo);
        intent.putExtra("info2",info2);
        intent.putExtra("info3",info3);

        intent.putExtra("Bem",myIntent.getStringExtra("Bem"));
        intent.putExtra("Materiale", materiale);
        Log.d("MaterialeMancanti", materiale);
        intent.putExtra("Ubic", ubicDest);

        ArrayList<HashMap<String, String>> listaValoriNext = new ArrayList<>();
        listaValoriNext.add(Global.putListaValori("Da Ubic",tubic + ubic,"READONLY"));
        listaValoriNext.add(Global.putListaValori("A Ubic", tipoDest + ubicDest ,"READONLY"));
        listaValoriNext.add(Global.putListaValori("Materiale", materiale,"READONLY"));
        listaValoriNext.add(Global.putListaValori("Descrizione",descrizione,"READONLY"));
        listaValoriNext.add(Global.putListaValori("Qtà", Global.fromSAPtoWS(rimanenti),"READONLY"));
        String lqta = gqta;
        // sceglie sempre la quantità inferiore
        if (Global.getFloat(gqta) > Global.getFloat(rimanenti) ) {
            lqta = rimanenti;
        }
        listaValoriNext.add(Global.putListaValori("Quantita", Global.zapZero(lqta), "NUM"));
        listaValoriNext.add(Global.putListaValori("HU", "", ""));

        intent.putExtra("listaValori", listaValoriNext );

        intent.putExtra("NLTYP", tipoDest);
        intent.putExtra("NLPLA", ubicDest);
        gqta = String.valueOf(Global.getFloat(gqta) - Global.getFloat(lqta));
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void bottoneAvantiClickLong(View v) {

        //TODO mettere la lista dei valori
        boolean stop = false;
        ArrayList<String> multiUrl = new ArrayList<>();
        ArrayList<Integer> rows = new ArrayList<>();

        rows = getSelectedRows();

        Intent intent = new Intent(MancantiZmag.this, Esito.class);
        if (listaValori != null) {
            for (int i = 0; i < rows.size(); i++) {
                intent.putExtra("backMenuLoop", "1");
                intent.putExtra("menuItem", menuItem);
                String rimanenti = listaValori.get(rows.get(i)).get("RIMANENTI");
                String tipoDest = listaValori.get(rows.get(i)).get("LGTYP");
                String ubicDest = listaValori.get(rows.get(i)).get("LGPLA");
                String descrizione = listaValori.get(rows.get(i)).get("MAKTX");

                String bem = myIntent.getStringExtra("Bem");
                intent.putExtra("titolo", "Manc.BEM: " + bem);

                String esercizio = bem.substring(0, 4);
                String num_doc = bem.substring(4, 14);
                String pos_doc = bem.substring(14);

                String lqta = gqta;
                // sceglie sempre la quantità inferiore
                if (Global.getFloat(gqta) > Global.getFloat(rimanenti) ) {
                    lqta = rimanenti;
                }

                gqta = String.valueOf(Global.getFloat(gqta) - Global.getFloat(lqta));

                String quantita = rimanenti;
                if (rimanenti.contains(".")) {
                    quantita = rimanenti.substring(0, rimanenti.indexOf("."));
                }

                String url = Global.serverURL + Global.trasfMancantiXML;

                url = url.replaceAll("#I_BADGE_USER#", Global.mioBadge);
                url = url.replaceAll("#I_LGNUM#", Global.getImpostazoniSAP("Numero Magazzino"));
                url = url.replaceAll("#I_MJAHR#", esercizio);
                url = url.replaceAll("#I_MBLNR#", num_doc);
                url = url.replaceAll("#I_ZEILE#", pos_doc);
                url = url.replaceAll("#I_QTA#", quantita);
                url = url.replaceAll("#I_NLTYP#", tipoDest);
                url = url.replaceAll("#I_NLPLA#", ubicDest);
                url = url.replaceAll("#I_NLENR#", "");

                url = url.replaceAll("#I_MODE#", Global.I_MODE);
                url = url.replaceAll("#I_UPDATE#", Global.I_UPDATE);
                url = url.replaceAll("#I_WDATU#", Global.dataSAP);

                /*Log.d("TestIntent", "I_Lgnum: " + Global.getImpostazoniSAP("Numero Magazzino"));
                Log.d("TestIntent", "I_MJAHR: " + esercizio);
                Log.d("TestIntent", "I_MBLNR: " + num_doc);
                Log.d("TestIntent", "I_ZEILE: " + pos_doc);
                Log.d("TestIntent", "I_QTA: " + getDescrizione("Quantita"));
                Log.d("TestIntent", "I_NLTYP: " + myIntent.getStringExtra("NLTYP"));
                Log.d("TestIntent", "I_NLPLA: " + myIntent.getStringExtra("NLPLA"));
                Log.d("TestIntent", "I_NLENR: " + getDescrizione("HU"));
                Log.d("TestIntent", "I_MODE: " + Global.I_MODE);

    */
                Log.d("URLParte1", url);
                multiUrl.add(url);


                // TODO: Prima di lanciare, bisognerà inserire il controllo che c'è l'ubicazione (Dest. | Prov.) OPPURE il cassone (HU Dest. | HU Prov.). Non è possibile specificarli entrambi

                String url1 = Global.serverURL + Global.LT24_TRASFERIMENTO_NEW;


                url1 = url1.replaceAll("#I_AUFNR#", "00" + ubicDest);
                //url1 = url1.replaceAll("#I_MATNR#", myIntent.getStringExtra("Materiale"));
                url1 = url1.replaceAll("#I_MATNR#", listaValori.get(rows.get(i)).get("MATNR"));
                url1 = url1.replaceAll("#I_QTA_262#", Global.I_QTA_NULLA);

                String quantita1 = rimanenti;
                if (rimanenti.contains(".")) {
                    quantita1 = rimanenti.substring(0, rimanenti.indexOf("."));
                }

                Log.d("TESTQUANTITA", quantita1);
                url1 = url1.replaceAll("#I_MENGE#", quantita1);

                url1 = url1.replaceAll("#I_WERKS#", "D110");  //
                url1 = url1.replaceAll("#I_LGORT#", "W110");  //
                url1 = url1.replaceAll("#I_WDATU#", Global.dataSAP);
                url1 = url1.replaceAll("#I_MEINS#", "ST"); //
                url1 = url1.replaceAll("#I_SOBKZ#", "");
                url1 = url1.replaceAll("#I_SONUM#", "");
                url1 = url1.replaceAll("#I_BADGE_USER#", Global.mioBadge);
                url1 = url1.replaceAll("#I_LGNUM#", Global.getImpostazoniSAP("Numero Magazzino"));

                url1 = url1.replaceAll("#I_VLTYP#", tipoDest);
                url1 = url1.replaceAll("#I_VLPLA#", ubicDest);

                // TODO: Prima o poi da aggiungere controllo O cassone O destinazione
                url1 = url1.replaceAll("#I_NLTYP#", tipoDest);
                url1 = url1.replaceAll("#I_NLPLA#", ubicDest);


                url1 = url1.replaceAll("#I_EXIDV2_DA#", "");
                url1 = url1.replaceAll("#I_EXIDV2_A#", "");

                url1 = url1.replaceAll("#I_IN#", "X");

                Log.d("URLParte2", url1);


                multiUrl.add(url1);


            }
            intent.putExtra("multiUrl", multiUrl);
            startActivity(intent);
            finish();
        }
    }
}
