package it.polito.ai.project.fragment;


import it.polito.ai.project.R;
import it.polito.ai.project.adapter.ItemAdapterListFragment;
import it.polito.ai.project.main.ItemListFragment;
import it.polito.ai.project.main.MyHttpClient;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
// After merge
public class ListFragment extends Fragment {

	private View rootView;
	private Spinner _spinner_allList;
	private EditText _edit_item_name, _edit_item_quantity;
	private Button  _button_addItem, _button_barcode, _button_hint;
	private ListView _listView;


	private ArrayAdapter<String> allListSpinnerArrayAdapter;
	private ArrayList<ItemListFragment> itemArrayList;
	private ItemAdapterListFragment itemAdapter;
	private Context _context;

	public ListFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		_context = container.getContext();
		rootView = inflater.inflate(R.layout.fragment_list, container, false);

		_button_addItem = (Button) rootView.findViewById(R.id.button_addItem);
		_button_barcode = (Button) rootView.findViewById(R.id.button_addItem_byBarCode);
		_button_hint = (Button) rootView.findViewById(R.id.button_hint);

		_spinner_allList = (Spinner) rootView.findViewById(R.id.spinner_allList);

		_edit_item_name = (EditText) rootView.findViewById(R.id.edit_item_name);
		_edit_item_quantity = (EditText) rootView.findViewById(R.id.edit_item_quantity);

		_listView = (ListView) rootView.findViewById(R.id.itemListView_ListFragment);

		itemArrayList = new ArrayList<ItemListFragment>();
		itemAdapter = new ItemAdapterListFragment( container.getContext(), R.layout.fragment_list_item, itemArrayList);
		_listView.setAdapter(itemAdapter);
 
		_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//TODO apri Dialog Dettagli 
			}
		});
		
		
		new BackgroundWorker().execute();

		_button_addItem.setEnabled(false);

		addSpinner();
		addListnerOnTexts();
		addListenerOnButtons();

		//commentato perchè carico mnualemente io i linear layout
		return rootView;
	}

	private void addSpinner() {

		// TODO creare metodo che tramite get http 
		MyHttpClient.get("/todolist/getTodoList", null, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(JSONArray response) {
				aggiornaSpinnerAllList(response);
			}

			@Override
			public void onFailure(Throwable error, String content) {
				Log.v("ERROR" , "onFailure error : " + error.toString() + "content : " + content);
			}
		});

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
						_edit_item_quantity.getText().toString());
				memorizzaProdottoNellaLista(_edit_item_name.getText().toString(), 
						_edit_item_quantity.getText().toString());
			}
		});

		_button_hint.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Toast.makeText(getActivity().getBaseContext(), " - COD_006_todo - test per acendere la luce - dopo elimina", Toast.LENGTH_LONG).show();
				// TODO COD_006_todo
				for(int i=0; i<itemArrayList.size(); i++)
				{
					// qui per ogni elemento devo chiedere al server se ci sono suggerimenti
					if(i%2==0)
						itemArrayList.get(i).setHint_is_present(true);			

				}
				itemAdapter.notifyDataSetChanged();
				
				// custom dialog
				showDialogHint();
				
			}
		});


	}
	
	void showDialogHint() {
	    // DialogFragment.show() will take care of adding the fragment
	    // in a transaction.  We also want to remove any currently showing
	    // dialog, so make our own transaction and take care of that here.
	    FragmentTransaction ft = getFragmentManager().beginTransaction();
	    Fragment prev = getFragmentManager().findFragmentByTag("dialog");
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);

	    // Create and show the dialog.
	    DialogHint newFragment = DialogHint.newInstance();
	    newFragment.show(ft, "dialog");
	}

	private void aggiornaSpinnerAllList(JSONArray response) {
		Toast.makeText(getActivity().getBaseContext(), " - COD_003_todo -  aggiornaSpinnerAllList() carica le lise dal DB", Toast.LENGTH_LONG).show();
		// TODO  COD_003_todo
		if(response== null)
			return;
		else
		{

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

						aggiornaItemList(position);

					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					}});

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @param position  uso questo per capire quale lista della spesa devo caricare
	 */
	protected void aggiornaItemList(int position) {

		MyHttpClient.get("/", null, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(JSONArray response) {
				for (int i = 0; i < response.length(); i++) 
					;//TODO aggiungiProdottoAllaLista(,);;	
					
			}

			@Override
			public void onFailure(Throwable error, String content) {
				Log.v("ERROR" , "onFailure error : " + error.toString() + "content : " + content);
			}
		});
		
			
	}

	protected void aggiungiProdottoAllaLista(String item_name, String edit_item_quantity) {
		//		aggoingi elementi nella lista
		ItemListFragment item = new ItemListFragment(	item_name, edit_item_quantity);

		//itemAdapter.add(i);
		itemArrayList.add(item);
		itemAdapter.notifyDataSetChanged();

		_edit_item_name.setText("");
		_edit_item_quantity.setText("");
	}

	protected void memorizzaProdottoNellaLista(String item_name, String edit_item_quantity) {
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
