package br.com.secompufscar.secomp_ufscar.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import br.com.secompufscar.secomp_ufscar.utilities.NetworkUtils;

public class Atividade {

    public final static String TAG_ATIVIDADES = "atividades";
    public final static String TAG_TIPOS_ATIVIDADE = "tipos_atividades";
    public final static String TAG_DOMINIO_IMAGEM = "dominio_imagens";

    public final static String TAG_ID = "id";
    public final static String TAG_TITULO = "titulo";
    public final static String TAG_MINISTRANTES = "ministrantes";
    public final static String TAG_DESCRICAO = "descricao";
    public final static String TAG_TIPO = "tipo";
    public final static String TAG_LOCAL = "local";
    public final static String TAG_FAVORITO = "favorito";

    public final static String TAG_HORARIOS = "horarios";
    public final static String TAG_DATAHORA_INICIO = "data_hora_inicio";
    public final static String TAG_DATAHORA_FIM = "data_hora_fim";


    public final static String API_URL = "api/atividades/";
    public final static String RESUMO_URL = API_URL + "?ministrantes_resumo=True/";

    class Horario {
        public Date dataHora_inicio, dataHora_fim;

        public Date dataHoraParser(String dateString) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date value = null;
            try {
                value = formatter.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return value;
        }

        public String dataHoraInCurrentTimeZone(Date dateObject) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy hh:mmaa");
            // TODO: Verificar se essa é a forma padrão de pegar a timezone do android
            dateFormatter.setTimeZone(TimeZone.getDefault());
            String datahora = dateFormatter.format(dateObject);

            return datahora;
        }

        public String getDataHoraInicio() {
            return dataHoraInCurrentTimeZone(this.dataHora_inicio);
        }

        public String getDataHoraFim() {
            return dataHoraInCurrentTimeZone(this.dataHora_fim);
        }

        public void setDataHora_inicio(String dataHora_inicio) {
            this.dataHora_inicio = dataHoraParser(dataHora_inicio);
        }

        public void setDataHora_Fim(String dataHora_fim) {
            this.dataHora_fim = dataHoraParser(dataHora_fim);
        }
    }

    private int id;
    private String titulo, local, tipo, descricao;
    private String horarios;
    private String dataHora_inicio;
    private ArrayList<Pessoa> ministrantes;
    private boolean favorito;

    public Atividade() {
        this.favorito = false;
    }

    @Override
    public String toString() {
        return this.titulo + " Tipo:" + this.tipo;
    }


    /**
     * Métodos set
     **/

    public void setId(int id) {
        this.id = id;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setHorarios(String horarios) {
        this.horarios = horarios;

        if (!this.horarios.isEmpty()) {
            try {
                JSONArray horariosArray = new JSONArray(this.horarios);
                JSONObject horarioObject = horariosArray.getJSONObject(0);
                this.dataHora_inicio = horarioObject.getString(TAG_DATAHORA_INICIO);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public void setMinistrantes(JSONArray ministrantes) {
        try {
            this.ministrantes = new ArrayList<>();

            for (int i = 0; i < ministrantes.length(); i++) {
                Pessoa pessoa = Pessoa.resumoPessoaParseJSON(ministrantes.getString(i));

                this.ministrantes.add(pessoa);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setFavorito(boolean isFavorito) {
        this.favorito = isFavorito;
    }

    /**
     * Métodos get
     **/

    public int getId() {
        return this.id;
    }

    public String getTitulo() {
        return this.titulo;
    }

    public String getLocal() {
        return this.local;
    }

    public String getTipo() {
        return this.tipo;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public String getHorariosString() {
        return this.horarios;
    }

    public String getDataHoraInicio(){
        return this.dataHora_inicio;
    }

    public ArrayList<Horario> getHorarios() {
        if (!this.horarios.isEmpty()) {
            ArrayList<Horario> horarios = new ArrayList<>();
            try {
                JSONArray horariosArray = new JSONArray(this.horarios);
                for (int j = 0; j < horariosArray.length(); j++) {
                    Horario horario = new Horario();
                    JSONObject horarioObject = horariosArray.getJSONObject(j);
                    horario.setDataHora_inicio(horarioObject.getString(TAG_DATAHORA_INICIO));
                    horario.setDataHora_Fim(horarioObject.getString(TAG_DATAHORA_FIM));

                    horarios.add(horario);
                }
                return horarios;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    public boolean isFavorito() {
        return this.favorito;
    }

    public static ArrayList<Atividade> atividadesParseJSON(String json) {
        if (json != null) {
            try {
                // Lista de patrocinadores
                ArrayList<Atividade> atividadeList = new ArrayList<>();
                // Lista de tipos de atividade
//                ArrayList<String> tipoList = new ArrayList<>();
                JSONObject jsonObj = new JSONObject(json);

                JSONArray tipos = jsonObj.getJSONArray(TAG_TIPOS_ATIVIDADE);
                JSONObject atividadesObject = jsonObj.getJSONObject(TAG_ATIVIDADES);
                String root_image = jsonObj.getString(TAG_DOMINIO_IMAGEM);

                String tipo;
                for (int i = 0; i < tipos.length(); i++) {
                    tipo = tipos.getString(i);
                    JSONArray tipo_atividade = atividadesObject.getJSONArray(tipo);

                    for (int j = 0; j < tipo_atividade.length(); j++) {
                        Atividade atividade = new Atividade();

                        JSONObject atividadeObject = tipo_atividade.getJSONObject(j);

                        atividade.setId(atividadeObject.getInt(TAG_ID));
                        atividade.setTitulo(atividadeObject.getString(TAG_TITULO));
                        atividade.setTipo(tipo);
                        atividade.setHorarios(atividadeObject.getString(TAG_HORARIOS));
                        atividade.setMinistrantes(atividadeObject.getJSONArray(TAG_MINISTRANTES));

                        atividadeList.add(atividade);
                    }
                }

                return atividadeList;

            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            //TODO: tratar esse problema no app
            Log.e("ServiceHandler", "No data received from HTTP request");
            return null;
        }
    }

    public static Atividade detalheAtividadeParseJSON(String json) {
        if (json != null) {
            try {
                JSONObject atividadeObject = new JSONObject(json);

                Atividade atividade = new Atividade();

                atividade.setDescricao(atividadeObject.getString(TAG_DESCRICAO));
                atividade.setLocal(atividadeObject.getString(TAG_LOCAL));

                return atividade;

            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            //TODO: tratar esse problema no app
            Log.e("ServiceHandler", "No data received from HTTP request");
            return null;
        }
    }

    public static void getAtividadesFromHTTP() {
        URL url = NetworkUtils.buildUrl(RESUMO_URL);
        String response;

        try {
            response = NetworkUtils.getResponseFromHttpUrl(url);
            if (response != null) {
                DatabaseHandler.getDB().addManyAtividades(atividadesParseJSON(response));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Atividade getDetalheAtividadeFromHTTP(int id) {
        URL url = NetworkUtils.buildUrl(API_URL + Integer.toString(id));
        String response;
        try {
            response = NetworkUtils.getResponseFromHttpUrl(url);
            if (response != null) {
                Atividade atividade = detalheAtividadeParseJSON(response);
//                DatabaseHandler.getDB().addAllAtividades(Atividade.AtividadeParseJSON(response));
                return atividade;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
