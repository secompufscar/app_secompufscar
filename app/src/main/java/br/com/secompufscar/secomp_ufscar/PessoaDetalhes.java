package br.com.secompufscar.secomp_ufscar;

import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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

import br.com.secompufscar.secomp_ufscar.data.DatabaseHandler;
import br.com.secompufscar.secomp_ufscar.data.Pessoa;
import br.com.secompufscar.secomp_ufscar.utilities.ClickListener;
import br.com.secompufscar.secomp_ufscar.utilities.RecyclerTouchListener;

public class PessoaDetalhes extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {
    public static final String EXTRA = "pessoa_atividade";

    public static List<Pessoa.Contato> contatoList = new ArrayList<>();

    RecyclerView recycler_contatos;
    private ContatoAdapter adapter;

    TextView nome, profissao_empresa, descricao;
    ImageView foto, teste;

    Pessoa pessoaAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int id = getIntent().getIntExtra(EXTRA, 0);

        pessoaAtual = DatabaseHandler.getDB().getDetalhePessoa(id);

        setContentView(R.layout.activity_pessoa_detalhes);

        recycler_contatos = (RecyclerView) findViewById(R.id.recycler_contatos);

        adapter = new ContatoAdapter(getBaseContext(), contatoList);
        recycler_contatos.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recycler_contatos.setLayoutManager(layoutManager);

        recycler_contatos.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recycler_contatos, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(getBaseContext(), contatoList.get(position).toString(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));



        nome = (TextView) findViewById(R.id.pessoa_detalhe_nome);
        nome.setText(pessoaAtual.getNomeCompleto());

        String profissao_empresa_string = pessoaAtual.getProfissao();
        profissao_empresa_string +=  pessoaAtual.getEmpresa() != null ? ", " + pessoaAtual.getEmpresa() : "" ;

        profissao_empresa = (TextView) findViewById(R.id.pessoa_detalhe_profissao_empresa);
        profissao_empresa.setText(profissao_empresa_string);
        
        foto = (ImageView) findViewById(R.id.pessoa_detalhe_foto);
        foto.setImageBitmap(pessoaAtual.getFotoBitmap(getBaseContext()));

        descricao = (TextView) findViewById(R.id.pessoa_detalhe_descricao);

        String descricao_pessoa = pessoaAtual.getDescricao() != null ? pessoaAtual.getDescricao() : getResources().getString(R.string.atividade_indisponivel_descricao);

        descricao.setText(Html.fromHtml(descricao_pessoa));

        ImageView backgroundCollapsing = (ImageView) findViewById(R.id.imagem_fundo);

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

    private class UpdatePessoa extends AsyncTask<Integer, Void, Pessoa> {
        @Override
        protected Pessoa doInBackground(Integer... params) {

            return Pessoa.getDetalhePessoaFromHTTP(params[0]);
        }

        @Override
        protected void onPostExecute(Pessoa pessoaAtualizada) {
            pessoaAtual = pessoaAtualizada;

            String descricao_pessoa = pessoaAtual.getDescricao() != null ? pessoaAtual.getDescricao() : getResources().getString(R.string.atividade_indisponivel_descricao);
            descricao.setText(Html.fromHtml(descricao_pessoa));

            contatoList.clear();
            contatoList.addAll(pessoaAtualizada.getContatos());
            adapter.notifyDataSetChanged();
        }
    }
}
