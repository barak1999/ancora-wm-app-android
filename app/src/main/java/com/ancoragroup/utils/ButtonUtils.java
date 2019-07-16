package com.ancoragroup.utils;

import android.content.Context;
import android.view.View;
import android.widget.Button;

/**
 * Created by aveneziano on 11/04/2018.
 */

public class ButtonUtils {
    private static final ButtonUtils ourInstance = new ButtonUtils();

    private Context context;

    public static ButtonUtils getInstance(Context context) {
        ourInstance.context = context;
        return ourInstance;
    }

    private ButtonUtils() { }

    // Imposta sfondo immagine
    public void impostaBottoneSfondoImmagine(Button btn, int res) {
        btn.setText("");
        btn.setBackgroundResource(res);
    }

    // Imposta testo, colore di sfondo, testo, tag
    public void setButtonProperties(Button btn, String text, int backgroundColor, int color, int tag) {
        btn.setText(text);
        btn.setBackgroundColor(context.getResources().getColor(backgroundColor));
        btn.setTextColor(context.getResources().getColor(color));
        if (tag != -1) {
            btn.setTag(context.getString(tag));
        }
    }

    // Imposta bottone invisibile
    public void impostaBottoneInvisibile(Button btn) {
        btn.setText("");
        btn.setVisibility(View.INVISIBLE);
    }
}
