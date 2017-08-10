package br.com.secompufscar.secomp_ufscar;

import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import br.com.secompufscar.secomp_ufscar.data.Atividade;
import br.com.secompufscar.secomp_ufscar.data.DatabaseHandler;

public class AtividadeDetalhes extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    public static final String EXTRA = "id_atividade";

    TextView descricao, local, horarios, titulo;

    Atividade atividadeAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int id = getIntent().getIntExtra(EXTRA, 0);

        atividadeAtual = DatabaseHandler.getDB().getDetalheAtividade(id);

        setContentView(R.layout.activity_atividade_detalhes);


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

        new UpdateDetalhes().execute(id);
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

    private class UpdateDetalhes extends AsyncTask<Integer, Void, Atividade> {
        @Override
        protected Atividade doInBackground(Integer... params) {

            return Atividade.getDetalheAtividadeFromHTTP(params[0]);
        }

        @Override
        protected void onPostExecute(Atividade atividadeAtualizada) {

            DatabaseHandler.getDB().getMinistrantes(atividadeAtual);

            if(atividadeAtualizada != null){
                boolean favorito = atividadeAtual.isFavorito();

                atividadeAtual = atividadeAtualizada;
                atividadeAtual.setFavorito(favorito);

                descricao.setText(Html.fromHtml(atividadeAtual.getDescricao()));
                local.setText(atividadeAtual.getLocal());
                horarios.setText(atividadeAtual.getHorarios());
            }
        }
    }

    private class salvaFavorito extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {

            return DatabaseHandler.getDB().updateFavorito(atividadeAtual);
        }

        @Override
        protected void onPostExecute(Boolean resultado) {
            if(resultado){
                if(atividadeAtual.isFavorito()){
                    Toast.makeText(AtividadeDetalhes.this, R.string.msg_favoritado, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AtividadeDetalhes.this, R.string.msg_nao_favoritado, Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(AtividadeDetalhes.this, R.string.msg_erro_favoritado, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
