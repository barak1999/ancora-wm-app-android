package com.ancoragroup.wm;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TableRow;

import java.util.LinkedHashMap;

public class ListaPrelProd extends Griglia {
    private String REFNRS = "";
    private String DESTINAZIONE = "";
    private String TITOLO = "";
    private String REFNTS = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        impostaBottoneInvisibile(bottoneAzione1);
        impostaBottoneInvisibile(bottoneAzione2);
        impostaBottoneInvisibile(bottoneAzione3);
        impostaBottoneSelezioneMultipleRighe(bottoneAvanti, "Dettagli");
    }

    @Override
    public void impostaTuttiCampi() {
        super.impostaTuttiCampi();
        
        setCampo("REFNT",4);
        setCampo("VBELN");
        setCampo("REFNR");
        setCampo("NLTYP");
        setCampo("NLPLA");
        setCampo("VLTYP");
        setCampo("VLPLA");
        setCampo("VSOLM_C", true, Color.BLUE);
        setCampo("ALTME", Global.getLabel("ALTME"), true);
        setCampo("MAKTX");
        setCampo("ZZCID");
        setCampo("ZZTBPR");
        setCampo("SPACE1", "                                                          ");
        setCampo("SPACE2", "");

    }

    @Override
    public void impostaCampiGriglia() {
        super.impostaCampiGriglia();

        aggiungiCampoRiga(1, "NLTYP");
        aggiungiCampoRiga(1, "NLPLA");
        aggiungiCampoRiga(1, "REFNR");
        aggiungiCampoRiga(1, "SPACE1");


        aggiungiCampoRiga(2, "SPACE2");
        aggiungiCampoRiga(2, "REFNT");

    }


    @Override
    public String getUrl(int ciclo) {
        String url = "";
        url = Global.serverURL + Global.listaPrelProdXML;

        url = url.replaceAll("#I_BADGE#",Global.mioBadge);
        url = url.replaceAll("#I_LGNUM#",Global.getImpostazoniSAP("Numero Magazzino"));
        url = url.replaceAll("#I_WORKSTATION#",Global.getImpostazoniSAP("Societa"));
        url = url.replaceAll("#I_PAGNO#","");
        url = url.replaceAll("#I_OBJTYPE#","G"); //C = Vendite P= Prod
        Log.d("URL", url);

        return url;
    }


    @Override
    public void bottoneAvantiClickStart() {
        REFNRS = "";
        DESTINAZIONE = "";
        TITOLO = "";
        REFNTS = "";
    }

    @Override
    public void bottoneAvantiClickEnd() {
        Intent intent = new Intent(ListaPrelProd.this, ListaPrelItem.class);
        intent.putExtra("menuItem", menuItem);

        intent.putExtra("titolo", TITOLO);
        intent.putExtra("REFNRS", REFNRS);
        intent.putExtra("REFNTS", REFNTS);
        intent.putExtra("Destinazione", DESTINAZIONE);
        startActivity(intent);
    }

    @Override
    public void bottoneAvantiSingolaRiga (int sel_riga) {
        REFNRS += listaValori.get(sel_riga).get("REFNR") + "|";
        DESTINAZIONE += listaValori.get(sel_riga).get("NLTYP") + listaValori.get(sel_riga).get("NLPLA") + "|";
        if (TITOLO.equals("")) TITOLO = "Lista.Pr. " + listaValori.get(sel_riga).get("REFNR");
        else TITOLO += " + " + listaValori.get(sel_riga).get("REFNR");
        REFNTS += listaValori.get(sel_riga).get("REFNT") + "|";

    }

}
