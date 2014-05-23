package it.polito.ai.project.androidside.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class Libri {
	//I campi della tabella libri
    public static final String ID = "_id";
    public static final String TITOLO = "titolo";
    public static final String PREZZO = "prezzo";
    public static final String TRAMA = "trama";
 
    public static final String TABELLA = "libri";
    public static final String[] COLONNE = new String[]{ID, TITOLO, PREZZO, TRAMA};
 
    /**
     * Funzione statica usata per inserire un libro nel DB
     * @param db
     * @param titolo
     * @param prezzo
     * @param trama
     */
    public static void insertLibro(SQLiteDatabase db, String titolo, int prezzo, String trama){
        ContentValues v = new ContentValues();
        v.put(TITOLO, titolo);
        v.put(PREZZO, prezzo);
        v.put(TRAMA, trama);
 
        db.insert(TABELLA, null, v);
    }
    /**
     * Ritorna un cursore che punta a tutti i libri contenuti nel DB
     * @param db
     * @return
     */
    public static Cursor getAllLibri(SQLiteDatabase db){
        return db.query(TABELLA, COLONNE, null, null, null, null, null);
    }
    /**
     * Cancella il libro che a l'id passato come parametro
     * @param db
     * @param id
     * @return
     */
    public static boolean deleteLibro(SQLiteDatabase db, long id) {
        return db.delete(TABELLA, ID + "=" + id, null) > 0;
    }
    /**
     * Ritorna un cursore che punta al libro che ha l'id passato come parametro
     * @param db
     * @param id
     * @return
     * @throws SQLException
     */
    public static Cursor getLibro(SQLiteDatabase db, long id) throws SQLException {
        Cursor c = db.query(true, TABELLA, COLONNE, ID + "=" + id, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }
    /**
     * Modifica i valori del libro il cui id è uguale a quello passato come parametro
     * @param db
     * @param id
     * @param titolo
     * @param prezzo
     * @param trama
     * @return
     */
    public static boolean updateLibro(SQLiteDatabase db, long id, String titolo, int prezzo, String trama){
        ContentValues v = new ContentValues();
        v.put(TITOLO, titolo);
        v.put(PREZZO, prezzo);
        v.put(TRAMA, trama);
 
        return db.update(TABELLA, v, ID + "=" + id, null) >0; 
    }
}
