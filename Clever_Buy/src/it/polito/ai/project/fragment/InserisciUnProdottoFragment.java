package it.polito.ai.project.fragment;

import it.polito.ai.project.R;
import it.polito.ai.project.adapter.SupermercatoCustomAdapter;
import it.polito.ai.project.main.MainActivity;
import it.polito.ai.project.main.MyHttpClient;
import it.polito.ai.project.model.Supermercato;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class InserisciUnProdottoFragment extends Fragment {

	protected static final int RESULT_INTENT_CAMERA = 0;
	protected static final int RESULT_ENABLE_GPS = 1;

	private ImageButton ib_scan, ib_foto;
	private ImageView iv_foto;
	private EditText et_descrizione, et_prezzo, et_barcode_number, et_valore_argomento;
	private Button btn_inserisci, btn_reset;
	private DatePicker dp_data_inizio, dp_data_fine;
	private Spinner spin_categoria, spin_sottocategoria, spin_supermercato, spin_argomento;
	private View rootView;
	private Switch switch_data_fine, switch_ulteriori_dettagli;
	private LocationManager locationManager;


	private ArrayList<Supermercato> supermercatiArrayList;
	private SupermercatoCustomAdapter supermercatiCustomAdapter;
	private ArrayAdapter<String> categoriaSpinnerArrayAdapter;
	private ArrayAdapter<String> sottocategoriaSpinnerArrayAdapter;

	private Bitmap bitmapFoto;

	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView 			= inflater.inflate(R.layout.fragment_inserisci_un_prodotto, container, false);

		ib_foto 			= (ImageButton) rootView.findViewById(R.id.ip_ib_foto);
		iv_foto 			= (ImageView) rootView.findViewById(R.id.ip_iv_foto);
		ib_scan 			= (ImageButton) rootView.findViewById(R.id.ip_ib_scan);

		btn_inserisci 		= (Button) rootView.findViewById(R.id.ip_btn_inserisci);
		btn_reset			= (Button) rootView.findViewById(R.id.ip_btn_reset);

		et_descrizione  	= (EditText) rootView.findViewById(R.id.ip_et_descrizione);
		et_barcode_number  	= (EditText) rootView.findViewById(R.id.ip_et_barcode_number);
		et_prezzo			= (EditText) rootView.findViewById(R.id.ip_et_prezzo);
		et_valore_argomento = (EditText) rootView.findViewById(R.id.ip_et_valore_argomento);

		dp_data_inizio 		= (DatePicker) rootView.findViewById(R.id.ip_dp_data_inizio);
		dp_data_fine    	= (DatePicker) rootView.findViewById(R.id.ip_dp_data_fine);

		spin_categoria 		= (Spinner) rootView.findViewById(R.id.ip_spin_categoria);
		spin_sottocategoria = (Spinner) rootView.findViewById(R.id.ip_spin_sottocategoria);
		spin_supermercato 	= (Spinner) rootView.findViewById(R.id.ip_spin_supermercato);
		spin_argomento 		= (Spinner) rootView.findViewById(R.id.ip_spin_argomento);

		switch_data_fine = (Switch) rootView.findViewById(R.id.ip_switch_data_fine);
		switch_ulteriori_dettagli = (Switch) rootView.findViewById(R.id.ip_switch_ulteriori_dettagli);

		addListeners();
		nascondiAnnoDatePicker();
		ottieniCategorie();

		supermercatiArrayList = new ArrayList<Supermercato>();


		ottieniSupermercati(MainActivity.getLocation().getLatitude(), MainActivity.getLocation().getLongitude());
		dp_data_fine.setEnabled(false);
		return rootView;
	}



	private void nascondiAnnoDatePicker() {
		try {
			Field f[] = dp_data_inizio.getClass().getDeclaredFields();
			for (Field field : f) {
				if (field.getName().equals("mYearSpinner")) {
					field.setAccessible(true);
					Object yearPickerInizio = new Object();
					Object yearPickerFine = new Object();

					yearPickerInizio = field.get(dp_data_inizio);
					yearPickerFine = field.get(dp_data_fine);
					((View) yearPickerInizio).setVisibility(View.GONE);
					((View) yearPickerFine).setVisibility(View.GONE);
				}
			}
		} catch (SecurityException e) {
			Log.d("ERROR", e.getMessage());
		} 
		catch (IllegalArgumentException e) {
			Log.d("ERROR", e.getMessage());
		} catch (IllegalAccessException e) {
			Log.d("ERROR", e.getMessage());
		}		
	}

	private void ottieniCategorie() {
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
	}

	private void addListeners() {

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
				RequestParams params = new RequestParams();

				/* DESCRIZIONE */
				if(et_descrizione.getText().toString() == null || et_descrizione.getText().toString().matches("")) {
					Toast.makeText(getActivity().getBaseContext(), "Descrizione del prodotto mancante!", Toast.LENGTH_SHORT).show();
					return;
				}
				params.put("descrizione", et_descrizione.getText().toString());

				/* BAR CODE */
				if(et_barcode_number.getText().toString() == null || et_barcode_number.getText().toString().matches("")) {
					Toast.makeText(getActivity().getBaseContext(), "Codice a barre del prodotto mancante", Toast.LENGTH_SHORT).show();
					return;
				}
				params.put("codiceBarre", et_barcode_number.getText().toString());

				/* CATEGORIA */
				params.put("categoria", spin_categoria.getSelectedItem().toString());

				/* SOTTOCATEGORIA */
				params.put("sottocategoria", spin_sottocategoria.getSelectedItem().toString());


				/* FOTO */
				if(bitmapFoto == null) {
					Toast.makeText(getActivity().getBaseContext(), "Foto del prodotto mancante", Toast.LENGTH_SHORT).show();
					return;
				}

				ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
				bitmapFoto.compress(CompressFormat.JPEG, 50/*ignored for PNG*/, bos); 
				byte[] bitmapdata = bos.toByteArray();
				//				ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);
				String encodedPicture = Base64.encodeToString(bitmapdata, Base64.DEFAULT);
				params.put("foto", encodedPicture);


				/* DATA INIZIO */
				DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
				String data_inizio_str = dp_data_inizio.getDayOfMonth() + "/" + (dp_data_inizio.getMonth() + 1) + "/" + dp_data_inizio.getYear();
				DateTime data_inizio = formatter.parseDateTime(data_inizio_str);

				/* DATA FINE */
				String data_fine_str = null;
				DateTime data_fine = null;
				if(switch_data_fine.isChecked()) {
					data_fine_str = dp_data_fine.getDayOfMonth() + "/" + (dp_data_fine.getMonth() + 1) + "/" + dp_data_fine.getYear();
					data_fine = formatter.parseDateTime(data_fine_str);
				}
				else{
					data_fine = data_inizio.plusDays(14);
					data_fine_str = DateTimeFormat.forPattern("dd/MM/yyyy").print(data_fine);
				}

				if(Days.daysBetween(data_inizio, data_fine).getDays() > 20) {
					Toast.makeText(getActivity().getBaseContext(), "Inserzione troppo lunga!", Toast.LENGTH_SHORT).show();
					return;
				} else if(Days.daysBetween(data_inizio, data_fine).getDays() < 5){
					Toast.makeText(getActivity().getBaseContext(), "Inserzione troppo breve!", Toast.LENGTH_SHORT).show();
					return;
				} else if(Days.daysBetween(new DateTime(), data_fine).getDays() < 5) {
					Toast.makeText(getActivity().getBaseContext(), "Fine inserzione troppo vicina!", Toast.LENGTH_SHORT).show();
					return;
				} else {
					params.put("data_inizio", data_inizio_str);
					params.put("data_fine", data_fine_str);
				}

				/* PREZZO */
				if(et_prezzo.getText().toString() == null || et_prezzo.getText().toString().matches("")) {
					Toast.makeText(getActivity().getBaseContext(), "Prezzo del prodotto mancante", Toast.LENGTH_SHORT).show();
					return;
				}
				params.put("prezzo", et_prezzo.getText().toString());

				/* SUPERMERCATO */
				Supermercato s = (Supermercato) spin_supermercato.getSelectedItem();
				params.put("supermercato", Integer.toString(s.getId()));

				/* ULTERIORI DETTAGLI*/
				if(switch_ulteriori_dettagli.isChecked())
					if(et_valore_argomento.getText().toString() == null || et_valore_argomento.getText().toString().matches("")) {
						Toast.makeText(getActivity().getBaseContext(), "Valore del dettaglio mancante", Toast.LENGTH_SHORT).show();
						return;
					} else {
						params.put("argomento", spin_argomento.getSelectedItem().toString());
						params.put("valore_argomento", et_valore_argomento.getText().toString());
					}

				/* INVIO DATI! */
				MyHttpClient.setBasicAuth("zorro@zorro.it", "zorro");
				MyHttpClient.post("/inserzione/aggiungi", params , new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(JSONArray response) {
						Toast.makeText(getActivity(), "Inserzione aggiunta con successo!", Toast.LENGTH_LONG).show();
						resetVista();
						Log.v("DEBUG", "tuttobene!" + response.toString());
					}

					@Override
					public void onFailure(Throwable error, String content) {
						Log.v("ERROR" , "onFailure error : " + error.toString() + "content : " + content);
					}
				});
			}
		});

		OnClickListener data_inizio_fine = new OnClickListener() { 			
			@Override
			public void onClick(View v) {
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, dp_data_inizio.getYear());
				cal.set(Calendar.MONTH, dp_data_inizio.getMonth());
				cal.set(Calendar.DAY_OF_MONTH, dp_data_inizio.getDayOfMonth());
				dp_data_fine.setMinDate(cal.getTimeInMillis());
			}
		}; 

		dp_data_inizio.setOnClickListener(data_inizio_fine);
		dp_data_fine.setOnClickListener(data_inizio_fine);

		ib_foto.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, RESULT_INTENT_CAMERA);
			}
		});

		switch_data_fine.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) 
					dp_data_fine.setEnabled(true);
				else
					dp_data_fine.setEnabled(false);
			}
		});

		switch_ulteriori_dettagli.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				System.out.println("changed!");
				if (isChecked) {
					// faccio partire una richiesta verso il server per avere tutti gli argomenti disponibili.
					if(spin_argomento.getChildCount() == 0)
						MyHttpClient.get("/inserzione/getArgomenti", null, new JsonHttpResponseHandler() {
							public void onSuccess(JSONArray response) {
								Log.v("DEBUG", "onSuccess : " + response.toString());
								ArrayList<String> argomentiArrayList = new ArrayList<String>();
								for (int i = 0; i < response.length(); i++)
									try {
										argomentiArrayList.add(response.getString(i));
										ArrayAdapter<String> argomentiArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, argomentiArrayList);
										argomentiArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
										spin_argomento.setAdapter(argomentiArrayAdapter);

										spin_argomento.setEnabled(true);
										spin_argomento.setClickable(true);
										et_valore_argomento.setEnabled(true);
										et_valore_argomento.setFocusable(true);
									} catch (JSONException e) {
										e.printStackTrace();
									}
							}

							@Override
							public void onFailure(Throwable error, String content) {
								Log.v("DEBUG" , "onFailure : " + error.toString() + "content : " + content);
							}
						});

				}
				else {
					spin_argomento.setEnabled(false);
					spin_argomento.setClickable(false);
					et_valore_argomento.setEnabled(false);
					et_valore_argomento.setFocusable(false);
				}
			}
		});

		btn_reset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(getActivity())
				.setTitle("Reset?")
				.setMessage("Vuoi resettare tutti i campi?")
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) { 
						resetVista();
					}
				})
				.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) { 
					}
				})
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setCancelable(false)
				.show();

			}
		});
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch (requestCode) {
		case IntentIntegrator.REQUEST_CODE:	// barcode scanner
			IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
			if(resultCode != Activity.RESULT_CANCELED && scanResult != null) {
				et_barcode_number.setText(scanResult.getContents());
				controllaBarcode(scanResult.getContents());
			}
			break;
		case RESULT_INTENT_CAMERA: 
			super.onActivityResult(requestCode, resultCode, intent);
			if(resultCode != Activity.RESULT_CANCELED) {
				bitmapFoto = (Bitmap) intent.getExtras().get("data");
				iv_foto.setImageBitmap(bitmapFoto);
			}
			break;
		} // switch
	}

	private void controllaBarcode(String barcode) {
		MyHttpClient.get("/inserzione/checkbarcode/" + barcode, null, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONArray response) {
				Log.v("DEBUG", "onSuccess : " + response.toString());
				aggiornaUi(response);
			}

			@Override
			public void onFailure(Throwable error, String content) {
				Log.v("DEBUG" , "onFailure : " + error.toString() + "content : " + content);
			}
		});
	}

	private void aggiornaUi(JSONArray response) {
		JSONObject jsonObj = null;
		try {
			jsonObj = response.getJSONObject(0);
			if(jsonObj.getBoolean("trovato")) {

				// prodotto trovato
				et_descrizione.setText(jsonObj.getString("descrizione")); 

				int position = 0;
				position = categoriaSpinnerArrayAdapter.getPosition(jsonObj.getString("categoria"));
				spin_categoria.setSelection(position);
				spin_categoria.setEnabled(false);
				spin_categoria.setClickable(false);

				ArrayList<String> tmp = new ArrayList<String>();
				tmp.add(jsonObj.getString("sottocategoria"));
				sottocategoriaSpinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, tmp);
				sottocategoriaSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

				spin_sottocategoria.setEnabled(false);
				spin_sottocategoria.setClickable(false);



			} else {
				// prodotto non trovato
				Toast.makeText(getActivity().getBaseContext(), "Prodotto non trovato nei nostri database!", Toast.LENGTH_SHORT).show();

				et_descrizione.setText(""); 
				spin_categoria.setEnabled(true);
				spin_categoria.setClickable(true);
				spin_sottocategoria.setEnabled(true);
				spin_sottocategoria.setClickable(true);

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void aggiornaSpinnerCategorie(JSONArray response) {
		try {
			ArrayList<String> categorieArray = new ArrayList<String>();
			for (int i = 0; i < response.length(); i++) 
				categorieArray.add(response.getString(i));

			categoriaSpinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categorieArray);
			categoriaSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spin_categoria.setAdapter(categoriaSpinnerArrayAdapter);
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

	private void ottieniSupermercati(double lat, double lng) {
		RequestParams params = new RequestParams();
		params.add("lat", Double.toString(lat));
		params.add("lng", Double.toString(lng));
		MyHttpClient.get("/inserzione/getSupermercati", params, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONArray response) {
				JSONObject jsonObj = null;

				try {
					for (int i = 0; i < response.length(); i++) 
						supermercatiArrayList.add(new Supermercato(
								Integer.valueOf(response.getJSONObject(i).getInt("id")), 
								response.getJSONObject(i).getString("nome"), 
								response.getJSONObject(i).getString("indirizzo"),
								Float.valueOf(response.getJSONObject(i).getLong("distanza"))
								));

					supermercatiCustomAdapter = new SupermercatoCustomAdapter(getActivity(), R.layout.supermercato_custom_spinner, supermercatiArrayList);

					spin_supermercato.setAdapter(supermercatiCustomAdapter);

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

	private void resetVista() {
		et_descrizione.setText("");
		et_barcode_number.setText("");
		et_prezzo.setText("");

		spin_categoria.setSelection(0);
		spin_categoria.setEnabled(true);
		spin_categoria.setClickable(true);
		spin_sottocategoria.setEnabled(true);
		spin_sottocategoria.setClickable(true);

		spin_supermercato.setSelection(0);

		iv_foto.setImageResource(R.drawable.no);
		dp_data_inizio.updateDate(DateTime.now().getYear(), DateTime.now().getMonthOfYear(), DateTime.now().getDayOfMonth());


		if(switch_data_fine.isChecked()) {
			dp_data_fine.updateDate(DateTime.now().getYear(), DateTime.now().getMonthOfYear(), DateTime.now().getDayOfMonth());
			switch_data_fine.setChecked(false);
		}
		if(switch_ulteriori_dettagli.isChecked()) {
			switch_ulteriori_dettagli.setChecked(false);
		}
	}
}

