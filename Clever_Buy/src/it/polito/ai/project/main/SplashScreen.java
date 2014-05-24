package it.polito.ai.project.main;

import it.polito.ai.project.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SplashScreen extends Activity {
	public final static String EXTRA_MESSAGE = "MESSAGE";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);

		Button _btn_salta = (Button) findViewById(R.id.ip_btn_inserisci);
		_btn_salta.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				salta();

			}
		});

		Button _btn_registrati = (Button) findViewById(R.id._btn_registrati);
		_btn_registrati.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v)  {
				AsyncHttpClient client = new AsyncHttpClient();
				RequestParams params = new RequestParams();
				EditText _et_userName = (EditText) findViewById(R.id._et_userName);
				EditText _et_password = (EditText) findViewById(R.id._et_password);
				EditText _et_confirmPassword = (EditText) findViewById(R.id._et_confirmPassword);
				EditText _et_email = (EditText) findViewById(R.id._et_email);
				
				client.setBasicAuth("zorro@zorro.it", "zo777rro");
				Log.v(EXTRA_MESSAGE, "eccolo");
				params.put("userName", _et_userName.getText().toString());
				params.put("password", _et_password.getText().toString());
				params.put("confirmPassword", _et_confirmPassword.getText().toString());
				params.put("email", _et_email.getText().toString());
				client.post("http://192.168.1.2:8080/supermarket/android/register", params,  new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONArray response) {
						EditText _et_userName = (EditText) findViewById(R.id._et_userName);
						JSONObject jsonObj = null;
						try {
							jsonObj = response.getJSONObject(0);
						} catch (JSONException e) {
							
							e.printStackTrace();
						}
						
						_et_userName.setText("lpol " + jsonObj.toString()); 
					}
					
				

					@Override
					public void onFailure(Throwable error, String content)
					{
						EditText _et_userName = (EditText) findViewById(R.id._et_userName);
						_et_userName.setText("lpol44444 " + content);
						Log.v(EXTRA_MESSAGE , "onFailure error : " + error.toString() + "content : " + content);
					}

				});
			}


		});
	}

	public void registrati() {

		/*
		client.post("http://192.168.1.2:8080/supermarket/register", params, new AsyncHttpResponseHandler(){
			@Override
		    public void onSuccess(String response) {
		        Log.v("POST", response);
		    }
		});*/
	}
	public void salta(){
		Log.v("SALTA", "saltaaaa");
		Intent intent = new Intent(this, MainActivity.class);
		String message = "porccoooo";
		intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
	}
}
