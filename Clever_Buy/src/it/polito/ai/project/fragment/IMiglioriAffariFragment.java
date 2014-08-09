package it.polito.ai.project.fragment;

import it.polito.ai.project.R;
import it.polito.ai.project.fragment.DialogDettagliIMiglioriAffari.MyDialogInterface;
import it.polito.ai.project.main.ItemHintListFragment;
import it.polito.ai.project.main.MainActivity;
import it.polito.ai.project.main.MyHttpClient;
import it.polito.ai.project.model.InserzioneInScadenza;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nhaarman.listviewanimations.itemmanipulation.AnimateDismissAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.OnDismissCallback;

import android.app.Fragment;
import android.app.FragmentManager;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class IMiglioriAffariFragment  extends Fragment  implements MyDialogInterface{

	private final int AUTOLOAD_THRESHOLD = 7;
	private boolean IsLoading = false;
	private boolean MoreDataAvailable = true;

	private View rootView;
	private ListView listView;
	private View footerView;

	private List<InserzioneInScadenza> iMiglioriAffariList;
	private ArrayList<Integer> idIMiglioriAffariList;
	private ArrayAdapter<InserzioneInScadenza> inserzioniIMiglioriAffariArrayAdapter;
	private AnimateDismissAdapter animateDismissAdapter;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_i_migliori_affari, container, false);
		listView = (ListView) rootView.findViewById(R.id.iMiglioriAffariListView);
		footerView = inflater.inflate(R.layout.listview_footer, null, false);

		idIMiglioriAffariList = new ArrayList<Integer>();

		iMiglioriAffariList = new ArrayList<InserzioneInScadenza>();

		inserzioniIMiglioriAffariArrayAdapter = new InserzioniInScadenzaAdapter();

		animateDismissAdapter = new AnimateDismissAdapter(inserzioniIMiglioriAffariArrayAdapter, new MyOnDismissCallback());
		animateDismissAdapter.setAbsListView(listView);

		listView.setAdapter(animateDismissAdapter);
		registerListenersOnListView();
		popolaInserzioniInScadenzaList();
		return rootView;
	}



	private void popolaInserzioniInScadenzaList() {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
		ArrayList<ItemHintListFragment> tmp = MainActivity.getMiglioriAffari_itemArrayList();
		for(ItemHintListFragment i : tmp) {
			System.out.println(i.getItem_id());
			InserzioneInScadenza inserzione = new InserzioneInScadenza(	
					i.getItem_id(), 
					Float.valueOf(i.getPrezzo()), 
					i.getData_fine(),
					i.getDescrizione(),
					i.getFoto(),
					i.getSupermercato(),
					"",
					null); 
			iMiglioriAffariList.add(inserzione);
		}

		IsLoading = false;
		if(listView.getFooterViewsCount() != 0)
			listView.removeFooterView(footerView);
		inserzioniIMiglioriAffariArrayAdapter.notifyDataSetChanged();
	}

	private void registerListenersOnListView() {

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				InserzioneInScadenza ins = iMiglioriAffariList.get(position);
				if(ins.getNome_todolist() != null) {
					Toast.makeText(getActivity(), "Hai già aggiunto questo elemento alle tue todolist [in "+ ins.getNome_todolist() + "]", Toast.LENGTH_SHORT).show();
					return false;
				}

				FragmentManager manager = getFragmentManager();
				DialogDettagliIMiglioriAffari dialog = DialogDettagliIMiglioriAffari.getInstance(IMiglioriAffariFragment.this);
				Bundle args = new Bundle();
				args.putSerializable("dialogInterface", dialog.getArguments().getSerializable("dialogInterface"));
				args.putParcelable("inserzioneInScadenza", ins);
				args.putInt("posizione", position);
				dialog.setArguments(args);
				dialog.show(manager, "inserzioneInScadenzaDialog");

				return true;
			}
		}); 

	}


	@Override
	public void onDialogPositiveClick(DialogDettagliIMiglioriAffari dialog) {
		RequestParams params = new RequestParams();
		params.put("idInserzione", String.valueOf(dialog.getInserzione().getIdInserzione()));
		params.put("idListaDesideri", String.valueOf(dialog.getIdTodoListSelezionata()));
		params.put("posizione", String.valueOf(dialog.getPosizione()));

		MyHttpClient.post("/inscadenza/aggiungiElemento", params, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				try {
					Toast.makeText(getActivity().getApplicationContext(), "Elemento aggiunto con successo!", Toast.LENGTH_LONG).show();
					settaInserzioneComeAggiunta(response.getJSONObject(0).getInt("posizione"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				Log.v("ERROR" , "onFailure error : " + throwable.getMessage() + " \n content : " + responseString);
			}
		});
	}


	@Override
	public void onDialogNegativeClick(DialogDettagliIMiglioriAffari dialog) {

	}

	private void settaInserzioneComeAggiunta(int posizione) {
		animateDismissAdapter.animateDismiss(posizione);
	}

	/*-----------------------------------  INNER CLASSES  -----------------------------------------------*/

	private class MyOnDismissCallback implements OnDismissCallback {

		@Override
		public void onDismiss(final AbsListView listView, final int[] reverseSortedPositions) {
			for (int position : reverseSortedPositions) {
				inserzioniIMiglioriAffariArrayAdapter.remove(inserzioniIMiglioriAffariArrayAdapter.getItem(position));
			}
		}

	}

	private class InserzioniInScadenzaAdapter extends ArrayAdapter<InserzioneInScadenza> {

		public InserzioniInScadenzaAdapter() {
			super(getActivity().getApplicationContext(), R.layout.fragment_i_migliori_affari_item, iMiglioriAffariList);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View itemView = convertView;

			if(itemView == null) 
				itemView = getActivity().getLayoutInflater().inflate(R.layout.fragment_i_migliori_affari_item, parent, false);

			// cerco l'inserzione  con la quale devo lavorare
			InserzioneInScadenza inserzione = iMiglioriAffariList.get(position);

			// riempio la sua vista
			TextView descrizione = (TextView) itemView.findViewById(R.id.listview_tv_descrizione);
			TextView data_fine = (TextView) itemView.findViewById(R.id.listview_tv_data_fine);
			TextView prezzo = (TextView) itemView.findViewById(R.id.listview_tv_prezzo);
			TextView supermercato = (TextView) itemView.findViewById(R.id.listview_tv_supermercato);
			ImageView foto = (ImageView) itemView.findViewById(R.id.listview_iv_foto);

			descrizione.setText(inserzione.getDescrizione());
			String tmpData = DateTimeFormat.forPattern("dd/MM/yyyy").print(inserzione.getDataFine());
			data_fine.setText( tmpData );
			prezzo.setText(Float.toString(inserzione.getPrezzo()) + " €");
			supermercato.setText(inserzione.getSupermercato());
			byte[] decodedString = Base64.decode(inserzione.getFoto(), Base64.DEFAULT);
			Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length); 
			foto.setImageBitmap(decodedByte);

			ImageView star = (ImageView) itemView.findViewById(R.id.listview_iv_star);
			if(inserzione.getNome_todolist() != null) 
				star.setVisibility(View.VISIBLE);
			else
				star.setVisibility(View.GONE);

			return itemView;
		}		

	}

}