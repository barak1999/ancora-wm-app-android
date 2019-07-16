package com.ancoragroup.wm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class VersioniActivity extends OmarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.versioni);


        WebView webViewer = (WebView) findViewById(R.id.html);
        webViewer.loadUrl("file:///android_asset/versioni.html");
    }
}
