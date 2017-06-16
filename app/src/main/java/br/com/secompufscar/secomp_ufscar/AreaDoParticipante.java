package br.com.secompufscar.secomp_ufscar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class AreaDoParticipante extends AppCompatActivity {
    private WebView areaParticipante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_do_participante);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbarWV));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Set title of Detail page

        areaParticipante = (WebView) findViewById(R.id.areaParticipante);
        areaParticipante.setWebViewClient(new MyBrowser());
        areaParticipante.getSettings().setJavaScriptEnabled(true);
        areaParticipante.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        areaParticipante.loadUrl("https://secompufscar.com.br/2016/areadoparticipante/");
    }
}

class MyBrowser extends WebViewClient {

    @SuppressWarnings("deprecation")
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }
}
