package br.com.secompufscar.secomp_ufscar;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.secompufscar.secomp_ufscar.utilities.SobreExpandableListAdapter;

/**import android.content.Context;

 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Sobre.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Sobre#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Sobre extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    SobreExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> sobreHeader;
    HashMap<String, List<String>> sobreInfo;

    public Sobre() {
        // Required empty public constructor
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
    public static Sobre newInstance(String param1, String param2) {
        Sobre fragment = new Sobre();
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
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_sobre, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        expListView = (ExpandableListView) getView().findViewById(R.id.sobreListView);

        prepareSobreInfo();

        listAdapter = new SobreExpandableListAdapter(getView().getContext(), sobreHeader, sobreInfo);

        expListView.setAdapter(listAdapter);

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return false;
            }
        });

        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                // oi, tudo bom? rs
            }
        });

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                return false;
            }
        });
    }

    private void prepareSobreInfo(){
        sobreHeader = new ArrayList<String>();
        sobreInfo = new HashMap<String, List<String>>();

        sobreHeader.add(getResources().getString(R.string.sobreSecomp));
        sobreHeader.add(getResources().getString(R.string.sobreDC));
        sobreHeader.add(getResources().getString(R.string.sobreUFSCar));

        List<String> Secomp = new ArrayList<String>();
        Secomp.add(getResources().getString(R.string.infoSecomp));
        Secomp.add(getResources().getString(R.string.infoSecomp2));
        Secomp.add(getResources().getString(R.string.infoSecomp3));
        Secomp.add(getResources().getString(R.string.infoSecomp4));

        List<String> DC = new ArrayList<String>();
        DC.add(getResources().getString(R.string.infoDC));
        DC.add(getResources().getString(R.string.infoDC2));
        DC.add(getResources().getString(R.string.infoDC3));


        List<String> UFSCar = new ArrayList<String>();
        UFSCar.add(getResources().getString(R.string.infoUFSCar));
        UFSCar.add(getResources().getString(R.string.infoUFSCar2));
        UFSCar.add(getResources().getString(R.string.infoUFSCar3));
        UFSCar.add(getResources().getString(R.string.infoUFSCar4));
        UFSCar.add(getResources().getString(R.string.infoUFSCar5));
        UFSCar.add(getResources().getString(R.string.infoUFSCar6));
        UFSCar.add(getResources().getString(R.string.infoUFSCar7));
        UFSCar.add(getResources().getString(R.string.infoUFSCar8));


        sobreInfo.put(sobreHeader.get(0), Secomp);
        sobreInfo.put(sobreHeader.get(1), DC);
        sobreInfo.put(sobreHeader.get(2), UFSCar);

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
