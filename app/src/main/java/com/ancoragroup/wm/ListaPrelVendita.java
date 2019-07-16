package com.ancoragroup.wm;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TableRow;

import java.util.LinkedHashMap;

public class ListaPrelVendita extends Griglia {
    private String VBELNS = "";
    private String DESTINAZIONE = "";
    private String TITOLO = "";
    private String REFNTS = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        impostaBottoneInvisibile(bottoneAzione1);
        impostaBottoneSelezioneMultipleRighe(bottoneAvanti, "Dettagli");
    }

    @Override
    public void impostaTuttiCampi() {
        super.impostaTuttiCampi();

        setCampo("REFNT");
        setCampo("VBELN", "Codice");
        setCampo("REFNR");
        setCampo("NLTYP");
        setCampo("NLPLA");
        setCampo("VLTYP");
        setCampo("VLPLA");
        setCampo("VSOLM_C", true, Color.BLUE);
        setCampo("ALTME", Global.getLabel("ALTME"), true);
        setCampo("MAKTX");
        setCampo("ZZCID");
        setCampo("ZZTBPRI");
        setCampo("SPACE1", "                                                          ");
        setCampo("SPACE2", "");

    }
    @Override
    public void impostaCampiGriglia() {
        super.impostaCampiGriglia();

        aggiungiCampoRiga(1, "VBELN");
        aggiungiCampoRiga(1, "SPACE1");


        aggiungiCampoRiga(2, "SPACE2");
        aggiungiCampoRiga(2, "REFNT");
        aggiungiCampoRiga(2, "SPACE1");

    }

    @Override
    public String getUrl(int ciclo) {
        String url = "";
        url = Global.serverURL + Global.listaPrelVenditaXML;

        url = url.replaceAll("#I_BADGE#",Global.mioBadge);
        url = url.replaceAll("#I_LGNUM#",Global.getImpostazoniSAP("Numero Magazzino"));
        url = url.replaceAll("#I_WORKSTATION#",Global.getImpostazoniSAP("Societa"));
        url = url.replaceAll("#I_PAGNO#","");
        url = url.replaceAll("#I_OBJTYPE#","C");
        Log.d("URL", url);

        return url;
    }


    @Override
    public void bottoneAvantiClickStart() {
        VBELNS = "";
        DESTINAZIONE = "";
        TITOLO = "";
        REFNTS = "";
    }

    @Override
    public void bottoneAvantiSingolaRiga (int sel_riga) {
        VBELNS += listaValori.get(sel_riga).get("VBELN") + "|";
        DESTINAZIONE += listaValori.get(sel_riga).get("NLTYP") + listaValori.get(sel_riga).get("NLPLA") + "|";
        if (TITOLO.equals("")) TITOLO = "Lista.Pr. " + listaValori.get(sel_riga).get("VBELN");
        else TITOLO += " + " + listaValori.get(sel_riga).get("VBELN");

        REFNTS += listaValori.get(sel_riga).get("REFNT") + "|";
    }

    @Override
    public void bottoneAvantiClickEnd() {
        Intent intent = new Intent(ListaPrelVendita.this, ListaPrelItem.class);
        intent.putExtra("menuItem", menuItem);
        intent.putExtra("titolo", TITOLO);
        intent.putExtra("VBELNS", VBELNS);
        intent.putExtra("REFNTS", REFNTS);
        intent.putExtra("Destinazione", DESTINAZIONE);
        startActivity(intent);
    }
}
