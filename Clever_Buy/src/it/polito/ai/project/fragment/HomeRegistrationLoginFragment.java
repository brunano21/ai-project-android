package it.polito.ai.project.fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import it.polito.ai.project.R;
import android.app.Fragment;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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
	TextView _tv_mail;
	TextView _tv_registration;
	TextView _tv_login;

	EditText _et_username;
	EditText _et_password;
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
		_tv_mail = new TextView(container.getContext());
		_tv_registration = new TextView(container.getContext());
		_tv_login = new TextView(container.getContext());

		_et_username = new EditText(container.getContext());
		_et_password = new EditText(container.getContext());
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
		_et_mail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

		_buttonLogin.setText("Login"); // se modifichi quello che scrivi qui, modifca anche nel On ClickListener
		_buttonLogin.setEnabled(false);

		_buttonRegistration.setText("Register New Account"); // se modifichi quello che scrivi qui, modifca anche nel On ClickListener
		_buttonRegistration.setEnabled(false);

		_linearLayout_home_registration_login.addView(_tv_username);
		_linearLayout_home_registration_login.addView(_et_username);
		_linearLayout_home_registration_login.addView(_tv_password);
		_linearLayout_home_registration_login.addView(_et_password);
		_linearLayout_home_registration_login.addView(_tv_mail);
		_linearLayout_home_registration_login.addView(_et_mail);
		_linearLayout_home_registration_login.addView(_buttonRegistration);
		_linearLayout_home_registration_login.addView(_tv_login);



		addListnerOnTexts();

		_buttonRegistration.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				// TODO Auto-generated method stub
				if( _buttonRegistration.isEnabled() )
				{
					StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
					StrictMode.setThreadPolicy(policy);

					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost("127.0.0.1/Register");

					try {

						HttpResponse response = httpclient.execute(httppost);
						String jsonResult = inputStreamToString(response.getEntity().getContent()).toString();

						JSONArray jsonArray = new JSONArray(jsonResult);

						for (int i = 0; i < jsonArray.length(); i++) {
							;
						}


					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
				_linearLayout_home_registration_login.addView(_tv_mail);
				_linearLayout_home_registration_login.addView(_et_mail);
				_linearLayout_home_registration_login.addView(_buttonRegistration);
				_linearLayout_home_registration_login.addView(_tv_login);
			}
		});




		TextWatcher onSearchFieldTextChanged = new TextWatcher(){
			public void afterTextChanged(Editable s) {
				//your business logic after text is changed

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