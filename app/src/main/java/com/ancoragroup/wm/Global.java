package com.ancoragroup.wm;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


// 99999
    // 12410 altro badge
//
public class Global {

    enum Manufactorer {
        NONE("NONE"),
        VH71T("VH-71T"),
        RPA520("Android Handheld Terminal");

        private String name;

        private Manufactorer(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static Manufactorer getByName(String str) {
            for (Manufactorer man : Manufactorer.values()) {
                if (man.getName().equals(str)) {
                    return man;
                }
            }
            return NONE;
        }
    }

    //  Copiati da Logic.cs

    public static final String  I_MODE = "N";          //!< indica modalità Non interattiva
    public static final String I_UPDATE = "S";        //!< indica aggiornamento Sincrono
    public static final String  I_CONTR = "H";         //!< indica controllo delle videate background
    public static final String I_SQUIT = "X";         //!< indica la conferma della posizione OT
    public static final String I_NLTYP = "998";       //!< ubicazione di destinazione nei mancanti BEM
    public static final String I_NLPLA = "TRASFPROD"; //!< ubicazione di destinazione nei mancanti BEM
    public static final String I_LGORT = "W110";      //!< gruppo magazzini di default
    public static final char I_PAGNO = '1';           //!< pagina iniziale
    public static final String I_DESCRIPTION = "X";   //!< indica che voglio recuperare la descrizione
    public static final String I_CODE = "03";         //!< codice per movimento merci BAPI
    public static final String I_MOV262 = "";        //!< codice per movimento 262
    public static final String I_MOVSTORNO = "X";     //!< codice per movimento storno
    public static final String I_QTA_NULLA = "0";     //!< codice per movimento storno
    public static final String I_VUOTO = "";          //!< stringa valore nulllo
    public static final String I_SETTATO = "X";       //!< stringa valore settato
    public static final String I_MONTH_AGO = "-1";    //!< indica i mesi passati di validità dell'inventario

    // Tempo da aspettare prima di poter cliccare nuovamente un pulsante (dove previsto)
    public static final int BUTTON_CLICK_MS_WAIT = 1 * 1000;
    // Abilita o meno la funzionalità che evita il doppio "clic" sul bottone (dove previsto)
    public static final boolean BUTTON_WAIT_FEATURE = true;

    public static boolean autoLogin = false;

    public static final int SVILUPPO = 0;
    public static final int PRODUZIONE = 1;

    public static int typeRun = PRODUZIONE;

    public static String serverPRODURL = "http://ws.ancoragroup.com/Services/FMProxy.aspx?";
    public static String serverDEVURL = "http://ws-dev.ancoragroup.com/Services/FMProxy.aspx?";


    public static String serverDOCUMENTAPRODURL = "http://ws.ancoragroup.com/Services/DocumentaProxy.aspx?";
    public static String serverDOCUMENTADEVURL = "http://ws-dev.ancoragroup.com/Services/DocumentaProxy.aspx?";


    public static String get2D = "GetDraw&ext=PNG16&dpi=300&orient=V&codAn=";
    public static String get3D = "Get3D&codice=";

    public static String serverURL = serverDEVURL;
    public static String serverDOCUMENTAURL = serverDOCUMENTADEVURL;

    public static String loginXML = "Z_WK_USER_LOGIN&Badge=";
    public static String menuXML = "Z_WK_GET_MENU&menu=";
    public static String zmagXML = "Z_WK_ZMAG&cod=#cod#&desc=#desc#&todp=#todp#&odp=#odp#&div=#div#&mag=#mag#&nmag=#nmag#&stock=#stock#&tubic=#tubic#&ubic=#ubic#&cassone=#cassone#";
    //public static String zmagXML = "Z_WK_ZMAG&cod=#cod#&mag=#mag#&stock=#stock#&div=#div#";
    public static String checkConfirmXML = "Z_WK_CONFIRM_PROD_ORDER_NEW&I_BADGE_USER=#I_BADGE_USER#&I_AUFNR=#I_AUFNR#&I_RUECK=#I_RUECK#&I_ANFME=#I_ANFME#&I_GETDATA=X";
    public static String confirmXML = "Z_WK_CONFIRM_PROD_ORDER_NEW&I_BADGE_USER=#I_BADGE_USER#&I_AUFNR=#I_AUFNR#&I_RUECK=#I_RUECK#&I_ANFME=#I_ANFME#&I_GETDATA=";
    public static String setSerialConfirmXML = "Z_WK_CONFIRM_PROD_ORDER_NEW&I_BADGE_USER=#I_BADGE_USER#&I_AUFNR=#I_AUFNR#&I_RUECK=#I_RUECK#&I_ANFME=#I_ANFME#&I_GETDATA=&T_SERNR=#T_SERNR#";
    public static String checkMatSerial = "Z_WK_CHECK_MATERIAL&I_MATNR=#I_MATNR#&I_WERKS=#I_WERKS#&I_LGNUM=#I_LGNUM#";

    public static String checkMatDocUXML="Z_WK_CHECK_MATERIAL_DOCU&I_LGNUM=#I_LGNUM#&I_MJAHR=#I_MJAHR#&I_MBLNR=#I_MBLNR#&I_ZEILE=#I_ZEILE#";
    public static String checkMatDocXML="Z_WK_CHECK_MATERIAL_DOC&I_LGNUM=#I_LGNUM#&I_MJAHR=#I_MJAHR#&I_MBLNR=#I_MBLNR#&I_ZEILE=#I_ZEILE#";
    public static String trasfMancantiXML="Z_WK_TRASF_MANCANTI_BEM_ZMAG&I_BADGE_USER=#I_BADGE_USER#&I_LGNUM=#I_LGNUM#&I_MJAHR=#I_MJAHR#&I_MBLNR=#I_MBLNR#&I_ZEILE=#I_ZEILE#&I_QTA=#I_QTA#" +
            "&I_NLTYP=#I_NLTYP#&I_NLPLA=#I_NLPLA#&I_MODE=#I_MODE#&I_UPDATE=#I_UPDATE#&I_WDATU=#I_WDATU#";

    public static String listaPrelVenditaXML="Z_WK_GET_PICKING_LIST&I_BADGE=#I_BADGE#&I_LGNUM=#I_LGNUM#&I_WORKSTATION=#I_WORKSTATION#&I_PAGNO=#I_PAGNO#&I_OBJTYPE=#I_OBJTYPE#";
    public static String listaPrelVenditaItemXML="Z_WK_GET_PICKING_ITEM&I_LGNUM=#I_LGNUM#&I_REFNR=#I_REFNR#&I_WORKSTATION=#I_WORKSTATION#&I_PAGNO=#I_PAGNO#&I_KQUIT=#I_KQUIT#&I_PQUIT=#I_PQUIT#&I_OBJTYPE=#I_OBJTYPE#";
   // public static String ListaPrelVenditaConfirmXML = "Z_WK_CONFIRM_OT&I_MODE=#I_MODE#&I_UPDATE=#I_UPDATE#&I_BADGE_USER=#I_BADGE_USER#&I_LGNUM=#I_LGNUM#&I_QTA_CONF=#I_QTA_CONF#&I_CNFTYPE=#I_CNFTYPE#&I_OBJTYPE=#I_OBJTYPE#&I_REFNR=#I_REFNR#&I_TANUM=#I_TANUM#&I_TAPOS=#I_TAPOS#&I_MATNR=#I_MATNR#&I_VLTYP=#I_VLTYP#&I_VLPLA=#I_VLPLA#&I_NLTYP=#I_NLTYP#&I_NLPLA=#I_NLPLA#&I_RETT=#I_RETT#&I_TIPO=#I_TIPO#";

