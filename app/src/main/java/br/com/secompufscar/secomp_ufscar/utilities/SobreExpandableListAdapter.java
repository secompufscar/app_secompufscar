package br.com.secompufscar.secomp_ufscar.utilities;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import br.com.secompufscar.secomp_ufscar.R;

public class SobreExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> sobreHeader;
    private HashMap<String, List<String>> sobreInfo;

    public SobreExpandableListAdapter(Context context, List<String> sobreHeader, HashMap<String, List<String>> sobreInfo){
        this.context = context;
        this.sobreHeader = sobreHeader;
        this.sobreInfo = sobreInfo;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.sobreInfo.get(this.sobreHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.sobre_listitem, null);
        }

        TextView txtListChild = (TextView) convertView.findViewById(R.id.sobreListItem);
        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.sobreInfo.get(this.sobreHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.sobreHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.sobreHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
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
