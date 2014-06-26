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
		
		
	public HomeFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		root = inflater.inflate(R.layout.fragment_home, container, false);

		// Session class instance
		session = new UserSessionManager(container.getContext());
		
		
		
		
		
		



		return root;
	}
}
