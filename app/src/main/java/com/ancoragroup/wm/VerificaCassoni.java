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

public class VerificaCassoni extends Griglia {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        impostaBottoneDocumenta(bottoneAzione1);
        impostaBottoneSelezioneSingolaRiga(bottoneAzione2, "Pack");
        impostaBottoneSelezioneMultipleRighe(bottoneAzione3, "Cons");
        impostaBottoneSelezioneMultipleRighe(bottoneAvanti, "Sposta");

    }

    @Override
    public void impostaTuttiCampi() {
        super.impostaTuttiCampi();

        setCampo("VEMNG");
        setCampo("VEMEH");
        setCampo("SOBKZ");
        setCampo("VARKEY");

        setCampo("WERKS");
        setCampo("LGNUM");
        setCampo("LGTYP", "Tip");
        setCampo("LGPLA", "Ubic");
        setCampo("CASSONI");

        setCampo("LOG", "St");
        setCampo("MATNR");
        setCampo("MAKTX", "Descrizione", 10);
        setCampo("GESME", "Giac");
        setCampo("VERME", "Disp");
        setCampo("PLNBEZ");
        setCampo("AUFNR");
        setCampo("ZCODMAT",5);
        setCampo("LGORT");
        setCampo("ENMNG", "OpPr");
        setCampo("TEXT", "Analisi", 4);

        // campi meno utilli
        setCampo("SOBKZ", "TpStk");
        setCampo("SONUM");
        setCampo("AUSME", "Pre");
        setCampo("EINME", "Ver");
        setCampo("LSONR", "StockSP");
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
        setCampo("NAME1",5);
        setCampo("PLNBEZ", "Mat. Odp");
        setCampo("KTEXT",4);
        setCampo("EDATU",2);
        setCampo("WENUM", "NrEM");
        setCampo("BKTXT");
        setCampo("SPACE2", "                                                          ");

        setCampo("EXIDV", 4);
        campoOrdinamento = 1;
    }

    @Override
    public void impostaCampiGriglia() {
        super.impostaCampiGriglia();
        aggiungiCampoRiga(1, "AUFNR");
        aggiungiCampoRiga(1, "MATNR");
        aggiungiCampoRiga(1, "VEMNG");
        aggiungiCampoRiga(1, "VEMEH");
        aggiungiCampoRiga(1, "SOBKZ");
        aggiungiCampoRiga(1, "SONUM");
        aggiungiCampoRiga(1, "VARKEY");

        aggiungiCampoRiga(2, "MAKTX");

    }

    @Override
    public void aggiungiCampiCalcolati(int ciclo) { }

    @Override
    public String getUrl(int ciclo) {
        String url = "";
        url = Global.serverURL + Global.CASSONI_LEGGI_MATERIALE;

        //Calendar ieri = new GregorianCalendar();
        //ieri.add(Calendar.DATE, -Integer.decode(myIntent.getStringExtra("Giorni")));
        url = url.replaceAll("#LENUM#", myIntent.getStringExtra("HU"));

        Log.d("URL", url);

        return url;
    }


    @Override
    public void bottoneAzione2SingolaRiga (int sel_riga) {
        if (sel_riga!=-1) {
            HashMap<String, String> xx = listaValori.get(sel_riga);
            xx.put("HU", myIntent.getStringExtra("HU"));

            Intent intent = new Intent(this, PrendiParametri.class);
            intent.putExtra("titolo", "5 Impacchettare Materiale");
            intent.putExtra("menuItem", Global.MENU_IMPACCHETTAMENTO_MATERIALE);
            intent.putExtra("listaValori", Global.getCampiImpacchettamento(xx));

            startActivity(intent);

            //openInfoStockMatr(false, listaValori.get(sel_riga));
        } else {
            Global.alert(this, "Riga non selezionata");
        }
    }

    @Override
    public void bottoneAzione3SingolaRiga (int sel_riga) {
        if (sel_riga!=-1) {
            HashMap<String, String> xx = listaValori.get(sel_riga);
            xx.put("HU", myIntent.getStringExtra("HU"));

            Intent intent = new Intent(this, PrendiParametri.class);
            intent.putExtra("titolo", "8 Consuma HU");
            intent.putExtra("menuItem", Global.MENU_CONSUMA_CASSONE);
            intent.putExtra("listaValori", Global.getCampiConsumaCassone(xx));

            startActivity(intent);

        } else {
            Global.alert(this, "Riga non selezionata");
        }
    }

    @Override
    public void bottoneAzione3ClickLong(final View v) {
        AlertDialog dialog = new AlertDialog.Builder(v.getContext())

        .setTitle("CONSUMA HU")
        .setMessage("Sei sicuro di voler procedere TUTTI i cassoni selezionati?")
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                consumaTuttiCassoni(v);
            }
        })
        .setNegativeButton("Annulla", null)
        .create();
        dialog.show();

    }

    public void consumaTuttiCassoni(View v) {
        ArrayList<Integer> selectedRows = getValueOfSelectdRowsMultipleOrSingleFromButton(v);

        // Ciclo tutte le righe selezionate
        for (Integer selectedRow : selectedRows) {
            consumaCassone(selectedRow);
        }

        Global.disabledItem = true;
        onResume();
    }

    public void consumaCassone(int riga) {
        HashMap<String, String> datiCassone = listaValori.get(riga);
        datiCassone.put("HU", myIntent.getStringExtra("HU"));

        String fromCassone = (datiCassone != null ? datiCassone.get("HU") : "");
        String fromOdp = (datiCassone != null ? datiCassone.get("AUFNR") : "");
        String matnr = (datiCassone != null ? datiCassone.get("MATNR") : "");
        String um = (datiCassone != null ? datiCassone.get("VEMEH") : "");
        String qta = (datiCassone != null ? datiCassone.get("VEMNG") : "");
        if (qta.contains(",")) {
            qta = qta.substring(0, qta.lastIndexOf(","));
        }
        /*
        String cassone = getDescrizione("Cassone");
        String odp = getDescrizione("Odp");
        String materiale = getDescrizione("Materiale");
        String qta = getDescrizione("Qta");
        String um = getDescrizione("Unita Misura");
        String cdStockSpe = getDescrizione("Cd Stock Spec.");
        String numStockSpe = getDescrizione("Num Stock Spec.");
        */

        String url = Global.serverURL + Global.CASSONI_WITHDRAWAL;
        url = url.replace("#IV_EXIDV2#", fromCassone);
        url = url.replace("#IV_AUFNR#", fromOdp);
        url = url.replace("#MATNR#", matnr);
        url = url.replace("#VEMNG#", qta);
        url = url.replace("#VEMEH#", um);
        url = url.replace("#SOBKZ#", "");
        url = url.replace("#SONUM#", "");
        url = url.replace("#I_BADGE_USER#", Global.mioBadge);


        Log.d("URL", url);

        LinkedHashMap<String, String> campiTest = new LinkedHashMap<>();
        campiTest.put("MESSAGE", "MESSAGE");
        ArrayList<HashMap<String, String>> listaValoriTemp = Global.getValoriXML(url, campiTest);
    }

    @Override
    public void bottoneAvantiSingolaRiga (int sel_riga) {
        if (sel_riga!=-1) {
            HashMap<String, String> xx = listaValori.get(sel_riga);
            xx.put("HU", myIntent.getStringExtra("HU"));

            Intent intent = new Intent(this, PrendiParametri.class);
            intent.putExtra("titolo", "7 Reimpacchettare Materiale");
            intent.putExtra("menuItem", Global.MENU_REIMPACCHETTAMENTO_MATERIALE);
            intent.putExtra("listaValori", Global.getCampiReimpacchettamento(xx));

            startActivity(intent);
        } else {
            Global.alert(this, "Riga non selezionata");
        }
    }

    @Override
    public void bottoneAvantiClickLong (final View v) {
        final EditText taskEditText = new EditText(v.getContext());
        taskEditText.setPadding(25, 25, 25, 25);
        AlertDialog dialog = new AlertDialog.Builder(v.getContext())

                .setTitle("Sei sicuro di voler procedere per TUTTI gli elementi selezionati?")
                .setMessage("HU")
                .setView(taskEditText)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String hu = String.valueOf(taskEditText.getText()).trim();

                        if (hu == null || "".equals(hu)) {
                            bottoneAvantiClickLong(v);
                        } else {
                            spostamentoMultiploCassoni(v, hu);
                        }
                    }
                })
                .setNegativeButton("Annulla", null)
                .create();
        dialog.show();
    }

    public void spostamentoMultiploCassoni(final View v, String hu) {
        ArrayList<Integer> selectedRows = getValueOfSelectdRowsMultipleOrSingleFromButton(v);

        // Ciclo tutte le righe selezionate
        for (Integer selectedRow : selectedRows) {
            spostamentoCassone(selectedRow, hu);
        }

        Global.disabledItem = true;
        onResume();
    }

    public void spostamentoCassone(Integer row, String huDestination) {
        // Prendo i dati del cassone dalla tabella
        HashMap<String, String> listaValoriRiga = listaValori.get(row);
        listaValoriRiga.put("HU", myIntent.getStringExtra("HU"));

        // Prendo i parametr
        String fromCassone = (listaValoriRiga != null ? listaValoriRiga.get("HU") : "");
        String toCassone = huDestination;
        String fromOdp = (listaValoriRiga != null ? listaValoriRiga.get("AUFNR") : "");
        String matnr = (listaValoriRiga != null ? listaValoriRiga.get("MATNR") : "");
        String um = (listaValoriRiga != null ? listaValoriRiga.get("VEMEH") : "");
        String qta = (listaValoriRiga != null ? listaValoriRiga.get("VEMNG") : "");
        if (qta.contains(",")) {
            qta = qta.substring(0, qta.lastIndexOf(","));
        }

        String errorText = Global.checkHuAndOdpConsistency(toCassone, fromOdp);

        if (errorText != null) {
             Global.alert(getApplicationContext(), errorText);
        } else {
            // Costruisco l'URL
            String url = Global.serverURL + Global.CASSONI_SPOSTAMENTO_MATERIALE;
            url = url.replace("#IV_EXIDV2_O#", fromCassone);
            url = url.replace("#IV_EXIDV2_D#", toCassone);
            url = url.replace("#IV_AUFNR_O#", fromOdp);
            url = url.replace("#IV_AUFNR_D#", fromOdp);
            url = url.replace("#MATNR#", matnr);
            url = url.replace("#VEMNG#", qta);
            url = url.replace("#VEMEH#", um);
            url = url.replace("#SOBKZ#", "");
            url = url.replace("#SONUM#", "");
            url = url.replace("#I_BADGE_USER#", Global.mioBadge);


            Log.d("URL", url);

            LinkedHashMap<String, String> campiTest = new LinkedHashMap<>();
            campiTest.put("MSGTX", "MSGTX");

            ArrayList<HashMap<String, String>> listaValoriTemp = Global.getValoriXML(url, campiTest);
        }
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

        //new BackgroundTask().execute();
    }
}