    public static String listaPrelProdXML="Z_WK_GET_PICKING_LIST&I_BADGE=#I_BADGE#&I_LGNUM=#I_LGNUM#&I_WORKSTATION=#I_WORKSTATION#&I_PAGNO=#I_PAGNO#&I_OBJTYPE=#I_OBJTYPE#";
    public static String listaPrelProdItemXML="Z_WK_GET_PICKING_ITEM&I_LGNUM=#I_LGNUM#&I_REFNR=#I_REFNR#&I_WORKSTATION=#I_WORKSTATION#&I_PAGNO=#I_PAGNO#&I_KQUIT=#I_KQUIT#&I_PQUIT=#I_PQUIT#&I_OBJTYPE=#I_OBJTYPE#";
    public static String listaPrelConfirmXML = "Z_WK_CONFIRM_OT&I_MODE=#I_MODE#&I_UPDATE=#I_UPDATE#&I_BADGE_USER=#I_BADGE_USER#&I_LGNUM=#I_LGNUM#&I_QTA_CONF=#I_QTA_CONF#&I_CNFTYPE=#I_CNFTYPE#&I_OBJTYPE=#I_OBJTYPE#&I_REFNR=#I_REFNR#&I_TANUM=#I_TANUM#&I_TAPOS=#I_TAPOS#&I_MATNR=#I_MATNR#&I_VLTYP=#I_VLTYP#&I_VLPLA=#I_VLPLA#&I_NLTYP=#I_NLTYP#&I_NLPLA=#I_NLPLA#&I_RETT=#I_RETT#&I_TIPO=#I_TIPO#&I_NLENR=#I_NLENR#&T_SERNR=#T_SERNR#&I_EXIDV2=#I_EXIDV2#";
    public static String trasferimento262="Z_WK_GOODSMVT_CREATE_262&I_AUFNR=#I_AUFNR#&I_MATNR=#I_MATNR#&I_QTA_STORNO=#I_QTA_STORNO#&I_QTA_262=#I_QTA_262#&I_NLTYP=#I_NLTYP#&I_NLPLA=#I_NLPLA#&I_NLENR=#I_NLENR#&I_LGNUM=#I_LGNUM#&I_VLTYP=#I_VLTYP#&I_VLPLA=#I_VLPLA#&I_WERKS=#I_WERKS#&I_LGORT=#I_LGORT#&I_MOV262=#I_MOV262#&I_WDATU=#I_WDATU#&I_MEINS=#I_MEINS#&I_SOBKZ=#I_SOBKZ#&I_SONUM=#I_SONUM#&I_BADGE_USER=#I_BADGE_USER#";

    public static String leggiDataXML = "Z_WK_GET_DATETIME";
    public static String leggiLT24 = "Z_TALEND_READ_TABLE&IV_QUERY_TABLE=LTAP&IV_DELIMITER=&IV_NO_DATA=&IV_ROWSKIPS=&IV_ROWCOUNT=&IV_WHERE_CLAUSE=QDATU_GE_'#DATA#'#MATR##VLTYP##VLPLA##NLPLA##NLTYP#&IT_FIELDS=";


    //public static String leggiCassoni = "Z_TALEND_READ_TABLE&IV_QUERY_TABLE=LQUA&IV_DELIMITER=&IV_NO_DATA=&IV_ROWSKIPS=&IV_ROWCOUNT=&IV_WHERE_CLAUSE=LENUM_EQ_'#LENUM#'&IT_FIELDS=";
    public static String leggiCassoni = "ZMAG_HU_LIST&IV_EXIDV2=#LENUM#";

    public static String leggiCassoniModula = "Z_TALEND_READ_TABLE&IV_QUERY_TABLE=LQUA&IV_DELIMITER=&IV_NO_DATA=&IV_ROWSKIPS=&IV_ROWCOUNT=&IV_WHERE_CLAUSE=GESME_GT_0_AND_LENUM_LIKE_'%25#POSIZIONE#'&IT_FIELDS=";
    public static String leggiMustCassoni = "Z_TALEND_READ_TABLE&IV_QUERY_TABLE=ZMAG_MAGTEMP";

    public static String consumaCassone = "";
    public static String elabSeriali = "Z_WK_EQUI_ELAB&I_FUNCT=#I_FUNCT#&I_SERNR=#I_SERNR#&I_SERNR_INF=#I_SERNR_INF#";

    public static String spostaCassone = "ZMAG_UPDATE_ZMAGLEIN&P_LENUM=#P_LENUM#&P_LGNUM=#P_LGNUM#&P_LGTYP=#P_LGTYP#&P_LGPLA=#P_LGPLA#&P_TANUM=#P_TANUM#&P_TAPOS=#P_TAPOS#&P_ALGTYP=#P_ALGTYP#&P_ALGPLA=#P_ALGPLA#";
    // DEPRECATA, vedere CASSONI_GET_LIST
    //public static String elencoCassoni = "Z_TALEND_READ_TABLE&IV_QUERY_TABLE=ZMAGLEIN&IV_DELIMITER=&IV_NO_DATA=&IV_ROWSKIPS=&IV_ROWCOUNT=&IV_WHERE_CLAUSE=#WHERE#&IT_FIELDS=";
    public static String leggiMustAree = "Z_TALEND_READ_TABLE&IV_QUERY_TABLE=LAGP&IV_DELIMITER=&IV_NO_DATA=&IV_ROWSKIPS=&IV_ROWCOUNT=&IV_WHERE_CLAUSE=LGBER_EQ_'000'_AND_LGTYP_EQ_'STK'&IT_FIELDS=";

    public static String leggiCronologiaCodice = "Z_WK_SERNR_HISTORY&IV_SERNR=#IV_SERNR#";


    // NUOVE CHIAMATE CASSONI 25/07/2018
    public static final String CASSONI_IMPACCHETTA_MATERIALE = "ZMAG_HU_PACKING" +
            "&IV_EXIDV2=#IV_EXIDV2#" +
            "&IV_AUFNR=#IV_AUFNR#" +
            "&IV_WERKS=D110" +
            "&IV_LGNUM=110" +
            "&IV_LGORT=W110" +
            "&IV_UMLGO=WH10" +
            "&IV_LGTYP=923" +
            "&IV_PACK_MAT=CASSONE" +
            "&MATNR=#MATNR#" +
            "&VEMNG=#VEMNG#" +
            "&VEMEH=#VEMEH#" +
            "&SOBKZ=#SOBKZ#" +
            "&SONUM=#SONUM#" +
            "&I_BADGE_USER=#I_BADGE_USER#";

    //Passare il I_BADGE_USER utente --


