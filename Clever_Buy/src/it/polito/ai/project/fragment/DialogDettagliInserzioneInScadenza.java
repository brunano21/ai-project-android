package it.polito.ai.project.fragment;

import it.polito.ai.project.R;
import it.polito.ai.project.main.MyHttpClient;
import it.polito.ai.project.model.InserzioneInScadenza;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;




import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class DialogDettagliInserzioneInScadenza extends DialogFragment {

	public interface MyDialogInterface extends Serializable{
		public void onDialogPositiveClick(DialogDettagliInserzioneInScadenza dialog);
		public void onDialogNegativeClick(DialogDettagliInserzioneInScadenza dialog);
	}

	private InserzioneInScadenza ins;
	private int posizione;
	private int idTodoListSelezionata;
	
	public InserzioneInScadenza getInserzione() {
		return this.ins;
	}

	public int getPosizione() {
		return this.posizione;
	}

	public int getIdTodoListSelezionata() {
		return this.idTodoListSelezionata;
	}
	
	private MyDialogInterface callbackListener;
	private HashMap<String, Integer> todoListHashMap;
	private ArrayAdapter<String> spinnerArrayAdapter;
	
	private TextView descrizione;
	private TextView prezzo; 
	private TextView dataFine;
	private TextView supermercato_nome;
	private TextView supermercato_indirizzo;
	private Spinner spinner_todolist;
	
	
	public static DialogDettagliInserzioneInScadenza getInstance(MyDialogInterface dialogInterface) {
		DialogDettagliInserzioneInScadenza fragmentDialog = new DialogDettagliInserzioneInScadenza();
		Bundle args = new Bundle();
		args.putSerializable("dialogInterface", dialogInterface);
		fragmentDialog.setArguments(args);
		return fragmentDialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view =  inflater.inflate(R.layout.dialog_inserzione_in_scadenza, null);
		callbackListener = (MyDialogInterface) getArguments().getSerializable("dialogInterface");

		builder.setView(view)
		.setTitle("Aggiungere alla tua todolist?")
		.setPositiveButton("Aggiungi", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				idTodoListSelezionata = todoListHashMap.get(spinner_todolist.getSelectedItem().toString()).intValue();
				callbackListener.onDialogPositiveClick(DialogDettagliInserzioneInScadenza.this);
			}
		})
		.setNegativeButton("Cancella", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				//callbackListener.onDialogNegativeClick(DialogDettagliInserzioneInScadenza.this);
			}
		});

		Bundle mArgs = getArguments();
		this.ins = mArgs.getParcelable("inserzioneInScadenza");
		this.posizione = mArgs.getInt("posizione");

		ImageView foto = (ImageView) view.findViewById(R.id.dialog_inserzione_in_scadenza_iv_foto);
		byte[] decodedString = Base64.decode(ins.getFoto(), Base64.DEFAULT);
		Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length); 
		foto.setImageBitmap(decodedByte);
		
		descrizione = (TextView) view.findViewById(R.id.dialog_inserzione_in_scadenza_tv_descrizione);
		descrizione.setText(ins.getDescrizione());

		prezzo = (TextView) view.findViewById(R.id.dialog_inserzione_in_scadenza_tv_prezzo);
		prezzo.setText(String.valueOf(ins.getPrezzo()) + " €");

		dataFine = (TextView) view.findViewById(R.id.dialog_inserzione_in_scadenza_tv_data_fine);
		dataFine.setText((ins.getDataFine() == DateTime.now()) ?  "Oggi" : "Domani");

		supermercato_nome = (TextView) view.findViewById(R.id.dialog_inserzione_in_scadenza_tv_supermercato);
		supermercato_nome.setText(ins.getSupermercato());

		supermercato_indirizzo = (TextView) view.findViewById(R.id.dialog_inserzione_in_scadenza_tv_supermercato_indirizzo);
		supermercato_indirizzo.setText(ins.getSupermercato_indirizzo());

		spinner_todolist = (Spinner) view.findViewById(R.id.dialog_inserzione_in_scadenza_spin_todolist);

		MyHttpClient.get("/inscadenza/getTodoLists", null, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONArray response) {
				todoListHashMap = new HashMap<String, Integer>();
				ArrayList<String> todoListArrayList = new ArrayList<String>();
				
				for(int i = 0; i < response.length(); i++) {
					try {
						todoListHashMap.put(response.getJSONObject(i).getString("nomeLista"), Integer.valueOf(response.getJSONObject(i).getString("idLista")));
						todoListArrayList.add(response.getJSONObject(i).getString("nomeLista"));
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
				}
				ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, todoListArrayList);
				spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinner_todolist.setAdapter(spinnerArrayAdapter);
			}

			@Override
			public void onFailure(Throwable error, String content) {
				Log.v("ERROR" , "onFailure error : " + error.toString() + "content : " + content);
			}

		});


		return builder.create();
	}
}
