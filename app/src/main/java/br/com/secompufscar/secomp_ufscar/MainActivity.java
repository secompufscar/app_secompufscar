package br.com.secompufscar.secomp_ufscar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.net.URL;

import br.com.secompufscar.secomp_ufscar.data.DatabaseHandler;
import br.com.secompufscar.secomp_ufscar.utilities.NetworkUtils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        Home.OnFragmentInteractionListener,
        Patrocinadores.OnFragmentInteractionListener,
        Cronograma.OnFragmentInteractionListener,
        ListaAtividades.OnFragmentInteractionListener,
        Pessoas.OnFragmentInteractionListener,
        Sobre.OnFragmentInteractionListener{
          
    private SharedPreferences mPrefs;
    private boolean notifications;
    private boolean internet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Checa se é a primeira vez rodando o app
        mPrefs = getApplicationContext().getSharedPreferences("Settings", 0);

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override

            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor mEditor = mPrefs.edit();
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        mEditor.putBoolean("notifications",true);

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        mEditor.putBoolean("notifications",false);
                        break;
                }
                mEditor.commit();
            }
        };
        DialogInterface.OnClickListener dialogClickListener2 = new DialogInterface.OnClickListener() {
            @Override

            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor mEditor = mPrefs.edit();
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        mEditor.putBoolean("internet",true);

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        mEditor.putBoolean("internet",false);
                        break;
                }
                mEditor.commit();
            }
        };
        if(mPrefs.getBoolean("first",true))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.nott).setPositiveButton("Sim", dialogClickListener)
                    .setNegativeButton("Não", dialogClickListener).show();
            builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.internets).setPositiveButton(R.string.yesint, dialogClickListener2)
                    .setNegativeButton(R.string.noint, dialogClickListener2).show();
            SharedPreferences.Editor mEditor = mPrefs.edit();
            mEditor.putBoolean("first",false);
            mEditor.commit();
        }
        internet = mPrefs.getBoolean("internet",true);
        notifications = mPrefs.getBoolean("notifications",true);
        //FIREBASE MENINO caso a pessoa queira
        if(notifications)
            FirebaseMessaging.getInstance().subscribeToTopic("secomp2l17");
        //Define uma font padrão para tudo no app
        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/ClearSans-Regular.ttf");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        Fragment fragment_inicial = new Home();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment_inicial).commit();

        DatabaseHandler.setInstance(this);

        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        //For 3G check
        boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .isConnectedOrConnecting();
        //For WiFi Check
        boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnectedOrConnecting();
        if(isWifi) {
            new GetDataTask().execute();
        }
        if(internet && is3g) {
            new GetDataTask().execute();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;

        if (id == R.id.nav_home) {
            fragment = new Home();
        } else if (id == R.id.nav_cronograma) {
            fragment = new Cronograma();
        } else if (id == R.id.nav_feed) {

        } else if (id == R.id.nav_pessoas) {
            fragment = new Pessoas();
        } else if (id == R.id.nav_minhasAtividades) {

        } else if (id == R.id.nav_map) {

        } else if (id == R.id.nav_patrocinadores) {
            fragment = new Patrocinadores();
        } else if (id == R.id.nav_sobre) {
            fragment = new Sobre();
        } else if (id == R.id.nav_areaParticipante){
            Intent intent = new Intent(MainActivity.this, AreaDoParticipante.class);
            MainActivity.this.startActivity(intent);
        }
        else if (id == R.id.nav_settings){
            Intent intent = new Intent(MainActivity.this, Settings.class);
            startActivity(intent);
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private class GetDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            URL url = NetworkUtils.buildUrl("app");
            String response;

            try {
                response = NetworkUtils.getResponseFromHttpUrl(url);
                if (response != null) {
                    //TODO Corrigir isso, por algum motivo não está salvando no SQLite
                    //DatabaseHandler.getDB().addAllAtividades(Atividade.AtividadeParseJSON(response));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void s) {
//            textForTest.setText(s);
        }
    }
}
