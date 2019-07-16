package com.ancoragroup.wm;

/**
 * Created by omar.valenti on 14/11/2017.
 */

public class FieldItem {

    private String descrizione ="";
    private int span = 1;
    private boolean calcolato = false;

    private boolean bold = false;
    private int color = -1;
    private boolean translate = false;

    public FieldItem(String descrizione, int span) {
        this(descrizione, span, false);
    }

    public FieldItem(String descrizione, int span, boolean calcolato) {
        this.descrizione = descrizione;
        this.span = span;
        this.calcolato = calcolato;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public int getSpan() {
        return span;
    }

    public boolean isCalcolato() {
        return calcolato;
    }

    public boolean isBold() {
        return bold;
    }

    public int getColor() {
        return color;
    }

    public FieldItem setColor(int color) {
        this.color = color;
        return this;
    }

    public FieldItem setBold(boolean bold) {
        this.bold = bold;
        return this;
    }

    public boolean isTranslate() {
        return translate;
    }

    public FieldItem setTranslate(boolean translate) {
        this.translate = translate;
        return this;
    }
}
