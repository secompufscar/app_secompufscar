package br.com.secompufscar.secomp_ufscar.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import br.com.secompufscar.secomp_ufscar.R;
import br.com.secompufscar.secomp_ufscar.utilities.NetworkUtils;

public class Pessoa {
    /**
     * Nome das propriedades fornecidas pelo sistema do site.
     * Esses atributos estáticos são utilizados para capturar cada campo do JSON que é envidado
     * e para dar nome as colunas da tabela Pessoa no banco de dados do aplicativo.
     **/
    public final static String TAG_MINISTRANTES = "ministrantes";
    public final static String TAG_DOMINIO_IMAGEM = "dominio_imagens";

    public final static String TAG_ID = "id";
    public final static String TAG_NOME = "nome";
    public final static String TAG_SOBRENOME = "sobrenome";
    public final static String TAG_DESCRICAO = "descricao";
    public final static String TAG_EMPRESA = "empresa";
    public final static String TAG_PROFISSAO = "profissao";
    public final static String TAG_FOTO = "link_foto";
    public final static String TAG_CONTATOS = "contatos";
    public final static String TAG_CONTATO_LINK = "link";
    public final static String TAG_CONTATO_TIPO = "tipo_contato";

    public final static String API_URL = "api/ministrantes/";


    public class Contato {
        String tipo, link;
    }

    /**
     * Atributos dos objetos pessoas
     **/
    private int id;
    private String nome, sobrenome, descricao, empresa, profissao;
    private byte[] foto;
    private String contatosJSON;

    @Override
    public String toString() {
        return getNomeCompleto();
    }

    /**
     * Métodos GET
     **/

    public int getId() {
        return this.id;
    }

    public String getNome() {
        return this.nome;
    }

    public String getSobrenome() {
        return this.sobrenome;
    }

    public String getNomeCompleto() {
        return this.nome + " " + this.sobrenome;
    }

    public String getProfissao() {
        return this.profissao;
    }

