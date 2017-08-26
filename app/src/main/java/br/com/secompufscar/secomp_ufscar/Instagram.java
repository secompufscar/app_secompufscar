package br.com.secompufscar.secomp_ufscar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import br.com.secompufscar.secomp_ufscar.adapters.InstagramAdapter;
import br.com.secompufscar.secomp_ufscar.utilities.NetworkUtils;

/**
 * Created by olivato on 08/08/17.
 */

public class Instagram extends Fragment {
    private final static String URL_INSTAGRAM = "https://beta.secompufscar.com.br/api/instagram/";
    private RecyclerView igRecyclerView;
    private InstagramAdapter igAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static ArrayList<InstagramPost> timelinePosts = new ArrayList<>();
    private static ArrayList<String> alreadyParsed = new ArrayList<>();

    public Instagram() {

    }

    private static boolean usingWIFI(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        new GetDataTask().execute();
        return inflater.inflate(R.layout.rs_instagram, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.ig_Swipe);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.loadingColor_1,
                R.color.loadingColor_2,
                R.color.loadingColor_3);
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        try {
                            new GetDataTask().execute();
                        } catch (Exception e) {
                            Toast.makeText(getContext(), R.string.verifique, Toast.LENGTH_SHORT).show();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }
        );
        igRecyclerView = (RecyclerView) getView().findViewById(R.id.recycler_instagram);
        igAdapter = new InstagramAdapter(timelinePosts);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        igRecyclerView.setLayoutManager(mLayoutManager);
        igRecyclerView.setAdapter(igAdapter);

    }

    private static void parseJSON(String entrada, Context context) {
        try {
            JSONObject js = new JSONObject(entrada);
            Log.d("instagram", js.toString());
            JSONArray posts = (JSONArray) js.get("data");
            InstagramAdapter.setUsername(
                    js.getJSONObject("user").getString("username")
            );
            InstagramAdapter.setUser_photo(
                    js.getJSONObject("user").getString("profile_picture"),
                    context);
            Log.d("instagram", posts.toString());
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = (JSONObject) posts.get(i);
                String id = post.getString("id");
                if (alreadyParsed.contains(id)) {
                    continue;
                } else {
                    alreadyParsed.add(id);
                }
                String url_image;
                if (Instagram.usingWIFI(context)) {
                    url_image = post.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
                } else {
                    url_image = post.getJSONObject("images").getJSONObject("low_resolution").getString("url");
                }
                String legenda = "";
                if (post.has("caption")) {
                    legenda = post.getJSONObject("caption").getString("text");
                }
                String created_time = "-";
                if (post.has("created_time")) {
                    created_time = post.getString("created_time");
                }
                InstagramAdapter.posts.add(
                        new InstagramPost(
                                id,
                                url_image,
                                legenda,
                                created_time,
                                context)
                );
            }
            Log.d("instagram", timelinePosts.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void getTimelineFromHTTP(Context context) {
        try {
            URL url = new URL(URL_INSTAGRAM);
            String response = NetworkUtils.getResponseFromHttpUrl(url, context);
            parseJSON(response, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static class InstagramPost {
        private byte[] image;
        private String legenda;
        private String id;
        private Date createdTime;
        private String tags;

        public InstagramPost(String id, String image_url, String legenda, String createdTime, Context context) {
            this.setImage(image_url, context);
            this.setLegenda(legenda);
            this.setId(id);
            this.setCreatedTime(createdTime);
        }

        public byte[] getImage() {
            return image;
        }

        public String getLegenda() {
            return legenda;
        }

        public String getId() {
            return id;
        }

        public Date getCreatedTime() {
            return createdTime;
        }

        public String getTags() {
            return tags;
        }

//        METODOS SETTER

        public void setImage(String url, Context context) {
            try {
                this.image = NetworkUtils.getImageFromHttpUrl(url, context);
            } catch (IOException e) {
                this.image = null;
                e.printStackTrace();
            }
        }

        public void setLegenda(String legenda) {
            this.legenda = legenda;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setCreatedTime(String createdTime) {
            if (createdTime.equals("-")) {
                this.createdTime = new Date(2017, 6, 8);
            } else {
                this.createdTime = Facebook.dataHoraParser(createdTime);
            }
        }

        public void setTags(String tags) {
            this.tags = tags;
        }
    }

    public static Bitmap getImageRound(byte[] imageByte) {

        Bitmap image;

        image = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);

        //TODO: Verificar se o formato da imagem está correto;
        Bitmap imageRounded = Bitmap.createBitmap(image.getWidth(), image.getHeight(), image.getConfig());
        Canvas canvas = new Canvas(imageRounded);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(image, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawRoundRect((new RectF(0, 0, image.getWidth(), image.getHeight())), image.getWidth() / 2, image.getWidth() / 2, paint);// Round Image Corner 100 100 100 100

        return imageRounded;
    }

    private class GetDataTask extends AsyncTask<Void, Void, Void> {
        private int nrOfPosts;


        @Override
        protected void onPreExecute() {
            try {
                nrOfPosts = InstagramAdapter.posts.size();
            } catch (NullPointerException e) {
                nrOfPosts = 0;
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            getTimelineFromHTTP(getContext());
            return null;
        }


        @Override
        protected void onPostExecute(Void s) {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }

            if (InstagramAdapter.posts.size() > nrOfPosts) {
                Log.d("Instagram", "tem post novo");
                igAdapter.notifyDataSetChanged();
            }
        }
    }
}
