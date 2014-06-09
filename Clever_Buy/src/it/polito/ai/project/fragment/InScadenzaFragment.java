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

import it.polito.ai.project.R;
import it.polito.ai.project.fragment.DialogDettagliInserzioneInScadenza;
import it.polito.ai.project.fragment.DialogDettagliInserzioneInScadenza.MyDialogInterface;
import it.polito.ai.project.main.MainActivity;
import it.polito.ai.project.main.MyHttpClient;
import it.polito.ai.project.model.InserzioneInScadenza;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
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

	private final int AUTOLOAD_THRESHOLD = 8;
	private boolean IsLoading = false;
	private boolean MoreDataAvailable = true;

	private View rootView;
	private ListView listView;
	private ListView footerView;

	private List<InserzioneInScadenza> inserzioniInScadenzaList;
	private ArrayList<Integer> idInserzioniInScandenzaList;
	private ArrayAdapter<InserzioneInScadenza> inserzioniInScadenzaArrayAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_in_scadenza, container, false);
		listView = (ListView) rootView.findViewById(R.id.inScadenzaListView);
		footerView = (ListView) rootView.findViewById(R.layout.listview_footer);

		idInserzioniInScandenzaList = new ArrayList<Integer>();
		inserzioniInScadenzaArrayAdapter = new InserzioniInScadenzaAdapter();

		getIdInserzioniInScadenza();

		listView.addFooterView(footerView);
		listView.setAdapter(inserzioniInScadenzaArrayAdapter);

		registerListenersOnListView();

		return rootView;
	}


	private void getIdInserzioniInScadenza() {
		RequestParams params = new RequestParams();
		params.put("lat", MainActivity.getLocation().getLatitude());
		params.put("lng", MainActivity.getLocation().getLatitude());

		MyHttpClient.get("/inscadenza/getIdInserzioni", params, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONArray response) {
				System.out.println(response.toString());
				for(int i = 0; i<response.length(); i++)
					try {
						idInserzioniInScandenzaList.add(Integer.valueOf(response.getInt(i)));
					} catch (JSONException e) {
						e.printStackTrace();
					}

				getInserzioniById(-1);
			}



			@Override
			public void onFailure(Throwable error, String content) {
				Log.v("ERROR" , "onFailure error : " + error.toString() + "content : " + content);
				Toast.makeText(getActivity(), "Ops, c'è stato un problema con il server.", Toast.LENGTH_SHORT).show();
			}

		});
	}

	private void getInserzioniById(int totalItemCount) {
		int count = totalItemCount + 1;
		List<Integer> idProssimeInserzioniList = new ArrayList<Integer>();

		idProssimeInserzioniList = idInserzioniInScandenzaList.subList(count, (count + AUTOLOAD_THRESHOLD) < idInserzioniInScandenzaList.size() ? (count + AUTOLOAD_THRESHOLD) : idInserzioniInScandenzaList.size());

		RequestParams params = new RequestParams();
		StringBuilder sb = new StringBuilder();
		for(Integer s : idProssimeInserzioniList) 
			sb.append(s).append(",");

		params.put("idInserzioneList", sb.toString());
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
				inserzioniInScadenzaList.add(new InserzioneInScadenza(
						Integer.valueOf(response.getJSONObject(i).getInt("id")), 
						Float.valueOf(response.getJSONObject(i).getString("prezzo")), 
						formatter.parseDateTime(response.getJSONObject(i).getString("data_fine")),
						response.getJSONObject(i).getString("descrizione"),
						response.getJSONObject(i).getString("foto"),
						response.getJSONObject(i).getString("supermercato"),
						response.getJSONObject(i).getString("supermercato_indirizzo")));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		inserzioniInScadenzaArrayAdapter.notifyDataSetChanged();
		IsLoading = false;
	}

	private void registerListenersOnListView() {

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				RelativeLayout transparentOverlay = (RelativeLayout) view.findViewById(R.id.listview_item_inserzione_in_scadenza_rl_transparentOverlay);
				if(transparentOverlay.getVisibility() == View.VISIBLE)
					return false; // TODO: check!

				InserzioneInScadenza ins = inserzioniInScadenzaList.get(position);
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
				System.out.println(firstVisibleItem + " " + visibleItemCount + " " + totalItemCount);
				System.out.println(IsLoading + " " + MoreDataAvailable);
				if(idInserzioniInScandenzaList.size() != 0)
					if (!IsLoading && MoreDataAvailable) {
						if (totalItemCount >= idInserzioniInScandenzaList.size()) {
							MoreDataAvailable = false;
							listView.removeFooterView(footerView);
						} else if (totalItemCount - AUTOLOAD_THRESHOLD <= firstVisibleItem + visibleItemCount) {
							IsLoading = true;
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
					Toast.makeText(getActivity().getApplicationContext(), "Elemento aggiunto!", Toast.LENGTH_LONG).show();
					settaInserzioneComeAggiunta(response.getInt(0));
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
		View itemView = (View) listView.getChildAt(posizione);
		RelativeLayout transparentOverlay = (RelativeLayout) itemView.findViewById(R.id.listview_item_inserzione_in_scadenza_rl_transparentOverlay);
		RelativeLayout contentElement = (RelativeLayout) itemView.findViewById(R.id.listview_item_inserzione_in_scadenza_rl_contentElement);
		contentElement.setAlpha(0.2f);
		transparentOverlay.setVisibility(1);
	}

	/*-----------------------------------  INNER CLASS  -----------------------------------------------*/

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
			ImageView foto = (ImageView) itemView.findViewById(R.id.listview_item_inserzione_in_scadenza_im_foto);

			descrizione.setText(inserzione.getDescrizione());
			data_fine.setText((inserzione.getDataFine() == DateTime.now()) ? "Oggi!" : "Domani!");
			prezzo.setText(Float.toString(inserzione.getPrezzo()));

			//TODO settare la foto! L'avevo fatto, mannaggia a cristo redentore!

			return itemView;
		}
	}











}