    public String getEmpresa() {
        return empresa;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public byte[] getFoto() {
        return this.foto;
    }

    public Bitmap getFotoBitmap() {

        Bitmap image = BitmapFactory.decodeByteArray(this.foto, 0, this.foto.length);
        //TODO: Verificar se o formato da imagem está correto;
        Bitmap imageRounded = Bitmap.createBitmap(image.getWidth(), image.getHeight(), image.getConfig());
        Canvas canvas = new Canvas(imageRounded);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(image, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawRoundRect((new RectF(0, 0, image.getWidth(), image.getHeight())), 25, 25, paint);// Round Image Corner 100 100 100 100

        return imageRounded;
    }

    public String getContatosString(){
        return this.contatosJSON;
    }

    public ArrayList<Contato> getContatos() {
        if (!this.contatosJSON.isEmpty()) {
            ArrayList<Contato> contatos = new ArrayList<>();

            try {
                JSONArray contatosArray = new JSONArray(this.contatosJSON);

                for (int j = 0; j < contatosArray.length(); j++) {
                    Contato contato = new Contato();
                    JSONObject contatoObject = contatosArray.getJSONObject(j);
                    contato.tipo = contatoObject.getString(TAG_CONTATO_TIPO);
                    contato.link = contatoObject.getString(TAG_CONTATO_LINK);
                    contatos.add(contato);
                }

                return contatos;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
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

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public void setProfissao(String profissao) {
        this.profissao = profissao;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    public void setContatos(String contatosJSON) {
        this.contatosJSON = contatosJSON;
    }

    public static ArrayList<Pessoa> PessoaResumoParseJSON(JSONArray pessoas) {
        try {
            ArrayList<Pessoa> pessoaList = new ArrayList<>();

            for (int i = 0; i < pessoas.length(); i++) {
                Pessoa pessoa = new Pessoa();
                JSONObject pessoaObject = pessoas.getJSONObject(i);

                pessoa.setId(Integer.valueOf(pessoaObject.getString(TAG_ID)));
                pessoa.setNome(pessoaObject.getString(TAG_NOME));
                pessoa.setSobrenome(pessoaObject.getString(TAG_SOBRENOME));
                pessoa.setProfissao(pessoaObject.getString(TAG_PROFISSAO));
                pessoa.setEmpresa(pessoaObject.getString(TAG_EMPRESA));

                try {
                    pessoa.setFoto(NetworkUtils.getImageFromHttpUrl(pessoaObject.getString(TAG_FOTO)));

                } catch (Exception IOException) {
                    pessoa.setFoto(null);
                }
                pessoaList.add(pessoa);
            }
            return pessoaList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Pessoa resumoPessoaParseJSON(String json) {
        try {
            JSONObject pessoaObject = new JSONObject(json);

            Pessoa pessoa = new Pessoa();

            pessoa.setId(Integer.valueOf(pessoaObject.getString(TAG_ID)));
            pessoa.setNome(pessoaObject.getString(TAG_NOME));
            pessoa.setSobrenome(pessoaObject.getString(TAG_SOBRENOME));
            pessoa.setProfissao(pessoaObject.getString(TAG_PROFISSAO));
            pessoa.setEmpresa(pessoaObject.getString(TAG_EMPRESA));
            try {
                pessoa.setFoto(NetworkUtils.getImageFromHttpUrl(NetworkUtils.BASE_URL + pessoaObject.getString(TAG_FOTO)));

            } catch (Exception IOException) {
                pessoa.setFoto(null);
            }

            return pessoa;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Pessoa detalhePessoaParseJSON(String json) {
        try {
            JSONObject pessoaObject = new JSONObject(json);

            Pessoa pessoa = new Pessoa();

            pessoa.setId(Integer.valueOf(pessoaObject.getString(TAG_ID)));
            pessoa.setNome(pessoaObject.getString(TAG_NOME));
            pessoa.setSobrenome(pessoaObject.getString(TAG_SOBRENOME));
            pessoa.setDescricao(pessoaObject.getString(TAG_DESCRICAO));
            pessoa.setProfissao(pessoaObject.getString(TAG_PROFISSAO));
            pessoa.setEmpresa(pessoaObject.getString(TAG_EMPRESA));
            pessoa.setContatos(pessoaObject.getString(TAG_CONTATOS));

            try {
                pessoa.setFoto(NetworkUtils.getImageFromHttpUrl(NetworkUtils.BASE_URL + pessoaObject.getString(TAG_FOTO)));

            } catch (Exception IOException) {
                pessoa.setFoto(null);
            }

            return pessoa;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<Pessoa> pessoasParseJSON(String json) {
        if (json != null) {
            try {
                // Lista de pessoas
                ArrayList<Pessoa> pessoaList = new ArrayList<>();

                // Inicializaçao de um objeto json para realizaçao do parsing
                JSONObject jsonObj = new JSONObject(json);

                JSONArray pessoas = jsonObj.getJSONArray(TAG_MINISTRANTES);

                // Loop em para pegar cada pessoa
                for (int i = 0; i < pessoas.length(); i++) {
                    Pessoa pessoa = new Pessoa();
                    JSONObject pessoaObject = pessoas.getJSONObject(i);

                    pessoa.setId(Integer.valueOf(pessoaObject.getString(TAG_ID)));
                    pessoa.setNome(pessoaObject.getString(TAG_NOME));
                    pessoa.setSobrenome(pessoaObject.getString(TAG_SOBRENOME));
                    pessoa.setDescricao(pessoaObject.getString(TAG_DESCRICAO));
                    pessoa.setProfissao(pessoaObject.getString(TAG_PROFISSAO));
                    pessoa.setEmpresa(pessoaObject.getString(TAG_EMPRESA));
                    pessoa.setContatos(pessoaObject.getString(TAG_CONTATOS));

                    try {
                        pessoa.setFoto(NetworkUtils.getImageFromHttpUrl(NetworkUtils.BASE_URL + pessoaObject.getString(TAG_FOTO)));

                    } catch (Exception IOException) {
                        pessoa.setFoto(null);
                    }

                    pessoaList.add(pessoa);
                }
                return pessoaList;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            Log.e("ServiceHandler", "No data received from HTTP request");
            return null;
        }
    }

    public static Pessoa getDetalhePessoaFromHTTP(int id) {
        URL url = NetworkUtils.buildUrl(API_URL + Integer.toString(id));
        String response;
        try {
            response = NetworkUtils.getResponseFromHttpUrl(url);
            if (response != null) {
                Pessoa pessoa = detalhePessoaParseJSON(response);
//                DatabaseHandler.getDB().addAllAtividades(Atividade.AtividadeParseJSON(response));
                return pessoa;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
