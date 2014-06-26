package it.polito.ai.project.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nhaarman.listviewanimations.itemmanipulation.AnimateDismissAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.OnDismissCallback;

import it.polito.ai.project.R;
import it.polito.ai.project.fragment.DialogDettagliInserzioneInScadenza;
import it.polito.ai.project.fragment.DialogDettagliInserzioneInScadenza.MyDialogInterface;
import it.polito.ai.project.main.MainActivity;
import it.polito.ai.project.main.MyHttpClient;
import it.polito.ai.project.model.InserzioneInScadenza;
import android.R.integer;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class InScadenzaFragment extends Fragment implements MyDialogInterface{

	private final int AUTOLOAD_THRESHOLD = 7;
	private boolean IsLoading = false;
	private boolean MoreDataAvailable = true;

	private View rootView;
	private ListView listView;
	private View footerView;

	private List<InserzioneInScadenza> inserzioniInScadenzaList;
	private ArrayList<Integer> idInserzioniInScandenzaList;
	private ArrayAdapter<InserzioneInScadenza> inserzioniInScadenzaArrayAdapter;
	private AnimateDismissAdapter animateDismissAdapter;
	private ProgressDialog progressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_in_scadenza, container, false);
		listView = (ListView) rootView.findViewById(R.id.inScadenzaListView);
		footerView = inflater.inflate(R.layout.listview_footer, null, false);

		idInserzioniInScandenzaList = new ArrayList<Integer>();

		inserzioniInScadenzaList = new ArrayList<InserzioneInScadenza>();
		
		inserzioniInScadenzaArrayAdapter = new InserzioniInScadenzaAdapter();
		
		getIdInserzioniInScadenza();
		
		animateDismissAdapter = new AnimateDismissAdapter(inserzioniInScadenzaArrayAdapter, new MyOnDismissCallback());
		animateDismissAdapter.setAbsListView(listView);
		
		listView.setAdapter(animateDismissAdapter);
		registerListenersOnListView();
		
		
		progressDialog = ProgressDialog.show(getActivity(), "Download", "Sto ricercando nel sistema inserzioni che scadranno a breve. Attendi...", false);

		return rootView;
	}

	private void getIdInserzioniInScadenza() {
		RequestParams params = new RequestParams();
		params.put("lat", Double.toString(MainActivity.getLocation().getLatitude()));
		params.put("lng", Double.toString(MainActivity.getLocation().getLatitude()));

		MyHttpClient.get("/inscadenza/getIdInserzioni", params, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONArray response) {
				if(response.length() != 0) {
					for(int i = 0; i<response.length(); i++)
						try {
							idInserzioniInScandenzaList.add(Integer.valueOf(response.getInt(i)));
						} catch (JSONException e) {
							e.printStackTrace();
						}
					getInserzioniById(0);
				}
				else {
					// TODO: mostrare qualcosa a video, differente da toast
					// TODO: nascondere anche il processDialog
					Toast.makeText(getActivity(), "Nessuna inserzione da valutare", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(Throwable error, String content) {
				Log.v("ERROR" , "onFailure error : " + error.toString() + "content : " + content);
				Toast.makeText(getActivity(), "Ops, c'è stato un problema con il server.", Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void getInserzioniById(int totalItemCount) {
		int count = totalItemCount;
		List<Integer> idProssimaInserzioneList = new ArrayList<Integer>();
		
		System.out.println("getInserzioniById(): totalItemCount =  " + count);
		System.out.println("getInserzioniById(): idInserzioneList.size() = " + idInserzioniInScandenzaList.size());

		idProssimaInserzioneList = idInserzioniInScandenzaList.subList(count, (count + AUTOLOAD_THRESHOLD) < idInserzioniInScandenzaList.size() ? (count + AUTOLOAD_THRESHOLD) : idInserzioniInScandenzaList.size());

		if(idProssimaInserzioneList.size() == 0)
			return;

		StringBuilder sb = new StringBuilder();
		for(Integer s : idProssimaInserzioneList) 
			sb.append(s).append(",");
		
		RequestParams params = new RequestParams();
		params.put("idInserzioneList",  sb.toString().substring(0, sb.toString().length()-1));
		MyHttpClient.get("/inscadenza/getInserzioneById", params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray response) {
				popolaInserzioniInScadenzaList(response);
			}

			@Override
			public void onFailure(Throwable error, String content) {
				Log.v("ERROR" , "onFailure error : " + error.toString() + "content : " + content);
				Toast.makeText(getActivity(), "Ops, c'è stato un problema con il server.", Toast.LENGTH_SHORT).show();
			}

		});
	}

	private void popolaInserzioniInScadenzaList(JSONArray response) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
		for(int i = 0; i < response.length(); i++) {
			try {
				InserzioneInScadenza inserzione = new InserzioneInScadenza(
						Integer.valueOf(response.getJSONObject(i).getInt("id")), 
						Float.valueOf(response.getJSONObject(i).getString("prezzo")), 
						formatter.parseDateTime(response.getJSONObject(i).getString("data_fine")),
						response.getJSONObject(i).getString("descrizione"),
						response.getJSONObject(i).getString("foto"),
						response.getJSONObject(i).getString("supermercato"),
						response.getJSONObject(i).getString("supermercato_indirizzo"),
						(response.getJSONObject(i).has("nome_todolist")) ? response.getJSONObject(i).getString("nome_todolist") : null); 
				
				inserzioniInScadenzaList.add(inserzione);


				
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		IsLoading = false;
		if(listView.getFooterViewsCount() != 0)
			listView.removeFooterView(footerView);
		inserzioniInScadenzaArrayAdapter.notifyDataSetChanged();
		progressDialog.dismiss();

	}

	private void registerListenersOnListView() {

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				InserzioneInScadenza ins = inserzioniInScadenzaList.get(position);
				if(ins.getNome_todolist() != null) {
					Toast.makeText(getActivity(), "Hai già aggiunto questo elemento alle tue todolist [in "+ ins.getNome_todolist() + "]", Toast.LENGTH_SHORT).show();
					return false;
				}
				
				FragmentManager manager = getFragmentManager();
				DialogDettagliInserzioneInScadenza dialog = DialogDettagliInserzioneInScadenza.getInstance(InScadenzaFragment.this);
				Bundle args = new Bundle();
				args.putSerializable("dialogInterface", dialog.getArguments().getSerializable("dialogInterface"));
				args.putParcelable("inserzioneInScadenza", ins);
				args.putInt("posizione", position);
				dialog.setArguments(args);
				dialog.show(manager, "inserzioneInScadenzaDialog");

				return true;
			}
		}); 
		
		listView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				System.out.println("onScroll(): firstVisibleItem = " + firstVisibleItem + ", visibleItemCount = " + visibleItemCount + ", totalItemCount = " + totalItemCount);
				System.out.println("onScroll(): IsLoading = " + IsLoading + ", MoreDataAvailable = " + MoreDataAvailable);
				if(idInserzioniInScandenzaList.size() != 0)
					if (!IsLoading && MoreDataAvailable) {
						if (totalItemCount >= idInserzioniInScandenzaList.size()) {
							MoreDataAvailable = false;
							if(listView.getFooterViewsCount() != 0)
								listView.removeFooterView(footerView);
							inserzioniInScadenzaArrayAdapter.notifyDataSetChanged();
						} else if (totalItemCount <= firstVisibleItem + visibleItemCount) {
							IsLoading = true;
							listView.addFooterView(footerView);
							inserzioniInScadenzaArrayAdapter.notifyDataSetChanged();
							getInserzioniById(totalItemCount);
						}
					}
			}
		});
	}


	@Override
	public void onDialogPositiveClick(DialogDettagliInserzioneInScadenza dialog) {
		RequestParams params = new RequestParams();
		params.put("idInserzione", String.valueOf(dialog.getInserzione().getIdInserzione()));
		params.put("idListaDesideri", String.valueOf(dialog.getIdTodoListSelezionata()));
		params.put("posizione", String.valueOf(dialog.getPosizione()));

		MyHttpClient.post("/inscadenza/aggiungiElemento", params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray response) {
				try {
					Toast.makeText(getActivity().getApplicationContext(), "Elemento aggiunto con successo!", Toast.LENGTH_LONG).show();
					settaInserzioneComeAggiunta(response.getJSONObject(0).getInt("posizione"));
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


	@Override
	public void onDialogNegativeClick(DialogDettagliInserzioneInScadenza dialog) {

	}

	private void settaInserzioneComeAggiunta(int posizione) {
		animateDismissAdapter.animateDismiss(posizione);
	}

	/*-----------------------------------  INNER CLASSES  -----------------------------------------------*/

	private class MyOnDismissCallback implements OnDismissCallback {

		@Override
		public void onDismiss(final AbsListView listView, final int[] reverseSortedPositions) {
			for (int position : reverseSortedPositions) {
				 inserzioniInScadenzaArrayAdapter.remove(inserzioniInScadenzaArrayAdapter.getItem(position));
            }
		}
		
	}
	
	private class InserzioniInScadenzaAdapter extends ArrayAdapter<InserzioneInScadenza> {

		public InserzioniInScadenzaAdapter() {
			super(getActivity().getApplicationContext(), R.layout.listview_item_inserzione_in_scandenza, inserzioniInScadenzaList);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View itemView = convertView;
			
			if(itemView == null) 
				itemView = getActivity().getLayoutInflater().inflate(R.layout.listview_item_inserzione_in_scandenza, parent, false);
			
			// cerco l'inserzione  con la quale devo lavorare
			InserzioneInScadenza inserzione = inserzioniInScadenzaList.get(position);

			// riempio la sua vista
			TextView descrizione = (TextView) itemView.findViewById(R.id.listview_item_inserzione_in_scadenza_tv_descrizione);
			TextView data_fine = (TextView) itemView.findViewById(R.id.listview_item_inserzione_in_scadenza_tv_data_fine);
			TextView prezzo = (TextView) itemView.findViewById(R.id.listview_item_inserzione_in_scadenza_tv_prezzo);
			ImageView foto = (ImageView) itemView.findViewById(R.id.listview_item_inserzione_in_scadenza_iv_foto);

			descrizione.setText(inserzione.getDescrizione());
			
			if(DateTimeFormat.forPattern("yyyy-MM-dd").print(DateTime.now()).equals(DateTimeFormat.forPattern("yyyy-MM-dd").print(inserzione.getDataFine()))) {
				data_fine.setTextColor(getResources().getColor(R.color.red));
				data_fine.setText("Oggi!");
			}
			else {
				data_fine.setTextColor(getResources().getColor(R.color.yellow));
				data_fine.setText("Domani!");
			}
			
			prezzo.setText(Float.toString(inserzione.getPrezzo()) + " €");
			
			byte[] decodedString = Base64.decode(inserzione.getFoto(), Base64.DEFAULT);
			Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length); 
			foto.setImageBitmap(decodedByte);
			
			ImageView star = (ImageView) itemView.findViewById(R.id.listview_item_inserzione_in_scadenza_iv_star);
			if(inserzione.getNome_todolist() != null) 
				star.setVisibility(View.VISIBLE);
			else
				star.setVisibility(View.GONE);
			
			return itemView;
		}		
		
	}











}