package com.ancoragroup.wm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import com.ancoragroup.utils.*;


// http://ws-dev.ancoragroup.com/Services/FMProxy.aspx?Z_WK_ZMAG&cod=14000209&desc=&todp=&odp=&div=D110&mag=W110&nmag=110&stock=X&tubic=&ubic=
public class Griglia extends AppCompatActivity  {
    final static int HEADER_HEIGHT = 70;

    int DIMENSIONE_TESTO_HEADER_FOOTER = 17;

    final static public int RIGHE = 4;
    final static public int RIGHE_VUOTE = 5;
    final static String FONT_APP = "sans-serif-condensed";

    boolean dontExecuteBackgroundCall = false;

    boolean goBack = false;

    String mag = "";
    String cod = "";
    ProgressDialog progress;
    ArrayList<HashMap<String, String>> listaValoriTemp = new ArrayList<>();
    ArrayList<HashMap<String, String>> listaValori = new ArrayList<>();
    TableLayout tabella;
    TableLayout tabellaH;
    TableLayout tabellaF;

    boolean reloadData = false;

    LinkedHashMap <String, FieldItem> campi;
    //LinkedHashMap <String, FieldItem> campiGriglia;

    ArrayList<ArrayList<String>> campiGriglia;

    List<Integer> dimensioniTestoPerRiga = Arrays.asList(17, 15, 14, 13);

    Intent myIntent;

    View item;
    Drawable col;

    HashMap<Object, Drawable> valoriSelezionati = new HashMap<Object, Drawable>();
    HashMap<Object, Drawable> valoriDisabilitati = new HashMap<Object, Drawable>();

    Button bottoneIndietro;
    Button bottoneAzione1;
    Button bottoneAzione2;
    Button bottoneAzione3;
    Button bottoneAvanti;


    TextView labelInfo1;
    TextView labelInfo2;
    TextView labelInfo3;
    TextView labelInfo4;

    String menuItem = "";

    ActionBar ab;
    String titolo = "---";
    String info2 = "---";
    String info3 = "---";
    String info4 = "---";

    int campoOrdinamento = -1;
    boolean orderByDesc = false;

    ArrayList<Integer> checkedRowsBeforeUpdate = new ArrayList<>();

    private Context context = this;

    String testoDaCercare = "";

    // Imposta il bottone con immagine di sfondo documenta
    public void impostaBottoneDocumenta(Button btn) {
        ButtonUtils.getInstance(this.getApplicationContext()).impostaBottoneSfondoImmagine(btn, R.drawable.documenta);
    }

