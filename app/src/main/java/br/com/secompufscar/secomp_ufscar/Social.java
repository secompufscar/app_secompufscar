package br.com.secompufscar.secomp_ufscar;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class Social extends AppCompatActivity {
    FragmentManager fm;
    TwitterHashtag twitterHashtag;
    Facebook facebook;
    Instagram instagram;
    FragmentTransaction ft;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            ft = fm.beginTransaction();
            switch (item.getItemId()) {
                case R.id.twitter:
                    ft.replace(R.id.content,twitterHashtag);
                    ft.addToBackStack(null);
                    ft.commit();
                    return true;
                case R.id.instagram:
                    ft.replace(R.id.content,instagram);
                    ft.addToBackStack(null);
                    ft.commit();
                    return true;
                case R.id.facebook:
                    ft.replace(R.id.content, facebook);
                    ft.addToBackStack(null);
                    ft.commit();
                    return true;

            }

            return false;
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.redes_sociais);
        //Início da gambiarra
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle("SECOMP");
        toolbar.setTitleTextColor(Color.BLACK);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Fim da gambiarra
        twitterHashtag = new TwitterHashtag();
        facebook = new Facebook();
        instagram = new Instagram();
        fm =  getSupportFragmentManager();
        ft = fm.beginTransaction();
        ft.add(R.id.content,twitterHashtag);
        ft.commit();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }




}
