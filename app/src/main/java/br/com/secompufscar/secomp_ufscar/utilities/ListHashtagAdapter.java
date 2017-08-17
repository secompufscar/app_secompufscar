package br.com.secompufscar.secomp_ufscar.utilities;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import br.com.secompufscar.secomp_ufscar.R;

/**
 * Created by olivato on 18/05/17.
 */
// Layout criado para mostrar os tweets em um listview²
public class ListHashtagAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] users;
    private final String[] tweet;
    private final String[] location;


    public ListHashtagAdapter(Activity context, String[] users,String[] tweet,String[] location) {
        super(context, R.layout.listview_twitter, tweet);
        this.context = context;
        this.users = users;
        this.tweet = tweet;
        this.location = location;

    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.listview_hashtag,parent, false);
        TextView user = (TextView) rowView.findViewById(R.id.user);
        TextView locationo = (TextView) rowView.findViewById(R.id.location);
        TextView tweeto = (TextView) rowView.findViewById(R.id.tweet);
        user.setText(users[position]);
        tweeto.setText(tweet[position]);
        if(location[position]!=null) {
            locationo.setText(location[position]);
        }
        return rowView;
    }

}