package it.polito.ai.project.fragment;

import java.util.ArrayList;

import org.joda.time.DateTime;

import it.polito.ai.project.R;
import it.polito.ai.project.adapter.ItemHintAdapterListFragment;
import it.polito.ai.project.fragment.DialogDettagliInserzioneInScadenza.MyDialogInterface;
import it.polito.ai.project.main.ItemHintListFragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

public class DialogHint  extends DialogFragment  {

	private ArrayList<ItemHintListFragment> hint_itemArrayList;
	private ItemHintAdapterListFragment hint_itemAdapter;

	private ListView _hint_listView;
	private Button _dialogButton;


	static DialogHint newInstance() {
		DialogHint f = new DialogHint();

        // Supply num input as an argument.
        //Bundle args = new Bundle();
       // args.putInt("num", num);
        //f.setArguments(args);

        return f;
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_list_dialog_hint, container, false);

		getDialog().setTitle("Ricerca Clever");

		_dialogButton = (Button) rootView.findViewById(R.id.dialog_hint_dialogButtonOK);
		// Watch for button clicks.
		_dialogButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.
                //((DialogFragment)getActivity()).getDialog().show();;
            }
        });
		
		
		_hint_listView = (ListView) rootView.findViewById(R.id.dialog_hint_lv_items);

		hint_itemArrayList = new ArrayList<ItemHintListFragment>();
		hint_itemArrayList.add(new ItemHintListFragment(1, true, "acqua fontacazzi", DateTime.now(), "conad","2.50€",""));
		hint_itemArrayList.add(new ItemHintListFragment(2, true, "acqua cazziMiaa", DateTime.now(), "despa","2.49€",""));
		hint_itemArrayList.add(new ItemHintListFragment(3, true, "acqua cazzuIuIu", DateTime.now(), "CazzoCoop","2.48€",""));
		hint_itemAdapter = new ItemHintAdapterListFragment( getActivity().getApplicationContext(), R.layout.fragment_list_dialog_hint_item, hint_itemArrayList);
		_hint_listView.setAdapter(hint_itemAdapter);
	
		
		
		return rootView;
	}
}
