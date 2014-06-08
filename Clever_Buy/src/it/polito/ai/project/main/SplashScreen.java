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
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

public class SplashScreen extends Activity {
	public final static String EXTRA_MESSAGE = "MESSAGE";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);

		
		//PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
		//MyHttpClient.setCookieStore(myCookieStore);
		
		
		Button _btn_salta = (Button) findViewById(R.id.ip_btn_inserisci);
		_btn_salta.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				salta();

			}
		});
		
		Button _btn_login = (Button) findViewById(R.id._btn_login);
		_btn_login.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				EditText _et_login_username = (EditText) findViewById(R.id.ss_et_login_username);
				EditText _et_login_password = (EditText) findViewById(R.id.ss_et_login_password);
				
				MyHttpClient.setBasicAuth(_et_login_username.getText().toString(), _et_login_password.getText().toString());
				
				MyHttpClient.post("/login", null, new AsyncHttpResponseHandler(){
					@Override
					public void onSuccess(String response) {
						EditText _et_tmp = (EditText) findViewById(R.id.ss_et_register_username);
						_et_tmp.setText(response);
					}
					public void onFailure(Throwable error, String content) {
						Log.v(EXTRA_MESSAGE , "onFailure error : " + error.toString() + "content : " + content);
					}
				});
			}
		});

		Button _btn_registrati = (Button) findViewById(R.id._btn_registrati);
		_btn_registrati.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v)  {
				
				EditText _et_register_username = (EditText) findViewById(R.id.ss_et_register_username);
				EditText _et_register_password = (EditText) findViewById(R.id.ss_et_register_password);
				EditText _et_register_confirmPassword = (EditText) findViewById(R.id.ss_et_register_confirmPassword);
				EditText _et_register_email = (EditText) findViewById(R.id.ss_et_register_email);
				
				RequestParams params = new RequestParams();
				params.put("userName", _et_register_username.getText().toString());
				params.put("password", _et_register_password.getText().toString());
				params.put("confirmPassword", _et_register_confirmPassword.getText().toString());
				params.put("email", _et_register_email.getText().toString());
				
				MyHttpClient.post("/register", params,  new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONArray response) {
						EditText _et_tmp = (EditText) findViewById(R.id.ip_et_prezzo);
						JSONObject jsonObj = null;
						try {
							jsonObj = response.getJSONObject(0);
						} catch (JSONException e) {
							
							e.printStackTrace();
						}
						
						_et_tmp.setText("lpol " + jsonObj.toString()); 
					}
					
				

					@Override
					public void onFailure(Throwable error, String content) {
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
