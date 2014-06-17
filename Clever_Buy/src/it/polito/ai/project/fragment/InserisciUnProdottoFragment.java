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

	private ImageButton _ib_scan, _ib_foto;
	private ImageView _iv_foto;
	private EditText _et_descrizione, _et_prezzo, _et_barcode_number, _et_valore_argomento;
	private Button _btn_inserisci, _btn_reset;
	private DatePicker _dp_data_inizio, _dp_data_fine;
	private Spinner _spin_categoria, _spin_sottocategoria, _spin_supermercato, _spin_argomento;
	private View rootView;
	private Switch _switch_data_fine, _switch_ulteriori_dettagli;
	
	private ArrayList<Supermercato> supermercatiArrayList;
	private SupermercatoCustomAdapter supermercatiCustomAdapter;
	private ArrayAdapter<String> categoriaSpinnerArrayAdapter;
	private ArrayAdapter<String> sottocategoriaSpinnerArrayAdapter;
	

	private Bitmap bitmapFoto;

	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {

		rootView 			= inflater.inflate(R.layout.fragment_inserisci_un_prodotto, container, false);

		_ib_foto 			= (ImageButton) rootView.findViewById(R.id.ip_ib_foto);
		_iv_foto 			= (ImageView) rootView.findViewById(R.id.ip_iv_foto);
		_ib_scan 			= (ImageButton) rootView.findViewById(R.id.ip_ib_scan);

		_btn_inserisci 		= (Button) rootView.findViewById(R.id.ip_btn_inserisci);
		_btn_reset			= (Button) rootView.findViewById(R.id.ip_btn_reset);
		
		_et_descrizione  	= (EditText) rootView.findViewById(R.id.ip_et_descrizione);
		_et_barcode_number  	= (EditText) rootView.findViewById(R.id.ip_et_barcode_number);
		_et_prezzo			= (EditText) rootView.findViewById(R.id.ip_et_prezzo);
		_et_valore_argomento = (EditText) rootView.findViewById(R.id.ip_et_valore_argomento);

		_dp_data_inizio 		= (DatePicker) rootView.findViewById(R.id.ip_dp_data_inizio);
		_dp_data_fine    	= (DatePicker) rootView.findViewById(R.id.ip_dp_data_fine);

		_spin_categoria 		= (Spinner) rootView.findViewById(R.id.ip_spin_categoria);
		_spin_sottocategoria = (Spinner) rootView.findViewById(R.id.ip_spin_sottocategoria);
		_spin_supermercato 	= (Spinner) rootView.findViewById(R.id.ip_spin_supermercato);
		_spin_argomento 		= (Spinner) rootView.findViewById(R.id.ip_spin_argomento);

		_switch_data_fine = (Switch) rootView.findViewById(R.id.ip_switch_data_fine);
		_switch_ulteriori_dettagli = (Switch) rootView.findViewById(R.id.ip_switch_ulteriori_dettagli);

		addListeners();
		nascondiAnnoDatePicker();
		ottieniCategorie();

		supermercatiArrayList = new ArrayList<Supermercato>();


		ottieniSupermercati(MainActivity.getLocation().getLatitude(), MainActivity.getLocation().getLongitude());
		_dp_data_fine.setEnabled(false);
		
		
		return rootView;
	}



	private void nascondiAnnoDatePicker() {
		try {
			Field f[] = _dp_data_inizio.getClass().getDeclaredFields();
			for (Field field : f) {
				if (field.getName().equals("mYearSpinner")) {
					field.setAccessible(true);
					Object yearPickerInizio = new Object();
					Object yearPickerFine = new Object();

					yearPickerInizio = field.get(_dp_data_inizio);
					yearPickerFine = field.get(_dp_data_fine);
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

		_ib_scan.setOnClickListener(new OnClickListener() {
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

		_btn_inserisci.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RequestParams params = new RequestParams();

				/* DESCRIZIONE */
				if(_et_descrizione.getText().toString() == null || _et_descrizione.getText().toString().matches("")) {
					Toast.makeText(getActivity().getBaseContext(), "Descrizione del prodotto mancante!", Toast.LENGTH_SHORT).show();
					return;
				}
				params.put("descrizione", _et_descrizione.getText().toString());

				/* BAR CODE */
				if(_et_barcode_number.getText().toString() == null || _et_barcode_number.getText().toString().matches("")) {
					Toast.makeText(getActivity().getBaseContext(), "Codice a barre del prodotto mancante", Toast.LENGTH_SHORT).show();
					return;
				}
				params.put("codiceBarre", _et_barcode_number.getText().toString());

				/* CATEGORIA */
				params.put("categoria", _spin_categoria.getSelectedItem().toString());

				/* SOTTOCATEGORIA */
				params.put("sottocategoria", _spin_sottocategoria.getSelectedItem().toString());


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
				String data_inizio_str = _dp_data_inizio.getDayOfMonth() + "/" + (_dp_data_inizio.getMonth() + 1) + "/" + _dp_data_inizio.getYear();
				DateTime data_inizio = formatter.parseDateTime(data_inizio_str);

				/* DATA FINE */
				String data_fine_str = null;
				DateTime data_fine = null;
				if(_switch_data_fine.isChecked()) {
					data_fine_str = _dp_data_fine.getDayOfMonth() + "/" + (_dp_data_fine.getMonth() + 1) + "/" + _dp_data_fine.getYear();
					data_fine = formatter.parseDateTime(data_fine_str);
				}
				else{
					data_fine = data_inizio.plusDays(14);
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
				if(_et_prezzo.getText().toString() == null || _et_prezzo.getText().toString().matches("")) {
					Toast.makeText(getActivity().getBaseContext(), "Prezzo del prodotto mancante", Toast.LENGTH_SHORT).show();
					return;
				}
				params.put("prezzo", _et_prezzo.getText().toString());

				/* SUPERMERCATO */
				Supermercato s = (Supermercato) _spin_supermercato.getSelectedItem();
				params.put("supermercato", Integer.toString(s.getId()));

				/* ULTERIORI DETTAGLI*/
				if(_switch_ulteriori_dettagli.isChecked())
					if(_et_valore_argomento.getText().toString() == null || _et_valore_argomento.getText().toString().matches("")) {
						Toast.makeText(getActivity().getBaseContext(), "Valore del dettaglio mancante", Toast.LENGTH_SHORT).show();
						return;
					} else {
						params.put("argomento", _spin_argomento.getSelectedItem().toString());
						params.put("valore_argomento", _et_valore_argomento.getText().toString());
					}
				
				/* INVIO DATI! */
				MyHttpClient.post("/inserzione/aggiungi", params , new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(JSONArray response) {
						System.out.println("tuttobene " + response.toString());
						Log.v("DEBUG", "tuttobene!" + response.toString());
						Toast.makeText(getActivity().getBaseContext(), "Perfetto! Inserzione inserita con successo. GRAZIE!", Toast.LENGTH_SHORT).show();
						resetVista();
					}

					@Override
					public void onFailure(Throwable error, String content) {
						Log.v("ERROR" , "onFailure error : " + error.toString() + "content : " + content);
						Toast.makeText(getActivity().getBaseContext(), "Ops! Si è verificato un errore.", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});

		OnClickListener data_inizio_fine = new OnClickListener() { 			
			@Override
			public void onClick(View v) {
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, _dp_data_inizio.getYear());
				cal.set(Calendar.MONTH, _dp_data_inizio.getMonth());
				cal.set(Calendar.DAY_OF_MONTH, _dp_data_inizio.getDayOfMonth());
				_dp_data_fine.setMinDate(cal.getTimeInMillis());
			}
		}; 

		_dp_data_inizio.setOnClickListener(data_inizio_fine);
		_dp_data_fine.setOnClickListener(data_inizio_fine);

		_ib_foto.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, RESULT_INTENT_CAMERA);
			}
		});

		_switch_data_fine.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) 
					_dp_data_fine.setEnabled(true);
				else
					_dp_data_fine.setEnabled(false);
			}
		});

		_switch_ulteriori_dettagli.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				System.out.println("changed!");
				if (isChecked) {
					// faccio partire una richiesta verso il server per avere tutti gli argomenti disponibili.
					if(_spin_argomento.getChildCount() == 0)
						MyHttpClient.get("/inserzione/getArgomenti", null, new JsonHttpResponseHandler() {
							public void onSuccess(JSONArray response) {
								Log.v("DEBUG", "onSuccess : " + response.toString());
								ArrayList<String> argomentiArrayList = new ArrayList<String>();
								for (int i = 0; i < response.length(); i++)
									try {
										argomentiArrayList.add(response.getString(i));
										ArrayAdapter<String> argomentiArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, argomentiArrayList);
										argomentiArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
										_spin_argomento.setAdapter(argomentiArrayAdapter);

										_spin_argomento.setEnabled(true);
										_spin_argomento.setClickable(true);
										_et_valore_argomento.setEnabled(true);
										_et_valore_argomento.setFocusable(true);
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
					_spin_argomento.setEnabled(false);
					_spin_argomento.setClickable(false);
					_et_valore_argomento.setEnabled(false);
					_et_valore_argomento.setFocusable(false);
				}
			}
		});
		
		_btn_reset.setOnClickListener(new OnClickListener() {
			
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
				_et_barcode_number.setText(scanResult.getContents());
				controllaBarcode(scanResult.getContents());
			}
			break;
		case RESULT_INTENT_CAMERA: 
			super.onActivityResult(requestCode, resultCode, intent);
			bitmapFoto = (Bitmap) intent.getExtras().get("data");
			_iv_foto.setImageBitmap(bitmapFoto);
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
				_et_descrizione.setText(jsonObj.getString("descrizione")); 
				
				int position = 0;
				position = categoriaSpinnerArrayAdapter.getPosition(jsonObj.getString("categoria"));
				_spin_categoria.setSelection(position);
				_spin_categoria.setEnabled(false);
				_spin_categoria.setClickable(false);
				
				position = sottocategoriaSpinnerArrayAdapter.getPosition(jsonObj.getString("sottocategoria"));
				_spin_sottocategoria.setSelection(position);
				_spin_sottocategoria.setEnabled(false);
				_spin_sottocategoria.setClickable(false);


			} else {
				// prodotto non trovato
				Toast.makeText(getActivity().getBaseContext(), "Prodotto non trovato nei nostri database!", Toast.LENGTH_SHORT).show();
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
			_spin_categoria.setAdapter(categoriaSpinnerArrayAdapter);
			_spin_categoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
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

					sottocategoriaSpinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, sottocategorieArray);
					sottocategoriaSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					_spin_sottocategoria.setAdapter(sottocategoriaSpinnerArrayAdapter);
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
				try {
					for (int i = 0; i < response.length(); i++) 
						supermercatiArrayList.add(new Supermercato(
								Integer.valueOf(response.getJSONObject(i).getInt("id")), 
								response.getJSONObject(i).getString("nome"), 
								response.getJSONObject(i).getString("indirizzo"),
								Float.valueOf(response.getJSONObject(i).getLong("distanza"))
								));

					supermercatiCustomAdapter = new SupermercatoCustomAdapter(getActivity(), R.layout.supermercato_custom_spinner, supermercatiArrayList);
					_spin_supermercato.setAdapter(supermercatiCustomAdapter);

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
		_et_descrizione.setText("");
		_et_barcode_number.setText("");
		_et_prezzo.setText("");

		_spin_categoria.setSelection(0);
		_spin_categoria.setEnabled(true);
		_spin_categoria.setClickable(true);
		_spin_sottocategoria.setEnabled(true);
		_spin_sottocategoria.setClickable(true);

		_spin_supermercato.setSelection(0);
		
		_iv_foto.setImageResource(R.drawable.no);
		_dp_data_inizio.updateDate(DateTime.now().getYear(), DateTime.now().getMonthOfYear(), DateTime.now().getDayOfMonth());
		
		
		if(_switch_data_fine.isChecked()) {
			_dp_data_fine.updateDate(DateTime.now().getYear(), DateTime.now().getMonthOfYear(), DateTime.now().getDayOfMonth());
			_switch_data_fine.setChecked(false);
		}
		if(_switch_ulteriori_dettagli.isChecked()) {
			_switch_ulteriori_dettagli.setChecked(false);
		}
	}

}