    public static final String CASSONI_SPACCHETTA_MATERIALE = "ZMAG_HU_UNPACKING" +
            "&IV_EXIDV2=#IV_EXIDV2#" +
            "&IV_AUFNR=#IV_AUFNR#" +
            "&IV_WERKS=D110" +
            "&IV_LGNUM=110" +
            "&IV_LGORT=W110" +
            "&IV_UMLGO=WH10" +
            "&IV_LGTYP=923" +
            "&IV_PACK_MAT=CASSONE" +
            "&MATNR=#MATNR#" +
            "&VEMNG=#VEMNG#" +
            "&VEMEH=#VEMEH#" +
            "&SOBKZ=#SOBKZ#" +
            "&SONUM=#SONUM#" +
            "&I_BADGE_USER=#I_BADGE_USER#";
    //Passare il I_BADGE_USER utente --


    public static final String CASSONI_SPOSTAMENTO_MATERIALE = "ZMAG_HU_REPACKING" +
            "&IV_EXIDV2_O=#IV_EXIDV2_O#" +
            "&IV_EXIDV2_D=#IV_EXIDV2_D#" +
            "&IV_AUFNR_O=#IV_AUFNR_O#" +
            "&IV_AUFNR_D=#IV_AUFNR_D#" +
            "&IV_WERKS=D110" +
            "&IV_LGNUM=110" +
            "&IV_LGORT=W110" +
            "&IV_UMLGO=WH10" +
            "&IV_LGTYP=923" +
            "&IV_PACK_MAT=CASSONE" +
            "&MATNR=#MATNR#" +
            "&VEMNG=#VEMNG#" +
            "&VEMEH=#VEMEH#" +
            "&SOBKZ=#SOBKZ#" +
            "&SONUM=#SONUM#" +
            "&I_BADGE_USER=#I_BADGE_USER#";
    //Passare il I_BADGE_USER utente

    public static final String CASSONI_WITHDRAWAL = "ZMAG_HU_WITHDRAWAL" +
            "&IV_EXIDV2=#IV_EXIDV2#" +
            "&IV_AUFNR=#IV_AUFNR#" +
            "&IV_EXTID=X" +
            "&IV_TOTAL=" +
            "&IV_OVERCONSUMPTION=" +
            "&IV_WERKS=D110" +
            "&IV_LGORT=W110"+
            "&MATNR=#MATNR#" +
            "&VEMNG=#VEMNG#" +
            "&VEMEH=#VEMEH#" +
            "&SOBKZ=#SOBKZ#" +
            "&SONUM=#SONUM#" +
            "&I_BADGE_USER=#I_BADGE_USER#";
    //Passare il I_BADGE_USER utente

    public static String LT24_TRASFERIMENTO_NEW = "ZMAG_HU_MOVE_SINGLE" +
            "&I_AUFNR=#I_AUFNR#" +
            "&I_MATNR=#I_MATNR#" +
            "&I_MENGE=#I_MENGE#" +          //ex I_QTA_262
            "&I_NLTYP=#I_NLTYP#" +
            "&I_NLPLA=#I_NLPLA#" +
            //"&I_NLENR=#I_NLENR#" +        TOLTO
            "&I_LGNUM=#I_LGNUM#" +
            "&I_VLTYP=#I_VLTYP#" +
            "&I_VLPLA=#I_VLPLA#" +
            "&I_WERKS=#I_WERKS#" +
            "&I_LGORT=#I_LGORT#" +
            //"&I_MOV262=#I_MOV262#" +      TOLTO
            "&I_WDATU=#I_WDATU#" +
            "&I_MEINS=#I_MEINS#" +
            "&I_SOBKZ=#I_SOBKZ#" +
            "&I_SONUM=#I_SONUM#" +
            "&I_EXIDV2_DA=#I_EXIDV2_DA#" +
            "&I_EXIDV2_A=#I_EXIDV2_A#" +
            "&I_ZMAG=" +
            "&I_IN=#I_IN#" +            // SPOSTA IN -> 'X', SPOSTA DA -> ''
            "&I_BADGE_USER=#I_BADGE_USER#";

    public static final String CASSONI_SPOSTAMENTO_CASSONI_UBIC = "ZMAG_HU_MOVE&IV_EXIDV2=#LENUM#&IV_LGTYP=#IV_LGTYP#&IV_LGPLA=#IV_LGPLA#&IV_BWLVS=999&I_BADGE_USER=#I_BADGE_USER#";         //Passare il I_BADGE_USER utente

    public static final String CASSONI_LEGGI_MATERIALE = "ZMAG_HU_LIST&IV_EXIDV2=#LENUM#";
    public static final String CASSONI_LEGGI_UBICAZIONE = "ZMAG_HU_GET_STORAGE&IV_EXIDV2=#LENUM#&IV_AUFNR=#IV_AUFNR#&IV_EXTID=X";
    public static final String CASSONI_GET_LIST = "ZMAG_HU_GET_LIST&IT_EXIDV2=#IT_EXIDV2#&IT_MATNR=#IT_MATNR#&IT_AUFNR=#IT_AUFNR#&I_GET_GIAC=";

    static Hashtable<String, String> labelCampi = new Hashtable<>();

    public static boolean nascondiTop = false;

    public static SharedPreferences impostazioniApp;

    public static SharedPreferences preferencesCopyBtnSavedValues = null;
    public static int MAX_COPIED_VALUES = 10;

    public static String mioBadge = "";

    public static ArrayList<HashMap<String, String>> impostazioniSAP = new ArrayList<>();


    public static Intent savedIntent = null;
    public static Bundle savedIntentBundle = null;

    public static ArrayList<Intent> savedIntentList = new ArrayList<>();
    public static ArrayList<Bundle> savedIntentBundleList = new ArrayList<>();
    public static ArrayList<ArrayList<Integer>> savedCheckBoxes = new ArrayList<>();

    public static double VALORE_INGRANDIMENTO_CAMPI_GRANDE = 1.6;

    public static int calculateFontDimension(int fontSize) {
        return (int)(fontSize * VALORE_INGRANDIMENTO_CAMPI_GRANDE);
    }
    // Menu caricato al login
    public static HashMap<String, ArrayList<HashMap<String, String>>> menuList = new HashMap<>();

    public static HashMap<String, String> translations = new HashMap<>();

    public static HashMap<String, Boolean> rfidToConfirm = new HashMap<>();

    public static void loadTranslations() {
        translations = new HashMap<>();

        translations.put("ST", "NR");
    }

    public static String getTranslation(String s) {
        return translations.get(s) != null ? translations.get(s) : s;
    }

    public static boolean saveIntentStatus(Intent intent, Bundle bundle) {
        return saveIntentStatus(intent, bundle, null);
    }
    public static boolean saveIntentStatus(Intent intent, Bundle bundle, ArrayList<Integer> checkboxChecked) {
        savedIntent = intent;
        savedIntentBundle = bundle;


        return deleteOrSaveIntent(intent, bundle, checkboxChecked);
    }

