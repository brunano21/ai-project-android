package it.polito.ai.project.adapter;

import it.polito.ai.project.R;
import it.polito.ai.project.main.ItemListFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Paint;
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
import android.widget.Toast;


public class ItemAdapterListFragment extends ArrayAdapter<ItemListFragment> {
	private int resource;
	private LayoutInflater inflater;
	private List<ItemListFragment> items;

	private Map<Integer, ImageButton> mapOfHint;

	public ItemAdapterListFragment(Context context, int resourceId, List<ItemListFragment> items) {
		super(context, resourceId, items);
		resource = resourceId;
		inflater = LayoutInflater.from(context);
		this.items = items;
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {

		ViewHolder holder = null;

		final ItemListFragment item = getItem(position);

		v = inflater.inflate(resource, parent, false);

		holder = new ViewHolder();
		holder.item = items.get(position);
		holder.acquistato = (CheckBox) v.findViewById(R.id.checkBox_item_acquistato);
		holder.nameTextView = (TextView) v.findViewById(R.id.text_item_name);
		holder.quantityTextView = (TextView) v.findViewById(R.id.text_item_quantity);
		holder.deleteItem = (ImageButton) v.findViewById(R.id.img_item_remove);
		holder.hint = (ImageButton) v.findViewById(R.id.img_item_hint);

		holder.deleteItem.setTag(holder.item);

		v.setTag(holder);

		holder.acquistato.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO   - COD_008_todo - invia al server l'info di oggetto acquistato

				//ItemListFragment itemToCut = (ItemListFragment) getTag();
				//itemToCut.setAcquistato(isChecked);
				item.setAcquistato(isChecked);

				notifyDataSetChanged();
			}
		});
		if(item.isAcquistato())
		{	
			if(!"".equals(holder.nameTextView))
			{
				holder.nameTextView.setPaintFlags(holder.nameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
				holder.quantityTextView.setPaintFlags(holder.quantityTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
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

		holder.acquistato.setChecked( item.isAcquistato() );
		holder.nameTextView.setText(item.getItem_name());
		holder.quantityTextView.setText( String.valueOf(item.getItem_quantity()) );
		holder.deleteItem.setTag(holder.item);
		holder.deleteItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ItemListFragment itemToRemove = (ItemListFragment)v.getTag();
				Toast.makeText(getContext(), "delete "+itemToRemove.getItem_name(), Toast.LENGTH_SHORT).show();
				remove(itemToRemove);
				notifyDataSetChanged();
			}
		});


		holder.hint.setTag(holder.item);
		holder.hint.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO  - COD_005_todo - 
				Toast.makeText(getContext(), " - COD_005_todo - se cliccato vai alla schermata dei suggerimenti - TODO", Toast.LENGTH_SHORT).show();

			}
		});
		holder.hint.setImageResource(item.isHint_is_present() ? R.drawable.ic_help_hint_sun : R.drawable.ic_help_hint );


		return v;
	}

	private static class ViewHolder {
		ItemListFragment item;
		CheckBox acquistato;
		TextView nameTextView;
		TextView quantityTextView;
		ImageButton hint;
		ImageButton deleteItem;
	}
}
