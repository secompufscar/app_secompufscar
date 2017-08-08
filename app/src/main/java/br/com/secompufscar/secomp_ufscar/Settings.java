package br.com.secompufscar.secomp_ufscar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Switch;

/**
 * Created by olivato on 05/08/17.
 */

public class Settings extends AppCompatActivity {
    private SharedPreferences mPrefs;
    private Switch switch_3g;
    private Switch switch_notifications;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        mPrefs = getApplicationContext().getSharedPreferences("Settings", 0);
        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/ClearSans-Regular.ttf");
        switch_3g = (Switch)findViewById(R.id.settings_3g);
        switch_notifications = (Switch)findViewById(R.id.settings_notification);
        onLoad();
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
    }
    public void onLoad()
    {
            switch_3g.setChecked(mPrefs.getBoolean("internet",true));
            switch_notifications.setChecked(mPrefs.getBoolean("notifications",true));
    }
    public void onSwitch(View v)
    {
        save(switch_notifications.isChecked(),switch_3g.isChecked());
    }

    private void save(boolean internet, boolean notifications )
    {
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putBoolean("internet",internet);
        mEditor.putBoolean("notifications",notifications);
        mEditor.commit();
    }
}
