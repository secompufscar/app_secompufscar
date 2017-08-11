package br.com.secompufscar.secomp_ufscar;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.secompufscar.secomp_ufscar.data.DatabaseHandler;
import br.com.secompufscar.secomp_ufscar.data.Patrocinador;
import br.com.secompufscar.secomp_ufscar.utilities.ClickListener;
import br.com.secompufscar.secomp_ufscar.utilities.RecyclerTouchListener;
import br.com.secompufscar.secomp_ufscar.utilities.SectionedGridRecyclerViewAdapter;

public class Patrocinadores extends Fragment {

    private List<Patrocinador> patrocinadores;
    private int delimOuro, delimPrata, delimDesafio, delimApoio;
    private RecyclerView recycler_patrocinadores;
    private PatrocinadorAdapter adapter;
    private SectionedGridRecyclerViewAdapter sectionedAdapter;


    public Patrocinadores() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        patrocinadores = new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_patrocinadores, container, false);
        recycler_patrocinadores = (RecyclerView) view.findViewById(R.id.recycler_patrocinadores);

        //Your RecyclerView
        recycler_patrocinadores = (RecyclerView) view.findViewById(R.id.recycler_patrocinadores);
        recycler_patrocinadores.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);

        recycler_patrocinadores.setLayoutManager(gridLayoutManager);

        recycler_patrocinadores.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), recycler_patrocinadores, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                int itemPosition = sectionedAdapter.getItemPosition(position);
                if (itemPosition >= 0) {
                    Patrocinador patrocinador = patrocinadores.get(itemPosition);

                    Toast.makeText(getActivity(), patrocinador.getNome(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        new UpdatePatrocinadores().execute();

        return view;
    }

    private class UpdatePatrocinadores extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            try {
                patrocinadores.clear();

                HashMap<String, List<Patrocinador>> patrocioByCota = DatabaseHandler.getDB().getPatrocinadoresByCota();
                patrocinadores.addAll(patrocioByCota.get(Patrocinador.COTA_DIAMANTE));
                delimOuro = patrocinadores.size();
                patrocinadores.addAll(patrocioByCota.get(Patrocinador.COTA_OURO));
                delimPrata = patrocinadores.size();
                patrocinadores.addAll(patrocioByCota.get(Patrocinador.COTA_PRATA));
                delimDesafio = patrocinadores.size();
                patrocinadores.addAll(patrocioByCota.get(Patrocinador.COTA_DESAFIO));
                delimApoio = patrocinadores.size();
                patrocinadores.addAll(patrocioByCota.get(Patrocinador.COTA_APOIO));

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {

            adapter = new PatrocinadorAdapter(getActivity(), patrocinadores);

            //This is the code to provide a sectioned grid
            List<SectionedGridRecyclerViewAdapter.Section> sections =
                    new ArrayList<>();

            //Sections
            sections.add(new SectionedGridRecyclerViewAdapter.Section(0, "Diamante", ContextCompat.getColor(getActivity(), R.color.diamanteColor)));
            sections.add(new SectionedGridRecyclerViewAdapter.Section(delimOuro, "Ouro", ContextCompat.getColor(getActivity(), R.color.ouroColor)));
            sections.add(new SectionedGridRecyclerViewAdapter.Section(delimPrata, "Prata", ContextCompat.getColor(getActivity(), R.color.prataColor)));
            sections.add(new SectionedGridRecyclerViewAdapter.Section(delimDesafio, "Desafio", ContextCompat.getColor(getActivity(), R.color.desafioColor)));
            sections.add(new SectionedGridRecyclerViewAdapter.Section(delimApoio, "Apoio", ContextCompat.getColor(getActivity(), R.color.apoioColor)));

            //Add your adapter to the sectionAdapter
            SectionedGridRecyclerViewAdapter.Section[] dummy = new SectionedGridRecyclerViewAdapter.Section[sections.size()];

            sectionedAdapter = new
                    SectionedGridRecyclerViewAdapter(getActivity(), R.layout.section_patrocinadores, R.id.section_text, recycler_patrocinadores, adapter);
            sectionedAdapter.setSections(sections.toArray(dummy));

            //Apply this adapter to the RecyclerView
            recycler_patrocinadores.setAdapter(sectionedAdapter);
        }
    }
}
