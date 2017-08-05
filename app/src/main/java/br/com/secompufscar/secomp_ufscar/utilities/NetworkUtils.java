package br.com.secompufscar.secomp_ufscar.utilities;

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


public class NetworkUtils {

    public final static String BASE_URL = "https://beta2.secompufscar.com.br/";

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

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */

    @Nullable
    public static String getResponseFromHttpUrl(URL url) throws IOException {

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
            Log.d("NetworkManager", "Error: " + e.toString());
            return null;
        } finally {
            urlConnection.disconnect();
        }
    }

    public static byte[] getImageFromHttpUrl(String url) throws IOException {
        try {
            URL imageUrl = new URL(url);
            Log.d("URL", url);

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
            Log.d("ImageManager", "Error: " + e.toString());
        }
        return null;
    }
}
