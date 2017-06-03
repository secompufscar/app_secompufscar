package br.com.secompufscar.secomp_ufscar.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

public class Atividade {
    private final static String TAG_ATIVIDADE = "palestras";
    private final static String TAG_ID = "id_atividade";
    private final static String TAG_DATA_INICIO = "data_inicio";
    private final static String TAG_HORA_INICIO = "hora_inicio";
    private final static String TAG_HORA_FIM = "hora_fim";
    private final static String TAG_NOME = "nome_atividade";
    private final static String TAG_MINISTRANTE = "ministrante_atividade";
    private final static String TAG_FOTO = "foto_atividade";
    private final static String TAG_DESCRICAO = "descricao_atividade";
    private final static String TAG_LOCAL = "local_atividade";

    private int id;
    private String nome, local, tipo, descricao;
    private Date horarioInicio, horarioFim;

    public Atividade() {

    }

    public Atividade(int id, String nome, String local, String descricao) {
        this.id = id;
        this.nome = nome;
        this.local = local;
        this.descricao = descricao;
    }

    /**
     * Métodos set
     **/

    public void setId(int id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setLocal(String local){
        this.local = local;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    // TODO: padronizar formato de hora da api do site
    // TODO: Arrumar formato da data
    public void setHorarioInicio(String data, String horario) {
        GregorianCalendar cal = new GregorianCalendar();
        // TODO: Achar solução para isso
        int ano = 2016;
        int dia = Integer.valueOf(data.substring(0, data.indexOf("/")));
        int mes = Integer.valueOf(data.substring(data.indexOf("/") + 1, data.length())) - 1;
        Log.d("data", ano + " " + mes + "  " + dia + "  " + "\n");
        cal.set(ano, mes, dia, 12, 00, 00);
        horarioInicio = cal.getTime();
    }

    public void setHorarioFim(String data, String horario) {
        GregorianCalendar cal = new GregorianCalendar();
        // TODO: Achar solução para isso
        int ano = 2016;
        int dia = Integer.valueOf(data.substring(0, data.indexOf("/")));
        int mes = Integer.valueOf(data.substring(data.indexOf("/") + 1, data.length())) - 1;
        Log.d("data", ano + " " + mes + "  " + dia + "  " + "\n");
        cal.set(ano, mes, dia, 12, 00, 00);
        horarioFim = cal.getTime();
    }

    /**
     * Métodos get
     * **/

    public int getId(){
        return this.id;
    }

    public String getNome(){
        return this.nome;
    }

    public String getLocal(){
        return this.local;
    }

    public String getDescricao(){
        return this.descricao;
    }


    public static ArrayList<Atividade> AtividadeParseJSON(String json) {

        if (json != null) {
            try {
                // Lista das atividades
                ArrayList<Atividade> atividadeList = new ArrayList<Atividade>();
                JSONObject jsonObj = new JSONObject(json);
                // Getting JSON Array node
                JSONArray atividades = jsonObj.getJSONArray(TAG_ATIVIDADE);
                // Looping em para pegar cada atividade
                for (int i = 0; i < atividades.length(); i++) {
                    Atividade atividade = new Atividade();
                    JSONObject atividadeObject = atividades.getJSONObject(i);

                    atividade.id = Integer.valueOf(atividadeObject.getString(TAG_ID));
                    atividade.nome = atividadeObject.getString(TAG_NOME);
                    atividade.descricao = atividadeObject.getString(TAG_DESCRICAO);
                    atividade.local = atividadeObject.getString(TAG_LOCAL);

                    // Adicionando a atividade a lista
                    Log.d("testsql","adicionaAtividade");
                    atividadeList.add(atividade);
                }
                return atividadeList;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            Log.e("ServiceHandler", "No data received from HTTP request");
            return null;
        }
    }

//    public String encodeAsJSON() {
//        GregorianCalendar calendar = new GregorianCalendar();
//        calendar.setTime(getData());
//        String data = String.valueOf(calendar.get(GregorianCalendar.DAY_OF_MONTH)) + "/" + String.valueOf(calendar.get(GregorianCalendar.MONTH));
//        if (descricao != null)
//            descricao = descricao.replace("\"", "\\\"");
//        if (local != null)
//            local = local.replace("\"", "\\\"");
//        nome = nome.replace("\"", "\\\"");
//        if (ministrante != null)
//            ministrante = ministrante.replace("\"", "\\\"");
//
//        String JSONString = "{ \"data_inicio_atividade\": \"" + data + "\","
//                + "\"foto_atividade\": \"" + urlImagem + " \","
//                + "\"hora_inicio_atividade\": \"" + horarioInicio + "\","
//                + "\"local_atividade\": \"" + local + "\","
//                + "\"nome_atividade\": \"" + nome + "\","
//                + "\"hora_fim_atividade\": \"" + horarioFim + "\","
//                + "\"ministrante_atividade\": \"" + ministrante + "\","
//                + "\"id_atividade\": " + eventoID + ","
//                + "\"descricao_atividade\": \"" + descricao + "\"}";
//
//        return JSONString;
//    }
}
