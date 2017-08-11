package br.com.secompufscar.secomp_ufscar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

import br.com.secompufscar.secomp_ufscar.data.Atividade;
import br.com.secompufscar.secomp_ufscar.data.DatabaseHandler;
import br.com.secompufscar.secomp_ufscar.data.Patrocinador;
import br.com.secompufscar.secomp_ufscar.utilities.NetworkUtils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        Home.OnFragmentInteractionListener {

    private final String CURRENT_FRAGMENT_PARAM = "current_fragment";

    private static final int HOME_POSITION = 0;
    private static final int CRONOGRAMA_POSITION = 1;
    private static final int PESSOAS_POSITION = 3;
    private static final int MINHAS_ATIVIDADES_POSITION = 4;
    private static final int PATROCINADORES_POSITION = 6;
    private static final int SOBRE_POSITION = 7;

    private boolean GET_DATA_FROM_SERVER;

    private SharedPreferences preferencias;
    private boolean notifications;
    private boolean internet;

    private HashMap<String, Fragment> fragmentos;

    private DrawerLayout drawer;
    private NavigationView navigationView;

    private View contentView;
    private View loadingView;
    private Toolbar toolbar;
    private GetDataTask getDataTask;

    private int current_fragment, previousItemSelected, itemSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        fragmentos = new HashMap<>();
        fragmentos.put("home", new Home());
        fragmentos.put("cronograma", new Cronograma());
        fragmentos.put("pessoas", new Pessoas());
        fragmentos.put("patrocinadores", new Patrocinadores());
        fragmentos.put("minhas_atividades", new MinhasAtividades());
        fragmentos.put("sobre", new Sobre());

        contentView = findViewById(R.id.content_frame);
        loadingView = findViewById(R.id.loading_spinner);

        if (savedInstanceState != null) {
            current_fragment = savedInstanceState.getInt(CURRENT_FRAGMENT_PARAM);
            GET_DATA_FROM_SERVER = false;
            loadingView.setVisibility(View.GONE);
        } else {
            current_fragment = HOME_POSITION;
            GET_DATA_FROM_SERVER = true;
            contentView.setVisibility(View.GONE);
        }

        //Define uma font padrão para tudo no app
        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/ClearSans-Regular.ttf");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {

                if (itemSelected != previousItemSelected) {
                    if(getDataTask.getStatus() == AsyncTask.Status.RUNNING){
                        navigationView.getMenu().getItem(current_fragment).setChecked(true);
                        itemSelected = previousItemSelected;
                    }
                    else {
                        previousItemSelected = itemSelected;
                        new HandleMenuClick().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, itemSelected);
                    }
                }
            }
        };

        drawer.addDrawerListener(toggle);

        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setFragment();

        // Outras configurações
        //Checa se é a primeira vez rodando o app
        preferencias = getApplicationContext().getSharedPreferences("Settings", 0);

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor mEditor = preferencias.edit();
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        mEditor.putBoolean("notifications", true);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        mEditor.putBoolean("notifications", false);
                        break;
                }
                mEditor.apply();
            }
        };

        DialogInterface.OnClickListener dialogClickListener2 = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor mEditor = preferencias.edit();
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        mEditor.putBoolean("internet", true);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        mEditor.putBoolean("internet", false);
                        break;
                }
                mEditor.apply();
            }
        };

        if (preferencias.getBoolean("first", true)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.nott).setPositiveButton("Sim", dialogClickListener)
                    .setNegativeButton("Não", dialogClickListener).show();
            builder = new AlertDialog.Builder(this);

            builder.setMessage(R.string.internets).setPositiveButton(R.string.yesint, dialogClickListener2)
                    .setNegativeButton(R.string.noint, dialogClickListener2).show();
            SharedPreferences.Editor mEditor = preferencias.edit();
            mEditor.putBoolean("first", false);
            mEditor.apply();
        }

        internet = preferencias.getBoolean("internet", true);
        notifications = preferencias.getBoolean("notifications", true);
        //FIREBASE MENINO caso a pessoa queira
        if (notifications)
            FirebaseMessaging.getInstance().subscribeToTopic("secomp2l17");

        DatabaseHandler.setInstance(this);
        getDataTask = new GetDataTask();

        if (GET_DATA_FROM_SERVER && NetworkUtils.updateConnectionState(getBaseContext())) {
            getDataTask.execute();
        }
    }

    private void fadeOut() {
        contentView.setAlpha(0f);
        contentView.setVisibility(View.VISIBLE);

        contentView.animate()
                .alpha(1f)
                .setDuration(getResources().getInteger(
                        android.R.integer.config_longAnimTime))
                .setListener(null);

        loadingView.animate()
                .alpha(0f)
                .setDuration(getResources().getInteger(
                        android.R.integer.config_longAnimTime))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        loadingView.setVisibility(View.GONE);
                    }
                });
    }

    private void setFragment() {
        contentView.setAlpha(1f);

        navigationView.getMenu().getItem(current_fragment).setChecked(true);

        Fragment fragment_atual;

        switch (current_fragment) {
            case CRONOGRAMA_POSITION:
                previousItemSelected = R.id.nav_cronograma;
                fragment_atual = fragmentos.get("cronograma");
                break;
            case PESSOAS_POSITION:
                previousItemSelected = R.id.nav_pessoas;
                fragment_atual = fragmentos.get("pessoas");
                break;
            case MINHAS_ATIVIDADES_POSITION:
                previousItemSelected = R.id.nav_minhasAtividades;
                fragment_atual = fragmentos.get("minhas_atividades");
                break;
            case PATROCINADORES_POSITION:
                previousItemSelected = R.id.nav_patrocinadores;
                fragment_atual = fragmentos.get("patrocinadores");
                break;
            case SOBRE_POSITION:
                previousItemSelected = R.id.nav_sobre;
                fragment_atual = fragmentos.get("sobre");
                break;
            default:
                current_fragment = HOME_POSITION;
                previousItemSelected = R.id.nav_home;
                fragment_atual = fragmentos.get("home");
                break;
        }

        if (fragment_atual != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment_atual).commit();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (current_fragment == HOME_POSITION) {
                super.onBackPressed();
            } else {
                current_fragment = HOME_POSITION;
                setFragment();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);

        itemSelected = item.getItemId();
        if (itemSelected != previousItemSelected) {
            contentView.animate()
                    .alpha(0f)
                    .setDuration(getResources().getInteger(
                            android.R.integer.config_shortAnimTime))
                    .setListener(null);
        }

        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putInt(CURRENT_FRAGMENT_PARAM, current_fragment);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        navigationView.getMenu().getItem(current_fragment).setChecked(true);
        setFragment();
    }

    private class HandleMenuClick extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... params) {
            Log.d("Teste", "doing");
            int id = params[0];

            Fragment fragment = null;

            switch (id) {
                case R.id.nav_home:
                    fragment = fragmentos.get("home");
                    current_fragment = HOME_POSITION;
                    break;
                case R.id.nav_cronograma:
                    fragment = fragmentos.get("cronograma");
                    current_fragment = CRONOGRAMA_POSITION;
                    break;
                case R.id.nav_feed:
                    startActivity(new Intent(MainActivity.this, Social.class));
                    break;
                case R.id.nav_pessoas:
                    fragment = fragmentos.get("pessoas");
                    current_fragment = PESSOAS_POSITION;
                    break;
                case R.id.nav_minhasAtividades:
                    fragment = fragmentos.get("minhas_atividades");
                    current_fragment = MINHAS_ATIVIDADES_POSITION;
                    break;
                case R.id.nav_map:
                    break;
                case R.id.nav_patrocinadores:
                    fragment = fragmentos.get("patrocinadores");
                    current_fragment = PATROCINADORES_POSITION;
                    break;
                case R.id.nav_sobre:
                    fragment = fragmentos.get("sobre");
                    current_fragment = SOBRE_POSITION;
                    break;
                case R.id.nav_areaParticipante:
                    startActivity(new Intent(MainActivity.this, AreaDoParticipante.class));
                    break;
                case R.id.nav_settings:
                    startActivity(new Intent(MainActivity.this, Settings.class));
                    break;
                default:
            }

            if (fragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void s) {
            contentView.setAlpha(1f);
        }
    }

    private class GetDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Patrocinador.getPatrocinadoresFromHTTP(getBaseContext());
            Atividade.getAtividadesFromHTTP(getBaseContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void s) {
            if (loadingView.isShown()) {
                fadeOut();
            }
        }
    }
}
