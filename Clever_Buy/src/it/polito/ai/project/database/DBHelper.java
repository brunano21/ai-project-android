package it.polito.ai.project.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	//Nome del database
    public static final String NOME_DB = "biblioteca";
    //Versione del database
    public static final int VERSIONE_DB = 1;
    //Stringa per creare la tabella libri
    private static final String CREATE_TABLE_LIBRI =
        "create table libri (_id integer primary key autoincrement, "
     + Libri.TITOLO+ " TEXT,"
     + Libri.PREZZO+ " INTEGER,"
     + Libri.TRAMA+ " TEXT" + ");";
 
    /**
     * Costruttore dell'helper
     * @param context
     */
    public DBHelper(Context context) {
        super(context, NOME_DB, null, VERSIONE_DB);
        // TODO Auto-generated constructor stub
    }
    /**
     * Metodo usato per creare il DB se non esiste
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        System.err.println("Dentro create tables");
        db.execSQL(CREATE_TABLE_LIBRI);
    }
    /**
     * Metodo usato per fare upgrade del DB se il numero di versione nuovo è maggiore del vecchio
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        System.err.println("Dentro update tables");
        db.execSQL("DROP TABLE IF EXISTS "+ Libri.TABELLA);
        onCreate(db);
    }

}
