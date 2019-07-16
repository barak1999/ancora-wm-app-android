package com.ancoragroup.wm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.barcode.Scanner;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.StrictMode;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ancoragroup.uhf.Util;
import com.ancoragroup.wm.Global.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import android_serialport_api.SerialPort;


import com.handheld.UHF.UhfManager;
import cn.pda.serialport.Tools;
import com.ancoragroup.uhf.EPC;
import com.olc.uhf.UhfAdapter;
import com.olc.uhf.tech.IUhfCallback;

public class PrendiParametri extends OmarActivity {
    String menuItem = "";
    ImageView anteprima ;
    protected Application mApplication;
    protected SerialPort mSerialPort;
    protected OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;

    EditText primo;


    ///////  GESTIONE SCANNER RPA520 ///////////////
    private Handler mHandler = new MainHandler();

    private Manufactorer manufactorerType = null;

    ////// GESTIONE RFID VH-71T////////
    private UhfManager manager;
    private ArrayList<String> listepc = new ArrayList<String>();
    private ArrayList<EPC> listEPC;
    boolean runFlag = false;
    boolean startFlag = false;
    Thread thread;

    ////// GESTIONE RFID RPA-520////////
    private com.olc.uhf.UhfManager managerVh;
    private com.olc.uhf.tech.ISO1800_6C uhf_6c;
    int allcount = 0;
    Thread threadRpa520;

    boolean noRFID = false;
    OmarAdapter omarAdapter;

