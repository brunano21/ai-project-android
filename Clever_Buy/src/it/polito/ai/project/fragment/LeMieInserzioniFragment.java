package it.polito.ai.project.fragment;

import it.polito.ai.project.R;
import it.polito.ai.project.main.MainActivity;
import it.polito.ai.project.main.MyHttpClient;
import it.polito.ai.project.model.MiaInserzione;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;









import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

public class LeMieInserzioniFragment extends Fragment{

	private final int AUTOLOAD_THRESHOLD = 7;
	private boolean IsLoading = false;
	private boolean MoreDataAvailable = true;

	private View rootView;
	private ListView listView;
	private View footerView;

	private ArrayList<Integer> idMiaInserzioneList;
	private List<MiaInserzione> miaInserzioneList;
	private ArrayAdapter<MiaInserzione> miaInserzioneArrayAdapter;

	private ProgressDialog progressDialog;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_le_mie_inserzioni, container, false);
		listView = (ListView) rootView.findViewById(R.id.leMieInserzioniListView);
		footerView = inflater.inflate(R.layout.listview_footer, null, false);

		idMiaInserzioneList = new ArrayList<Integer>();

		miaInserzioneList = new ArrayList<MiaInserzione>();
		miaInserzioneArrayAdapter = new MiaInserzioneArrayAdapter();

		getIdInserzioni();

		listView.setAdapter(miaInserzioneArrayAdapter);
		registerListenersOnListView();

		progressDialog = ProgressDialog.show(getActivity(), "Download", "Sto ricercando le tue inserzioni. Attendi...", false);

		return rootView;
	}


	private void getIdInserzioni() {
		MyHttpClient.get("/lemieinserzioni/getIdInserzioni", null, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				if(response.length() != 0) {
					for(int i = 0; i<response.length(); i++)
						try {
							idMiaInserzioneList.add(Integer.valueOf(response.getInt(i)));
						} catch (JSONException e) {
							e.printStackTrace();
						}
					System.out.println("getIdInserzioni(): idInserzioneList.size() = " + idMiaInserzioneList.size());
					getInserzioniById(0);
				}
				else {
					TextView tv = new TextView(getActivity());
					tv.setText("Spiacenti, ma non hai ancora inserito alcuna inserzione.");
					tv.setTextSize(2, 22);
					tv.setTypeface(null, Typeface.ITALIC);
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

		idProssimaInserzioneList = idMiaInserzioneList.subList(count, (count + AUTOLOAD_THRESHOLD) < idMiaInserzioneList.size() ? (count + AUTOLOAD_THRESHOLD) : idMiaInserzioneList.size());
		if(idProssimaInserzioneList.size() == 0)
			return;

		StringBuilder sb = new StringBuilder();
		for(Integer s : idProssimaInserzioneList)
			sb.append(s).append(",");

		RequestParams params = new RequestParams();
		params.put("idInserzioneList", sb.toString().substring(0, sb.toString().length()-1));
		MyHttpClient.get("/lemieinserzioni/getInserzioneById", params, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				popolaMiaInserzioneList(response);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				Log.v("ERROR" , "onFailure error : " + throwable.getMessage() + " \n content : " + responseString);
				Toast.makeText(getActivity(), "Ops, c'è stato un problema con il server.", Toast.LENGTH_SHORT).show();
			}

		});
	}

	private void popolaMiaInserzioneList(JSONArray response) {
		for(int i = 0; i < response.length(); i++) {
			try {
				System.out.println(response.getJSONObject(i).getString("data_inizio"));
				System.out.println("ID_INSERZIONE: " + response.getJSONObject(i).getInt("id"));
				MiaInserzione inserzione = new MiaInserzione(Integer.valueOf(response.getJSONObject(i).getInt("id")),
						response.getJSONObject(i).getString("categoria"),
						response.getJSONObject(i).getString("sottocategoria"),
						Float.valueOf(response.getJSONObject(i).getString("prezzo")),
						DateTimeFormat.forPattern("yyyy/MM/dd").parseDateTime(response.getJSONObject(i).getString("data_inizio")),
						DateTimeFormat.forPattern("yyyy/MM/dd").parseDateTime(response.getJSONObject(i).getString("data_fine")),
						response.getJSONObject(i).getString("descrizione"),
						response.getJSONObject(i).getString("foto"),
						response.getJSONObject(i).getString("valutazioni_positive"),
						response.getJSONObject(i).getString("valutazioni_negative"));
				miaInserzioneList.add(inserzione);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		IsLoading = false;
		if(listView.getFooterViewsCount() != 0)
			listView.removeFooterView(footerView);
		miaInserzioneArrayAdapter.notifyDataSetChanged();
		progressDialog.dismiss();

	}

	private void registerListenersOnListView() {

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final MiaInserzione inserzione = miaInserzioneList.get(position);
				DateTime today = new  DateTime();
				//if(today.withTimeAtStartOfDay().isBefore(inserzione.getDataFine().withTimeAtStartOfDay()));

				if(Integer.valueOf(inserzione.getValutazioniPositive()) == 0 && Integer.valueOf(inserzione.getValutazioniNegative()) == 0 && !(DateTime.now().withTimeAtStartOfDay().isAfter(inserzione.getDataFine().withTimeAtStartOfDay()))) {
					// inserzione modificabile
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setIcon(android.R.drawable.ic_dialog_info);
					builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							((MainActivity) getActivity()).modificaInserzioneById(inserzione.getIdInserzione());
						}
					});
					builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// User cancelled the dialog
						}
					});
					builder.setTitle("Modificare?");
					builder.setMessage("Vuoi modificare la tua inserzione?");
					builder.setCancelable(false);
					AlertDialog dialog = builder.create();
					dialog.show();

				} else {
					// inserzione non modificabile
					Toast.makeText(getActivity(), "Inserzione non più modificabile!", Toast.LENGTH_SHORT).show();
				}


			}
		});

		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if(idMiaInserzioneList.size() != 0)
					if (!IsLoading && MoreDataAvailable) {
						if (totalItemCount >= idMiaInserzioneList.size()) {
							MoreDataAvailable = false;
							if(listView.getFooterViewsCount() != 0)
								listView.removeFooterView(footerView);
							miaInserzioneArrayAdapter.notifyDataSetChanged();
						} else if (totalItemCount <= firstVisibleItem + visibleItemCount) {
							IsLoading = true;
							listView.addFooterView(footerView);
							miaInserzioneArrayAdapter.notifyDataSetChanged();
							getInserzioniById(totalItemCount);
						}
					}
			}
		});
	}

	/*-----------------------------------  INNER CLASSES  -----------------------------------------------*/

	private class MiaInserzioneArrayAdapter extends ArrayAdapter<MiaInserzione> {

		public MiaInserzioneArrayAdapter() {
			super(getActivity().getApplicationContext(), R.layout.listview_item_mia_inserzione, miaInserzioneList);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View itemView = convertView;
			if(itemView == null) 
				itemView = getActivity().getLayoutInflater().inflate(R.layout.listview_item_mia_inserzione, parent, false);

			// cerco l'insezione con la quale devo lavorare
			MiaInserzione inserzione = miaInserzioneList.get(position);

			// riempio la sua vista
			ImageView foto = (ImageView) itemView.findViewById(R.id.listview_item_mia_inserzione_iv_foto);
			TextView descrizione = (TextView) itemView.findViewById(R.id.listview_item_mia_inserzione_tv_descrizione);
			TextView cat_sotto_cat = (TextView) itemView.findViewById(R.id.listview_item_mia_inserzione_tv_categoria_sottocategoria);
			TextView data_inizio = (TextView) itemView.findViewById(R.id.listview_item_mia_inserzione_tv_data_inizio);
			TextView data_fine = (TextView) itemView.findViewById(R.id.listview_item_mia_inserzione_tv_data_fine);
			TextView prezzo = (TextView) itemView.findViewById(R.id.listview_item_mia_inserzione_tv_prezzo);
			TextView val_pos = (TextView) itemView.findViewById(R.id.listview_item_mia_inserzione_tv_valutazioni_positive);
			TextView val_neg = (TextView) itemView.findViewById(R.id.listview_item_mia_inserzione_tv_valutazioni_negative);

			descrizione.setText(inserzione.getDescrizione());
			cat_sotto_cat.setText(inserzione.getCategoria() + " > " + inserzione.getSottocategoria());
			data_inizio.setText(DateTimeFormat.forPattern("dd-MM-yyyy").print(inserzione.getDataInizio()));

			if(DateTime.now().withTimeAtStartOfDay().isAfter(inserzione.getDataFine().withTimeAtStartOfDay())) {
				data_fine.setTextColor(getResources().getColor(R.color.red));
				data_fine.setText("Scaduta!");
			}
			else {
				data_fine.setTextColor(data_inizio.getTextColors().getDefaultColor());
				data_fine.setText(DateTimeFormat.forPattern("dd-MM-yyyy").print(inserzione.getDataFine()));
			}

			prezzo.setText(Float.toString(inserzione.getPrezzo()) + " €");

			byte[] decodedString = Base64.decode(inserzione.getFoto(), Base64.DEFAULT);
			Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length); 
			foto.setImageBitmap(decodedByte);

			val_pos.setText(inserzione.getValutazioniPositive());
			val_neg.setText(inserzione.getValutazioniNegative());

			return itemView;
		}
	}

}



