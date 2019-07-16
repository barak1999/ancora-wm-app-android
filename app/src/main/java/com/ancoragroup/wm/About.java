package com.ancoragroup.wm;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

//public class About extends AppCompatActivity {
public class About extends OmarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLayoutXML(R.layout.activity_omar);
        super.onCreate(savedInstanceState);

        bottoneAvanti.setText("");
        grigliaViste = (GridView) findViewById(R.id.listview);

        //grigliaViste = (GridView) findViewById(R.id.about_view);

        if (getResources().getConfiguration().orientation==ORIENTATION_PORTRAIT) grigliaViste.setNumColumns(1);
        else grigliaViste.setNumColumns(2);

        // info sul wifi
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        // calcolo della memoria
         Runtime runtime = Runtime.getRuntime();
         long freeMemInMB=(runtime.freeMemory()) / 1048576L;
         long usedMemInMB=(runtime.totalMemory() - runtime.freeMemory()) / 1048576L;
         long maxHeapSizeInMB=runtime.maxMemory() / 1048576L;
         long availHeapSizeInMB = maxHeapSizeInMB - usedMemInMB;


        listaValori.add(putListaValori("Sistema Operativo", Build.VERSION.RELEASE));
        listaValori.add(putListaValori("Modello", Build.MODEL));
        listaValori.add(putListaValori("Versione", String.valueOf(Build.VERSION.SDK_INT)));
        listaValori.add(putListaValori("Memoria Libera" , String.valueOf(freeMemInMB)));
        listaValori.add(putListaValori("Memoria Usata" , String.valueOf(usedMemInMB)));
        listaValori.add(putListaValori("Memoria Heap" , String.valueOf(maxHeapSizeInMB)));
        listaValori.add(putListaValori("Memoria Heap Libera" , String.valueOf(availHeapSizeInMB)));
        listaValori.add(putListaValori("Produttore" , Build.MANUFACTURER));
        listaValori.add(putListaValori("Display" , Build.DISPLAY));
        listaValori.add(putListaValori("DPI", Global.getDPI(getResources().getDisplayMetrics().density)));
        listaValori.add(putListaValori("Width", String.valueOf(getResources().getDisplayMetrics().widthPixels)));
        listaValori.add(putListaValori("Height", String.valueOf(getResources().getDisplayMetrics().heightPixels)));
        listaValori.add(putListaValori("Build", new String(BuildConfig.BUILDTIME)));
        listaValori.add(putListaValori("Orientamento", getResources().getConfiguration().orientation==ORIENTATION_PORTRAIT?"Portrait":"Landscape"));
        listaValori.add(putListaValori("WiFi", wifiInfo.getSSID()));
        listaValori.add(putListaValori("Titolo", Global.serverURL));


        // se sono dispari nella visualizzazione orizzontale crea un placehoolder per riempire la riga
        if ((listaValori.size() % 2) == 1) { listaValori.add(putListaValori("", ""));}

        String[] da = {"Titolo", "Descrizione"};//string array
        int[] a = {R.id.titolo, R.id.descrizione};//int array of views id's
        OmarAdapter simpleAdapter = new OmarAdapter(this, listaValori, R.layout.omar_riga_visualizzazione, da, a);//Create object and set the parameters for simpleAdapter
        grigliaViste.setAdapter(simpleAdapter);//sets the adapter for listView
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}

