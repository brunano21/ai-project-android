package it.polito.ai.project.fragment;


import java.util.ArrayList;

import it.polito.ai.project.adapter.ItemAdapterListFragment;
import it.polito.ai.project.andoidside.R;
import it.polito.ai.project.andoidside.R.layout;
import it.polito.ai.project.main.ItemListFragment;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class ListFragment extends Fragment {

	private View rootView;
	private Spinner _spinner_allList, _spinner_item_quantity;
	private EditText _edit_item_name, _edit_item_quantity;
	private Button  _button_addItem;
	private ListView _listView;
    ItemAdapterListFragment itemAdapter;
	
	public ListFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_list, container, false);

		_button_addItem = (Button) rootView.findViewById(R.id.button_addItem);
		
		_spinner_allList = (Spinner) rootView.findViewById(R.id.spinner_allList);
		_spinner_item_quantity = (Spinner) rootView.findViewById(R.id.spinner_item_quantity);

		_edit_item_name = (EditText) rootView.findViewById(R.id.edit_item_name);
		_edit_item_quantity = (EditText) rootView.findViewById(R.id.edit_item_quantity);
		
		_listView = (ListView) rootView.findViewById(R.id.itemListView_ListFragment);

        itemAdapter = new ItemAdapterListFragment( container.getContext(), R.layout.fragment_list_item, new ArrayList<ItemListFragment>());

        _listView.setAdapter(itemAdapter);

        new BackgroundWorker().execute();
		
		_button_addItem.setEnabled(false);
		
		addSpinner();
		addListnerOnTexts();
		addListenerOnButtons();

		return rootView;
	}

	private void addSpinner() {
		

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource( 
				getActivity(), R.array.list_item_quantity, android.R.layout.simple_spinner_item);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
		_spinner_item_quantity.setAdapter(adapter);
		
	}

	private void addListnerOnTexts() {
		TextWatcher onSearchFieldTextChanged = new TextWatcher(){
			public void afterTextChanged(Editable s) {
				//your business logic after text is changed
				if(!"".equals(s.toString()))
					_button_addItem.setEnabled(true);
				else
					_button_addItem.setEnabled(false);
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after){
				//your business logic before text is changed
			}

			public void onTextChanged(CharSequence s, int start, int before, int count){
				//your business logic while text has changed
			}
		};
		_edit_item_name.addTextChangedListener(onSearchFieldTextChanged);
		
	}

	private void addListenerOnButtons() {
		
		_button_addItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			//	Intent browserIntent = 
			//			new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
			//	startActivity(browserIntent);
				ItemListFragment i = new ItemListFragment(	_edit_item_name.getText().toString(), 
															_edit_item_quantity.getText().toString(),
															String.valueOf(_spinner_item_quantity.getSelectedItem()));
				itemAdapter.add(i);
				_edit_item_name.setText("");
				_edit_item_quantity.setText("");
				
			}

		});
	}
	
	
	
	
	
	
	
	private class BackgroundWorker extends AsyncTask<Void, ItemListFragment, Void> {

        @Override
        protected void onPreExecute() {
                // Prima di iniziare a inserire gli elementi svuotiamo l'adapter
                itemAdapter.clear();
                super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
//TODO
                // Qui dentro si possono mettere le operazioni che potrebbero
                // rallentare il caricamento della listview, come ad sempio il
                // caricamento da db degli oggetti

                //        publishProgress( oggetto di tipo ItemListFragment );
              
                return null;
        }
        
        @Override
        protected void onProgressUpdate(ItemListFragment... values) {
                // Aggiungiamo il progresso pubblicato all'adapter
                itemAdapter.add(values[0]);
                super.onProgressUpdate(values);
        }

}
	
}
