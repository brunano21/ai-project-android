package it.polito.ai.project.fragment;

import it.polito.ai.project.R;
import it.polito.ai.project.main.ItemHintListFragment;
import it.polito.ai.project.main.MainActivity;
import it.polito.ai.project.main.MyHttpClient;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class DialogHint extends Dialog {

	private ArrayList<ItemHintListFragment> hint_itemArrayList;
	private ItemHintAdapterListFragment hint_itemAdapter;

	private ListView _hint_listView;
	private Button _dialogButton;

	public myOnClickListener myListener;
	
	private String id_elemento;
	private String descrizione;

	private ProgressDialog progressDialog;
	
	public DialogHint(Context context, myOnClickListener myclick, String descrizione) {
        super(context);
        this.myListener = myclick;
        this.descrizione = descrizione;
    }

	   // This is my interface //
    public interface myOnClickListener {
        void onButtonClick(ItemHintListFragment item);
    }
 
	@Override
	public void onCreate(Bundle savedInstanceState) {
 		setContentView(R.layout.fragment_list_dialog_hint);
		setTitle("Ricerca Clever");
		
		_dialogButton = (Button) findViewById(R.id.dialog_hint_dialogButtonOK);
		// Watch for button clicks.
		_dialogButton.setOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		
		_hint_listView = (ListView) findViewById(R.id.dialog_hint_lv_items);

		hint_itemAdapter = new ItemHintAdapterListFragment( getContext(), R.layout.fragment_list_dialog_hint_item, getHintsFromServer(Double.toString(MainActivity.getLocation().getLatitude()),Double.toString(MainActivity.getLocation().getLongitude()),descrizione));
		_hint_listView.setAdapter(hint_itemAdapter);
	}

	private List<ItemHintListFragment> getHintsFromServer(String longitudine, String latitudine, String descrizione) {
		progressDialog = ProgressDialog.show(getContext(), "Loading", "Login in corso...", false);
		
		hint_itemArrayList = new ArrayList<ItemHintListFragment>();
		RequestParams param = new RequestParams();
		String NULL = null;
		param.put("cmd","ottieni_suggerimenti");
		param.put("latitudine",latitudine);
		param.put("longitudine",longitudine);
		param.put("id_elemnto",NULL);
		param.put("descrizione",descrizione);
		
		MyHttpClient.post("/todolist", param, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				progressDialog.dismiss();
				for (int i = 0; i < response.length(); i++) 
					try {
						if(response.getJSONObject(i).has("id_elemento"))
							continue;
						ItemHintListFragment hint = null;
						DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
						int id_inserzione = response.getJSONObject(i).getInt("id_inserzione");
						boolean selezionato = false;
						String descrizione = response.getJSONObject(i).getString("descrizione");
						DateTime data_fine = formatter.parseDateTime(response.getJSONObject(i).getString("data_fine"));
						String supermercato = response.getJSONObject(i).getString("supermercato");
						String prezzo = response.getJSONObject(i).getString("prezzo");
						String foto = response.getJSONObject(i).getString("foto");
						hint = new ItemHintListFragment(id_inserzione, selezionato, descrizione, data_fine, supermercato, prezzo, foto);
						hint_itemArrayList.add(hint);
					} catch (Exception e) {
						e.printStackTrace();
					}
				_hint_listView.setAdapter(hint_itemAdapter);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				progressDialog.dismiss();
				Toast.makeText(getContext(), "Errore connessione - suggerimenti non ricevuti", Toast.LENGTH_SHORT).show();
				Log.v("ERROR" , "onFailure error : " + throwable.getMessage() + " \n content : " + responseString);
			}
		});

		return hint_itemArrayList;
	}
	
	
	
	
	class ItemHintAdapterListFragment  extends ArrayAdapter<ItemHintListFragment> {
		private int resource;
		private LayoutInflater inflater;
		private List<ItemHintListFragment> items;
		
		public ItemHintAdapterListFragment(Context context, int resourceId, List<ItemHintListFragment> items) {
			super(context, resourceId, items);
			resource = resourceId;
			inflater = LayoutInflater.from(context);
			this.items = items;
		}
		

		@Override
		public View getView(int position, View v, ViewGroup parent) {

			ViewHolder holder = null;

			final ItemHintListFragment item = getItem(position);

			v = inflater.inflate(resource, parent, false);

			holder = new ViewHolder();
			holder.item = items.get(position);
			holder.data_fine = (TextView) v.findViewById(R.id.list_dialog_item_tv_data_fine);
			holder.descrizione = (TextView) v.findViewById(R.id.list_dialog_item_tv_descrizione);
			holder.prezzo = (TextView) v.findViewById(R.id.list_dialog_item_tv_prezzo);
			holder.supermercato = (TextView) v.findViewById(R.id.list_dialog_item_tv_supermercato);
			holder.foto = (ImageView) v.findViewById(R.id.list_dialog_item_iv_foto);
			holder.buttonAggiungi = (Button) v.findViewById(R.id.list_dialog_item_b_aggiungi);
			
			holder.data_fine.setText( DateTimeFormat.forPattern("dd/MM/yyyy").print( holder.item.getData_fine()) );
			holder.descrizione.setText(holder.item.getDescrizione() );
			holder.prezzo.setText(holder.item.getPrezzo() + " €");
			holder.supermercato.setText(holder.item.getSupermercato() );
			holder.seleziona = item.isSelezionato();
				
			if(!"".equals(holder.item.getFoto())) {
				byte[] decodedString = Base64.decode(holder.item.getFoto(), Base64.DEFAULT);
				Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length); 
				holder.foto.setImageBitmap(decodedByte);
			}
			
			holder.buttonAggiungi.setTag(holder.item);
			v.setTag(holder);

			holder.buttonAggiungi.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					myListener.onButtonClick( item  );
					dismiss();		
				}
			});
			
		
	     
			

			return v;
		}

		
	}
	
	private static class ViewHolder {
			ItemHintListFragment item;
			ImageView foto;
			TextView descrizione;
			TextView data_fine;
			TextView prezzo;
			TextView supermercato;
			Button buttonAggiungi;
			Boolean seleziona;
			
		}
}
