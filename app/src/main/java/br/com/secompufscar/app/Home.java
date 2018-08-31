package br.com.secompufscar.app;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


public class Home extends Fragment {
    public static String URL_API = "https://secompufscar.com.br/api/nojenta/";
    final private TextView antes = (TextView) getView().findViewById(R.id.antes);
    final private TextView agora = (TextView) getView().findViewById(R.id.agora);
    final private TextView depois = (TextView) getView().findViewById(R.id.depois);

    public  Home(){

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_atualizacoes, container, false);
    }
    public void onStart() {
        super.onStart();
        JSONObject j = this.parseJSON();
        try {
            antes.setText(j.getJSONObject("antes").getString("0"));
            agora.setText(j.getJSONObject("agora").getString("0"));
            depois.setText(j.getJSONObject("depois").getString("0"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private static JSONObject parseJSON() {
        try {
            JSONObject js = new JSONObject(URL_API);
            return js;
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }
}