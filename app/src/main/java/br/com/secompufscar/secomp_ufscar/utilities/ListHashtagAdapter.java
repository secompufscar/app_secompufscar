package br.com.secompufscar.secomp_ufscar.utilities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

import br.com.secompufscar.secomp_ufscar.R;

/**
 * Created by olivato on 18/05/17.
 */
// Layout criado para mostrar os tweets em um listview²
public class ListHashtagAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] users;
    private final String[] tweet;
    private final String[] location;
    private final String[] photo;
    private final String[] username;
    private final Date[] datas;
    private final boolean[] hasMedia;
    private final ArrayList<ArrayList<String>> media;


    public ListHashtagAdapter(Activity context, String[] users, String[] tweet, String[] location, String[] photos, String[] username, Date[] datas, ArrayList<ArrayList<String>> media, boolean[] hasMedia) {
        super(context, R.layout.listview_twitter, tweet);
        this.context = context;
        this.users = users;
        this.tweet = tweet;
        this.location = location;
        this.photo = photos;
        this.username = username;
        this.datas = datas;
        this.media = media;
        this.hasMedia = hasMedia;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.listview_hashtag, parent, false);
        TextView user = (TextView) rowView.findViewById(R.id.ht_user);
        TextView locationo = (TextView) rowView.findViewById(R.id.location);
        TextView tweeto = (TextView) rowView.findViewById(R.id.tweet);
        TextView username_v = (TextView) rowView.findViewById(R.id.ht_name);
        ImageView photo_v = (ImageView) rowView.findViewById(R.id.ht_photo);
        TextView data_v = (TextView) rowView.findViewById(R.id.ht_data);
        ImageView tweet_image = (ImageView) rowView.findViewById(R.id.ht_image);

        user.setText(users[position]);

        if (hasMedia[position]) { // se tem foto no tweet
            tweet_image.setImageBitmap(getTweetImage(media.get(position).get(0)));
        }

        tweeto.setText(getFormattedTweet(tweet[position], hasMedia[position]));

        username_v.setText(username[position]);

        if (datas[position] != null) {
            data_v.setText(getFormatedData(datas[position]));
        }

        if (photo[position] != null) {
            try {
                photo_v.setImageBitmap(
                        getFotoBitmap(getImageByte(photo[position]))
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (location[position] != null) {
            locationo.setText(location[position]);
        } else {
            // só pra dar uma ajeitada no layout e não ficar um espaço em branco muito grande
            locationo.setVisibility(View.GONE);
        }


        return rowView;
    }

    private byte[] getImageByte(String url) {
        try {
            return NetworkUtils.getImageFromHttpUrl(url, getContext());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Bitmap getTweetImage(String url) {
        byte[] image_b = getImageByte(url);

        Bitmap image;

        if (image_b != null) {
            image = BitmapFactory.decodeByteArray(image_b, 0, image_b.length);
        } else {
            image = BitmapFactory.decodeResource(context.getResources(), R.drawable.pessoa_foto_default);
        }

        Bitmap imageRounded = Bitmap.createBitmap(image.getWidth(), image.getHeight(), image.getConfig());
        Canvas canvas = new Canvas(imageRounded);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(image, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawRoundRect((new RectF(0, 0, image.getWidth(), image.getHeight())), 12, 12, paint);
        return imageRounded;
    }

    private Bitmap getFotoBitmap(byte[] photo) {

        Bitmap image;
        image = BitmapFactory.decodeByteArray(photo, 0, photo.length);
        Bitmap imageRounded = Bitmap.createBitmap(image.getWidth(), image.getHeight(), image.getConfig());
        Canvas canvas = new Canvas(imageRounded);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(image, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawRoundRect((new RectF(0, 0, image.getWidth(), image.getHeight())), image.getWidth() / 2, image.getWidth() / 2, paint);// Round Image Corner 100 100 100 100

        return imageRounded;
    }

    private String getFormatedData(Date d) {
        String out;
        DateTime d1 = new DateTime(d);
        DateTime now = DateTime.now();
        long diff = now.getMillis() - d1.getMillis();
        long diff_hours = TimeUnit.MILLISECONDS.toHours(diff);
        if (diff_hours < 24) {
            if (diff_hours > 0) {
                out = String.valueOf(diff_hours) + "h";
            } else {
                out = String.valueOf(TimeUnit.MILLISECONDS.toMinutes(diff)) + " min";
            }
        } else {
            if (now.getYear() == d1.getYear()) {
                SimpleDateFormat format = new SimpleDateFormat("d MMM", new Locale("pt", "BR"));
                format.setTimeZone(TimeZone.getDefault());
                out = format.format(d);
            } else {
                SimpleDateFormat format = new SimpleDateFormat("d MMM yy", new Locale("pt", "BR"));
                format.setTimeZone(TimeZone.getDefault());
                out = format.format(d);
            }
        }
        return out;
    }

    private SpannableStringBuilder getFormattedTweet(String tweet, boolean hasMedia) {
        if (hasMedia) {
            int excluir = tweet.indexOf("https://t.co/");
            while (tweet.indexOf("https://t.co/", excluir + 1) != excluir) {
                int excluir_novo = tweet.indexOf("https://t.co/", excluir + 1);
                if (excluir_novo == -1) {
                    break;
                } else {
                    excluir = excluir_novo;
                }
            }
            tweet = tweet.replace(tweet.substring(excluir, tweet.length()), "");
        }
        SpannableStringBuilder str = new SpannableStringBuilder(tweet);
        ArrayList<int[]> indexes = new ArrayList<>();
        int start = -1;
        while (true) { // É TÃO DIVERTIDO BRINCAR COM FOGO.
            int current = tweet.indexOf("#", start + 1);
            if (current == -1) {
                break;
            }
            indexes.add(new int[]{current, tweet.indexOf(" ", current)});
            start = current;
        }
        for (int[] arr : indexes) {
            int inicio = arr[0];
            int fim = arr[1];
            if (fim < 0) {
                fim = str.length();
            }
            str.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), inicio, fim, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return str;
    }

}