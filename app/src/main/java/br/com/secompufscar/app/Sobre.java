package br.com.secompufscar.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.secompufscar.app.utilities.SobreExpandableListAdapter;

public class Sobre extends Fragment {
    private int lastExpanded = -1;
    SobreExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> sobreHeader;
    HashMap<String, List<String>> sobreInfo;

    public Sobre() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
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
                // fecha o anterior quando abre o novo
                // [comentar aqui para desativar esse feature]
                if(lastExpanded != -1 && groupPosition  != lastExpanded) {
                    expListView.collapseGroup(lastExpanded);
                }
                lastExpanded = groupPosition;
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
        sobreHeader.add(getString(R.string.dev));

        List<String> Secomp = new ArrayList<String>();
        Secomp.add(getResources().getString(R.string.infoSecomp));
        Secomp.add(getResources().getString(R.string.infoSecomp2));
        Secomp.add(getResources().getString(R.string.infoSecomp3));

        List<String> DC = new ArrayList<String>();
        DC.add(getResources().getString(R.string.infoDC));
        DC.add(getResources().getString(R.string.infoDC2));
        DC.add(getResources().getString(R.string.infoDC3));


        List<String> UFSCar = new ArrayList<String>();
        UFSCar.add(getResources().getString(R.string.infoUFSCar));
        UFSCar.add(getResources().getString(R.string.infoUFSCar2));
        UFSCar.add(getResources().getString(R.string.infoUFSCar3));

        List<String> Desenvolvedores = new ArrayList<>();
        Log.d("Preenchendo o map", getString(R.string.dev1));
        Desenvolvedores.add(getString(R.string.dev1));


        sobreInfo.put(sobreHeader.get(0), Secomp);
        sobreInfo.put(sobreHeader.get(1), DC);
        sobreInfo.put(sobreHeader.get(2), UFSCar);
        sobreInfo.put(sobreHeader.get(3), Desenvolvedores);

    }
}
