package br.com.secompufscar.secomp_ufscar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by olivato on 05/08/17.
 */

public class Settings extends AppCompatActivity {
    //Usado para pegar as preferências salvas do usuário
    private SharedPreferences mPrefs;

    //Linka com o switch do visual
    private Switch switch_3g;
    private Switch switch_notifications;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Visual (CTRL+Click pra vc abrir rápido)
        setContentView(R.layout.settings);
        //Pega as preferências do usuário
        mPrefs = getApplicationContext().getSharedPreferences("Settings", 0);
        //Set a font
        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/ClearSans-Regular.ttf");
        //Linka com o visual
        switch_3g = (Switch)findViewById(R.id.settings_3g);
        switch_notifications = (Switch)findViewById(R.id.settings_notification);
        textView = (TextView)findViewById(R.id.fragment_title);
        //Carrega as prefências em variáveis e adiciona o estado switch
        onLoad();
        //Início da gambiarra
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        textView.setText("Configurações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Fim da gambiarra


    }
    public void onLoad()
    {
            switch_3g.setChecked(mPrefs.getBoolean("internet",true));
            switch_notifications.setChecked(mPrefs.getBoolean("notifications",true));
    }
    //Autoexplicativo
    public void onSwitch(View v)
    {
        save(switch_3g.isChecked(),switch_notifications.isChecked());
    }

    //Salva as preferências de acordo com o estado dos switches
    private void save(boolean internet, boolean notifications )
    {
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putBoolean("internet",internet);
        mEditor.putBoolean("notifications",notifications);
        mEditor.commit();
    }
}
