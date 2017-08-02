package br.com.secompufscar.secomp_ufscar;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import br.com.secompufscar.secomp_ufscar.data.Pessoa;
import br.com.secompufscar.secomp_ufscar.utilities.NetworkUtils;

public class AtividadeDetalhes extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {
    public static final String EXTRA_POSITION = "position";
    TextView texto;
    ImageView imageTeste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int id = getIntent().getIntExtra("id_atividade", 0);
        Log.d("Teste-Extra", Integer.toString(id));
        atividadeAtual = DatabaseHandler.getDB().getAtividade(id);

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
                collapsingToolbar.setTitle(atividadeAtual.getNome());

                break;
            case Surface.ROTATION_90:

                break;
        }


        //TODO:Verificar se não há outra forma para trocar a cor
//        collapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.white));


        TextView titulo = (TextView) findViewById(R.id.atividade_detalhe_titulo);
        titulo.setText(atividadeAtual.getNome());

        TextView local = (TextView) findViewById(R.id.atividade_detalhe_local);
        local.setText(atividadeAtual.getLocal());

        TextView descricao = (TextView) findViewById(R.id.atividade_detalhe_descricao);
        descricao.setText(atividadeAtual.getDescricao());

        ImageView backgroundCollapsing = (ImageView) findViewById(R.id.image);
        backgroundCollapsing.setImageDrawable(getDrawable(R.drawable.fundo_triangulos_verde));

        ImageView foto1 = (ImageView) findViewById(R.id.imagem_teste_1);
        foto1.setImageBitmap(DatabaseHandler.getDB().getPessoa(1).getFotoBitmap());

        ImageView foto2 = (ImageView) findViewById(R.id.imagem_teste_2);
        foto2.setImageBitmap(DatabaseHandler.getDB().getPessoa(1).getFotoBitmap());

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
                    DatabaseHandler.getDB().updateAtividade(atividadeAtual);
                    fab.setImageResource(R.drawable.ic_atividade_favorito_borda);
                    Toast.makeText(AtividadeDetalhes.this, R.string.msg_nao_favoritado, Toast.LENGTH_SHORT).show();

                } else {
                    atividadeAtual.setFavorito(true);
                    DatabaseHandler.getDB().updateAtividade(atividadeAtual);
                    fab.setImageResource(R.drawable.ic_menu_favorite);
                    Toast.makeText(AtividadeDetalhes.this, R.string.msg_favoritado, Toast.LENGTH_SHORT).show();
                }

                MinhasAtividades.updateAtividades();
            }
        });

        //new setFotoTask().execute("https://secompufscar.com.br/media/images/secompufscar2017/equipe/felipe_sampaio_de_souza.jpg");
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

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//
//        // Checks the orientation of the screen for landscape and portrait
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            setContentView(R.layout.activity_atividade_detalhes);
//            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
//            setContentView(R.layout.activity_atividade_detalhes);
//            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
//        }
//
//    }

    // TODO: Retirar esse trecho de código, ele está aqui apenas para teste
    private class setFotoTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            Pessoa pessoa1 = new Pessoa();
            Pessoa pessoa2 = new Pessoa();
            pessoa1.setId(1);
            pessoa1.setNome("Felipe");
            try {

                pessoa1.setFoto(NetworkUtils.getImageFromHttpUrl(params[0]));
                DatabaseHandler.getDB().addPessoa(pessoa1);
                pessoa2 = DatabaseHandler.getDB().getPessoa(1);

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return pessoa2.getFotoBitmap();
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            //imageTeste.setImageBitmap(result);
        }
    }
}
