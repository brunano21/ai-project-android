package it.applicazioniinternet.project.android.cleverbuy;


/**
 * Questo servise manterrà l'allineamento tra i dati presenti sull'applicazione android ed il server centrale.
 */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class SyncronizationDataService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate(){
		//TODO debug
		Toast.makeText(this, "Service Created", Toast.LENGTH_LONG);

	}

	@Override  
	public void onStart(Intent intent, int startid) {  
		//TODO debug
		Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();    
	}  

	@Override  
	public void onDestroy() {  
		//TODO debug
		Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();  
		  
	}  
}
