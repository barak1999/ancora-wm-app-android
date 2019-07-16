package com.ancoragroup.wm;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ListaPrelItem extends  Griglia {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bottoneAzione1.setText("");
        bottoneAzione1.setBackgroundResource(R.drawable.documenta);
        bottoneAzione2.setText("Info\nOdp");
        bottoneAzione3.setText("Info\nStock");
        bottoneAvanti.setText("Prel.");
    }
    @Override
    public void impostaTuttiCampi() {
        super.impostaTuttiCampi();

        setCampo("REFNT");
        setCampo("VBELN");
        setCampo("REIHF");
        setCampo("VLTYP");
        setCampo("VLPLA");
        setCampo("NLTYP");
        setCampo("NLPLA");
        setCampo("VSOLM");
        setCampo("MATNR");
        setCampo("VSOLM_C", true, Color.BLUE);
        setCampo("ALTME", Global.getLabel("ALTME"), true);
        setCampo("MAKTX");
        setCampo("TANUM");
        setCampo("TAPOS");
        setCampo("GESME");
        setCampo("Lista", "Lista", 4, true,true);

        setCampo("REFNR", true);
        setCampo("VBELN", true);

        setCampo("DTIP");
        setCampo("DUBIC");
        setCampo("BKTXT");

    }

    @Override
    public void impostaCampiGriglia() {
        super.impostaCampiGriglia();

        aggiungiCampoRiga(1, "VLTYP");
        aggiungiCampoRiga(1, "VLPLA");
        aggiungiCampoRiga(1, "VSOLM_C");
        aggiungiCampoRiga(1, "MATNR");
        aggiungiCampoRiga(1, "MAKTX");


        aggiungiCampoRiga(2, "ALTME");
        aggiungiCampoRiga(2, "DTIP");
        aggiungiCampoRiga(2, "DUBIC");
        aggiungiCampoRiga(2, "GESME");
        if (menuItem.equals(Global.LISTA_PREL_PROD)) {
            aggiungiCampoRiga(2, "REFNR");
        } else {
            aggiungiCampoRiga(2, "VBELN");
        }
        aggiungiCampoRiga(2, "Lista");
        aggiungiCampoRiga(2, "Modifica");
        aggiungiCampoRiga(3, "BKTXT");


    }

    @Override
    public int getCicli() {
        String[] values;

        if (menuItem.equals(Global.LISTA_PREL_PROD)) {
            values = myIntent.getStringExtra("REFNRS").split("\\|");
        } else {
            values = myIntent.getStringExtra("VBELNS").split("\\|");
        }
        Log.d("URLListaPrel", "Tot Cicli:" + values.length);

        return values.length * 2;

    }

    @Override
    public String getUrl(int i) {
        String url = "";
        if (menuItem.equals(Global.LISTA_PREL_PROD)) {
            url = Global.serverURL + Global.listaPrelProdItemXML;
            url = url.replaceAll("#I_OBJTYPE#", "G");
            String temp = myIntent.getStringExtra("REFNRS").split("\\|")[i / 2];
            if (temp==null || temp.equals("")) temp = "-1";
            url = url.replaceAll("#I_REFNR#", temp);
        } else  {
            url = Global.serverURL + Global.listaPrelVenditaItemXML;
            url = url.replaceAll("#I_OBJTYPE#", "C");
            String temp = myIntent.getStringExtra("VBELNS").split("\\|")[i / 2];
            if (temp==null || temp.equals("")) temp = "-1";
            url = url.replaceAll("#I_REFNR#", temp);
        }

        url = url.replaceAll("#I_LGNUM#",Global.getImpostazoniSAP("Numero Magazzino"));
        url = url.replaceAll("#I_WORKSTATION#", (i%2) == 0 ? Global.getImpostazoniSAP("Societa") : "9999");
        url = url.replaceAll("#I_PAGNO#","");

        url = url.replaceAll("#I_KQUIT#","");
        url = url.replaceAll("#I_PQUIT#","");
        Log.d("URLListaPrel", url);

        return url;
    }

    @Override
    public void  aggiungiCampiCalcolati(int ciclo) {
        String[] values = myIntent.getStringExtra("Destinazione").split("\\|");
        for (int t = 0; t < listaValoriTemp.size(); t++) {
            HashMap<String, String> item = listaValoriTemp.get(t);
            if (item.get("Destinazione") == null) {
                String tdest = values[ciclo / 2];
                item.put("Destinazione", tdest);
                if (tdest.length() > 3) {
                    item.put("DTIP", tdest.substring(0, 3));
                    item.put("DUBIC", tdest.substring(3));
                }
            }
        }
        Log.d("URLListaPrel", "LISTA PREL: " + listaValoriTemp.size());



        if (menuItem.equals(Global.LISTA_PREL_PROD)) {
            values = myIntent.getStringExtra("REFNRS").split("\\|");
            for (int t = 0; t < listaValoriTemp.size(); t++) {
                HashMap<String, String> item = listaValoriTemp.get(t);
                if (item.get("REFNR") == null) {
                    item.put("REFNR", values[ciclo / 2]);
                }
            }
        } else {
            values = myIntent.getStringExtra("VBELNS").split("\\|");
            for (int t = 0; t < listaValoriTemp.size(); t++) {
                HashMap<String, String> item = listaValoriTemp.get(t);
                if (item.get("VBELN") == null) {
                    item.put("VBELN", values[ciclo / 2]);
                }
            }
        }

        values = myIntent.getStringExtra("REFNTS").split("\\|");
        for (int t = 0; t < listaValoriTemp.size(); t++) {
            HashMap<String, String> item = listaValoriTemp.get(t);
            if (item.get("Lista") == null) {
                item.put("Lista", values[ciclo / 2]);
            }
        }


        if(ciclo % 2 == 0){
            for (int t = 0; t < listaValoriTemp.size(); t++) {
                HashMap<String, String> item = listaValoriTemp.get(t);
                item.put("Modifica", Global.getImpostazoniSAP("Societa"));

            }
        }

        if(ciclo % 2 == 1){
            for (int t = 0; t < listaValoriTemp.size(); t++) {
                HashMap<String, String> item = listaValoriTemp.get(t);
                if(item.get("VLPLA").equals("MODULA")) {
                    item.put("Modifica", "9999");
                }
            }
        }

        Log.d("URLListaPrel", "Cicli: " + ciclo);


    }


    @Override
    public void bottoneAzione2Click (View v) {
        int sel_riga = -1;
        for (Object o :  valoriSelezionati.keySet()) {
            sel_riga = ((Integer) o).intValue() / RIGHE;
        }

        if (sel_riga!=-1) {
            openInfoStockMatr(false, listaValori.get(sel_riga));
        } else {
            Global.alert(this, "Riga non selezionata");
        }
    }

    @Override
    public void bottoneAzione3Click (View v) {
        int sel_riga = -1;
        for (Object o :  valoriSelezionati.keySet()) {
            sel_riga = ((Integer) o).intValue() / RIGHE;
        }

        if (sel_riga!=-1) {
            openInfoStockMatr(true, listaValori.get(sel_riga));
        } else {
            Global.alert(this, "Riga non selezionata");
        }
    }


    @Override
    public void bottoneAzione2ClickLong(View v) {
        int sel_riga = getSelectedRowFromTable();

        if (sel_riga!=-1) {
            openLT24(true, listaValori.get(sel_riga));
        } else {
            Global.alert(this, "Riga non selezionata");
        }
    }

    @Override
    public void bottoneAzione3ClickLong(View v) {
        int sel_riga = getSelectedRowFromTable();

        if (sel_riga!=-1) {
            openLT24(true, listaValori.get(sel_riga), 365);
        } else {
            Global.alert(this, "Riga non selezionata");
        }
    }

    @Override
    public void bottoneAvantiSingolaRiga (int sel_riga) {
        Intent intent = new Intent(this, PrendiParametri.class);
        intent.putExtra("menuItem", myIntent.getStringExtra("menuItem") + "PASSO2");
        if (menuItem.equals(Global.LISTA_PREL_PROD)) {
            intent.putExtra("titolo", myIntent.getStringExtra("titolo"));
            intent.putExtra("REFNR", listaValori.get(sel_riga).get("REFNR"));
            intent.putExtra("VBELN", "");
        } else {
            intent.putExtra("titolo", myIntent.getStringExtra("titolo"));
            intent.putExtra("REFNR", "");
            intent.putExtra("VBELN", listaValori.get(sel_riga).get("VBELN"));
        }

        intent.putExtra("VLTYP", listaValori.get(sel_riga).get("VLTYP"));
        intent.putExtra("VLPLA", listaValori.get(sel_riga).get("VLPLA"));
        intent.putExtra("VSOLM_C", listaValori.get(sel_riga).get("VSOLM_C"));

        ArrayList<HashMap<String, String>> listaValoriNext = new ArrayList<>();
        listaValoriNext.add(Global.putListaValori("Da Ubic",listaValori.get(sel_riga).get("VLTYP") + listaValori.get(sel_riga).get("VLPLA"),"READONLY"));
        //listaValoriNext.add(Global.putListaValori("A Ubic",myIntent.getStringExtra("Destinazione"),"READONLY"));
        String odp = "";
        String ubic = listaValori.get(sel_riga).get("DUBIC");
        if(listaValori.get(sel_riga).get("DUBIC").startsWith("0")) {
            odp = listaValori.get(sel_riga).get("DUBIC").substring(1);
        }
        intent.putExtra("ODP", odp);
        intent.putExtra("Ubic", ubic);
        //listaValoriNext.add(Global.putListaValori("ODPNR",listaValori.get(sel_riga).get("AUFNR"),"HIDDEN"));
        listaValoriNext.add(Global.putListaValori("A Ubic",listaValori.get(sel_riga).get("Destinazione"),"READONLY"));
        listaValoriNext.add(Global.putListaValori("Materiale",listaValori.get(sel_riga).get("MATNR"),"READONLY"));
        listaValoriNext.add(Global.putListaValori("Descrizione",listaValori.get(sel_riga).get("MAKTX"),"READONLY"));
        listaValoriNext.add(Global.putListaValori("Stock - QtaPrel",listaValori.get(sel_riga).get("GESME") + " - " +  listaValori.get(sel_riga).get("VSOLM_C"),"READONLY"));
        if (menuItem.equals(Global.LISTA_PREL_PROD)) {
            listaValoriNext.add(Global.putListaValori("Quantita1", listaValori.get(sel_riga).get("VSOLM_C"), "NUM"));
            listaValoriNext.add(Global.putListaValori("HU1", "", ""));
            listaValoriNext.add(Global.putListaValori("Quantita2", "", "NUM"));
            listaValoriNext.add(Global.putListaValori("HU2", "", ""));
            listaValoriNext.add(Global.putListaValori("Quantita3", "", "NUM"));
            listaValoriNext.add(Global.putListaValori("HU3", "", ""));

        } else if (menuItem.equals(Global.LISTA_PREL_VEND)) {

            listaValoriNext.add(Global.putListaValori("Quantita1", listaValori.get(sel_riga).get("VSOLM_C"), "NUM"));


        }
        listaValoriNext.add(Global.putListaValori("Da Confermare", "NO;NO;SI", "BOOL", true));
        listaValoriNext.add(Global.putListaValori("Rettifica", "!F;!F;NO;SI", "RADIO"));

        intent.putExtra("listaValori", listaValoriNext );
        startActivity(intent);

    }

    @Override
    public void bottoneAvantiClickLong (View v) {

        final EditText taskEditText = new EditText(v.getContext());
        taskEditText.setPadding(25, 25, 25, 25);
        AlertDialog dialog = new AlertDialog.Builder(v.getContext())

                .setTitle("Sei sicuro di voler procedere per TUTTI gli elementi selezionati?")
                .setMessage("HU")
                .setView(taskEditText)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String task = String.valueOf(taskEditText.getText());
                        faiTutti(task);
                    }
                })
                .setNegativeButton("Annulla", null)
                .create();
        dialog.show();
    }

    public void faiTutti(String cassone) {
        //Global.alert(getBaseContext(), cassone);

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
                            if (Global.mustCassoni(listaValori.get(sel_riga).get("Destinazione")) && cassone.equalsIgnoreCase("")) {
                                Global.alert(getBaseContext(), "HU obbligatorio " + listaValori.get(sel_riga).get("MATNR"));
                                cb.setChecked(false);
                            } else {

                                String url = Global.serverURL + Global.listaPrelConfirmXML;
                                url = url.replaceAll("#I_MODE#", Global.I_MODE);
                                url = url.replaceAll("#I_UPDATE#", Global.I_UPDATE);
                                url = url.replaceAll("#I_BADGE_USER#", Global.mioBadge);
                                url = url.replaceAll("#I_LGNUM#", Global.getImpostazoniSAP("Numero Magazzino"));
                                url = url.replaceAll("#I_QTA_CONF#", listaValori.get(sel_riga).get("VSOLM_C"));
                                url = url.replaceAll("#I_CNFTYPE#", "2");   //1 == ?  2 === ?
                                if (menuItem.equals(Global.LISTA_PREL_VEND)) {
                                    url = url.replaceAll("#I_OBJTYPE#", "C");   //C == Vendita
                                    url = url.replaceAll("#I_REFNR#", listaValori.get(sel_riga).get("VBELN"));
                                } else {
                                    url = url.replaceAll("#I_OBJTYPE#", "G");   //G == Prod
                                    url = url.replaceAll("#I_REFNR#", listaValori.get(sel_riga).get("REFNR"));
                                }

                                url = url.replaceAll("#I_RETT#", "");
                                url = url.replaceAll("#I_TANUM#", "");
                                url = url.replaceAll("#I_TAPOS#", "");
                                url = url.replaceAll("#I_MATNR#", listaValori.get(sel_riga).get("MATNR"));
                                url = url.replaceAll("#I_VLTYP#", listaValori.get(sel_riga).get("VLTYP"));
                                url = url.replaceAll("#I_VLPLA#", listaValori.get(sel_riga).get("VLPLA"));
                                url = url.replaceAll("#I_NLTYP#", "");
                                url = url.replaceAll("#I_NLPLA#", "");
                                url = url.replaceAll("#I_EXIDV2#", cassone);
                                url = url.replaceAll("#I_NLENR#", "");

                                url = url.replaceAll("#I_TIPO#", "");
                                url = url.replaceAll("#I_BADGE_USER#", Global.mioBadge);
                                url = url.replaceAll("#I_AUFNR#", "");
                                url = url.replaceAll("#T_SERNR#", "");

                                Log.d("URL", url);
                                //intent.putExtra("url", url);


                                //startActivity(intent);
                                LinkedHashMap<String, String> campiTest = new LinkedHashMap<>();
                                campiTest.put("MSGTX", "MSGTX");
                                ArrayList<HashMap<String, String>> listaValoriTemp = Global.getValoriXML(url, campiTest);


                            }
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
