package it.polito.ai.project.main;

import it.polito.ai.project.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

public class SplashScreen extends Activity {

	public final static String EXTRA_MESSAGE = "MESSAGE";



	Toast _toast;
	LinearLayout _linearLayout_home_registration_login;
	
	CheckBox _cb_auto_login;
	
	TextView _tv_username;
	TextView _tv_password;
	TextView _tv_conferma_password;
	TextView _tv_mail;
	TextView _tv_registration;
	TextView _tv_login;
	TextView _tv_error_message;

	EditText _et_username;
	EditText _et_password;
	EditText _et_conferma_password;
	EditText _et_mail;

	Button	_buttonLogin;
	Button	_buttonRegistration;
	Button	_buttonSalta;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		_linearLayout_home_registration_login = new LinearLayout(this.getApplicationContext());
		_linearLayout_home_registration_login.setOrientation(LinearLayout.VERTICAL);
		_linearLayout_home_registration_login.setGravity(Gravity.CENTER_VERTICAL);
		_linearLayout_home_registration_login.setPadding(15, 15, 15, 30);

		_cb_auto_login = new CheckBox(this.getApplicationContext());
		
		_tv_username = new TextView(this.getApplicationContext());
		_tv_password = new TextView(this.getApplicationContext());
		_tv_conferma_password = new TextView(this.getApplicationContext());
		_tv_mail = new TextView(this.getApplicationContext());
		_tv_registration = new TextView(this.getApplicationContext());
		_tv_login = new TextView(this.getApplicationContext());
		_tv_error_message = new TextView(this.getApplicationContext());


		_et_username = new EditText(this.getApplicationContext());
		_et_password = new EditText(this.getApplicationContext());
		_et_conferma_password = new EditText(this.getApplicationContext());
		_et_mail = new EditText(this.getApplicationContext());

		_buttonLogin = new Button(this.getApplicationContext());
		_buttonRegistration = new Button(this.getApplicationContext());
		_buttonSalta = new Button(this.getApplicationContext());


		_cb_auto_login.setText("Auto Log");
		
		_tv_username.setText("Username");
		_tv_password.setText("Password");
		_tv_conferma_password.setText("Confirm Password");
		_tv_mail.setText("Mail");
		_tv_registration.setText("New? Register here...");
		_tv_login.setText("Login here...");


		_et_username.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
		_et_password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
		_et_conferma_password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
		_et_mail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

		_buttonLogin.setText("Login"); // se modifichi quello che scrivi qui, modifca anche nel On ClickListener
		_buttonLogin.setEnabled(false);

		_buttonRegistration.setText("Register New Account"); // se modifichi quello che scrivi qui, modifca anche nel On ClickListener
		_buttonRegistration.setEnabled(false);

		_buttonSalta.setText("Salta");


		_linearLayout_home_registration_login.addView(_cb_auto_login);
		_linearLayout_home_registration_login.addView(_tv_username);
		_linearLayout_home_registration_login.addView(_et_username);
		_linearLayout_home_registration_login.addView(_tv_password);
		_linearLayout_home_registration_login.addView(_et_password);
		_linearLayout_home_registration_login.addView(_tv_conferma_password);
		_linearLayout_home_registration_login.addView(_et_conferma_password);
		_linearLayout_home_registration_login.addView(_tv_mail);
		_linearLayout_home_registration_login.addView(_et_mail);
		_linearLayout_home_registration_login.addView(_buttonRegistration);
		_linearLayout_home_registration_login.addView(_buttonSalta);
		_linearLayout_home_registration_login.addView(_tv_login);
		_linearLayout_home_registration_login.addView(_tv_error_message);

		addListnerOnTexts();

		addListnerOnButton();

		addListnerOnCheckBox();

