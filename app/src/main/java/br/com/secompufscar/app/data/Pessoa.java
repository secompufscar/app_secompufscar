package br.com.secompufscar.app.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import br.com.secompufscar.app.R;
import br.com.secompufscar.app.utilities.NetworkUtils;

public class Pessoa {
    /**
     * Nome das propriedades fornecidas pelo sistema do site.
     * Esses atributos estáticos são utilizados para capturar cada campo do JSON que é envidado
     * e para dar nome as colunas da tabela Pessoa no banco de dados do aplicativo.
     **/

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

    public final static String TAG_ULTIMA_ATUALIZACAO = "ultima_atualizacao";
    public final static String API_URL = "2018/api/ministrantes/";


    public class Contato {
        private String tipo, link;

        @Override
        public String toString() {
            if (this.tipo != null)
                return "[" + this.tipo + "] " + this.link;
            else
                return "";
        }

        public String getTipo() {
            return this.tipo;
        }

        public String getLink() {
            return this.link;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        public void setLink(String link) {
            this.link = link;
        }
    }

    /**
     * Atributos dos objetos pessoas
     **/
    private int id;
    private String nome, sobrenome, descricao, empresa, profissao;
    private byte[] foto;
    private String contatosJSON;
    private String horarioUltimaAtualizacao;

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

    public Bitmap getFotoBitmap(Context context) {

        Bitmap image;

        if (this.foto != null) {
            image = BitmapFactory.decodeByteArray(this.foto, 0, this.foto.length);
        } else {
            image = BitmapFactory.decodeResource(context.getResources(), R.drawable.pessoa_foto_default);
        }

        Bitmap imageRounded = Bitmap.createBitmap(image.getWidth(), image.getHeight(), image.getConfig());
        Canvas canvas = new Canvas(imageRounded);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(image, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawRoundRect((new RectF(0, 0, image.getWidth(), image.getHeight())), image.getWidth() / 2, image.getWidth() / 2, paint);// Round Image Corner 100 100 100 100

        return imageRounded;
    }

    public String getHorarioUltimaAtualizacao() {
        return this.horarioUltimaAtualizacao;
    }

    public String getContatosRaw() {
        return this.contatosJSON;
    }

    public ArrayList<Contato> getContatos() {
        ArrayList<Contato> contatos = new ArrayList<>();
        if (this.contatosJSON != null) {
            try {
                JSONArray contatosArray = new JSONArray(this.contatosJSON);

                for (int j = 0; j < contatosArray.length(); j++) {
                    Contato contato = new Contato();
                    JSONObject contatoObject = contatosArray.getJSONObject(j);
                    contato.setTipo(contatoObject.getString(TAG_CONTATO_TIPO));
                    contato.setLink(contatoObject.getString(TAG_CONTATO_LINK));
                    contatos.add(contato);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return contatos;
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

    public void setHorarioUltimaAtualizacao(String horario) {
        this.horarioUltimaAtualizacao = horario;
    }

//    public static ArrayList<Pessoa> PessoaResumoParseJSON(JSONArray pessoas, Context context) {
//        try {
//            ArrayList<Pessoa> pessoaList = new ArrayList<>();
//
//            for (int i = 0; i < pessoas.length(); i++) {
//                Pessoa pessoa = new Pessoa();
//                JSONObject pessoaObject = pessoas.getJSONObject(i);
//
//                pessoa.setId(Integer.valueOf(pessoaObject.getString(TAG_ID)));
//                pessoa.setNome(pessoaObject.getString(TAG_NOME));
//                pessoa.setSobrenome(pessoaObject.getString(TAG_SOBRENOME));
//                pessoa.setProfissao(pessoaObject.getString(TAG_PROFISSAO));
//                pessoa.setEmpresa(pessoaObject.getString(TAG_EMPRESA));
//                pessoa.setHorarioUltimaAtualizacao(pessoaObject.getString(TAG_ULTIMA_ATUALIZACAO));
//
//                try {
//                    pessoa.setFoto(NetworkUtils.getImageFromHttpUrl(pessoaObject.getString(TAG_FOTO), context));
//
//                } catch (Exception IOException) {
//                    pessoa.setFoto(null);
//                }
//                pessoaList.add(pessoa);
//            }
//            return pessoaList;
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    public static Pessoa resumoPessoaParseJSON(String json, Context context) {
        try {
            JSONObject pessoaObject = new JSONObject(json);

            Pessoa pessoa = new Pessoa();

            pessoa.setId(Integer.valueOf(pessoaObject.getString(TAG_ID)));
            pessoa.setNome(pessoaObject.getString(TAG_NOME));
            pessoa.setSobrenome(pessoaObject.getString(TAG_SOBRENOME));
            pessoa.setProfissao(pessoaObject.getString(TAG_PROFISSAO));
            pessoa.setEmpresa(pessoaObject.getString(TAG_EMPRESA));

            try {
                pessoa.setFoto(NetworkUtils.getImageFromHttpUrl(pessoaObject.getString(TAG_FOTO), context));
            } catch (Exception IOException) {
                pessoa.setFoto(null);
            }
            return pessoa;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Pessoa detalhePessoaParseJSON(String json, Context context) {
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
            pessoa.setHorarioUltimaAtualizacao(pessoaObject.getString(TAG_ULTIMA_ATUALIZACAO));

            try {
                pessoa.setFoto(NetworkUtils.getImageFromHttpUrl(pessoaObject.getString(TAG_FOTO), context));
            } catch (Exception IOException) {
                pessoa.setFoto(null);
            }

            return pessoa;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static Pessoa getDetalhePessoaFromHTTP(Pessoa pessoa_antiga, Context context) {
        URL url = NetworkUtils.buildUrl(API_URL + Integer.toString(pessoa_antiga.getId()));
        String response;
        try {
            response = NetworkUtils.getResponseFromHttpUrl(url, context);
            if (response != null) {
                try {
                    JSONObject horarioObject = new JSONObject(response);
                    String dataUtimaAtualizacao = horarioObject.getString(TAG_ULTIMA_ATUALIZACAO);

                    if (pessoa_antiga.getHorarioUltimaAtualizacao() == null || !pessoa_antiga.getHorarioUltimaAtualizacao().equals(dataUtimaAtualizacao)) {
                        Pessoa pessoa = detalhePessoaParseJSON(response, context);
                        DatabaseHandler.getDB().updatePessoa(pessoa);

                        return pessoa;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
