package br.com.secompufscar.secomp_ufscar.data;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static DatabaseHandler db;

    private static final DateTime inicioSecomp = new DateTime("2017-09-18");

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Nome do banco
    private static final String DATABASE_NAME = "secompManager";
    // Nome das tabelas
    private static final String TABLE_ATIVIDADE = "atividade";
    private static final String TABLE_PESSOA = "pessoa";
    private static final String TABLE_PATROCINADOR = "patrocinador";
    private static final String TABLE_MINISTRANTE = "ministrante";

    private DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static void setInstance(Context context) {

        if (db == null) {
            db = new DatabaseHandler(context.getApplicationContext());
        }
    }

    public static synchronized DatabaseHandler getDB() {
        return db;
    }

    // Criação das tabelas
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tabela Atividade
        String CREATE_ATIVIDADE_TABLE = "CREATE TABLE " + TABLE_ATIVIDADE + "("
                + Atividade.TAG_ID + " INTEGER PRIMARY KEY,"
                + Atividade.TAG_TITULO + " TEXT,"
                + Atividade.TAG_LOCAL + " TEXT,"
                + Atividade.TAG_TIPO + " TEXT,"
                + Atividade.TAG_DESCRICAO + " TEXT,"
                + Atividade.TAG_HORARIOS + " TEXT,"
                + Atividade.TAG_DATAHORA_INICIO + " DATETIME,"
                + Atividade.TAG_FAVORITO + " INTEGER)";

        // Tabela Pessoa
        String CREATE_PESSOA_TABLE = "CREATE TABLE " + TABLE_PESSOA + "("
                + Pessoa.TAG_ID + " INTEGER PRIMARY KEY,"
                + Pessoa.TAG_NOME + " TEXT,"
                + Pessoa.TAG_SOBRENOME + " TEXT,"
                + Pessoa.TAG_DESCRICAO + " TEXT,"
                + Pessoa.TAG_EMPRESA + " TEXT,"
                + Pessoa.TAG_PROFISSAO + " TEXT,"
                + Pessoa.TAG_CONTATOS + " TEXT,"
                + Pessoa.TAG_FOTO + " BLOB)";

        // Tabela Patrocinador
        String CREATE_PATROCINADOR_TABLE = "CREATE TABLE " + TABLE_PATROCINADOR + "("
                + Patrocinador.TAG_ID + " INTEGER PRIMARY KEY,"
                + Patrocinador.TAG_ORDEM + " INTEGER,"
                + Patrocinador.TAG_NOME + " TEXT,"
                + Patrocinador.TAG_WEBSITE + " TEXT,"
                + Patrocinador.TAG_COTA + " TEXT,"
                + Patrocinador.TAG_LOGO + " BLOB)";

        // Tabela Ministrante: vincula pessoa com a atividade que ela irá ministrar
        String CREATE_MINISTRANTE_TABLE = "CREATE TABLE " + TABLE_MINISTRANTE + "("
                + "ID_ATIVIDADE INTEGER,"
                + "ID_PESSOA INTEGER,"
                + "FOREIGN KEY(ID_ATIVIDADE) REFERENCES " + TABLE_ATIVIDADE + "(" + Atividade.TAG_ID + "),"
                + "FOREIGN KEY(ID_PESSOA) REFERENCES " + TABLE_PESSOA + "(" + Pessoa.TAG_ID + ")"
                + "PRIMARY KEY (ID_ATIVIDADE, ID_PESSOA))";

        db.execSQL(CREATE_ATIVIDADE_TABLE);
        db.execSQL(CREATE_PESSOA_TABLE);
        db.execSQL(CREATE_PATROCINADOR_TABLE);
        db.execSQL(CREATE_MINISTRANTE_TABLE);
    }

    // Atualização do banco
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATIVIDADE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PESSOA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PATROCINADOR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MINISTRANTE);

        // Create tables again
        onCreate(db);
    }

    /**
     * Operações para a tabela Atividade
     **/

    // Configura valores para insereir uma atividade
    private ContentValues getAtividadeRow(Atividade atividade) {
        ContentValues values = new ContentValues();
        values.put(Atividade.TAG_ID, atividade.getId());
        values.put(Atividade.TAG_TITULO, atividade.getTitulo());
        values.put(Atividade.TAG_LOCAL, atividade.getLocal());
        values.put(Atividade.TAG_DESCRICAO, atividade.getDescricao());
        values.put(Atividade.TAG_TIPO, atividade.getTipo());
        values.put(Atividade.TAG_HORARIOS, atividade.getHorariosString());
        values.put(Atividade.TAG_DATAHORA_INICIO, atividade.getDataHoraInicio());

        return values;
    }
    // TODO: É necessário verificar os ministrantes ainda

    // Adicionar uma nova atividade
    public void addAtividade(Atividade atividade) throws SQLiteException {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insertOrThrow(TABLE_ATIVIDADE, null, getAtividadeRow(atividade));
        db.close(); // Closing database connection

        addMinistrantes(atividade);
    }

    // Adiciona várias atividades de uma única vez, se alguma atividade já existir ela é atualizada
    public void addManyAtividades(List<Atividade> atividades) {
        if (atividades != null) {

            for (int i = 0; i < atividades.size(); i++) {
                try {
                    addAtividade(atividades.get(i));
                } catch (SQLiteConstraintException e) {
                    updateAtividade(atividades.get(i));
                }
            }
        }
    }

    // Atualiza uma atividade
    public boolean updateAtividade(Atividade atividade) {
        SQLiteDatabase db = this.getWritableDatabase();
        int linhasAfetadas = db.update(TABLE_ATIVIDADE, getAtividadeRow(atividade), Atividade.TAG_ID + " = ?",
                new String[]{String.valueOf(atividade.getId())});
        db.close();

        return (linhasAfetadas > 0);
    }

    public boolean updateFavorito(Atividade atividade) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        if (atividade.isFavorito()) {
            values.put(Atividade.TAG_FAVORITO, 1);
        } else {
            values.put(Atividade.TAG_FAVORITO, 0);
        }

        // atualiza o valor de favorito da atividade
        int linhasAfetadas = db.update(TABLE_ATIVIDADE, values, Atividade.TAG_ID + " = ?",
                new String[]{String.valueOf(atividade.getId())});
        db.close();

        return linhasAfetadas>0;
    }

    // Recupera uma atividade pelo seu ID
    public Atividade getDetalheAtividade(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ATIVIDADE,
                new String[]{Atividade.TAG_ID,
                        Atividade.TAG_TITULO,
                        Atividade.TAG_LOCAL,
                        Atividade.TAG_DESCRICAO,
                        Atividade.TAG_HORARIOS,
                        Atividade.TAG_TIPO,
                        Atividade.TAG_FAVORITO},
                Atividade.TAG_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Atividade atividade = new Atividade();
        atividade.setId(id);
        atividade.setTitulo(cursor.getString(1));
        atividade.setLocal(cursor.getString(2));
        atividade.setDescricao(cursor.getString(3));
        atividade.setHorarios(cursor.getString(4));
        atividade.setTipo(cursor.getString(5));


        if (cursor.getInt(6) == 1) {
            atividade.setFavorito(true);
        }

        cursor.close();
        db.close();

        return atividade;
    }

    public Atividade getResumoAtividade(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ATIVIDADE,
                new String[]{Atividade.TAG_ID,
                        Atividade.TAG_TITULO,
                        Atividade.TAG_HORARIOS,
                        Atividade.TAG_TIPO},
                Atividade.TAG_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Atividade atividade = new Atividade();
        atividade.setId(id);
        atividade.setTitulo(cursor.getString(1));
        atividade.setHorarios(cursor.getString(2));
        atividade.setTipo(cursor.getString(3));

        cursor.close();
        db.close();

        return atividade;
    }

    // Retorna uma lista com todas as atividades
    public List<Atividade> getAllAtividades() {
        List<Atividade> atividades = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_ATIVIDADE,
                new String[]{Atividade.TAG_ID,
                        Atividade.TAG_TITULO,
                        Atividade.TAG_LOCAL,
                        Atividade.TAG_HORARIOS,
                        Atividade.TAG_TIPO}, null, null, null, null, "date(" + Atividade.TAG_DATAHORA_INICIO + "), " + Atividade.TAG_TITULO + " ASC");

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Atividade atividade = new Atividade();
                atividade.setId(cursor.getInt(0));
                atividade.setTitulo(cursor.getString(1));
                atividade.setLocal(cursor.getString(2));
                atividade.setHorarios(cursor.getString(3));
                atividade.setTipo(cursor.getString(4));

                atividades.add(atividade);
            } while (cursor.moveToNext());
        }

        db.close();
        cursor.close();
        // retorna a lista de atividades
        return atividades;
    }

    public List<Atividade> getAtividadesByDay(int offset) {

        DateTime dia_offset = inicioSecomp.plusDays(offset);

        List<Atividade> atividades = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_ATIVIDADE,
                new String[]{Atividade.TAG_ID,
                        Atividade.TAG_TITULO,
                        Atividade.TAG_LOCAL,
                        Atividade.TAG_HORARIOS,
                        Atividade.TAG_TIPO,
                        Atividade.TAG_DATAHORA_INICIO}, "date(" + Atividade.TAG_DATAHORA_INICIO + ") = date(?)",
                new String[]{dia_offset.toString()}, null, null, "datetime(" + Atividade.TAG_DATAHORA_INICIO + "), " + Atividade.TAG_TITULO + " ASC");

        if (cursor.moveToFirst()) {
            do {
                Atividade atividade = new Atividade();

                atividade.setId(cursor.getInt(0));
                atividade.setTitulo(cursor.getString(1));
                atividade.setLocal(cursor.getString(2));
                atividade.setHorarios(cursor.getString(3));
                atividade.setTipo(cursor.getString(4));

                atividades.add(atividade);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return atividades;
    }

    public List<Atividade> getAllFavoritos() {
        List<Atividade> atividades = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_ATIVIDADE,
                new String[]{Atividade.TAG_ID,
                        Atividade.TAG_TITULO,
                        Atividade.TAG_LOCAL,
                        Atividade.TAG_HORARIOS,
                        Atividade.TAG_TIPO,
                        Atividade.TAG_FAVORITO}, Atividade.TAG_FAVORITO + "=?",
                new String[]{"1"}, null, null, "datetime(" + Atividade.TAG_DATAHORA_INICIO + "), " + Atividade.TAG_TITULO + " ASC");

        if (cursor.moveToFirst()) {
            do {
                Atividade atividade = new Atividade();

                atividade.setId(cursor.getInt(0));
                atividade.setTitulo(cursor.getString(1));
                atividade.setLocal(cursor.getString(2));
                atividade.setHorarios(cursor.getString(3));
                atividade.setTipo(cursor.getString(4));
                atividade.setFavorito(true);

                atividades.add(atividade);
            } while (cursor.moveToNext());
        }

        db.close();
        cursor.close();
        // retorna a lista de atividades
        return atividades;
    }

    // Deleta uma Atividade
    public void deleteAtividade(Atividade atividade) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ATIVIDADE, Atividade.TAG_ID + " = ?",
                new String[]{String.valueOf(atividade.getId())});
        db.close();
    }

    // Retorna a quantidade de atividades
    public int getAtividadesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_ATIVIDADE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    /**
     * Operações para a tabela Pessoa
     **/

    // Configura valores para inserir uma pessoa
    private ContentValues getPessoaRow(Pessoa pessoa) {
        ContentValues values = new ContentValues();
        values.put(Pessoa.TAG_ID, pessoa.getId());
        values.put(Pessoa.TAG_NOME, pessoa.getNome());
        values.put(Pessoa.TAG_SOBRENOME, pessoa.getSobrenome());
        values.put(Pessoa.TAG_DESCRICAO, pessoa.getDescricao());
        values.put(Pessoa.TAG_EMPRESA, pessoa.getEmpresa());
        values.put(Pessoa.TAG_PROFISSAO, pessoa.getProfissao());
        values.put(Pessoa.TAG_FOTO, pessoa.getFoto());
        values.put(Pessoa.TAG_CONTATOS, pessoa.getContatosString());

        return values;
    }

    public void addPessoa(Pessoa pessoa) throws SQLiteConstraintException {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insertOrThrow(TABLE_PESSOA, null, getPessoaRow(pessoa));
        db.close(); // Closing database connection
    }

    // Adiciona vários patrocinadores de uma única vez
    public void addManyPessoas(List<Pessoa> pessoas) {
        if (pessoas != null) {
            for (int i = 0; i < pessoas.size(); i++) {
                try {
                    addPessoa(pessoas.get(i));
                } catch (SQLiteConstraintException e){
                    updatePessoa(pessoas.get(i));
                }
            }
        }
    }

    // Atualiza uma pessoa
    public int updatePessoa(Pessoa pessoa) {
        SQLiteDatabase db = this.getWritableDatabase();
        // atualiza a pessoa no banco
        int linhasAfetadas = db.update(TABLE_PESSOA, getPessoaRow(pessoa), Pessoa.TAG_ID + " = ?",
                new String[]{String.valueOf(pessoa.getId())});
        db.close();

        return linhasAfetadas;
    }

    public Pessoa getDetalhePessoa(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PESSOA,
                new String[]{Pessoa.TAG_ID,
                        Pessoa.TAG_NOME,
                        Pessoa.TAG_SOBRENOME,
                        Pessoa.TAG_DESCRICAO,
                        Pessoa.TAG_EMPRESA,
                        Pessoa.TAG_PROFISSAO,
                        Pessoa.TAG_FOTO,
                        Pessoa.TAG_CONTATOS},
                Pessoa.TAG_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);


        if (cursor != null)
            cursor.moveToFirst();

        Pessoa pessoa = new Pessoa();
        pessoa.setId(id);
        pessoa.setNome(cursor.getString(1));
        pessoa.setSobrenome(cursor.getString(2));
        pessoa.setDescricao(cursor.getString(3));
        pessoa.setEmpresa(cursor.getString(4));
        pessoa.setProfissao(cursor.getString(5));
        pessoa.setFoto(cursor.getBlob(6));
        pessoa.setContatos(cursor.getString(7));

        db.close();
        cursor.close();

        return pessoa;
    }

    public Pessoa getResumoPessoa(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PESSOA,
                new String[]{Pessoa.TAG_ID,
                        Pessoa.TAG_NOME,
                        Pessoa.TAG_SOBRENOME,
                        Pessoa.TAG_EMPRESA,
                        Pessoa.TAG_FOTO},
                Pessoa.TAG_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);


        if (cursor != null)
            cursor.moveToFirst();

        Pessoa pessoa = new Pessoa();
        pessoa.setId(id);
        pessoa.setNome(cursor.getString(1));
        pessoa.setSobrenome(cursor.getString(2));
        pessoa.setEmpresa(cursor.getString(3));
        pessoa.setFoto(cursor.getBlob(4));

        db.close();
        cursor.close();

        return pessoa;
    }

    public List<Pessoa> getAllPessoas() {
        List<Pessoa> pessoas = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_PESSOA,
                new String[]{Pessoa.TAG_ID,
                        Pessoa.TAG_NOME,
                        Pessoa.TAG_SOBRENOME,
                        Pessoa.TAG_EMPRESA,
                        Pessoa.TAG_FOTO}, null, null, null, null, Pessoa.TAG_NOME + " ASC");

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Pessoa pessoa = new Pessoa();
                pessoa.setId(cursor.getInt(0));
                pessoa.setNome(cursor.getString(1));
                pessoa.setSobrenome(cursor.getString(2));
                pessoa.setEmpresa(cursor.getString(3));
                pessoa.setFoto(cursor.getBlob(4));

                pessoas.add(pessoa);
            } while (cursor.moveToNext());
        }

        db.close();
        cursor.close();

        return pessoas;
    }


    // Deleta uma pessoa
    public void deletePessoa(Pessoa pessoa) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PESSOA, Pessoa.TAG_ID + " = ?",
                new String[]{String.valueOf(pessoa.getId())});
        db.close();
    }


    /**
     * Operações para a tabela Patrocinador
     **/

    // Configura valores para inserir um patrocinador
    private ContentValues getPatrocinadorRow(Patrocinador patrocinador) {
        ContentValues values = new ContentValues();
        values.put(Patrocinador.TAG_ID, patrocinador.getId());
        values.put(Patrocinador.TAG_ORDEM, patrocinador.getOrdem());
        values.put(Patrocinador.TAG_NOME, patrocinador.getNome());
        values.put(Patrocinador.TAG_WEBSITE, patrocinador.getWebsite());
        values.put(Patrocinador.TAG_COTA, patrocinador.getCota());
        values.put(Patrocinador.TAG_LOGO, patrocinador.getLogo());

        return values;
    }

    // Adiciona vários patrocinadores de uma única vez
    public void addManyPatrocinadores(List<Patrocinador> patrocinadores) {
        if (patrocinadores != null) {

            SQLiteDatabase db = this.getWritableDatabase();

            for (int i = 0; i < patrocinadores.size(); i++) {

                try {
                    db.insertOrThrow(TABLE_PATROCINADOR, null, getPatrocinadorRow(patrocinadores.get(i)));
                } catch (Exception e) {
                    int linhasAfetadas = db.update(TABLE_PATROCINADOR, getPatrocinadorRow(patrocinadores.get(i)), Patrocinador.TAG_ID + " = ?",
                            new String[]{String.valueOf(patrocinadores.get(i).getId())});
                }
            }

            db.close(); // Closing database connection
        }
    }

    public HashMap<String, List<Patrocinador>> getPatrocinadoresByCota() {
        HashMap<String, List<Patrocinador>> map = new HashMap<>();

        // TODO: Arrumar isso, dessa forma fica estática (uma solução é criar uma tabela cotas)
        String[] cotas = {Patrocinador.COTA_DIAMANTE, Patrocinador.COTA_OURO, Patrocinador.COTA_PRATA, Patrocinador.COTA_DESAFIO, Patrocinador.COTA_APOIO};

        SQLiteDatabase db = this.getWritableDatabase();

        for (String cota : cotas) {
            List<Patrocinador> patrocinadores = new ArrayList<>();

            Cursor cursor = db.query(TABLE_PATROCINADOR,
                    new String[]{Patrocinador.TAG_ID,
                            Patrocinador.TAG_ORDEM,
                            Patrocinador.TAG_NOME,
                            Patrocinador.TAG_WEBSITE,
                            Patrocinador.TAG_COTA,
                            Patrocinador.TAG_LOGO},
                    Patrocinador.TAG_COTA + "=?",
                    new String[]{cota}, null, null, Patrocinador.TAG_ORDEM + " ASC");

            if (cursor.moveToFirst()) {
                do {
                    Patrocinador patrocinador = new Patrocinador();
                    patrocinador.setId(cursor.getInt(0));
                    patrocinador.setOrdem(cursor.getInt(1));
                    patrocinador.setNome(cursor.getString(2));
                    patrocinador.setWebsite(cursor.getString(3));
                    patrocinador.setCota(cursor.getString(4));
                    patrocinador.setLogo(cursor.getBlob(5));

                    patrocinadores.add(patrocinador);
                } while (cursor.moveToNext());
            }

            map.put(cota, patrocinadores);

            cursor.close();
        }

        db.close();

        return map;
    }

    // Atualiza um patrocinador

    public int updatePatrocinador(Patrocinador patrocinador) {
        SQLiteDatabase db = this.getWritableDatabase();

        int linhasAfetadas = db.update(TABLE_PATROCINADOR, getPatrocinadorRow(patrocinador), Patrocinador.TAG_ID + " = ?",
                new String[]{String.valueOf(patrocinador.getId())});
        db.close();

        return linhasAfetadas;
    }

    /**
     * Operações para a tabela Ministrante
     **/

    public void addMinistrantes(Atividade atividade) {
        Log.d("TESTE addMinistrante", atividade.getMinistrantes().toString());
        if (atividade.getMinistrantes() != null) {

            List<Pessoa> pessoas = atividade.getMinistrantes();

            addManyPessoas(pessoas);

            ContentValues values = new ContentValues();
            values.put("ID_ATIVIDADE", atividade.getId());

            SQLiteDatabase db = this.getWritableDatabase();

            for (int i = 0; i < pessoas.size(); i++) {
                values.put("ID_PESSOA", pessoas.get(i).getId());

                try {
                    db.insertOrThrow(TABLE_MINISTRANTE, null, values);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            db.close(); // Closing database connection
        }
    }
}
