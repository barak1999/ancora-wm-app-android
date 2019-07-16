package com.ancoragroup.wm;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class LT24 extends Griglia {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        impostaBottoneInvisibile(bottoneAzione1);
        impostaBottoneInvisibile(bottoneAzione2);
        impostaBottoneInvisibile(bottoneAzione3);
        impostaBottoneInvisibile(bottoneAvanti);

        // preordinato per data
        orderByDesc = true;
        campoOrdinamento = 3;

    }
    @Override
    public void impostaTuttiCampi() {
        super.impostaTuttiCampi();

        setCampo("VLTYP", "Cat");
        setCampo("VLPLA","Ubic. Prov");
        setCampo("NLTYP", "Cat");
        setCampo("NLPLA","Ubic. Dest");
        setCampo("NLENR");
        setCampo("ALTME", "UMA", true);
        setCampo("VSOLA", true, Color.BLUE);
        setCampo("VSOLM");
        setCampo("NISTM");
        setCampo("REFNR", "Gruppo", 2);
        setCampo("BENUM", 3);
        setCampo("QNAME");
        setCampo("BNAME");
        setCampo("TANUM");
        setCampo("ENAME");
        setCampo("MEINS");
        setCampo("LGORT", "Mag.");
        setCampo("QZEIT");
        setCampo("QDATU");
        setCampo("MAKTX");
    }
    @Override
    public void impostaCampiGriglia() {
        super.impostaCampiGriglia();

        aggiungiCampoRiga(1,"VLTYP"); // Ricerca
        aggiungiCampoRiga(1,"VLPLA"); // Ricerca
        aggiungiCampoRiga(1,"VSOLA");
        aggiungiCampoRiga(1,"QDATU");

        aggiungiCampoRiga(2,"ALTME");
        aggiungiCampoRiga(2,"NLTYP");
        aggiungiCampoRiga(2,"NLPLA");
        aggiungiCampoRiga(2,"NISTM");
        aggiungiCampoRiga(2,"QZEIT");

        aggiungiCampoRiga(3,"LGORT");
        aggiungiCampoRiga(3,"QNAME");
        aggiungiCampoRiga(3,"BNAME");
        aggiungiCampoRiga(3,"ENAME");

    }

    @Override
    public void aggiungiCampiCalcolati(int ciclo) {
        if (listaValoriTemp.size()>0) {
            HashMap<String, String> item =  listaValoriTemp.get(0);
            info3 = item.get("MAKTX");
        }
    }

    @Override
    public String getUrl(int ciclo) {
        String url = "";
        url = Global.serverURL + Global.leggiLT24;

        Calendar ieri = new GregorianCalendar();
        ieri.add(Calendar.DATE, -Integer.decode(myIntent.getStringExtra("Giorni")));
        url = url.replaceAll("#DATA#", Global.norDATE.format(ieri.getTime()));

        if(!myIntent.getStringExtra("Materiale").isEmpty()) {
            url = url.replaceAll("#MATR#", "_AND_MATNR_EQ_'" + myIntent.getStringExtra("Materiale") + "'");
        }  else {
            url = url.replaceAll("#MATR#", "");
        }

        if(!myIntent.getStringExtra("Tipo").isEmpty()) {
            url = url.replaceAll("#VLTYP#", "_AND_VLTYP_EQ_'" + myIntent.getStringExtra("Tipo") + "'");
        }  else {
            url = url.replaceAll("#VLTYP#", "");
        }

        if(!myIntent.getStringExtra("Ubic").isEmpty()) {
            url = url.replaceAll("#VLPLA#", "_AND_VLPLA_EQ_'" + myIntent.getStringExtra("Ubic") + "'");
        }  else {
            url = url.replaceAll("#VLPLA#", "");
        }

        if(!myIntent.getStringExtra("Tipo ").isEmpty()) {
            url = url.replaceAll("#NLTYP#", "_AND_NLTYP_EQ_'" + myIntent.getStringExtra("Tipo ") + "'");
        }  else {
            url = url.replaceAll("#NLTYP#", "");
        }

        if(!myIntent.getStringExtra("Ubic ").isEmpty()) {
            url = url.replaceAll("#NLPLA#", "_AND_NLPLA_EQ_'" + myIntent.getStringExtra("Ubic ") + "'");
        }  else {
            url = url.replaceAll("#NLPLA#", "");
        }





        Log.d("URLLT24", url);

        return url;
    }


}
