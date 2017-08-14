package br.com.secompufscar.secomp_ufscar.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import br.com.secompufscar.secomp_ufscar.MainActivity;


public class NetworkUtils {

    public final static String BASE_URL = "https://secompufscar.com.br/";
    public static boolean CONNECTED = false;

    public static URL buildUrl(String path) {

        Uri builtUri = Uri.parse(BASE_URL + path).buildUpon().build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static boolean updateConnectionState(Context context) {
        try {

            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);

            //For WiFi Check
            boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                    .isConnectedOrConnecting();

            if (isWifi) {
                CONNECTED = true;
            } else {
                //For 3G check
                boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                        .isConnectedOrConnecting();

                SharedPreferences preferencias = context.getSharedPreferences("Settings", 0);
                boolean internet = preferencias.getBoolean("internet", false);

                if (internet && is3g) {
                    CONNECTED = true;
                } else {
                    CONNECTED = false;
                }
            }

            return CONNECTED;
        } catch (NullPointerException e){
            e.printStackTrace();
            return false;
        }
    }

    @Nullable
    public static String getResponseFromHttpUrl(URL url, Context context) throws IOException {
        if (updateConnectionState(context)) {

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                InputStream in = urlConnection.getInputStream();

                Scanner scanner = new Scanner(in);
                scanner.useDelimiter("\\A");

                boolean hasInput = scanner.hasNext();
                if (hasInput) {
                    return scanner.next();
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                urlConnection.disconnect();
            }
        } else {
            return null;
        }
    }

    // Retorna verdadeiro se for necessário atualizar;
    public static boolean checkUpdate(Context context, String datetime, String preferenceName) {
        boolean status;
        SharedPreferences dataUltimaAtualizacao = context.getSharedPreferences("updateDatetime", Context.MODE_PRIVATE);

        String ultimaAtualizacao = dataUltimaAtualizacao.getString(preferenceName, null);
        SharedPreferences.Editor editor = dataUltimaAtualizacao.edit();

        if (ultimaAtualizacao != null) {
            if (!ultimaAtualizacao.equals(datetime)) {
                editor.putString(preferenceName, datetime);
                status = true;
            } else {
                status = false;
            }
        } else {
            editor.putString(preferenceName, datetime);
            status = true;
        }

        editor.apply();

        return status;
    }

    public static byte[] getImageFromHttpUrl(String url, Context context) throws IOException {
        if (updateConnectionState(context)) {
            try {
                URL imageUrl = new URL(url);
                HttpURLConnection urlConnection = (HttpURLConnection) imageUrl.openConnection();

                InputStream in = urlConnection.getInputStream();
                BufferedInputStream input = new BufferedInputStream(in);
                ByteArrayOutputStream output = new ByteArrayOutputStream();

                int current;

                while ((current = input.read()) != -1) {
                    output.write((byte) current);
                }
                return output.toByteArray();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
