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
import java.util.HashMap;
import java.util.Iterator;

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
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
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
	private TextView tv_barcode_number;
	private EditText et_descrizione, et_prezzo, et_valore_argomento;
	private Button btn_inserisci, btn_reset;
	private DatePicker dp_data_inizio, dp_data_fine;
	private Spinner spin_categoria, spin_sottocategoria, spin_supermercato, spin_argomento;
	private View rootView;
	private Switch switch_data_fine, switch_ulteriori_dettagli;

	private ProgressDialog progressDialog;

	private ArrayList<Supermercato> supermercatiArrayList;
	private SupermercatoCustomAdapter supermercatiCustomAdapter;
	private ArrayAdapter<String> categoriaSpinnerArrayAdapter;
	private ArrayAdapter<String> sottocategoriaSpinnerArrayAdapter;

	private HashMap<String, ArrayList<String>> categoriaSottocategoriaMap;
	//private Bitmap bitmapFoto;

	private boolean modificaInserzione = false;
	private Integer idInserzioneDaModificare;

	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		Bundle bundle = this.getArguments();

		if(bundle != null && bundle.containsKey("idInserzione")) {
			modificaInserzione = true;
			idInserzioneDaModificare = bundle.getInt("idInserzione");
		}

		rootView 			= inflater.inflate(R.layout.fragment_inserisci_un_prodotto, container, false);

		ib_foto 			= (ImageButton) rootView.findViewById(R.id.ip_ib_foto);
		iv_foto 			= (ImageView) rootView.findViewById(R.id.ip_iv_foto);
		ib_scan 			= (ImageButton) rootView.findViewById(R.id.ip_ib_scan);

		btn_inserisci 		= (Button) rootView.findViewById(R.id.ip_btn_inserisci);
		btn_reset			= (Button) rootView.findViewById(R.id.ip_btn_reset);

		et_descrizione  	= (EditText) rootView.findViewById(R.id.ip_et_descrizione);
		tv_barcode_number  	= (TextView) rootView.findViewById(R.id.ip_et_barcode_number);
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


		categoriaSottocategoriaMap = new HashMap<String, ArrayList<String>>();
		addListeners();
		nascondiAnnoDatePicker();
		getCategorieSottocategorie();


		supermercatiArrayList = new ArrayList<Supermercato>();
		getSupermercati(MainActivity.getLocation().getLatitude(), MainActivity.getLocation().getLongitude());

		dp_data_fine.setEnabled(false);

		if(modificaInserzione) {
			getInserzioneById(bundle.getInt("idInserzione"));
			progressDialog = ProgressDialog.show(getActivity(), "Download..", "Sto scaricando i dati relativi all'inserzione da modificare...");
		}

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

	private void getCategorieSottocategorie() {
		MyHttpClient.get("/inserzione/getCategorieSottocategorie", null, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(JSONArray response) {
				setSpinnerCategorieSottocategorie(response);
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
				if(tv_barcode_number.getText().toString() == null || tv_barcode_number.getText().toString().matches("")) {
					Toast.makeText(getActivity().getBaseContext(), "Codice a barre del prodotto mancante", Toast.LENGTH_SHORT).show();
					return;
				}
				params.put("codiceBarre", tv_barcode_number.getText().toString());

				/* CATEGORIA */
				params.put("categoria", spin_categoria.getSelectedItem().toString());

				/* SOTTOCATEGORIA */
				params.put("sottocategoria", spin_sottocategoria.getSelectedItem().toString());


				/* FOTO */
				if(iv_foto.getDrawable() == null) {
					Toast.makeText(getActivity().getBaseContext(), "Foto del prodotto mancante", Toast.LENGTH_SHORT).show();
					return;
				}

				ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
				Bitmap bitmap = ((BitmapDrawable) iv_foto.getDrawable()).getBitmap();
				bitmap.compress(CompressFormat.JPEG, 50/*ignored for PNG*/, bos); 
				byte[] bitmapdata = bos.toByteArray();
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

				/* ULTERIORI DETTAGLI */
				if(switch_ulteriori_dettagli.isChecked())
					if(et_valore_argomento.getText().toString() == null || et_valore_argomento.getText().toString().matches("")) {
						Toast.makeText(getActivity().getBaseContext(), "Valore del dettaglio mancante", Toast.LENGTH_SHORT).show();
						return;
					} else {
						params.put("argomento", spin_argomento.getSelectedItem().toString());
						params.put("valore_argomento", et_valore_argomento.getText().toString());
					}


				/* CHECK IF modificaInserzione */
				if(modificaInserzione) {
					params.put("modificaInserzione", String.valueOf(modificaInserzione)); 
					params.put("idInserzione", String.valueOf(idInserzioneDaModificare));
				}
				else
					params.put("modificaInserzione", String.valueOf(false));

				/* SHOW OVERLAY PANEL*/
				showOverlayPanel();

				/* INVIO DATI! */
				MyHttpClient.post("/inserzione/aggiungi", params , new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(JSONArray response) {
						hideOverlayPanel();

						try {
							JSONObject jsonObj = response.getJSONObject(0);
							if(jsonObj.getBoolean("modificaInserzione") == false) {
								resetVista();
								AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
								builder.setTitle("Inserimento");
								builder.setMessage("Inserimento dell'inserzione avvenuta con successo!");
								builder.setCancelable(false);
								builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										dialog.cancel();
									}
								});

								AlertDialog alert = builder.create();
								alert.show();
							}
							else {
								AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
								builder.setTitle("Modifica");
								builder.setMessage("Modifica dell'inserzione avvenuta con successo!");
								builder.setCancelable(false);
								builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										dialog.cancel();
										((MainActivity) getActivity()).displayView(3);
									}
								});

								AlertDialog alert = builder.create();
								alert.show();

							}
						} catch (JSONException e) {
							e.printStackTrace();
						}


					}

					@Override
					public void onFailure(Throwable error, String content) {
						hideOverlayPanel();
						Toast.makeText(getActivity(), "Uffa, si è verificato un errore con il server. Riprova più tardi!", Toast.LENGTH_LONG).show();
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
				if(!modificaInserzione) {

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
				else {
					((MainActivity) getActivity()).displayView(3); // ritorno alla pagina delle mie inserzioni
				}

			}
		});
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch (requestCode) {
		case IntentIntegrator.REQUEST_CODE:	// barcode scanner
			IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
			if(resultCode != Activity.RESULT_CANCELED && scanResult != null) {
				tv_barcode_number.setText(scanResult.getContents());
				controllaBarcode(scanResult.getContents());
			}
			break;
		case RESULT_INTENT_CAMERA: 
			super.onActivityResult(requestCode, resultCode, intent);
			if(resultCode != Activity.RESULT_CANCELED) {
				Bitmap bitmapFoto = (Bitmap) intent.getExtras().get("data");
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
				Toast.makeText(getActivity().getBaseContext(), "Prodotto trovato nei nostri database!", Toast.LENGTH_SHORT).show();

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

	private void setSpinnerCategorieSottocategorie(JSONArray response) {
		try {
			JSONObject jsonObj = response.getJSONObject(0);
			for(Iterator<String> iter = jsonObj.keys(); iter.hasNext(); ) {
				String categoria = iter.next();
				categoriaSottocategoriaMap.put(categoria, new ArrayList<String>());
				for(int i = 0; i < jsonObj.getJSONArray(categoria).length(); i++) 
					categoriaSottocategoriaMap.get(categoria).add((String) jsonObj.getJSONArray(categoria).get(i));

			}

			categoriaSpinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, new ArrayList<String>(categoriaSottocategoriaMap.keySet()));
			categoriaSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spin_categoria.setAdapter(categoriaSpinnerArrayAdapter);

			spin_categoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					ArrayAdapter<String> sottocategorieArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categoriaSottocategoriaMap.get(parent.getItemAtPosition(position).toString()));
					sottocategorieArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spin_sottocategoria.setAdapter(sottocategorieArrayAdapter);
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
				}});

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void getSupermercati(double lat, double lng) {
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


	private void showOverlayPanel() {
		progressDialog = ProgressDialog.show(getActivity(), "Caricamento", "Upload dei dati dell'inserzione...", false);
	}

	private void hideOverlayPanel() {
		progressDialog.dismiss();
	}

	private void resetVista() {
		et_descrizione.setText("");
		tv_barcode_number.setText("");
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
		ScrollView sv = (ScrollView) rootView.findViewById(R.id.fragment_inserisci_un_prodotto_scrollview);
		sv.fullScroll(View.FOCUS_UP);
		et_descrizione.requestFocus();
	}

	private void getInserzioneById(Integer idInserzione) {
		RequestParams params = new RequestParams();
		params.put("idInserzione", String.valueOf(idInserzione));
		MyHttpClient.get("/inserzione/modifica/getInserzioneById", params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray response) {
				setDatiInserzione(response);
			}

			@Override
			public void onFailure(Throwable error, String content) {
				Log.v("ERROR" , "onFailure error : " + error.toString() + "content : " + content);
			}
		});
	}

	private void setDatiInserzione(JSONArray response) {
		try {
			et_descrizione.setText(response.getJSONObject(0).getString("descrizione"));
			tv_barcode_number.setText(response.getJSONObject(0).getString("codiceBarre"));
			et_prezzo.setText(response.getJSONObject(0).getString("prezzo"));
			for(int i = 0; i < spin_supermercato.getCount(); i++) {
				Supermercato s = (Supermercato) spin_supermercato.getItemAtPosition(i);
				if(s.getId() == response.getJSONObject(0).getInt("supermercato")) {
					spin_supermercato.setSelection(i);
					break;
				}
			}
			System.out.println("CAT: " + response.getJSONObject(0).getString("categoria"));
			System.out.println("\t" + response.getJSONObject(0).getString("sottocategoria"));

			
			int i = categoriaSpinnerArrayAdapter.getPosition(response.getJSONObject(0).getString("categoria"));
			spin_categoria.setSelection(i);
			
			sottocategoriaSpinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categoriaSottocategoriaMap.get(response.getJSONObject(0).getString("categoria")));
			sottocategoriaSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spin_sottocategoria.setAdapter(sottocategoriaSpinnerArrayAdapter);
			i = sottocategoriaSpinnerArrayAdapter.getPosition(response.getJSONObject(0).getString("sottocategoria"));
			spin_sottocategoria.setSelection(i);
			 
			DateTime data_inizio = DateTimeFormat.forPattern("yyyy/MM/dd").parseDateTime(response.getJSONObject(0).getString("data_inizio"));
			System.out.println(data_inizio.toString());
			dp_data_inizio.updateDate(data_inizio.getYear(), data_inizio.getMonthOfYear()-1, data_inizio.getDayOfMonth());

			DateTime data_fine = DateTimeFormat.forPattern("yyyy/MM/dd").parseDateTime(response.getJSONObject(0).getString("data_fine"));
			dp_data_fine.updateDate(data_fine.getYear(), data_fine.getMonthOfYear()-1, data_fine.getDayOfMonth());

			switch_data_fine.setChecked(true);

			byte[] decodedString = Base64.decode(response.getJSONObject(0).getString("foto"), Base64.DEFAULT);
			Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length); 
			iv_foto.setImageBitmap(decodedByte);


			btn_inserisci.setText("Modifica!");
			btn_reset.setText("Annulla");

			progressDialog.dismiss();

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}

