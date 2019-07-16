package com.ancoragroup.wm;

import java.util.HashMap;
import java.util.List;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.provider.MediaStore;
import android.renderscript.ScriptGroup;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class OmarAdapter extends SimpleAdapter {
    private int[] colors = new int[] {
            R.color.omareven,
            R.color.omarodd
    };

    private final short MAX_CHARS_PER_LINE = 50;
    private final short BASE_LINE_HEIGHT = 90;
    private final short HEIGHT_PER_LINE = 40;

    final static String FONT_APP = "sans-serif-condensed";

    boolean first = false;
    Context mContext = null;

    boolean type = false;
    String title = "";

    HashMap<String, String> savedValues = new HashMap<>();




    public OmarAdapter(Context context, List<HashMap<String, String>> items, int resource, String[] from, int[] to, boolean type) {
        super(context, items, resource, from, to);

        this.type = type;

        mContext = context;
    }

    public OmarAdapter(Context context, List<HashMap<String, String>> items, int resource, String[] from, int[] to) {
        super(context, items, resource, from, to);

        this.type = false;

        mContext = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View riga = super.getView(position, convertView, parent);

        HashMap<String, String> val = (HashMap<String, String>) getItem(position);

        int col = ((GridView) parent).getNumColumns();
        position = position / col;
        int colorPos = position % colors.length;

        riga.setBackgroundResource(colors[colorPos]);

        TextView titolo = riga.findViewById(R.id.titolo);
        title = titolo.getText().toString();

        if (val != null && val.get("Tipo").equals("E")) {
            riga.setLayoutParams(new ViewGroup.LayoutParams(riga.getLayoutParams().width, BASE_LINE_HEIGHT));
            if (first) {
                riga.setBackgroundResource(R.color.colorERROR);
                titolo.setTypeface(Typeface.create(FONT_APP, Typeface.BOLD));
            } else  {
                int height = BASE_LINE_HEIGHT;

                // Alzo la riga se serve
                height += calculateHeightByText(titolo.getText().toString());

                riga.setLayoutParams(new ViewGroup.LayoutParams(riga.getLayoutParams().width, height));

                if (Global.isSamsung()) {
                    riga.setPadding(0, 2, 0, 2);
                    riga.setLayoutParams(new ViewGroup.LayoutParams(riga.getLayoutParams().width, height * 3));
                }

                //riga.setBackgroundResource(R.drawable.background_esito_error);
                riga.setBackgroundResource(R.color.colorERROR);
            }

            first = !first;
        }

        if (val != null && (val.get("Tipo").equals("I") || val.get("Tipo").equals("S"))) {
            riga.setLayoutParams(new ViewGroup.LayoutParams(riga.getLayoutParams().width, BASE_LINE_HEIGHT));
            if (first) {
                riga.setBackgroundResource(R.color.colorOK);
                titolo.setTypeface(Typeface.create(FONT_APP, Typeface.BOLD));
            } else  {
                int height = BASE_LINE_HEIGHT;

                // Alzo la riga se serve
                height += calculateHeightByText(titolo.getText().toString());

                riga.setLayoutParams(new ViewGroup.LayoutParams(riga.getLayoutParams().width, height));
                if (Global.isSamsung()) {
                    riga.setLayoutParams(new ViewGroup.LayoutParams(riga.getLayoutParams().width, height * 3));
                }

                //riga.setBackgroundResource(R.drawable.background_esito_ok);
                riga.setBackgroundResource(R.color.colorOK);
            }

            first = !first;
        }

        /*
        if (val != null && (val.get("Tipo").equals("E") || val.get("Tipo").equals("I")) && first) {
            titolo.setTypeface(Typeface.create(FONT_APP, Typeface.BOLD));
            first = false;
        }
        */
        if (val != null && val.get("Mandatory") != null && val.get("Mandatory").equals("true")) {
            titolo.setTypeface(Typeface.create(FONT_APP, Typeface.BOLD));

        }

        Object descrizione = riga.findViewById(R.id.descrizione);
        if (descrizione != null) {
            if (descrizione instanceof EditText) {
                EditText temp = ((EditText) descrizione);
                temp.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

                if (Global.getImpostazioniApp("TASTIERA NUM").equals("SI")) {
                    temp.setInputType(InputType.TYPE_CLASS_PHONE);
                    temp.setKeyListener(DigitsKeyListener.getInstance("0123456789._R#-,*HUAhua"));
                } else {
                    temp.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                }

                if (val != null && val.get("Tipo").equals("RADIO")) {
                    temp.setText("");
                } else if (val != null && val.get("Tipo").equals("BOOL")) {
                    temp.setText("");
                } else if (val != null && val.get("Tipo").equals("NUM")) {
                    temp.setInputType(InputType.TYPE_CLASS_PHONE);
                    temp.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    temp.setKeyListener(DigitsKeyListener.getInstance("0123456789._R#-,*HUAhua"));
                } else if (val != null && val.get("Tipo").equals("READONLY")) {
                    // riga
                    riga.setBackgroundResource(R.color.colorOK);
                    riga.setLayoutParams(new RelativeLayout.LayoutParams(-1, 50));

                    // Se è samsung alzo la riga a 100 px
                    if (Global.isSamsung()) {
                        riga.setLayoutParams(new RelativeLayout.LayoutParams(-1, 70));
                    }

                    // edittext
                    temp.setEnabled(false);
                    temp.setBackgroundResource(android.R.color.transparent);

                    if (Global.dpi.equals("xhdpi")) {
                        temp.setPadding(4,0,4,2);
                        //temp.setTextSize(8);
                        titolo.setPadding(2,0,2,1);
                        //titolo.setTextSize(8);
                    } else {
                        temp.setPadding(8,4,0,0);
                        temp.setTextSize(14);
                        titolo.setPadding(8,3,0,0);
                        titolo.setTextSize(14);
                    }



                    titolo.setTypeface(Typeface.create(FONT_APP, Typeface.BOLD));
                    titolo.setBackgroundResource(android.R.color.transparent);
                }  else if (val != null && val.get("Tipo").equals("HIDDEN")) {
                    riga.setVisibility(View.INVISIBLE);

                } else {
                    if ("RFID".equals(val.get("Tipo"))) {
                        CheckBox cc = (CheckBox) riga.findViewById(R.id.checkBoxConferma);
                        cc.setVisibility(View.VISIBLE);

                        String sernr = val.get("Descrizione");
                        Boolean toConfirm = false;

                    }
                    temp.setSingleLine(true);
                    temp.setImeOptions(EditorInfo.IME_ACTION_DONE);
                }

                // GIGI
            }
        }


        Object onoff = riga.findViewById(R.id.onoff);
        if (onoff != null) {
            if (onoff instanceof ToggleButton) {
                if (val != null && val.get("Tipo").equals("BOOL")) {
                    String descr = val.get("Descrizione") + "; ; ; ; ;";
                    String[] values = descr.split(";");

                    ((ToggleButton) onoff).setText(values[0]);
                    ((ToggleButton) onoff).setTextOff(values[1]);
                    ((ToggleButton) onoff).setTextOn(values[2]);
                    ((ToggleButton) onoff).setBackgroundResource(R.drawable.togglebutton_selector);
                    if (values[0].equals(values[2])) {
                        ((ToggleButton) onoff).setChecked(true);
                    }
                    ((ToggleButton) onoff).setVisibility(View.VISIBLE);
                    ((EditText) descrizione).setVisibility(View.INVISIBLE);
                } else {
                    ((ToggleButton) onoff).setVisibility(View.INVISIBLE);
                }
            }
        }

        Object radio = riga.findViewById(R.id.radio);
        if (radio != null) {
            if (radio instanceof RadioGroup) {
                if (val != null && val.get("Tipo").equals("RADIO")) {
                    String descr = val.get("Descrizione") + "; ; ; ; ;";
                    String[] values = descr.split(";");

                    String valoreSelezionato = values[0];
                    if (!values[1].trim().equals("")) {
                        ((RadioButton) riga.findViewById(R.id.radio1)).setText(values[1]);
                        ((RadioButton) riga.findViewById(R.id.radio1)).setChecked(valoreSelezionato.equals(values[1]));
                        // !F = FORSE
                        if (values[1].equals("!F"))
                            ((RadioButton) riga.findViewById(R.id.radio1)).setVisibility(View.INVISIBLE);
                        //if (values[1].equals ("!F"))  ((RadioButton) riga.findViewById(R.id.radio1)).setText("");
                    }
                    if (!values[2].trim().equals("")) {
                        ((RadioButton) riga.findViewById(R.id.radio2)).setText(values[2]);
                        ((RadioButton) riga.findViewById(R.id.radio2)).setChecked(valoreSelezionato.equals(values[2]));
                    }
                    if (!values[3].trim().equals("")) {
                        ((RadioButton) riga.findViewById(R.id.radio3)).setText(values[3]);
                        ((RadioButton) riga.findViewById(R.id.radio3)).setChecked(valoreSelezionato.equals(values[3]));
                    } else {
                        ((RadioButton) riga.findViewById(R.id.radio3)).setVisibility(View.INVISIBLE);
                    }

                    if (descrizione != null) ((View) descrizione).setVisibility(View.INVISIBLE);
                } else {
                    ((RadioGroup) radio).setVisibility(View.INVISIBLE);

                }
            }



                Object copia = riga.findViewById(R.id.bottoneCopia);
                if (copia != null) {
                    if (copia instanceof Button) {
                        Button temp = ((Button) copia);
                        temp.setTag(position);
                        temp.setVisibility(((EditText) descrizione).getVisibility());
                        if (val != null && val.get("Tipo").equals("READONLY"))
                            temp.setVisibility(View.INVISIBLE);
                    }
                }


                Object possibilita = riga.findViewById(R.id.bottonePossibilità);
                if (possibilita != null) {
                    if (possibilita instanceof Button) {
                        Button temp = ((Button) possibilita);
                        temp.setTag(position);
                        temp.setVisibility(((EditText) descrizione).getVisibility());
                        if (val != null && val.get("Tipo").equals("READONLY"))
                            temp.setVisibility(View.INVISIBLE);
                    }
                }

                Log.d("TitoloOmarAdapter", titolo.getText().toString());

            if (!type) {

                ((View) copia).setVisibility(View.VISIBLE);
                ((View) possibilita).setVisibility(View.GONE);

            } else {
                Log.d("TESTTitle", title);
                if (title.equals("HU") || title.equals("HU1") || title.equals("HU2") || title.equals("HU3")) {

                    ((View) copia).setVisibility(View.GONE);
                    ((View) possibilita).setVisibility(View.VISIBLE);
                } else {

                    ((View) copia).setVisibility(View.VISIBLE);
                    ((View) possibilita).setVisibility(View.GONE);

                }
            }
            if (val != null && (val.get("Tipo").equals("READONLY") || val.get("Tipo").equals("RADIO") || val.get("Tipo").equals("BOOL"))){

                ((View) copia).setVisibility(View.GONE);
                ((View) possibilita).setVisibility(View.GONE);
            }

                /*
        // gestione della riga già disabilitata
        if (val != null && val.get("Stato")!=null && val.get("Stato").equals("false")) {
            riga.setEnabled(false);
            riga.setBackgroundResource(R.color.omardisableGriglia);
        }*/

            Log.d("TESTCONTEXT", mContext + "");

        }
        return riga;
    }

    private int calculateHeightByText(String text) {
        return calculateHeightByChars(text != null ? text.length() : 1);
    }
    private int calculateHeightByChars(int chars) {
        return (HEIGHT_PER_LINE * (int)(chars / MAX_CHARS_PER_LINE));
    }

}

