package br.com.secompufscar.secomp_ufscar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
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
import java.util.ArrayList;
import java.util.Date;

import br.com.secompufscar.secomp_ufscar.utilities.NetworkUtils;

/**
 * Created by olivato on 08/08/17.
 */

public class Instagram extends Fragment {
    private final static String URL_INSTAGRAM = "https://beta.secompufscar.com.br/api/instagram/";
    private RecyclerView igRecyclerView;
    private InstagramAdapter igAdapter;
    private static ArrayList<InstagramPost> timelinePosts = new ArrayList<>();
    private static ArrayList<String> alreadyParsed = new ArrayList<>();

    public Instagram(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        igAdapter = new InstagramAdapter(timelinePosts);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rs_instagram, container, false);
        igRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_instagram);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        igRecyclerView.setLayoutManager(mLayoutManager);

        igRecyclerView.setAdapter(igAdapter);

        return view;
    }


    private static void parseJSON (String entrada) {
        try {
            JSONObject js = new JSONObject(entrada);
            Log.d("instagram", js.toString());
            JSONArray posts = (JSONArray) js.get("data");
            InstagramAdapter.setUsername(
                    js.getJSONObject("user").getString("username")
            );
            InstagramAdapter.setUser_photo(
                    js.getJSONObject("user").getString("profile_picture")
            );
            Log.d("instagram", posts.toString());
            for (int i = 0; i < posts.length(); i++){
                JSONObject post = (JSONObject) posts.get(i);
                String id = post.getString("id");
                if (alreadyParsed.contains(id)) {
                    continue;
                } else {
                    alreadyParsed.add(id);
                }
                String url_image = post.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
                String legenda = "";
                if (post.has("caption")){
                    legenda = post.getJSONObject("caption").getString("text");
                }
                String created_time = "-";
                if (post.has("created_time")){
                    created_time = post.getString("created_time");
                }
                timelinePosts.add(
                        new InstagramPost(
                                id,
                                url_image,
                                legenda,
                                created_time
                        )
                );
            }
            Log.d("instagram", timelinePosts.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void getTimelineFromHTTP(){
        try {
            URL url = new URL(URL_INSTAGRAM);
            String response = NetworkUtils.getResponseFromHttpUrl(url);
            parseJSON(response);
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

        public InstagramPost(String id, String image_url, String legenda, String createdTime){
            this.setImage(image_url);
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

        public void setImage(String url) {
            try {
                this.image = NetworkUtils.getImageFromHttpUrl(url);
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
            if(createdTime.equals("-")){
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
        canvas.drawRoundRect((new RectF(0, 0, image.getWidth(), image.getHeight())), image.getWidth()/2, image.getWidth()/2, paint);// Round Image Corner 100 100 100 100

        return imageRounded;
    }
}
