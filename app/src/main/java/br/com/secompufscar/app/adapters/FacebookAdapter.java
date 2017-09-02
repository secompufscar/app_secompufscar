package br.com.secompufscar.app.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import br.com.secompufscar.app.Facebook;
import br.com.secompufscar.app.R;
import br.com.secompufscar.app.utilities.NetworkUtils;

/**
 * Created by felipequecole on 11/08/17.
 */

public class FacebookAdapter extends RecyclerView.Adapter<FacebookAdapter.ViewHolder> {
    public static ArrayList<Facebook.FacebookPost> posts;
    private static String username;
    private static byte[] user_photo;

    public static void setUsername(String username) {
        FacebookAdapter.username = username;
    }

    public static void setUser_photo(String user_photo, Context context) {
        try {
            FacebookAdapter.user_photo = NetworkUtils.getImageFromHttpUrl(user_photo, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView conteudo_post;
        public ImageView foto_post;
        public ImageView profile_picture;
        public TextView username;
        public TextView created_time;

        public ViewHolder(View view) {
            super(view);
            conteudo_post = (TextView) view.findViewById(R.id.fb_text);
            foto_post = (ImageView) view.findViewById(R.id.fb_photo);
            profile_picture = (ImageView) view.findViewById(R.id.fb_profilepicture);
            username = (TextView) view.findViewById(R.id.fb_username);
            created_time = (TextView) view.findViewById(R.id.fb_createdtime);
        }
    }

    public FacebookAdapter(ArrayList<Facebook.FacebookPost> posts) {
        FacebookAdapter.posts = posts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.rs_facebook_row, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.conteudo_post.setText(posts.get(position).getMessage());

        if (posts.get(position).getImage() != null) {
            holder.foto_post.setImageBitmap(
                    BitmapFactory.decodeByteArray(posts.get(position).getImage(), 0, posts.get(position).getImage().length)
            );
        }

        if(FacebookAdapter.user_photo != null){
            holder.profile_picture.setImageBitmap(
                    BitmapFactory.decodeByteArray(FacebookAdapter.user_photo, 0, FacebookAdapter.user_photo.length)
            );
        }

        holder.username.setText(FacebookAdapter.username);
        holder.created_time.setText(getTimeStampString(posts.get(position).getCreatedTime()));
    }

    private String getTimeStampString(Date dt) {
        String out;
        DateTime d1 = new DateTime(dt);
        DateTime d2 = DateTime.now();
        long difference = d2.getMillis() - d1.getMillis();
        long minutes = TimeUnit.MILLISECONDS.toMinutes(difference);
        long hours = TimeUnit.MILLISECONDS.toHours(difference);
        long days = TimeUnit.MILLISECONDS.toDays(difference);
        int daydif = d2.getDayOfYear() - d1.getDayOfYear();
        boolean thisyear = (d1.getYear() == d2.getYear());
        boolean ontem = (daydif == 1 && (days < 2));
        if (minutes < 60) {
            out = String.valueOf(minutes) + " min";
        } else if (hours < 24 && !ontem) {
            out = String.valueOf(hours) + " h";
        } else {
            SimpleDateFormat format = new SimpleDateFormat("d,MMM/yyyy-H:mm", new Locale("pt", "BR"));
            format.setTimeZone(TimeZone.getDefault());
            String aux = format.format(dt);
            if (ontem) {
                String[] split = aux.split("-");
                out = "Ontem às " + split[split.length - 1];
            } else {
                if (thisyear) {
                    String year = String.valueOf(d2.getYear());
                    out = aux.replace(",", " de ");
                    out = out.replace("/" + year + "-", " às ");
                } else {
                    out = aux.replace(",", " de ");
                    out = out.replace("/", " de");
                    out = out.replace("-", " às ");
                }
            }
        }
        return out;
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}


