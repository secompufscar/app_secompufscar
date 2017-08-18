package br.com.secompufscar.secomp_ufscar.data;

import android.content.Context;
import android.support.v4.content.ContextCompat;
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
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import br.com.secompufscar.secomp_ufscar.R;
import br.com.secompufscar.secomp_ufscar.utilities.NetworkUtils;

public class Atividade {

    public final static String TAG_ATIVIDADES = "atividades";
    public final static String TAG_TIPOS_ATIVIDADE = "tipos_atividades";

    public final static String TAG_ID = "id";
    public final static String TAG_TITULO = "titulo";
    public final static String TAG_MINISTRANTES = "ministrantes";
    public final static String TAG_DESCRICAO = "descricao";
    public final static String TAG_TIPO = "tipo";
    public final static String TAG_LOCAL = "local";
    public final static String TAG_SALA = "sala";
    public final static String TAG_PREDIO = "predio";
    public final static String TAG_FAVORITO = "favorito";

    public final static String TAG_HORARIOS = "horarios";
    public final static String TAG_DATAHORA_INICIO = "data_hora_inicio";
    public final static String TAG_DATAHORA_FIM = "data_hora_fim";


    public final static String TAG_ULTIMA_ATUALIZACAO = "ultima_atualizacao";
    public final static String API_URL = "api/atividades/";
    public final static String RESUMO_URL = API_URL + "?ministrantes_resumo=True";

    class Horario {
        public Date dataHora_inicio, dataHora_fim;

        @Override
        public String toString() {
            return dateInCurrentTimeZone(this.dataHora_inicio, "EE").toUpperCase() + " " + getHoras();
        }

        public String getHoras() {
            String horarioFim = (this.dataHora_fim != null) ? " - " + dateInCurrentTimeZone(this.dataHora_fim, "HH:mm") : "";
            return dateInCurrentTimeZone(this.dataHora_inicio, "HH:mm") + horarioFim;
        }

        public Date dataHoraParser(String dateString) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", new Locale("pt", "BR"));
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date value = null;
            try {
                value = formatter.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return value;
        }

        /**
         * String format: formato utilizado para formatar a saída (veja documentação do SimpleDateFormat)
         * Exemplos: "dd/MM/yyyy HH:mm" (Dia/Mes/Ano Horas:minutos), "EE" (dia da semana em formato reduzido)
         **/

        public String dateInCurrentTimeZone(Date dateObject, String format) {

            SimpleDateFormat dateFormatter = new SimpleDateFormat(format, new Locale("pt", "BR"));
            dateFormatter.setTimeZone(TimeZone.getDefault());
            String datahora = dateFormatter.format(dateObject);

            return datahora;
        }

        public String getDataHoraInicio() {
            return dateInCurrentTimeZone(this.dataHora_inicio, "dd/MM/yyyy HH:mm");
        }

        public String getDataHoraFim() {
            return dateInCurrentTimeZone(this.dataHora_fim, "dd/MM/yyyy HH:mm");
        }

        public void setDataHora_inicio(String dataHora_inicio) {
            this.dataHora_inicio = dataHoraParser(dataHora_inicio);
        }

        public void setDataHora_Fim(String dataHora_fim) {
            this.dataHora_fim = dataHoraParser(dataHora_fim);
        }
    }

    private int id;
    private String titulo, tipo, descricao;
    private String predio, sala;
    private String horarios;
    private String dataHora_inicio;
    private String horarioUltimaAtualizacao;
    private ArrayList<Pessoa> ministrantes;
    private boolean favorito;

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

