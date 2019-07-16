package com.ancoragroup.wm;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.ancoragroup.uhf.EPC;
import com.ancoragroup.uhf.Util;
import com.olc.uhf.UhfAdapter;
import com.olc.uhf.tech.IUhfCallback;

import java.util.ArrayList;
import java.util.List;

import cn.pda.serialport.Tools;

import static com.ancoragroup.wm.Global.Manufactorer;

public class ScriviRFID extends AppCompatActivity {

    ///////  GESTIONE SCANNER RPA520 ///////////////
    private Handler mHandler = new MainHandler();

    private Manufactorer manufactorerType = null;

    ////// GESTIONE RFID VH-71T////////
    private com.handheld.UHF.UhfManager manager;
    private ArrayList<String> listepc = new ArrayList<String>();
    private ArrayList<EPC> listEPC;
    boolean runFlag = false;
    boolean startFlag = false;
    Thread thread;

    ////// GESTIONE RFID RPA-520////////
    private com.olc.uhf.UhfManager managerVh;
    private com.olc.uhf.tech.ISO1800_6C uhf_6c;
    int allcount = 0;
    Thread threadRpa520;

    private byte[] currentEPC;

    private TextView lblCurrentRFID;

    private boolean isEPCWriting = false;

    // Password RFID
    final String EPC_PASSWORD = "00000000";
    // Da quale indirizzo inizare a scrivere
    final int EPC_START_ADDRESS = 2;
    // Area di memoria RFID dove è memorizzato il codice
    final int EPC_MEMBANK_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrivi_rfid);

        Util.initSoundPool(this);

        lblCurrentRFID = (TextView)findViewById(R.id.lblCurrentRFID);

        // Ricavo il tipo di modello dal nome
        manufactorerType = Manufactorer.getByName(Build.MODEL);

        // Instanzio il manager a seconda del dispositivo
        initManagerFromDevices();

        initReaderFromDevices();
    }

    public void initReaderFromDevices() {
        startFlag = !startFlag;
        if (startFlag) {
            runFlag = true;
            switch (manufactorerType) {
                case VH71T: {
                    thread = new InventoryThread();
                    thread.start();
                    break;
                }
                case RPA520: {
                    LoopReadEPCdRPA520();
                    break;
                }
                default: {
                    Global.alert(this.getApplicationContext(), "Attenzione, il terminale non supporta l'RFID");
                    runFlag = false;
                    break;
                }
            }
            //}
        } else {
            runFlag = false;
        }
    }
    public void scrivi(android.view.View view) {
        if (isEPCWriting) {
            Toast.makeText(getApplicationContext(), "Operazione in corso...",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentEPC == null) {
            Toast.makeText(getApplicationContext(), "Nessun EPC trovato...",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String epcStr = Tools.Bytes2HexString(currentEPC, currentEPC.length);
        if (epcStr == null || "".equals(epcStr.trim())) {
            Toast.makeText(getApplicationContext(), "Nessun EPC trovato...",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        TextView editWriteData = (TextView) findViewById(R.id.edtWriteData);
        String writeData = editWriteData.getText().toString();
        if (writeData == null || "".equals(writeData.trim())) {
            Toast.makeText(getApplicationContext(), "Nessun seriale inserito...",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        isEPCWriting = true;

        // Nascondo la tastiera
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),  InputMethodManager.HIDE_NOT_ALWAYS);

        // Converto password in byte
        byte[] accessPassword = Tools.HexString2Bytes(EPC_PASSWORD);

        TextView editLog = (TextView) findViewById(R.id.edtLog);

        if (accessPassword.length != 4) {
            Toast.makeText(getApplicationContext(), "Password non corretta",
                    Toast.LENGTH_SHORT).show();
            isEPCWriting = false;
            return;
        }

        if (writeData.length() % 4 != 0) {
            Toast.makeText(getApplicationContext(),
                    "valore non corretto 1 word = 2 byte", Toast.LENGTH_SHORT)
                    .show();
            isEPCWriting = false;
            return;
        }

        byte[] dataBytes = Tools.HexString2Bytes(writeData);

        boolean writeFlag = writeTo6CFromDevices(accessPassword, EPC_MEMBANK_CODE, EPC_START_ADDRESS, dataBytes, writeData.length() / 4);

        isEPCWriting = false;
        if (writeFlag) {
            editLog.setTextColor(getResources().getColor(R.color.colorOKDark));
            editLog.setText("Scritto correttamente");

            Util.play(1, 0);
        } else {
            editLog.setTextColor(getResources().getColor(R.color.colorERROR));
            editLog.setText("Errore!");

            Util.play(2, 0);

        }
    }


    private boolean writeTo6CFromDevices(byte[] password, int memBank, int startAddr, byte[] data, int strLength) {
        boolean success = false;
        switch (manufactorerType) {
            case VH71T: {
                success = manager.writeTo6C(password, memBank, startAddr, data.length / 2, data);
                break;
            }
            case RPA520: {

                byte[] myByte = data;
                byte[] pwrite = new byte[strLength * 2];

                System.arraycopy(myByte, 0, pwrite, 0, myByte.length > strLength * 2 ? strLength * 2 : myByte.length);

                // ADDRESS = 2, LENGTH = 6
                int response = uhf_6c.write(password, currentEPC.length, currentEPC, (byte)memBank, (byte)startAddr, (byte)strLength * 2, data);
                Log.d("RISPOSTA", String.valueOf(response));

                if (response != 0) {
                    Toast.makeText(getApplicationContext(),  uhf_6c.getErrorDescription(response),
                            Toast.LENGTH_SHORT).show();

                }

                success = (response == 0);
                break;
            }
        }

        currentEPC = null;
        return success;
    }

    private void initManagerFromDevices() {
        switch (manufactorerType) {
            case VH71T: {
                try {
                    manager = com.handheld.UHF.UhfManager.getInstance();
                } catch (Exception e) {
                    Log.e("ERRORE",e.getMessage());
                }
                break;
            }
            case RPA520: {
                try {
                    managerVh = UhfAdapter.getUhfManager(this.getApplicationContext());
                    if (managerVh != null) {
                        managerVh.open();
                        uhf_6c = (com.olc.uhf.tech.ISO1800_6C) managerVh.getISO1800_6C();
                    }
                } catch (Exception e) {
                    //Toast.makeText(App.this, "dasd", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }
    private class MainHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

        }
    };


    public void LoopReadEPCdRPA520() {
        threadRpa520 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (startFlag) {
                    Log.d("RFID","Start thread");
                    uhf_6c.inventory(callback);
                    if (!startFlag) {
                        Log.d("RFID","Fine thread");
                        break;
                    }
                    try {
                        Thread.sleep(150);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;

                    }
                }
            }
        });
        threadRpa520.start();
    }

    IUhfCallback callback = new IUhfCallback.Stub() {
        @Override
        public void doInventory(List<String> str) throws RemoteException {
            // for (Iterator it2 = str.iterator(); it2.hasNext();)
            Log.d("dqw", "count111=" + str.size());
            allcount += str.size();
            Log.d("dqw00000007", "count111=" + allcount);

            for (int i = 0; i < str.size(); i++) {
                String epcStr = (String) str.get(i);
                if (epcStr.length() > 6) {
                    epcStr = epcStr.substring(2, 6) + epcStr.substring(6);
                }
                Log.d("RFID", epcStr);


                currentEPC = Tools.HexString2Bytes(epcStr);
                processEpcRead(epcStr);
            }

        }


        @Override
        public void doTIDAndEPC(List<String> str) throws RemoteException {  }

    };
    class InventoryThread extends Thread {
        private List<byte[]> epcList;

        @Override
        public void run() {
            super.run();
            while (runFlag) {
                if (startFlag) {
                    // manager.stopInventoryMulti()

                    epcList = manager.inventoryRealTime(); // inventory real time
                    if (epcList != null && !epcList.isEmpty()) {
                        byte[] epc = epcList.get(0);
                        currentEPC = epc;

                        String epcStr = Tools.Bytes2HexString(epc, epc.length);

                        processEpcRead(epcStr);
                    }
                    epcList = null;
                    try {
                        Thread.sleep(40);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void processEpcRead(final String epcStr) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                String epc = epcStr;
                // Per lo smartphone piccolo devo fare substring
                if (manufactorerType == Manufactorer.RPA520) {
                    if (epc.length() > 4) {
                        epc = epc.substring(4);
                    }
                }
                lblCurrentRFID.setText(epc);

            }
        });

    }

    @Override
    protected void onDestroy() {
        if (thread != null) {
            thread.interrupt();

            thread = null;
        }
        if (threadRpa520 != null) {
            threadRpa520.interrupt();

            threadRpa520 = null;
        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (thread != null) {
            thread.interrupt();

            thread = null;
        }
        if (threadRpa520 != null) {
            threadRpa520.interrupt();

            threadRpa520 = null;
        }
        super.onBackPressed();
    }
}
