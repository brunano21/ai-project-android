package it.polito.ai.project.andoidside.fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.loopj.android.http.*;

import it.polito.ai.project.andoidside.R;
import android.app.Fragment;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HomeRegistrationLoginFragment extends Fragment {

	Toast _toast;
	LinearLayout _linearLayout_home_registration_login;
	TextView _tv_username;
	TextView _tv_password;
	TextView _tv_conferma_password;
	TextView _tv_mail;
	TextView _tv_registration;
	TextView _tv_login;

	EditText _et_username;
	EditText _et_password;
	EditText _et_conferma_password;
	EditText _et_mail;

	Button	_buttonLogin;
	Button	_buttonRegistration;

	public HomeRegistrationLoginFragment(){

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {


		_linearLayout_home_registration_login = new LinearLayout(container.getContext());
		_linearLayout_home_registration_login.setOrientation(LinearLayout.VERTICAL);
		_linearLayout_home_registration_login.setGravity(Gravity.CENTER_VERTICAL);
		_linearLayout_home_registration_login.setPadding(15, 15, 15, 30);

		_tv_username = new TextView(container.getContext());
		_tv_password = new TextView(container.getContext());
		_tv_conferma_password = new TextView(container.getContext());
		_tv_mail = new TextView(container.getContext());
		_tv_registration = new TextView(container.getContext());
		_tv_login = new TextView(container.getContext());

		_et_username = new EditText(container.getContext());
		_et_password = new EditText(container.getContext());
		_et_conferma_password = new EditText(container.getContext());
		_et_mail = new EditText(container.getContext());

		_buttonLogin = new Button(container.getContext());
		_buttonRegistration = new Button(container.getContext());



		_tv_username.setText("Username");
		_tv_password.setText("Password");
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

		_linearLayout_home_registration_login.addView(_tv_username);
		_linearLayout_home_registration_login.addView(_et_username);
		_linearLayout_home_registration_login.addView(_tv_password);
		_linearLayout_home_registration_login.addView(_et_password);
		_linearLayout_home_registration_login.addView(_tv_conferma_password);
		_linearLayout_home_registration_login.addView(_et_conferma_password);
		_linearLayout_home_registration_login.addView(_tv_mail);
		_linearLayout_home_registration_login.addView(_et_mail);
		_linearLayout_home_registration_login.addView(_buttonRegistration);
		_linearLayout_home_registration_login.addView(_tv_login);



		addListnerOnTexts();

		_buttonLogin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				// TODO Auto-generated method stub
				if( _buttonLogin.isEnabled() )
				{
					StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
					StrictMode.setThreadPolicy(policy);

					AsyncHttpClient client = new AsyncHttpClient();
					//client.setBasicAuth("zorro@zorro.t", "zorro");
					RequestParams params = new RequestParams();
					params.put("j_username", _et_username.getText().toString());
					params.put("j_password", _et_password.getText().toString());
					Log.v("debug"," sono passato");
					
					AsyncHttpResponseHandler x =new AsyncHttpResponseHandler();
			 
					client.post("http://192.168.1.42:8080/supermarket/j_spring_security_check", params, new AsyncHttpResponseHandler() {
								@Override
						public void onSuccess(int statusCode, String content) {
							Log.v("login","onSuccess");
							Log.v("login", statusCode + " " + content);
						} 
						
						@Override
						public void onFailure(Throwable error, String content)
						{
							Log.e("login" , "onFailure error : " + error.toString() + "content : " + content);
						}

						@Override
						public void onFinish()
						{
							Log.v("login" , "onFinish");
							
						}
					});		
				}
				else
				{
					;
				}
			}
		});



		//	View rootView = inflater.inflate(R.layout.fragment_home_registration_login, container, false);

		//	return rootView;
		return _linearLayout_home_registration_login;
	}

	private void addListnerOnTexts() {


		_tv_login.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// se tocchi qui vai alla pagina login
				_linearLayout_home_registration_login.removeAllViews();
				_linearLayout_home_registration_login.addView(_tv_username);
				_linearLayout_home_registration_login.addView(_et_username);
				_linearLayout_home_registration_login.addView(_tv_password);
				_linearLayout_home_registration_login.addView(_et_password);
				_linearLayout_home_registration_login.addView(_tv_conferma_password);
				_linearLayout_home_registration_login.addView(_et_conferma_password);
				_linearLayout_home_registration_login.addView(_buttonLogin);
				_linearLayout_home_registration_login.addView(_tv_registration);

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
				_linearLayout_home_registration_login.addView(_tv_login);
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


	private StringBuilder inputStreamToString(InputStream is) {
		String rLine = "";
		StringBuilder answer = new StringBuilder();

		InputStreamReader isr = new InputStreamReader(is);

		BufferedReader rd = new BufferedReader(isr);

		try {
			while ((rLine = rd.readLine()) != null) {
				answer.append(rLine);
			}
		}

		catch (IOException e) {
			e.printStackTrace();
		}
		return answer;
	}


}