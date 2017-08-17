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
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

import br.com.secompufscar.secomp_ufscar.data.Atividade;
import br.com.secompufscar.secomp_ufscar.data.DatabaseHandler;
import br.com.secompufscar.secomp_ufscar.utilities.NetworkUtils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        Home.OnFragmentInteractionListener {

    private final String CURRENT_FRAGMENT_PARAM = "current_fragment";

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

        } else {
            current_fragment = HOME_POSITION;
        }

        getDataTask = new GetDataTask();

        DatabaseHandler.setInstance(this);

        setContentView(R.layout.activity_main);

        title = (TextView) findViewById(R.id.fragment_title);

        fragmentos = new HashMap<>();
        fragmentos.put("home", new Home());
        fragmentos.put("cronograma", new Cronograma());
        fragmentos.put("pessoas", new Pessoas());
        fragmentos.put("patrocinadores", new Patrocinadores());
        fragmentos.put("minhas_atividades", new MinhasAtividades());
        fragmentos.put("sobre", new Sobre());

        contentView = findViewById(R.id.content_frame);
        loadingView = findViewById(R.id.loading_spinner);


        loadingView.setVisibility(View.GONE);


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
                    if (getDataTask.getStatus() == AsyncTask.Status.RUNNING) {
                        navigationView.getMenu().getItem(current_fragment).setChecked(true);
                        itemSelected = previousItemSelected;
                    } else {
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

                if (getDataTask.getStatus() != AsyncTask.Status.RUNNING) {
                    getDataTask.execute();
                }
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
            if (NetworkUtils.updateConnectionState(getBaseContext()) && getDataTask.getStatus() != AsyncTask.Status.RUNNING) {
                getDataTask.execute();
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

    private void setFragment() {
        contentView.setAlpha(1f);

        navigationView.getMenu().getItem(current_fragment).setChecked(true);

        Fragment fragment_atual;

        switch (current_fragment) {
            case CRONOGRAMA_POSITION:
                title.setText("CRONOGRAMA");
                previousItemSelected = R.id.nav_cronograma;
                fragment_atual = fragmentos.get("cronograma");
                break;
            case PESSOAS_POSITION:
                title.setText("PESSOAS");
                previousItemSelected = R.id.nav_pessoas;
                fragment_atual = fragmentos.get("pessoas");
                break;
            case MINHAS_ATIVIDADES_POSITION:
                title.setText("MINHAS ATIVIDADES");
                previousItemSelected = R.id.nav_minhasAtividades;
                fragment_atual = fragmentos.get("minhas_atividades");
                break;
            case PATROCINADORES_POSITION:
                title.setText("PATROCINIO");
                previousItemSelected = R.id.nav_patrocinadores;
                fragment_atual = fragmentos.get("patrocinadores");
                break;
            case SOBRE_POSITION:
                title.setText("SOBRE");
                previousItemSelected = R.id.nav_sobre;
                fragment_atual = fragmentos.get("sobre");
                break;
            default:
                title.setText("HOME");
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
                        SharedPreferences.Editor mEditor = preferencias.edit();
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                finish();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                        mEditor.apply();
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.sair).setPositiveButton("Sim", dialogClickListener)
                        .setNegativeButton("Não", dialogClickListener).show();
                builder = new AlertDialog.Builder(this);

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

        switch (item.getItemId()) {
            case R.id.nav_home:
                title.setText("HOME");
                break;
            case R.id.nav_cronograma:
                title.setText("CRONOGRAMA");
                break;
            case R.id.nav_pessoas:
                title.setText("PESSOAS");
                break;
            case R.id.nav_minhasAtividades:
                title.setText("MINHAS ATIVIDADES");
                break;
            case R.id.nav_patrocinadores:
                title.setText("PATROCINIO");
                break;
            case R.id.nav_sobre:
                title.setText("SOBRE");
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
            loadingView.setVisibility(View.VISIBLE);
            contentView.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (DatabaseHandler.getDB().getAtividadesCount() == 0) {
                Atividade.getAtividadesFromHTTP(getBaseContext());
            }

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
