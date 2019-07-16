package com.ancoragroup.wm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.barcode.Scanner;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends OmarActivity {
    EditText editBadge;
    public static final String PREFS_NAME = "WM.ANCORA.impostazioniAPP";
    public static final String COPIED_VALUES_NAME = "WM.ANCORA.copiedValues";
    ToggleButton server;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // inzializza le impostazioni iniziali
        // quando si ritorna a questa pagina vengono sempre azzerate
        Global.impostazioniApp = getSharedPreferences(PREFS_NAME, 0);
        Global.preferencesCopyBtnSavedValues = getSharedPreferences(COPIED_VALUES_NAME, 0);

        // Carico le traduzioni
        Global.loadTranslations();

        // Per compatibilità, rimuovo l'RFID dalle impostazioni APP (ora vengono supportanti entrambi i modelli)
        Global.removeImpostazioniApp("RFID");
        //if (Global.isUnSet("RFID")) Global.setImpostazioniApp("RFID", "NO");

        if (Global.isUnSet("SELEZIONA")) Global.setImpostazioniApp("SELEZIONA", "SI");
        if (Global.isUnSet("CARDINALE")) Global.setImpostazioniApp("CARDINALE", "NO");
        if (Global.isUnSet("SCANNER")) Global.setImpostazioniApp("SCANNER", "NO");
        if (Global.isUnSet("INVERTI SELEZIONE")) Global.setImpostazioniApp("INVERTI SELEZIONE", "SI");
        if (Global.isUnSet("CARATTERI GRANDI")) Global.setImpostazioniApp("CARATTERI GRANDI", "NO");
        if (Global.isUnSet("SAMSUNG")) Global.setImpostazioniApp("SAMSUNG", "NO");
        if (Global.isUnSet("CHROME")) Global.setImpostazioniApp("CHROME", "NO");
        if (Global.isUnSet("TASTIERA NUM")) Global.setImpostazioniApp("TASTIERA NUM", "NO");


        Global.setBold();
        Global.setCassoni();
        Global.setAree();
        Global.setLabels();
        Global.mioBadge = "";
        Global.impostazioniSAP.clear();
        Global.getDPI(getResources().getDisplayMetrics().density);

        // imposta il layout xml corretto se diverso dallo standard
        setLayoutXML(R.layout.activity_main);

        // richiama solo ora la superclasse perchè deve sapere su quali valori lavorare
        super.onCreate(savedInstanceState);

        // titolo della pagina
        titolo = "LOGIN";

        // imposta la gestione dell'invio della tastiera
        editBadge =  (EditText) findViewById(R.id.editBadge);
        editBadge.setSingleLine(true);
        editBadge.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editBadge.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    start(v);
                    handled = true;
                }
                return handled;
            }
        });


        // se impostato l'autologin legge il badge dalle impostazioni dell'app
        if (Global.autoLogin) {
            if (Global.impostazioniApp.getString("myBadge", null) != null) {
                Global.mioBadge = Global.impostazioniApp.getString("myBadge", null);
                editBadge.setText(Global.mioBadge);
                next();
            }
        }

        server = (ToggleButton) findViewById(R.id.server);
        server.setTextOff("SVILUPPO");
        server.setTextOn("PRODUZIONE");
        server.setBackgroundResource(R.drawable.togglebutton_selector);
        if (Global.typeRun == Global.PRODUZIONE) {
            server.setText("PRODUZIONE");
            server.setChecked(true);
        } else {
            server.setText("SVILUPPO");
            server.setChecked(false);
        }

    }

    @Override
    public void onResume() {
        Global.mioBadge = "";
        editBadge.setText(Global.mioBadge);
        Global.impostazioniSAP.clear();
        super.onResume();

    }

    // disabilita il menu impostazioni
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.item_settings_sap);
        item.setVisible(false);
        return true;
    }

    public void start(View v)
    {
        if (server.getText().toString().equals("SVILUPPO"))  {
            Global.serverURL = Global.serverDEVURL;
            Global.typeRun = Global.SVILUPPO;
            updateToolBar();
            Log.d("URL", "SVILUPPO");
        } else {
            Global.serverURL = Global.serverPRODURL;
            Global.typeRun = Global.PRODUZIONE;
            updateToolBar();
            Log.d("URL", "PRODUZIONE");
        }

        String badge = editBadge.getText().toString();

        if (!"".equals(badge.trim())) {
            next();
        } else {
            Global.alert(MainActivity.this, "Badge errato");
        }
    }

    // esegue il il task asincrono per leggere i dati da SAP
    public void next()
    {
        new BackgroundTask(editBadge.getText().toString()).execute();
    }

    private class BackgroundTask extends AsyncTask<Void, Integer, String>
    {
        String error = null;
        String myBadge = "";

        public BackgroundTask(String _myBadge) {
            myBadge = _myBadge;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setProgress(0);
            progress.show();
        }

        @Override
        protected String doInBackground(Void... arg0)
        {
            try {

                ///// PRENDO LA DATA DA SAP
                String data = "";
                LinkedHashMap<String, String> campi = new LinkedHashMap<String, String>();
                campi.put("E_DATE","E_DATE");
                ArrayList<HashMap<String, String>> valori = Global.getValoriXML(Global.serverURL + Global.leggiDataXML, campi);
                if (valori!=null) data = valori.get(0).get("E_DATE");
                if (data!=null) {
                    try {
                        data = Global.norDATE.format(Global.extDATE.parse(data));
                        Global.dataSAP = data;
                    } catch (Exception e) {
                        // se c'è errore non faccio nulla
                    }
                }

                Log.d("URL:", Global.dataSAP);
                /////

                String url = Global.serverURL + Global.loginXML + myBadge;

                Log.d("URL:", url);

                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(2 * 60 * 1000);
                connection.setReadTimeout(2 * 60 * 1000);

                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(connection.getInputStream());


                NodeList listTavole = doc.getElementsByTagName("XmlRow");
                progress.setMax(listTavole.getLength());


                for (int temp = 0; temp < listTavole.getLength(); temp++) {
                    publishProgress(temp);
                    Node nNode = listTavole.item(temp);
                    HashMap<String,String> hashMap;
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;


                        Global.impostazioniSAP.add(putListaValori("Nome",eElement.getElementsByTagName("NAME").item(0).getTextContent(),"READONLY"));
                        Global.impostazioniSAP.add(putListaValori("Cognome",eElement.getElementsByTagName("SNAME").item(0).getTextContent()));
                        Global.impostazioniSAP.add(putListaValori("Societa",eElement.getElementsByTagName("BUKRS").item(0).getTextContent()));
                        // D110
                        Global.impostazioniSAP.add(putListaValori("Divisione",eElement.getElementsByTagName("WERKS").item(0).getTextContent()));
                        // 110
                        Global.impostazioniSAP.add(putListaValori("Numero Magazzino",eElement.getElementsByTagName("LGNUM").item(0).getTextContent()));
                        // W110
                        Global.impostazioniSAP.add(putListaValori("Magazzino",eElement.getElementsByTagName("LGORT").item(0).getTextContent()));
                        Global.impostazioniSAP.add(putListaValori("Stampante",eElement.getElementsByTagName("PDEST").item(0).getTextContent()));
                        Global.impostazioniSAP.add(putListaValori("Menu",eElement.getElementsByTagName("MENU").item(0).getTextContent()));
                        Global.setImpostazioniSAP("Stampante","MAIL");
                        progress.setProgress(temp+1);
                    }
                }

            } catch (Exception e) {
                Log.e("LOAD SAP SETTINGS: ", e.getMessage());
                error = e.getMessage();
            }


            return String.valueOf(Global.impostazioniSAP.size());
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
                Global.alert(MainActivity.this, "Badge errato");

            } else {
                // se il badge è corretto lo imposta a livello globale ed entra nel menu
                Global.mioBadge = myBadge;
                if (myBadge.equals("99999")) {
                    server.setChecked(false);
                    Global.serverURL = Global.serverDEVURL;
                    Global.typeRun = Global.SVILUPPO;
                    updateToolBar();
                    Log.d("URL", "SVILUPPO");
                }

                // Resetto il menu al login
                Global.menuList = new HashMap<>();
                Global.loadMenu(Global.getImpostazoniSAP("Menu"));
                Intent intent = new Intent(MainActivity.this, SapMenu.class);
                intent.putExtra("menu",Global.getImpostazoniSAP("Menu"));
                intent.putExtra("titolo","Menu principale");
                startActivity(intent);
                progress.dismiss();
            }
        }
    }



}
