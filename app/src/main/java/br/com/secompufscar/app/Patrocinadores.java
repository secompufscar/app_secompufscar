package br.com.secompufscar.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.secompufscar.app.adapters.PatrocinadorAdapter;
import br.com.secompufscar.app.data.DatabaseHandler;
import br.com.secompufscar.app.data.Patrocinador;
import br.com.secompufscar.app.utilities.ClickListener;
import br.com.secompufscar.app.utilities.RecyclerTouchListener;
import br.com.secompufscar.app.utilities.SectionedGridRecyclerViewAdapter;

public class Patrocinadores extends Fragment {

    private List<Patrocinador> patrocinadores;
    private int delimOuro, delimPrata, delimDesafio, delimApoio;
    private RecyclerView recycler_patrocinadores;
    private PatrocinadorAdapter adapter;
    private SectionedGridRecyclerViewAdapter sectionedAdapter;

    private View loadingView;
    private View erro_screen;


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

        erro_screen = view.findViewById(R.id.sem_dados);
        TextView erro_text = (TextView) view.findViewById(R.id.texto_erro);
        erro_text.setText(R.string.erro_sem_dados_patrocinadores);
        erro_screen.setVisibility(View.GONE);

        loadingView = view.findViewById(R.id.loading_spinner_patrocinadores);
        loadingView.setVisibility(View.GONE);

        //Your RecyclerView
        recycler_patrocinadores = (RecyclerView) view.findViewById(R.id.recycler_patrocinadores);
        recycler_patrocinadores.setVisibility(View.GONE);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);

        recycler_patrocinadores.setLayoutManager(gridLayoutManager);
        recycler_patrocinadores.setHasFixedSize(true);

        recycler_patrocinadores.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recycler_patrocinadores, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                int itemPosition = sectionedAdapter.getItemPosition(position);
                if (itemPosition >= 0) {

                    final Patrocinador patrocinador = patrocinadores.get(itemPosition);

                    if (patrocinador.getWebsite() != null && !patrocinador.getWebsite().isEmpty()) {
                        String msg = getResources().getString(R.string.open_browser_patrocinador, patrocinador.getNome());

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(msg);

                        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    Uri url = Uri.parse(patrocinador.getWebsite());

                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, url);
                                    startActivity(browserIntent);
                                } catch (Exception e) {
                                    Toast.makeText(getActivity(), "Não foi possível abrir o site",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        Toast.makeText(getActivity(), patrocinador.getNome(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        if (DatabaseHandler.getDB().getPatrocinadoresCount() == 0) {
            loadingView.setVisibility(View.VISIBLE);
            new GetPatrocinadores().execute();
            new UpdatePatrocinadores().execute();

        } else {
            loadingView.setVisibility(View.VISIBLE);
            new UpdatePatrocinadores().execute();
            new GetPatrocinadores().execute();
        }

        return view;
    }

    private void setupRecycler() {

        if (getActivity() != null) {
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

    @Override
    public void onResume() {
        super.onResume();
    }

    private class GetPatrocinadores extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            return Patrocinador.getPatrocinadoresFromHTTP(getActivity());
        }
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
            if (isAdded()) {
                if (patrocinadores.isEmpty()) {
                    erro_screen.setVisibility(View.VISIBLE);
                } else {
                    setupRecycler();

                    recycler_patrocinadores.setVisibility(View.VISIBLE);

                    recycler_patrocinadores.animate()
                            .alpha(1f)
                            .setDuration(getResources().getInteger(
                                    android.R.integer.config_longAnimTime))
                            .setListener(null);
                }

                if (loadingView.isShown()) {
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
}
