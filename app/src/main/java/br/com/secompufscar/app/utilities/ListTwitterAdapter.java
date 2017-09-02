package br.com.secompufscar.app.utilities;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import br.com.secompufscar.app.R;

/**
 * Created by olivato on 18/05/17.
 */
// Layout criado para mostrar os tweets em um listview
public class ListTwitterAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] display;


    public ListTwitterAdapter(Activity context, String[] display) {
        super(context, R.layout.listview_twitter, display);
        this.context = context;
        this.display = display;


    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.listview_twitter,parent, false);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.textView_twitter);
        txtTitle.setText(display[position]);
        return rowView;
    }

}