package com.ancoragroup.wm;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ancoragroup.utils.ButtonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Pattern;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

/**
 * Created by ovalenti on 11/08/2017.
 */

public class OmarActivity extends AppCompatActivity {

    boolean goBack = false;

    ProgressDialog progress;
    ArrayList<HashMap<String, String>> listaValori = new ArrayList<>();
    GridView grigliaViste;
    String[] da = {"Titolo", "Descrizione"};//string array
    int[] a = {R.id.titolo, R.id.descrizione};//int array of views id's
    String menu;

    Button bottoneIndietro;
    Button bottoneAzione1;
    Button bottoneAzione2;
    Button bottoneAzione3;
    Button bottoneAvanti;

    ActionBar ab;
    Intent myIntent;
    int layoutXML = R.layout.activity_omar;
    String titolo = "[TITOLO NON IMPOSTATO]";
    boolean stop = false;

    TextView labelInfo1;
    TextView labelInfo2;
    TextView labelInfo3;
    TextView labelInfo4;

    String menuItem = "";

    String info2 = "---";
    String info3 = "---";
    String info4 = "---";

    public void setLayoutXML(int layoutXML) {
        this.layoutXML = layoutXML;
    }

    MenuItem barcode1d;

    EditText descrizioneCopia;

    boolean refresh = true;


    final static String FONT_APP = "sans-serif-condensed";

    // Imposta il bottone con immagine di sfondo documenta
    public void impostaBottoneDocumenta(Button btn) {
        ButtonUtils.getInstance(this.getApplicationContext()).impostaBottoneSfondoImmagine(btn, R.drawable.documenta);
    }

    // Imposta il bottone come azione su singola riga
    public void impostaBottoneSelezioneSingolaRiga(Button btn, String text) {
        ButtonUtils.getInstance(this.getApplicationContext()).setButtonProperties(btn, text, R.color.colorPrimary, R.color.white, R.string.button_type_single);
    }

    // Imposta il bottone indietro
    public void impostaBottoneIndietro(Button btn, String text) {
        ButtonUtils.getInstance(this.getApplicationContext()).setButtonProperties(btn, text, R.color.colorERROR, R.color.white, -1);
    }

    // Imposta il bottone come azione su righe multiple (checkbox)
    public void impostaBottoneSelezioneMultipleRighe(Button btn, String text) {
        ButtonUtils.getInstance(this.getApplicationContext()).setButtonProperties(btn, text, R.color.colorOK, R.color.black, R.string.button_type_multiple);
    }

    public void impostaBottoneInvisibile(Button btn) {
        ButtonUtils.getInstance(this.getApplicationContext()).impostaBottoneInvisibile(btn);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutXML);

        myIntent = getIntent();
        titolo = myIntent.getStringExtra("titolo");
        info2 = myIntent.getStringExtra("info2");
        info3 = myIntent.getStringExtra("info3");
        info4 = myIntent.getStringExtra("info4");
        Log.d("DESCRIZIONECOPIA", "Menu item: " + myIntent.getStringExtra("menuItem"));

        if( myIntent.getStringExtra("menuItem") != null) {
            menuItem = myIntent.getStringExtra("menuItem");
        }

        // imposta la progess bar
        progress=new ProgressDialog(OmarActivity.this);
        progress.setMax(0);
        progress.setTitle("Caricamento");
        progress.setMessage("");

        //progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(false);

        // imposta i pulsanti di navigazione
        bottoneIndietro = (Button) findViewById(R.id.bottoneIndietro);

