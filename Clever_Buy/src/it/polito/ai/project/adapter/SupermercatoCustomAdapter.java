package it.polito.ai.project.adapter;

import it.polito.ai.project.R;
import it.polito.ai.project.model.Supermercato;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SupermercatoCustomAdapter extends ArrayAdapter<String> {

	private Activity activity;
	private ArrayList data;

	Supermercato tempValues = null;
	LayoutInflater inflater;

	public SupermercatoCustomAdapter(Activity mainActivity, int textViewResourceId, ArrayList objects) {
		super(mainActivity, textViewResourceId, objects);
		activity = mainActivity;
		data     = objects;

		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getDropDownView(int position, View convertView,ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	public View getCustomView(int position, View convertView, ViewGroup parent) {
		View row = inflater.inflate(R.layout.supermercato_custom_spinner, parent, false);

		tempValues = null;
		tempValues = (Supermercato) data.get(position);

		TextView nome = (TextView) row.findViewById(R.id.spinner_item_nome_supermercato);
		TextView indirizzo = (TextView) row.findViewById(R.id.spinner_item_indirizzo_supermercato);
		TextView id = (TextView) row.findViewById(R.id.spinner_item_id_supermercato);
		TextView distanza = (TextView) row.findViewById(R.id.spinner_item_distanza_supermercato);

		nome.setText(tempValues.getNome());
		indirizzo.setText(tempValues.getIndirizzo());
		id.setText(Integer.toString(tempValues.getId()));
		
		if(tempValues.getDistanza() <= 1)
			distanza.setText(new DecimalFormat("###").format(tempValues.getDistanza()/1000) + "m");
		else
			distanza.setText(new DecimalFormat("###.###").format(tempValues.getDistanza()) + "km");
		return row;

	}


}

