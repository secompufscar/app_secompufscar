package br.com.secompufscar.secomp_ufscar.data;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import br.com.secompufscar.secomp_ufscar.utilities.NetworkUtils;

public class Patrocinador {
    /**
     * Nome das propriedades fornecidas pelo sistema do site.
     * Esses atributos estáticos são utilizados para capturar cada campo do JSON que é envidado.
     **/

    public final static String TAG_COTAS = "cotas";
    public final static String TAG_DOMINIO_IMAGEM = "dominio_imagens";

    public final static String TAG_PATROCINADORES = "patrocinadores";
    public final static String TAG_ID = "id";
    public final static String TAG_LOGO = "url_logo";
    public final static String TAG_NOME = "nome";
    public final static String TAG_WEBSITE = "link_website";

    public final static String TAG_ORDEM = "ordem";
    public final static String TAG_COTA = "cota";

    public final static String API_URL = "api/patrocinadores/";

    /**
     * Atributos dos objetos patrocinador
     **/
    private int id, ordem;
    private String nome, website, cota;
    private byte[] logo;

    /**
     * Métodos GET
     **/

    @Override
    public String toString() {
        return nome + " Cota: " + cota;
    }

    public int getId() {
        return this.id;
    }

    public int getOrdem() {
        return this.ordem;
    }

    public String getNome() {
        return this.nome;
    }


    public String getWebsite() {
        return this.website;
    }

    public String getCota() {
        return this.cota;
    }

    public byte[] getLogo() {
        return this.logo;
    }

    public Bitmap getLogoBitmap() {

        Bitmap image = BitmapFactory.decodeByteArray(this.logo, 0, this.logo.length);
        return image;
    }

    /**
     * Métodos SET
     **/

    public void setId(int id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

    public void setWebsite(String link) {
        this.website = link;
    }

    public void setCota(String cota) {
        this.cota = cota;
    }

    public static ArrayList<Patrocinador> patrocinadoresParseJSON(String json) {
        if (json != null) {
            try {
                // Lista de patrocinadores
                ArrayList<Patrocinador> patrocinadorList = new ArrayList<>();
                // Lista de cotas
//                TODO: Armazenar as cotas enviadas
//                ArrayList<String> cotaList = new ArrayList<>();

                // Inicializaçao do objeto json para realizaçao do parsing
                JSONObject jsonObj = new JSONObject(json);

                JSONArray cotas = jsonObj.getJSONArray(TAG_COTAS);
                JSONObject patrocinadoresObject = jsonObj.getJSONObject(TAG_PATROCINADORES);
                String root_image = jsonObj.getString(TAG_DOMINIO_IMAGEM);

                String cota;
                // Loop em para pegar cada cota
                for (int i = 0; i < cotas.length(); i++) {
                    cota = cotas.getString(i);
                    JSONArray cota_patrocinador = patrocinadoresObject.getJSONArray(cota);

                    for (int j = 0; j < cota_patrocinador.length(); j++) {
                        Patrocinador patrocinador = new Patrocinador();
                        JSONObject patrocinadorObject = cota_patrocinador.getJSONObject(j);

                        patrocinador.setId(Integer.valueOf(patrocinadorObject.getString(TAG_ID)));
                        patrocinador.setOrdem(j + 1);
                        patrocinador.setNome(patrocinadorObject.getString(TAG_NOME));
                        patrocinador.setWebsite(patrocinadorObject.getString(TAG_WEBSITE));
                        patrocinador.setCota(cota);

                        try {
                            patrocinador.setLogo(NetworkUtils.getImageFromHttpUrl("https://" + root_image + patrocinadorObject.getString(TAG_LOGO)));
                        } catch (Exception IOException) {
                            patrocinador.setLogo(null);
                        }

                        patrocinadorList.add(patrocinador);
                    }
                }

                return patrocinadorList;

            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            Log.e("ServiceHandler", "No data received from HTTP request");
            return null;
        }
    }

    public static void getPatrocinadoresFromHTTP() {
        URL url = NetworkUtils.buildUrl(API_URL);
        String response;

        try {
            response = NetworkUtils.getResponseFromHttpUrl(url);
            if (response != null) {
                DatabaseHandler.getDB().addManyPatrocinadores(patrocinadoresParseJSON(response));
//                Log.d("Teste", patrocinadoresParseJSON(response).toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
