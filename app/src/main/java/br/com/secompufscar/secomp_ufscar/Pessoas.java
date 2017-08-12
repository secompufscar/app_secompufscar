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

    public Pessoas() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }

        adapter = new PessoasAdapter(getActivity(), pessoaList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pessoas, container, false);
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

        new UpdatePessoas().execute();

        return view;
    }

    private class UpdatePessoas extends AsyncTask<Void, Void, List<Pessoa>> {
        @Override
        protected List<Pessoa> doInBackground(Void... params) {
            try {
                return DatabaseHandler.getDB().getAllPessoas();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Pessoa> pessoasFromDB) {
            pessoaList.clear();
            pessoaList.addAll(pessoasFromDB);
            adapter.notifyDataSetChanged();
        }
    }
}
