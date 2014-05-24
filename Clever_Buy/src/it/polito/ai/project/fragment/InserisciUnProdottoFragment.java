package it.polito.ai.project.fragment;

import it.polito.ai.project.R;





import it.polito.ai.project.main.MyHttpClient;

import java.io.File;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;


abstract class AlbumStorageDirFactory {
	public abstract File getAlbumStorageDir(String albumName);
}

public class InserisciUnProdottoFragment extends Fragment{

	protected static final int RESULT_INTENT_CAMERA = 0;
	
	private ImageButton ib_scan;
	private Button btn_foto;
	private ImageView iv_foto;
	private EditText et_descrizione, et_barcode;
	private Button btn_inserisci;
	private DatePicker dp_data_inizio, dp_data_fine;
	private Spinner spin_categoria, spin_sottocategoria;
	private View rootView;
	
	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView 		= inflater.inflate(R.layout.fragment_inserisci_un_prodotto, container, false);
		btn_foto 		= (Button) rootView.findViewById(R.id.ip_btn_foto);
		ib_scan 		= (ImageButton) rootView.findViewById(R.id.ip_ib_scan);
		btn_inserisci 	= (Button) rootView.findViewById(R.id.ip_btn_inserisci);
		et_barcode  	= (EditText) rootView.findViewById(R.id.ip_et_barcode);
		et_descrizione  = (EditText) rootView.findViewById(R.id.ip_et_descrizione);
		dp_data_inizio 	= (DatePicker) rootView.findViewById(R.id.ip_dp_data_inizio);
		dp_data_fine    = (DatePicker) rootView.findViewById(R.id.ip_dp_data_fine);

		spin_categoria = (Spinner) rootView.findViewById(R.id.ip_spin_categoria);
		spin_sottocategoria = (Spinner) rootView.findViewById(R.id.ip_spin_sottocategoria);
		
		addListener();
		return rootView;
	}
	
	private void addListener() {
		ib_scan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					IntentIntegrator integrator = new IntentIntegrator(InserisciUnProdottoFragment.this);
					integrator.initiateScan();

				} catch (Exception e) {
					e.printStackTrace();
					
				}

			}
		});

		btn_inserisci.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// devo dapprima controllare che sia tutto in ordine
				
				// poi fa la post verso il server
			}
		});
		
		OnClickListener data_inizio_fine = new OnClickListener() { 			
			@Override
			public void onClick(View v) {
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, dp_data_inizio.getYear());
				cal.set(Calendar.MONTH, dp_data_inizio.getMonth());
				cal.set(Calendar.DAY_OF_MONTH, dp_data_inizio.getDayOfMonth());
				dp_data_fine.setMinDate( cal.getTimeInMillis());
			}
		}; 

		dp_data_inizio.setOnClickListener( data_inizio_fine);
		dp_data_fine.setOnClickListener( data_inizio_fine);
/*
		AsyncHttpClient client = new AsyncHttpClient();
		client.get("http://192.168.1.2:8080/supermarket/android/inserzione/getCategorie", new JsonHttpResponseHandler() {
			
			@Override
			public void onSuccess(JSONArray response) {
				JSONObject jsonObj = null;
				try {
					ArrayList<String> categorieArray = new ArrayList<String>();
					for (int i = 0; i < response.length(); i++) 
						categorieArray.add(response.getString(i));
					
					ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categorieArray);
					spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spin_categoria.setAdapter(spinnerArrayAdapter);
				} catch (JSONException e) {
					
					e.printStackTrace();
				}
			
			}
			
			@Override
			public void onFailure(Throwable error, String content) {
				Log.v("ERROR" , "onFailure error : " + error.toString() + "content : " + content);
			}
		});
*/

		btn_foto.setOnClickListener(new OnClickListener() {


			@Override
			public void onClick(View v) {
				/*
				//pic = (ImageView)findViewById(R.id.iup_iv_foto);
				cameraObject = isCameraAvailiable();
				showCamera = new ShowCamera(getActivity().getApplicationContext(), cameraObject);
				_preview.addView(showCamera);
				 */
				Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, RESULT_INTENT_CAMERA);

			}
		});
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch (requestCode) {
			case IntentIntegrator.REQUEST_CODE:	// barcode scanner// barcode scanner
				IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
				if(scanResult!=null){
					et_barcode.setText(""+scanResult.getContents());
					checkBarcode(scanResult.getContents());
				}
			break;
			case RESULT_INTENT_CAMERA: 
				super.onActivityResult(requestCode, resultCode, intent);
				Bitmap bp = (Bitmap) intent.getExtras().get("data");
				iv_foto.setImageBitmap(bp);
				break;
		} // switch
	}

	private String getAlbumName() {
		return getString(R.string.album_name);
	}
	
	private void checkBarcode(String barcode) {
		// far partire la richiesta di check per il server.
		System.out.println("sssssssss");
		
		MyHttpClient.get("/inserzione/checkbarcode/" + barcode, null, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONArray response) {
				Log.v("DEBUG", "FAIL: " + response.toString());
				System.out.println("ooooooooooo");
				updateGui(response);
				
			}
			
			@Override
			public void onSuccess(String resp) {
				Log.v("DEBUG", "FAIL: " + resp);
				
				
			}
			

			@Override
			public void onFailure(Throwable error, String content) {
				Log.v("ERROR" , "onFailure error : " + error.toString() + "content : " + content);
			}
			
		});
		
		
	}
	
	private void updateGui(JSONArray response) {
		JSONObject jsonObj = null;
		try {
			Log.v("DEBUG", "FAIL: " + response.toString());
			jsonObj = response.getJSONObject(0);
			if(jsonObj.getBoolean("trovato"))
			{
				//prodotto trovato
				EditText tmp = (EditText) rootView.findViewById(R.id.ip_et_descrizione);
				et_descrizione.setText(jsonObj.getString("descrizione")); 
			}
			else{
				// prodotto non trovato
				EditText tmp = (EditText) rootView.findViewById(R.id.ip_et_descrizione);
				et_descrizione.setText("NON TROVATO");
			}
			
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
	}
}
	
