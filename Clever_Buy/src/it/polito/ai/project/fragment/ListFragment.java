package it.polito.ai.project.fragment;


import it.polito.ai.project.R;
import it.polito.ai.project.adapter.ItemAdapterListFragment;
import it.polito.ai.project.adapter.ItemAllListSpinnerAdapter;
import it.polito.ai.project.main.ItemHintListFragment;
import it.polito.ai.project.main.ItemListFragment;
import it.polito.ai.project.main.ItemSpinnerAllList;
import it.polito.ai.project.main.MyHttpClient;

import java.util.ArrayList;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

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
import android.widget.ImageButton;
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
	private ImageButton _img_add_new_list, _img_delete_current_list;
	private ListView _listView;

	private ArrayList<ItemSpinnerAllList> itemAllListSpinner_ArrayList;
	private ItemAllListSpinnerAdapter itemAllListSpinner_ArrayAdapter;
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

		_img_add_new_list = (ImageButton) rootView.findViewById(R.id.img_add_new_list);
		_img_delete_current_list = (ImageButton) rootView.findViewById(R.id.img_delete_current_list);
		
		_img_add_new_list.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				aggiungiNuovaLista_User();
			}
		});
		
		
		
		_img_delete_current_list.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int position = _spinner_allList.getSelectedItemPosition();
				ItemSpinnerAllList tmp = (ItemSpinnerAllList) _spinner_allList.getSelectedItem();
				int id = tmp.getId();
				eliminaLista( tmp );
			}

			private void eliminaLista(final ItemSpinnerAllList item ) {
				RequestParams param = new RequestParams();
				param.put("cmd","eliminaListaDesideri");
				param.put("id_lista_desideri",Integer.toString(item.getId()));
				MyHttpClient.post("/todolist", param, new JsonHttpResponseHandler() {

					@Override
					public void onSuccess(JSONArray response) {
						// elimino tutti gli oggetti
						itemArrayList.clear();
						itemAdapter.notifyDataSetChanged();
						// elimina lista da spinner
						itemAllListSpinner_ArrayList.remove(item);
						itemAllListSpinner_ArrayAdapter.notifyDataSetChanged();
					}
					@Override
					public void onFailure(Throwable error, String content) {
						Log.v("ERROR" , "onFailure error : " + error.toString() + "content : " + content);
					}
				});
			}
		});
		
		
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


		aggiornaSpinnerAllList();

		addListnerOnTexts();
		addListenerOnButtons();

		//commentato perchè carico mnualemente io i linear layout
		return rootView;
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
				// id_lista_desideri -> prendi dallo spinner all list
				// id_elemento 
				// descrizione
				// quantita
				int id_elemento = DateTime.now().toString().hashCode();
				int id_allSpinner = ((ItemSpinnerAllList) _spinner_allList.getSelectedItem()).getId();
				aggiungiNuovoProdottoAllaLista(	id_allSpinner,
						id_elemento, 
						_edit_item_name.getText().toString(), 
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


	protected void aggiornaSpinnerAllList() 
	{
		itemAllListSpinner_ArrayList = new ArrayList<ItemSpinnerAllList>();
		itemAllListSpinner_ArrayAdapter = new ItemAllListSpinnerAdapter(getActivity(), R.layout.fragment_list_all_spinner_item, itemAllListSpinner_ArrayList);
		//itemAllListSpinner_ArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		_spinner_allList.setAdapter(itemAllListSpinner_ArrayAdapter);
		_spinner_allList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// aggiorna la lista dei prodotti contenuti nella lista della spesa appena selezionata.
				//ItemSpinnerAllList tmp = (ItemSpinnerAllList) view.getTag();
				ItemSpinnerAllList tmp = (ItemSpinnerAllList) parent.getItemAtPosition(position);
				aggiornaItemList( tmp.getId() );
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}});

		RequestParams params = new RequestParams();
		MyHttpClient.get("/todolist/getTodoListIDs", params, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONArray response) {
				if(response.length() != 0) {
					for(int i = 0; i<response.length(); i++)
						try {
							aggiungiNuovaLista(Integer.valueOf(response.getJSONObject(i).getString("id")),response.getJSONObject(i).getString("nome"));
						} catch (JSONException e) {
							e.printStackTrace();
						}
				}
				else {
					Toast.makeText(getActivity(), "Nessuna Todo List presente sul server. Clicca sul bottone + per crearne una nuova", Toast.LENGTH_LONG).show();
				}
			}
			@Override
			public void onFailure(Throwable error, String content) {
				Log.v("ERROR" , "onFailure error : " + error.toString() + "content : " + content);
				Toast.makeText(getActivity(), "Ops, c'è stato un problema con il server.", Toast.LENGTH_SHORT).show();
			}
		});


	}

	/**
	 * 
	 * @param position  uso questo per capire quale lista della spesa devo caricare
	 */
	protected void aggiornaItemList(final int idLista) {
		RequestParams param = new RequestParams();
		param.put("cmd","todoListItems");
		param.put("id_lista_desideri",Integer.toString(idLista));
		MyHttpClient.post("/todolist", param, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(JSONArray response) {
				for (int i = 0; i < response.length(); i++) 
					try {
						ItemHintListFragment hint = null;

						if(response.getJSONObject(i).has("inserzione"))
						{
							DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
							int item_id = response.getJSONObject(i).getJSONObject("inserzione").getInt("id_inserzione");
							boolean selezionato = true;
							String descrizione = response.getJSONObject(i).getJSONObject("inserzione").getString("descrizione");
							DateTime data_fine = formatter.parseDateTime(response.getJSONObject(i).getJSONObject("inserzione").getString("data_fine"));
							String supermercato = response.getJSONObject(i).getJSONObject("inserzione").getString("supermercato");
							String prezzo = response.getJSONObject(i).getJSONObject("inserzione").getString("prezzo");
							String foto = response.getJSONObject(i).getJSONObject("inserzione").getString("foto");
							hint = new ItemHintListFragment(item_id, selezionato, descrizione, data_fine, supermercato, prezzo, foto);
						}
						int ar1 = response.getJSONObject(i).getInt("id_elemento");
						String ar2 = response.getJSONObject(i).getString("descrizione");
						String ar3 = response.getJSONObject(i).getString("quantita");
						boolean ar4 = response.getJSONObject(i).getBoolean("acquistato"); 

						aggiungiProdottoAllaLista(idLista, ar1,ar2,ar3,ar4, hint  );
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

	/*
	 * aggiunge una todolist prendendola dal server
	 */
	private void aggiungiNuovaLista(int id, String nome) {

		ItemSpinnerAllList item = new ItemSpinnerAllList(id, nome);
		itemAllListSpinner_ArrayList.add(item );
		itemAllListSpinner_ArrayAdapter.notifyDataSetChanged();
	}
	
	/*
	 * associato al bottone + che aggiunge nuova todolist, avvia una dialog box
	 */
	private void aggiungiNuovaLista_User(){
		// TODO dialog per acquisire il nome della nuova todolist
		
		String nome = DateTime.now().toString();
		int id = DateTime.now().toString().hashCode();
		inviaNuovaLista_User(id, nome);
		
	}

	/*
	 * aggiungo un nuovo prodotto alla lista e mando questa info al server
	 */
	protected void aggiungiNuovoProdottoAllaLista(int id_lista_desideri, int id_elemento, String descrizione, String quantita) {

		inviaNuovoProdottoAlServer(id_lista_desideri, id_elemento, descrizione, quantita);

		aggiungiProdottoAllaLista(id_lista_desideri, id_elemento, descrizione, quantita, false, null); 

	}


	protected void aggiungiProdottoAllaLista(int id_lista_desideri, int id_elemento, String descrizione, String quantita, boolean acquistato, ItemHintListFragment inserzione) {
		//		aggoingi elementi nella lista
		ItemListFragment item = new ItemListFragment(id_lista_desideri, id_elemento, descrizione, quantita, acquistato, inserzione);
		itemArrayList.add(item);
		itemAdapter.notifyDataSetChanged();

		_edit_item_name.setText("");
		_edit_item_quantity.setText("");
	}

	

	private void inviaNuovaLista_User(final int id, final String nome) {
		RequestParams param = new RequestParams();
		param.put("cmd","nuovaListaDesideri");
		param.put("id_lista_desideri",Integer.toString(id));
		param.put("nome",nome);
		MyHttpClient.post("/todolist", param, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(JSONArray response) {
				aggiungiNuovaLista(id,nome);
			}
			@Override
			public void onFailure(Throwable error, String content) {
				Log.v("ERROR" , "onFailure error : " + error.toString() + "content : " + content);
			}
		});

	}

	void inviaNuovoProdottoAlServer(int id_lista_desideri, int id_elemento, String descrizione, String quantita){
		String NULL = null;
		RequestParams param = new RequestParams();
		param.put("cmd","nuovo_elemento");
		param.put("id_lista_desideri",String.valueOf(id_lista_desideri));
		param.put("id_elemento",String.valueOf(id_elemento));
		param.put("descrizione",descrizione);
		param.put("quantita","".equals(quantita)?"1":quantita);
		param.put("id_inserzione",NULL);
		// TODO metti le coordinate GPS
		MyHttpClient.post("/todolist", param, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(JSONArray response) {
				for (int i = 0; i < response.length(); i++) 
					try {
						ItemHintListFragment hint = null;
						DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
						int item_id = response.getJSONObject(i).getJSONObject("inserzione").getInt("item_id");
						boolean selezionato = true;
						String descrizione = response.getJSONObject(i).getString("descrizione");
						DateTime data_fine = formatter.parseDateTime(response.getJSONObject(i).getString("data_fine"));
						String supermercato = response.getJSONObject(i).getString("supermercato");
						String prezzo = response.getJSONObject(i).getString("");
						String foto = response.getJSONObject(i).getString("");
						hint = new ItemHintListFragment(item_id, selezionato, descrizione, data_fine, supermercato, prezzo, foto);
						// TODO queste info le devi memorizzare da qualche parte per visualizzarlo negli hint
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

			//

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
