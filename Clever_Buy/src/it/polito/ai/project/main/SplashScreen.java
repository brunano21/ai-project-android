package it.polito.ai.project.main;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SplashScreen extends Activity {

	// User Session Manager Class
	private UserSessionManager session;

	public final static String EXTRA_MESSAGE = "MESSAGE";
	private LinearLayout _linearLayout_home_registration_login;

	private CheckBox _cb_auto_login;

	private TextView _tv_username, _tv_password, _tv_conferma_password, _tv_mail, _tv_registration, _tv_login, _tv_error_message;
	private EditText _et_username, _et_password, _et_conferma_password, _et_mail;
	private Button	_buttonLogin, _buttonRegistration, _buttonSalta;

	
	private Context _context;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_context = this;
		session = new UserSessionManager(getApplicationContext());

		// Check user logged
		//If User is not logged in, this will redirect user to MainActivity and finish current activity from activity stack.
		if(session.checkLoginAble()) {
			progressDialog = ProgressDialog.show(this, "Loading", "Login in corso...", false);

			// get user data from session
			HashMap<String, String> user = session.getUserDetails();
			String username = user.get(UserSessionManager.KEY_NAME);
			String password = user.get(UserSessionManager.KEY_PASSWORD);

			funzioneLogin(username, password);
		} 
		else {
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

			_cb_auto_login.setText("Ricordami");
			_tv_username.setText("Username");
			_tv_password.setText("Password");
			_tv_conferma_password.setText("Conferma Password");
			_tv_mail.setText("Mail");
			_tv_registration.setText("Nuovo? Registrati qui...");
			_tv_login.setText("Accedi qui...");



			_et_username.setInputType( InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS |  InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
			_et_password.setInputType( InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			_et_conferma_password.setInputType( InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			_et_mail.setInputType( InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

			_buttonLogin.setText("Login"); // se modifichi quello che scrivi qui, modifca anche nel On ClickListener
			_buttonLogin.setEnabled(false);

			_buttonRegistration.setText("Registrati"); // se modifichi quello che scrivi qui, modifca anche nel On ClickListener
			_buttonRegistration.setEnabled(false);

			_buttonSalta.setText("Salta");


			_linearLayout_home_registration_login.addView(_tv_mail);
			_linearLayout_home_registration_login.addView(_et_mail);
			_linearLayout_home_registration_login.addView(_tv_password);
			_linearLayout_home_registration_login.addView(_et_password);
			_linearLayout_home_registration_login.addView(_buttonLogin);
			_linearLayout_home_registration_login.addView(_buttonSalta);
			_linearLayout_home_registration_login.addView(_tv_registration);
			_linearLayout_home_registration_login.addView(_tv_error_message);
			_linearLayout_home_registration_login.addView(_cb_auto_login);

			addListnerOnTexts();

			addListnerOnButton();

			addListnerOnCheckBox();

			setContentView(_linearLayout_home_registration_login);
		}
	}


	private void addListnerOnTexts() {

		_tv_login.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// se tocchi qui vai alla pagina login
				_linearLayout_home_registration_login.removeAllViews();
				_linearLayout_home_registration_login.addView(_tv_mail);
				_linearLayout_home_registration_login.addView(_et_mail);
				_linearLayout_home_registration_login.addView(_tv_password);
				_linearLayout_home_registration_login.addView(_et_password);
				_linearLayout_home_registration_login.addView(_buttonLogin);
				_linearLayout_home_registration_login.addView(_buttonSalta);
				_linearLayout_home_registration_login.addView(_tv_registration);
				_linearLayout_home_registration_login.addView(_cb_auto_login);


			}
		});

		_tv_registration.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
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
			}
		});

		TextWatcher onSearchFieldTextChanged = new TextWatcher(){
			public void afterTextChanged(Editable s) {

				if(!"".equals(_et_mail.getText().toString()) && !"".equals(_et_password.getText().toString()) && !"".equals(_et_mail.getText().toString()))
					_buttonRegistration.setEnabled(true);
				else
					_buttonRegistration.setEnabled(false);

				if(!"".equals(_et_mail.getText().toString()) && !"".equals(_et_password.getText().toString()))

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
				progressDialog = ProgressDialog.show(_context, "Loading", "Login in corso...", false);
				funzioneLogin(_et_mail.getText().toString(), _et_password.getText().toString());

			}
		});

		_buttonRegistration.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v)  {
				if(!_et_conferma_password.getText().toString().equals(_et_password.getText().toString())) {
					Toast.makeText(getApplicationContext(), "Le password inserite non coincidono.", Toast.LENGTH_LONG).show();
					return;
				}
				progressDialog = ProgressDialog.show(_context, "Loading", "Registrazione in corso...", false);


				RequestParams params = new RequestParams();
				params.put("userName", _et_username.getText().toString());
				params.put("password", _et_password.getText().toString());
				params.put("confirmPassword", _et_conferma_password.getText().toString());
				params.put("email", _et_mail.getText().toString());

				MyHttpClient.post("/register", params, new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
						
						JSONObject jsonObj = null;
						try {
							jsonObj = response.getJSONObject(0);
							if(jsonObj.getBoolean("registration_result")) {
								// registazione avvenuta con successo
								progressDialog.dismiss();
								Toast.makeText(_context, "Controlla la tua casella di posta per abilitare il tuo account", Toast.LENGTH_LONG).show();
							} else {
								// registrazione fallita
								JSONObject a = new JSONObject(jsonObj.getString("errors"));
								String errorString = "ERRORE\n";
								for (int i = 0; i < a.names().length(); i++)
									errorString += a.names().getString(i).toUpperCase() + ": " + a.getString(a.names().getString(i)) + "\n";
								errorString.substring(0, errorString.length()-2);
								progressDialog.dismiss();
								Toast.makeText(_context, errorString, Toast.LENGTH_LONG).show();
							}
								
						} catch (JSONException e) {

							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
						Log.v(EXTRA_MESSAGE , "onFailure error : " + throwable.getMessage() + "content : " + responseString);
						progressDialog.dismiss();
						Toast.makeText(_context, "Si è verificato un errore con il server", Toast.LENGTH_LONG).show();
					}

				});

			}
		});

	} // fine addListnerOnButton()


	protected void funzioneLogin(String username, String password) {

		// Validate if username, password is filled             
		//if(username.trim().length() > 0 && password.trim().length() > 0) {
			MyHttpClient.setBasicAuth(username, password);
			MyHttpClient.post("/login", null, new JsonHttpResponseHandler(){
				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
					if(_cb_auto_login != null && _cb_auto_login.isChecked()) 
						session.createUserLoginSession(_et_mail.getText().toString(), _et_password.getText().toString(), _cb_auto_login.isChecked());

					try {
						JSONObject jsonObject = response.getJSONObject(0);
						session.setUserData(UserSessionManager.KEY_USERNAME, jsonObject.getString("username")); 
						session.setUserData(UserSessionManager.KEY_REPUTAZIONE, jsonObject.getString("reputazione")); 
						session.setUserData(UserSessionManager.KEY_CREDITI_PENDENTI, jsonObject.getString("crediti_pendenti")); 
						session.setUserData(UserSessionManager.KEY_CREDITI_ACQUISITI, jsonObject.getString("crediti_acquisiti")); 
						session.setUserData(UserSessionManager.KEY_NUMERO_INFRAZIONI, jsonObject.getString("numero_infrazioni")); 
						session.setUserData(UserSessionManager.KEY_NUMERO_INSERZIONI_TOTALI, jsonObject.getString("numero_inserzioni_totali")); 
						session.setUserData(UserSessionManager.KEY_NUMERO_INSERZIONI_POSITIVE, jsonObject.getString("numero_inserzioni_positive")); 
						session.setUserData(UserSessionManager.KEY_NUMERO_VALUTAZIONI_TOTALI, jsonObject.getString("numero_valutazioni_totali")); 
						session.setUserData(UserSessionManager.KEY_NUMERO_VALUTAZIONI_POSITIVE, jsonObject.getString("numero_valutazioni_positive")); 
						session.setUserData(UserSessionManager.KEY_NUMERO_INSERZIONI_CORRENTI, jsonObject.getString("numero_inserzioni_correnti")); 
					} catch (JSONException e) {
						e.printStackTrace();
					} 
					salta();
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
					progressDialog.dismiss();
					Toast.makeText(getApplicationContext(), "Username e/o Password non corretti! " + statusCode, Toast.LENGTH_LONG).show();
					System.out.println(responseString);
					System.out.println(throwable.getMessage());
				}

				
				/*
				@Override
				public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
					super.onFailure(statusCode, headers, throwable, errorResponse);
					System.out.println(errorResponse);
					System.out.println(statusCode);
					System.out.println(throwable.toString());
					System.out.println(throwable.getLocalizedMessage());
					if (throwable.getCause() instanceof ConnectException ) { 
						Toast.makeText(getApplicationContext(), "Impossibile contattare il server. Riprova più tardi! " + statusCode, Toast.LENGTH_LONG).show();
				    }
				}*/

			});
		/*} 
		else 
		{
			// user didn't entered username or password
			Toast.makeText(getApplicationContext(), "Per favore inserisci username e password",  Toast.LENGTH_LONG).show();
		}*/
	}

	private void addListnerOnCheckBox() {
		_cb_auto_login.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				_tv_error_message.setText(isChecked?"true":"false");
			}
		});
	}


	public void salta() {
		progressDialog.dismiss();
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}


}
