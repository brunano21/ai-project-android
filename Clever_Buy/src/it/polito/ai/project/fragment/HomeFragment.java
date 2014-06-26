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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class HomeFragment extends Fragment {

	// User Session Manager Class
	UserSessionManager session;
	
	private View root;
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {

		root = inflater.inflate(R.layout.fragment_home, container, false);
		UserSessionManager session = new UserSessionManager(getActivity());

		TextView tmp = (TextView) root.findViewById(R.id.home_tv_username);
		tmp.setText(session.getUserData(UserSessionManager.KEY_USERNAME) + "!!");
		tmp = (TextView) root.findViewById(R.id.home_tv_reputazione_valore);
		tmp.setText(session.getUserData(UserSessionManager.KEY_REPUTAZIONE) + "%");
		tmp = (TextView) root.findViewById(R.id.home_tv_crediti_pendenti_valore);
		tmp.setText(session.getUserData(UserSessionManager.KEY_CREDITI_PENDENTI));
		tmp = (TextView) root.findViewById(R.id.home_tv_crediti_acquisiti_valore);
		tmp.setText(session.getUserData(UserSessionManager.KEY_CREDITI_ACQUISITI));
		tmp = (TextView) root.findViewById(R.id.home_tv_numero_inserzioni_positive_valore);
		tmp.setText(session.getUserData(UserSessionManager.KEY_NUMERO_INSERZIONI_POSITIVE));
		tmp = (TextView) root.findViewById(R.id.home_tv_numero_inserzioni_totali_valore);
		tmp.setText(session.getUserData(UserSessionManager.KEY_NUMERO_INSERZIONI_TOTALI));
		tmp = (TextView) root.findViewById(R.id.home_tv_numero_inserzioni_negative_valore);
		tmp.setText(String.valueOf(Integer.valueOf(session.getUserData(UserSessionManager.KEY_NUMERO_INSERZIONI_TOTALI)) - Integer.valueOf(session.getUserData(UserSessionManager.KEY_NUMERO_INSERZIONI_POSITIVE))));
		tmp = (TextView) root.findViewById(R.id.home_tv_inserzioni_in_corso_valore);
		tmp.setText(session.getUserData(UserSessionManager.KEY_NUMERO_INSERZIONI_CORRENTI));
		tmp = (TextView) root.findViewById(R.id.home_tv_numero_valutazioni_positive_valore);
		tmp.setText(session.getUserData(UserSessionManager.KEY_NUMERO_VALUTAZIONI_TOTALI));
		tmp = (TextView) root.findViewById(R.id.home_tv_numero_valutazioni_totali_valore);
		tmp.setText(session.getUserData(UserSessionManager.KEY_NUMERO_VALUTAZIONI_TOTALI));
		tmp = (TextView) root.findViewById(R.id.home_tv_numero_valutazioni_negative_valore);
		tmp.setText(String.valueOf(Integer.valueOf(session.getUserData(UserSessionManager.KEY_NUMERO_VALUTAZIONI_TOTALI)) - Integer.valueOf(session.getUserData(UserSessionManager.KEY_NUMERO_VALUTAZIONI_POSITIVE))));

		return root;
	}
}
