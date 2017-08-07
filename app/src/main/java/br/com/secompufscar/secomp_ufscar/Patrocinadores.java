package br.com.secompufscar.secomp_ufscar;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import br.com.secompufscar.secomp_ufscar.data.Atividade;
import br.com.secompufscar.secomp_ufscar.data.DatabaseHandler;
import br.com.secompufscar.secomp_ufscar.data.Patrocinador;
import br.com.secompufscar.secomp_ufscar.utilities.ClickListener;
import br.com.secompufscar.secomp_ufscar.utilities.RecyclerTouchListener;
import br.com.secompufscar.secomp_ufscar.utilities.SectionedGridRecyclerViewAdapter;

public class Patrocinadores extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private List<Patrocinador> patrocinadores;
    private int delimOuro, delimPrata, delimDesafio, delimApoio;
    private RecyclerView recycler_patrocinadores;
    private PatrocinadorAdapter adapter;

    public Patrocinadores() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Patrocinadores.
     */
    // TODO: Rename and change types and number of parameters
    public static Patrocinadores newInstance(String param1, String param2) {
        Patrocinadores fragment = new Patrocinadores();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        patrocinadores = new ArrayList<>();
        HashMap<String, List<Patrocinador>> patrocioByCota =  DatabaseHandler.getDB().getPatrocinadoresByCota();
        patrocinadores.addAll(patrocioByCota.get(Patrocinador.COTA_DIAMANTE));
        delimOuro = patrocinadores.size();
        patrocinadores.addAll(patrocioByCota.get(Patrocinador.COTA_OURO));
        delimPrata = patrocinadores.size();
        patrocinadores.addAll(patrocioByCota.get(Patrocinador.COTA_PRATA));
        delimDesafio = patrocinadores.size();
        patrocinadores.addAll(patrocioByCota.get(Patrocinador.COTA_DESAFIO));
        delimApoio = patrocinadores.size();
        patrocinadores.addAll(patrocioByCota.get(Patrocinador.COTA_APOIO));

        adapter = new PatrocinadorAdapter(getActivity(), patrocinadores);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_patrocinadores, container, false);
        recycler_patrocinadores = (RecyclerView) view.findViewById(R.id.recycler_patrocinadores);
        recycler_patrocinadores.setAdapter(adapter);

        //Your RecyclerView
        recycler_patrocinadores = (RecyclerView) view.findViewById(R.id.recycler_patrocinadores);
        recycler_patrocinadores.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
//        gridLayoutManager.set;
        recycler_patrocinadores.setLayoutManager(gridLayoutManager);

        //This is the code to provide a sectioned grid
        List<SectionedGridRecyclerViewAdapter.Section> sections =
                new ArrayList<>();

        //Sections
        sections.add(new SectionedGridRecyclerViewAdapter.Section(0, "Diamante", ContextCompat.getColor(getActivity(), R.color.diamanteColor)));
        sections.add(new SectionedGridRecyclerViewAdapter.Section(delimOuro, "Ouro",ContextCompat.getColor(getActivity(), R.color.ouroColor)));
        sections.add(new SectionedGridRecyclerViewAdapter.Section(delimPrata, "Prata", ContextCompat.getColor(getActivity(), R.color.prataColor)));
        sections.add(new SectionedGridRecyclerViewAdapter.Section(delimDesafio, "Desafio", ContextCompat.getColor(getActivity(), R.color.desafioColor)));
        sections.add(new SectionedGridRecyclerViewAdapter.Section(delimApoio, "Apoio", ContextCompat.getColor(getActivity(), R.color.apoioColor)));

        //Add your adapter to the sectionAdapter
        SectionedGridRecyclerViewAdapter.Section[] dummy = new SectionedGridRecyclerViewAdapter.Section[sections.size()];
        final SectionedGridRecyclerViewAdapter sectionedAdapter = new
                SectionedGridRecyclerViewAdapter(getActivity(), R.layout.section_patrocinadores, R.id.section_text, recycler_patrocinadores, adapter);
        sectionedAdapter.setSections(sections.toArray(dummy));

        //Apply this adapter to the RecyclerView
        recycler_patrocinadores.setAdapter(sectionedAdapter);

        recycler_patrocinadores.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), recycler_patrocinadores, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                int itemPosition = sectionedAdapter.getItemPosition(position);
                if(itemPosition >= 0)
                {
                    Patrocinador patrocinador = patrocinadores.get(itemPosition) ;

                    Toast.makeText(getActivity(), patrocinador.getNome(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