        bottoneAzione1 = (Button) findViewById(R.id.bottoneAzione1);
        if (bottoneAzione1!=null) {
            // long press su documenta
            bottoneAzione1.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    bottoneAzione1ClickLong(v);
                    return true;
                }
            });
        }
        bottoneAzione2 = (Button) findViewById(R.id.bottoneAzione2);
        bottoneAzione3 = (Button) findViewById(R.id.bottoneAzione3);
        bottoneAvanti = (Button) findViewById(R.id.bottoneAvanti);

        if (bottoneAvanti!=null) {
            bottoneAvanti.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    bottoneAvantiClickLong(v);
                    return true;
                }
            });
        }

        // label della toolbar
        labelInfo1 = ((TextView) findViewById(R.id.label_info1));
        labelInfo2 = ((TextView) findViewById(R.id.label_info2));
        labelInfo3 = ((TextView) findViewById(R.id.label_info3));
        //labelInfo4 = ((TextView) findViewById(R.id.label_info4));


        grigliaViste = (GridView) findViewById(R.id.listview);
        // se esiste la grigliaValori allora ne gestisce le colonne
        if (grigliaViste !=null ) {
            if (getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT)
                grigliaViste.setNumColumns(1);
            else grigliaViste.setNumColumns(2);
        }

        // Always cast your custom Toolbar here, and set it as the ActionBar.
        Toolbar tb = (Toolbar) findViewById(R.id.my_toolbar);
        tb.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetector.onTouchEvent(motionEvent);
                return true;
            }

            private GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(OmarActivity.this);
                    builderSingle.setIcon(R.drawable.ic_launcher);

                    builderSingle.setTitle("SELEZIONA PAGINA SALVATA");

                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(OmarActivity.this, android.R.layout.select_dialog_singlechoice);

                    for (Intent intent : Global.savedIntentList) {
                        String titolo = intent.getStringExtra("titolo");
                        arrayAdapter.add(titolo);
                    }

                    builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });

                    builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which>-1) {
                                Intent intent = new Intent(Global.savedIntentList.get(which));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                Bundle bundle = Global.savedIntentBundleList.get(which);

                                if (bundle != null) {
                                    intent.putExtras(bundle);
                                }

                                intent.putIntegerArrayListExtra("savedPageCheckBoxes", Global.savedCheckBoxes.get(which));

                                getApplicationContext().startActivity(intent);
                            }
                        }
                    });
                    builderSingle.show();

                    return super.onDoubleTap(e);
                }
            });

        });
        setSupportActionBar(tb);
        ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(false); // show or hide the default home button
        //ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)
        ab.setDisplayUseLogoEnabled(false);
        ab.setDisplayShowHomeEnabled(true);
        ab.setIcon(R.drawable.ic_launcher);

        // se il menu è da nascondere
        if (Global.nascondiTop) ab.hide();

        updateToolBar();
    }

    public void updateToolBar() {
        if (titolo!=null) labelInfo1.setText(titolo);
        if (info2!=null) labelInfo2.setText(info2);
        if (info3!=null) labelInfo3.setText(info3);
        if (info4!=null) labelInfo4.setText(info4);

        if (Global.typeRun==Global.SVILUPPO) ab.setBackgroundDrawable(getResources().getDrawable(R.color.colorAccent));
        else ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_splash));

    }

    @Override
    protected void onResume() {
        super.onResume(); // Always call the superclass method first
        updateToolBar();

        if (Global.backMenu) {
            if (Global.backMenuLoop==0) {
                Global.backMenu = false;
                if (getLocalClassName().equals("PrendiParametri") && titolo!=null && titolo.equals("3 Info Stock")) {
                    bottoneAvantiClick(new TextView(this));
                    //Global.alert(this, "PERCHE'?");
                }
            } else {
                Global.backMenuLoop--;
                customBackPressed();
            }
        }

        try {
            if (bottoneIndietro.getText().toString().equals(""))
                bottoneIndietro.setVisibility(View.INVISIBLE);
            if (bottoneAzione1.getText().toString().equals(""))
                bottoneAzione1.setVisibility(View.INVISIBLE);
            if (bottoneAzione2.getText().toString().equals(""))
                bottoneAzione2.setVisibility(View.INVISIBLE);
            if (bottoneAzione3.getText().toString().equals(""))
                bottoneAzione3.setVisibility(View.INVISIBLE);
            if (bottoneAvanti.getText().toString().equals(""))
                bottoneAvanti.setVisibility(View.INVISIBLE);
        } catch (Exception e) {

        }
    }
    public void customBackPressed() {
        goBack = true;
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (goBack) {
            super.onBackPressed();
            goBack = false;
        }
    }
    // CREAZIONE DEL MENU CONTESTUALE A DESTRA
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getLocalClassName().equals("SapMenu") || getLocalClassName().equals("MainActivity")) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.toolmenu, menu);

            return true;
        } else  {
            return false;
        }

    }

    // GESTIONE DEL MENU NELLA TOOLBAR
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        //Impostazioni SAP che corrispondono all'icona "dell'omino"
        if (id == R.id.item_settings_sap) {
            Intent intent = new Intent(this, ImpostazioniSAP.class);
            startActivity(intent);
            id = 0; //Strano
        }

        if (id == R.id.item_about) {

            Intent intent = new Intent(this, About.class);
            startActivity(intent);
        }

        if (id == R.id.item_release) {

            Intent intent = new Intent(this, VersioniActivity.class);
            startActivity(intent);
        }

        if (id == R.id.item_settings_app) {

            Intent intent = new Intent(this, ImpostazioniApp.class);
            startActivity(intent);
        }

        if (id == R.id.item_aggiorna) {
            Uri uri = Uri.parse("http://ws.ancoragroup.com/APK/WM.apk");
            Intent browserIntent = new Intent(Intent.ACTION_VIEW).setData(uri);
            startActivity(browserIntent);
        }

        if (id == R.id.item_download) {
            Uri uri = Uri.parse("http://ws.ancoragroup.com/APK/");
            Intent browserIntent = new Intent(Intent.ACTION_VIEW).setData(uri);
            startActivity(browserIntent);
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.item_exit) {
            finish();
        }

        if (id == R.id.item_settings_sap) {
            Intent intent = new Intent(this, ImpostazioniSAP.class);
            startActivity(intent);
        }

        if (id == R.id.item_test_rfid) {
            Intent intent = new Intent(this, PrendiParametri.class);
            intent.putExtra("titolo", "Leggi RFID");
            intent.putExtra("menuItem", Global.TESTRFID);
            intent.putExtra("listaValori", Global.getListaValoriRFID(30));
            startActivity(intent);
        }

        if (id == R.id.item_documenta) {
            Intent intent = new Intent(this, PrendiParametri.class);
            intent.putExtra("titolo", "Disegno documenta");
            intent.putExtra("menuItem", Global.DOCUMENTA_GET_DRAW);
            intent.putExtra("listaValori", Global.getValoriDocumentaGetDraw());
            startActivity(intent);
        }

        if (id == R.id.item_write_rfid) {
            Intent intent = new Intent(this, ScriviRFID.class);

            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void bottoneIndietroClick (View v){
        customBackPressed();
    }

   /* @Override
    public boolean onTouchEvent(MotionEvent event) {
        Global.hideTop = !Global.hideTop;
        if (Global.hideTop) ab.hide();
        else ab.show();
        return true;
    }*/


    public void bottoneAzione2Click (View v) {
        Global.alert(this, "Comando non implementato");
    }
    public void bottoneAzione3Click (View v) {
        Global.alert(this, "Comando non implementato");
    }
    public void bottoneAvantiClick (View v) {
        Global.alert(this, "Comando non implementato");
    }
    public void bottoneAvantiClickLong (View v) {Global.alert(this, "Comando non implementato");}



    public String getTitolo(int pos) {
        View view= grigliaViste.getChildAt(pos);
        if (view==null) return "";
        TextView name = (TextView) view.findViewById(R.id.titolo);
        return name.getText().toString();
    }

    public String getDescrizione(int pos) {
     return getDescrizione(pos, true);
    }

    public String getDescrizione(int pos, boolean mandatory) {
        View view= grigliaViste.getChildAt(pos);
        if (view==null) return "";
        TextView name = (TextView) view.findViewById(R.id.titolo);
        EditText descrizione = (EditText) view.findViewById(R.id.descrizione);
        ToggleButton onoff = (ToggleButton) view.findViewById(R.id.onoff);
        RadioGroup radio = (RadioGroup) view.findViewById(R.id.radio);
        CheckBox check = (CheckBox) view.findViewById(R.id.checkBoxConferma);


        String valore = descrizione.getText().toString();
        if (listaValori!= null && (listaValori.size()>0) && listaValori.get(pos).get("Tipo").equals("BOOL")) valore = onoff.isChecked()? "SI" : "NO";
        if (listaValori!= null && (listaValori.size()>0) && listaValori.get(pos).get("Tipo").equals("RADIO")) {
            int selectedId = radio.getCheckedRadioButtonId();
            RadioButton radioSel = (RadioButton) view.findViewById(selectedId);
            valore = radioSel.getText().toString();
        }
        if (valore==null) valore = "";
        // obbligatorio per campo testuale
        if (valore.equals("") && mandatory && (listaValori!= null && (listaValori.size()>0) && !listaValori.get(pos).get("Tipo").equals("BOOL") && listaValori.get(pos).get("Mandatory").equals("true"))) {
            Global.alert(this, "A - IL CAMPO " + name.getText().toString() +" OBBLIGATORIO");
            stop = true;
        }

        // obbligatorio per campo bool
        if (listaValori!= null && valore.equals("NO") && mandatory && (listaValori.size()>0) && listaValori.get(pos).get("Tipo").equals("BOOL") && listaValori.get(pos).get("Mandatory").equals("true")) {
            if (!onoff.isChecked()) {
                //TODO: occhio che prima c'era
                //Global.alert(this, "B - IL CAMPO " + name.getText().toString() + " OBBLIGATORIO");
                //stop = true;
            }
        }

        if ((name.getText().toString().equalsIgnoreCase("HU") && Global.mustCassoni(getDescrizione("A Ubic")))) {
            if (valore.equalsIgnoreCase("")) {
                Global.alert(this, "C - PER QUESTA UBICAZIONE HU OBBLIGATORIO");
                stop = true;
            }
        }

        if (listaValori!= null && (listaValori.size()>0) && listaValori.get(pos).get("Tipo").equals("RFID")) {
            if (check.isChecked()) {
                valore += ":X";
            } else {
                valore += ":";
            }
        }
        return valore;
    }

    public boolean getDescrizioneReadOnly(String name) {
        boolean temp = false;

        for (int t = 0; t< grigliaViste.getCount(); t++) {
            if (getTitolo(t).equalsIgnoreCase(name)) {
                temp = !grigliaViste.getChildAt(t).findViewById(R.id.descrizione).isEnabled();
            }
        }
        return temp;
    }

    public String getDescrizione(String name) {
        return getDescrizione(name, true);
    }
    public String getDescrizione(String name, boolean mandatory) {
        int temp = -1;
        for (int t = 0; t< grigliaViste.getCount(); t++) {
            if (getTitolo(t).equalsIgnoreCase(name)) temp = t;
        }
        if (temp==-1) return "";
        else return getDescrizione(temp, mandatory);
    }


    public void setDescrizione(int pos, String valore) {
        View view= grigliaViste.getChildAt(pos);
        if (view==null) return ;
        TextView name = (TextView) view.findViewById(R.id.descrizione);
        name.setText(valore);
    }
    public void setDescrizione(String name, String valore) {
        int temp = -1;
        for (int t = 0; t< grigliaViste.getCount(); t++) {
            if (getTitolo(t).equalsIgnoreCase(name)) setDescrizione(t, valore);
        }


    }

    public static HashMap<String, String>  putListaValori(String titolo, String descrizione, String tipo) {
        return Global.putListaValori(titolo, descrizione, tipo);
    }

    public static HashMap<String, String>  putListaValori(String titolo, String descrizione) {
        return Global.putListaValori(titolo, descrizione);
    }

    public static HashMap<String, String>  putListaValori(String titolo, String descrizione, String tipo,  boolean mandatory) {
        return Global.putListaValori(titolo, descrizione, tipo, mandatory);
    }

    public void bottoneAzione1ClickLong (View v) {
        openDocumenta(true);
    }

    public void bottoneAzione1Click (View v) {

        openDocumenta(false);
    }

    public void openDocumenta (boolean treD) {
        String path2D =  Global.serverDOCUMENTAURL + Global.get2D + getDescrizione("Materiale", false);
        String path3D =  Global.serverDOCUMENTAURL + Global.get3D + getDescrizione("Materiale", false);

        Intent tempIntent = new Intent(Intent.ACTION_VIEW);
        Uri data;
        if (treD) {
            data = Uri.parse(path3D);
        } else {
            data = Uri.parse(path2D);
        }

        tempIntent.setDataAndType(data, "image/png");
        startActivity(tempIntent);

    }

    public void setKeyboard(View v) {
        int temp = -1;
        for (int t = 0; t< grigliaViste.getCount(); t++) {
            if (getTitolo(t).equalsIgnoreCase(((TextView) v).getText().toString())) temp = t;
        }

        View view= grigliaViste.getChildAt(temp);
        EditText descrizione = (EditText) view.findViewById(R.id.descrizione);
        if (descrizione.getInputType()==InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS) descrizione.setInputType(InputType.TYPE_CLASS_PHONE);
        else descrizione.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        descrizione.requestFocus();
    }

    public void bottoneCopia(View v) {
        refresh = false;
        View view= grigliaViste.getChildAt((Integer)v.getTag());
        TextView titoloCopia = (TextView) view.findViewById(R.id.titolo);

        //Vector<String> valoriCopia = Global.listaCopia.get(titoloCopia.getText().toString());

        String keyCopy = titoloCopia.getText().toString();
        ArrayList<String> valoriCopia = Global.readCopiedValues(keyCopy);

        if (valoriCopia==null) {
            valoriCopia = new ArrayList<>();

            //Global.insertCopyValues(keyCopy, valoriCopia);
            //Global.listaCopia.put(titoloCopia.getText().toString(),valoriCopia);
        }
        Log.d("DESCRIZIONECOPIA", "Valori copia " + valoriCopia.size());

        descrizioneCopia = (EditText) view.findViewById(R.id.descrizione);

        if (descrizioneCopia!=null) {

            if (descrizioneCopia.getText().toString().equals("")) {

                AlertDialog.Builder builderSingle = new AlertDialog.Builder(OmarActivity.this);
                builderSingle.setIcon(R.drawable.ic_launcher);
                builderSingle.setTitle(titoloCopia.getText().toString());

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(OmarActivity.this, android.R.layout.select_dialog_singlechoice);
                Hashtable<String, String> stamp = new Hashtable<>();
                for (String elem : valoriCopia) {
                    if (elem==null || elem.equalsIgnoreCase("")) continue;
                    if (stamp.containsKey(elem)) continue;
                    arrayAdapter.add(elem);
                    stamp.put(elem, elem);
                }

                builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which>-1) {
                            descrizioneCopia.setText(arrayAdapter.getItem(which));
                        }
                    }
                });
                Log.d("DESCRIZIONECOPIA", "sono entrato");

                builderSingle.show();


            } else if (!descrizioneCopia.getText().toString().equals("")) {
                descrizioneCopia.setText("");
            }
        }
    }

    public void bottonePossibilita(View v) {
        refresh = false;
        View view= grigliaViste.getChildAt((Integer)v.getTag());
        TextView titoloCopia = (TextView) view.findViewById(R.id.titolo);

        //Vector<String> valoriCopia = Global.listaCopia.get(titoloCopia.getText().toString());

        descrizioneCopia = (EditText) view.findViewById(R.id.descrizione);

        if (descrizioneCopia!=null) {

            if (descrizioneCopia.getText().toString().equals("")) {
                String keyCopy = null;
                if (myIntent.getStringExtra("Ubic") != null) {
                    keyCopy = myIntent.getStringExtra("Ubic");
                    Log.d("keyCopyUBIC", keyCopy);
                }
                if (myIntent.getStringExtra("A Ubic")!= null) {
                    keyCopy = myIntent.getStringExtra("A Ubic");
                    Log.d("keyCopy", keyCopy);
                }


                ArrayList<String> valoriCopia = Global.readPossibilityValues(keyCopy, OmarActivity.this);

                if (valoriCopia == null) {
                    valoriCopia = new ArrayList<>();
                    //Global.insertCopyValues(keyCopy, valoriCopia);
                    //Global.listaCopia.put(titoloCopia.getText().toString(),valoriCopia);
                }

                AlertDialog.Builder builderSingle = new AlertDialog.Builder(OmarActivity.this);
                builderSingle.setIcon(R.drawable.ic_launcher);
                builderSingle.setTitle(titoloCopia.getText().toString());

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(OmarActivity.this, android.R.layout.select_dialog_singlechoice);
                Hashtable<String, String> stamp = new Hashtable<>();
                for (String elem : valoriCopia) {
                    if (elem == null || elem.equalsIgnoreCase("")) continue;
                    if (stamp.containsKey(elem)) continue;
                    arrayAdapter.add(elem);
                    stamp.put(elem, elem);
                }

                builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which > -1) {
                            String val = arrayAdapter.getItem(which).substring(0, 7);
                            Log.d("TESTITEM", val);
                            descrizioneCopia.setText(val);
                        }
                    }
                });

                builderSingle.show();
            } else if (!descrizioneCopia.getText().toString().equals("")) {
                descrizioneCopia.setText("");
            }
        }
    }


    public void openInfoStock (boolean stock, String matnr){
        Intent intent = new Intent(this, InfoStockZmag.class);
        intent.putExtra("menuItem", Global.INFO_STOCK_MATERIALE);
        intent.putExtra("titolo", "Info Stock Materiale " + (stock? "per Stock":" per ODP") + ": " +  matnr);
        intent.putExtra("Materiale",  matnr);
        intent.putExtra("Divisione", Global.getImpostazoniSAP("Divisione"));
        intent.putExtra("Numero Mag.", Global.getImpostazoniSAP("Numero Magazzino"));
        intent.putExtra("Stock/Odp", stock ? "STOCK":"");

        intent.putExtra("Descrizione", "");
        intent.putExtra("Tipo ordine", "");
        intent.putExtra("Ordine", "");
        intent.putExtra("Magazzino", "");
        intent.putExtra("HU", "");

        startActivity(intent);
    }
}
