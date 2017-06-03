package br.com.secompufscar.secomp_ufscar;

import android.app.ActionBar;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import br.com.secompufscar.secomp_ufscar.data.DatabaseHandler;

public class AtividadeDetalhes extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    TextView texto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atividade_detalhes);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_atividade_detalhe);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        texto = (TextView) findViewById(R.id.texto);


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            int teste = (int) extras.get("id_atividade");
            texto.setText(DatabaseHandler.getDB().getAtividade(teste).getDescricao());
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