		setContentView(_linearLayout_home_registration_login);
	}





	private void addListnerOnTexts() {


		_tv_login.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// se tocchi qui vai alla pagina login
				_linearLayout_home_registration_login.addView(_cb_auto_login);
				_linearLayout_home_registration_login.removeAllViews();
				_linearLayout_home_registration_login.addView(_tv_username);
				_linearLayout_home_registration_login.addView(_et_username);
				_linearLayout_home_registration_login.addView(_tv_password);
				_linearLayout_home_registration_login.addView(_et_password);
				_linearLayout_home_registration_login.addView(_tv_conferma_password);
				_linearLayout_home_registration_login.addView(_et_conferma_password);
				_linearLayout_home_registration_login.addView(_buttonLogin);
				_linearLayout_home_registration_login.addView(_buttonSalta);
				_linearLayout_home_registration_login.addView(_tv_registration);
				_linearLayout_home_registration_login.addView(_tv_error_message);

			}
		});

		_tv_registration.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				_linearLayout_home_registration_login.addView(_cb_auto_login);
				_linearLayout_home_registration_login.removeAllViews();
				_linearLayout_home_registration_login.addView(_tv_username);
				_linearLayout_home_registration_login.addView(_et_username);
				_linearLayout_home_registration_login.addView(_tv_password);
				_linearLayout_home_registration_login.addView(_et_password);
				_linearLayout_home_registration_login.addView(_tv_conferma_password);
				_linearLayout_home_registration_login.addView(_et_conferma_password);
				_linearLayout_home_registration_login.addView(_tv_mail);
				_linearLayout_home_registration_login.addView(_et_mail);
				_linearLayout_home_registration_login.addView(_buttonRegistration);
				_linearLayout_home_registration_login.addView(_buttonSalta);
				_linearLayout_home_registration_login.addView(_tv_login);
				_linearLayout_home_registration_login.addView(_tv_error_message);
			}
		});




		TextWatcher onSearchFieldTextChanged = new TextWatcher(){
			public void afterTextChanged(Editable s) {
				//your business logic after text is changed

				if(!_et_conferma_password.getText().toString().equals(_et_password.getText().toString()))
				{
					//suggerire nell'editbox che la conferma della password non è corretta
					_et_conferma_password.setHint("errore in password ripetuta");
				}
				if(		!"".equals(_et_username.getText().toString()) &&  
						!"".equals(_et_password.getText().toString()) &&  
						!"".equals(_et_mail.getText().toString())  		)
					_buttonRegistration.setEnabled(true);
				else
					_buttonRegistration.setEnabled(false);


				if(		!"".equals(_et_username.getText().toString()) &&  
						!"".equals(_et_password.getText().toString()) )
					_buttonLogin.setEnabled(true);
				else
					_buttonLogin.setEnabled(false);

			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after){
				//your business logic before text is changed
			}

			public void onTextChanged(CharSequence s, int start, int before, int count){
				//your business logic while text has changed
			}
		};

		_et_username.addTextChangedListener(onSearchFieldTextChanged);
		_et_password.addTextChangedListener(onSearchFieldTextChanged);
		_et_conferma_password.addTextChangedListener(onSearchFieldTextChanged);
		_et_mail.addTextChangedListener(onSearchFieldTextChanged);

	}



	private void addListnerOnButton() {

		_buttonSalta.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				salta();
			}
		});

		_buttonLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				MyHttpClient.setBasicAuth(_et_username.getText().toString(), _et_password.getText().toString());

				MyHttpClient.post("/login", null, new AsyncHttpResponseHandler(){
					@Override
					public void onSuccess(String response) {
						EditText _et_tmp = (EditText) findViewById(R.id.ss_et_register_username);
						_et_tmp.setText(response);
						salta();
					}
					public void onFailure(Throwable error, String content) {
						Log.v(EXTRA_MESSAGE , "onFailure error : " + error.toString() + "content : " + content);
						if("HTTP Status 401 - Bad credentials".equals(content))
						{
							_tv_error_message.setText("Bad credentials");
						}
					}
				});
			}
		});


		_buttonRegistration.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v)  {

				RequestParams params = new RequestParams();
				params.put("userName", _et_username.getText().toString());
				params.put("password", _et_password.getText().toString());
				params.put("confirmPassword", _et_conferma_password.getText().toString());
				params.put("email", _et_mail.getText().toString());

				MyHttpClient.post("/register", params,  new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONArray response) {
						
						JSONObject jsonObj = null;
						try {
							jsonObj = response.getJSONObject(0);
						} catch (JSONException e) {

							e.printStackTrace();
						}
						_tv_error_message.setText("lpol " + jsonObj.toString()); 
					}



					@Override
					public void onFailure(Throwable error, String content) {
						Log.v(EXTRA_MESSAGE , "onFailure error : " + error.toString() + "content : " + content);
					}

				});
			}


		});


	} // fine addListnerOnButton()




	private void addListnerOnCheckBox() {
		// TODO Auto-generated method stub
		_cb_auto_login.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				_tv_error_message.setText(isChecked?"true":"false");
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