    public void setLocal(JSONObject local) {
        try {
            this.predio = local.getString(TAG_PREDIO);
            this.sala = local.getString(TAG_SALA);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setLocal(String predio, String sala) {
        this.predio = predio;
        this.sala = sala;
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

    public void setHorarioUltimaAtualizacao(String horario) {
        this.horarioUltimaAtualizacao = horario;
    }

    public void setMinistrantes(JSONArray ministrantes, Context context) {
        try {
            this.ministrantes = new ArrayList<>();

            for (int i = 0; i < ministrantes.length(); i++) {
                Pessoa pessoa = Pessoa.resumoPessoaParseJSON(ministrantes.getString(i), context);
                this.ministrantes.add(pessoa);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setMinistrantes(List<Pessoa> ministrantes){
        this.ministrantes = new ArrayList<>();
        this.ministrantes.addAll(ministrantes);
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

    public String getPredio() {
        return this.predio;
    }

    public String getSala() {
        return this.sala;
    }

    public String getLocal() {
        String local = this.predio;
        if (this.sala != null && !this.sala.equals("null"))
            local += ", sala " + this.sala;

        return local;
    }

    public String getTipo() {
        return this.tipo;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public String getHorariosRaw() {
        return this.horarios;
    }

    public String getDataHoraInicio() {
        return this.dataHora_inicio;
    }

    public String getHorarioInicial() {
        Horario horario_inicio = new Horario();
        horario_inicio.setDataHora_inicio(this.dataHora_inicio);

        return horario_inicio.dateInCurrentTimeZone(horario_inicio.dataHora_inicio, "HH:mm");
    }

    public String getHorarioInicialDiaDaSemana() {
        Horario horario_inicio = new Horario();
        horario_inicio.setDataHora_inicio(this.dataHora_inicio);

        return horario_inicio.dateInCurrentTimeZone(horario_inicio.dataHora_inicio, "EE HH:mm").toUpperCase();
    }

    public String getHorarios() {
        String horarios_string = "";
        if (!this.horarios.isEmpty()) {

            try {
                JSONArray horariosArray = new JSONArray(this.horarios);

                for (int j = 0; j < horariosArray.length(); j++) {
                    Horario horario = new Horario();
                    JSONObject horarioObject = horariosArray.getJSONObject(j);
                    horario.setDataHora_inicio(horarioObject.getString(TAG_DATAHORA_INICIO));
                    horario.setDataHora_Fim(horarioObject.getString(TAG_DATAHORA_FIM));

                    if (j == 0) {
                        horarios_string = horario.toString();
                    } else if (j < horariosArray.length() - 1) {
                        horarios_string += ", " + horario.getHoras();
                    } else {
                        horarios_string += " e " + horario.getHoras();
                    }
                }
                return horarios_string;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    public String getHorarioUltimaAtualizacao() {
        return this.horarioUltimaAtualizacao;
    }

    public int getColor(Context context) {
        int color;
        switch (this.tipo.toLowerCase()) {
            case "palestra":
                color = ContextCompat.getColor(context, R.color.palestraColor);
                break;
            case "minicurso":
                color = ContextCompat.getColor(context, R.color.minicursoColor);
                break;
            default:
                color = ContextCompat.getColor(context, R.color.atividadeDefaultColor);
        }

        return color;
    }

    public List<Pessoa> getMinistrantes() {
        return this.ministrantes;
    }

    public Boolean isFavorito() {
        return this.favorito;
    }

    public static ArrayList<Atividade> atividadesParseJSON(String json, Context context) {
        if (json != null) {
            try {
                // Lista de patrocinadores
                ArrayList<Atividade> atividadeList = new ArrayList<>();
                // Lista de tipos de atividade
//                ArrayList<String> tipoList = new ArrayList<>();
                JSONObject jsonObj = new JSONObject(json);

                JSONArray tipos = jsonObj.getJSONArray(TAG_TIPOS_ATIVIDADE);
                JSONObject atividadesObject = jsonObj.getJSONObject(TAG_ATIVIDADES);

                String tipo;
                for (int i = 0; i < tipos.length(); i++) {
                    tipo = tipos.getString(i);
                    JSONArray tipo_atividade = atividadesObject.getJSONArray(tipo);

                    for (int j = 0; j < tipo_atividade.length(); j++) {
                        Atividade atividade = new Atividade();

                        JSONObject atividadeObject = tipo_atividade.getJSONObject(j);

                        atividade.setId(atividadeObject.getInt(TAG_ID));
                        atividade.setTitulo(atividadeObject.getString(TAG_TITULO));
                        atividade.setDescricao(atividadeObject.getString(TAG_DESCRICAO));
                        atividade.setTipo(tipo);
                        atividade.setLocal(atividadeObject.getJSONObject(TAG_LOCAL));
                        atividade.setHorarios(atividadeObject.getString(TAG_HORARIOS));
                        atividade.setMinistrantes(atividadeObject.getJSONArray(TAG_MINISTRANTES), context);

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

    public static Atividade detalheAtividadeParseJSON(String json, Context context) {
        if (json != null) {
            try {
                JSONObject atividadeObject = new JSONObject(json);

                Atividade atividade = new Atividade();

                atividade.setId(atividadeObject.getInt(TAG_ID));
                atividade.setTitulo(atividadeObject.getString(TAG_TITULO));
                atividade.setDescricao(atividadeObject.getString(TAG_DESCRICAO));
                atividade.setLocal(atividadeObject.getJSONObject(TAG_LOCAL));
                atividade.setTipo(atividadeObject.getString(TAG_TIPO));
                atividade.setHorarios(atividadeObject.getString(TAG_HORARIOS));
                atividade.setHorarioUltimaAtualizacao(atividadeObject.getString(TAG_ULTIMA_ATUALIZACAO));


                atividade.setMinistrantes(atividadeObject.getJSONArray(TAG_MINISTRANTES), context);

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

    public static boolean getAtividadesFromHTTP(Context context) {
        URL url = NetworkUtils.buildUrl(RESUMO_URL);
        String response;

        try {
            response = NetworkUtils.getResponseFromHttpUrl(url, context);
            if (response != null) {
                try {
                    JSONObject jsonObj = new JSONObject(response);
                    if (NetworkUtils.checkUpdate(context, jsonObj.getString(TAG_ULTIMA_ATUALIZACAO), "atividades")) {
                        DatabaseHandler.getDB().addManyAtividades(atividadesParseJSON(response, context));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static Atividade getDetalheAtividadeFromHTTP(Atividade atividade_antiga, Context context) {
        URL url = NetworkUtils.buildUrl(API_URL + Integer.toString(atividade_antiga.getId()));
        String response;
        try {
            response = NetworkUtils.getResponseFromHttpUrl(url, context);
            if (response != null) {
                try {
                    JSONObject horarioObject = new JSONObject(response);
                    String dataUtimaAtualizacao = horarioObject.getString(TAG_ULTIMA_ATUALIZACAO);

                    if (atividade_antiga.getHorarioUltimaAtualizacao() == null || !atividade_antiga.getHorarioUltimaAtualizacao().equals(dataUtimaAtualizacao)) {
                        Atividade atividade = detalheAtividadeParseJSON(response, context);
                        DatabaseHandler.getDB().updateAtividade(atividade);
                        Log.d("teste", "atualizando atividade");
                        return atividade;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}