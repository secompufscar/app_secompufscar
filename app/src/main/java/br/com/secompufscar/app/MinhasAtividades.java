package br.com.secompufscar.app;

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

import br.com.secompufscar.app.adapters.MinhasAtividadesAdapter;
import br.com.secompufscar.app.data.Atividade;
import br.com.secompufscar.app.data.DatabaseHandler;
import br.com.secompufscar.app.utilities.ClickListener;
import br.com.secompufscar.app.utilities.RecyclerTouchListener;


public class MinhasAtividades extends Fragment {
    
    public static List<Atividade> atividadeList = new ArrayList<>();
    private RecyclerView recycler_atividades;
    private MinhasAtividadesAdapter adapter;

    private View erro_screen;

    public MinhasAtividades() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new MinhasAtividadesAdapter(getActivity(), atividadeList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_atividades, container, false);

        erro_screen =  view.findViewById(R.id.sem_dados);
        TextView erro_text = (TextView) view.findViewById(R.id.texto_erro);
        erro_text.setText(R.string.erro_sem_dados_atividades);

        recycler_atividades = (RecyclerView) view.findViewById(R.id.recycler_atividades);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recycler_atividades.setLayoutManager(mLayoutManager);

        recycler_atividades.setAdapter(adapter);

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

    @Override
    public void onResume() {
        super.onResume();
        updateAtividades();
    }

    public void updateAtividades(){

        atividadeList.clear();
        atividadeList.addAll(DatabaseHandler.getDB().getAllFavoritos());
        adapter.notifyDataSetChanged();

        if(!atividadeList.isEmpty()){
            erro_screen.setVisibility(View.GONE);
        } else {
            erro_screen.setVisibility(View.VISIBLE);
        }
    }
}
