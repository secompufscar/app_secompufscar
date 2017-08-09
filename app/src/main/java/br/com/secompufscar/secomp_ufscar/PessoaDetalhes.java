package br.com.secompufscar.secomp_ufscar;

import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.MenuItem;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.secompufscar.secomp_ufscar.data.Atividade;
import br.com.secompufscar.secomp_ufscar.data.DatabaseHandler;
import br.com.secompufscar.secomp_ufscar.data.Pessoa;

public class PessoaDetalhes extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {
    public static final String EXTRA = "pessoa_atividade";

    TextView nome, profissao_empresa, descricao, contatos;
    ImageView foto;

    Pessoa pessoaAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int id = getIntent().getIntExtra(EXTRA, 0);

        pessoaAtual = DatabaseHandler.getDB().getDetalhePessoa(id);

        setContentView(R.layout.activity_pessoa_detalhes);


//        Get current screen orientation
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
                collapsingToolbar.setTitle(pessoaAtual.getNomeCompleto());

                break;
            case Surface.ROTATION_90:
                break;
        }

        nome = (TextView) findViewById(R.id.pessoa_detalhe_nome);
        nome.setText(pessoaAtual.getNomeCompleto());

        String profissao_empresa_string = pessoaAtual.getProfissao();
        profissao_empresa_string +=  pessoaAtual.getEmpresa() != null ? ", " + pessoaAtual.getEmpresa() : "" ;

        profissao_empresa = (TextView) findViewById(R.id.pessoa_detalhe_profissao_empresa);
        profissao_empresa.setText(profissao_empresa_string);
        
        foto = (ImageView) findViewById(R.id.pessoa_detalhe_foto);
        foto.setImageBitmap(pessoaAtual.getFotoBitmap(getBaseContext()));
        

        ImageView backgroundCollapsing = (ImageView) findViewById(R.id.imagem_tipo_atividade);

        backgroundCollapsing.setColorFilter(ContextCompat.getColor(getBaseContext(), R.color.apoioColor), PorterDuff.Mode.MULTIPLY);
        backgroundCollapsing.setImageDrawable(getDrawable(R.drawable.fundo_triangulos_branco));


        new UpdatePessoa().execute(id);
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

    private class UpdatePessoa extends AsyncTask<Integer, Void, Atividade> {
        @Override
        protected Atividade doInBackground(Integer... params) {

            return Atividade.getDetalheAtividadeFromHTTP(params[0]);
        }

        @Override
        protected void onPostExecute(Atividade atividadeAtualizada) {

        }
    }
}
