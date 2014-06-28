package it.polito.ai.project.adapter;

import java.util.List;

import it.polito.ai.project.R;
import it.polito.ai.project.main.ItemSpinnerAllList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ItemAllListSpinnerAdapter  extends ArrayAdapter<ItemSpinnerAllList>{
	private int resource;
	private LayoutInflater inflater;
	private List<ItemSpinnerAllList> items;
	
	public ItemAllListSpinnerAdapter(Context context, int resourceId, List<ItemSpinnerAllList> items) {
		super(context, resourceId, items);
		resource = resourceId;
		inflater = LayoutInflater.from(context);
		this.items = items;
	}
	
	

	

	@Override
	public View getDropDownView(int position, View convertView,ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}


	public View getCustomView(int position, View v, ViewGroup parent) {
			
		ViewHolder holder = null;
		if(v==null)
			v = inflater.inflate(resource, parent, false);

		holder = new ViewHolder();
		holder.item = items.get(position);
		
		holder.nome = (TextView) v.findViewById(R.id._spinner_allList_tv_item);
		holder.nome.setText(holder.item.getNome());
		
		v.setTag(holder);
						
		return v;
	}

	private static class ViewHolder {
		ItemSpinnerAllList item;
		TextView nome;
	}
}