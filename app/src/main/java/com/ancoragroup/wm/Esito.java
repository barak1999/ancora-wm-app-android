package com.ancoragroup.wm;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.lang.invoke.MethodHandles;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Esito extends OmarActivity {

    String url = "";
    ArrayList<String> multiUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        url = myIntent.getStringExtra("url");
        multiUrl = myIntent.getStringArrayListExtra("multiUrl");
        if (myIntent.getStringExtra("matnr")!=null) bottoneAzione3.setText("Info\nStock");

        bottoneAvanti.setVisibility(View.INVISIBLE);

        new BackgroundTask().execute();

    }


    private long lastClickTime = 0;
    @Override
    public void bottoneAvantiClick (View v) {
        // se il badge è corretto lo imposta a livello globale ed entra nel menu
        // Evito il doppio TAP
        if (Global.BUTTON_WAIT_FEATURE) {
            if (SystemClock.elapsedRealtime() - lastClickTime < Global.BUTTON_CLICK_MS_WAIT) {
                return;
            }

            Log.d("CLIC", "Click " + lastClickTime);

            lastClickTime = SystemClock.elapsedRealtime();
        }
        try {
            Global.backMenu = true;
            Global.backMenuLoop = Integer.parseInt(myIntent.getStringExtra("backMenuLoop"));
            Global.disabledItem = true;
        } catch ( Exception e) {
            Global.backMenu = false;
        }
        customBackPressed();
    }

    public String getUrl() {
     return  url;
    }

    public ArrayList<String> getMultiUrl() {
        if (multiUrl==null) {
            multiUrl = new ArrayList<>();
            multiUrl.add(getUrl());
        }
        return  multiUrl;
    }

    private class BackgroundTask extends AsyncTask<Void, Integer, String>
    {
        String error = null;
        String type = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setProgress(0);
            progress.show();
        }


        @Override
        protected String doInBackground(Void... arg0)
        {
            HashMap<String, String> paramToShowInError = new HashMap<>();

            Object objParamsToShow = myIntent.getSerializableExtra("paramToShowInError");
            if (objParamsToShow != null) {
                paramToShowInError = (HashMap<String, String>)objParamsToShow;
            }

            String result = "";
            ArrayList<String> urls = getMultiUrl();
            for (String url : urls) {
                Log.d("URL", url);
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(2 * 60 * 1000);
                    connection.setReadTimeout(2 * 60 * 1000);

                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                    Document doc = dBuilder.parse(connection.getInputStream());

                    type = doc.getElementsByTagName("TYPE").item(0).getTextContent();

                    if (listaValori == null) listaValori = new ArrayList<>();

                    NodeList message = doc.getElementsByTagName("MESSAGE");
                    HashMap<String, String> hashMap;
                    hashMap = new HashMap<>();

                    String titolo = message.item(0).getTextContent();

                    for (String paramName : paramToShowInError.keySet()) {
                        String description = paramToShowInError.get(paramName);

                        String value = extractParam(url, paramName);

                        titolo += " - " + description + " " + value;

                    }

                    hashMap.put("Titolo", titolo);
                    hashMap.put("Descrizione", "");
                    hashMap.put("Tipo", type);
                    listaValori.add(hashMap);
                    //error = message.item(0).getTextContent();
                    // se ci sono errori recuper delle info
                    NodeList listTavole = doc.getElementsByTagName("XmlRow");
                    progress.setMax(listTavole.getLength());

                    for (int temp = 0; temp < listTavole.getLength(); temp++) {
                        Log.i("--->", String.valueOf(temp));
                        publishProgress(temp);
                        Node nNode = listTavole.item(temp);

                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element eElement = (Element) nNode;
                            hashMap = new HashMap<>();
                            String title = "";

                            Log.d("NODO NOME", eElement.getNodeName());
                            Log.d("NODO VALORE", eElement.getNodeValue() != null ? eElement.getNodeValue() : "");

                            Node nTmp = eElement.getElementsByTagName("MESSAGE").item(0);
                            Node nMsgText = eElement.getElementsByTagName("MSGTX").item(0);
                            if (nTmp != null) {
                                title = nTmp.getTextContent();
                            } else if (nMsgText != null) {
                                title = nMsgText.getTextContent();
                            } else {
                                // ATTENZIONE, introdotto dopo le modifiche ai cassoni.
                                // Il problema attualmente è che la risposta XML è uguale in tutte le chiamate App a parte nelle nuove chiamate cassoni
                                // Quindi con le nuove chiamate andava in eccezione perchè i nodi message e msgtex erano NULL
                                // Così facendo salta il nodo corrente e prova in qello dopo. Così facendo funziona.
                                // Avrebbe più senso uniformare il tutto?
                                continue;
                            }
                            hashMap.put("Titolo", title);
                            hashMap.put("Descrizione", "");
                            hashMap.put("Tipo", type);
                            listaValori.add(hashMap);
                            progress.setProgress(temp + 1);
                        }
                    }

                } catch(Exception e){
                    error = e.getMessage();
                }
            }

            return result;
        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            super.onProgressUpdate(values);
            progress.setProgress(values[0].intValue());
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);


            if (error!=null) {
                progress.dismiss();
                Global.alert(Esito.this, error);
                customBackPressed();
            } else {

                if (type.equals("I") || type.equals("S")) {
                    bottoneAvanti.setVisibility(View.VISIBLE);
                    bottoneIndietro.setVisibility(View.INVISIBLE);

                    // Se provengo dalle liste di vendita o produzione, eseguo il PACKING del materiale, solo se specificato il cassone
                    /*
                    if (menuItem.equals(Global.LISTA_PREL_PROD + "PASSO2") || (menuItem.equals(Global.LISTA_PREL_VEND + "PASSO2"))){
                        String cassone = getDescrizione("Cassone");
                        if (cassone != null && !"".equals(cassone)) {
                            String odp = myIntent.getStringExtra("ODPNR");
                            String materiale = getDescrizione("Materiale");
                            String quantita = getDescrizione("Quantita"); // Qta

                            String url = Global.serverURL + Global.CASSONI_IMPACCHETTA_MATERIALE;
                            url = url.replace("#IV_EXIDV2#", cassone);
                            url = url.replace("#IV_AUFNR#", odp);
                            url = url.replace("#MATNR#", materiale);
                            url = url.replace("#VEMNG#", quantita);
                            url = url.replace("#VEMEH#", "");
                            url = url.replace("#SOBKZ#", "");
                            url = url.replace("#SONUM#", "");

                            Intent intent = new Intent(Esito.this, Esito.class);
                            intent.putExtra("titolo", "Esito Impacchettare Materiale");
                            intent.putExtra("info2", "Esito");
                            intent.putExtra("backMenuLoop", "1");
                            Log.d("URL", url);
                            intent.putExtra("url", url);

                        }
                    }
                    */
                }
                String[] daS = {"Titolo"};//string array
                int[] aS = {R.id.titolo};//int array of views id's
                OmarAdapter simpleAdapter = new OmarAdapter(Esito.this, listaValori, R.layout.omar_riga_esito, daS, aS);//Create object and set the parameters for simpleAdapter
                grigliaViste.setAdapter(simpleAdapter);//sets the adapter for listView

                progress.dismiss();
            }
        }
    }

    @Override
    public void bottoneAzione3Click (View v) {
        openInfoStock(true, myIntent.getStringExtra("matnr"));
    }

    public String extractParam(String url, String paramName) {
        Uri uri = Uri.parse(url);
        return uri.getQueryParameter(paramName);
    }


}