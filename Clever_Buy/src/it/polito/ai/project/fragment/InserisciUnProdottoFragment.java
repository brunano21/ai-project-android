package it.polito.ai.project.fragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import it.polito.ai.project.andoidside.R;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


abstract class AlbumStorageDirFactory {
	public abstract File getAlbumStorageDir(String albumName);
}

public class InserisciUnProdottoFragment extends Fragment {



	protected static final int MM_RESULT_CAMERA = 0;
	protected static final int MM_RESULT_BARCODE = 1;
	
	private Button _scan, _foto;
	private EditText _etBarcode;

	private DatePicker _data_inizio, _data_fine;
	
	private ImageView _immagine_foto;
	
	public static Camera isCameraAvailiable(){
		Camera object = null;
		try {
			object = Camera.open(); 
		}
		catch (Exception e){
		}
		return object; 
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_inserisci_un_prodotto, container, false);

		_scan = (Button) rootView.findViewById(R.id.iup_bc_btnScan);
		_foto = (Button) rootView.findViewById(R.id.iup_bc_btnFoto);
		_immagine_foto = (ImageView) rootView.findViewById(R.id.iup_iv_foto);
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
				try {

					Intent intent = new Intent(
							"com.google.zxing.client.android.SCAN");
					intent.putExtra("SCAN_MODE", "QR_CODE_MODE,PRODUCT_MODE");
					startActivityForResult(intent, MM_RESULT_BARCODE);

				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(getActivity(), "installa questa applicazione per scansionare il codice a barre", Toast.LENGTH_LONG).show();
					Intent intent = new Intent(Intent.ACTION_VIEW, 	Uri.parse("http://zxing.appspot.com/scan"));
					startActivity(intent);
				}

			}
		});

		_foto.setOnClickListener(new OnClickListener() {
			

			@Override
			public void onClick(View v) {
/*
				//pic = (ImageView)findViewById(R.id.iup_iv_foto);
				cameraObject = isCameraAvailiable();
				showCamera = new ShowCamera(getActivity().getApplicationContext(), cameraObject);
				_preview.addView(showCamera);
	*/
				Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			      startActivityForResult(intent, MM_RESULT_CAMERA);
				
			}
		});

		OnClickListener data_inizio_fine = new OnClickListener() { 			
			@Override
			public void onClick(View v) {
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

		switch (requestCode) 
		{
		case MM_RESULT_BARCODE: // barcode scanner// barcode scanner
			if (resultCode == android.app.Activity.RESULT_OK) 
			{
				_etBarcode.setText(intent.getStringExtra("SCAN_RESULT"));
			} 
			else 
				if (resultCode == android.app.Activity.RESULT_CANCELED) 
				{
					_etBarcode.setText("Scan cancelled.");
				}
			break;

		case MM_RESULT_CAMERA: 
			super.onActivityResult(requestCode, resultCode, intent);
		      Bitmap bp = (Bitmap) intent.getExtras().get("data");
		      _immagine_foto.setImageBitmap(bp);
			break;

		} // switch
	}

	private String getAlbumName() {
		return getString(R.string.album_name);
	}


}
