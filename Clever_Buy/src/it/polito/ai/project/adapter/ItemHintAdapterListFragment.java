package it.polito.ai.project.adapter;

import it.polito.ai.project.R;
import it.polito.ai.project.main.ItemHintListFragment;

import java.util.List;

import org.joda.time.format.DateTimeFormat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

public class ItemHintAdapterListFragment  extends ArrayAdapter<ItemHintListFragment> {
	private int resource;
	private LayoutInflater inflater;
	private List<ItemHintListFragment> items;
	private boolean userSelected = false;
    private RadioButton mCurrentlyCheckedRB;
	
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
		holder.prezzo.setText(holder.item.getPrezzo() );
		holder.supermercato.setText(holder.item.getSupermercato() );
		holder.seleziona = item.isSelezionato();
				
		if(!"".equals(holder.item.getFoto()))
		{
			byte[] decodedString = Base64.decode(holder.item.getFoto(), Base64.DEFAULT);
			Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length); 
			holder.foto.setImageBitmap(decodedByte);
		}
		
		

		v.setTag(holder);

		holder.buttonAggiungi.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		
     
		

		return v;
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