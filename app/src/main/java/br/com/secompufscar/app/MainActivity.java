package br.com.secompufscar.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
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
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Calendar;
import java.util.HashMap;

import br.com.secompufscar.app.data.Atividade;
import br.com.secompufscar.app.data.DatabaseHandler;
import br.com.secompufscar.app.utilities.NetworkUtils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        Home.OnFragmentInteractionListener {

    private final String CURRENT_FRAGMENT_PARAM = "current_fragment";

    public static final String CURRENT_TAB_PARAM = "currenttab";
    public static final String GET_DATA_PARAM = "getdata";
    public static int current_tab;
    public static boolean get_atividades_from_server = true;

    private static final int HOME_POSITION = 0;
    private static final int CRONOGRAMA_POSITION = 1;
    private static final int PESSOAS_POSITION = 3;
    private static final int MINHAS_ATIVIDADES_POSITION = 4;
    private static final int PATROCINADORES_POSITION = 7;
    private static final int SOBRE_POSITION = 8;

    private SharedPreferences preferencias;
    private boolean notifications;
    private boolean internet;

    private HashMap<String, Fragment> fragmentos;

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private TextView title;

    private View contentView;
    private View loadingView;

    private Toolbar toolbar;
    private GetDataTask getDataTask;

    private int current_fragment, previousItemSelected, itemSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            current_fragment = savedInstanceState.getInt(CURRENT_FRAGMENT_PARAM);
            //TODO: essas duas variáveis são utilizadas no fragmento cronograma, devemos achar uma forma de lidar com isso dentro do fragmento
            current_tab = savedInstanceState.getInt(CURRENT_TAB_PARAM);
            get_atividades_from_server = savedInstanceState.getBoolean(GET_DATA_PARAM);

        } else {
            current_fragment = HOME_POSITION;
            //TODO: esse processamento de ser feito no fragmento cronograma
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_WEEK); // começa em domingo = 1

            if (day > 1 && day < 7) {
                current_tab = day - 2;
            } else {
                current_tab = 4;
            }

            get_atividades_from_server = true;
        }

        DatabaseHandler.setInstance(this);

        setContentView(R.layout.activity_main);

        title = (TextView) findViewById(R.id.fragment_title);

        contentView = findViewById(R.id.content_frame);
        contentView.setVisibility(View.GONE);
        initializeFragments();

        loadingView = findViewById(R.id.loading_spinner);
        loadingView.setVisibility(View.GONE);

        //Apaga todas as notificações ao entrar no app
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        //Define uma font padrão para tudo no app
        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/ClearSans-Regular.ttf");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {

                if (itemSelected != previousItemSelected) {
                    new HandleMenuClick().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, itemSelected);
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
                new GetDataTask().execute();
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
        } else {
            contentView.setVisibility(View.VISIBLE);

            if (DatabaseHandler.getDB().getAtividadesCount() == 0) {
                if (NetworkUtils.updateConnectionState(getBaseContext())) {
                    new GetDataTask().execute();
                }
            }
        }

        internet = preferencias.getBoolean("internet", true);
        notifications = preferencias.getBoolean("notifications", true);

        //FIREBASE MENINO caso a pessoa queira
        if (notifications)
            FirebaseMessaging.getInstance().subscribeToTopic("secomp2l17");
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

    private void initializeFragments() {
        fragmentos = new HashMap<>();
        fragmentos.put("home", new Home());
        fragmentos.put("cronograma", new Cronograma());
        fragmentos.put("pessoas", new Pessoas());
        fragmentos.put("patrocinadores", new Patrocinadores());
        fragmentos.put("minhas_atividades", new MinhasAtividades());
        fragmentos.put("sobre", new Sobre());
    }

    private void setFragment() {
        contentView.setAlpha(1f);

        navigationView.getMenu().getItem(current_fragment).setChecked(true);

        Fragment fragment_atual;

        switch (current_fragment) {
            case CRONOGRAMA_POSITION:
                title.setText(R.string.cronograma_button);
                previousItemSelected = R.id.nav_cronograma;
                fragment_atual = fragmentos.get("cronograma");
                break;
            case PESSOAS_POSITION:
                title.setText(R.string.pessoas_button);
                previousItemSelected = R.id.nav_pessoas;
                fragment_atual = fragmentos.get("pessoas");
                break;
            case MINHAS_ATIVIDADES_POSITION:
                title.setText(R.string.favoritos_button);
                previousItemSelected = R.id.nav_minhasAtividades;
                fragment_atual = fragmentos.get("minhas_atividades");
                break;
            case PATROCINADORES_POSITION:
                title.setText(R.string.patrocinio_button);
                previousItemSelected = R.id.nav_patrocinadores;
                fragment_atual = fragmentos.get("patrocinadores");
                break;
            case SOBRE_POSITION:
                title.setText(R.string.sobre_button);
                previousItemSelected = R.id.nav_sobre;
                fragment_atual = fragmentos.get("sobre");
                break;
            default:
                title.setText(R.string.home_button);
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

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                finish();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.sair).setPositiveButton("Sim", dialogClickListener)
                        .setNegativeButton("Não", dialogClickListener).show();
            } else {
                current_fragment = HOME_POSITION;
                itemSelected = R.id.nav_home;
                previousItemSelected = itemSelected;
                setFragment();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);
        itemSelected = item.getItemId();

        switch (item.getItemId()) {
            case R.id.nav_home:
                title.setText(R.string.home_button);
                break;
            case R.id.nav_cronograma:
                title.setText(R.string.cronograma_button);
                break;
            case R.id.nav_pessoas:
                title.setText(R.string.pessoas_button);
                break;
            case R.id.nav_minhasAtividades:
                title.setText(R.string.favoritos_button);
                break;
            case R.id.nav_patrocinadores:
                title.setText(R.string.patrocinio_button);
                break;
            case R.id.nav_sobre:
                title.setText(R.string.sobre_button);
                break;
            default:
        }

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
        savedInstanceState.putInt(CURRENT_TAB_PARAM, current_tab);
        savedInstanceState.putBoolean(GET_DATA_PARAM, get_atividades_from_server);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        navigationView.getMenu().getItem(current_fragment).setChecked(true);
        setFragment();
        itemSelected = previousItemSelected;

    }

    private class HandleMenuClick extends AsyncTask<Integer, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Integer... params) {
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
                    startActivity(new Intent(MainActivity.this, MapsActivity.class));
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
                previousItemSelected = itemSelected;
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean isActivity) {
            if (!isActivity) {
                contentView.setAlpha(1f);
            }
        }
    }

    private class GetDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            title.setVisibility(View.GONE);

            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            toggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            toggle.setDrawerIndicatorEnabled(false);
            toggle.syncState();

            loadingView.setVisibility(View.VISIBLE);
            contentView.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            Atividade.getAtividadesFromHTTP(getBaseContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void s) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            toggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_UNLOCKED);
            toggle.setDrawerIndicatorEnabled(true);
            toggle.syncState();

            if (loadingView.isShown()) {
                fadeOut();
            }

            title.setVisibility(View.VISIBLE);
        }
    }
}