package br.com.secompufscar.secomp_ufscar.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import br.com.secompufscar.secomp_ufscar.utilities.NetworkUtils;

public class Pessoa {
    /**
     * Nome das propriedades fornecidas pelo sistema do site.
     * Esses atributos estáticos são utilizados para capturar cada campo do JSON que é envidado
     * e para dar nome as colunas da tabela Pessoa no banco de dados do aplicativo.
     **/
    public final static String TAG_ID = "id_pessoa";// Chave primária do banco provedor
    public final static String TAG_NOME = "nome_pessoa";
    public final static String TAG_PROFISSAO = "profissao_pessoa";
    public final static String TAG_EMPRESA = "empresa_pessoa";
    public final static String TAG_DESCRICAO = "desccricao_pessoa";
    public final static String TAG_FOTO = "foto_pessoa";
    public final static String TAG_FACEBOOK = "facebook_pessoa";
    public final static String TAG_TWITTER = "twitter_pessoa";
    public final static String TAG_GITHUB = "github_pessoa";
    public final static String TAG_LINKEDIN = "linkedin_pessoa";

    /** Atributos dos objetos pessoas **/
    private int id;
    private String nome, profissao, empresa, descricao;
    private byte[] foto;
    private String facebook, twitter, github, linkedin;

    /** Métodos GET **/

    public int getId() {
        return this.id;
    }

    public String getNome() {
        return this.nome;
    }

    public String getProfissao() {
        return this.profissao;
    }

    public String getEmpresa() {
        return empresa;
    }

    public String getDescricao(){
        return this.descricao;
    }

    public byte[] getFoto() {
        return this.foto;
    }

    public Bitmap getFotoBitmap(){

        return BitmapFactory.decodeByteArray(this.foto, 0, this.foto.length);
    }

    public String getFacebook() {
        return this.facebook;
    }

    public String getTwitter() {
        return this.twitter;
    }

    public String getGithub() {
        return this.github;
    }

    public String getLinkedin(){
        return this.linkedin;
    }

    /** Métodos SET **/

    public void setId(int id) {
        this.id = id;
    }

    public void setNome(String nome){
        this.nome = nome;
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

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public static ArrayList<Pessoa> AtividadeParseJSON(String json) {
        if (json != null) {
            try {
                // Lista de pessoas
                ArrayList<Pessoa> pessoaList = new ArrayList<Pessoa>();

                // Inicializaçao de um objeto json para realizaçao do parsing
                JSONObject jsonObj = new JSONObject(json);

                // TODO: Ajustar a etapa de aquisição dos dados
                // Getting JSON Array node
                JSONArray pessoas = jsonObj.getJSONArray("pessoas"); // Verificar o atributo daqui
                // Loop em para pegar cada pessoa
                for (int i = 0; i < pessoas.length(); i++) {
                    Pessoa pessoa = new Pessoa();
                    JSONObject pessoaObject = pessoas.getJSONObject(i);

                    pessoa.id = Integer.valueOf(pessoaObject.getString(TAG_ID));
                    pessoa.nome = pessoaObject.getString(TAG_NOME);
                    pessoa.profissao = pessoaObject.getString(TAG_PROFISSAO);
                    pessoa.empresa = pessoaObject.getString(TAG_EMPRESA);
                    pessoa.descricao = pessoaObject.getString(TAG_DESCRICAO);
                    //pessoa.foto = NetworkUtils.getImageFromHttpUrl(pessoaObject.getString(TAG_FOTO));
                    pessoa.facebook = pessoaObject.getString(TAG_FACEBOOK);
                    pessoa.twitter = pessoaObject.getString(TAG_TWITTER);
                    pessoa.github = pessoaObject.getString(TAG_GITHUB);
                    pessoa.linkedin = pessoaObject.getString(TAG_LINKEDIN);

                    // Adicionando a pessoa na lista de pessoas
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
}
