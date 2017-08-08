package br.com.secompufscar.secomp_ufscar;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import br.com.secompufscar.secomp_ufscar.data.DatabaseHandler;
import br.com.secompufscar.secomp_ufscar.data.Pessoa;
import br.com.secompufscar.secomp_ufscar.utilities.ClickListener;
import br.com.secompufscar.secomp_ufscar.utilities.RecyclerTouchListener;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Pessoas.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Pessoas#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Pessoas extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public static List<Pessoa> pessoaList = new ArrayList<>();

    private RecyclerView recycler_pessoas;
    private PessoasAdapter adapter;

    public Pessoas() {
        // Required empty public constructor
    }

    public void updatePessoas(){

        try {
            pessoaList.clear();
            pessoaList.addAll(DatabaseHandler.getDB().getAllPessoas());
            adapter.notifyDataSetChanged();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Pessoas.
     */
    // TODO: Rename and change types and number of parameters
    public static Pessoas newInstance(String param1, String param2) {
        Pessoas fragment = new Pessoas();
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

        adapter = new PessoasAdapter(getActivity(), pessoaList);
        updatePessoas();
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

//                Atividade atividade = atividadeList.get(position);
//
//                Context context = view.getContext();
//                Intent detalhesAtividade = new Intent(context, AtividadeDetalhes.class);
//                detalhesAtividade.putExtra("id_atividade", atividade.getId());
//                context.startActivity(detalhesAtividade);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return view;    }

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