    /**
     * @author wyt
     */
    private class MainHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Scanner.BARCODE_READ: {
                    //Global.alert( OmarActivity.this, (String) msg.obj);
                    EditText tv = getPrimo();
                    if (tv != null) {
                        tv.append((String) msg.obj);
                        String temp = (String) msg.obj;
                        Log.d("BARCODEDETECTED temp", temp);
                        if (temp != null) {
                            Global.toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                            bottoneAvantiClick(tv);
                        }
                    }
                    break;
                }
                case Scanner.BARCODE_NOREAD: {
                    break;
                }
                default:
                    break;
            }
        }
    };

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("KEY", String.valueOf(keyCode));
        if (event.getRepeatCount() == 0) {
            if (keyCode == 4){// 返回键
                finish();
            } else if ((keyCode == 220) | (keyCode == 211) | (keyCode == 212)
                    | (keyCode == 221)){// 扫描键

                // 扫描开始
                Scanner.Read();
            }
        }
        return true;
    }


    /**
     *
     */
    protected void onStart() {
        // TODO Auto-generated method stub
        if (Global.getImpostazioniApp("SCANNER").equals("SI")) {
            Scanner.m_handler = mHandler;
            Scanner.InitSCA();
        }
            super.onStart();
    }

    //////////////////////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (myIntent.getStringExtra("matnr")!=null) bottoneAzione3.setText("Info\nStock");

        menuItem = myIntent.getStringExtra("menuItem");
        Log.d("menuItem", menuItem);

        // permette di tenere visibile il campo in edit
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        listaValori = (ArrayList<HashMap<String, String>>) myIntent.getSerializableExtra("listaValori");

        grigliaViste = (GridView) findViewById(R.id.listview);

        if (menuItem.equals(Global.MANCANTI_DA_ZMAG + "PASSO2") || menuItem.equals(Global.LISTA_PREL_PROD + "PASSO2")){

            omarAdapter = new OmarAdapter(this, listaValori, R.layout.omar_riga_modifica, da, a, true);//Create object and set the parameters for simpleAdapter

        } else {

            omarAdapter = new OmarAdapter(this, listaValori, R.layout.omar_riga_modifica, da, a);//Create object and set the parameters for simpleAdapter
            //NuovoAdapter omarAdapter = new NuovoAdapter(this, listaValori, R.layout.omar_riga_modifica,da,a);
        }
        grigliaViste.setAdapter(omarAdapter);//sets the adapter for listView

        anteprima = (ImageView) findViewById(R.id.anteprima);


        if (Global.getImpostazioniApp("BARCODE1D").equals("Yes")) {
                mApplication = (Application) getApplication();
            try {
                mSerialPort = mApplication.getSerialPort();
                mOutputStream = mSerialPort.getOutputStream();
                mInputStream = mSerialPort.getInputStream();

                /* Create a receiving thread */
                mReadThread = new ReadThread();
                mReadThread.start();
            } catch (SecurityException e) {
                DisplayError("ERRORE DI SICUREZZA");
            } catch (IOException e) {
                DisplayError("ERRORE SCONOSCIUTO");
            } catch (InvalidParameterException e) {
                DisplayError("ERRORE DI CONFIGURAIZONE");
            }
       }


       ///// RFID //////
        // Ricavo il tipo di modello dal nome
        manufactorerType = Manufactorer.getByName(Build.MODEL);

        listEPC = new ArrayList<EPC>();
        //if (Global.getImpostazioniApp("RFID").equals("SI")) {
            // Instanzio il manager a secoda del tipo di dispositivo
            Util.initSoundPool(this);

            switch (manufactorerType) {
                case VH71T: {
                    try {
                        manager = UhfManager.getInstance();
                    } catch (Exception e) {
                        Log.e("ERRORE",e.getMessage());
                    }
                    break;
                }
                case RPA520: {
                    try {
                        managerVh = UhfAdapter.getUhfManager(this.getApplicationContext());
                        if (managerVh != null) {
                            managerVh.open();
                            uhf_6c = (com.olc.uhf.tech.ISO1800_6C) managerVh.getISO1800_6C();
                        }
                    } catch (Exception e) {
                        //Toast.makeText(App.this, "dasd", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
            }


        //}
    }

    private void DisplayError(String label) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Error");
        b.setMessage(label);
        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                PrendiParametri.this.finish();
            }
        });
        b.show();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        if (hasFocus) {
            if (refresh) {
                // sull'ultimo capo l'invio corrisponde all'avanti
                // attenzione che se non viene visualizzato l'ultimo campo la lista dei child è diversa da quella completa
                //if (grigliaViste.getChildCount()>= (listaValori.size() - 1)) {
                View ultimo = grigliaViste.getChildAt(listaValori.size() - 1);
                if (ultimo != null) {
                    EditText temp = (EditText) ultimo.findViewById(R.id.descrizione);

                    if (temp != null) {
                        temp.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                            @Override
                            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                boolean handled = false;
                                if (actionId == EditorInfo.IME_ACTION_DONE) {
                                    bottoneAvantiClick(v);
                                    handled = true;
                                }
                                return handled;
                            }
                        });
                    }
                }
                // }


                boolean allReadOnly = false;
                // sul primo mette il focus sul primo campo non READONLY
                int first = 0;
                try {
                    while (listaValori.get(first).get("Tipo").equals("READONLY"))
                        first++;
                } catch (Exception e) {

                }

                // Sono tutti campi readonly se first (che è un campo di testo) è uguale alla grandezza della listavalori
                allReadOnly = (first == listaValori.size());

                //  Sposta il campo attivo su ubizione - NON BELLO perchè pensa che sia il +2 rispetto al primo se sposti l'ordine non va
                // if (menuItem.equals(Global.INFO_STOCK_UBICAZIONE)) first += 2;

                if (allReadOnly == false) {
                    primo = (EditText) grigliaViste.getChildAt(first).findViewById(R.id.descrizione);
                    if (
                            menuItem.equals(Global.INFO_STOCK_UBICAZIONE) ||
                                    menuItem.equals(Global.INFO_STOCK_MATERIALE) ||
                                    menuItem.equals(Global.INFO_STOCK) ||
                                    menuItem.equals(Global.TRASF_TRA_UBICAZIONE) ||
                                    menuItem.equals(Global.MANCANTI_DA_ZMAG) ||
                                    menuItem.equals(Global.AVANZAVA_FASE) ||
                                    menuItem.equals(Global.LT24) ||
                                    menuItem.equals(Global.INFO_STOCK_ODP) ||
                                    menuItem.equals(Global.MESSA_IN_UBICAZIONE_DA_BEM) ||
                                    menuItem.equals(Global.CRONOLOGIA_CODICE) ||
                                    menuItem.equals(Global.ELENCOCASSONI) ||
                                    menuItem.equals(Global.SPOSTACASSONE) ||
                                    menuItem.equals(Global.VERIFICACASSONE) ||
                                    menuItem.equals(Global.MENU_DOVE_CASSONE) ||
                                    menuItem.equals(Global.MENU_IMPACCHETTAMENTO_MATERIALE) ||
                                    menuItem.equals(Global.MENU_SPACCHETTAMENTO_MATERIALE) ||
                                    menuItem.equals(Global.MENU_REIMPACCHETTAMENTO_MATERIALE) ||
                                    menuItem.equals(Global.MENU_CONSUMA_CASSONE)
                            ) {
                        //primo.setText("");
                        bottoneAzione1.setVisibility(View.INVISIBLE);
                        primo.requestFocus();
                        primo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                            @Override
                            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                boolean handled = false;
                                if (actionId == EditorInfo.IME_ACTION_DONE) {
                                    bottoneAvantiClick(v);
                                    handled = true;
                                }
                                return handled;
                            }
                        });
                    }
                }
                // Svuoto il campo materiale quando rientro da info stock
                if (menuItem.equals(Global.INFO_STOCK_MATERIALE) || menuItem.equals(Global.MESSA_IN_UBICAZIONE_DA_BEM)) {
                    primo.setText("");
                }

                if (menuItem.equals(Global.AVANZAVA_FASE)) {
                    ((EditText) grigliaViste.getChildAt(first + 1).findViewById(R.id.descrizione)).setText("");
                }


                if (menuItem.equals(Global.TESTRFID)) {
                    bottoneAzione2.setVisibility(View.VISIBLE);
                    bottoneAzione2.setText("Inizia Lettura RFID");
                    runFlag = false;
                    startFlag = false;
                }

                if(menuItem.equals(Global.CRONOLOGIA_CODICE)) {
                    bottoneAzione2.setVisibility(View.VISIBLE);
                    bottoneAzione2.setText("Inizia Lettura RFID");
                    runFlag = false;
                    startFlag = false;
                }

                if (menuItem.equals(Global.AVANZAVA_FASE + "PASSO2")) {
                    bottoneAzione2.setVisibility(View.VISIBLE);
                    bottoneAzione2.setText("Inizia Lettura RFID");
                    runFlag = false;
                    startFlag = false;
                }

                if (menuItem.equals(Global.LISTA_PREL_PROD + "PASSO3") || (menuItem.equals(Global.LISTA_PREL_VEND + "PASSO3"))) {
                    bottoneAzione2.setVisibility(View.VISIBLE);
                    bottoneAzione2.setText("Inizia Lettura RFID");
                    runFlag = false;
                    startFlag = false;
                }

                if (menuItem.equals(Global.DISTINTAMACCHINA)) {
                    primo.requestFocus();
                    bottoneAzione2.setVisibility(View.VISIBLE);
                    bottoneAzione2.setText("Inizia Lettura RFID");
                    runFlag = false;
                    startFlag = false;
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                }

                if (menuItem.equals(Global.DISTINTAMACCHINA + "PASSO2")) {
                    bottoneAzione2.setVisibility(View.VISIBLE);
                    bottoneAzione2.setText("Inizia Lettura RFID");
                    runFlag = false;
                    startFlag = false;
                }

                if (!getDescrizione("Materiale", false).equals("")) {
                    //bottoneAzione1.setText("Documenta");
                    bottoneAzione1.setBackgroundResource(R.drawable.documenta);
                    bottoneAzione1.setVisibility(View.VISIBLE);
                    if (getLocalClassName().equals("PrendiParametri") && getDescrizioneReadOnly("Materiale")) {
                        String urlStr = Global.serverDOCUMENTAURL + Global.get3D + getDescrizione("Materiale", false);
                        new DownloadImageTask(anteprima).execute(urlStr);
                    }
                }

            }
            // di default il refresh è attivo
            refresh = true;
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        bottoneAvanti.setEnabled(true);
    }

    @Override
    public void bottoneAzione2Click (View v) {

        startFlag = !startFlag;
        if (startFlag) {
            //Global.alert(this,"Abilita RFID");
            /*
            if (Global.getImpostazioniApp("RFID").equals("NO")) {
                Global.alert(getBaseContext(), "Lettore RFID non attivo");
            } else {*/
                runFlag = true;
                bottoneAzione2.setText("Ferma Lettura RFID");

                // Svuoto l'array di RFID quando clicco "Inizia lettura" su distinta macchina
                if (menuItem.equals(Global.DISTINTAMACCHINA)) {
                    listEPC = new ArrayList<>();
                    listepc = new ArrayList<>();
                }
                switch (manufactorerType) {
                    case VH71T: {
                        thread = new InventoryThread();
                        thread.start();
                        break;
                    }
                    case RPA520: {
                        LoopReadEPCdRPA520();
                        break;
                    }
                    default: {
                        Global.alert(this.getApplicationContext(), "Attenzione, il terminale non supporta l'RFID");
                        runFlag = false;
                        bottoneAzione2.setText("Inizia Lettura RFID");
                        break;
                    }
                }
            //}
        } else {
            runFlag = false;
            bottoneAzione2.setText("Inizia Lettura RFID");
        }
    }

    private long lastClickTime = 0;
    // se vado avanti imposto tutti valori scelti
    @Override
    public void bottoneAvantiClick (View v) {
        Intent intent = new Intent();
        if (Global.BUTTON_WAIT_FEATURE) {
            // Evito il doppio TAP
            if (SystemClock.elapsedRealtime() - lastClickTime < Global.BUTTON_CLICK_MS_WAIT) {
                return;
            }

            Log.d("CLIC", "Click " + lastClickTime);

            lastClickTime = SystemClock.elapsedRealtime();
        }
        Log.d("menuItem", menuItem);

        // Disabilito per evitare un secondo tap
        bottoneAvanti.setEnabled(false);

        startFlag = false;
        runFlag = false;
        bottoneAzione2.setText("Inizia Lettura RFID");
        // Svuoto l'array di RFID quando clicco "Inizia lettura" su distinta macchina
        if (menuItem.equals(Global.DISTINTAMACCHINA)) {
            listEPC = new ArrayList<>();
            listepc = new ArrayList<>();
        }

        // azzera il controllo dei campi obbligatori
        stop = false;

        if (menuItem.equals(Global.AVANZAVA_FASE)) {
            String conferma = getDescrizione("Conferma");

            // se il codice della Conferma è di una matricola che deve avere il seriale tag chiedo altre informazioni
            LinkedHashMap<String, String> campiTest = new LinkedHashMap<>();
            campiTest.put("E_SERNR", "E_SERNR");

            ArrayList<HashMap<String, String>> listaValoriTemp = Global.getValoriXML(getUrlConfermaTest(), campiTest);

            if ("X".equals(listaValoriTemp.get(0).get("E_SERNR"))) {
                intent = new Intent(PrendiParametri.this, PrendiParametri.class);
                intent.putExtra("titolo", titolo);
                intent.putExtra("quantita", getDescrizione("Quantita"));
                intent.putExtra("info2", "Qta: " + getDescrizione("Quantita") + " - " + conferma);
                intent.putExtra("menuItem", menuItem + "PASSO2");
                intent.putExtra("listaValori", getListaValori(Integer.decode(getDescrizione("Quantita"))));
                // richiamo la prendiparametri chiedendo il numero di serie degli N elementi

            } else {
                //altrimenti eseguo l'operazione standard
                intent = new Intent(PrendiParametri.this, Esito.class);

                intent.putExtra("titolo", titolo);
                intent.putExtra("info2", "Qta: " + getDescrizione("Quantita") + " - " + conferma);
                String url = Global.serverURL + Global.confirmXML;
                url = url.replaceAll("#I_BADGE_USER#", Global.mioBadge);
                url = url.replaceAll("#I_AUFNR#", "");
                url = url.replaceAll("#I_ANFME#", getDescrizione("Quantita"));
                url = url.replaceAll("#I_RUECK#", conferma);
                Log.d("URL", url);
                intent.putExtra("url", url);
            }
        } else if (menuItem.equals(Global.AVANZAVA_FASE + "PASSO2")) {


            String conferma = getDescrizione("Conferma");
            String t_sernr = getRFID(Integer.decode(myIntent.getStringExtra("quantita")));

            intent = new Intent(PrendiParametri.this, Esito.class);
            intent.putExtra("backMenuLoop", "2");
            intent.putExtra("titolo", titolo);
            intent.putExtra("info2", "Qta: " + intent.getStringExtra("quantita") + " - " + conferma);

            String url = Global.serverURL + Global.setSerialConfirmXML;
            url = url.replaceAll("#I_BADGE_USER#", Global.mioBadge);
            url = url.replaceAll("#I_AUFNR#", "");
            url = url.replaceAll("#I_ANFME#", myIntent.getStringExtra("quantita"));
            url = url.replaceAll("#I_RUECK#", conferma);
            url = url.replaceAll("#T_SERNR#", t_sernr);
            Log.d("URL", "t_sernr: " + t_sernr);

            Log.d("URL", url);
            intent.putExtra("url", url);

        } else if (menuItem.equals(Global.INFO_STOCK) ||
                menuItem.equals(Global.INFO_STOCK_MATERIALE) ||
                menuItem.equals(Global.INFO_STOCK_UBICAZIONE) ||
                menuItem.equals(Global.INFO_STOCK_ODP)) {
            intent = new Intent(PrendiParametri.this, InfoStockZmag.class);

            intent.putExtra("menuItem", menuItem);
            intent.putExtra("titolo", titolo);

            // In InfoStock sparano sulla BEM. Il codice BEM è composto da una stringa di 18 caratteri, così composta
            // ANNO|10 caratteri ubic| altri 4 carateri
            // In caso si tratta di bem, prendiamo i 10 caratteri dopo l'anno e aggiungiamo il suffisso 902
            String tipoUbic = getDescrizione("Tipo + Ubic");
            if (tipoUbic != null && tipoUbic.length() == 18 && tipoUbic.startsWith("20")) {
                tipoUbic = tipoUbic.substring(4, 14);
                tipoUbic = "902" + tipoUbic;

                // Imposto il campo ubic
                setDescrizione("Tipo + Ubic", tipoUbic);
            }

            intent.putExtra("info2", getDescrizione("Materiale") + " - " + getDescrizione("Descrizione") + " - " + tipoUbic + " - " + getDescrizione("Ordine"));

            if (menuItem.equals(Global.INFO_STOCK_MATERIALE)) {
                intent.putExtra("titolo", titolo + " " + getDescrizione("Materiale"));
            } else if (menuItem.equals(Global.INFO_STOCK_UBICAZIONE)) {
                intent.putExtra("titolo", titolo + " " + tipoUbic);
            }
        } else if (menuItem.equals(Global.TRASF_TRA_UBICAZIONE)) {
            intent = new Intent(PrendiParametri.this, Esito.class);
            intent.putExtra("backMenuLoop", "1");
            intent.putExtra("titolo", titolo);
            intent.putExtra("matnr", getDescrizione("Materiale"));
            intent.putExtra("info2", getDescrizione("Materiale"));
            intent.putExtra("info3", "Qta: " + getDescrizione("Quantita") + " - " + getDescrizione("Provenienza") + "-->" + getDescrizione("Destinazione"));

            String url = Global.serverURL + Global.trasferimento262;

            String hu = getDescrizione("HU");
            if (hu != null && !"".equals(hu)) {
                if (!Global.isValidHuInput(hu)) {
                    Global.alert(this, "CAMPO HU IN FORMATO NON VALIDO");
                    stop = true;
                }
            }

            url = url.replaceAll("#I_AUFNR#", "");
            url = url.replaceAll("#I_MATNR#", getDescrizione("Materiale"));
            url = url.replaceAll("#I_QTA_262#", Global.I_QTA_NULLA);
            url = url.replaceAll("#I_QTA_STORNO#", getDescrizione("Quantita"));

            url = url.replaceAll("#I_LGORT#", getDescrizione("Magazzino"));
            url = url.replaceAll("#I_MOV262#", "");
            url = url.replaceAll("#I_WDATU#", Global.dataSAP);
            url = url.replaceAll("#I_MEINS#", "");
            url = url.replaceAll("#I_SOBKZ#", "");
            url = url.replaceAll("#I_SONUM#", "");
            url = url.replaceAll("#I_BADGE_USER#", Global.mioBadge);
            url = url.replaceAll("#I_NLENR#", getDescrizione("HU"));

            url = url.replaceAll("#I_WERKS#", Global.getImpostazoniSAP("Divisione"));
            url = url.replaceAll("#I_LGNUM#", getDescrizione("Numero Mag."));

            String Provenienza = getDescrizione("Provenienza");
            String Destinazione = getDescrizione("Destinazione");

            if ((Provenienza.length() > 3) && (Destinazione.length() > 3)) {
                url = url.replaceAll("#I_VLTYP#", Provenienza.substring(0, 3));
                url = url.replaceAll("#I_VLPLA#", Provenienza.substring(3));
                url = url.replaceAll("#I_NLTYP#", Destinazione.substring(0, 3));
                url = url.replaceAll("#I_NLPLA#", Destinazione.substring(3));
            } else {
                url = url.replaceAll("#I_VLTYP#", "");
                url = url.replaceAll("#I_VLPLA#", "");
                url = url.replaceAll("#I_NLTYP#", "");
                url = url.replaceAll("#I_NLPLA#", "");
            }

            Log.d("URL", url);
            intent.putExtra("url", url);
        } else if ((menuItem.equals(Global.INFO_STOCK + "TR.DA")) ||
                (menuItem.equals(Global.INFO_STOCK + "TR.IN"))) {
            intent = new Intent(PrendiParametri.this, Esito.class);
            intent.putExtra("backMenuLoop", "1");

            intent.putExtra("titolo", titolo);
            intent.putExtra("matnr", getDescrizione("Materiale"));
            intent.putExtra("info2", getDescrizione("Materiale"));
            intent.putExtra("info3", "Qta: " + getDescrizione("Quantita") + " - " + getDescrizione("Provenienza") + "-->" + getDescrizione("Destinazione"));

            // TODO: Prima di lanciare, bisognerà inserire il controllo che c'è l'ubicazione (Dest. | Prov.) OPPURE il cassone (HU Dest. | HU Prov.). Non è possibile specificarli entrambi

            String url = Global.serverURL + Global.LT24_TRASFERIMENTO_NEW;

            String quantita = getDescrizione("Quantita");

            url = url.replaceAll("#I_AUFNR#", myIntent.getStringExtra("AUFNR"));
            url = url.replaceAll("#I_MATNR#", myIntent.getStringExtra("MATNR"));
            url = url.replaceAll("#I_QTA_262#", Global.I_QTA_NULLA);
            //url = url.replaceAll("#I_QTA_STORNO#",getDescrizione("Quantita"));        // TOLTO
            url = url.replaceAll("#I_MENGE#", quantita);

            url = url.replaceAll("#I_WERKS#", myIntent.getStringExtra("WERKS"));
            url = url.replaceAll("#I_LGORT#", myIntent.getStringExtra("LGORT"));
            //url = url.replaceAll("#I_MOV262#",myIntent.getStringExtra("MOV262"));
            url = url.replaceAll("#I_WDATU#", Global.dataSAP);
            url = url.replaceAll("#I_MEINS#", myIntent.getStringExtra("MEINS"));
            url = url.replaceAll("#I_SOBKZ#", myIntent.getStringExtra("SOBKZ"));
            url = url.replaceAll("#I_SONUM#", myIntent.getStringExtra("SONUM"));
            url = url.replaceAll("#I_BADGE_USER#", Global.mioBadge);
            //url = url.replaceAll("#I_NLENR#",getDescrizione("HU"));
            url = url.replaceAll("#I_LGNUM#", myIntent.getStringExtra("LGNUM"));

            String destinazione = getDescrizione("Dest.");
            String huDestinazione = getDescrizione("HU Dest.");
            String provenienza = getDescrizione("Prov.");
            String huProvenienza = getDescrizione("HU Prov.");

            String hu = menuItem.equals(Global.INFO_STOCK + "TR.IN") ? huDestinazione : huProvenienza;
            if (hu != null && !"".equals(hu)) {
                if (!Global.isValidHuInput(hu)) {
                    Global.alert(this, "CAMPO HU IN FORMATO NON VALIDO");
                    stop = true;
                }
            }

            if (menuItem.equals(Global.INFO_STOCK + "TR.IN")) {
                url = url.replaceAll("#I_VLTYP#", myIntent.getStringExtra("LGTYP"));
                url = url.replaceAll("#I_VLPLA#", myIntent.getStringExtra("LGPLA"));

                // TODO: Prima o poi da aggiungere controllo O cassone O destinazione
                if (destinazione != null && destinazione.length() > 3) {
                    url = url.replaceAll("#I_NLTYP#", destinazione.substring(0, 3));
                    url = url.replaceAll("#I_NLPLA#", destinazione.substring(3));
                } else {
                    url = url.replaceAll("#I_NLTYP#", "");
                    url = url.replaceAll("#I_NLPLA#", "");
                }

                url = url.replaceAll("#I_EXIDV2_DA#", huProvenienza);
                url = url.replaceAll("#I_EXIDV2_A#", huDestinazione);

                url = url.replaceAll("#I_IN#", "X");
            } else {
                url = url.replaceAll("#I_NLTYP#", myIntent.getStringExtra("LGTYP"));
                url = url.replaceAll("#I_NLPLA#", myIntent.getStringExtra("LGPLA"));

                if (provenienza != null && provenienza.length() > 3) {
                    url = url.replaceAll("#I_VLTYP#", provenienza.substring(0, 3));
                    url = url.replaceAll("#I_VLPLA#", provenienza.substring(3));
                } else {
                    url = url.replaceAll("#I_VLTYP#", "");
                    url = url.replaceAll("#I_VLPLA#", "");
                }

                url = url.replaceAll("#I_EXIDV2_DA#", huProvenienza);
                url = url.replaceAll("#I_EXIDV2_A#", huDestinazione);

                url = url.replaceAll("#I_IN#", "");
            }

            Log.d("URL", url);
            intent.putExtra("url", url);
        } else if (menuItem.equals(Global.MANCANTI_DA_ZMAG)) {
            intent.putExtra("menuItem", menuItem);
            Log.d("DESCRIZIONECOPIA", "Menu item: " + menuItem);

            intent = new Intent(PrendiParametri.this, MancantiZmag.class);
        } else if (menuItem.equals(Global.MANCANTI_DA_ZMAG + "PASSO2")) {

            //TODO: occhio all'intent prima era Esito.class
            intent = new Intent(PrendiParametri.this, Esito.class);

            intent.putExtra("backMenuLoop", "1");
            intent.putExtra("menuItem", menuItem);

            String bem = myIntent.getStringExtra("Bem");
            intent.putExtra("titolo", "Manc.BEM: " + bem);

            String esercizio = bem.substring(0, 4);
            String num_doc = bem.substring(4, 14);
            String pos_doc = bem.substring(14);

            String hu = getDescrizione("HU");
            if (hu != null && !"".equals(hu)) {
                if (!Global.isValidHuInput(hu)) {
                    Global.alert(this, "CAMPO HU IN FORMATO NON VALIDO");
                    stop = true;
                }
            }
            ArrayList<String> multiUrl = new ArrayList<>();
            String url = Global.serverURL + Global.trasfMancantiXML;

            url = url.replaceAll("#I_BADGE_USER#", Global.mioBadge);
            url = url.replaceAll("#I_LGNUM#", Global.getImpostazoniSAP("Numero Magazzino"));
            url = url.replaceAll("#I_MJAHR#", esercizio);
            url = url.replaceAll("#I_MBLNR#", num_doc);
            url = url.replaceAll("#I_ZEILE#", pos_doc);
            url = url.replaceAll("#I_QTA#", getDescrizione("Quantita"));
            url = url.replaceAll("#I_NLTYP#", myIntent.getStringExtra("NLTYP"));
            url = url.replaceAll("#I_NLPLA#", myIntent.getStringExtra("NLPLA"));
            url = url.replaceAll("#I_NLENR#", getDescrizione("HU"));

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
            Log.d("TestIntent", "I_NLENR: " + getDescrizione("HU"));            Log.d("TestIntent", "I_NLENR: " + getDescrizione("HU"));

            Log.d("TestIntent", "I_MODE: " + Global.I_MODE);

*/
            multiUrl.add(url);


            // TODO: Prima di lanciare, bisognerà inserire il controllo che c'è l'ubicazione (Dest. | Prov.) OPPURE il cassone (HU Dest. | HU Prov.). Non è possibile specificarli entrambi

            String url1 = Global.serverURL + Global.LT24_TRASFERIMENTO_NEW;


            url1 = url1.replaceAll("#I_AUFNR#", "00" + myIntent.getStringExtra("NLPLA"));
            url1 = url1.replaceAll("#I_MATNR#", myIntent.getStringExtra("Materiale"));
            url1 = url1.replaceAll("#I_QTA_262#", Global.I_QTA_NULLA);

            String quantita = getDescrizione("Quantita");
            if (getDescrizione("Quantita").contains(".")) {
                quantita = getDescrizione("Quantita").substring(0, getDescrizione("Quantita").indexOf("."));
            }

            Log.d("TESTQUANTITA", quantita);
            url1 = url1.replaceAll("#I_MENGE#", quantita);

            url1 = url1.replaceAll("#I_WERKS#", "D110");  //
            url1 = url1.replaceAll("#I_LGORT#", "W110");  //
            url1 = url1.replaceAll("#I_WDATU#", Global.dataSAP);
            url1 = url1.replaceAll("#I_MEINS#", "ST"); //
            url1 = url1.replaceAll("#I_SOBKZ#", "");
            url1 = url1.replaceAll("#I_SONUM#", "");
            url1 = url1.replaceAll("#I_BADGE_USER#", Global.mioBadge);
            url1 = url1.replaceAll("#I_LGNUM#", Global.getImpostazoniSAP("Numero Magazzino"));

            url1 = url1.replaceAll("#I_VLTYP#", myIntent.getStringExtra("NLTYP"));
            url1 = url1.replaceAll("#I_VLPLA#", myIntent.getStringExtra("NLPLA"));

            // TODO: Prima o poi da aggiungere controllo O cassone O destinazione
            url1 = url1.replaceAll("#I_NLTYP#", myIntent.getStringExtra("NLTYP"));
            url1 = url1.replaceAll("#I_NLPLA#", myIntent.getStringExtra("NLPLA"));


            url1 = url1.replaceAll("#I_EXIDV2_DA#", "");
            url1 = url1.replaceAll("#I_EXIDV2_A#", getDescrizione("HU"));

            url1 = url1.replaceAll("#I_IN#", "X");

            Log.d("URLParte2", url1);


            multiUrl.add(url1);

            intent.putExtra("multiUrl", multiUrl);


        } else if (menuItem.equals(Global.MESSA_IN_UBICAZIONE_DA_BEM)) {
            intent = new Intent(PrendiParametri.this, MessaInUbicazioneDaBem.class);
            intent.putExtra("Bem", getDescrizione("Bem"));

        } else if (menuItem.equals(Global.MESSA_IN_UBICAZIONE_DA_BEM + "PASSO2")) {
            intent = new Intent(PrendiParametri.this, Esito.class);
            String url = Global.serverURL + Global.trasfMancantiXML;

            String bem = myIntent.getStringExtra("Bem");
            String esercizio = bem.substring(0, 4);
            String num_doc = bem.substring(4, 14);
            String pos_doc = bem.substring(14);

            /*String hu = getDescrizione("HU");
            if (hu != null && !"".equals(hu)) {
                if (!Global.isValidHuInput(hu)) {
                    Global.alert(this, "CAMPO HU IN FORMATO NON VALIDO");
                    stop = true;
                }
            }*/

            String destinazione = getDescrizione("Dest.");

            url = url.replaceAll("#I_BADGE_USER#", Global.mioBadge);
            url = url.replaceAll("#I_LGNUM#", Global.getImpostazoniSAP("Numero Magazzino"));
            url = url.replaceAll("#I_MJAHR#", esercizio);
            url = url.replaceAll("#I_MBLNR#", num_doc);
            url = url.replaceAll("#I_ZEILE#", pos_doc);
            url = url.replaceAll("#I_QTA#", getDescrizione("Quantita"));
            url = url.replaceAll("#I_NLTYP#", destinazione.substring(0, 3));
            url = url.replaceAll("#I_NLPLA#", destinazione.substring(4));
            url = url.replaceAll("#I_NLENR#", "");

            url = url.replaceAll("#I_MODE#", Global.I_MODE);
            url = url.replaceAll("#I_UPDATE#", Global.I_UPDATE);
            url = url.replaceAll("#I_WDATU#", Global.dataSAP);
            Log.d("URLPRENDIUBICAZIONE", url);
            intent.putExtra("url", url);


        } else if (menuItem.equals(Global.LISTA_PREL_PROD + "PASSO2")) {
            intent = new Intent(PrendiParametri.this, Esito.class);
            intent.putExtra("backMenuLoop", "1");
            intent.putExtra("titolo", titolo);
            intent.putExtra("matnr", getDescrizione("Materiale"));
            intent.putExtra("info2", getDescrizione("Materiale") + " - " + getDescrizione("Descrizione"));
            intent.putExtra("info3", "Qta: " + getDescrizione("Quantita") + " - " + getDescrizione("Da Ubic") + "-->" + getDescrizione("A Ubic"));

            String parRettifica = "";
            if (getDescrizione("Rettifica").equals("SI")) parRettifica = "X";

            //int valueQ = Integer.valueOf(getDescrizione("Quantita1"));
            float valueQ = 0;
            int quantityCounter = 0;

            String s = "";

            for (int i = 1; i < 4; i++) {
                if (!getDescrizione("Quantita" + i).equals("")) {
                    s = getDescrizione("Quantita" + i);
                    if (getDescrizione("Quantita" + i).contains(",")) {
                        s = s.replace(",",".");
                        Log.d("TESTVALQUANTITA+", "Quantita Stringa: "  + s);
                    }
                    Log.d("TESTVALQUANTITA+", "Quantita: "  + Float.parseFloat(s));
                    valueQ = valueQ + Float.parseFloat(s);
                    String hu = getDescrizione("HU" + i);
                    if (hu != null && !"".equals(hu)) {
                        if (!Global.isValidHuInput(hu)) {
                            Global.alert(this, "CAMPO HU IN FORMATO NON VALIDO");
                            stop = true;
                        } else {
                            quantityCounter++;
                        }
                    }
                }
            }


            //Dimensioni di controllo da rivedere e da accorpare
            String quantitaFinale = ""+valueQ;
            if (quantitaFinale.contains(".")) {
                quantitaFinale = quantitaFinale.replace(".", ",");
            }
            if(myIntent.getStringExtra("VSOLM_C").length() > quantitaFinale.length()) {
                for (int k = quantitaFinale.length(); k < myIntent.getStringExtra("VSOLM_C").length(); k++) {
                    quantitaFinale = quantitaFinale + "0";
                }
            }

            if(!myIntent.getStringExtra("VSOLM_C").contains(",")) {
                quantitaFinale = quantitaFinale.substring(0, quantitaFinale.indexOf(","));
            }


            //TODO: da togliere il controllo sulla rettifica
            Log.d("Rettifica", "RETTIFICA: " + getDescrizione("Rettifica"));
            /*if ((!quantitaFinale.equalsIgnoreCase(myIntent.getStringExtra("VSOLM_C"))) && (getDescrizione("Rettifica").equals("!F"))) {
                stop = true;
                Global.alert(this, "QTA CONFERMATA DIVERSA - VALORE 'RETTIFICA' OBBLIGATORIO");
            }*/
            if(getDescrizione("Rettifica").equals("!F")){
                stop = true;
                Global.alert(this, "INSERIRE VALORE DI RETTIFICA");
            }

            if(valueQ < 0) {
                stop = true;
                Global.alert(this, "QTA CONFERMATA NEGATIVA");
            }

            ArrayList<String> multiUrl = new ArrayList<>();


            if (stop == false) {
                String url = Global.serverURL + Global.listaPrelConfirmXML;
                url = url.replaceAll("#I_MODE#", Global.I_MODE);
                url = url.replaceAll("#I_UPDATE#", Global.I_UPDATE);
                url = url.replaceAll("#I_BADGE_USER#", Global.mioBadge);
                url = url.replaceAll("#I_LGNUM#", Global.getImpostazoniSAP("Numero Magazzino"));
                url = url.replaceAll("#I_QTA_CONF#", quantitaFinale);
                url = url.replaceAll("#I_CNFTYPE#", "2");   //1 == ?  2 === ?
                if (menuItem.equals(Global.LISTA_PREL_VEND + "PASSO2")) {
                    url = url.replaceAll("#I_OBJTYPE#", "C");   //C == Vendita
                    url = url.replaceAll("#I_REFNR#", myIntent.getStringExtra("VBELN"));
                } else {
                    url = url.replaceAll("#I_OBJTYPE#", "G");   //G == Prod
                    url = url.replaceAll("#I_REFNR#", myIntent.getStringExtra("REFNR"));
                }


                url = url.replaceAll("#I_RETT#", parRettifica);
                url = url.replaceAll("#I_TANUM#", "");
                url = url.replaceAll("#I_TAPOS#", "");
                url = url.replaceAll("#I_MATNR#", getDescrizione("Materiale"));
                url = url.replaceAll("#I_VLTYP#", myIntent.getStringExtra("VLTYP"));
                url = url.replaceAll("#I_VLPLA#", myIntent.getStringExtra("VLPLA"));
                url = url.replaceAll("#I_NLTYP#", "");
                url = url.replaceAll("#I_NLPLA#", "");
                url = url.replaceAll("#I_NLENR#", "");
                url = url.replaceAll("#I_EXIDV2#", getDescrizione("HU1"));

                url = url.replaceAll("#I_TIPO#", "");
                url = url.replaceAll("#I_BADGE_USER#", Global.mioBadge);
                url = url.replaceAll("#I_AUFNR#", "");

                Log.d("TestURL", url + "");


                // Dopo aver fatto la chiamata, esegue il packing
                intent.putExtra("menuItem", menuItem);
                intent.putExtra("ODPNR", getDescrizione("ODPNR"));

                // se il codice della Conferma è di una matricola che deve avere il seriale tag chiedo altre informazioni
                LinkedHashMap<String, String> campiTest = new LinkedHashMap<>();
                campiTest.put("E_SERIALE", "E_SERIALE");

                ArrayList<HashMap<String, String>> listaValoriTemp = Global.getValoriXML(getUrlPrelievoTest(), campiTest);

                // TODO: ATTENZIONE!! DECOMMENTARE, SOLO PER DEBUG
                if ("X".equals(listaValoriTemp.get(0).get("E_SERIALE"))) {

                    // servono gli RFID quindi li chiede e li aggiunge all'url passata per parametro
                    intent = new Intent(PrendiParametri.this, PrendiParametri.class);
                    intent.putExtra("menuItem", menuItem.replace("PASSO2", "PASSO3"));
                    intent.putExtra("quantita", getDescrizione("Quantita"));
                    intent.putExtra("info2", getDescrizione("Materiale") + " - " + getDescrizione("Descrizione"));
                    intent.putExtra("info3", "Qta: " + getDescrizione("Quantita") + " - " + getDescrizione("Da Ubic") + "-->" + getDescrizione("A Ubic"));
                    intent.putExtra("listaValori", getListaValoriPrelievo(Integer.decode(getDescrizione("Quantita"))));
                    intent.putExtra("VLTYP", myIntent.getStringExtra("VLTYP"));
                    intent.putExtra("VLPLA", myIntent.getStringExtra("VLPLA"));
                    intent.putExtra("REFNR", myIntent.getStringExtra("REFNR"));
                    intent.putExtra("VBELN", myIntent.getStringExtra("VBELN"));

                } else {
                    url = url.replaceAll("#T_SERNR#", "");
                    Log.d("URLDEBUG", url);
                    //intent.putExtra("multiurl", url);
                }
                multiUrl.add(url);

                //Chiamata secondara WORKARAOUND
                if (quantityCounter > 1) {
                    for (int i = 1; i < quantityCounter + 1; i++) {
                        if (!getDescrizione("HU" + i).equals("") && !getDescrizione("Quantita" + i).equals("")) {
                            String daCassone = getDescrizione("HU1");

                            //String daOdp = getDescrizione("Da Odp");

                            String aCassone = getDescrizione("HU" + i);

                            String hu = aCassone;
                            if (hu != null && !"".equals(hu)) {
                                if (!Global.isValidHuInput(hu)) {
                                    Global.alert(this, "CAMPO HU IN FORMATO NON VALIDO");
                                    stop = true;
                                }
                            }

                            // String aOdp = getDescrizione("A Odp");
                            String materiale = getDescrizione("Materiale");
                            String qta = getDescrizione("Quantita" + i);
                            //String um = getDescrizione("Unita Misura");
                            String cdStockSpe = "";
                            String numStockSpe = "";
                            String odp = myIntent.getStringExtra("ODP");
                            Log.d("ODP", odp);

                            url = Global.serverURL + Global.CASSONI_SPOSTAMENTO_MATERIALE;
                            url = url.replace("#IV_EXIDV2_O#", daCassone);
                            url = url.replace("#IV_EXIDV2_D#", hu);
                            url = url.replace("#IV_AUFNR_O#", "000" + odp);
                            url = url.replace("#IV_AUFNR_D#", "000" + odp);
                            url = url.replace("#MATNR#", materiale);
                            url = url.replace("#VEMNG#", qta);
                            url = url.replace("#VEMEH#", "ST");
                            url = url.replace("#SOBKZ#", cdStockSpe);
                            url = url.replace("#SONUM#", numStockSpe);
                            url = url.replace("#I_BADGE_USER#", Global.mioBadge);

                            Log.d("TestURL", url + "");
                            String errorText = Global.checkHuAndOdpConsistency(daCassone, odp);


                                intent = new Intent(PrendiParametri.this, Esito.class);
                                intent.putExtra("titolo", "Esito Reimpacchettare Materiale");
                                intent.putExtra("info2", "Esito");
                                intent.putExtra("backMenuLoop", "1");

                                Log.d("URLTEST", url);
                                multiUrl.add(url);

                        } else {
                            quantityCounter++;
                        }
                    }
                }
                intent.putExtra("multiUrl", multiUrl);
            }

        }
        /*else if (menuItem.equals(Global.LISTA_PREL_PROD + "PASSO2") || (menuItem.equals(Global.LISTA_PREL_VEND + "PASSO2"))) {
            intent = new Intent(PrendiParametri.this, Esito.class);
            intent.putExtra("backMenuLoop", "1");
            intent.putExtra("titolo", titolo);
            intent.putExtra("matnr", getDescrizione("Materiale"));
            intent.putExtra("info2", getDescrizione("Materiale") + " - " + getDescrizione("Descrizione") );
            intent.putExtra("info3",  "Qta: " + getDescrizione("Quantita1") + " - " + getDescrizione("Da Ubic") + "-->" + getDescrizione("A Ubic"));

            String url = Global.serverURL + Global.listaPrelConfirmXML;
            url = url.replaceAll("#I_MODE#", Global.I_MODE);
            url = url.replaceAll("#I_UPDATE#", Global.I_UPDATE);
            url = url.replaceAll("#I_BADGE_USER#", Global.mioBadge);
            url = url.replaceAll("#I_LGNUM#", Global.getImpostazoniSAP("Numero Magazzino"));
            url = url.replaceAll("#I_QTA_CONF#", getDescrizione("Quantita1"));
            url = url.replaceAll("#I_CNFTYPE#", "2");   //1 == ?  2 === ?
            if (menuItem.equals(Global.LISTA_PREL_VEND + "PASSO2")) {
                url = url.replaceAll("#I_OBJTYPE#", "C");   //C == Vendita
                url = url.replaceAll("#I_REFNR#", myIntent.getStringExtra("VBELN"));
            } else {
                url = url.replaceAll("#I_OBJTYPE#", "G");   //G == Prod
                url = url.replaceAll("#I_REFNR#", myIntent.getStringExtra("REFNR"));
            }

            String parRettifica = "";
            if (getDescrizione("Rettifica").equals("SI")) parRettifica = "X";

            if ((!getDescrizione("Quantita1").equalsIgnoreCase(myIntent.getStringExtra("VSOLM_C")))
                    && (getDescrizione("Rettifica").equals("!F"))) {
                stop = true;
                Global.alert(this, "QTA CONFERMATA DIVERSA - VALORE 'RETTIFICA' OBBLIGATORIO");
            }

            String hu = getDescrizione("HU1");
            if (hu != null && !"".equals(hu)) {
                if (!Global.isValidHuInput(hu)) {
                    Global.alert(this, "CAMPO HU IN FORMATO NON VALIDO");
                    stop = true;
                }
            }

            url = url.replaceAll("#I_RETT#", parRettifica);
            url = url.replaceAll("#I_TANUM#", "");
            url = url.replaceAll("#I_TAPOS#", "");
            url = url.replaceAll("#I_MATNR#", getDescrizione("Materiale"));
            url = url.replaceAll("#I_VLTYP#", myIntent.getStringExtra("VLTYP"));
            url = url.replaceAll("#I_VLPLA#", myIntent.getStringExtra("VLPLA"));
            url = url.replaceAll("#I_NLTYP#", "");
            url = url.replaceAll("#I_NLPLA#", "");
            url = url.replaceAll("#I_NLENR#", "");
            url = url.replaceAll("#I_EXIDV2#", getDescrizione("HU1"));

            url = url.replaceAll("#I_TIPO#", "");
            url = url.replaceAll("#I_BADGE_USER#", Global.mioBadge);
            url = url.replaceAll("#I_AUFNR#", "");

            // Dopo aver fatto la chiamata, esegue il packing
            intent.putExtra("menuItem", menuItem);
            intent.putExtra("ODPNR", getDescrizione("ODPNR"));

            // se il codice della Conferma è di una matricola che deve avere il seriale tag chiedo altre informazioni
            LinkedHashMap<String, String> campiTest = new LinkedHashMap<>();
            campiTest.put("E_SERIALE", "E_SERIALE");

            ArrayList<HashMap<String, String>> listaValoriTemp = Global.getValoriXML(getUrlPrelievoTest(), campiTest);

            // TODO: ATTENZIONE!! DECOMMENTARE, SOLO PER DEBUG
            if ("X".equals(listaValoriTemp.get(0).get("E_SERIALE"))) {

                // servono gli RFID quindi li chiede e li aggiunge all'url passata per parametro
                intent = new Intent(PrendiParametri.this, PrendiParametri.class);
                intent.putExtra("menuItem", menuItem.replace("PASSO2", "PASSO3"));
                intent.putExtra("quantita", getDescrizione("Quantita"));
                intent.putExtra("info2", getDescrizione("Materiale") + " - " + getDescrizione("Descrizione") );
                intent.putExtra("info3",  "Qta: " + getDescrizione("Quantita") + " - " + getDescrizione("Da Ubic") + "-->" + getDescrizione("A Ubic"));
                intent.putExtra("listaValori", getListaValoriPrelievo(Integer.decode(getDescrizione("Quantita"))));
                intent.putExtra("VLTYP", myIntent.getStringExtra("VLTYP"));
                intent.putExtra("VLPLA", myIntent.getStringExtra("VLPLA"));
                intent.putExtra("REFNR", myIntent.getStringExtra("REFNR"));
                intent.putExtra("VBELN", myIntent.getStringExtra("VBELN"));

            } else {
                url = url.replaceAll("#T_SERNR#", "");
                Log.d("URLDEBUG",url);
                //intent.putExtra("url", url);
            }
        }*/
        else if ((menuItem.equals(Global.LISTA_PREL_VEND + "PASSO2"))){
            intent = new Intent(PrendiParametri.this, Esito.class);
            intent.putExtra("backMenuLoop", "1");
            intent.putExtra("titolo", titolo);
            intent.putExtra("matnr", getDescrizione("Materiale"));
            intent.putExtra("info2", getDescrizione("Materiale") + " - " + getDescrizione("Descrizione") );
            intent.putExtra("info3",  "Qta: " + getDescrizione("Quantita1") + " - " + getDescrizione("Da Ubic") + "-->" + getDescrizione("A Ubic"));

            String parRettifica = "";
            if (getDescrizione("Rettifica").equals("SI")) parRettifica = "X";

            //int valueQ = Integer.valueOf(getDescrizione("Quantita1"));



            //Dimensioni di controllo da rivedere e da accorpare
            String quantitaFinale = getDescrizione("Quantita1");


            /*if ((!quantitaFinale.equalsIgnoreCase(myIntent.getStringExtra("VSOLM_C"))) && (getDescrizione("Rettifica").equals("!F"))) {
                stop = true;
                Global.alert(this, "QTA CONFERMATA DIVERSA - VALORE 'RETTIFICA' OBBLIGATORIO");
            }*/
            if(getDescrizione("Rettifica").equals("!F")){
                stop = true;
                Global.alert(this, "INSERIRE VALORE DI RETTIFICA");
            }

            if(Integer.valueOf(getDescrizione("Quantita1")) < 0) {
                stop = true;
                Global.alert(this, "QTA CONFERMATA NEGATIVA");
            }



            /*Log.d("HU", "HU1: " + getDescrizione("HU1"));
            Log.d("HU", "HU2: " + getDescrizione("HU2"));
            Log.d("HU", "HU3: " + getDescrizione("HU3"));
            for(int i = 1; i < 4; i++) {
                if (!getDescrizione("HU"+i).equals("")) {
                    String hu = getDescrizione("HU"+i);
                    Log.d("HUFOR", hu);
                    if (hu != null && !"".equals(hu)) {
                        if (!Global.isValidHuInput(hu)) {
                            Global.alert(this, "CAMPO HU IN FORMATO NON VALIDO");
                            stop = true;
                        } else {
                            HUCounter++;
                        }
                    }
                }
            }*/


            if (stop == false) {
                String url = Global.serverURL + Global.listaPrelConfirmXML;
                url = url.replaceAll("#I_MODE#", Global.I_MODE);
                url = url.replaceAll("#I_UPDATE#", Global.I_UPDATE);
                url = url.replaceAll("#I_BADGE_USER#", Global.mioBadge);
                url = url.replaceAll("#I_LGNUM#", Global.getImpostazoniSAP("Numero Magazzino"));
                url = url.replaceAll("#I_QTA_CONF#", quantitaFinale);
                url = url.replaceAll("#I_CNFTYPE#", "2");   //1 == ?  2 === ?
                if (menuItem.equals(Global.LISTA_PREL_VEND + "PASSO2")) {
                    url = url.replaceAll("#I_OBJTYPE#", "C");   //C == Vendita
                    url = url.replaceAll("#I_REFNR#", myIntent.getStringExtra("VBELN"));
                } else {
                    url = url.replaceAll("#I_OBJTYPE#", "G");   //G == Prod
                    url = url.replaceAll("#I_REFNR#", myIntent.getStringExtra("REFNR"));
                }


                url = url.replaceAll("#I_RETT#", parRettifica);
                url = url.replaceAll("#I_TANUM#", "");
                url = url.replaceAll("#I_TAPOS#", "");
                url = url.replaceAll("#I_MATNR#", getDescrizione("Materiale"));
                url = url.replaceAll("#I_VLTYP#", myIntent.getStringExtra("VLTYP"));
                url = url.replaceAll("#I_VLPLA#", myIntent.getStringExtra("VLPLA"));
                url = url.replaceAll("#I_NLTYP#", "");
                url = url.replaceAll("#I_NLPLA#", "");
                url = url.replaceAll("#I_NLENR#", "");
                url = url.replaceAll("#I_EXIDV2#", getDescrizione("HU1"));

                url = url.replaceAll("#I_TIPO#", "");
                url = url.replaceAll("#I_BADGE_USER#", Global.mioBadge);
                url = url.replaceAll("#I_AUFNR#", "");

                Log.d("TestURL", url+"");

                // Dopo aver fatto la chiamata, esegue il packing
                intent.putExtra("menuItem", menuItem);
                intent.putExtra("ODPNR", getDescrizione("ODPNR"));

                // se il codice della Conferma è di una matricola che deve avere il seriale tag chiedo altre informazioni
                LinkedHashMap<String, String> campiTest = new LinkedHashMap<>();
                campiTest.put("E_SERIALE", "E_SERIALE");

                ArrayList<HashMap<String, String>> listaValoriTemp = Global.getValoriXML(getUrlPrelievoTest(), campiTest);

                // TODO: ATTENZIONE!! DECOMMENTARE, SOLO PER DEBUG
                if ("X".equals(listaValoriTemp.get(0).get("E_SERIALE"))) {

                    // servono gli RFID quindi li chiede e li aggiunge all'url passata per parametro
                    intent = new Intent(PrendiParametri.this, PrendiParametri.class);
                    intent.putExtra("menuItem", menuItem.replace("PASSO2", "PASSO3"));
                    intent.putExtra("quantita", getDescrizione("Quantita1"));
                    intent.putExtra("info2", getDescrizione("Materiale") + " - " + getDescrizione("Descrizione"));
                    intent.putExtra("info3", "Qta: " + getDescrizione("Quantita1") + " - " + getDescrizione("Da Ubic") + "-->" + getDescrizione("A Ubic"));
                    intent.putExtra("listaValori", getListaValoriPrelievo(Integer.decode(getDescrizione("Quantita1"))));
                    intent.putExtra("VLTYP", myIntent.getStringExtra("VLTYP"));
                    intent.putExtra("VLPLA", myIntent.getStringExtra("VLPLA"));
                    intent.putExtra("REFNR", myIntent.getStringExtra("REFNR"));
                    intent.putExtra("VBELN", myIntent.getStringExtra("VBELN"));

                } else {
                    url = url.replaceAll("#T_SERNR#", "");
                    Log.d("URL", url);
                    intent.putExtra("url", url);
                }


                //Chiamata secondara WORKARAOUND

            }

        } else if (menuItem.equals(Global.LISTA_PREL_PROD + "PASSO3") || (menuItem.equals(Global.LISTA_PREL_VEND + "PASSO3"))) {
            intent = new Intent(PrendiParametri.this, Esito.class);
            intent.putExtra("backMenuLoop", "1");
            intent.putExtra("titolo", titolo);
            intent.putExtra("matnr", getDescrizione("Materiale"));
            intent.putExtra("info2", getDescrizione("Materiale") + " - " + getDescrizione("Descrizione") );
            intent.putExtra("info3",  "Qta: " + getDescrizione("Quantita") + " - " + getDescrizione("Da Ubic") + "-->" + getDescrizione("A Ubic"));


            String qta = getDescrizione("Qta scelta");
            Log.d("URLFPITIEJF", "QTA: " + qta);
            Log.d("URLFPITIEJF", "Quantita: " + getDescrizione("Quantita"));
            Log.d("URLFPITIEJF", "Quantita1: " + getDescrizione("Quantita1"));



            qta = qta.substring(0, qta.indexOf(" "));
            String url = Global.serverURL + Global.listaPrelConfirmXML;
            url = url.replaceAll("#I_MODE#", Global.I_MODE);
            url = url.replaceAll("#I_UPDATE#", Global.I_UPDATE);
            url = url.replaceAll("#I_BADGE_USER#", Global.mioBadge);
            url = url.replaceAll("#I_LGNUM#", Global.getImpostazoniSAP("Numero Magazzino"));
            url = url.replaceAll("#I_QTA_CONF#", qta);
            url = url.replaceAll("#I_CNFTYPE#", "2");   //1 == ?  2 === ?
            if (menuItem.equals(Global.LISTA_PREL_VEND + "PASSO3")) {
                url = url.replaceAll("#I_OBJTYPE#", "C");   //C == Vendita
            } else {
                url = url.replaceAll("#I_OBJTYPE#", "G");   //G == Prod
            }

            if (myIntent.getStringExtra("VBELN") != null && !myIntent.getStringExtra("VBELN").isEmpty()){
                url = url.replaceAll("#I_REFNR#", myIntent.getStringExtra("VBELN"));
            }
            if (myIntent.getStringExtra("REFNR") != null && !myIntent.getStringExtra("REFNR").isEmpty()){
                url = url.replaceAll("#I_REFNR#", myIntent.getStringExtra("REFNR"));
            }

            String hu = getDescrizione("HU");
            if (hu != null && !"".equals(hu)) {
                if (!Global.isValidHuInput(hu)) {
                    Global.alert(this, "CAMPO HU IN FORMATO NON VALIDO");
                    stop = true;
                }
            }

            String parRettifica = "";
            if (getDescrizione("Rettifica").equals("SI")) parRettifica = "X";

            url = url.replaceAll("#I_RETT#", parRettifica);
            url = url.replaceAll("#I_TANUM#", "");
            url = url.replaceAll("#I_TAPOS#", "");
            url = url.replaceAll("#I_MATNR#", getDescrizione("Materiale"));
            url = url.replaceAll("#I_VLTYP#", myIntent.getStringExtra("VLTYP"));
            url = url.replaceAll("#I_VLPLA#", myIntent.getStringExtra("VLPLA"));
            url = url.replaceAll("#I_NLTYP#", "");
            url = url.replaceAll("#I_NLPLA#", "");
            url = url.replaceAll("#I_NLENR#", "");
            url = url.replaceAll("#I_EXIDV2#", getDescrizione("HU"));

            url = url.replaceAll("#I_TIPO#", "");
            url = url.replaceAll("#I_BADGE_USER#", Global.mioBadge);
            url = url.replaceAll("#I_AUFNR#", "");

            // Dopo aver fatto la chiamata, esegue il packing
            intent.putExtra("menuItem", menuItem);
            intent.putExtra("ODPNR", getDescrizione("ODPNR"));

            // se il codice della Conferma è di una matricola che deve avere il seriale tag chiedo altre informazioni
            LinkedHashMap<String, String> campiTest = new LinkedHashMap<>();
            campiTest.put("E_SERIALE", "E_SERIALE");

            ArrayList<HashMap<String, String>> listaValoriTemp = Global.getValoriXML(getUrlPrelievoTest(), campiTest);

            // TODO: ATTENZIONE!! DECOMMENTARE, SOLO PER DEBUG
            if ("X".equals(listaValoriTemp.get(0).get("E_SERIALE"))) {
                String t_sernr = getRFID(Integer.decode(qta));
                url = url.replaceAll("#T_SERNR#", t_sernr );
                Log.d("URLFPITIEJF", "t_sernr: " + t_sernr );
            } else {
                url = url.replaceAll("#T_SERNR#", "");
            }

            intent.putExtra("url", url);
            Log.d("URLFPITIEJF",url);

        } else if (menuItem.equals(Global.LISTA_PREL_VEND + "PASSO999") ) {
            // TODO: Chiedere da Omar cos'è ? Sopra era Global.LISTA_PREL_VEND + "PASSO3"
            /*
            intent = new Intent(PrendiParametri.this, Esito.class);
            intent.putExtra("backMenuLoop", "1");
            intent.putExtra("titolo", titolo);
            String url = myIntent.getStringExtra("url");
            url = url.replaceAll("#T_SERNR#", "");
            Log.d("URL",url);
            intent.putExtra("url", url);
            */
        } else  if (menuItem.equals(Global.LT24)) {
            intent = new Intent(PrendiParametri.this, LT24.class);
            intent.putExtra("titolo", titolo);
            intent.putExtra("info2", getDescrizione("Materiale"));
            intent.putExtra("matnr", getDescrizione("Materiale"));

        } else  if (menuItem.equals(Global.VERIFICACASSONE)) {
            intent = new Intent(PrendiParametri.this, VerificaCassoni.class);
            intent.putExtra("titolo", titolo);
            intent.putExtra("info2", getDescrizione("HU"));
            intent.putExtra("HU", getDescrizione("HU"));
        }  else  if (menuItem.equals(Global.MENU_DOVE_CASSONE)) {
            intent = new Intent(PrendiParametri.this, DoveCassone.class);
            intent.putExtra("titolo", titolo);
            intent.putExtra("info2", getDescrizione("HU"));
            intent.putExtra("HU", getDescrizione("HU"));
        } else  if (menuItem.equals(Global.DAMODULAACASSONE)) {
            /*
            intent = new Intent(PrendiParametri.this, SpostaModula.class);

            intent.putExtra("titolo", titolo);
            intent.putExtra("info2", getDescrizione("Posizione"));
            intent.putExtra("Posizione", getDescrizione("Posizione"));
            */
            intent = new Intent(PrendiParametri.this, ElencoCassoni.class);
            intent.putExtra("titolo", titolo);
            intent.putExtra("menuItem", menuItem);
            intent.putExtra("info1", getDescrizione("Posizione"));
            intent.putExtra("info2", getDescrizione("Odp"));
            intent.putExtra("info3", getDescrizione("Materiale"));

            intent.putExtra("HU", "*" + getDescrizione("Posizione"));
            intent.putExtra("Odp", getDescrizione("Odp"));
            intent.putExtra("Materiale", getDescrizione("Materiale"));
            intent.putExtra("fromModula","true");

        } else  if (menuItem.equals(Global.DISTINTAMACCHINA)) {
            intent = new Intent(PrendiParametri.this, DistintaMacchina.class);
            intent.putExtra("titolo", titolo);
            intent.putExtra("info2", getDescrizione("Macchina"));
            intent.putExtra("menuItem", menuItem);
            intent.putExtra("Macchina", getDescrizione("Macchina"));
        }  else  if (menuItem.equals(Global.DISTINTAMACCHINA + "PASSO2")) {
            intent = new Intent(PrendiParametri.this, Esito.class);
            intent.putExtra("backMenuLoop", "1");
            intent.putExtra("titolo", titolo);
            intent.putExtra("info2", getDescrizione("Macchina"));

            HashMap<String, String> paramToShowInError = new HashMap<>();
            paramToShowInError.put("I_SERNR_INF", "Seriale");
            intent.putExtra("paramToShowInError", paramToShowInError);

            ArrayList<String> multiUrl = new ArrayList<>();
            for (int i=1; i<=24; i++) {
                  if (getDescrizione("RFID"+i) != null && !getDescrizione("RFID"+i).equalsIgnoreCase("")) {
                    String url = Global.serverURL + Global.elabSeriali;
                    url = url.replaceAll("#I_FUNCT#", "APPEND_SINGLE");
                    url = url.replaceAll("#I_SERNR#", myIntent.getStringExtra("Macchina"));
                    url = url.replaceAll("#I_SERNR_INF#", getDescrizione("RFID" + i));
                    multiUrl.add(url);
                }
            }
            intent.putExtra("multiUrl", multiUrl);

        } else  if (menuItem.equals(Global.SPOSTACASSONE)) {
            String tipoubic = getDescrizione("Tipo + ubic");

            String tubic = "";
            String ubic = "";

            if (tipoubic != null && tipoubic.length() > 4) {
                tubic = tipoubic.substring(0, 3);
                ubic = tipoubic.substring(3);

                String url = Global.serverURL + Global.CASSONI_SPOSTAMENTO_CASSONI_UBIC;
                String hu = getDescrizione("HU");
                if (hu != null && !"".equals(hu)) {
                    if (!Global.isValidHuInput(hu)) {
                        Global.alert(this, "CAMPO HU IN FORMATO NON VALIDO");
                        stop = true;
                    }
                }
                intent = new Intent(PrendiParametri.this, Esito.class);
                intent.putExtra("backMenuLoop", "1");
                intent.putExtra("titolo", titolo);
                intent.putExtra("info1", getDescrizione("HU"));
                intent.putExtra("info2",tipoubic);

                url = url.replaceAll("#LENUM#", getDescrizione("HU"));
                url = url.replaceAll("#IV_LGTYP#", tubic);
                url = url.replaceAll("#IV_LGPLA#", ubic);
                url = url.replaceAll("#P_LGPLA#", "");
                url = url.replace("#I_BADGE_USER#", Global.mioBadge);

                intent.putExtra("url", url);
            } else {
                Global.alert(this, "Tipo + ubic non corretto");

                stop = true;
            }

            /*
            if (Global.mustArea(getDescrizione("Area"))) {
                String url = Global.serverURL + Global.spostaCassone;

                url = url.replaceAll("#P_LENUM#", getDescrizione("Cassone"));
                url = url.replaceAll("#P_LGNUM#", "");
                url = url.replaceAll("#P_LGTYP#", "");
                url = url.replaceAll("#P_LGPLA#", "");
                url = url.replaceAll("#P_TANUM#", "");
                url = url.replaceAll("#P_TAPOS#", "");
                url = url.replaceAll("#P_ALGTYP#", "STK");
                url = url.replaceAll("#P_ALGPLA#", getDescrizione("Area"));
                intent.putExtra("url", url);
            } else {
                Global.alert(this, "Area non corretta");
            }
            */
        } else  if (menuItem.equals(Global.ELENCOCASSONI)) {
            intent = new Intent(PrendiParametri.this, ElencoCassoni.class);
            intent.putExtra("titolo", titolo);
            intent.putExtra("menuItem", menuItem);
            intent.putExtra("info1", getDescrizione("HU"));
            intent.putExtra("info2", getDescrizione("Odp"));
            intent.putExtra("info3", getDescrizione("Materiale"));

            intent.putExtra("HU", getDescrizione("HU"));
            intent.putExtra("Odp", getDescrizione("Odp"));
            intent.putExtra("Materiale", getDescrizione("Materiale"));

        } else if (menuItem.equals(Global.MENU_REIMPACCHETTAMENTO_MATERIALE)) {

            String daCassone = getDescrizione("Da HU");
            String daOdp = getDescrizione("Da Odp");
            String aCassone = getDescrizione("A HU");

            String hu = aCassone;
            if (hu != null && !"".equals(hu)) {
                if (!Global.isValidHuInput(hu)) {
                    Global.alert(this, "CAMPO HU IN FORMATO NON VALIDO");
                    stop = true;
                }
            }

            String aOdp = getDescrizione("A Odp");
            String materiale = getDescrizione("Materiale");
            String qta = getDescrizione("Qta");
            String um = getDescrizione("Unita Misura");
            String cdStockSpe = getDescrizione("Cd Stock Spec.");
            String numStockSpe = getDescrizione("Num Stock Spec.");

            String url = Global.serverURL + Global.CASSONI_SPOSTAMENTO_MATERIALE;
            url = url.replace("#IV_EXIDV2_O#", daCassone);
            url = url.replace("#IV_EXIDV2_D#", aCassone);
            url = url.replace("#IV_AUFNR_O#", daOdp);
            url = url.replace("#IV_AUFNR_D#", aOdp);
            url = url.replace("#MATNR#", materiale);
            url = url.replace("#VEMNG#", qta);
            url = url.replace("#VEMEH#", um);
            url = url.replace("#SOBKZ#", cdStockSpe);
            url = url.replace("#SONUM#", numStockSpe);
            url = url.replace("#I_BADGE_USER#", Global.mioBadge);

            Log.d("URLTESTREIMPACCHE", url);
            String errorText = Global.checkHuAndOdpConsistency(aCassone, aOdp);

            if (errorText != null) {
                Global.alert(getApplicationContext(), errorText);

                stop = true;
            } else {
                intent = new Intent(PrendiParametri.this, Esito.class);
                intent.putExtra("titolo", "Esito Reimpacchettare Materiale");
                intent.putExtra("info2", "Esito");
                intent.putExtra("backMenuLoop", "1");

                Log.d("URL", url);
                intent.putExtra("url", url);
            }

            Log.d("URLTESTREIMPACCHE", url);

        } else if (menuItem.equals(Global.MENU_IMPACCHETTAMENTO_MATERIALE)) {
            String cassone = getDescrizione("HU");
            String odp = getDescrizione("Odp");
            String materiale = getDescrizione("Materiale");
            String qta = getDescrizione("Qta");
            String um = getDescrizione("Unita Misura");
            String cdStockSpe = getDescrizione("Cd Stock Spec.");
            String numStockSpe = getDescrizione("Num Stock Spec.");

            /*
            String hu = cassone;
            if (hu != null && !"".equals(hu)) {
                if (!Global.isValidHuInput(hu)) {
                    Global.alert(this, "CAMPO HU IN FORMATO NON VALIDO");
                    stop = true;
                }
            }
            */
            String url = Global.serverURL + Global.CASSONI_IMPACCHETTA_MATERIALE;
            url = url.replace("#IV_EXIDV2#", cassone);
            url = url.replace("#IV_AUFNR#", odp);
            url = url.replace("#MATNR#", materiale);
            url = url.replace("#VEMNG#", qta);
            url = url.replace("#VEMEH#", um);
            url = url.replace("#SOBKZ#", cdStockSpe);
            url = url.replace("#SONUM#", numStockSpe);
            url = url.replace("#I_BADGE_USER#", Global.mioBadge);


            String errorText = Global.checkHuAndOdpConsistency(cassone, odp);

            if (errorText != null) {
                Global.alert(getApplicationContext(), errorText);

                stop = true;
            } else {
                intent = new Intent(PrendiParametri.this, Esito.class);
                intent.putExtra("titolo", "Esito Impacchettare Materiale");
                intent.putExtra("info2", "Esito");
                intent.putExtra("backMenuLoop", "1");
                Log.d("URL", url);
                intent.putExtra("url", url);
            }
        } else if (menuItem.equals(Global.MENU_SPACCHETTAMENTO_MATERIALE)) {
            String cassone = getDescrizione("HU");
            String odp = getDescrizione("Odp");
            String materiale = getDescrizione("Materiale");
            String qta = getDescrizione("Qta");
            String um = getDescrizione("Unita Misura");
            String cdStockSpe = getDescrizione("Cd Stock Spec.");
            String numStockSpe = getDescrizione("Num Stock Spec.");

            String hu = getDescrizione("HU");
            if (hu != null && !"".equals(hu)) {
                if (!Global.isValidHuInput(hu)) {
                    Global.alert(this, "CAMPO HU IN FORMATO NON VALIDO");
                    stop = true;
                }
            }

            String url = Global.serverURL + Global.CASSONI_SPACCHETTA_MATERIALE;
            url = url.replace("#IV_EXIDV2#", cassone);
            url = url.replace("#IV_AUFNR#", odp);
            url = url.replace("#MATNR#", materiale);
            url = url.replace("#VEMNG#", qta);
            url = url.replace("#VEMEH#", um);
            url = url.replace("#SOBKZ#", cdStockSpe);
            url = url.replace("#SONUM#", numStockSpe);
            url = url.replace("#I_BADGE_USER#", Global.mioBadge);


            intent = new Intent(PrendiParametri.this, Esito.class);
            intent.putExtra("titolo", "Esito Spacchettare Materiale");
            intent.putExtra("info2", "Esito");
            intent.putExtra("backMenuLoop", "1");

            Log.d("URL", url);
            intent.putExtra("url", url);

        } else if (menuItem.equals(Global.MENU_CONSUMA_CASSONE)) {
            String cassone = getDescrizione("HU");
            String odp = getDescrizione("Odp");
            String materiale = getDescrizione("Materiale");
            String qta = getDescrizione("Qta");
            String um = getDescrizione("Unita Misura");
            String cdStockSpe = getDescrizione("Cd Stock Spec.");
            String numStockSpe = getDescrizione("Num Stock Spec.");

            /*
            String hu = getDescrizione("HU");
            if (hu != null && !"".equals(hu)) {
                if (!Global.isValidHuInput(hu)) {
                    Global.alert(this, "CAMPO HU IN FORMATO NON VALIDO");
                    stop = true;
                }
            }
            */

            String url = Global.serverURL + Global.CASSONI_WITHDRAWAL;
            url = url.replace("#IV_EXIDV2#", cassone);
            url = url.replace("#IV_AUFNR#", odp);
            url = url.replace("#MATNR#", materiale);
            url = url.replace("#VEMNG#", qta);
            url = url.replace("#VEMEH#", um);
            url = url.replace("#SOBKZ#", cdStockSpe);
            url = url.replace("#SONUM#", numStockSpe);
            url = url.replace("#I_BADGE_USER#", Global.mioBadge);


            intent = new Intent(PrendiParametri.this, Esito.class);
            intent.putExtra("titolo", "Esito Consuma HU");
            intent.putExtra("info2", "Esito");
            intent.putExtra("backMenuLoop", "1");

            Log.d("URL", url);
            intent.putExtra("url", url);
        } else if (menuItem.equals(Global.DOCUMENTA_GET_DRAW)) {
            String materiale = getDescrizione("Materiale");
            String tipo = getDescrizione("Tipo");

            boolean treD = "3D".equals(tipo);
            String path2D = "http://ws-dev.ancoragroup.com/Services/DocumentaProxy.aspx?GetDraw&codAn=" + materiale + "&ext=PNG16&dpi=300&orient=V";
            String path3D = "http://ws-dev.ancoragroup.com/Services/DocumentaProxy.aspx?Get3D&codice=" + materiale;

            Intent tempIntent = new Intent(Intent.ACTION_VIEW);

            Uri data = null;
            if (treD) {
                data = Uri.parse(path3D);
            } else {
                data = Uri.parse(path2D);
            }

            tempIntent.setDataAndType(data, "image/png");
            tempIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            //startActivity(tempIntent);
            //Eliminato perchè sembra esserci due volte

            stop = true;
        } else if (menuItem.equals(Global.CRONOLOGIA_CODICE)) {

            String serNR = getDescrizione("RFID"+0);

            intent = new Intent(PrendiParametri.this, History.class);
            intent.putExtra("RFID"+0, serNR);
            intent.putExtra("backMenuLoop", "1");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);



           /* String cassone = getDescrizione("HU");
            String odp = getDescrizione("Odp");
            String materiale = getDescrizione("Materiale");
            String qta = getDescrizione("Qta");
            String um = getDescrizione("Unita Misura");
            String cdStockSpe = getDescrizione("Cd Stock Spec.");
            String numStockSpe = getDescrizione("Num Stock Spec.");

            *//*
            String hu = getDescrizione("HU");
            if (hu != null && !"".equals(hu)) {
                if (!Global.isValidHuInput(hu)) {
                    Global.alert(this, "CAMPO HU IN FORMATO NON VALIDO");
                    stop = true;
                }
            }
            *//*

            String url = Global.serverURL + Global.CASSONI_WITHDRAWAL;
            url = url.replace("#IV_EXIDV2#", cassone);
            url = url.replace("#IV_AUFNR#", odp);
            url = url.replace("#MATNR#", materiale);
            url = url.replace("#VEMNG#", qta);
            url = url.replace("#VEMEH#", um);
            url = url.replace("#SOBKZ#", cdStockSpe);
            url = url.replace("#SONUM#", numStockSpe);

            intent = new Intent(PrendiParametri.this, Esito.class);
            intent.putExtra("titolo", "Esito Consuma HU");
            intent.putExtra("info2", "Esito");
            intent.putExtra("backMenuLoop", "1");

            Log.d("URL", url);
            intent.putExtra("url", url);*/
        } else {
            Global.alert(this,"Azione non gestita" );
            stop = true;
        }

        // se esiste l'intent lo esegue
        if (intent != null) {
            // prende i parametri di filtro
            for (int t = 0; t < grigliaViste.getCount(); t++) {
                String titolo = getTitolo(t);
                intent.putExtra(getTitolo(t), getDescrizione(t));
                if (menuItem.equals(Global.LISTA_PREL_PROD + "PASSO2")) {
                    if(getTitolo(t).equals("HU1")){
                        titolo = getTitolo(t).substring(0, getTitolo(t).indexOf("1"));
                    }
                    if(getTitolo(t).equals("HU2")){
                        titolo = getTitolo(t).substring(0, getTitolo(t).indexOf("2"));
                    }
                    if(getTitolo(t).equals("HU3")){
                        titolo = getTitolo(t).substring(0, getTitolo(t).indexOf("3"));
                    }
                }
                Global.insertCopyValues(titolo, getDescrizione(t));
                Log.d("TabellaValoriG", "Titolo: " + titolo + "\tDescrizione: " + getDescrizione(t));
            }

            if (stop) {
                bottoneAvanti.setEnabled(true);
            } else {
                startActivity(intent);
            }
        }
    }

    private void hideKeyboard() {

    }



    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            Bitmap mIcon11 = null;
            try {
                URL url = new URL(urls[0]);
                mIcon11 = BitmapFactory.decodeStream(url.openConnection().getInputStream());

            } catch (Exception e) {
                Log.e("ERRORE", e.getMessage());
            }

            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            bmImage.setVisibility(View.VISIBLE);
        }
    }

    public EditText getPrimo() {
        return primo;
    }

    protected void onDataReceived(final byte[] buffer, final int size) {

        runOnUiThread(new Runnable() {
            public void run() {
                EditText tv = getPrimo();
                if (tv != null) {
                    tv.append(new String(buffer, 0, size));
                    String temp = new String(buffer, 0, size);
                    if (temp != null) {
                        Global.toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                        bottoneAvantiClick(tv);
                    }
                }
            }
        });

    }


    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            while(!isInterrupted()) {
                int size;
                try {
                    byte[] buffer = new byte[64];
                    if (mInputStream == null) return;
                    size = mInputStream.read(buffer);
                    if (size > 0) {
                        onDataReceived(buffer, size);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (Global.getImpostazioniApp("BARCODE1D").equals("Yes")) {
            if (mReadThread != null)
                mReadThread.interrupt();
            mApplication.closeSerialPort();
            mSerialPort = null;
        }

        startFlag = false;
        runFlag = false;
        bottoneAzione2.setText("Inizia Lettura RFID");

        if (thread != null) {
            thread.interrupt();

            thread = null;
        }
        if (threadRpa520 != null) {
            threadRpa520.interrupt();

            threadRpa520 = null;
        }

        super.onDestroy();
    }
    @Override
    public void onBackPressed() {
        if (goBack) {
            if (thread != null) {
                thread.interrupt();

                thread = null;
            }
            if (threadRpa520 != null) {
                threadRpa520.interrupt();

                threadRpa520 = null;
            }
            super.onBackPressed();
            goBack = false;
        }
    }

    @Override
    public void bottoneAzione3Click (View v) {
        openInfoStock(true, myIntent.getStringExtra("matnr"));
    }


    public String getUrlConfermaTest() {
        String url = "";
        url = Global.serverURL + Global.checkConfirmXML;
        url = url.replaceAll("#I_BADGE_USER#", Global.mioBadge);
        url = url.replaceAll("#I_AUFNR#", "");
        url = url.replaceAll("#I_ANFME#", getDescrizione("Quantita"));
        url = url.replaceAll("#I_RUECK#", getDescrizione("Conferma") );


        Log.d("URL", url);

        return url;
    }

    public String getUrlPrelievoTest() {
        String url = "";
        url = Global.serverURL + Global.checkMatSerial;

        url = url.replaceAll("#I_MATNR#", getDescrizione("Materiale"));
        url = url.replaceAll("#I_WERKS#",Global.getImpostazoniSAP("Divisione"));
        url = url.replaceAll("#I_LGNUM#",getDescrizione("Numero Mag."));
        Log.d("URL", url);

        return url;
    }

    public ArrayList<HashMap<String, String>> getListaValori(int quantita) {
        ArrayList<HashMap<String, String>> temp = new ArrayList<>();

        temp.add(putListaValori("Conferma", getDescrizione("Conferma"), "READONLY"));

        for (int i=1; i<=quantita; i++) {
            temp.add(putListaValori("RFID"+i, "", "RFID"));
        }
        return temp;
    }

    public ArrayList<HashMap<String, String>> getListaValoriPrelievo(int quantita) {
        ArrayList<HashMap<String, String>> temp = new ArrayList<>();

        String quantitaStockPrel = getDescrizione("Stock - QtaPrel");

        String qtSceltaTot = String.valueOf(quantita) + " /";
        if (quantitaStockPrel.contains("-")) {
            qtSceltaTot += quantitaStockPrel.substring(quantitaStockPrel.lastIndexOf("-") + 1);
        }

        temp.add(putListaValori("Da Ubic", getDescrizione("Da Ubic"), "READONLY"));
        temp.add(putListaValori("A Ubic", getDescrizione("A Ubic"), "READONLY"));
        temp.add(putListaValori("Materiale", getDescrizione("Materiale"), "READONLY"));
        temp.add(putListaValori("Descrizione",getDescrizione("Descrizione"), "READONLY"));

        temp.add(putListaValori("Qta scelta", qtSceltaTot, "READONLY"));



        for (int i=1; i<=quantita; i++) {
            temp.add(putListaValori("RFID"+i, "", "RFID"));
        }

        temp.add(putListaValori("Quantita",getDescrizione("Quantita"), "HIDDEN"));
        temp.add(putListaValori("ODPNR", getDescrizione("ODPNR"), "HIDDEN"));
        temp.add(putListaValori("Conferma", getDescrizione("Conferma"), "HIDDEN"));
        temp.add(putListaValori("Stock - QtaPrel", getDescrizione("Stock - QtaPrel"), "HIDDEN"));


        return temp;
    }

    public ArrayList<HashMap<String, String>> getListaValoriVendita(int quantita) {
        ArrayList<HashMap<String, String>> temp = new ArrayList<>();

        temp.add(putListaValori("Conferma", getDescrizione("Conferma"), "READONLY"));

        for (int i=1; i<=quantita; i++) {
            temp.add(putListaValori("RFID"+i, "", "RFID"));
        }
        return temp;
    }



    public boolean writeEPC(String writeData) {
        boolean temp = false;

        return temp;
    }

    public String getRFID(int quantita) {
        String temp = "";

        for (int i=1; i<=quantita; i++) {
            temp += getDescrizione("RFID"+i);
            if (!getDescrizione("RFID"+i).endsWith(":")) {
                temp += ":";
            }
            temp += "|";
        }
        return temp;
    }


    ///RFID//////
    private void addToList(final List<EPC> list, final String epc) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // The epc for the first time
                if (list.isEmpty()) {
                    EPC epcTag = new EPC();
                    epcTag.setEpc(epc);
                    epcTag.setCount(1);
                    list.add(epcTag);
                    listepc.add(epc);
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        EPC mEPC = list.get(i);
                        // list contain this epc
                        if (epc.equals(mEPC.getEpc())) {
                            mEPC.setCount(mEPC.getCount() + 1);
                            list.set(i, mEPC);
                            break;
                        } else if (i == (list.size() - 1)) {
                            // list doesn't contain this epc
                            EPC newEPC = new EPC();
                            newEPC.setEpc(epc);
                            newEPC.setCount(1);
                            list.add(newEPC);
                            listepc.add(epc);
                        }
                    }
                }

                Collections.sort(listepc);

                boolean hasRFIDField = false;
                String focusField = null;
                for (int t = 0; t< grigliaViste.getCount(); t++) {
                    String name = getTitolo(t);
                    if (getTitolo(t).equalsIgnoreCase(name)) {
                        View v = grigliaViste.getChildAt(t);
                        if (v == null) {
                            continue;
                        }

                        EditText descrizione = (EditText) v.findViewById(R.id.descrizione);
                        if (descrizione == null) {
                            continue;
                        }

                        if (descrizione instanceof EditText) {
                            if (descrizione.hasFocus()) {
                                focusField = name;
                            }
                            if (name.startsWith("RFID")) {
                                hasRFIDField = true;
                            }
                        }
                    }
                }

                // Se il campo inizia con RFID compilo tutti i campi con gli RFID
                if (hasRFIDField) {
                    for (int i=0; i<listepc.size(); i++) {
                        setDescrizione("RFID"+(i+1), listepc.get(i));
                    }
                } else {
                    // Se sto mettendo l'RFID su un campo focus, alla fine svuoto la lista così se sparo nuovamente
                    // Un RFID lo sovrascrive
                    for (int i=0; i<listepc.size(); i++) {
                        setDescrizione(focusField, listepc.get(i));
                    }

                    listepc = new ArrayList<>();
                    listEPC = new ArrayList<>();
                }



            }
        });
    }

    /**
     * Inventory EPC Thread - Legge l'RFID
     */
    class InventoryThread extends Thread {
        private List<byte[]> epcList;

        @Override
        public void run() {
            super.run();
            while (runFlag) {
                if (startFlag) {
                    // manager.stopInventoryMulti()

                    epcList = manager.inventoryRealTime(); // inventory real time
                    if (epcList != null && !epcList.isEmpty()) {
                        // play sound
                        Util.play(1, 1);
                        for (byte[] epc : epcList) {
                            String epcStr = Tools.Bytes2HexString(epc, epc.length);

                            processEpcRead(epcStr);
                        }
                    }
                    epcList = null;
                    try {
                        Thread.sleep(40);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    public void LoopReadEPCdRPA520() {
        threadRpa520 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (startFlag) {
                    Log.d("RFID","Start thread");
                    uhf_6c.inventory(callback);
                    if (!startFlag) {
                        Log.d("RFID","Fine thread");
                        break;
                    }
                    try {
                        Thread.sleep(150);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;

                    }
                }
            }
        });
        threadRpa520.start();
    }

    // Lettura RFID
    IUhfCallback callback = new IUhfCallback.Stub() {
        @Override
        public void doInventory(List<String> str) throws RemoteException {
            // for (Iterator it2 = str.iterator(); it2.hasNext();)
            Log.d("dqw", "count111=" + str.size());
            allcount += str.size();
            Log.d("dqw00000007", "count111=" + allcount);

            for (int i = 0; i < str.size(); i++) {
                String epcStr = (String) str.get(i);
                if (epcStr.length() > 6) {
                    epcStr = epcStr.substring(6);
                }
                Log.d("RFID", epcStr);

                processEpcRead(epcStr);
            }

        }

        @Override
        public void doTIDAndEPC(List<String> str) throws RemoteException {  }

    };

    public void processEpcRead(String epcStr) {
        if (epcStr.startsWith("ACA0000")) {
            epcStr = epcStr.replaceAll("ACA0000", "A");
            addToList(listEPC, epcStr);
            Log.d("RFID", epcStr);
            Util.play(1, 0);
        } else if (epcStr.startsWith("201")) {
            epcStr = epcStr.replaceAll("ACA0000", "A");
            addToList(listEPC, epcStr);
            Log.d("RFID", epcStr);
            Util.play(1, 0);
        } else {
            if (menuItem.equalsIgnoreCase(Global.TESTRFID)) {
                epcStr = epcStr.replaceAll("ACA0000", "A");
                addToList(listEPC, epcStr);
            }
            Log.d("RFID", epcStr);
            Util.play(2, 0);

        }
    }
}