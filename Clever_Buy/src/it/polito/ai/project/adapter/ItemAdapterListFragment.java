package it.polito.ai.project.adapter;

import it.polito.ai.project.R;
import it.polito.ai.project.fragment.DialogHint;
import it.polito.ai.project.fragment.DialogHint.myOnClickListener;
import it.polito.ai.project.fragment.DialogHintSingolo;
import it.polito.ai.project.fragment.DialogHintSingolo.mySingoloOnClickListener;
import it.polito.ai.project.main.ItemHintListFragment;
import it.polito.ai.project.main.ItemListFragment;
import it.polito.ai.project.main.MyHttpClient;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.TextView;


public class ItemAdapterListFragment extends ArrayAdapter<ItemListFragment> {
	private int resource;
	private LayoutInflater inflater;
	private List<ItemListFragment> items;


	public myOnClickListener myListener;
	 
	
	public ItemAdapterListFragment(Context context, int resourceId, List<ItemListFragment> items) {
		super(context, resourceId, items);
		resource = resourceId;
		inflater = LayoutInflater.from(context);
		this.items = items;
	}

	@Override
	public View getView(final int position, View v, ViewGroup parent) {

		ViewHolder holder = null;
		
		final ItemListFragment item = getItem(position);

		v = inflater.inflate(resource, parent, false);

		holder = new ViewHolder();
		holder.item = items.get(position);
		holder.acquistato = (CheckBox) v.findViewById(R.id.checkBox_item_acquistato);
		holder.nameTextView = (TextView) v.findViewById(R.id.text_item_name);
		holder.quantityTextView = (TextView) v.findViewById(R.id.text_item_quantity);
		holder.supermercatoTextView = (TextView) v.findViewById(R.id.text_item_supermercato);
		holder.deleteItem = (ImageButton) v.findViewById(R.id.img_item_remove);
		holder.hint = (ImageButton) v.findViewById(R.id.img_item_hint);

		holder.deleteItem.setTag(holder.item);

		v.setTag(holder);
		
		holder.acquistato.setChecked( item.isAcquistato() );
		holder.acquistato.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				item.setAcquistato(isChecked);
				notifyDataSetChanged();
				inviaAcquistatoServer( item );
			}
		});
		if(item.isAcquistato())
		{	
			if(!"".equals(holder.nameTextView))
			{
				holder.nameTextView.setPaintFlags(holder.nameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
				holder.quantityTextView.setPaintFlags(holder.quantityTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
				holder.supermercatoTextView.setPaintFlags(holder.supermercatoTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			}
		}
		else
		{
			if(!"".equals(holder.nameTextView))
			{
				holder.nameTextView.setPaintFlags(holder.nameTextView.getPaintFlags());
				holder.quantityTextView.setPaintFlags(holder.nameTextView.getPaintFlags());
			}
		}

		
		holder.nameTextView.setText(item.getDescrizione());
		holder.quantityTextView.setText( String.valueOf(item.getQuantita()) );
		holder.deleteItem.setTag(holder.item);
		holder.deleteItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ItemListFragment itemToRemove = (ItemListFragment)v.getTag();
				//Toast.makeText(getContext(), "delete "+itemToRemove.getDescrizione(), Toast.LENGTH_SHORT).show();
				
				eliminaElemento( itemToRemove.getId_lista_desideri() ,itemToRemove.getId_elemento());
				//TODO inva info al server
				remove(itemToRemove);
				notifyDataSetChanged();
			}

			private void eliminaElemento(int id_lista_desideri, int id_elemento) {
				RequestParams param = new RequestParams();
				param.put("cmd","eliminaElemento");
				param.put("id_lista_desideri",String.valueOf(id_lista_desideri));
				param.put("id_elemento",String.valueOf(id_elemento));
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
		});

		holder.hint.setTag(holder.item);
		holder.hint.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// create new onclicklistener interface //
		        myListener = new myOnClickListener( ) {
		            @Override
		            public void onButtonClick( ItemHintListFragment item_hint ) {
		                items.get(position).setInserzione(item_hint,true);
		                notifyDataSetChanged();
		                setHintDescrizioneToServer(String.valueOf(item.getId_lista_desideri()), String.valueOf(item.getId_elemento()), String.valueOf(item.getInserzione().getItem_id()) );
		            }
		        };
		        if(item.getInserzione()!=null)
				{	
					mySingoloOnClickListener mySingoloListner = new mySingoloOnClickListener() {
						
						@Override
						public void onButtonClick(ItemHintListFragment item) {
							//TODO -> se volessi modificare l'inserzione devo fare un passo in dietro:
							//        - nel server non deve essere modificata descrizione elemento. La stampo a video io, mettendo la precedenza alla descrizione che viene da Inserzione.
							//		  - solo così posso riusare la descrizione base per trovare altri oggetti nel DB
							
						}
					};
					
					DialogHintSingolo singoloDialog = new DialogHintSingolo(getContext(), mySingoloListner , item.getInserzione());//TODO nuovo fottuto dialog per vedere l'inserzione associata che permetta di eliminarla o modificarla
					singoloDialog.show();
				}	
		        else
		        if( item.isHint_is_present() )
		        {	
		        	// custom dialog per aprire i suggerimenti
		        	DialogHint newFragment = new DialogHint(getContext(), myListener, item.getDescrizione() );
		        	newFragment.show();
		        } 
		        
			}
		});
		
		if(holder.item.getInserzione()!=null)
		{
			holder.hint.setImageResource(R.drawable.ic_carrello);
			holder.supermercatoTextView.setVisibility(View.VISIBLE);
			holder.supermercatoTextView.setText(holder.item.getInserzione().getSupermercato() );
		}
		else
		{
			holder.hint.setImageResource(item.isHint_is_present() ? R.drawable.ic_help_hint_sun : R.drawable.ic_help_hint );
		}

		return v;
	}


	/*void showDialogHint() {
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
	
	protected void inviaAcquistatoServer(ItemListFragment item) {
		RequestParams param = new RequestParams();
		param.put("cmd","modificaFlagAcquistatoElemento");
		param.put("id_lista_desideri",String.valueOf(item.getId_lista_desideri()));
		param.put("id_elemento",String.valueOf(item.getId_elemento()));
		param.put("acquistato",String.valueOf(item.isAcquistato()));
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


	private static class ViewHolder {
		ItemListFragment item;
		CheckBox acquistato;
		TextView nameTextView;
		TextView quantityTextView;
		TextView supermercatoTextView;
		ImageButton hint;
		ImageButton deleteItem;
	}

	private void setHintDescrizioneToServer(String id_lista_desideri, String id_elemento, String id_inserzione) {
		RequestParams param = new RequestParams();
		param.put("cmd","aggiungiIDInserzione");
		param.put("id_lista_desideri",id_lista_desideri);
		param.put("id_elemento",id_elemento);
		param.put("id_inserzione",id_inserzione);
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
}
