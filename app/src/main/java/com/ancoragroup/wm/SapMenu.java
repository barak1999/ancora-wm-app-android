package com.ancoragroup.wm;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

public class SapMenu extends OmarActivity {

    View item;
    Drawable col;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent myIntent = getIntent(); // gets the previously created intent
        menu = myIntent.getStringExtra("menu");

        bottoneAvanti.setVisibility(View.INVISIBLE);


        String[] daS = {"Titolo"};//string array
        int[] aS = {R.id.titolo};//int array of views id's

        //OmarAdapter simpleAdapter = new OmarAdapter(SapMenu.this, Global.getMenu(menu), R.layout.omar_riga_visualizzazione_singolo_valore, daS, aS);

        NuovoAdapter simpleAdapter = new NuovoAdapter(SapMenu.this, Global.getMenu(menu), R.layout.omar_riga_visualizzazione_singolo_valore, daS, aS);
        if (getResources().getConfiguration().orientation==ORIENTATION_PORTRAIT) grigliaViste.setNumColumns(1);
        else grigliaViste.setNumColumns(2);


        grigliaViste.setAdapter(simpleAdapter);//sets the adapter for listView

        grigliaViste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                col = v.getBackground();
                item = v;
                v.setBackgroundResource(R.color.selected);

                try {Thread.sleep(100);} catch (Exception e) {}
                //Toast.makeText(SapMenu.this, "" + arrayList.get(position).get("Titolo"), Toast.LENGTH_SHORT).show();
                Intent intent = null;

                ArrayList<HashMap<String, String>> menuTapped = Global.getMenu(menu);

                String menuItem = menuTapped.get(position).get("Descrizione");
                String descrItem = menuTapped.get(position).get("Titolo");
                String tipoItem = menuTapped.get(position).get("Tipo");
                // Se è una voce di menu ricorsivamente richiama l'Activity

