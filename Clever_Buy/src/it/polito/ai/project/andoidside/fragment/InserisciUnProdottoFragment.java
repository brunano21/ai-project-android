package it.polito.ai.project.andoidside.fragment;

import java.util.Calendar;

import it.polito.ai.project.andoidside.R;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class InserisciUnProdottoFragment extends Fragment {

	private Button _scan;
	private EditText _etBarcode;
	
	private DatePicker _data_inizio, _data_fine;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_inserisci_un_prodotto, container, false);

		_scan = (Button) rootView.findViewById(R.id.iup_bc_btnScan);
		_etBarcode  = (EditText) rootView.findViewById(R.id.iup_editText2);
		
		_data_inizio  = (DatePicker) rootView.findViewById(R.id.iup_dp_data_inizio);
		_data_fine    = (DatePicker) rootView.findViewById(R.id.iup_dp_data_fine);
		
		addListner();
		
		return rootView;
	}

	private void addListner() {
		_scan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				try {
					
					Intent intent = new Intent(
							"com.google.zxing.client.android.SCAN");
					intent.putExtra("SCAN_MODE", "QR_CODE_MODE,PRODUCT_MODE");
					startActivityForResult(intent, 0);
				
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
					Toast.makeText(getActivity(), "installa questa applicazione per scansionare il codice a barre", Toast.LENGTH_LONG).show();
					
					Intent intent = new Intent(Intent.ACTION_VIEW, 
						     Uri.parse("http://zxing.appspot.com/scan"));
						startActivity(intent);
				}

			}
		});
		
		OnClickListener data_inizio_fine = new OnClickListener() { 			
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stubcal
					Calendar cal = Calendar.getInstance();
					cal.set(Calendar.YEAR, _data_inizio.getYear());
					cal.set(Calendar.MONTH, _data_inizio.getMonth());
					cal.set(Calendar.DAY_OF_MONTH, _data_inizio.getDayOfMonth());
					_data_fine.setMinDate( cal.getTimeInMillis());
				}
			}; 
		
			_data_inizio.setOnClickListener( data_inizio_fine);
			_data_fine.setOnClickListener( data_inizio_fine);
			
		
	}
	
	//In the same activity you’ll need the following to retrieve the results:
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {

			if (resultCode == android.app.Activity.RESULT_OK) {
				_etBarcode.setText(intent.getStringExtra("SCAN_RESULT"));
			} else if (resultCode == android.app.Activity.RESULT_CANCELED) {
				_etBarcode.setText("Scan cancelled.");
			}
		}
	}
}
