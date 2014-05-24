package it.polito.ai.project.fragment;

import it.polito.ai.project.R;
import it.polito.ai.project.main.MyHttpClient;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;


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
		spin_sottocategoria.setPrompt("Seleziona la categoria");
		
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

		MyHttpClient.get("/inserzione/getCategorie", null, new JsonHttpResponseHandler() {
			
			@Override
			public void onSuccess(JSONArray response) {
				aggiornaSpinnerCategorie(response);
			}
			
			@Override
			public void onFailure(Throwable error, String content) {
				Log.v("ERROR" , "onFailure error : " + error.toString() + "content : " + content);
			}
		});


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
					controllaBarcode(scanResult.getContents());
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
	
	private void controllaBarcode(String barcode) {
		// far partire la richiesta di check per il server.
		System.out.println("sssssssss");
		
		MyHttpClient.get("/inserzione/checkbarcode/" + barcode, null, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONArray response) {
				Log.v("DEBUG", "onSuccess: " + response.toString());
				aggiornaUi(response);
			}

			@Override
			public void onFailure(Throwable error, String content) {
				Log.v("ERROR" , "onFailure error : " + error.toString() + "content : " + content);
			}
			
		});
		
		
	}
	
	private void aggiornaUi(JSONArray response) {
		JSONObject jsonObj = null;
		try {
			Log.v("DEBUG", "FAIL: " + response.toString());
			jsonObj = response.getJSONObject(0);
			if(jsonObj.getBoolean("trovato"))
			{
				//prodotto trovato
				EditText tmp = (EditText) rootView.findViewById(R.id.ip_et_descrizione);
				et_descrizione.setText(jsonObj.getString("descrizione")); 
				// settare anche categoria e sottocategoria
			}
			else
				// prodotto non trovato
				Toast.makeText(getActivity().getApplicationContext(), "Prodotto non trovato nei nostri database!", Toast.LENGTH_SHORT).show();
			
			
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
	}
	
	private void aggiornaSpinnerCategorie(JSONArray response) {
		JSONObject jsonObj = null;
		try {
			ArrayList<String> categorieArray = new ArrayList<String>();
			for (int i = 0; i < response.length(); i++) 
				categorieArray.add(response.getString(i));
			
			ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categorieArray);
			spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spin_categoria.setAdapter(spinnerArrayAdapter);
			spin_categoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					richiediSottocategorie(parent.getItemAtPosition(position).toString());
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
				}});
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void richiediSottocategorie(String categoria) {
		System.out.println(categoria);
		MyHttpClient.get("/inserzione/getSottoCategorie/" + categoria , null, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONArray response) {
				JSONObject jsonObj = null;
				try {
					ArrayList<String> sottocategorieArray = new ArrayList<String>();
					for (int i = 0; i < response.length(); i++) 
						sottocategorieArray.add(response.getString(i));
					
					ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, sottocategorieArray);
					spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spin_sottocategoria.setAdapter(spinnerArrayAdapter);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
			
			@Override
			public void onFailure(Throwable error, String content) {
				Log.v("ERROR" , "onFailure error : " + error.toString() + "content : " + content);
			}
		});
		
	}

}
	
