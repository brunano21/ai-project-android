package it.polito.ai.project.fragment;


import it.polito.ai.project.R;
import it.polito.ai.project.adapter.ItemAdapterListFragment;
import it.polito.ai.project.adapter.ItemAllListSpinnerAdapter;
import it.polito.ai.project.main.ItemHintListFragment;
import it.polito.ai.project.main.ItemListFragment;
import it.polito.ai.project.main.ItemSpinnerAllList;
import it.polito.ai.project.main.MainActivity;
import it.polito.ai.project.main.MyHttpClient;
import it.polito.ai.project.main.UserSessionManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
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
				param.put("id_lista_desideri",String.valueOf(item.getId()));
				MyHttpClient.post("/todolist", param, new JsonHttpResponseHandler() {

					@Override
					public void onSuccess(JSONArray response) {
						// elimino tutti gli oggetti
						itemArrayList.clear();
						itemAdapter.notifyDataSetChanged();
						// elimina lista da spinner
						boolean trovato = itemAllListSpinner_ArrayList.remove(item);
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

				for(int i=0; i<itemArrayList.size(); i++)
				{
					// qui per ogni elemento devo chiedere al server se ci sono suggerimenti
					if(i%2==0)
						itemArrayList.get(i).setHint_is_present(true);			

				}
				itemAdapter.notifyDataSetChanged();


			}
		});


	}
	/*
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
	}*/


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
				itemArrayList.clear();
				itemAdapter.notifyDataSetChanged();
				ItemSpinnerAllList tmp = (ItemSpinnerAllList) parent.getItemAtPosition(position);
				aggiornaItemList( tmp.getId() );

				UserSessionManager session = new UserSessionManager(_context);
				session.setId_Lista_Desideri(tmp.getId());
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

					// TODO - seleziona ultima lista visualizzata 
					UserSessionManager session = new UserSessionManager(_context);
					int id_lista_desideri = session.getId_Lista_Desideri();
					int x = _spinner_allList.getChildCount();
					for(int iMarco = 0; iMarco < _spinner_allList.getChildCount(); iMarco++)
					{
						ItemSpinnerAllList tmp = ((ItemSpinnerAllList)_spinner_allList.getChildAt(iMarco).getTag());
						if(id_lista_desideri == tmp.getId() )
						{
							_spinner_allList.setSelection(iMarco);
						}
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
		param.put("id_lista_desideri",String.valueOf(idLista));
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

		LayoutInflater li = LayoutInflater.from(_context);
		View promptsView = li.inflate(R.layout.fragment_list_prompts_inserimento_nuova_lista,null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(_context);

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);

		final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

		// set dialog message
		alertDialogBuilder
		.setCancelable(false)
		.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				// get user input and set it to result
				// edit text
				inviaNuovaLista_User(DateTime.now().toString().hashCode(), userInput.getText().toString());
			}
		})
		.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			}
		});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();

	}

	/*
	 * aggiungo un nuovo prodotto alla lista e mando questa info al server
	 */
	protected void aggiungiNuovoProdottoAllaLista(int id_lista_desideri, int id_elemento, String descrizione, String quantita) 
	{

		inviaNuovoProdottoAlServer( aggiungiProdottoAllaLista(id_lista_desideri, id_elemento, descrizione, quantita, false, null) );
		itemAdapter.notifyDataSetChanged(); // per aggiornare l'eventuale lampadina accesa per il suggerimento che setto nella funzione sopra.
	}


	protected ItemListFragment aggiungiProdottoAllaLista(int id_lista_desideri, int id_elemento, String descrizione, String quantita, boolean acquistato, ItemHintListFragment inserzione) {
		//		aggoingi elementi nella lista
		ItemListFragment item = new ItemListFragment(id_lista_desideri, id_elemento, descrizione, quantita, acquistato, inserzione);
		itemArrayList.add(item);
		itemAdapter.notifyDataSetChanged();

		if(item.getInserzione() == null)
			testHintsFromServer(Double.toString(MainActivity.getLocation().getLatitude()),Double.toString(MainActivity.getLocation().getLongitude()), String.valueOf(item.getId_elemento()), item.getDescrizione());

		_edit_item_name.setText("");
		_edit_item_quantity.setText("");
		return item;
	}




	private void inviaNuovaLista_User(final int id, final String nome) {
		RequestParams param = new RequestParams();
		param.put("cmd","nuovaListaDesideri");
		param.put("id_lista_desideri",String.valueOf(id));
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

	void inviaNuovoProdottoAlServer(final ItemListFragment in){
		int id_lista_desideri = in.getId_lista_desideri() ;
		int id_elemento = in.getId_elemento() ;
		String descrizione = in.getDescrizione() ;
		String quantita = in.getQuantita() ;
		String NULL = null;
		RequestParams param = new RequestParams();
		param.put("cmd","nuovo_elemento");
		param.put("latitudine",Double.toString(MainActivity.getLocation().getLatitude()));
		param.put("longitudine",Double.toString(MainActivity.getLocation().getLongitude()));
		param.put("id_lista_desideri",String.valueOf(id_lista_desideri));
		param.put("id_elemento",String.valueOf(id_elemento));
		param.put("descrizione",descrizione);
		param.put("quantita","".equals(quantita)?"1":quantita);
		param.put("id_inserzione",NULL);
		MyHttpClient.post("/todolist", param, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray response) {

			}
			@Override
			public void onFailure(Throwable error, String content) {
				Log.v("ERROR" , "onFailure error : " + error.toString() + "content : " + content);
			}
		});

	}





	protected void accendiLampadina(int id_elemento) {
		Iterator<ItemListFragment> iter = itemArrayList.iterator();
		while( iter.hasNext() )
		{
			ItemListFragment tmp = iter.next();
			if( tmp.getId_elemento() == id_elemento )
			{
				tmp.setHint_is_present(true);
				itemAdapter.notifyDataSetChanged();
				break;
			}
		}
	}


	private List<ItemHintListFragment> testHintsFromServer(String longitudine, String latitudine, String id_elemento, String descrizione) {
		ArrayList<ItemHintListFragment> hint_itemArrayList = new ArrayList<ItemHintListFragment>();
		RequestParams param = new RequestParams();
		param.put("cmd","ottieni_suggerimenti");
		param.put("latitudine",latitudine);
		param.put("longitudine",longitudine);
		param.put("id_elemento",id_elemento);
		param.put("descrizione",descrizione);
		MyHttpClient.post("/todolist", param, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(JSONArray response) {
				for (int i = 0; i < response.length() && i<1 ; i++) 
					try {
						// avro un array con un unico id_elemento solo se ho suggerimenti
						if( response.getJSONObject(0).has("id_elemento") )
						{
							int id_elemento = Integer.valueOf( response.getJSONObject(i).getString("id_elemento") );
							accendiLampadina(id_elemento);
							break;
						}
					} catch (Exception e) 
					{
						e.printStackTrace();
					}

				itemAdapter.notifyDataSetChanged();
			}
			@Override
			public void onFailure(Throwable error, String content) {
				Log.v("ERROR" , "onFailure error : " + error.toString() + "content : " + content);
			}
		});

		return hint_itemArrayList;
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
