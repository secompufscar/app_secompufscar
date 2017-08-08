package br.com.secompufscar.secomp_ufscar;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by felipequecole on 09/06/17.
 */

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Sobre.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Sobre#newInstance} factory method to
 * create an instance of this fragment.
 */
class ExpandableListAdapter extends BaseExpandableListAdapter{
    private Context _context;
    private List<String> _sobreHeader;
    private HashMap<String, List<String>> _sobreInfo;

    public ExpandableListAdapter(Context context, List<String> sobreHeader, HashMap<String, List<String>> sobreInfo){
        this._context = context;
        this._sobreHeader = sobreHeader;
        this._sobreInfo = sobreInfo;
    }



    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this._sobreInfo.get(this._sobreHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.sobre_listitem, null);
        }

        TextView txtListChild = (TextView) convertView.findViewById(R.id.sobreListItem);
        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._sobreInfo.get(this._sobreHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._sobreHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._sobreHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.sobre_listgroup, null);
        }

        TextView txtHeader = (TextView) convertView.findViewById(R.id.sobreListHeader);
        txtHeader.setTypeface(null, Typeface.BOLD);
        txtHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}



public class Sobre extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    ExpandableListAdapter listAdapter;
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

        listAdapter = new ExpandableListAdapter(getView().getContext(), sobreHeader, sobreInfo);

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
