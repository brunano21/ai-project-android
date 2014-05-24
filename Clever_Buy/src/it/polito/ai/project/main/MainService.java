package it.polito.ai.project.main;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MainService extends Service {


	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("service", "onCreate()");

		Toast.makeText(this.getApplicationContext(), "onCreate", Toast.LENGTH_LONG);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//TODO do something useful
		Log.d("service", "onStartCommand");
		Toast.makeText(this.getApplicationContext(), "onStartCommand", Toast.LENGTH_LONG);
		return Service.START_NOT_STICKY;
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("service", "onDestroy()");

		Toast.makeText(this.getApplicationContext(), "onDestroy", Toast.LENGTH_LONG);
	}



	@Override
	public IBinder onBind(Intent arg0) {
		// Ritorniamo null in quanto non si vuole permettere
		// l'accesso al servizio da una applicazione diversa
		return null;
	}

}
