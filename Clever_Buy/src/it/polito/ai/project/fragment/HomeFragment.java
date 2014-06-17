package it.polito.ai.project.fragment;

import it.polito.ai.project.R;
import it.polito.ai.project.main.UserSessionManager;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class HomeFragment extends Fragment {

	// User Session Manager Class
	UserSessionManager session;
	
	private View root;
	private CheckBox _cb_auto_login;
		
		
	public HomeFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		root = inflater.inflate(R.layout.fragment_home, container, false);

		// Session class instance
		session = new UserSessionManager(container.getContext());
		
		_cb_auto_login = (CheckBox) root.findViewById(R.id.checkBox_auto_login);
		
		
		
		
		
		
		
		_cb_auto_login.setChecked(session.checkLoginAble());
		_cb_auto_login.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				session.setCheckLoginAble( isChecked );
			}
		});



		return root;
	}
}
