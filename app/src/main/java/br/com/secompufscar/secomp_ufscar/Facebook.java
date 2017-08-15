package br.com.secompufscar.secomp_ufscar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import br.com.secompufscar.secomp_ufscar.utilities.NetworkUtils;


public class Facebook extends Fragment {
    private RecyclerView fbRecyclerView;
    private FacebookAdapter fbAdapter;
    private final static String URL_FACEBOOK = "https://beta.secompufscar.com.br/api/facebook/";

//    private AccessToken token = new AccessToken(ACCESS_TOKEN, APP_ID, USER_ID)
    private static ArrayList<FacebookPost> timelinePosts = new ArrayList<>();
    private static ArrayList<String> alreadyParsed = new ArrayList<>();


    public Facebook() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fbAdapter = new FacebookAdapter(timelinePosts);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rs_facebook, container, false);
        fbRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_facebook);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        fbRecyclerView.setLayoutManager(mLayoutManager);

        fbRecyclerView.setAdapter(fbAdapter);

        return view;
    }


    private static void parseJSON (String entrada) {
        try {
            JSONObject js = new JSONObject(entrada);
            Log.d("facebook", js.toString());
            JSONArray posts = js.getJSONObject("posts").getJSONArray("data");
            FacebookAdapter.setUsername(js.getString("name"));
            FacebookAdapter.setUser_photo(
                    js.getJSONObject("picture").getJSONObject("data").getString("url")
            );
            for (int i = 0; i < posts.length(); i++){
                JSONObject post = (JSONObject) posts.get(i);
                if (alreadyParsed.contains(post.getString("id"))){
                    continue;
                }
                alreadyParsed.add(post.getString("id"));
                String message = "";
                if(post.has("message")) {
                    message = post.getString("message");
                }
                String shares = "0";
                if(post.has("shares")){
                    shares = post.getJSONObject("shares").getString("count");
                }
                timelinePosts.add(
                        new FacebookPost(
                                post.getString("id"),
                                message,
                                post.getString("full_picture"),
                                post.getString("created_time"),
                                post.getJSONObject("likes").getJSONObject("summary").getString("total_count"),
                                post.getJSONObject("comments").getJSONObject("summary").getString("total_count"),
                                shares
                        )
                );
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void getTimelineFromHTTP(){
        try {
            URL url = new URL(URL_FACEBOOK);
            String response = NetworkUtils.getResponseFromHttpUrl(url);
            parseJSON(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Date dataHoraParser(String dateString) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date value = null;
        try {
            value = formatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return value;
    }

    public static class FacebookPost{
        private Date createdTime;
        private byte[] image;
        private String message;
        private String id;
        private String likes;
        private String comments;
        private String shares;

        public FacebookPost(String id, String message, String image_url, String created_time,
                                String likes, String comments, String shares){
            this.setCreatedTime(created_time);
            this.setId(id);
            this.setImage(image_url);
            this.setMessage(message);
            this.likes = likes;
            this.comments = comments;
            this.shares = shares;
        }

        public void setCreatedTime(String createdTime) {
            this.createdTime = dataHoraParser(createdTime);
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setImage(String image_url) {
            try {
                this.image = NetworkUtils.getImageFromHttpUrl(image_url);
            } catch (IOException e) {
                this.image = null;
                e.printStackTrace();
            }
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public String getId() {
            return id;
        }

        public byte[] getImage() {
            return image;
        }

        public Date getCreatedTime() {
            return createdTime;
        }

        public String getComments() {
            return comments;
        }

        public String getLikes() {
            return likes;
        }

        public String getShares() {
            return shares;
        }
    }

}
