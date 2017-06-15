package br.com.secompufscar.secomp_ufscar.data;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static DatabaseHandler db;

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Nome do banco
    private static final String DATABASE_NAME = "secompManager";
    // Nome das tabelas
    private static final String TABLE_ATIVIDADE = "atividade";
    private static final String TABLE_PESSOA = "pessoa";
    private static final String TABLE_PATROCINADOR = "patrocinador";


    // Nome das colunas de Atividade
    private static final String KEY_ID = "id";
    private static final String KEY_NOME = "nome";
    private static final String KEY_LOCAL = "local";
    private static final String KEY_DESCRICAO = "descricao";
    private static final String KEY_FAVORITO = "favorito";

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
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOME + " TEXT,"
                + KEY_LOCAL + " TEXT," + KEY_DESCRICAO + " TEXT," + KEY_FAVORITO + " INTEGER" + ")";

        // Tabela Pessoa
        String CREATE_PESSOA_TABLE = "CREATE TABLE " + TABLE_PESSOA + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOME + " TEXT,"
                + "foto" + " BLOB," + KEY_DESCRICAO + " TEXT" + ")";

        // Tabela Patrocinador
        // TODO: A definir

        db.execSQL(CREATE_ATIVIDADE_TABLE);
        db.execSQL(CREATE_PESSOA_TABLE);
    }

    // Atualização do banco
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATIVIDADE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PESSOA);

        // Create tables again
        onCreate(db);
    }

    /**
     * Operações para a tabela Atividade
     **/

    // Adicionar uma nova atividade
    void addAtividade(Atividade atividade) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, atividade.getId());
        values.put(KEY_NOME, atividade.getNome());
        values.put(KEY_LOCAL, atividade.getLocal());
        values.put(KEY_DESCRICAO, atividade.getDescricao());

        if(atividade.isFavorito()){
            values.put(KEY_FAVORITO, 1);
        } else {
            values.put(KEY_FAVORITO, 0);
        }

        // Inserting Row
        db.insert(TABLE_ATIVIDADE, null, values);
        db.close(); // Closing database connection
    }

    // Adiciona várias atividades de uma única vez
    public void addAllAtividades(List<Atividade> atividades) {

        if (atividades != null) {

            SQLiteDatabase db = this.getWritableDatabase();

            for (int i = 0; i < atividades.size(); i++) {
                ContentValues values = new ContentValues();

                values.put(KEY_ID, atividades.get(i).getId());
                values.put(KEY_NOME, atividades.get(i).getNome());
                values.put(KEY_LOCAL, atividades.get(i).getLocal());
                values.put(KEY_DESCRICAO, atividades.get(i).getDescricao());

                if(atividades.get(i).isFavorito()){
                    values.put(KEY_FAVORITO, 1);
                } else {
                    values.put(KEY_FAVORITO, 0);
                }

                db.insert(TABLE_ATIVIDADE, null, values);
            }

            db.close(); // Closing database connection
        }
    }

    // Recupera uma atividade pelo seu ID
    public Atividade getAtividade(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ATIVIDADE, new String[]{KEY_ID,
                        KEY_NOME, KEY_LOCAL, KEY_DESCRICAO, KEY_FAVORITO}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Atividade atividade = new Atividade(id, cursor.getString(1), cursor.getString(2), cursor.getString(3));

        if(cursor.getInt(4) == 1){
            atividade.setFavorito(true);
        }

        db.close();

        return atividade;
    }

    // Retorna uma lista com todas as atividades
    public List<Atividade> getAllAtividades() {
        List<Atividade> atividades = new ArrayList<Atividade>();

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_ATIVIDADE, new String[]{KEY_ID,
                        KEY_NOME, KEY_LOCAL, KEY_DESCRICAO, KEY_FAVORITO}, null,
                null, null, null, KEY_NOME + " ASC", null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Atividade atividade = new Atividade();
                atividade.setId(cursor.getInt(0));
                atividade.setNome(cursor.getString(1));
                atividade.setLocal(cursor.getString(2));
                atividade.setDescricao(cursor.getString(3));

                if(cursor.getInt(4) == 1){
                    atividade.setFavorito(true);
                }

                atividades.add(atividade);
            } while (cursor.moveToNext());
        }

        db.close();
        // retorna a lista de atividades
        return atividades;
    }

    public List<Atividade> getAllFavoritos() {
        List<Atividade> atividades = new ArrayList<Atividade>();

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_ATIVIDADE, new String[]{KEY_ID,
                        KEY_NOME, KEY_LOCAL, KEY_DESCRICAO, KEY_FAVORITO}, KEY_FAVORITO + "=?",
                new String[]{"1"}, null, null, KEY_NOME + " ASC", null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Atividade atividade = new Atividade();
                atividade.setId(cursor.getInt(0));
                atividade.setNome(cursor.getString(1));
                atividade.setLocal(cursor.getString(2));
                atividade.setDescricao(cursor.getString(3));
                atividade.setFavorito(true);
                atividades.add(atividade);

//                if(cursor.getInt(4) == 1){
//                    atividade.setFavorito(true);
//                    atividades.add(atividade);
//                }

            } while (cursor.moveToNext());
        }

        db.close();
        // retorna a lista de atividades
        return atividades;
    }

    // Atualiza uma atividade
    public void updateAtividade(Atividade atividade) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        if(atividade.isFavorito()){
            values.put(KEY_FAVORITO, 1);
        } else {
            values.put(KEY_FAVORITO, 0);
        }

        // atualiza a atividade no banco
        db.update(TABLE_ATIVIDADE, values, KEY_ID + " = ?",
                new String[]{String.valueOf(atividade.getId())});
        db.close();
    }

    // Deleta uma Atividade
    public void deleteContact(Atividade atividade) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ATIVIDADE, KEY_ID + " = ?",
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
     * Operações para a tabela Atividade
     **/

    public void addPessoa(Pessoa pessoa) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, pessoa.getId());
        values.put(KEY_NOME, pessoa.getNome());
        Log.d("testeFoto", "Antes de Salvar: " + Integer.toString(pessoa.getFoto().length));

        values.put("foto", pessoa.getFoto());
        values.put(KEY_DESCRICAO, pessoa.getDescricao());

        // Inserting Row
        db.insert(TABLE_PESSOA, null, values);
        Log.d("testeFoto", "Salvou");
        db.close(); // Closing database connection
    }

    // TODO: Completar as informaçoes de pessoa
    public Pessoa getPessoa(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PESSOA, new String[]{KEY_ID,
                        KEY_NOME, "foto", KEY_DESCRICAO}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Pessoa pessoa = new Pessoa();
        pessoa.setId(id);
        pessoa.setNome(cursor.getString(1));
        pessoa.setFoto(cursor.getBlob(2));
        pessoa.setDescricao(cursor.getString(3));

        return pessoa;
    }
}
