package it.polito.ai.project.fragment;


import it.polito.ai.project.R;
import it.polito.ai.project.adapter.ItemAdapterListFragment;
import it.polito.ai.project.main.ItemListFragment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class ListFragment extends Fragment {

	private View rootView;
	private Spinner _spinner_allList, _spinner_item_quantity;
	private EditText _edit_item_name, _edit_item_quantity;
	private Button  _button_addItem, _button_barcode, _button_hint, _button_hint_close;
	private ListView _listView;

	private ArrayAdapter<String> allListSpinnerArrayAdapter;
	ItemAdapterListFragment itemAdapter;

	private boolean visualeHINTabilitata = false;   // uso quesa visuale per 
	public ListFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_list, container, false);

		_button_addItem = (Button) rootView.findViewById(R.id.button_addItem);
		_button_barcode = (Button) rootView.findViewById(R.id.button_addItem_byBarCode);
		_button_hint = (Button) rootView.findViewById(R.id.button_hint);
		_button_hint_close = (Button) rootView.findViewById(R.id.button_hint);

		_spinner_allList = (Spinner) rootView.findViewById(R.id.spinner_allList);
		_spinner_item_quantity = (Spinner) rootView.findViewById(R.id.spinner_item_quantity);

		_edit_item_name = (EditText) rootView.findViewById(R.id.edit_item_name);
		_edit_item_quantity = (EditText) rootView.findViewById(R.id.edit_item_quantity);

		_listView = (ListView) rootView.findViewById(R.id.itemListView_ListFragment);

		itemAdapter = new ItemAdapterListFragment( container.getContext(), R.layout.fragment_list_item, new ArrayList<ItemListFragment>());

		_listView.setAdapter(itemAdapter);

		new BackgroundWorker().execute();

		_button_addItem.setEnabled(false);

		addSpinner();
		addListnerOnTexts();
		addListenerOnButtons();

		return rootView;
	}

	private void addSpinner() {



		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource( 
				getActivity(), R.array.list_item_quantity, android.R.layout.simple_spinner_item);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
		_spinner_item_quantity.setAdapter(adapter);

	}

	private void addListnerOnTexts() {
		TextWatcher onSearchFieldTextChanged = new TextWatcher(){
			public void afterTextChanged(Editable s) {
				//your business logic after text is changed
				if(!"".equals(s.toString()))
					_button_addItem.setEnabled(true);
				else
					_button_addItem.setEnabled(false);
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after){
				//your business logic before text is changed
			}

			public void onTextChanged(CharSequence s, int start, int before, int count){
				//your business logic while text has changed
			}
		};
		_edit_item_name.addTextChangedListener(onSearchFieldTextChanged);

	}

	private void addListenerOnButtons() {

		_button_barcode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					IntentIntegrator integrator = new IntentIntegrator(ListFragment.this);
					integrator.initiateScan();

				} catch (Exception e){
					e.printStackTrace();
				}

			}
		});

		_button_addItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				aggiungiProdottoAllaLista(_edit_item_name.getText().toString(), 
						_edit_item_quantity.getText().toString(),
						String.valueOf(_spinner_item_quantity.getSelectedItem()));
				memorizzaProdottoNellaLista(_edit_item_name.getText().toString(), 
						_edit_item_quantity.getText().toString(),
						String.valueOf(_spinner_item_quantity.getSelectedItem()));
			}

		});

		_button_hint.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			}
		});
		
		_button_hint_close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			}
		});

	}


	private void aggiornaSpinnerAllList(JSONArray response) {
		Toast.makeText(getActivity().getBaseContext(), " - COD_003_todo -  aggiornaSpinnerAllList() carica le lise dal DB", Toast.LENGTH_LONG).show();
		// TODO  COD_003_todo
		if(response== null)
			return;
		try {
			ArrayList<String> allListArray = new ArrayList<String>();
			for (int i = 0; i < response.length(); i++) 
				allListArray.add(response.getString(i));

			allListSpinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, allListArray);
			allListSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			_spinner_allList.setAdapter(allListSpinnerArrayAdapter);
			_spinner_allList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					// aggiorna la lista dei prodotti contenuti nella lista della spesa appena selezionata.

					// -1- ottieni i dati

				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
				}});

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


	protected void aggiungiProdottoAllaLista(String item_name, String edit_item_quantity,String spinner_item_quantity) {
		//		aggoingi elementi nella lista
		ItemListFragment i = new ItemListFragment(	item_name, 
				edit_item_quantity,
				spinner_item_quantity);
		itemAdapter.add(i);
		_edit_item_name.setText("");
		_edit_item_quantity.setText("");
	}

	protected void memorizzaProdottoNellaLista(String item_name, String edit_item_quantity,String spinner_item_quantity) {
		Toast.makeText(getActivity().getBaseContext(), " chiama funzione per cercare nel DB i prodotti da suggerire - COD_002_todo - ", Toast.LENGTH_LONG).show();
		// TODO  COD_002_todo
	}






	private class BackgroundWorker extends AsyncTask<Void, ItemListFragment, Void> {

		@Override
		protected void onPreExecute() {
			// Prima di iniziare a inserire gli elementi svuotiamo l'adapter
			itemAdapter.clear();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			//TODO
			// Qui dentro si possono mettere le operazioni che potrebbero
			// rallentare il caricamento della listview, come ad sempio il
			// caricamento da db degli oggetti

			//        publishProgress( oggetto di tipo ItemListFragment );

			//aggiornaSpinnerAllList(null);
			return null;
		}

		@Override
		protected void onProgressUpdate(ItemListFragment... values) {
			// Aggiungiamo il progresso pubblicato all'adapter
			itemAdapter.add(values[0]);
			super.onProgressUpdate(values);
		}

	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch (requestCode) {
		case IntentIntegrator.REQUEST_CODE:	// barcode scanner
			IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
			if(resultCode != Activity.RESULT_CANCELED && scanResult != null) {
				//scanResult.getContents()
				Toast.makeText(getActivity().getBaseContext(), "barcode letto:"+scanResult.getContents()+ " chiama funzione per cercare nel DB - COD_001_todo - ", Toast.LENGTH_LONG).show();
				// TODO COD_001_todo
			}
			break;

		} // switch
	}

}
