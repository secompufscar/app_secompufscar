package br.com.secompufscar.app;
import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.net.Uri;
import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.elyeproj.loaderviewlibrary.LoaderTextView;


public class Home extends Fragment {
    public static String URL_API = "https://secompufscar.com.br/api/nojenta/";
    JSONObject js = new JSONObject();
    private LoaderTextView antes, agora, depois;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public  Home(){

    }

    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

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

    public void onStart() {
        super.onStart();
        antes = (LoaderTextView) getView().findViewById(R.id.antes);
        agora = (LoaderTextView) getView().findViewById(R.id.agora);
        depois = (LoaderTextView) getView().findViewById(R.id.depois);
        antes.resetLoader();
        agora.resetLoader();
        depois.resetLoader();

        MegaChecker a = new MegaChecker();
        a.execute("");

    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }
    public class MegaChecker  extends AsyncTask<String,Integer,JSONObject> {
        @Override
        protected void onPreExecute(){

        }
        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                js = readJsonFromUrl(URL_API);
                Log.i("AsyncTask", "Carregando");
            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return js;
        }
        protected void onPostExecute(JSONObject j) {
            int tam;
            js = j;
            String before="", now="", after="";
            JSONArray x = new JSONArray();

            try {
                x = js.getJSONObject("anterior").getJSONArray("atividades");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            tam = x.length();
            if(tam == 0 || x == null){
                antes.setText("Sem Atividades");
            }
            else {
                try {
                    before = x.getString(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 1; i < tam; i++) {
                    try {
                        before += "\n\n" + x.getString(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    before += "\n\n" + js.getJSONObject("anterior").getString("horario");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                antes.setText(before);
            }
            try {
                x = js.getJSONObject("agora").getJSONArray("atividades");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            tam = x.length();
            if(tam == 0 || x == null){
                agora.setText("Sem Atividades");
            }
            else {
                try {
                    now = x.getString(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 1; i < tam; i++) {
                    try {
                        now += "\n\n" + x.getString(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    now += "\n\n" + js.getJSONObject("agora").getString("horario");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                agora.setText(now);
            }

            try {
                x = js.getJSONObject("proximo").getJSONArray("atividades");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            tam = x.length();
            if(tam == 0 || x == null){
                depois.setText("Sem Atividades");
            }
            else {
                try {
                    after = x.getString(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 1; i < tam; i++) {
                    try {
                        after += "\n\n" + x.getString(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    after += "\n\n" + js.getJSONObject("anterior").getString("horario");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                depois.setText(after);
            }
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
        }

    }
}