    public static boolean deleteOrSaveIntent(Intent intentToSave, Bundle bundleToSave, ArrayList<Integer> checkboxChecked) {
        boolean saved = true;
        String titolo = intentToSave.getStringExtra("titolo");
        if (titolo == null) {
            titolo = "";
        }

        for (int i = savedIntentList.size() - 1; i >= 0; i--) {
            Intent intent = savedIntentList.get(i);
            String title = intent.getStringExtra("titolo");
            if (titolo.equalsIgnoreCase(title)) {
                savedIntentList.remove(i);

                savedCheckBoxes.remove(i);

                saved = false;
                break;
            }
        }

        if (saved) {
            savedIntentList.add(intentToSave);
            savedIntentBundleList.add(bundleToSave);
            savedCheckBoxes.add(checkboxChecked);
        }

        return saved;
    }
    public static boolean launchSavedIntent(Context c) {
        boolean started = false;
        if (savedIntent != null) {
            Intent intent = new Intent(savedIntent);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (savedIntentBundle != null) {
                intent.putExtras(savedIntentBundle);
            }

            c.startActivity(savedIntent);
            started = true;
        }

        return started;

    }

    public static String getImpostazoniSAP (String titolo) {
        String temp = "";
        for (HashMap<String, String> g: impostazioniSAP) {
            if (g.get("Titolo").equalsIgnoreCase(titolo) ) temp = g.get("Descrizione");
        }
        return temp;
    }

    public static void setImpostazioniSAP (String titolo, String descrizione) {
        String temp = "";
        for (HashMap<String, String> g: impostazioniSAP) {
            if (g.get("Titolo").equalsIgnoreCase(titolo) ) {
                g.put("Descrizione", descrizione);
            }
        }
    }
    public static void removeImpostazioniApp(String campo) {
        SharedPreferences.Editor editor = impostazioniApp.edit();
        editor.remove(campo);
        editor.commit();
    }

    public static void setImpostazioniApp (String campo, String valore) {
        SharedPreferences.Editor editor = impostazioniApp.edit();
        editor.putString(campo, valore);
        editor.commit();
    }

    public static boolean isUnSet (String campo) {
        boolean set = true;

        if (Global.impostazioniApp != null) {
            if (Global.impostazioniApp.getString(campo, null) != null) {
                set = false;
            }
        }
        return set;
    }


    public static String getImpostazioniApp (String campo) {

        String temp = "";
        if (Global.impostazioniApp!=null) {
            if (Global.impostazioniApp.getString(campo, null) != null) {
                temp = Global.impostazioniApp.getString(campo, null);
            }
        }

        return temp;
    }

    public static ArrayList<HashMap<String, String>> getImpostazioniAppHash () {
        ArrayList<HashMap<String, String>> temp = new ArrayList<>();

        Map<String, ?> entries = impostazioniApp.getAll();
        Set<String> keys = entries.keySet();
        for (String key : keys) {
            String valore = impostazioniApp.getString(key, "");
            String dato = (valore.equals("SI")?"SI":"NO") + ";NO;SI";
            temp.add(putListaValori(key, dato , "BOOL"));
        }

        return temp;
    }

    public static boolean isSamsung() {
        return "SI".equals(getImpostazioniApp("SAMSUNG"));
    }

    public static boolean isChrome() {
        return "SI".equals(getImpostazioniApp("CHROME"));
    }

    // Toast rapido perchè non ricordo mai la sintassi :-(
    public static void alert(Context c, String testo) {
        //Toast.makeText(c , testo, Toast.LENGTH_SHORT).show();

        /*TextView text;
        Toast toast = Toast.makeText(c, testo, Toast.LENGTH_SHORT);
        View view = toast.getView();
        text = (TextView) view.findViewById(android.R.id.message);
        text.setShadowLayer(5,5,5,5);
        view.setBackgroundResource(R.color.colorAccent);
        toast.show();*/

        Toast toast = Toast.makeText(c, testo, Toast.LENGTH_SHORT);
        toast.getView().setPadding(20, 20, 20, 20);
        toast.getView().setBackgroundResource(R.drawable.background_toast);
        TextView text = (TextView) toast.getView().findViewById(android.R.id.message);
        text.setTextColor(Color.WHITE);
        text.setTextSize(28);
        toast.show();
    }
    // Toast rapido perchè non ricordo mai la sintassi :-(
    public static void show(Context c, View vg) {

        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(c);

        alertDialog2.setView(vg);
        alertDialog2.show();
    }

    // crea le hashmap per le griglia
    public static HashMap<String, String>  putListaValori(String titolo, String descrizione) {
      return    putListaValori( titolo, descrizione, "TEXT");
    }

    public static HashMap<String, String>  putListaValori(String titolo, String descrizione, String tipo) {
        return    putListaValori( titolo, descrizione, tipo, false, true);
    }

    public static HashMap<String, String>  putListaValori(String titolo, String descrizione, String tipo, boolean mandatory) {
        return    putListaValori( titolo, descrizione, tipo, mandatory, true);
    }

    public static HashMap<String, String>  putListaValori(String titolo, String descrizione, String tipo, boolean mandatory, boolean enabled) {
        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put("Titolo",titolo);
        hashMap.put("Descrizione", descrizione);
        hashMap.put("Tipo", tipo);
        hashMap.put("Mandatory", String.valueOf(mandatory));
        hashMap.put("Stato", String.valueOf(enabled));

        return hashMap;

    }

    public static  SimpleDateFormat extDATE = new SimpleDateFormat("dd/MM/yyyy");
    public static SimpleDateFormat norDATE = new SimpleDateFormat("yyyyMMdd");

    // da richiamare per i WS di appoggio che non ritorano griglie di dati
    public static ArrayList<HashMap<String, String>> getValoriXML(String url, LinkedHashMap<String, String> campi) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Log.d("URL", "Global.getValoriXML");
        ArrayList<HashMap<String, String>> listaValori = new ArrayList<>();

        try {

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(2 * 60 * 1000);
            connection.setReadTimeout(2 * 60 * 1000);

            Log.d("URL", url);

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            Log.d("URL",  "1");
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Log.d("URL",  "2");
            Document doc = dBuilder.parse(connection.getInputStream());
            Log.d("URL", "3");

            String type = doc.getElementsByTagName("TYPE").item(0).getTextContent();

            Log.d("URL", "4");

            if (type != null && type.equals("E")) {
                NodeList message = doc.getElementsByTagName("MESSAGE");
                listaValori.add(putListaValori("ERRORE",message.item(0).getTextContent()));
                Log.d("URL", "ERRORE: " + message.toString());
            } else {
                HashMap<String, String> hashMap = new HashMap<>();
                Log.d("URL", "OK");

                if (campi!=null){
                    for (String chiave : campi.keySet()) {
                        Log.d("URL", " -->" + chiave);
                        hashMap.put(chiave, doc.getElementsByTagName(chiave).item(0).getTextContent());
                        Log.d("URL", " -->" + doc.getElementsByTagName(chiave).item(0).getTextContent());
                    }
                } else {
                    // altrimenti li prende tutti
                    /*NodeList nodeList = eElement.getChildNodes();
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        Node currentNode = nodeList.item(i);
                        if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                            //calls this method for all the children which is Element
                            //currentNode
                        }
                    }*/
                }
                listaValori.add(hashMap);
            }

        } catch (Exception e) {
            listaValori.add(putListaValori("ERRORE",e.toString()));
            Log.d("ERRORE:", e.toString());
        }

        return listaValori;
    }