                Log.d("MENU", menuItem + " " + descrItem );
                if (tipoItem.equalsIgnoreCase("M")) {
                    intent = new Intent(SapMenu.this, SapMenu.class);
                    intent.putExtra("menu",menuItem );
                    intent.putExtra("titolo",descrItem );
                } else {

                    // in base all nome del menu decide l'avticity
                    switch (menuItem) {
                        case (Global.LISTA_PREL_VEND):
                            intent = new Intent(SapMenu.this, ListaPrelVendita.class);
                            intent.putExtra("titolo",descrItem );
                            intent.putExtra("menuItem", menuItem);
                            break;
                        case (Global.LISTA_PREL_PROD):
                            intent = new Intent(SapMenu.this, ListaPrelProd.class);
                            intent.putExtra("titolo",descrItem );
                            intent.putExtra("menuItem", menuItem);
                            break;
                        case (Global.INFO_STOCK):
                        case (Global.INFO_STOCK_MATERIALE):
                        case (Global.INFO_STOCK_UBICAZIONE):
                        case (Global.INFO_STOCK_ODP):
                        case (Global.MANCANTI_DA_ZMAG):
                        case (Global.MESSA_IN_UBICAZIONE_DA_BEM):
                        case (Global.TRASF_TRA_UBICAZIONE):
                        case (Global.AVANZAVA_FASE):
                        case (Global.DAMODULAACASSONE):
                        case (Global.DISTINTAMACCHINA):
                        case (Global.LT24) :
                        case (Global.SPOSTACASSONE):
                        case (Global.VERIFICACASSONE):
                        case (Global.ELENCOCASSONI):
                        case (Global.MENU_DOVE_CASSONE):
                        case (Global.MENU_IMPACCHETTAMENTO_MATERIALE):
                        case (Global.MENU_REIMPACCHETTAMENTO_MATERIALE):
                        case (Global.MENU_SPACCHETTAMENTO_MATERIALE):
                        case (Global.MENU_CONSUMA_CASSONE) :
                        case (Global.CRONOLOGIA_CODICE):
                            intent = new Intent(SapMenu.this, PrendiParametri.class);
                            intent.putExtra("titolo", descrItem );
                            intent.putExtra("menuItem", menuItem);
                            intent.putExtra("listaValori", getListaValori(menuItem));
                            break;
                        default:
                            Global.alert(SapMenu.this, "Funzione non ancora implementata");
                            v.setBackground(col);
                            break;
                    }
                }
                if (intent != null) startActivity(intent);
            }
        });

        //new BackgroundTask().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (item!=null && col!=null) {
            item.setBackground(col);
            item=null;
            col=null;
        }
    }

    public ArrayList<HashMap<String, String>> getListaValori(String menu) {
        ArrayList<HashMap<String, String>> temp = new ArrayList<>();
        switch (menu) {
            case (Global.INFO_STOCK):
            case (Global.INFO_STOCK_ODP):
                temp.add(putListaValori("Materiale", "", "NUM", menu.equals(Global.INFO_STOCK_MATERIALE)));
                temp.add(putListaValori("Descrizione", ""));
                temp.add(putListaValori("Tipo + ubic", "", "", menu.equals(Global.INFO_STOCK_UBICAZIONE)));
                temp.add(putListaValori("HU", ""));
                temp.add(putListaValori("Divisione", Global.getImpostazoniSAP("Divisione")));
                temp.add(putListaValori("Numero Mag.", Global.getImpostazoniSAP("Numero Magazzino"), "NUM"));
                temp.add(putListaValori("Magazzino", ""));

                temp.add(putListaValori("Tipo ordine", ""));
                temp.add(putListaValori("Ordine", "", "NUM"));
                if (menu.equals(Global.INFO_STOCK_ODP)) {
                    temp.add(putListaValori("Stock/Odp", "ODP;STOCK;ODP", "RADIO"));
                } else {
                    temp.add(putListaValori("Stock/Odp", "STOCK;STOCK;ODP", "RADIO"));
                }
                break;
            case (Global.INFO_STOCK_MATERIALE):
                temp.add(putListaValori("Materiale", "", "NUM", menu.equals(Global.INFO_STOCK_MATERIALE)));
                temp.add(putListaValori("Stock/Odp", "STOCK;STOCK;ODP", "RADIO"));
                temp.add(putListaValori("HU", "", "HIDDEN"));
                temp.add(putListaValori("Tipo + ubic", "","HIDDEN"));
                temp.add(putListaValori("Descrizione","","HIDDEN"));
                temp.add(putListaValori("Divisione", Global.getImpostazoniSAP("Divisione"), "HIDDEN"));
                temp.add(putListaValori("Numero Mag.", "", "HIDDEN"));
                temp.add(putListaValori("Magazzino", "", "HIDDEN"));
                temp.add(putListaValori("Tipo ordine", "", "HIDDEN"));
                temp.add(putListaValori("Ordine", "", "HIDDEN"));
                //temp.add(putListaValori("Stock/Odp", "STOCK", "HIDDEN"));

                break;
            case (Global.INFO_STOCK_UBICAZIONE):
                temp.add(putListaValori("Tipo + ubic", "", "", menu.equals(Global.INFO_STOCK_UBICAZIONE)));
                temp.add(putListaValori("HU", ""));
                temp.add(putListaValori("Stock/Odp", "STOCK;STOCK;ODP", "RADIO"));
                temp.add(putListaValori("Materiale", "", "HIDDEN"));
                temp.add(putListaValori("Descrizione","","HIDDEN"));
                temp.add(putListaValori("Divisione", Global.getImpostazoniSAP("Divisione"), "HIDDEN"));
                temp.add(putListaValori("Numero Mag.", "", "HIDDEN"));
                temp.add(putListaValori("Magazzino", "", "HIDDEN"));
                temp.add(putListaValori("Tipo ordine", "", "HIDDEN"));
                temp.add(putListaValori("Ordine", "", "HIDDEN"));
                //temp.add(putListaValori("Stock/Odp", "STOCK", "HIDDEN"));

                break;
            case (Global.MANCANTI_DA_ZMAG):
                temp.add(putListaValori("Bem", "", "NUM", true));
                break;
            case (Global.MESSA_IN_UBICAZIONE_DA_BEM):
                temp.add(putListaValori("Bem", "", "NUM", true));
                break;
            case (Global.AVANZAVA_FASE):
                temp.add(putListaValori("Conferma", "", "NUM", true));
                temp.add(putListaValori("Quantita", "0", "NUM", true));
                break;
            case ("ANC0000010"):
                temp.add(putListaValori("Ubicazione", "", "", true));
                break;
            case ("ANC0000110"):
                temp.add(putListaValori("Ubicazione", "", "", true));
                temp.add(putListaValori("Divisione", Global.getImpostazoniSAP("Divisione")));
                break;
            case (Global.TRASF_TRA_UBICAZIONE):
                temp.add(putListaValori("Materiale", "", "NUM" ,true));
                temp.add(putListaValori("Provenienza", "", "", true));
                temp.add(putListaValori("Destinazione", "", "", true));
                temp.add(putListaValori("HU", "", ""));
                temp.add(putListaValori("Quantita", "", "NUM", true));
                temp.add(putListaValori("Numero Mag.", Global.getImpostazoniSAP("Numero Magazzino"), "", true));
                temp.add(putListaValori("Magazzino", Global.getImpostazoniSAP("Magazzino"), "", true));
                break;
            case (Global.LT24):
                temp.add(putListaValori("Materiale", "", "NUM", false));
                temp.add(putListaValori("Giorni", "31", "NUM", true));
                temp.add(putListaValori("Provenienza", "", "READONLY", false));
                temp.add(putListaValori("Tipo", "", "", false));
                temp.add(putListaValori("Ubic", "", "", false));
                temp.add(putListaValori("Destinazione", "", "READONLY", false));
                temp.add(putListaValori("Tipo ", "", "", false));
                temp.add(putListaValori("Ubic ", "", "", false));
                //Aggiungere 2 campi guardare nome dell'XML
                break;
            case (Global.VERIFICACASSONE):
                //temp.add(putListaValori("Storico/Zmag", "STORICO;STORICO;ZMAG", "RADIO", true));
                temp.add(putListaValori("HU", "", "", true));
                break;
            case (Global.MENU_DOVE_CASSONE): {
                temp.add(putListaValori("HU", "", "", true));
                temp.add(putListaValori("Odp", "", "", false));
                break;
            }
            case (Global.DAMODULAACASSONE):
                temp.add(putListaValori("Posizione", "MOD;MOD;GEN;STK", "RADIO"));
                temp.add(putListaValori("Odp", "", "", false));
                temp.add(putListaValori("Materiale", "", "", false));
                break;
            case (Global.DISTINTAMACCHINA):
                temp.add(putListaValori("Macchina", "", ""));
                temp.add(putListaValori("Testa/Mandrino", "", ""));
                break;
            case (Global.SPOSTACASSONE):
                temp.add(putListaValori("HU", "", "", true));
                //temp.add(putListaValori("Area", "", "", true));
                temp.add(putListaValori("Tipo + ubic", "", "", true));
                break;
            case (Global.ELENCOCASSONI):
                temp.add(putListaValori("HU", "", "", false));
                temp.add(putListaValori("Odp", "", "", false));
                temp.add(putListaValori("Materiale", "", "", false));
                //temp.add(putListaValori("Area", "", "", false));
                break;

            case (Global.CRONOLOGIA_CODICE):
                temp.add(putListaValori("RFID"+0, "","", true));
                break;

            case (Global.MENU_IMPACCHETTAMENTO_MATERIALE):
                temp.addAll(Global.getCampiImpacchettamento());
                break;
            case (Global.MENU_SPACCHETTAMENTO_MATERIALE):
                temp.addAll(Global.getCampiSpacchettamento());
                break;
            case (Global.MENU_REIMPACCHETTAMENTO_MATERIALE): {
                temp.addAll(Global.getCampiReimpacchettamento());
                break;
            }
            case (Global.MENU_CONSUMA_CASSONE) : {
                temp.addAll(Global.getCampiConsumaCassone());
                break;
            }
        }
        return temp;
    }
}
