package it.polito.ai.project.main;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
 
public class UserSessionManager {
     
    // Shared Preferences reference
    SharedPreferences pref;
     
    // Editor reference for Shared preferences
    Editor editor;
     
    // Context
    Context _context;
     
    // Shared pref mode
    int PRIVATE_MODE = 0;
     
    // Sharedpref file name
    private static final String PREFER_NAME = "AndroidPref";
     
    // All Shared Preferences Keys
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";
     
    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";

    // Email address (make variable public to access from outside)
    public static final String KEY_PASSWORD = "password";

    
    public static final String KEY_USERNAME = "username";
    public static final String KEY_REPUTAZIONE = "reputazione";
    public static final String KEY_CREDITI_PENDENTI = "crediti_pendenti";
    public static final String KEY_CREDITI_ACQUISITI = "crediti_acquisiti";
    public static final String KEY_NUMERO_INFRAZIONI = "numero_infrazioni";
    public static final String KEY_NUMERO_INSERZIONI_TOTALI = "numero_inserzioni_totali";
    public static final String KEY_NUMERO_INSERZIONI_POSITIVE = "numero_inserzioni_positive";
    public static final String KEY_NUMERO_VALUTAZIONI_TOTALI = "numero_valutazioni_totali";
    public static final String KEY_NUMERO_VALUTAZIONI_POSITIVE = "numero_valutazioni_positive";
    public static final String KEY_NUMERO_INSERZIONI_CORRENTI = "numero_inserzioni_correnti";
    
    
    // Constructor
    public UserSessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
     
    //Create login session
    public void createUserLoginSession(String name, String password, boolean auto_login){
        // Storing login value as TRUE
        editor.putBoolean(IS_USER_LOGIN, auto_login);
         
        // Storing name in pref
        editor.putString(KEY_NAME, name);

        // Storing password in pref
        editor.putString(KEY_PASSWORD, password);
         
        // commit changes
        editor.commit();
    }   
     
    /**
     * Check login method will check user login status
     * If false it will redirect user to login page
     * Else do anything
     * */
    public boolean checkLoginAble(){
        // Check login status
        if(this.isUserLoggedIn())
            return true;
    	return false;
    }
     
     
     
    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails() {
         
        //Use hashmap to store user credentials
        HashMap<String, String> user = new HashMap<String, String>();
         
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
         
        // user email id
        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));
         
        // return user
        return user;
    }
     
    /**
     * Clear session details
     * */
    public void logoutUser(){
         
        // Clearing all user data from Shared Preferences
        editor.clear();
        editor.commit();
         
        // After logout redirect user to Login Activity
        Intent i = new Intent(_context, SplashScreen.class);
         
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
         
        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
         
        // Staring Login Activity
        _context.startActivity(i);
    }
     
     
    // Check for login
    public boolean isUserLoggedIn(){
        return pref.getBoolean(IS_USER_LOGIN, false);
    }

	public void setCheckLoginAble(boolean isChecked) {
		// Storing login value as TRUE
        editor.putBoolean(IS_USER_LOGIN, isChecked);
        editor.commit();
	}
	
	public void setUserData(String key, String value) {
		editor.putString(key, value);
		Boolean ret = editor.commit();
		if(!ret)
			System.out.println("ERRORE:" + key + " - " + value);
	}
	
	public String getUserData(String key) {
		return pref.getString(key, null);
	}
		
}
