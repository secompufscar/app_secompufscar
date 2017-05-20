package br.com.secompufscar.secomp_ufscar;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import br.com.secompufscar.secomp_ufscar.utilities.ListTwitterAdapter;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

/*

<------ AQUELE COMENTÁRIO MANEIRO ------->

->Seria mais interessante e melhor, caso esses Fragments , fossem activities

*/
public class Home extends Fragment {
    /*
        Minhas declarações
    */
    private ListView lv;
    private String tweetsArray[];
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home.
     */
    // TODO: Rename and change types and number of parameters
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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            // Onde vai aparecer os tweets




        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        new MegaChecker().execute("");
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

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

    // No android nós não podemos realizar network calls, na atual activity, por isso usamos um "AsyncTask"
    // Uma thread que faz uma network call em background

    public class MegaChecker extends AsyncTask<String,String,String>
    {
        boolean ok = true;
        ArrayList<String> tweets = new ArrayList<>();

        @Override
        protected void onPreExecute()
        {
            /*new SimpleTooltip.Builder(getContext())
                    .anchorView(lv)
                    .text("Loading...")
                    .gravity(Gravity.CENTER)
                    .animated(true)
                    .transparentOverlay(false)
                    .build()
                    .show();*/

        }
        @Override
        protected String doInBackground(String... params) {


            ConfigurationBuilder cf = new ConfigurationBuilder();
            cf.setDebugEnabled(true)
                    .setOAuthConsumerKey(getString(R.string.OAuthConsumer))
                    .setOAuthConsumerSecret(getString(R.string.OAuthSecret))
                    .setOAuthAccessToken(getString(R.string.OAuthToken))
                    .setOAuthAccessTokenSecret(getString(R.string.OAuthTokenSecret));
            TwitterFactory tf = new TwitterFactory(cf.build());
            Twitter twitter = tf.getInstance();

            // É possível colocar vários twitters aqui
            String[] twitters={"feedtisecomp"};



            try {
                //Primeira e única posição na lista de Twitters
                String[] srch = new String[]{twitters[0]};

                //Procura pelo nome do twitter no twitter
                ResponseList<User> users = twitter.lookupUsers(srch);

                //Para todos os usuários encontrados
                for (User user : users) {

                    //Pega o nome encontrado, só pra ter certeza que pegamos o twitter certo
                    System.out.println("TWITTER:" + user.getName());

                    // Se a timeline não for nula então
                    if (user.getStatus() != null) {
                        System.out.println("Timeline");

                        //Pega a timeline
                        ResponseList<twitter4j.Status> statusess = twitter.getUserTimeline(twitters[0]);

                        //Joga os tweets em um ArrayList
                        //Pode ser melhorado
                        for (twitter4j.Status status3 : statusess)
                        {
                            tweets.add(status3.getText());
                        }
                    }
                }
                //Passa o conteúdo do arraylist para um string array
                tweetsArray = new String[tweets.size()];
                for(int i=0;i<tweetsArray.length;i++)
                {
                    tweetsArray[i]=tweets.get(i).toString();
                }

            } catch (TwitterException e) {
                ok = false;
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if(ok) {
                lv = (ListView)getView().findViewById(R.id.listViewTwitter);
                ListTwitterAdapter adapter = new ListTwitterAdapter(getActivity(), tweetsArray);
                lv.setAdapter(adapter);
            }
        }
    }
}