    // Imposta il bottone come azione su singola riga
    public void impostaBottoneSelezioneSingolaRiga(Button btn, String text) {
        ButtonUtils.getInstance(this.getApplicationContext()).setButtonProperties(btn, text, R.color.colorPrimary, R.color.white, R.string.button_type_single);
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
        setContentView(R.layout.activity_griglia);

        myIntent = getIntent();

        titolo = myIntent.getStringExtra("titolo");
        info2 = myIntent.getStringExtra("info2");
        info3 = myIntent.getStringExtra("info3");
        info4 = myIntent.getStringExtra("info4");

        try {
            menuItem = myIntent.getStringExtra("menuItem");
            Log.d("menuItem", menuItem);
        } catch (Exception e) {
            Log.e("ERRORE onCreate 1", e.getMessage());
        }

        try {
            titolo = myIntent.getStringExtra("titolo");
            Log.d("titolo", titolo);
        } catch (Exception e) {
            Log.e("ERRORE onCreate 2", e.getMessage());
        }

        String fontGrande = Global.getImpostazioniApp("CARATTERI GRANDI");
        if ("SI".equals(fontGrande)) {
            for (int i = 0; i < dimensioniTestoPerRiga.size(); i++) {
                dimensioniTestoPerRiga.set(i, Global.calculateFontDimension(dimensioniTestoPerRiga.get(i)));
            }

            DIMENSIONE_TESTO_HEADER_FOOTER = Global.calculateFontDimension(DIMENSIONE_TESTO_HEADER_FOOTER );
        }

        // imposta la progess bar
        progress=new ProgressDialog(Griglia.this);
        progress.setTitle("Caricamento");
        progress.setMax(0);
        progress.setCancelable(false);

        // --------Start Scroll Bar Slide--------
        final HorizontalScrollView xHorizontalScrollViewHeader = (HorizontalScrollView) findViewById(R.id.horizontalViewHeader);
        final HorizontalScrollView xHorizontalScrollViewData = (HorizontalScrollView) findViewById(R.id.horizontalView);
        final HorizontalScrollView xHorizontalScrollViewFooter = (HorizontalScrollView) findViewById(R.id.horizontalViewFooter);
        xHorizontalScrollViewData.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollX; int scrollY;
                scrollX=xHorizontalScrollViewData.getScrollX();
                scrollY=xHorizontalScrollViewData.getScrollY();
                xHorizontalScrollViewHeader.scrollTo(scrollX, scrollY);
                xHorizontalScrollViewFooter.scrollTo(scrollX, scrollY);
            }
        });
        // ---------End Scroll Bar Slide---------

        impostaTuttiCampi();
        impostaCampiGriglia();
        /*
        impostaCampiGrigliaRiga2();
        impostaCampiGrigliaRiga3();
        impostaCampiGrigliaRiga4();
        */

        // imposta i pulsanti di navigazione
        bottoneIndietro = (Button) findViewById(R.id.bottoneIndietro);
        bottoneAzione1 = (Button) findViewById(R.id.bottoneAzione1);
        bottoneAzione2 = (Button) findViewById(R.id.bottoneAzione2);
        bottoneAzione3 = (Button) findViewById(R.id.bottoneAzione3);
        bottoneAvanti = (Button) findViewById(R.id.bottoneAvanti);

        labelInfo1 = ((TextView) findViewById(R.id.label_info1));
        labelInfo2 = ((TextView) findViewById(R.id.label_info2));
        labelInfo3 = ((TextView) findViewById(R.id.label_info3));
        labelInfo4 = ((TextView) findViewById(R.id.label_info4));

        // long press su documenta
        bottoneAzione1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                bottoneAzione1ClickLong(v);
                return true;
            }
        });

        // long press su avanti
        bottoneAvanti.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                bottoneAvantiClickLong(v);
                return true;
            }
        });

        bottoneAzione2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                bottoneAzione2ClickLong(v);
                return true;
            }
        });

        bottoneAzione3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                bottoneAzione3ClickLong(v);
                return true;
            }
        });

        // chiama un metodo per preparare i dati
        preparaDati();

        // Always cast your custom Toolbar here, and set it as the ActionBar.
        Toolbar tb = (Toolbar) findViewById(R.id.my_toolbar);
        tb.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = ((AppCompatActivity) context).getIntent();

                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    for (String key : bundle.keySet()) {
                        Object value = bundle.get(key);
                    }
                }

                Global.alert(getApplicationContext(), "Pagina salvata");
                Global.saveIntentStatus(intent, bundle);

                return false;
            }
        });

        tb.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetector.onTouchEvent(motionEvent);
                return true;
            }

            private GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDown(MotionEvent event) {
                    // triggers first for both single tap and long press
                    return true;
                }
                @Override
                public boolean onSingleTapUp(MotionEvent event) {
                    // triggers after onDown only for single tap
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent event) {
                    Intent intent = ((AppCompatActivity) context).getIntent();

                    Bundle bundle = intent.getExtras();
                    if (bundle != null) {
                        for (String key : bundle.keySet()) {
                            Object value = bundle.get(key);
                        }
                    }

                    ArrayList<Integer> checkedCheckBox = getSelectedRows();
                    boolean saved = Global.saveIntentStatus(intent, bundle, checkedCheckBox);

                    String label = saved ? "Pagina salvata" : "Pagina eliminata";
                    Global.alert(getApplicationContext(), label);

                    super.onLongPress(event);
                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                      AlertDialog.Builder builderSingle = new AlertDialog.Builder(Griglia.this);
                      builderSingle.setIcon(R.drawable.ic_launcher);

                      builderSingle.setTitle("SELEZIONA PAGINA SALVATA");

                      final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Griglia.this, android.R.layout.select_dialog_singlechoice);

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

                                  // Passo le check con il flag
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


        // Creo il menu e lo mostro se serve
        createActionBar();

        updateToolBar();

        // aggiorna i dati
        new BackgroundTask().execute();
    }

    public void createActionBar() {
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
    }
    public void updateToolBar() {
        if (titolo!=null) labelInfo1.setText(titolo);
        if (info2!=null) labelInfo2.setText(info2);
        if (info3!=null) labelInfo3.setText(info3);
        if (info4!=null) labelInfo4.setText(info4);

        if (Global.typeRun==Global.SVILUPPO) ab.setBackgroundDrawable(getResources().getDrawable(R.color.colorAccent));
        else ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_splash));

    }


    public void preparaDati() {  }


    // CREAZIONE DEL MENU CONTESTUALE A DESTRA
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.toolmenu_griglia, menu);

        return true;
    }

    // GESTIONE DEL MENU NELLA TOOLBAR
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.item_about) {
            Intent intent = new Intent(this, About.class);
            startActivity(intent);
        }

        if (id == R.id.item_refresh) {
            // Ricavo le righe checked
            checkedRowsBeforeUpdate = getTagsOfCheckedCheckboxRows();

            new BackgroundTask().execute();
        }

        if (id == R.id.item_find) {
            if ((testoDaCercare==null) || (testoDaCercare.equals(""))) {
                testoDaCercare = "";
                final AlertDialog.Builder inputAlert = new AlertDialog.Builder(context);
                inputAlert.setTitle("Testo da cercare");
                inputAlert.setMessage("Inserisci il testo da cercare");
                final EditText userInput = new EditText(context);
                userInput.setText(testoDaCercare);

                userInput.setPadding(50, 20, 50, 20);
                userInput.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                userInput.setSelection(testoDaCercare.length(), testoDaCercare.length());

                inputAlert.setView(userInput);
                inputAlert.setPositiveButton("CERCA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        testoDaCercare = userInput.getText().toString();
                        disegnaGriglia(campoOrdinamento, orderByDesc);
                        info4 = "Filtro: " + testoDaCercare;
                        updateToolBar();
                    }
                });
                inputAlert.setNegativeButton("ANNULLA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = inputAlert.create();
                alertDialog.show();
            } else {
                testoDaCercare = "";
                disegnaGriglia(campoOrdinamento, orderByDesc);
                Global.alert(this, "Rimosso filtro");
                info4 = "";
                updateToolBar();

            }
        }


        if (id == R.id.item_exit) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        disableSelectedRows();

        updateToolBar();

        // gestisco il back a ritroso
        if (Global.backMenu) {
            if (Global.backMenuLoop == 0) {
                // quando arrivo alla acvtivity giusta mi fermo e aggiorno
                // aggiorna i dati
                Global.backMenu = false;
               // new BackgroundTask().execute();
            } else {
                Global.backMenuLoop--;
                customBackPressed();
            }
        }


        if (bottoneIndietro.getText().toString().equals("")) bottoneIndietro.setVisibility(View.INVISIBLE);
        if (bottoneAzione1.getText().toString().equals("")) bottoneAzione1.setVisibility(View.INVISIBLE);
        // BOttone Documenta
        //if (getLocalClassName().equals("ListaPrelItem") || getLocalClassName().equals("InfoStockZmag") || getLocalClassName().equals("VerificaCassoni")) {

        boolean isButtonDocumenta = bottoneAzione1.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.documenta).getConstantState());

        if ((getLocalClassName().equals("ListaPrelItem") || getLocalClassName().equals("InfoStockZmag")) || isButtonDocumenta) {
            bottoneAzione1.setVisibility(View.VISIBLE);
        }


        if (bottoneAzione2.getText().toString().equals("")) bottoneAzione2.setVisibility(View.INVISIBLE);
        if (bottoneAzione3.getText().toString().equals("")) bottoneAzione3.setVisibility(View.INVISIBLE);
        if (bottoneAvanti.getText().toString().equals("")) bottoneAvanti.setVisibility(View.INVISIBLE);

    }

    public void disableSelectedRows() {
        if (tabella != null && Global.disabledItem) {
            int howMuchRows = RIGHE;
            // Ciclo tutte le righe della tabella
            for (int currentTableRow = 0; currentTableRow < tabella.getChildCount(); currentTableRow += howMuchRows) {
                // Prendo la prima riga
                View child = tabella.getChildAt(currentTableRow);
                if (child instanceof TableRow) {
                    TableRow row = (TableRow) child;

                    // Ciclo ciascuna colonna per cercare una checkbox
                    for (int column = 0; column < row.getChildCount(); column++) {
                        View view = row.getChildAt(column);

                        // Se è una checkbox
                        if (view instanceof CheckBox) {
                            CheckBox cb = (CheckBox) view;
                            // Se la checkbox è checked devo disablitare la riga e le sottorighe relative al record
                            if (cb.isChecked()) {
                                // Disabilito l acheckbox
                                cb.setChecked(false);
                                cb.setEnabled(false);

                                // Inserisco i valori disabilitati
                                valoriDisabilitati.put(row.getTag(),    row.getBackground());
                                Global.disabledItem = false;

                                // Ciclo le sottorighe e le disabilito
                                for (int currentRow = currentTableRow; currentRow < (currentTableRow + howMuchRows); currentRow ++) {
                                    View childToDisable = tabella.getChildAt(currentRow);
                                    if (childToDisable instanceof TableRow) {
                                        // Disablito la riga
                                        disableRow((TableRow)childToDisable);
                                    }
                                }

                            }
                        }
                    }
                }

            }
        }
    }

    private void disableRow(TableRow tR) {
        tR.setEnabled(false);
        tR.setBackgroundResource(R.color.omardisableGriglia);

        // Tolgo la riga selezionata
        if (valoriSelezionati.containsKey(tR.getTag())) {
            valoriSelezionati.remove(tR.getTag());
        }
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (reloadData) {
            // Allargo le colonne nello stesso modo
            TableRow rigaH = (TableRow) tabellaH.getChildAt(0);
            TableRow rigaD = (TableRow) tabella.getChildAt(0);
            TableRow rigaF = (TableRow) tabellaF.getChildAt(0);

            for (int i = 0; i < rigaD.getChildCount(); i++) {
                View tvH = rigaH.getChildAt(i);
                View tvD = rigaD.getChildAt(i);
                View tvF = rigaF.getChildAt(i);

                int tvHW = tvH.getMeasuredWidth();
                int tvDW = tvD.getMeasuredWidth();

                //int paddingRight = tvD.getPaddingRight();

                int tvFW = tvF.getMeasuredWidth();
                int tvW = 0;

                // normalizza le latghezze
                if (tvHW > tvDW) tvW = tvHW;
                else tvW = tvDW;

                if (tvFW > tvW) tvW = tvFW;

                // TextViewHeader
                if (tvH instanceof TextView) {
                    ((TextView) tvH).setWidth(tvW);

                } else if (tvH instanceof CheckBox) {
                    ((CheckBox) tvH).setWidth(tvW);
                }

                // TextViewDati
                if (tvD instanceof TextView) {
                    ((TextView) tvD).setWidth(tvW);
                } else if (tvD instanceof CheckBox) {
                    ((CheckBox) tvD).setWidth(tvW);
                }

                // TextViewFooter
                ((TextView) tvF).setWidth(tvW);
            }
            reloadData = false;
        }
        updateToolBar();
    }

    public TextView addTextViewVuota() {
        return addTextView(null, false, 0);
    }

    public TextView addTextView(String testo) {
        return addTextView(testo, false, 0);
    }

    public TextView addTextView(boolean right, String testo) {
        return addTextViewGen(false, right, testo, false, 0);
    }

    public TextView addTextView(boolean right, String testo, boolean header) {
        return addTextViewGen(false, right, testo, header, 0);
    }

    public TextView addTextView(String testo, boolean header) {
        return addTextView(testo, header, 0);
    }

    public TextView addTextView(String testo, boolean header, int tag) {
        return addTextViewGen(false, false, testo, header, tag);
    }

    // ALTRE
    public TextView addTextViewAltraVuota() {
        return addTextViewAltra(null, false, 0);
    }

    public TextView addTextViewAltra(String testo) {
        return addTextViewAltra(testo, false, 0);
    }

    public TextView addTextViewAltra(boolean right, String testo) {
        return addTextViewGen(false, right, testo, false, 0);
    }

    public TextView addTextViewAltra(boolean right, String testo, boolean header) {
        return addTextViewGen(false, right, testo, header, 0);
    }

    public TextView addTextViewAltra(String testo, boolean header) {
        return addTextViewAltra(testo, header, 0);
    }

    public TextView addTextViewAltra(String testo, boolean header, int tag) {
        return addTextViewGen(true ,false, testo, header, tag);
    }
    ////////////

    public TextView addTextViewGen(boolean altra, boolean right, String testo, boolean header, int tag ) {
        TextView tv = new TextView(this);
        tv.setHorizontallyScrolling(true);
        tv.setTypeface(Typeface.create(FONT_APP, Typeface.NORMAL));
        tv.setTag(tag);
        //tv.setGravity(Gravity.CENTER);
        tv.setBackgroundResource(R.drawable.border_style);
        if (testo==null) {
            tv.setBackgroundResource(R.drawable.border_style_transparent);
            testo = "";
        }
        tv.setText(testo);


        if (Global.dpi.equals("xhdpi")) {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, DIMENSIONE_TESTO_HEADER_FOOTER);
        } else {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, DIMENSIONE_TESTO_HEADER_FOOTER);
        }

        if (Global.dpi.equals("xhdpi")) {
            if (altra) {
                tv.setPadding(5, 5, 5, 5);
            } else {
                tv.setPadding(5, 2, 5, 2);
            }
        } else {
            //tv.setPadding(10, 10, 10, 10);
            tv.setPadding(10, 3, 10, 3);
        }

        if (header) {
            tv.setTextColor(Color.WHITE);
            tv.setTypeface(Typeface.create(FONT_APP, Typeface.BOLD));

            // TODO: Test per visualizzazione Gogle Chrome
            if (Global.isChrome()) {
                tv.setPadding(1, 30, 1, 0);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, DIMENSIONE_TESTO_HEADER_FOOTER + 15);
            }

            if (right) {
                tv.setGravity(Gravity.RIGHT);
            }

            if (!Global.dpi.equals("xhdpi")) {
                tv.setHeight(HEADER_HEIGHT);
            }

            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v instanceof CheckBox || (v instanceof TextView && ((TextView) v).getText().toString().equals("#"))) {
                        checkUnCheckRowsCheckBoxes(v);
                    } else {
                        int campoOrdine = (Integer) v.getTag();
                        if (campoOrdine != campoOrdinamento) {
                            orderByDesc = false;
                        } else {
                            orderByDesc = !orderByDesc;
                        }

                        checkedRowsBeforeUpdate = getTagsOfCheckedCheckboxRows();

                        disegnaGriglia(campoOrdine, orderByDesc);
                    }
                }
            });
        }
        else {
            tv.setTextColor(Color.BLACK);
            // se il valore che scriviamo contiene uno spazio vuol dire che è testo e non lo allineo
            // il resto lo allinea a destrass
            //if (testo!=null && Global.isNumeric(testo)) {
            if (right) {
                tv.setGravity(Gravity.RIGHT);
                tv.setText(Global.zapZero(testo));
            }
        }

        return tv;
    }

    // Seleziona / Deseleziona le checkbox delle righe cliccando la label # oppure la checkbox nella prima colonna dell'header
    public void checkUnCheckRowsCheckBoxes(View v) {
        for (int sel_riga = 0; sel_riga < tabella.getChildCount(); sel_riga++) {
            View child = tabella.getChildAt(sel_riga);

            if (child instanceof TableRow) {
                TableRow row = (TableRow) child;

                for (int t = 0; t < row.getChildCount(); t++) {
                    View view = row.getChildAt(t);
                    if (view instanceof CheckBox) {
                        CheckBox cb = (CheckBox) view;
                        if (cb.isEnabled()) {
                            if ("SI".equalsIgnoreCase(Global.getImpostazioniApp("INVERTI SELEZIONE"))) {
                                cb.setChecked(!(cb).isChecked());
                            } else {
                                cb.setChecked(((CheckBox) v).isChecked());
                            }
                        }
                    }
                }
            }
        }
    }

    public void bottoneIndietroClick (View v){
        customBackPressed();
    }

    public void bottoneAzione1ClickLong (View v) {
        openDocumenta(false);
    }

    public void bottoneAzione1Click (View v) {
        openDocumenta(true);
    }

    public void openDocumenta (boolean treD) {
        int sel_riga = -1;
        for (Object o :  valoriSelezionati.keySet()) {
            sel_riga = ((Integer) o).intValue();
        }

        if (sel_riga!=-1) {
            String path2D = "http://ws-dev.ancoragroup.com/Services/DocumentaProxy.aspx?GetDraw&codAn=" + listaValori.get(sel_riga / RIGHE).get("MATNR") + "&ext=PNG16&dpi=300&orient=V";
            String path3D = "http://ws-dev.ancoragroup.com/Services/DocumentaProxy.aspx?Get3D&codice=" + listaValori.get(sel_riga / RIGHE).get("MATNR");

            Intent tempIntent = new Intent(Intent.ACTION_VIEW);

            Uri data = null;
            if (treD) {
                data = Uri.parse(path3D);
            } else {
                data = Uri.parse(path2D);
            }

            tempIntent.setDataAndType(data, "image/png");
            tempIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(tempIntent);
        } else {
            Global.alert(this, "Nessuna riga selezionata");
        }

    }
    public void openDocumentaOld (boolean treD) {
        for (int sel_riga = 0; sel_riga < tabella.getChildCount(); sel_riga++) {
            View child = tabella.getChildAt(sel_riga);

            if (child instanceof TableRow) {
                TableRow row = (TableRow) child;

                for (int t = 0; t < row.getChildCount(); t++) {
                    View view = row.getChildAt(t);

                    if (view instanceof CheckBox) {
                        CheckBox cb = (CheckBox) view;
                        if (cb.isChecked()) {

                            //String path = "http://ws.ancoragroup.com/Services/DocumentaProxy.aspx?GetFile&codice=14010000&rev=0&ext=PDF";
                            String path2D ="http://ws-dev.ancoragroup.com/Services/DocumentaProxy.aspx?GetDraw&codAn=" + listaValori.get(sel_riga).get("MATNR") + "&ext=PNG16&dpi=300&orient=V";
                            String path3D ="http://ws-dev.ancoragroup.com/Services/DocumentaProxy.aspx?Get3D&codice=" + listaValori.get(sel_riga).get("MATNR");

                            Intent tempIntent = new Intent(Intent.ACTION_VIEW);
                            Uri data;
                            if (treD) {
                                data = Uri.parse(path3D);
                            } else {
                                data = Uri.parse(path2D);
                            }

                            tempIntent.setDataAndType(data, "image/png");
                            //tempIntent.setPackage("com.adobe.reader");
                            //tempIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            startActivity(tempIntent);

                        }
                    }
                }
            }
        }
    }

    // ================ BOTTONE AZIONE 2 ================ //
    public void bottoneAzione2Click(View v) {
        bottoneAzione2ClickStart();

        // Ricavo le righe selezionate a seconda se il bottone è multiplo oppure no
        ArrayList<Integer> selectedRows = getValueOfSelectdRowsMultipleOrSingleFromButton(v);
        for (int sel_riga : selectedRows) {
            bottoneAzione2SingolaRiga(sel_riga);
        }

        // Eseguo eventuale post codice
        bottoneAzione2ClickEnd();
    }
    public void bottoneAzione2ClickStart() { }
    public void bottoneAzione2ClickEnd() { }


    public void bottoneAzione2SingolaRiga(int currentRow) {
        Global.alert(this, "Comando non implementato");
    }

    // ================ BOTTONE AZIONE 3 ================ //
    public void bottoneAzione3Click(View v) {
        bottoneAzione3ClickStart();

        // Ricavo le righe selezionate a seconda se il bottone è multiplo oppure no
        ArrayList<Integer> selectedRows = getValueOfSelectdRowsMultipleOrSingleFromButton(v);

        for (int sel_riga : selectedRows) {
            bottoneAzione3SingolaRiga(sel_riga);
        }

        // Eseguo eventuale post codice
        bottoneAzione3ClickEnd();
    }
    public void bottoneAzione3ClickStart() { }
    public void bottoneAzione3ClickEnd() { }

    public void bottoneAzione3SingolaRiga(int currentRow) {
        Global.alert(this, "Comando non implementato");
    }

    private long lastClickTime = 0;
    // ================ BOTTONE AVANTI ================ //
    public void bottoneAvantiClick (View v) {
        // Evito il doppio TAP
        if (Global.BUTTON_WAIT_FEATURE) {
            if (SystemClock.elapsedRealtime() - lastClickTime < Global.BUTTON_CLICK_MS_WAIT) {
                return;
            }

            Log.d("CLIC", "Click " + lastClickTime);

            lastClickTime = SystemClock.elapsedRealtime();
        }
        // Eseguo eventuale pre codice
        bottoneAvantiClickStart();

        // Ricavo le righe selezionate a seconda se il bottone è multiplo oppure no
        ArrayList<Integer> selectedRows = getValueOfSelectdRowsMultipleOrSingleFromButton(v);

        for (int sel_riga : selectedRows) {
            bottoneAvantiSingolaRiga(sel_riga);
        }

        // Eseguo eventuale post codice
        bottoneAvantiClickEnd();
    }

    public void bottoneAvantiClickStart() { }
    public void bottoneAvantiClickEnd() { }
    public void bottoneAvantiSingolaRiga(int currentRow) {
        Global.alert(this, "Comando non implementato");
    }

    // Ritorno un array con le righe con le checkbox spuntate (se è multiplo) altrimenti della riga selezionata
    public ArrayList<Integer> getValueOfSelectdRowsMultipleOrSingleFromButton(View v) {
        ArrayList<Integer> selectedRows = new ArrayList<>();

        boolean multiple = getString(R.string.button_type_multiple).equals(v.getTag().toString());

        // Se multiple ricavo le righe con le checkbox spuntate
        if (multiple) {
            selectedRows = getSelectedRows();
        } else {
            selectedRows.add(getSelectedRowFromTable());
        }
        return selectedRows;
    }

    // Ricavo le righe selezionate (quelle che hanno la checkbox spuntata)
    public ArrayList<Integer> getSelectedRows() {
        ArrayList<Integer> rows = new ArrayList<>();
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
                            rows.add(sel_riga);
                        }
                    }
                }
            }
        }
        return rows;
    }

    public ArrayList<Integer> getTagsOfCheckedCheckboxRows() {
        ArrayList<Integer> rows = new ArrayList<>();
        for (int sel_riga_temp = 0; sel_riga_temp < tabella.getChildCount(); sel_riga_temp++) {
            View child = tabella.getChildAt(sel_riga_temp);

            if (child instanceof TableRow) {
                TableRow row = (TableRow) child;

                for (int t = 0; t < row.getChildCount(); t++) {
                    View view = row.getChildAt(t);

                    if (view instanceof CheckBox) {
                        CheckBox cb = (CheckBox) view;
                        int sel_riga = sel_riga_temp / RIGHE;
                        Object tag = cb.getTag();

                        if (cb.isChecked()) {
                            rows.add(Integer.valueOf(tag.toString()));
                        }
                    }
                }
            }
        }
        return rows;
    }

    // Ricavo la riga selezionata
    public int getSelectedRowFromTable() {
        int sel_riga = -1;
        for (Object o :  valoriSelezionati.keySet()) {
            sel_riga = ((Integer) o).intValue() / RIGHE;
        }
        return sel_riga;
    }
    public void bottoneAvantiClickLong (View v) {Global.alert(this, "Comando non implementato");}
    public void bottoneAzione2ClickLong(View v) {}
    public void bottoneAzione3ClickLong(View v) {}

    public class BackgroundTask extends AsyncTask<Void, Integer, String> {
        String error = null;
        boolean changed = false;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progress.setProgress(0);
            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progress.setMessage("Caricamento dati da SAP...");
            progress.show();

        }



        @Override
        protected String doInBackground(Void... arg0)
        {
            listaValoriTemp.clear();
            int val = getCicli();
            try {
                for (int t=0; t<val; t++) {
                    String url = getUrl(t);
                    // Se passo NO_URL_CALL non fa chiamata
                    if (!"NO_URL_CALL".equalsIgnoreCase(url)) {
                        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(2 * 60 * 1000);
                        connection.setReadTimeout(2 * 60 * 1000);

                        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                        Document doc = dBuilder.parse(connection.getInputStream());
                        Element element = doc.getDocumentElement();

                        String type = doc.getElementsByTagName("TYPE").item(0).getTextContent();
                        NodeList message = doc.getElementsByTagName("MESSAGE");

                        if (type != null && type.equals("E") && getCicli() < 1) {
                            error = message.item(0).getTextContent();

                        } else {
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

                            Log.d("SAP COUNT:", String.valueOf(nodi.getLength()));

                            for (int temp = 0; temp < nodi.getLength(); temp++) {

                                Log.i("--->", String.valueOf(temp));
                                publishProgress(temp);
                                Node nNode = nodi.item(temp);


                                HashMap<String, String> hashMap;
                                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element eElement = (Element) nNode;

                                    hashMap = new HashMap<>();
                                    Log.d("SAP LOOP:", String.valueOf(temp));
                                    for (String chiave : campi.keySet()) {
                                        if (campi.get(chiave).isCalcolato()) {
                                            continue;
                                        }

                                        NodeList nl = eElement.getElementsByTagName(chiave);
                                        if (nl != null) {
                                            if (nl.item(0) == null) {
                                                hashMap.put(chiave, "");
                                                Log.d("XML MANCANTE:", chiave);
                                            } else {
                                                String valore = nl.item(0).getTextContent();
                                                if (chiave.equalsIgnoreCase("QZEIT"))
                                                    valore = valore.substring(0, 2) + ":" + valore.substring(2, 4) + ":" + valore.substring(4, 6);
                                                if (chiave.equalsIgnoreCase("QDATU"))
                                                    valore = valore.substring(0, 4) + "." + valore.substring(4, 6) + "." + valore.substring(6, 8);
                                                if (valore != null) valore = valore.trim();
                                                hashMap.put(chiave, valore);
                                            }

                                        } else {
                                            hashMap.put(chiave, "");
                                            Log.d("XML MANCANTE:", chiave);
                                        }
                                    }

                                    listaValoriTemp.add(hashMap);
                                    progress.setProgress(temp + 1);
                                }
                            }
                        }
                        aggiungiCampiCalcolati(t);
                    }
                }
                progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            } catch (Exception e) {
                //error =  e.getMessage();
                StringWriter errors = new StringWriter();
                e.printStackTrace(new PrintWriter(errors));
                String terror = errors.toString();
                //int lung = 1000;
                //if (lung>terror.length()-terror.indexOf("ancora")) lung = terror.length()-terror.indexOf("ancora");
                //terror = terror.substring(terror.indexOf("ancora"), terror.indexOf("ancora") +1000);
                Log.e("ERRORE GRIGLIA:",terror );
                error = "Nessun record: " + e.getMessage();

            }
            return String.valueOf(listaValoriTemp.size());
        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            super.onProgressUpdate(values);
            if (values[0].intValue()>1) progress.setMessage("Preparazione Tabella...");
            progress.setProgress(values[0].intValue());
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            if (error!=null ) {
                Log.e("ERRORE onPostExecute", error);
                progress.dismiss();
                Global.alert(Griglia.this, error);
                customBackPressed();
            } else if (result.equals("0")) {
                progress.dismiss();
                Global.alert(Griglia.this, "Nessun record");
                customBackPressed();
            } else {

                disegnaGriglia(campoOrdinamento, orderByDesc);
            }
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

    public CheckBox createCheckBox(Context context) {
        return createCheckBox(context, getResources().getColorStateList(R.color.checkbox));
    }

    public CheckBox createCheckBox(Context context, ColorStateList color) {
        return createCheckBox(context, color, false, -1);
    }

    public CheckBox createCheckBox(Context context, boolean checked, int idCheckBox) {
        return createCheckBox(context, getResources().getColorStateList(R.color.checkbox), checked, idCheckBox);
    }
    public CheckBox createCheckBox(Context context, ColorStateList color, boolean checked, int idCheckBox) {
        CheckBox cb = new CheckBox(context);
        cb.setChecked(checked);
        if (idCheckBox != -1) {
            cb.setTag(idCheckBox);
        }
        if (Global.dpi.equals("xhdpi")) {
            cb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            cb.setTextColor(color);
            cb.setPadding(0, 0, 5, 0);
            cb.setScaleX(1.3f);
            cb.setScaleY(1.3f);
        } else {
            cb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            cb.setTextColor(color);
            //cb.setPadding(10, 20, 10, 20);
            cb.setPadding(10, 10, 10, 10);

            cb.setScaleX(1.4f);
            cb.setScaleY(1.4f);

        }
        TableRow.LayoutParams lp2 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        if (Global.getImpostazioniApp("CARDINALE").equals("SI")) {
            lp2.width = 100;
        } else {
            lp2.width = 60;
        }
        // TODO: SE tolto viene centrato
        /*
        lp2.height = 50;
        */
        lp2.setMargins(10, 0, 10, 0);
        cb.setLayoutParams(lp2);
        cb.setButtonTintList(color);
        return cb;
    }

    public void disegnaGriglia (int campoOrdine) {
        disegnaGriglia(campoOrdine, true);
    }
    public void disegnaGriglia (int campoOrdine, boolean change) {

        float totale[] = new float[campi.size()+1];
        progress.show();
        if (campoOrdine != -1) {

            //Collections.sort(listaValoriTemp, new MapComparator(getCampo(campoOrdine), (campoOrdinamento==campoOrdine) && change));
            Collections.sort(listaValoriTemp, new MapComparator(getCampo(campoOrdine), orderByDesc));
            campoOrdinamento = campoOrdine;

            /*
            if (campoOrdinamento==campoOrdine) {
                campoOrdinamento = -1;
            }  else {
                campoOrdinamento = campoOrdine;
            }
            */
        }

        //
        // INTESTAZIONE
        //
        tabellaH = (TableLayout) findViewById(R.id.tabellah);
        tabellaH.removeAllViews();
        TableRow row = new TableRow(Griglia.this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);

        row.setBackgroundResource(R.drawable.background_splash);
        // checkbox per la selezione
        CheckBox cb = createCheckBox(Griglia.this, getResources().getColorStateList(R.color.white));
        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof CheckBox) {
                    checkUnCheckRowsCheckBoxes(v);
                }
            }
        });
        row.addView(cb);


        int count = 0;
        // scorre tutti i campi presenti

        for (String chiave : campiGriglia.get(0)) {
            row.addView(addTextView(campi.get(chiave).getDescrizione(), true, count));
            count++;
        }
        tabellaH.addView(row);

        //
        // CORPO
        //
        tabella = (TableLayout) findViewById(R.id.tabella);
        tabella.removeAllViews();


        listaValori.clear();
        for (int i = 0; i < listaValoriTemp.size(); i++) {
            HashMap<String, String> riga = listaValoriTemp.get(i);
            if (saltaValori(riga)) continue;
            listaValori.add(riga);
        }


        //For per togliere tutte le righe finte
        if(listaValori != null && listaValori.size() >= 1) {
            if (listaValori.get(0).get("Modifica") != null) {
                for (int i = 0; i < listaValori.size(); i++) {
                    HashMap<String, String> riga = listaValori.get(i);
                    if (riga.get("VLTYP").equals("")) {
                        listaValori.remove(i);
                    }
                }
            }
        } else {
            Global.alert(this, "Nessun Record");
            customBackPressed();
        }
        for (int i = 0; i < listaValori.size(); i++) {
            int currentRow = 0;
            for (ArrayList<String> rigaCorrente : campiGriglia) {
                ++currentRow;

                Log.d("GRIGLIA LOOP:", String.valueOf(i));
                //Log.e("I-->", String.valueOf(i));
                row = new TableRow(Griglia.this);
                // La prima riga è alta 48
                row.setMinimumHeight((currentRow == 1 ? 48 : 1));
                lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);

                HashMap<String, String> riga = listaValori.get(i);

                boolean modula = false;
                String fieldTipVal = riga.get("VLTYP");


                if (fieldTipVal != null && fieldTipVal.equalsIgnoreCase("MOD")) {
                    modula = true;
                }

                if (riga.get("Modifica") != null) {
                    String modificaValore = riga.get("Modifica");
                    if (!modificaValore.equals("9999")) {
                        modula = false;
                    } else {
                        modula = true;
                    }
                }

                    int tag = i * RIGHE + (currentRow - 1);
                    int tagRiga = tag;

                    row.setTag(tag);

                    int idRiga = riga.toString().hashCode();

                    // alterna i colori per migliorare la leggibilità
                    if (i % 2 == 0) {
                        row.setBackgroundResource(R.color.omarevenGriglia);
                    } else {
                        row.setBackgroundResource(R.color.omaroddGriglia);
                    }

                    // Checkbox solo la prima riga
                    if (currentRow == 1) {
                        row.addView(createCheckBox(Griglia.this, checkedRowsBeforeUpdate.contains(idRiga), idRiga));
                        if (modula) {
                            row.getChildAt(0).setEnabled(false);
                        }
                    }

                    // aggiunge tutte le celle della tabella gestendo in maniera diversa il campo LOG perchè è una immagine
                    int loop = 0;

                    for (String chiave : rigaCorrente) {
                        loop++;
                        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                        FieldItem fieldInfo = campi.get(chiave);

                        boolean fieldBold = false;
                        int fieldColor = -1;

                        if (fieldInfo != null) {
                            params.span = campi.get(chiave).getSpan();

                            fieldBold = campi.get(chiave).isBold();
                            fieldColor = campi.get(chiave).getColor();
                        }
                        if (chiave.equals("LOG")) {
                            ImageView cella = new ImageView(Griglia.this);
                            if (Global.dpi.equals("xhdpi")) {
                                cella.setPadding(5, 6, 5, 6);
                            } else {
                                cella.setPadding(10, 13, 10, 13);
                            }

                            cella.setImageResource(getImageId(this, "ico_" + riga.get(chiave).toLowerCase().replaceAll("@", "")));
                            cella.setLayoutParams(params);

                            row.addView(cella);
                        } else {
                            TextView cella = addTextView(isRight(chiave), Global.getTranslation(riga.get(chiave)));

                            cella.setTextSize(TypedValue.COMPLEX_UNIT_SP, getRowDimension(currentRow));

                            if (isBold(chiave) || fieldBold) {
                                cella.setTypeface(Typeface.create(FONT_APP, Typeface.BOLD));
                            }
                            if (fieldColor != -1) {
                                cella.setTextColor(fieldColor);
                            }

                            cella.setLayoutParams(params);

                            row.addView(cella);
                            // calcola i totali di riga
                            // per ora cerca di convertire tutto in float se riesce lo considera un numerp
                            totale[loop] += Global.prendiFloat(riga.get(chiave));
                        }

                    }


                    row.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            clickRow((TableRow) v);

                        }
                    });

                    row.setOnLongClickListener(new View.OnLongClickListener() {
                        public boolean onLongClick(View v) {
                            //v.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                            ArrayList<HashMap<String, String>> listaTemp = new ArrayList<HashMap<String, String>>();
                            Log.d("LONG PRESS", String.valueOf(v.getTag()));
                            HashMap<String, String> riga = listaValori.get((int) v.getTag() / RIGHE);
                            for (String chiave : campi.keySet()) {
                                listaTemp.add(Global.putListaValori(campi.get(chiave).getDescrizione() + " (" + chiave + ")", riga.get(chiave)));
                            }
                            Intent intentTemp = new Intent(Griglia.this, Dettaglio.class);
                            intentTemp.putExtra("listaValori", listaTemp);
                            intentTemp.putExtra("titolo", "Dettaglio " + riga.get("MATNR"));
                            startActivity(intentTemp);
                            return true;
                        }
                    });

                    tabella.addView(row);

            }
        }

        // Stampo 8 righe vuote in fondo
        for (int i = 0; i < RIGHE_VUOTE; i++) {
            TableRow emptyRow = new TableRow(Griglia.this);
            emptyRow.setBackgroundResource(R.color.omarbackground);
            emptyRow.addView(addTextViewVuota());
            ArrayList<String> rigaCorrente = campiGriglia.get(0);

            for (String chiave : rigaCorrente) {
                emptyRow.addView(addTextViewVuota());
            }
            tabella.addView(emptyRow);
        }

        //
        // PIE DI PAGINA
        //
        tabellaF = (TableLayout) findViewById(R.id.tabellaf);
        tabellaF.removeAllViews();

        TableRow row4 = new TableRow(Griglia.this);
        row4.setBackgroundResource(R.drawable.background_splash);
        row4.addView(addTextView(String.valueOf(listaValori.size()),true));
        int loopFooter = 0;
        for (String chiave: campiGriglia.get(0)) {
            loopFooter++;
            if (totale[loopFooter]>0) {
                row4.addView(addTextView(isRight(chiave), String.valueOf(totale[loopFooter]).replaceAll("\\.",",").replaceAll(",0",""), true));
            } else {
                row4.addView(addTextView(String.valueOf(""), true));
            }
        }
        tabellaF.addView(row4);

        progress.dismiss();
        reloadData = true;

        ScrollView sv = findViewById(R.id.layout);
        ConstraintLayout.LayoutParams layoutParamsScrollView = (ConstraintLayout.LayoutParams)sv.getLayoutParams();

        Toolbar tb = (Toolbar) findViewById(R.id.my_toolbar);

        // La scrollView si stacca dal top sommando l'altezza dell header e quella della toolbar
        int scrollViewMarginTop = HEADER_HEIGHT + tb.getHeight() - 5;
        layoutParamsScrollView.topMargin = scrollViewMarginTop;
        sv.setLayoutParams(layoutParamsScrollView);


        checkAllCheckBox();
    }

    private void checkAllCheckBox() {
        ArrayList<Integer> selectedRowsFromSavedPage = getIntent().getIntegerArrayListExtra("savedPageCheckBoxes");
        if (selectedRowsFromSavedPage != null) {
            for (int i = 0; i < selectedRowsFromSavedPage.size(); i++) {
                int sel_riga = (selectedRowsFromSavedPage.get(i) * RIGHE);

                View child = tabella.getChildAt(sel_riga);

                if (child instanceof TableRow) {
                    TableRow rowToCheck = (TableRow) child;

                    for (int t = 0; t < rowToCheck.getChildCount(); t++) {
                        View view = rowToCheck.getChildAt(t);
                        if (view instanceof CheckBox) {
                            CheckBox cbToCheck = (CheckBox) view;
                            if (cbToCheck.isEnabled()) {
                                cbToCheck.setChecked(true);
                            }
                        }
                    }
                }
            }
        }
    }

    public void impostaTuttiCampi() {
        campi = new LinkedHashMap<String, FieldItem>();

    }

    public void impostaCampiGriglia() {
        campiGriglia = new ArrayList<>();

        // Genero N righe vuote
        for (int i = 0; i < RIGHE; i++) {
            campiGriglia.add(new ArrayList<String>());
        }
    }
    public String getUrl(int ciclo) {
        return "";
    }

    public int getCicli() {
        return 1;
    }

    public String getCampo (int pos) {
        String temp = "";

        int count = 0;
        for (String chiave : campiGriglia.get(0)) {
            if (count==pos) {
                temp = chiave;
            }
            count++;
        }
        return temp;
    }

    public void aggiungiCampiCalcolati(int ciclo) {

    }


    public static int getImageId(Context context, String imageName) {
        return context.getResources().getIdentifier("drawable/" + imageName, null, context.getPackageName());
    }

    public void openInfoStockMatr (boolean stock, HashMap<String, String> lista){
        Intent intent = new Intent(this, InfoStockZmag.class);
        intent.putExtra("menuItem", Global.INFO_STOCK_MATERIALE);
        intent.putExtra("titolo", "Info Stock Materiale " + (stock? "per Stock":" per ODP") + ": " +  lista.get("MATNR"));
        intent.putExtra("Materiale",  lista.get("MATNR"));
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

    public void openLT24 (boolean stock, HashMap<String, String> lista){
        openLT24(stock, lista, 30);
    }
    public void openLT24 (boolean stock, HashMap<String, String> lista, int giorni){
        Intent intent = new Intent(this, LT24.class);

        intent.putExtra("titolo", "11 LT24 - Ultimi Mov Materiale - " + (giorni) + " giorni");
        intent.putExtra("menuItem", Global.LT24);
        intent.putExtra("info2", lista.get("MATNR"));
        intent.putExtra("matnr", lista.get("MATNR"));
        intent.putExtra("Giorni", String.valueOf(giorni));
        intent.putExtra("Materiale", lista.get("MATNR"));
        startActivity(intent);
    }
    public void openInfoStockUbic (boolean stock, HashMap<String, String> lista){
        Intent intent = new Intent(this, InfoStockZmag.class);
        intent.putExtra("menuItem", Global.INFO_STOCK_UBICAZIONE);
        intent.putExtra("Materiale",  "");
        intent.putExtra("titolo", "Info Stock Ubic " + (stock? "per Stock":" per ODP") + ": " +  lista.get("LGTYP") + " " +  lista.get("LGPLA"));
        intent.putExtra("Tipo + ubic", lista.get("LGTYP")+lista.get("LGPLA"));
        Log.d("URL: openInfoStockUbic", lista.get("LGTYP")+lista.get("LGPLA"));

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
    public String getLabel2(HashMap<String, String> item) {
        String matnr = item.get("MATNR");
        if (matnr==null) matnr = "";
        String descr1 = item.get("MAKTX");
        if (descr1==null) descr1 = "";
        String descr2 = item.get("REFNT");
        if (descr2==null) descr2 = "";
        return (matnr + " "+  descr1 +  " " + descr2);
    }


    public boolean saltaValori (HashMap<String, String> item) {

        // di default non salta nulla
        if (testoDaCercare.equals("")) return false;

        // se lo trovo usando il bolean ||
        boolean temp = false;

        // Ciclo tutti i valori della riga
        for (String key : item.keySet()) {
            String text = item.get(key);

            temp = temp || (text != null && text.toLowerCase().contains(testoDaCercare.toLowerCase()));
        }


        return !temp;
    }

    public static boolean isBold(String campo) {
        boolean temp = false;

        if (campo.startsWith("Qt")) temp = true;

        for (String elem: Global.fieldBold) {
            if (elem.equals(campo)) temp = true;
        }

        return temp;
    }

    public static boolean isRight(String campo) {
        boolean temp = false;

        if (campo.startsWith("Qt")) temp = true;

        switch (campo) {
            case "VSOLM_C":
            case "VSOLM":
            case "GESME":
            case "VERME":
            case "EINME":
            case "AUSME":
            case "BDMNG":
            case "RIMANENTI":
            case "ENMNG":
            case "VEMNG":
            case "VSOLA":
            case "NISTM":
                temp = true;
        }

        return temp;
    }

    public void setRiga(TableRow trow, int valore) {
        ////// RIGA2 //////
        //trow.setBackground((valoriSelezionati.get(v.getTag())));
        if ((valore / RIGHE) % 2 == 0) {
            trow.setBackgroundResource(R.color.omarevenGriglia);
        } else {
            trow.setBackgroundResource(R.color.omaroddGriglia);
        }
        for (int y = 0; y < ((TableRow) trow).getChildCount(); y++) {
            View tv = ((TableRow) trow).getChildAt(y);
            if (tv instanceof TextView) {
                int color = ((TextView) tv).getCurrentTextColor();
                if (color != Color.BLUE) {
                    ((TextView) tv).setTextColor(Color.BLACK);
                    tv.setBackgroundResource(R.drawable.border_style);
                }
            }
        }
        /////////////////////
    }
    public void setRigaBianca(TableRow trow) {
        for (int y = 0; y < trow.getChildCount(); y++) {
            View tv = trow.getChildAt(y);
            if (tv instanceof TextView) {
                int color = ((TextView) tv).getCurrentTextColor();
                if (color != Color.BLUE) {
                    ((TextView) tv).setTextColor(Color.WHITE);
                    tv.setBackgroundResource(R.drawable.border_style_transparent);
                }
            }
        }
    }


    public void clickRow(TableRow r) {
        int riga =  ((Integer) r.getTag()).intValue();
        riga = riga / RIGHE;
        riga = riga * RIGHE;

        TableRow v = (TableRow) tabella.getChildAt(riga);
        // evidenzio il campo checkbox della riga selezionata
        CheckBox cb = (CheckBox) ((TableRow) v).getChildAt(0);
        // prendo dalla lista i valori di quella riga
        HashMap<String, String> item = listaValori.get(Integer.parseInt(String.valueOf(v.getTag())) / RIGHE);

        // se riclicco sulla riga selezionata lo riporta allo stato normale
        if (valoriSelezionati.containsKey(v.getTag())) {

            if (valoriDisabilitati.get(v.getTag())==null) {

                int valore = Integer.decode(String.valueOf(v.getTag()));
                setRiga((TableRow) v, valore);
                TableRow trow2 = (TableRow) tabella.getChildAt(riga +1);
                setRiga(trow2, valore);
                TableRow trow3 = (TableRow) tabella.getChildAt(riga +2);
                setRiga(trow3, valore);
                TableRow trow4 = (TableRow) tabella.getChildAt(riga +3);
                setRiga(trow4, valore);

            }
            valoriSelezionati.remove(v.getTag());
            cb.setChecked(false);
            cb.jumpDrawablesToCurrentState();

        } else {


            // deselezionata eventuali altri valori ma non toglie il check
            for (Object o : valoriSelezionati.keySet()) {
                // COLORA LO SFONDO CON IL COLORE ORIGINALE
                TableRow trow = (TableRow) tabella.getChildAt(((Integer) o).intValue());
                if (valoriDisabilitati.get(o) == null) {
                    int valore = ((Integer) o).intValue();
                    setRiga(trow, valore);

                    TableRow trow2 = (TableRow) tabella.getChildAt(((Integer) o).intValue() + 1);
                    setRiga(trow2, valore);

                    TableRow trow3 = (TableRow) tabella.getChildAt(((Integer) o).intValue() + 2);
                    setRiga(trow3, valore);

                    TableRow trow4 = (TableRow) tabella.getChildAt(((Integer) o).intValue() + 3);
                    setRiga(trow4, valore);

                }

                valoriSelezionati.remove(trow.getTag());
            }


            valoriSelezionati.put(v.getTag(), v.getBackground());
            info3 = getLabel2(item);
            labelInfo3.setText(info3);

            // se il bottone è visibile per scelta di pagina ma il codice LOG non lo permette lo nascondo
            if (bottoneAvanti.getVisibility()==View.VISIBLE) {
                if (("@DF@").equals(listaValori.get(Integer.parseInt(String.valueOf(v.getTag())) / RIGHE).get("LOG"))) {
                    bottoneAvanti.setVisibility(View.INVISIBLE);
                } else {
                    bottoneAvanti.setVisibility(View.VISIBLE);
                }
            }


            // COLORA LO SFONDO DI SELECTED E METTE LE SCRITTE IN BIANCO SULLA RIGA PRINCIPALE
            v.setBackgroundResource(R.color.selected);
            setRigaBianca((TableRow) v);

            TableRow trow2 = (TableRow) tabella.getChildAt(riga + 1);
            trow2.setBackgroundResource(R.color.selected);
            setRigaBianca(trow2);

            TableRow trow3 = (TableRow) tabella.getChildAt(riga + 2);
            trow3.setBackgroundResource(R.color.selected);
            setRigaBianca(trow3);

            TableRow trow4 = (TableRow) tabella.getChildAt(riga + 3);
            trow4.setBackgroundResource(R.color.selected);
            setRigaBianca(trow4);

            if (Global.getImpostazioniApp("SELEZIONA").equals("SI")) {
                if (cb.isEnabled()) {
                    cb.setChecked(true);
                    cb.jumpDrawablesToCurrentState();
                }
            }

        }

    }

    public int getRowDimension(int riga) {
        int current = riga - 1;
        if (current > dimensioniTestoPerRiga.size()) {
            return dimensioniTestoPerRiga.get(dimensioniTestoPerRiga.size());
        }
        return dimensioniTestoPerRiga.get(current);
    }

    public void aggiungiCampoRiga(int riga, String campo) {

        campiGriglia.get(riga - 1).add(campo);
    }

    public void setCampo (String campo, String descrizione, int span, boolean calcolato, boolean translate) {
        campi.put (campo, new FieldItem(descrizione, span, calcolato).setTranslate(translate));
    }
    public void setCampo (String campo, String descrizione, int span, boolean translate) {
        setCampo(campo, descrizione, span, false, translate);
    }
    public void setCampo (String campo, String descrizione, int span) {
        setCampo(campo, descrizione, span, false, false);
    }

    public void setCampo (String campo, int span) {
        setCampo(campo, Global.getLabel(campo), span);
    }

    public void setCampo(String campo, boolean calcolato) {
        setCampo(campo, Global.getLabel(campo), 1, calcolato, false);
    }
    public void setCampo (String campo, String descrizione) {
        setCampo(campo, descrizione, 1);
    }
    public void setCampo (String campo, String descrizione, boolean translate) {
        setCampo(campo, descrizione, 1);
    }
    public void setCampo (String campo) {
        setCampo(campo, Global.getLabel(campo));
    }

    public void setCampo (String campo, boolean bold, int color) {
        campi.put (campo, new FieldItem(Global.getLabel(campo), 1, false).setBold(bold).setColor(color));
    }



    public void setCheckBoxColor(CheckBox checkBox, int checkedColor, int uncheckedColor) {
        int states[][] = {{android.R.attr.state_checked}, {}};
        int colors[] = {checkedColor, uncheckedColor};
        CompoundButtonCompat.setButtonTintList(checkBox, new
                ColorStateList(states, colors));
    }
}


class MapComparator implements Comparator<Map<String, String>> {
    private final String key;
    private final boolean desc;

    public MapComparator(String key, boolean desc){
        this.key = key;
        this.desc = desc;
    }

    public int compare(Map<String, String> first,
                       Map<String, String> second){

        int ritorno = 0;
        // TODO: Null checking, both for maps and values
        String firstValue = first.get(key);
        if (firstValue==null) firstValue = "";

        String secondValue = second.get(key);
        if (secondValue==null) secondValue = "";

        if (isNumeric(firstValue) && isNumeric(secondValue)) {
            ritorno =  Math.round(Global.getFloat(firstValue)-Global.getFloat(secondValue));
        }

        ritorno =  firstValue.compareTo(secondValue);

        if (desc) return -ritorno;
        else return ritorno;

    }


    public static boolean isNumeric(String str)
    {
        str = str.replaceAll(",",".");
        try
        {
            float d = Float.parseFloat(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }





}