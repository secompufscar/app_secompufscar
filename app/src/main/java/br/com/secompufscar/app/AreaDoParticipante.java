package br.com.secompufscar.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import br.com.secompufscar.app.utilities.NetworkUtils;

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

        areaParticipante.loadUrl(NetworkUtils.BASE_URL+"2018/area-do-participante/");
    }

    class MyBrowser extends WebViewClient {

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}


