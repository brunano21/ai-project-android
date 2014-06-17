package it.polito.ai.project.database;
 
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Credenziali {

	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String ACCEDI_AUTO = "accedi_auto";
	
	public static final String TABELLA = "credenziali";
    public static final String[] COLONNE = new String[]{ USERNAME, PASSWORD, ACCEDI_AUTO};
 

    public static void insertCredenziali(SQLiteDatabase db, String username, String password, Boolean accedi_auto){
    	
    	// TODO 
    	// voglio solo n record nel mio DB, quindi elimino tutto prima di fare una insert
    	deleteAllCredenziali(db);
    	
        ContentValues v = new ContentValues();
        v.put(USERNAME, username);
        v.put(PASSWORD, password);
        v.put(ACCEDI_AUTO, accedi_auto);
 
        db.insert(TABELLA, null, v);
    }

    public static Cursor getAllCredenziali(SQLiteDatabase db){
        return db.query(TABELLA, COLONNE, null, null, null, null, null);
    }
    
    public static boolean updateCredenziali(SQLiteDatabase db, String username, String password, Boolean accedi_auto){
        ContentValues v = new ContentValues();
        v.put(USERNAME, username);
        v.put(PASSWORD, password);
        v.put(ACCEDI_AUTO, accedi_auto);
 
        return db.update(TABELLA, v, USERNAME + "=" + username, null) >0; 
    }
    
    public static boolean deleteAllCredenziali(SQLiteDatabase db) {
        return db.delete(TABELLA, USERNAME + "= *", null) > 0;
    }
}
