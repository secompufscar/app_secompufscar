package br.com.secompufscar.secomp_ufscar.listaAtividades;

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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.secompufscar.secomp_ufscar.AtividadeDetalhes;
import br.com.secompufscar.secomp_ufscar.adapters.AtividadesAdapter;
import br.com.secompufscar.secomp_ufscar.R;
import br.com.secompufscar.secomp_ufscar.data.Atividade;
import br.com.secompufscar.secomp_ufscar.data.DatabaseHandler;
import br.com.secompufscar.secomp_ufscar.utilities.ClickListener;
import br.com.secompufscar.secomp_ufscar.utilities.RecyclerTouchListener;


public class ListaTerca extends Fragment {
    private static final String ARG_PARAM1 = "offset";

    private int offset;


    public static List<Atividade> atividadeList = new ArrayList<>();
    private RecyclerView recycler_atividades;
    private AtividadesAdapter adapter;

    private View erro_screen;

    public ListaTerca() {
        // Required empty public constructor
    }

    public static ListaTerca newInstance(String param1) {
        ListaTerca fragment = new ListaTerca();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            offset = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_lista_atividades, container, false);

        erro_screen = view.findViewById(R.id.sem_dados);
        TextView erro_text = (TextView) view.findViewById(R.id.texto_erro);
        erro_text.setText(R.string.erro_sem_dados_atividades);

        recycler_atividades = (RecyclerView) view.findViewById(R.id.recycler_atividades);

        adapter = new AtividadesAdapter(getActivity(), atividadeList);
        recycler_atividades.setAdapter(adapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recycler_atividades.setLayoutManager(mLayoutManager);

        recycler_atividades.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), recycler_atividades, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Atividade atividade = atividadeList.get(position);

                Context context = view.getContext();
                Intent detalhesAtividade = new Intent(context, AtividadeDetalhes.class);
                detalhesAtividade.putExtra("id_atividade", atividade.getId());
                context.startActivity(detalhesAtividade);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return view;
    }

    public void updateAtividades() {
        try {
            atividadeList.clear();
            atividadeList.addAll(DatabaseHandler.getDB().getAtividadesByDay(offset));
            adapter.notifyDataSetChanged();

            if (!atividadeList.isEmpty()) {
                erro_screen.setVisibility(View.GONE);
            } else {
                erro_screen.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateAtividades();
    }

}
