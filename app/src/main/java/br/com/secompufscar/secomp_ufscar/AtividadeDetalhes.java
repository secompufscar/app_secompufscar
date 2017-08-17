package br.com.secompufscar.secomp_ufscar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Display;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.secompufscar.secomp_ufscar.adapters.MinistrantesAdapter;
import br.com.secompufscar.secomp_ufscar.data.Atividade;
import br.com.secompufscar.secomp_ufscar.data.DatabaseHandler;
import br.com.secompufscar.secomp_ufscar.data.Pessoa;
import br.com.secompufscar.secomp_ufscar.utilities.ClickListener;
import br.com.secompufscar.secomp_ufscar.utilities.RecyclerTouchListener;

public class AtividadeDetalhes extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    public static final String EXTRA = "id_atividade";

    public static List<Pessoa> ministranteList = new ArrayList<>();

    private TextView descricao, local, horarios, titulo;

    private RecyclerView recycler_ministrantes;
    private MinistrantesAdapter adapter;

    private Atividade atividadeAtual;

    private View contentView;
    private View loadingView;

    public static final int TELA_DETALHES_ATIVIDADE = 1; //IDENTIFICAO DE QUAL TELA O RESULTADO ESTA VINDO

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int id = getIntent().getIntExtra(EXTRA, 0);

        atividadeAtual = DatabaseHandler.getDB().getDetalheAtividade(id);

        ministranteList.clear();
        ministranteList.addAll(atividadeAtual.getMinistrantes());

        setContentView(R.layout.activity_atividade_detalhes);

        contentView = findViewById(R.id.atividade_detalhes_scroll);
        loadingView = findViewById(R.id.loading_spinner_atividade);

        if (savedInstanceState == null) {
            contentView.setVisibility(View.GONE);
            new UpdateDetalhes().execute();
        } else {
            loadingView.setVisibility(View.GONE);
        }

        //Get current screen orientation
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        int orientation = display.getRotation();

        switch (orientation) {
            case Surface.ROTATION_0:
                setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                // Set Collapsing Toolbar layout to the screen
                CollapsingToolbarLayout collapsingToolbar =
                        (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
                collapsingToolbar.setExpandedTitleColor(0);
                // Set title of Detail page
                collapsingToolbar.setTitle(atividadeAtual.getTitulo());

                break;
            case Surface.ROTATION_90:

                break;
        }

        //TODO:Verificar se não há outra forma para trocar a cor
//        collapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.white));

        recycler_ministrantes = (RecyclerView) findViewById(R.id.recycler_ministrantes);

        adapter = new MinistrantesAdapter(getBaseContext(), ministranteList);
        recycler_ministrantes.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recycler_ministrantes.setLayoutManager(layoutManager);

        recycler_ministrantes.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recycler_ministrantes, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Pessoa ministrantes = ministranteList.get(position);

                Context context = view.getContext();
                Intent detalhesPessoa = new Intent(context, PessoaDetalhes.class);
                detalhesPessoa.putExtra(PessoaDetalhes.EXTRA, ministrantes.getId());
                context.startActivity(detalhesPessoa);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        titulo = (TextView) findViewById(R.id.atividade_detalhe_titulo);
        titulo.setText(atividadeAtual.getTitulo());

        String local_atividade = atividadeAtual.getLocal() != null ? atividadeAtual.getLocal() : getResources().getString(R.string.atividade_indisponivel_local);

        local = (TextView) findViewById(R.id.atividade_detalhe_local);
        local.setText(local_atividade);

        horarios = (TextView) findViewById(R.id.atividade_detalhe_horarios);
        horarios.setText(atividadeAtual.getHorarios());

        descricao = (TextView) findViewById(R.id.atividade_detalhe_descricao);
        String descricao_atividade = atividadeAtual.getDescricao() != null ? atividadeAtual.getDescricao() : getResources().getString(R.string.atividade_indisponivel_descricao);

        descricao.setText(Html.fromHtml(descricao_atividade));

        ImageView backgroundCollapsing = (ImageView) findViewById(R.id.imagem_tipo_atividade);

        backgroundCollapsing.setColorFilter(atividadeAtual.getColor(getBaseContext()), PorterDuff.Mode.MULTIPLY);
        backgroundCollapsing.setImageDrawable(getDrawable(R.drawable.fundo_triangulos_branco));

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.atividade_detalhe_fab);

        if (atividadeAtual.isFavorito()) {
            fab.setImageResource(R.drawable.ic_menu_favorite);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                if (atividadeAtual.isFavorito()) {

                    atividadeAtual.setFavorito(false);
                    new salvaFavorito().execute();
                    fab.setImageResource(R.drawable.ic_atividade_favorito_borda);

                } else {

                    atividadeAtual.setFavorito(true);
                    new salvaFavorito().execute();
                    fab.setImageResource(R.drawable.ic_menu_favorite);
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Make sure to call the super method so that the states of our views are saved
        super.onSaveInstanceState(outState);
        // Save our own state now
//        outState.putInt(STATE_COUNTER, mCounter);
    }

    private class UpdateDetalhes extends AsyncTask<Void, Void, Atividade> {
        @Override
        protected Atividade doInBackground(Void... params) {

            return Atividade.getDetalheAtividadeFromHTTP(atividadeAtual, getBaseContext());
        }

        @Override
        protected void onPostExecute(Atividade atividadeAtualizada) {

            if (atividadeAtualizada != null) {
                boolean favorito = atividadeAtual.isFavorito();

                atividadeAtual = atividadeAtualizada;
                atividadeAtual.setFavorito(favorito);

                String descricao_atividade = atividadeAtual.getDescricao() != null ? atividadeAtual.getDescricao() : getResources().getString(R.string.atividade_indisponivel_descricao);

                descricao.setText(Html.fromHtml(descricao_atividade));
                local.setText(atividadeAtual.getLocal());
                horarios.setText(atividadeAtual.getHorarios());

                ministranteList.clear();
                ministranteList.addAll(atividadeAtual.getMinistrantes());
                adapter.notifyDataSetChanged();
            }

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
    }

    private class salvaFavorito extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {

            return DatabaseHandler.getDB().updateFavorito(atividadeAtual);
        }

        @Override
        protected void onPostExecute(Boolean resultado) {
            if (resultado) {
                if (atividadeAtual.isFavorito()) {
                    Toast.makeText(AtividadeDetalhes.this, R.string.msg_favoritado, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AtividadeDetalhes.this, R.string.msg_nao_favoritado, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AtividadeDetalhes.this, R.string.msg_erro_favoritado, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void EnviarDadosMapa(View view) {

        Bundle params = new Bundle();
        switch (atividadeAtual.getPredio()) {
            case "Auditório Mauro Biajiz":
                params.putInt("Local", 1);
                break;
            case "Anfiteatro Bento Prado Jr.":
                params.putInt("Local", 2);
                break;
            default:
                params.putInt("Local", 0);
        }
        Intent i = new Intent(this, MapsActivity.class);
        i.putExtras(params);

        startActivityForResult(i, TELA_DETALHES_ATIVIDADE);
    }
}
