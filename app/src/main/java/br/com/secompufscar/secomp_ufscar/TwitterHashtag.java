package br.com.secompufscar.secomp_ufscar;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

import br.com.secompufscar.secomp_ufscar.utilities.ListHashtagAdapter;
import twitter4j.MediaEntity;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by olivato on 08/08/17.
 */

public class TwitterHashtag extends Fragment {
    private SwipeRefreshLayout swipeLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        return inflater.inflate(R.layout.rs_twitter, container, false);

    }
    @Override
    public void onStart() {
        super.onStart();
        swipeLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swiperefresh);
        //Famoso migué
        swipeLayout.setRefreshing(true);
        //Seta as corzinhas do loading (Fun)
        swipeLayout.setColorSchemeResources(R.color.loadingColor_1, R.color.loadingColor_2, R.color.loadingColor_3);
        swipeLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                        //Executa a atualização dos tweets
                        //Apenas se a thread não está sendo executada
                            try {
                                new MegaChecker().execute("");
                            } catch (Exception e) {
                                Toast.makeText(getContext(), R.string.verifique, Toast.LENGTH_SHORT).show();
                                swipeLayout.setRefreshing(false);
                            }
                        }
                }

        );
        new MegaChecker().execute("");
    }
    public class MegaChecker extends AsyncTask<String, String, String> {
        /*private ArrayList<String> user = new ArrayList();
        private ArrayList<String> tweet = new ArrayList();
        private ArrayList<String> location = new ArrayList();*/
        String[] tweet;
        String[] user;
        String[] location;
        String[] photos;
        String[] username;
        Date[] datas;
        ArrayList<ArrayList<String>> medias;
        boolean[] hasMedia;
        @Override
        protected void onPreExecute() {

        }

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
                Query query = new Query("#secompufscar");
                QueryResult result = twitter.search(query);
                int tamanho = result.getTweets().size();
                tweet = new String[tamanho];
                user = new String[tamanho];
                location = new String[tamanho];
                photos = new String[tamanho];
                username = new String[tamanho];
                datas = new Date[tamanho];
                medias = new ArrayList<>();
                hasMedia = new boolean[tamanho];

                int x=0;
                for (twitter4j.Status status : result.getTweets())
                {
                    medias.add(extractMediaURL(status.getMediaEntities()));
                    if (medias.get(x).isEmpty()){
                        hasMedia[x] = false;
                    } else {
                        hasMedia[x] = true;
                    }
                    user[x] = ("@"+status.getUser().getScreenName());
                    tweet[x] = (status.getText());
                    photos[x] = (status.getUser().getOriginalProfileImageURL());
                    username[x] = (status.getUser().getName());
                    datas[x] = status.getCreatedAt();
                    if(status.getGeoLocation()!=null)
                        location[x] = (status.getGeoLocation().toString());
                    x++;
                }

                //Se der ruim... Já sabe
            } catch (Exception e) {
                e.printStackTrace();
                return "Deu ruim";
            }
            return null;
        }

        private ArrayList<String> extractMediaURL(MediaEntity[] medias){
            ArrayList<String> urls = new ArrayList<>();
            for (MediaEntity m : medias){
                if (m.getType().equals("photo")){
                    urls.add(m.getMediaURL());
                }
            }
            return urls;
        }

        @Override
        protected void onPostExecute(String s) {
            //Checa se o loading está ativo
            if (swipeLayout.isRefreshing()) {
                //Se estiver cancela ele, pois nossa tarefa já foi executada
                swipeLayout.setRefreshing(false);
            }

            if(s!="Deu ruim") {
                try {
                    ListView ht;
                    ht = (ListView) getView().findViewById(R.id.hashtag);
                    ListHashtagAdapter adapter = new ListHashtagAdapter(getActivity(), user, tweet, location, photos, username, datas, medias, hasMedia);
                    ht.setAdapter(adapter);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
