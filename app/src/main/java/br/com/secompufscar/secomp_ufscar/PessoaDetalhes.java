package br.com.secompufscar.secomp_ufscar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.secompufscar.secomp_ufscar.adapters.ContatoAdapter;
import br.com.secompufscar.secomp_ufscar.adapters.MinhasAtividadesAdapter;
import br.com.secompufscar.secomp_ufscar.data.Atividade;
import br.com.secompufscar.secomp_ufscar.data.DatabaseHandler;
import br.com.secompufscar.secomp_ufscar.data.Pessoa;
import br.com.secompufscar.secomp_ufscar.utilities.ClickListener;
import br.com.secompufscar.secomp_ufscar.utilities.RecyclerTouchListener;

public class PessoaDetalhes extends AppCompatActivity {
    public static final String EXTRA = "pessoa_atividade";

    public static List<Pessoa.Contato> contatoList = new ArrayList<>();
    public static List<Atividade> atividadesList = new ArrayList<>();


    private RecyclerView recycler_contatos, recycler_atividades;
    private ContatoAdapter adapter;
    private MinhasAtividadesAdapter atividadesAdapter;

    TextView nome, profissao_empresa, descricao;
    ImageView foto;

    private View contentView;
    private View loadingView;

    private AlertDialog.Builder alertBuilder;

    Pessoa pessoaAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int id = getIntent().getIntExtra(EXTRA, 0);

        pessoaAtual = DatabaseHandler.getDB().getDetalhePessoa(id);

        contatoList.clear();
        contatoList.addAll(pessoaAtual.getContatos());

        atividadesList = DatabaseHandler.getDB().getAtividadesByMinistrante(pessoaAtual);

        alertBuilder = new AlertDialog.Builder(this);

        setContentView(R.layout.activity_pessoa_detalhes);

        contentView = findViewById(R.id.pessoa_detalhes_scroll);
        loadingView = findViewById(R.id.loading_spinner_pessoa);
        loadingView.setVisibility(View.GONE);


        recycler_contatos = (RecyclerView) findViewById(R.id.recycler_contatos);

        adapter = new ContatoAdapter(getBaseContext(), contatoList);
        recycler_contatos.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recycler_contatos.setLayoutManager(layoutManager);

        recycler_contatos.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recycler_contatos, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                final Pessoa.Contato contato = contatoList.get(position);

                alertBuilder.setMessage(contato.toString());

                alertBuilder.setPositiveButton("Abrir link", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            Uri url = Uri.parse(contato.getLink());

                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, url);
                            startActivity(browserIntent);
                        } catch (Exception e) {
                            Toast.makeText(getParent(), "Não foi possível abrir o link",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                alertBuilder.setNegativeButton("Voltar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                AlertDialog dialog = alertBuilder.create();
                dialog.show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        atividadesAdapter = new MinhasAtividadesAdapter(this, atividadesList);

        recycler_atividades = (RecyclerView) findViewById(R.id.recycler_atividades_pessoa);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getApplicationContext());
        recycler_atividades.setLayoutManager(mLayoutManager);

        recycler_atividades.setAdapter(atividadesAdapter);

        recycler_atividades.addOnItemTouchListener(new RecyclerTouchListener(this.getApplicationContext(), recycler_atividades, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Atividade atividade = atividadesList.get(position);

                Context context = view.getContext();
                Intent detalhesAtividade = new Intent(context, AtividadeDetalhes.class);
                detalhesAtividade.putExtra("id_atividade", atividade.getId());
                context.startActivity(detalhesAtividade);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        nome = (TextView) findViewById(R.id.pessoa_detalhe_nome);
        nome.setText(pessoaAtual.getNomeCompleto());

        String profissao_empresa_string = pessoaAtual.getProfissao();
        profissao_empresa_string += pessoaAtual.getEmpresa() != null ? ", " + pessoaAtual.getEmpresa() : "";

        profissao_empresa = (TextView) findViewById(R.id.pessoa_detalhe_profissao_empresa);
        profissao_empresa.setText(profissao_empresa_string);

        foto = (ImageView) findViewById(R.id.pessoa_detalhe_foto);
        foto.setImageBitmap(pessoaAtual.getFotoBitmap(getBaseContext()));

        descricao = (TextView) findViewById(R.id.pessoa_detalhe_descricao);
        String descricao_pessoa;
        if (pessoaAtual.getDescricao() == null) {
            descricao_pessoa = getResources().getString(R.string.atividade_indisponivel_descricao);
            contentView.setAlpha(0f);
            loadingView.setVisibility(View.VISIBLE);
        } else {
            descricao_pessoa = pessoaAtual.getDescricao();
        }
        descricao.setText(Html.fromHtml(descricao_pessoa));

        ImageView backgroundCollapsing = (ImageView) findViewById(R.id.imagem_fundo);

        backgroundCollapsing.setColorFilter(ContextCompat.getColor(getBaseContext(), R.color.pessoaDetalhe), PorterDuff.Mode.MULTIPLY);
        backgroundCollapsing.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.fundo_triangulos_branco));

        new UpdatePessoa().execute();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Make sure to call the super method so that the states of our views are saved
        super.onSaveInstanceState(outState);
        // Save our own state now
//        outState.putInt(STATE_COUNTER, mCounter);
    }

    private class UpdatePessoa extends AsyncTask<Void, Void, Pessoa> {
        @Override
        protected Pessoa doInBackground(Void... params) {

            return Pessoa.getDetalhePessoaFromHTTP(pessoaAtual, getBaseContext());
        }

        @Override
        protected void onPostExecute(Pessoa pessoaAtualizada) {
            if (pessoaAtualizada != null) {
                pessoaAtual = pessoaAtualizada;

                String descricao_pessoa = pessoaAtual.getDescricao() != null ? pessoaAtual.getDescricao() : getResources().getString(R.string.atividade_indisponivel_descricao);
                descricao.setText(Html.fromHtml(descricao_pessoa));

                contatoList.clear();
                contatoList.addAll(pessoaAtualizada.getContatos());
                adapter.notifyDataSetChanged();
            }

            if (loadingView.isShown()) {

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
    }
}
