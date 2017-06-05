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

    // Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NOME = "nome";
    private static final String KEY_LOCAL = "local";
    private static final String KEY_DESCRICAO = "descricao";

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

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ATIVIDADE_TABLE = "CREATE TABLE " + TABLE_ATIVIDADE + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOME + " TEXT,"
                + KEY_LOCAL + " TEXT," + KEY_DESCRICAO + " TEXT" + ")";

        String CREATE_PESSOA_TABLE = "CREATE TABLE " + TABLE_PESSOA + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOME + " TEXT,"
                + "foto" + " BLOB," + KEY_DESCRICAO + " TEXT" + ")";

        db.execSQL(CREATE_ATIVIDADE_TABLE);
        db.execSQL(CREATE_PESSOA_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATIVIDADE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PESSOA);

        // Create tables again
        onCreate(db);
    }

    /**
     * Operações com Atividades
     **/

    // Adicionando uma nova atividade
    void addAtividade(Atividade atividade) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, atividade.getId());
        values.put(KEY_NOME, atividade.getNome());
        values.put(KEY_LOCAL, atividade.getLocal());
        values.put(KEY_DESCRICAO, atividade.getDescricao());

        // Inserting Row
        db.insert(TABLE_ATIVIDADE, null, values);
        db.close(); // Closing database connection
    }


    public void addAllAtividades(List<Atividade> atividades) {

        if (atividades != null) {

            SQLiteDatabase db = this.getWritableDatabase();

            for (int i = 0; i < atividades.size(); i++) {
                ContentValues values = new ContentValues();

                values.put(KEY_ID, atividades.get(i).getId());
                values.put(KEY_NOME, atividades.get(i).getNome());
                values.put(KEY_LOCAL, atividades.get(i).getLocal());
                values.put(KEY_DESCRICAO, atividades.get(i).getDescricao());

                db.insert(TABLE_ATIVIDADE, null, values);
            }

            db.close(); // Closing database connection
        }
    }

    // Recuperando uma atividade
    public Atividade getAtividade(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ATIVIDADE, new String[]{KEY_ID,
                        KEY_NOME, KEY_LOCAL, KEY_DESCRICAO}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Atividade atividade = new Atividade(id, cursor.getString(1), cursor.getString(2), cursor.getString(3));

        return atividade;
    }

    // Getting All Contacts
    public List<Atividade> getAllAtividades() {
        List<Atividade> atividades = new ArrayList<Atividade>();
        // Select All Query
//        String selectQuery = "SELECT  * FROM " + TABLE_ATIVIDADE;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_ATIVIDADE, new String[]{KEY_ID,
                        KEY_NOME, KEY_LOCAL, KEY_DESCRICAO}, null,
                null, null, null, KEY_NOME + " ASC", null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Atividade atividade = new Atividade();
                atividade.setId(Integer.parseInt(cursor.getString(0)));
                atividade.setNome(cursor.getString(1));
                atividade.setLocal(cursor.getString(2));
                atividade.setDescricao(cursor.getString(3));

                atividades.add(atividade);
            } while (cursor.moveToNext());
        }

        // return contact list
        return atividades;
    }

//    // Updating single contact
//    public int updateContact(Atividade atividade) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(KEY_NAME, contact.getName());
//        values.put(KEY_PH_NO, contact.getPhoneNumber());
//
//        // updating row
//        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
//                new String[] { String.valueOf(contact.getID()) });
//    }
//
//    // Deleting single contact
//    public void deleteContact(Contact contact) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
//                new String[] { String.valueOf(contact.getID()) });
//        db.close();
//    }


    // Getting contacts Count
    public int getAtividadesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_ATIVIDADE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        return count;
    }

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
    public Pessoa getPessoa(int id){
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
