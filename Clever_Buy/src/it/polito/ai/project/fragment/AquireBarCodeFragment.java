package it.polito.ai.project.fragment;

import it.polito.ai.project.andoidside.R;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AquireBarCodeFragment extends Fragment {

	private Button _scan;
	private TextView _tvStatus, _tvResult;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_acquire_barcode, container, false);
         
        _scan = (Button) rootView.findViewById(R.id.acq_bc_btnScan);
        _tvStatus = (TextView) rootView.findViewById(R.id.acq_bc_tvStatus);
        _tvResult = (TextView) rootView.findViewById(R.id.acq_bc_tvResult);
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
				}

			}
		});
	}
	
	//In the same activity you’ll need the following to retrieve the results:
		public void onActivityResult(int requestCode, int resultCode, Intent intent) {
			if (requestCode == 0) {

				if (resultCode == android.app.Activity.RESULT_OK) {
					_tvStatus.setText(intent.getStringExtra("SCAN_RESULT_FORMAT"));
					_tvResult.setText(intent.getStringExtra("SCAN_RESULT"));
				} else if (resultCode == android.app.Activity.RESULT_CANCELED) {
					_tvStatus.setText("Press a button to start a scan.");
					_tvResult.setText("Scan cancelled.");
				}
			}
		}
}