    // da richiamare per i WS di appoggio che non ritorano griglie di dati
    public static Vector<String> getValoreXML(String url, String... campi) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Log.d("URL", "Global.getValoreXML");
        Vector<String> listaValori = new Vector<String>();

        try {

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(2 * 60 * 1000);
            connection.setReadTimeout(2 * 60 * 1000);

            Log.d("URL", url);

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document doc = dBuilder.parse(connection.getInputStream());
            String type = doc.getElementsByTagName("TYPE").item(0).getTextContent();

            if (type != null && type.equals("E")) {
                NodeList message = doc.getElementsByTagName("MESSAGE");
            } else {
                for (String campo: campi) {
                    listaValori.add(doc.getElementsByTagName(campo).item(0).getTextContent());
                    Log.d("Mustcassoni:", doc.getElementsByTagName(campo).item(0).getTextContent());
                }
            }

        } catch (Exception e) {
            Log.d("ERRORE:", e.toString());
        }

        return listaValori;
    }


        /*public static String getValoreListaValori(ArrayList<HashMap<String, String>> listaValori , String campo) {
            String temp = "";
            for (int t = 0; t < listaValori.size(); t++) {
                HashMap<String, String> item =  listaValori.get(t);
                temp = item.get(campo);
            }
            return temp;
        }*/

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static String dataSAP = norDATE.format(new Date());


    public static float prendiFloat(String valore) {
        float totale = 0.0F;
        try {
            if (valore.contains(",") || (valore.contains(".") && StringUtils.countMatches(valore, ".") == 1)) {
                totale = Float.parseFloat(valore.replaceAll(",","."));
            }
        } catch (Exception e ) {
            totale = 0;
        }
        return totale;
    }

    final static String MANCANTI_DA_ZMAG = "ANC0000120";
    final static String MESSA_IN_UBICAZIONE_DA_BEM = "MU00000010";
    final static String LISTA_PREL_PROD = "UP00000220";
    final static String LISTA_PREL_VEND = "UP00000130";
    final static String INFO_STOCK = "ZM00000010";
    final static String INFO_STOCK_MATERIALE = "LS00000010";
    final static String INFO_STOCK_UBICAZIONE = "LS00000020";
    final static String INFO_STOCK_ODP = "ZM00000020";
    final static String TRASF_TRA_UBICAZIONE = "TR00000010";
    final static String AVANZAVA_FASE = "EM00000050";
    final static String LT24 = "TR00000011";
    final static String DAMODULAACASSONE = "TR00000013";
    final static String CRONOLOGIA_CODICE = "HI00000001";


    final static String DISTINTAMACCHINA = "TR00000014";

    final static String ELENCOCASSONI = "CA00000001";
    final static String SPOSTACASSONE = "CA00000002";
    final static String VERIFICACASSONE = "CA00000003";

    final static String MENU_DOVE_CASSONE = "CA00000004";
    final static String MENU_IMPACCHETTAMENTO_MATERIALE = "CA00000005";
    final static String MENU_SPACCHETTAMENTO_MATERIALE = "CA00000006";
    final static String MENU_REIMPACCHETTAMENTO_MATERIALE = "CA00000007";
    final static String MENU_CONSUMA_CASSONE = "CA00000008";


    final static String TESTRFID = "TESTRFID";

    final static String DOCUMENTA_GET_DRAW = "DOCUMENTA_GET_DRAW";

    public static int backMenuLoop = 0;
    public static boolean backMenu = false;
    public static boolean disabledItem = false;


    public static boolean isNumericBella(String s) {
        return s != null && s.matches("[-+]?\\d*\\.?\\d+");
    }

    public static final boolean isNumeric(final String s) {
        if (s == null || s.isEmpty()) return false;
        for (int x = 0; x < s.length(); x++) {
            final char c = s.charAt(x);
            if (x == 0 && (c == '-')) continue;  // negative
            if ((c == '.')) continue;  // separatori
            if ((c == ',')) continue;  // separatori
            if ((c >= '0') && (c <= '9')) continue;  // 0 - 9
            return false; // invalid
        }
        return true; // valid
    }

    public static ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);


    // converte una stringa che contiene un numero da formato visivo a formato SAP
    public static String fromWStoSAP(String valore) {
        // toglie i separatori di migliaia
        valore = valore.replaceAll("\\.","");
        // le virgole diventano punti
        valore = valore.replaceAll(",","\\.");
        return valore;
    }

    // converte una stringa che contiene un numero da formato SAP a formato visivo
    public static String fromSAPtoWS(String valore) {
        // le virgole diventano punti
        valore = valore.replaceAll("\\.",",");
        return valore;
    }

    public static int getNumericI(String str)
    {
        str = str.replaceAll(",",".");
        int d;
        try
        {    float f = Float.parseFloat(str);
            d = Math.round(f);
        }
        catch(NumberFormatException nfe) {
            return 0;
        }
        return d;
    }

    public static float getFloat(String str)
    {
        float f = 0;
        str = str.replaceAll(",",".");
        try
        {
            f = Float.parseFloat(str);
        }
        catch(NumberFormatException nfe) {
            return f;
        }
        return f;
    }

    public static String zapZero(String str)
    {
        if (str==null) return  "0";
        String temp  = str;
        temp = temp.replaceAll(",000","");
        temp = temp.replaceAll("\\.000","");
        return temp;
    }

    /*
    public static HashMap<String, Vector<String>> listaCopia = new HashMap<String, Vector<String>>();

    public static void addValoreListaCopia(String campo, String valore) {
        Vector<String> valori = listaCopia.get(campo);
        if (valori==null) {
            valori = new Vector<String>();
            listaCopia.put(campo, valori);
        }
        valori.add(0, valore);
        for (int t = valori.size(); t>MAX_COPIED_VALUES; t--) {
            valori.remove(t-1);
        }

    }
    */

    public static String dpi = "mdpi";

    public static String getDPI(float density) {
        dpi = String.valueOf(density);

        if (density == 0.75) dpi = "ldpi";
        else if (density ==1.0) dpi = "mdpi";
        else if (density ==1.5) dpi = "hdpi";
        else if (density ==2.0) dpi = "xhdpi";
        else if (density ==3.0) dpi = "xxhdpi";
        else if (density ==4.0) dpi = "xxxhdpi";

        return dpi;
    }


    public static Vector<String> fieldBold = new Vector<String>();
    public static Vector<String> cassoni = new Vector<String>();
    public static Vector<String> aree = new Vector<String>();


    public static void setBold() {
        fieldBold.add("MATNR");
        fieldBold.add("VLPLA");
        fieldBold.add("VLTYP");
        fieldBold.add("NLPLA");
        fieldBold.add("NLTYP");
        fieldBold.add("LGTYP");
        fieldBold.add("LGPLA");
        fieldBold.add("NLENR");
        fieldBold.add("HU");
        fieldBold.add("CASSONI");
        fieldBold.add("LENUM");
        fieldBold.add("AREE");
    }

    public static void setCassoni() {
        cassoni = getValoreXML(serverURL + leggiMustCassoni, "VLTYP", "NLTYP");

    }

    public static boolean mustCassoni(String ubic) {
        boolean temp = false;

        for (String elem: Global.cassoni) {
            if (ubic.startsWith(elem)) temp = true;
        }

        return  temp;
    }


    public static void setAree() {
        aree = getValoreXML(serverURL + leggiMustAree, "LGPLA", "LGPLA");

    }

    public static boolean mustArea(String area) {
        boolean temp = false;

        for (String elem: Global.aree) {
            if (area.startsWith(elem.trim())) temp = true;
        }

        return  temp;
    }


    public static ArrayList<HashMap<String, String>> getListaValoriRFID(int quantita) {
        ArrayList<HashMap<String, String>> temp = new ArrayList<>();
        temp.add(putListaValori("RFID"+0, "", ""));
        for (int i=1; i<=quantita; i++) {
            temp.add(putListaValori("RFID"+i, "", "READONLY"));
        }
        return temp;
    }

    public static ArrayList<HashMap<String, String>> getValoriDocumentaGetDraw() {
        ArrayList<HashMap<String, String>> temp = new ArrayList<>();

        temp.add(putListaValori("Materiale", "", "", true));
        temp.add(putListaValori("Tipo", "2D;2D;3D", "RADIO"));

        return temp;
    }

    public static void setLabels() {
        labelCampi.put("WERKS","Divisione");
        labelCampi.put("NLTYP","Tip");
        labelCampi.put("NLPLA","Ubic");
        labelCampi.put("VLPLA","Ubic");
        labelCampi.put("REFNR","Numero OT");

        labelCampi.put("ALTME", "UM");
        labelCampi.put("DTIP","DTip");
        labelCampi.put("DUBIC", "DUbic");
        labelCampi.put("GESME", "Giac");
        labelCampi.put("VBELN", "Numero OT");
        labelCampi.put("VLTYP", "Tip");
        labelCampi.put("VSOLM_C", "Qta");
        labelCampi.put("MATNR", "Materiale");
        labelCampi.put("MAKTX", "Descrizione");
        labelCampi.put("REFNT", "Descrizione");

        labelCampi.put("LGNUM", "Num. Mag.");
        labelCampi.put("LGTYP", "Tipo Mag.");
        labelCampi.put("LGPLA", "Ubicazione");
        labelCampi.put("CASSONI", "HU");
        labelCampi.put("AREE", "Area");

        labelCampi.put("LOG", "Icona");
        labelCampi.put("VERME", "Stock Disp.");
        labelCampi.put("PLNBEZ", "Materiale");
        labelCampi.put("AUFNR", "Ordine");
        labelCampi.put("ZCODMAT", "Mat. Cliente");
        labelCampi.put("LGORT", "Gruppo Mag.");
        labelCampi.put("ENMNG", "Qtà Prelevata");
        labelCampi.put("TEXT", "Testo");

        labelCampi.put("SOBKZ", "Cod. Stock Spe.");
        labelCampi.put("SONUM", "Num. Stock Spe.");
        labelCampi.put("AUSME", "Stock da Prel.");
        labelCampi.put("EINME", "Stock in Imm.");
        labelCampi.put("LSONR", "Stock Spe.");
        labelCampi.put("PRVBE", "Area Approvig.");
        labelCampi.put("MENGE", "Qtà Sospesa FT");
        labelCampi.put("FTMENGE", "Qtà FT");
        labelCampi.put("FTOFMEN", "Qtà FT Sospeso");
        labelCampi.put("BDMNG", "Qtà OdP Aperta");
        labelCampi.put("RLOG", "Icona2");
        labelCampi.put("RTEXT", "Testo2");
        labelCampi.put("LOG_QTY", "Icona3");
        labelCampi.put("MBLNR", "Num. Doc. Mat.");
        labelCampi.put("MJAHR", "Esercizio");
        labelCampi.put("MEINS", "Unità di misura");
        labelCampi.put("NAME1", "Cli/For");
        labelCampi.put("KTEXT", "Descr. Mat. Odp");
        labelCampi.put("EDATU", "DataEM");
        labelCampi.put("WENUM", "NrEM");
        labelCampi.put("BKTXT", "Zona");

        labelCampi.put("SERNR", "Seriale");
        labelCampi.put("LENUM", "HU");
        labelCampi.put("ALGPLA", "Area");

		labelCampi.put("QNAME", "Utente");
		labelCampi.put("BNAME", "Utente");
		labelCampi.put("ENAME", "Utente");
		labelCampi.put("NLENR", "HU");

		labelCampi.put("TANUM", "OT");

		labelCampi.put("QZEIT", "Ora");
        labelCampi.put("QDATU", "Data");

		labelCampi.put("VSOLA", "QtaProv");

		labelCampi.put("NISTM", "QtaDest");

		labelCampi.put("BENUM", "N.Fabb");

		labelCampi.put("RIMAMENTI", "Quantita");

		labelCampi.put("VEMNG", "Qta");
		labelCampi.put("VEMEH", "VEMEH");
		labelCampi.put("VARKEY", "VARKEY");

        labelCampi.put("EV_EXIDV2", "HU");
        labelCampi.put("EXIDV2", "HU");
        labelCampi.put("EV_EXIDV", "EV_EXIDV");
        labelCampi.put("EV_LGTYP", "Tipo Mag");
        labelCampi.put("EV_LGPLA", "Area");
        labelCampi.put("EV_LGNUM", "Num. Mag");
        labelCampi.put("EV_AUFNR", "Odp");
    }

    public static String getLabel(String campo) {
        String valore = labelCampi.get(campo);
        if (valore==null) valore = campo;
        return valore;
    }

    public static ArrayList<String> readPossibilityValues(String odp, Context context) {
        ArrayList<String> values = new ArrayList<>();
        String url = "";
        ProgressDialog progress;
        progress = new ProgressDialog(context);
        progress.setTitle("Caricamento");
        progress.setMax(0);
        progress.setCancelable(false);
        url = Global.serverURL + Global.CASSONI_GET_LIST;


        LinkedHashMap<String, FieldItem> campi = new LinkedHashMap<String, FieldItem>();

        // Se ho specificato ODP ci metto uno 0 davanti altrimenti non trova niente
        //String odp = myIntent.getStringExtra("Odp");


        //url = url.replace("#IT_EXIDV2#", HU);
        url = url.replace("#IT_EXIDV2#", "");
        url = url.replace("#IT_MATNR#", "");
        url = url.replace("#IT_AUFNR#", odp);

        Log.d("URLCASSONI", url);

        if (!"NO_URL_CALL".equalsIgnoreCase(url)) {
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) new URL(url).openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                connection.setRequestMethod("GET");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            connection.setConnectTimeout(2 * 60 * 1000);
            connection.setReadTimeout(2 * 60 * 1000);

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = null;
            try {
                dBuilder = dbFactory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
            Document doc = null;
            try {
                doc = dBuilder.parse(connection.getInputStream());
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Element element = doc.getDocumentElement();

            String type = doc.getElementsByTagName("TYPE").item(0).getTextContent();
            NodeList message = doc.getElementsByTagName("MESSAGE");

                // ATTENZIONE! Aggiunto da Alessandro Veneziano 26/07/2018
                // Escludo il nodo ET_RETURN perchè daniele nelle nuove chiamate per i cassoni mi ha aggiunto questa tabella che è fuori standard rispetto alle altre chiamate
                // ET_RETURN contiene <XmlRow> e quindi anche se ho 0 record, ne legge sempre 1 che è il ritorno
                // Rimuovo il nodo in modo da non leggere quelle righe come record
                NodeList nodiDaRimuovere = doc.getElementsByTagName("ET_RETURN");
                for (int i = 0; i < nodiDaRimuovere.getLength(); i++) {
                    element.removeChild(nodiDaRimuovere.item(i));
                }
                // Fine modifica Alessandro

                NodeList nodi = doc.getElementsByTagName("XmlRow");
                progress.setMax(nodi.getLength());
                Log.d("NODESIZE", ""+nodi.getLength());
                Log.d("SAP COUNT:", String.valueOf(nodi.getLength()));

                for (int temp = 0; temp < nodi.getLength(); temp++) {

                    Log.i("--->", String.valueOf(temp));
                    //publishProgress(temp);
                    Node nNode = nodi.item(temp);


                    HashMap<String, String> hashMap;
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        String chiave = "EXIDV2";
                        String chiave2 = "LGTYP";
                        String chiave3 = "LGPLA";

                        hashMap = new HashMap<>();
                        Log.d("SAP LOOP:", String.valueOf(temp));
                        String value = "";
                            NodeList nl = eElement.getElementsByTagName(chiave);
                            if (nl != null) {
                                if (nl.item(0) == null) {
                                    Log.d("XML MANCANTE:", chiave);
                                } else {
                                    String valore = nl.item(0).getTextContent();
                                    Log.d("TESTVALORE", valore);
                                    value = valore;
                                }

                                NodeList n2 = eElement.getElementsByTagName(chiave2);
                                if (n2 != null) {
                                    if (n2.item(0) == null) {
                                        Log.d("XML MANCANTE:", chiave2);
                                    } else {

                                        String valore = n2.item(0).getTextContent();
                                        Log.d("TESTVALORE", valore);
                                        if (valore.equals("STK")) {
                                            value = value + " " + valore;
                                        }
                                    }
                                }

                                NodeList n3 = eElement.getElementsByTagName(chiave3);
                                if (n3 != null) {
                                    if (n3.item(0) == null) {
                                        Log.d("XML MANCANTE:", chiave3);
                                    } else {
                                        String valore = n3.item(0).getTextContent();
                                        Log.d("TESTVALORE", valore);
                                        if (n2.item(0).getTextContent().equals("STK")) {
                                            value = value + " " + valore;
                                        }
                                    }
                                }

                                values.add(value);

                            } else {
                                hashMap.put(chiave, "");
                            }
                        }

                        progress.setProgress(temp + 1);
                    }

                }

        for(int i = values.size() - 1; i >= 0; i--) {
            if (!values.get(i).startsWith("HU")) {
                values.remove(i);
            }
        }

        values.size();

        return values;
    }








    // Leggo i valori del campo input compilato (Tasto C di fianco ad ogni campo)
    // Il formato del file è [chiave]=[valore],[valore],...
    public static ArrayList<String> readCopiedValues(String key) {
        ArrayList<String> values = new ArrayList<>();

        if(key.equals("Dest.")) {
            values.add("MODMODULA");
            values.add("LAVSEMILAV");
        }
        if (preferencesCopyBtnSavedValues != null) {
            String valuesWithComma = preferencesCopyBtnSavedValues.getString(key, null);

            if (valuesWithComma != null) {
                if (!valuesWithComma.endsWith(",")) {
                    valuesWithComma += ",";
                }

                values.addAll(Arrays.asList(valuesWithComma.split(",")));
            }

        }
        return values;
    }



    // Salvo sul dispositivo il valore del campo compilato (tasto C di fianco ad ogni campo)
    // Il formato del file è [chiave]=[valore],[valore],...
    public static void insertCopyValues(String key, String valueToSave) {
        // non memorizza i campi letti dagli RFID perchè sono fuorvianti
        if (key.startsWith("RFID")) return;

        ArrayList<String> currentValuesSaved = readCopiedValues(key);

        // Se è vuoto oppure è già contenuto non inserisco
        if (valueToSave.equals("") || currentValuesSaved.contains(valueToSave)) {
            return;
        }

        currentValuesSaved.add(0, valueToSave);

        for (int i = currentValuesSaved.size(); i > MAX_COPIED_VALUES; i--) {
            currentValuesSaved.remove(i - 1);
        }

        StringBuilder valueSeparatedByComma = new StringBuilder();
        for (String value : currentValuesSaved) {
            valueSeparatedByComma.append(value + ",");
        }

        SharedPreferences.Editor editor = preferencesCopyBtnSavedValues.edit();
        editor.putString(key, valueSeparatedByComma.toString());
        editor.commit();
    }

    public static void insertArrayCopyValues(String key, ArrayList<String> valuesToSave) {
        // non memorizza i campi letti dagli RFID perchè sono fuorvianti
        if (key.startsWith("RFID")) return;

        if(valuesToSave != null) {

            ArrayList<String> currentValuesSaved = readCopiedValues(key);

            for (int j = 0; j < valuesToSave.size(); j++) {
                // Se è vuoto oppure è già contenuto non inserisco
                if (valuesToSave.get(j).equals("") || currentValuesSaved.contains(valuesToSave.get(j))) {
                    return;
                }

                currentValuesSaved.add(0, valuesToSave.get(j));

                for (int i = currentValuesSaved.size(); i > MAX_COPIED_VALUES; i--) {
                    currentValuesSaved.remove(i - 1);
                }

                StringBuilder valueSeparatedByComma = new StringBuilder();
                for (String value : currentValuesSaved) {
                    valueSeparatedByComma.append(value + ",");
                }

                SharedPreferences.Editor editor = preferencesCopyBtnSavedValues.edit();
                editor.putString(key, valueSeparatedByComma.toString());
                editor.commit();
            }
        }
    }

    public static ArrayList<HashMap<String, String>> getMenu(String menu) {
        ArrayList<HashMap<String, String>> menuToShow = menuList.get(menu);
        if (menuToShow == null) {
            menuToShow = new ArrayList<HashMap<String, String>>();
        }

        return menuToShow;
    }
    public static void loadMenu(String menuName) {
        try {
            String url = Global.serverURL + Global.menuXML + menuName;
            Log.d("URL",url);

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(2 * 60 * 1000);
            connection.setReadTimeout(2 * 60 * 1000);

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(connection.getInputStream());

            NodeList listTavole = doc.getElementsByTagName("XmlRow");

            for (int temp = 0; temp < listTavole.getLength(); temp++) {
                Log.i("--->", String.valueOf(temp));
                Node nNode = listTavole.item(temp);
                HashMap<String,String> hashMap;
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    hashMap=new HashMap<>();
                    String tipoItem = eElement.getElementsByTagName("TIPOOP").item(0).getTextContent();
                    String titolo = eElement.getElementsByTagName("DESCR").item(0).getTextContent();
                    String figlio = eElement.getElementsByTagName("FIGLIO").item(0).getTextContent();

                    hashMap.put("Titolo", titolo);
                    hashMap.put("Descrizione",figlio);
                    hashMap.put("Tipo",tipoItem);

                    ArrayList<HashMap<String, String>> menu = menuList.get(menuName);
                    if (menu == null) {
                        menu = new ArrayList<>();
                    }
                    menu.add(hashMap);
                    menuList.put(menuName, menu);

                    if (tipoItem.equalsIgnoreCase("M")) {
                        loadMenu(figlio);
                    }

                }
            }

        } catch (Exception e) {

        }
    }

    public static ArrayList<HashMap<String, String>> getCampiReimpacchettamento() {
        return getCampiReimpacchettamento(null);
    }

    public static ArrayList<HashMap<String, String>> getCampiReimpacchettamento(HashMap<String, String> lista) {
        boolean readOnly = (lista != null);

        String fromCassone = (lista != null ? lista.get("HU") : "");
        String fromOdp = (lista != null ? lista.get("AUFNR") : "");
        String matnr = (lista != null ? lista.get("MATNR") : "");
        String um = (lista != null ? lista.get("VEMEH") : "");
        String qta = (lista != null ? lista.get("VEMNG") : "");
        if (qta.contains(",")) {
            qta = qta.substring(0, qta.lastIndexOf(","));
        }
        //qta = qta.replaceAll("\\.",",").replaceAll(",0","");

        ArrayList<HashMap<String, String>> temp = new ArrayList<>();
        temp.add(putListaValori("Da HU", fromCassone, readOnly ? "READONLY" : "", true));
        temp.add(putListaValori("Da Odp", fromOdp, readOnly ? "READONLY" : "", true));
        temp.add(putListaValori("A HU", "", "", true));
        temp.add(putListaValori("A Odp", fromOdp, "", true));
        temp.add(putListaValori("Materiale", matnr, readOnly ? "READONLY" : "", true));
        temp.add(putListaValori("Qta", qta, "", true));
        temp.add(putListaValori("Unita Misura", um, readOnly ? "READONLY" : "", true));
        temp.add(putListaValori("Cd Stock Spec.", "", "", false));
        temp.add(putListaValori("Num Stock Spec.", "", "", false));

        return temp;
    }

    public static ArrayList<HashMap<String, String>> getCampiImpacchettamento() {
        return getCampiImpacchettamento(null);
    }
    public static ArrayList<HashMap<String, String>> getCampiImpacchettamento(HashMap<String, String> lista) {
        ArrayList<HashMap<String, String>> temp = new ArrayList<>();

        boolean readOnly = (lista != null);

        String cassone = (lista != null ? lista.get("HU") : "");
        String odp = (lista != null ? lista.get("AUFNR") : "");
        String matnr = (lista != null ? lista.get("MATNR") : "");
        String um = (lista != null ? lista.get("VEMEH") : "");
        String qta = (lista != null ? lista.get("VEMNG") : "");
        if (qta.contains(",")) {
            qta = qta.substring(0, qta.lastIndexOf(","));
        }

        temp.add(putListaValori("HU", cassone, readOnly ? "READONLY" : "", true));
        temp.add(putListaValori("Odp", odp, readOnly ? "READONLY" : "", true));
        temp.add(putListaValori("Materiale", matnr, readOnly ? "READONLY" : "", true));
        temp.add(putListaValori("Qta", "", "", true));
        temp.add(putListaValori("Unita Misura", um, readOnly ? "READONLY" : "", false));
        temp.add(putListaValori("Cd Stock Speciale", "", "", false));
        temp.add(putListaValori("Numero Stock Speciale", "", "", false));

        return temp;
    }

    public static ArrayList<HashMap<String, String>> getCampiSpacchettamento() {
        return getCampiSpacchettamento(null);
    }
    public static ArrayList<HashMap<String, String>> getCampiSpacchettamento(HashMap<String, String> lista) {
        ArrayList<HashMap<String, String>> temp = new ArrayList<>();

        boolean readOnly = (lista != null);

        String cassone = (lista != null ? lista.get("HU") : "");
        String odp = (lista != null ? lista.get("AUFNR") : "");
        String matnr = (lista != null ? lista.get("MATNR") : "");
        String um = (lista != null ? lista.get("VEMEH") : "");
        String qta = (lista != null ? lista.get("VEMNG") : "");
        if (qta.contains(",")) {
            qta = qta.substring(0, qta.lastIndexOf(","));
        }

        temp.add(putListaValori("HU", cassone, readOnly ? "READONLY" : "", true));
        temp.add(putListaValori("Odp", odp, readOnly ? "READONLY" : "", true));
        temp.add(putListaValori("Materiale", matnr, readOnly ? "READONLY" : "", true));
        temp.add(putListaValori("Qta", "", "", true));
        temp.add(putListaValori("Unita Misura", um, readOnly ? "READONLY" : "", false));
        temp.add(putListaValori("Cd Stock Speciale", "", "", false));
        temp.add(putListaValori("Numero Stock Speciale", "", "", false));

        return temp;
    }

    public static ArrayList<HashMap<String, String>> getCampiConsumaCassone() {
        return getCampiConsumaCassone(null);
    }
    public static ArrayList<HashMap<String, String>> getCampiConsumaCassone(HashMap<String, String> lista) {
        ArrayList<HashMap<String, String>> temp = new ArrayList<>();

        boolean readOnly = (lista != null);

        String cassone = (lista != null ? lista.get("HU") : "");
        String odp = (lista != null ? lista.get("AUFNR") : "");
        String matnr = (lista != null ? lista.get("MATNR") : "");
        String um = (lista != null ? lista.get("VEMEH") : "");
        String qta = (lista != null ? lista.get("VEMNG") : "");

        if (qta.endsWith(",00")) {
            qta = qta.substring(0, qta.lastIndexOf(","));
        }

        /*
        if (qta.contains(",")) {
            qta = qta.substring(0, qta.lastIndexOf(","));
        }
        */

        temp.add(putListaValori("HU", cassone, readOnly ? "READONLY" : "", true));
        temp.add(putListaValori("Odp", odp, readOnly ? "READONLY" : "", true));
        temp.add(putListaValori("Materiale", matnr, readOnly ? "READONLY" : "", true));
        temp.add(putListaValori("Qta", qta, "", true));
        temp.add(putListaValori("Unita Misura", um, readOnly ? "READONLY" : "", false));
        temp.add(putListaValori("Cd Stock Speciale", "", "", false));
        temp.add(putListaValori("Numero Stock Speciale", "", "", false));

        return temp;
    }


    public static String checkHuAndOdpConsistency(String cassone, String odpNr) {
        String errorText = null;


        String url = serverURL + Global.CASSONI_LEGGI_UBICAZIONE;
        url = url.replace("#LENUM#", cassone);
        url = url.replace("#IV_AUFNR#", odpNr);


        ArrayList<HashMap<String, String>> rows = getValoriXML(url, new LinkedHashMap<String, String>());

        for (HashMap<String, String> row  : rows) {
            String title = row.get("Titolo");

            if (title != null && title.equals("ERRORE")) {
                errorText = row.get("Descrizione");
            }
        }
        Log.d("TestURLUBIC", url);
        return errorText;
    }

    public static boolean isValidHuInput(String valore) {
        if (valore.equals("")) {
            return true;
        }
        return (valore.toUpperCase().matches("HU[a-zA-Z]{1}[0-9]{4}"));
    }
}

