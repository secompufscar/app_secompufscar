package br.com.secompufscar.secomp_ufscar;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;

import br.com.secompufscar.secomp_ufscar.data.Atividade;
import br.com.secompufscar.secomp_ufscar.data.DatabaseHandler;
import br.com.secompufscar.secomp_ufscar.data.Pessoa;
import br.com.secompufscar.secomp_ufscar.utilities.NetworkUtils;

public class AtividadeDetalhes extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    TextView texto;
    ImageView imageTeste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atividade_detalhes);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_atividade_detalhe);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        texto = (TextView) findViewById(R.id.texto);
        imageTeste = (ImageView) findViewById(R.id.imagemTeste);


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            int teste = (int) extras.get("id_atividade");
            texto.setText(DatabaseHandler.getDB().getAtividade(teste).getDescricao());
        }

        new setFotoTask().execute("https://secompufscar.com.br/media/images/secompufscar2017/equipe/felipe_sampaio_de_souza.jpg");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
    // TODO:
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
            imageTeste.setImageBitmap(result);
        }
    }
}
