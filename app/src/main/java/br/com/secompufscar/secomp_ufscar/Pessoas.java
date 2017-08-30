package br.com.secompufscar.secomp_ufscar;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.secompufscar.secomp_ufscar.adapters.PessoasAdapter;
import br.com.secompufscar.secomp_ufscar.data.DatabaseHandler;
import br.com.secompufscar.secomp_ufscar.data.Pessoa;
import br.com.secompufscar.secomp_ufscar.utilities.ClickListener;
import br.com.secompufscar.secomp_ufscar.utilities.RecyclerTouchListener;

public class Pessoas extends Fragment {

    public static List<Pessoa> pessoaList = new ArrayList<>();

    private RecyclerView recycler_pessoas;
    private PessoasAdapter adapter;

    private View erro_screen;

    public Pessoas() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new PessoasAdapter(getActivity(), pessoaList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pessoas, container, false);

        erro_screen = view.findViewById(R.id.sem_dados);
        TextView erro_text = (TextView) view.findViewById(R.id.texto_erro);
        erro_text.setText(R.string.erro_sem_dados_pessoas);

        recycler_pessoas = (RecyclerView) view.findViewById(R.id.recycler_pessoas);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recycler_pessoas.setLayoutManager(mLayoutManager);

        recycler_pessoas.setAdapter(adapter);

        recycler_pessoas.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), recycler_pessoas, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                Pessoa pessoa = pessoaList.get(position);

                Context context = view.getContext();
                Intent detalhesPessoa = new Intent(context, PessoaDetalhes.class);
                detalhesPessoa.putExtra(PessoaDetalhes.EXTRA, pessoa.getId());
                context.startActivity(detalhesPessoa);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePessoas();
    }

    public void updatePessoas() {
        pessoaList.clear();
        pessoaList.addAll(DatabaseHandler.getDB().getAllPessoas());
        adapter.notifyDataSetChanged();

        if (pessoaList.isEmpty()) {
            erro_screen.setVisibility(View.VISIBLE);
        } else {
            erro_screen.setVisibility(View.GONE);
        }
    }
}
