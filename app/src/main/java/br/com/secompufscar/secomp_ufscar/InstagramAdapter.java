package br.com.secompufscar.secomp_ufscar;

import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
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

import br.com.secompufscar.secomp_ufscar.utilities.NetworkUtils;

/**
 * Created by felipequecole on 11/08/17.
 */

public class InstagramAdapter extends RecyclerView.Adapter<InstagramAdapter.ViewHolder>{
    public static ArrayList<Instagram.InstagramPost> posts;
    private static String username;
    private static byte[] user_photo;

    public static void setUser_photo(String url) {
        try {
            InstagramAdapter.user_photo = NetworkUtils.getImageFromHttpUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setUsername(String username) {
        InstagramAdapter.username = username;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView info;
        public ImageView foto;
        public TextView legenda;
        public ImageView user_photo;
        public TextView tempo;
        public ViewHolder(View view) {
            super(view);
            info = (TextView) view.findViewById(R.id.ig_info);
            foto = (ImageView) view.findViewById(R.id.ig_image);
            legenda = (TextView) view.findViewById(R.id.ig_legenda);
            user_photo = (ImageView) view.findViewById(R.id.ig_profilepicture);
            tempo = (TextView) view.findViewById(R.id.ig_tempo);
        }
    }

    public InstagramAdapter(ArrayList<Instagram.InstagramPost> posts){
        InstagramAdapter.posts = posts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.rs_instagram_row, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.info.setText(username);
        holder.foto.setImageBitmap(
                BitmapFactory.decodeByteArray(posts.get(position).getImage(), 0, posts.get(position).getImage().length)
        );
        SpannableStringBuilder str = new SpannableStringBuilder(InstagramAdapter.username);
        str.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        str.append(" ");
        str.append(posts.get(position).getLegenda());
        holder.legenda.setText(str);
        holder.user_photo.setImageBitmap(Instagram.getImageRound(InstagramAdapter.user_photo));
        String out = getFormattedTimeInstagram(posts.get(position).getCreatedTime());
        holder.tempo.setText(out);
    }

    private String getFormattedTimeInstagram(Date data){
        DateTime d1 = new DateTime(data);
        DateTime d2 = DateTime.now();
        long difference = d2.getMillis() - d1.getMillis();
        DateTime d3 = new DateTime(difference);
        long days = TimeUnit.MILLISECONDS.toDays(d3.getMillis());
        long hours = TimeUnit.MILLISECONDS.toHours(d3.getMillis());
        String out  = "";
        if (hours < 24){
            out =  "HÁ " + String.valueOf(hours) + " HORAS.";
            if (hours < 0) {
                //TODO tentar entender esse bug, enquanto isso, gambiarra rs
                out = "5 DE JUNHO DE 2017";
            }
        } else if (days < 8) {
            out = "HÁ " +  String.valueOf(days) + " DIAS.";
        } else {
            SimpleDateFormat format = new SimpleDateFormat("d,MMMM,yyyy", new Locale("pt", "BR"));
            format.setTimeZone(TimeZone.getDefault());
            out = format.format(data);
            out = out.replace(",", " DE ");
            //TODO: corrigir espaçamento no layout e colocar tudo em maiuscula com semparador "DE"
        }
        out = out.toUpperCase();
        out = out.replace(" DE 2017", "");
        return out;
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }


}


