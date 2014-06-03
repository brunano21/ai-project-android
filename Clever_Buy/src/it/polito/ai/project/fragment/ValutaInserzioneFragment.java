package it.polito.ai.project.fragment;

import it.polito.ai.project.R;
import it.polito.ai.project.fragment.ValutazioneDettagliDialog.MyDialogInterface;
import it.polito.ai.project.main.MyHttpClient;
import it.polito.ai.project.model.Valutazione;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ValutaInserzioneFragment extends Fragment implements MyDialogInterface{ 

	private View rootView;
	private ListView listView;
	private List<Valutazione> valutazioneList;
	private ArrayAdapter<Valutazione> valutazioneAdapter;


	private final int AUTOLOAD_THRESHOLD = 8;
	private final int MAXIMUM_ITEMS = 52; //size() array dalla richiesta al server.
	private boolean IsLoading = false;
	private boolean MoreDataAvailable = true;

	private View footerView; 
	private ArrayList<Integer> idInserzioneList;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_valuta_inserzione, container, false);

		listView = (ListView) rootView.findViewById(R.id.valutazioniListView);
		footerView = inflater.inflate(R.layout.valutazione_list_footer_view, null, false);

		idInserzioneList = new ArrayList<Integer>();

		valutazioneList = new ArrayList<Valutazione>();
		valutazioneAdapter = new ValutazioneAdapeter();

		chiediIdInserzioni();

		registraClick();
		listView.addFooterView(footerView);
		listView.setAdapter(valutazioneAdapter);


		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (!IsLoading && MoreDataAvailable) {
					if (totalItemCount >= idInserzioneList.size()) {
						MoreDataAvailable = false;
						listView.removeFooterView(footerView);
					} else if (totalItemCount - AUTOLOAD_THRESHOLD <= firstVisibleItem + visibleItemCount) {
						if(idInserzioneList.size() != 0){
							IsLoading = true;
							chiediAltreValutazioni(totalItemCount);
						}
					}
				}
			}
		});

		return rootView;

	}

	private void chiediIdInserzioni() {
		RequestParams params = new RequestParams();
		// TODO aggiungere latitudine e logitudine
		params.put("lat", "38.0658");
		params.put("lng", "15.4824");
		MyHttpClient.get("/valutazione/getIdInserzioni", params, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONArray response) {
				System.out.println(response.toString());
				for(int i = 0; i<response.length(); i++)
					try {
						System.out.println("VAL = " + response.getInt(i));
						idInserzioneList.add(Integer.valueOf(response.getInt(i)));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				System.out.println("onSuccess " + idInserzioneList.size());
				Log.v("DEBUG", "onSuccess " + idInserzioneList.size());
				chiediAltreValutazioni(-1);
			}

			@Override
			public void onFailure(Throwable error, String content) {
				Log.v("ERROR" , "onFailure error : " + error.toString() + "content : " + content);
			}
		});
	}

	private void chiediAltreValutazioni(int totalItemCount) {
		int count = totalItemCount + 1;
		List<Integer> idProssimaInserzioneList = new ArrayList<Integer>();

		System.out.println("totalItemCount " + totalItemCount);
		System.out.println("idInserzioneList.size() " + idInserzioneList.size());

		idProssimaInserzioneList = idInserzioneList.subList(count, (count + AUTOLOAD_THRESHOLD) < idInserzioneList.size() ? (count + AUTOLOAD_THRESHOLD) : idInserzioneList.size());

		System.out.println("idProssimaInserzioneList " + idProssimaInserzioneList.size());
		RequestParams params = new RequestParams();
		StringBuilder sb = new StringBuilder();
		for(Integer s : idProssimaInserzioneList) {
			sb.append(s).append(",");
		}
		params.put("idInserzioneList", sb.toString());
		MyHttpClient.get("/valutazione/getInserzioneById", params, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(JSONArray response) {
				popolaValutazioneList(response);
			}


		});
	}

	private void popolaValutazioneList(JSONArray response) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
		for(int i = 0; i < response.length(); i++) {
			try {
				valutazioneList.add(new Valutazione(
						Integer.valueOf(response.getJSONObject(i).getInt("id")), 
						response.getJSONObject(i).getString("categoria"),
						response.getJSONObject(i).getString("sottocategoria"),
						Float.valueOf(response.getJSONObject(i).getString("prezzo")), 
						formatter.parseDateTime(response.getJSONObject(i).getString("data_inizio")),
						formatter.parseDateTime(response.getJSONObject(i).getString("data_fine")),
						response.getJSONObject(i).getString("descrizione"),
						null,
						response.getJSONObject(i).getString("codiceBarre"),
						response.getJSONObject(i).getString("supermercato"),
						response.getJSONObject(i).getString("supermercato_indirizzo")));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		valutazioneAdapter.notifyDataSetChanged();
		IsLoading = false;
	}



	private void registraClick() {
		ListView list = (ListView) rootView.findViewById(R.id.valutazioniListView);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				RelativeLayout transparentOverlay = (RelativeLayout) view.findViewById(R.id.valutazione_item_relative_layout_transparentOverlay);
				if(transparentOverlay.getVisibility() != View.VISIBLE) {
					Valutazione val = valutazioneList.get(position);
					FragmentManager manager = getFragmentManager();
					ValutazioneDettagliDialog dialog = ValutazioneDettagliDialog.getInstance(ValutaInserzioneFragment.this);

					Bundle args = new Bundle();
					args.putSerializable("dialogInterface", dialog.getArguments().getSerializable("dialogInterface"));
					args.putParcelable("valutazione", val);
					args.putInt("posizione", position);

					dialog.setArguments(args);
					dialog.show(manager, "myDialog");
				}
			}
		});
	}

	@Override
	public void onDialogPositiveClick(ValutazioneDettagliDialog dialog) {
		inviaValutazione(dialog.getValutazione().getIdInserzione(), dialog.getPosizione(), +1);
	}


	@Override
	public void onDialogNegativeClick(ValutazioneDettagliDialog dialog) {
		inviaValutazione(dialog.getValutazione().getIdInserzione(), dialog.getPosizione(), -1);
	}


	private void inviaValutazione(int idInserzioneValutata, int posizione, int risultato) {
		System.out.println("ID_INSERZIONE " + idInserzioneValutata);
		RequestParams params = new RequestParams();
		params.put("idInserzione", String.valueOf(idInserzioneValutata));
		params.put("risultato", String.valueOf(risultato));
		params.put("posizione", String.valueOf(posizione));
		
		MyHttpClient.post("/valutazione/aggiungiValutazione", params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray response) {
				Toast.makeText(getActivity().getApplicationContext(),"Valutazione ricevuta. Grazie!", Toast.LENGTH_LONG).show();
				try {
					settaInserzioneValutata(response.getInt(0), response.getInt(1));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void settaInserzioneValutata(int idInserzione, int posizione){
		System.out.println(posizione);
		View itemView = (View) listView.getChildAt(posizione);
		RelativeLayout transparentOverlay = (RelativeLayout) itemView.findViewById(R.id.valutazione_item_relative_layout_transparentOverlay);
		RelativeLayout contentElement = (RelativeLayout) itemView.findViewById(R.id.valutazione_item_relative_layout_contentElement);
		contentElement.setAlpha(0.2f);
		transparentOverlay.setVisibility(1);
	}
	
	private class ValutazioneAdapeter extends ArrayAdapter<Valutazione> {

		public ValutazioneAdapeter() {
			super(getActivity().getApplicationContext(), R.layout.valutazione_item_view, valutazioneList);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View itemView = convertView;
			if(itemView == null) 
				itemView = getActivity().getLayoutInflater().inflate(R.layout.valutazione_item_view, parent, false);

			// cerco la valutazione che la quale devo lavorare
			Valutazione valutazione = valutazioneList.get(position);

			// riempio la sua vista
			TextView descrizione = (TextView) itemView.findViewById(R.id.valutazione_item_descrizione);
			TextView data_fine = (TextView) itemView.findViewById(R.id.valutazione_item_data_fine);
			TextView prezzo = (TextView) itemView.findViewById(R.id.valutazione_item_prezzo);
			ImageView foto = (ImageView) itemView.findViewById(R.id.valutazione_item_foto);

			descrizione.setText(valutazione.getDescrizione());
			data_fine.setText(valutazione.getDataFine().toString());
			prezzo.setText(Float.toString(valutazione.getPrezzo()));
			return itemView;
		}
	}
}