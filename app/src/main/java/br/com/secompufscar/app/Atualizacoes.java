package br.com.secompufscar.app;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import br.com.secompufscar.app.utilities.ListTwitterAdapter;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

/*

<------ AQUELE COMENTÁRIO MANEIRO ------->

*/
public class Atualizacoes extends Fragment {
    /*
        Minhas declarações
    */

    //Nossa amada lista de tweets (Visual)
    private ListView lv;
    //Nosso amado array de tweets
    private String tweetsArray[];
    //Swipe Refresh
    private SwipeRefreshLayout swipeLayout;
    //Happening now field
    private TextView hn;

    //Testes
    AsyncTask<String, String, String> a;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Atualizacoes() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Atualizacoes.
     */
    // TODO: Rename and change types and number of parameters
    public static Atualizacoes newInstance(String param1, String param2) {
        Atualizacoes fragment = new Atualizacoes();
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
        // Faz a referência ao Swipe Refresh do XML
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_atualizacoes, container, false);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    //Executado quando a view está pronta. Caso o contrario o código tentaria
    //Acessar o que ainda não existe
    @Override
    public void onStart() {
        super.onStart();

        a = new MegaChecker();
        hn = (TextView) getView().findViewById(R.id.info_text);
        //Executa pra pegar os tweets


        ScheduledExecutorService scheduler =
                Executors.newSingleThreadScheduledExecutor();


        if (!(a.getStatus() == AsyncTask.Status.RUNNING)) {
            a.execute("");
            //Referencia o layout definido no xml
            swipeLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swiperefresh);
            //Famoso migué
            swipeLayout.setRefreshing(true);
            //Seta as corzinhas do loading (Fun)
            swipeLayout.setColorSchemeResources(R.color.loadingColor_1, R.color.loadingColor_2, R.color.loadingColor_3, R.color.loadingColor_4);
        }
        //Listener para executar o código quando der um swipezinho
        swipeLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        //Executa a atualização dos tweets
                        //Apenas se a thread não está sendo executada
                        if (!(a.getStatus() == AsyncTask.Status.RUNNING)) {
                            try {
                                new MegaChecker().execute("");
                            } catch (Exception e) {
                                Toast.makeText(getContext(), R.string.verifique, Toast.LENGTH_SHORT).show();
                                swipeLayout.setRefreshing(false);
                            }
                        }
                    }
                }

        );
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

    // No android nós não podemos realizar network calls, na atual activity, por isso usamos um "AsyncTask"
    // Uma thread que faz uma network call em background

    public class MegaChecker extends AsyncTask<String, String, String> {
        //Declaração da #NOW
        private String now = "";
        boolean ok = true;
        ArrayList<String> tweets = new ArrayList<>();

        @Override
        protected String doInBackground(String... params) {
            try {
                //Configuração na API do twitter
                ConfigurationBuilder cf = new ConfigurationBuilder();
                cf.setDebugEnabled(true)
                        .setOAuthConsumerKey(getString(R.string.OAuthConsumer))
                        .setOAuthConsumerSecret(getString(R.string.OAuthSecret))
                        .setOAuthAccessToken(getString(R.string.OAuthToken))
                        .setOAuthAccessTokenSecret(getString(R.string.OAuthTokenSecret));
                TwitterFactory tf = new TwitterFactory(cf.build());
                Twitter twitter = tf.getInstance();

                // É possível colocar vários twitters aqui
                String[] twitters = {getString(R.string.twitter_user)};

                //Primeira e única posição na lista de Twitters
                String[] srch = new String[]{twitters[0]};

                //Procura pelo nome do twitter no twitter
                ResponseList<User> users = twitter.lookupUsers(srch);

                //Para todos os usuários encontrados
                for (User user : users) {

                    // Se a timeline não for nula então
                    if (user.getStatus() != null) {

                        //Pega a timeline
                        ResponseList<twitter4j.Status> statusess = twitter.getUserTimeline(twitters[0]);

                        //Joga os tweets em um ArrayList
                        //Pode ser melhorado
                        for (twitter4j.Status status3 : statusess) {
                            tweets.add(status3.getText());
                        }
                    }
                }


                //Passa o conteúdo do arraylist para um string array

                for (int i = 0; i < tweets.size(); i++) {
                    //Agora ficou show
                    if (tweets.get(i).trim().contains(getString(R.string.now)))
                    {
                        tweets.set(i, tweets.get(i).replace(getString(R.string.now), ""));
                        if (now.isEmpty()) {
                            now = tweets.get(i);
                            tweets.remove(i);
                        }
                    }
                    tweets.set(i, tweets.get(i).replace(getString(R.string.now), ""));
                }

                tweetsArray = new String[tweets.size()];
                for (int i = 0; i < tweets.size(); i++) {
                    tweetsArray[i] = tweets.get(i);
                }
                //Se der ruim... Já sabe
            } catch (Exception e) {
                ok = false;
                return "";
            }
            return now;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != "") {
                hn.setText(now);
            }
            if (ok) {
                try {
                    //Referencia a lista do layout
                    lv = (ListView) getView().findViewById(R.id.listViewTwitter);

                    // Com o nosso adapter customizado, tenta adicionar as informações nele
                    ListTwitterAdapter adapter = new ListTwitterAdapter(getActivity(), tweetsArray);
                    //Adiciona o listAdapter no visual

                    lv.setAdapter(adapter);
                } catch (Exception e) {
                }

            } else {
                hn.setText(R.string.noconnection);
            }
            //Checa se o loading está ativo
            if (swipeLayout.isRefreshing()) {
                //Se estiver cancela ele, pois nossa tarefa já foi executada
                swipeLayout.setRefreshing(false);
            }
        }
    }
}
