package it.polito.ai.project.fragment;

import it.polito.ai.project.R;
import it.polito.ai.project.fragment.DialogDettagliValutazioneInserzione.MyDialogInterface;
import it.polito.ai.project.main.MainActivity;
import it.polito.ai.project.main.MyHttpClient;
import it.polito.ai.project.main.UserSessionManager;
import it.polito.ai.project.model.InserzioneDaValutare;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
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
import com.nhaarman.listviewanimations.itemmanipulation.AnimateDismissAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.OnDismissCallback;

public class ValutaInserzioneFragment extends Fragment implements MyDialogInterface{ 

	private final int AUTOLOAD_THRESHOLD = 7;
	private boolean IsLoading = false;
	private boolean MoreDataAvailable = true;

	private View rootView;
	private View footerView; 
	private ListView listView;

	private List<InserzioneDaValutare> valutazioneList;
	private ArrayList<Integer> idInserzioneList;
	private ArrayAdapter<InserzioneDaValutare> valutazioneArrayAdapter;
	private AnimateDismissAdapter animateDismissAdapter;

	private ProgressDialog progressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_valuta_inserzione, container, false);
		listView = (ListView) rootView.findViewById(R.id.valutazioniListView);
		footerView = inflater.inflate(R.layout.listview_footer, null, false);

		idInserzioneList = new ArrayList<Integer>();

		valutazioneList = new ArrayList<InserzioneDaValutare>();
		valutazioneArrayAdapter = new ValutazioneAdapter();

		getIdInserzioni();

		animateDismissAdapter = new AnimateDismissAdapter(valutazioneArrayAdapter, new MyOnDismissCallback());
		animateDismissAdapter.setAbsListView(listView);
		listView.setAdapter(animateDismissAdapter);
		registerListenersOnListView();

		progressDialog = ProgressDialog.show(getActivity(), "Download", "Sto ricercando nel sistema le inserzioni che potresti valutare. Attendi...", false);

		return rootView;
	}

	private void getIdInserzioni() {
		RequestParams params = new RequestParams();
		params.put("lat", Double.toString(MainActivity.getLocation().getLatitude()));
		params.put("lng", Double.toString(MainActivity.getLocation().getLongitude()));

		MyHttpClient.get("/valutazione/getIdInserzioni", params, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				if(response.length() != 0) {
					for(int i = 0; i<response.length(); i++)
						try {
							idInserzioneList.add(Integer.valueOf(response.getInt(i)));
						} catch (JSONException e) {
							e.printStackTrace();
						}
					System.out.println("getIdInserzioni(): idInserzioneList.size() = " + idInserzioneList.size());
					getInserzioniById(0);
				}
				else {
					TextView tv = new TextView(getActivity());
					tv.setText("Spiacenti, ma non è stata trovata alcuna inserzione in scadenza nei supermercati vicini a te.");
					tv.setTypeface(null, Typeface.ITALIC);
					tv.setTextSize(2, 22);
					tv.setGravity(Gravity.CENTER);
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
					params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
					params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
					((RelativeLayout) rootView).addView(tv, params);
					listView.setVisibility(View.GONE);
					progressDialog.dismiss();

				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				Log.v("ERROR" , "onFailure error : " + throwable.getMessage() + " \n content : " + responseString);
				Toast.makeText(getActivity(), "Ops, c'è stato un problema con il server.", Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void getInserzioniById(int totalItemCount) {
		int count = totalItemCount;
		List<Integer> idProssimaInserzioneList = new ArrayList<Integer>();

		System.out.println("getInserzioniById(): totalItemCount =  " + count);
		System.out.println("getInserzioniById(): idInserzioneList.size() = " + idInserzioneList.size());

		idProssimaInserzioneList = idInserzioneList.subList(count, (count + AUTOLOAD_THRESHOLD) < idInserzioneList.size() ? (count + AUTOLOAD_THRESHOLD) : idInserzioneList.size());
		System.out.println("getInserzioniById(): idProssimaInserzioneList.size() = " + idProssimaInserzioneList.size());

		if(idProssimaInserzioneList.size() == 0)
			return;

		StringBuilder sb = new StringBuilder();
		for(Integer s : idProssimaInserzioneList)
			sb.append(s).append(",");

		RequestParams params = new RequestParams();
		params.put("idInserzioneList", sb.toString().substring(0, sb.toString().length()-1));
		MyHttpClient.get("/valutazione/getInserzioneById", params, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				popolaValutazioneList(response);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				Log.v("ERROR" , "onFailure error : " + throwable.getMessage() + " \n content : " + responseString);
				Toast.makeText(getActivity(), "Ops, c'è stato un problema con il server.", Toast.LENGTH_SHORT).show();
			}

		});
	}

	private void popolaValutazioneList(JSONArray response) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
		for(int i = 0; i < response.length(); i++) {
			try {
				System.out.println("ID_INSERZIONE: " + response.getJSONObject(i).getInt("id"));
				valutazioneList.add(new InserzioneDaValutare(
						Integer.valueOf(response.getJSONObject(i).getInt("id")), 
						response.getJSONObject(i).getString("categoria"),
						response.getJSONObject(i).getString("sottocategoria"),
						Float.valueOf(response.getJSONObject(i).getString("prezzo")), 
						formatter.parseDateTime(response.getJSONObject(i).getString("data_inizio")),
						formatter.parseDateTime(response.getJSONObject(i).getString("data_fine")),
						response.getJSONObject(i).getString("descrizione"),
						response.getJSONObject(i).getString("foto"),
						response.getJSONObject(i).getString("codiceBarre"),
						response.getJSONObject(i).getString("supermercato"),
						response.getJSONObject(i).getString("supermercato_indirizzo")));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		IsLoading = false;
		if(listView.getFooterViewsCount() != 0)
			listView.removeFooterView(footerView);
		valutazioneArrayAdapter.notifyDataSetChanged();
		progressDialog.dismiss();
	}



	private void registerListenersOnListView() {

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				InserzioneDaValutare val = valutazioneList.get(position);
				FragmentManager manager = getFragmentManager();
				DialogDettagliValutazioneInserzione dialog = DialogDettagliValutazioneInserzione.getInstance(ValutaInserzioneFragment.this);

				Bundle args = new Bundle();
				args.putSerializable("dialogInterface", dialog.getArguments().getSerializable("dialogInterface"));
				args.putParcelable("valutazione", val);
				args.putInt("posizione", position);

				dialog.setArguments(args);
				dialog.show(manager, "myDialog");

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
				if(idInserzioneList.size() != 0)
					if (!IsLoading && MoreDataAvailable) {
						System.out.println("onScroll(): totalItemCount >= idInserzioneList.size() ?? " + totalItemCount + "vs" + idInserzioneList.size());
						if (totalItemCount >= idInserzioneList.size()) {
							MoreDataAvailable = false;
							if(listView.getFooterViewsCount() != 0)
								listView.removeFooterView(footerView);
							valutazioneArrayAdapter.notifyDataSetChanged();
						} else if (totalItemCount <= firstVisibleItem + visibleItemCount) {
							IsLoading = true;
							listView.addFooterView(footerView);
							valutazioneArrayAdapter.notifyDataSetChanged();
							getInserzioniById(totalItemCount);
						}
					}
			}
		});


	}

	@Override
	public void onDialogPositiveClick(DialogDettagliValutazioneInserzione dialog) {
		inviaValutazione(dialog.getValutazione().getIdInserzione(), dialog.getPosizione(), +1);
	}


	@Override
	public void onDialogNegativeClick(DialogDettagliValutazioneInserzione dialog) {
		inviaValutazione(dialog.getValutazione().getIdInserzione(), dialog.getPosizione(), -1);
	}


	private void inviaValutazione(int idInserzioneValutata, int posizione, int risultato) {
		RequestParams params = new RequestParams();
		params.put("idInserzione", String.valueOf(idInserzioneValutata));
		params.put("risultato", String.valueOf(risultato));
		params.put("posizione", String.valueOf(posizione));

		MyHttpClient.post("/valutazione/aggiungiValutazione", params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				Toast.makeText(getActivity().getApplicationContext(), "Valutazione ricevuta. Grazie!", Toast.LENGTH_LONG).show();
				try {
					settaInserzioneValutata(response.getInt(1), response.getString(2));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				Log.v("ERROR" , "onFailure error : " + throwable.getMessage() + " \n content : " + responseString);
				Toast.makeText(getActivity(), "Ops, c'è stato un problema con il server.", Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void settaInserzioneValutata(int posizione, String result){
		animateDismissAdapter.animateDismiss(posizione);
		UserSessionManager session = new UserSessionManager(getActivity());
		session.setUserData(UserSessionManager.KEY_CREDITI_PENDENTI, session.getUserData(UserSessionManager.KEY_CREDITI_PENDENTI) + 2);
		session.setUserData(UserSessionManager.KEY_NUMERO_VALUTAZIONI_TOTALI, session.getUserData(UserSessionManager.KEY_NUMERO_VALUTAZIONI_TOTALI) + 1);
		if(result.equals("+1"))
			session.setUserData(UserSessionManager.KEY_NUMERO_VALUTAZIONI_POSITIVE, session.getUserData(UserSessionManager.KEY_NUMERO_VALUTAZIONI_POSITIVE) + 1);
	}

	/*-----------------------------------  INNER CLASSES  -----------------------------------------------*/

	private class MyOnDismissCallback implements OnDismissCallback {

		@Override
		public void onDismiss(final AbsListView listView, final int[] reverseSortedPositions) {
			for (int position : reverseSortedPositions) {
				valutazioneArrayAdapter.remove(valutazioneArrayAdapter.getItem(position));
			}
		}

	}

	private class ValutazioneAdapter extends ArrayAdapter<InserzioneDaValutare> {

		public ValutazioneAdapter() {
			super(getActivity().getApplicationContext(), R.layout.listview_item_inserzione_da_valutare, valutazioneList);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View itemView = convertView;
			if(itemView == null) 
				itemView = getActivity().getLayoutInflater().inflate(R.layout.listview_item_inserzione_da_valutare, parent, false);

			// cerco la valutazione con la quale devo lavorare
			InserzioneDaValutare valutazione = valutazioneList.get(position);

			// riempio la sua vista
			TextView descrizione = (TextView) itemView.findViewById(R.id.listview_item_inserzione_da_valutare_tv_descrizione);
			TextView data_fine = (TextView) itemView.findViewById(R.id.listview_item_inserzione_da_valutare_tv_data_fine);
			TextView prezzo = (TextView) itemView.findViewById(R.id.listview_item_inserzione_da_valutare_tv_prezzo);
			ImageView foto = (ImageView) itemView.findViewById(R.id.listview_item_inserzione_da_valutare_iv_foto);

			descrizione.setText(valutazione.getDescrizione());
			data_fine.setText(DateTimeFormat.forPattern("dd/MM/yyyy").print(valutazione.getDataFine()));
			prezzo.setText(Float.toString(valutazione.getPrezzo()) + " €");

			byte[] decodedString = Base64.decode(valutazione.getFoto(), Base64.DEFAULT);
			Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length); 
			foto.setImageBitmap(decodedByte);

			return itemView;
		}
	}
}
