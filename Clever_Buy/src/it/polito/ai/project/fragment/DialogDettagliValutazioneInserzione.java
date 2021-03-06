package it.polito.ai.project.fragment;

import it.polito.ai.project.R;
import it.polito.ai.project.model.InserzioneDaValutare;

import java.io.Serializable;

import org.joda.time.format.DateTimeFormat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DialogDettagliValutazioneInserzione extends DialogFragment {

	public interface MyDialogInterface extends Serializable{
		public void onDialogPositiveClick(DialogDettagliValutazioneInserzione dialog);
		public void onDialogNegativeClick(DialogDettagliValutazioneInserzione dialog);
	}

	private InserzioneDaValutare val;
	private int posizione;
	
	public InserzioneDaValutare getValutazione() {
		return this.val;
	}

	public int getPosizione() {
		return this.posizione;
	}
	
	private MyDialogInterface callbackListener;

	public static DialogDettagliValutazioneInserzione getInstance(MyDialogInterface dialogInterface) {

		DialogDettagliValutazioneInserzione fragmentDialog = new DialogDettagliValutazioneInserzione();
	    Bundle args = new Bundle();
	    args.putSerializable("dialogInterface", dialogInterface);
	    fragmentDialog.setArguments(args);
	    
	    return fragmentDialog;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view =  inflater.inflate(R.layout.dialog_inserzione_dettagli, null);
		callbackListener = (MyDialogInterface) getArguments().getSerializable("dialogInterface");

		builder.setView(view)
			.setPositiveButton("+1", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					callbackListener.onDialogPositiveClick(DialogDettagliValutazioneInserzione.this);
				}
			})
			.setNegativeButton("-1", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					callbackListener.onDialogNegativeClick(DialogDettagliValutazioneInserzione.this);
				}
			});
			
		Bundle mArgs = getArguments();
		this.val = mArgs.getParcelable("valutazione");
		this.posizione = mArgs.getInt("posizione");
		
		TextView descrizione =  (TextView) view.findViewById(R.id.vd_tv_descrizione);
		descrizione.setText(val.getDescrizione());

		TextView barcode =  (TextView) view.findViewById(R.id.vd_tv_barcode);
		barcode.setText(val.getCodiceBarre());

		TextView categoria =  (TextView) view.findViewById(R.id.vd_tv_categoria);
		categoria.setText(val.getCategoria());

		TextView sottocategoria =  (TextView) view.findViewById(R.id.vd_tv_sottocategoria);
		sottocategoria.setText(val.getSottocategoria());

		TextView prezzo =  (TextView) view.findViewById(R.id.vd_tv_prezzo);
		prezzo.setText(String.valueOf(val.getPrezzo()) + "�");

		TextView dataInizio =  (TextView) view.findViewById(R.id.vd_tv_data_inizio);
		dataInizio.setText(DateTimeFormat.forPattern("dd/MM/yyyy").print(val.getDataInizio()));

		TextView dataFine =  (TextView) view.findViewById(R.id.vd_tv_data_fine);
		dataFine.setText(DateTimeFormat.forPattern("dd/MM/yyyy").print(val.getDataFine()));

		TextView supermercato_nome =  (TextView) view.findViewById(R.id.vd_tv_supermercato_nome);
		supermercato_nome.setText(val.getSupermercato());

		TextView supermercato_indirizzo =  (TextView) view.findViewById(R.id.vd_tv_supermercato_indirizzo);
		supermercato_indirizzo.setText(val.getSupermercato_indirizzo());

		ImageView foto = (ImageView) view.findViewById(R.id.vd_iv_foto);
		byte[] decodedString = Base64.decode(val.getFoto(), Base64.DEFAULT);
		Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length); 
		foto.setImageBitmap(decodedByte);
		
		return builder.create();
	}

}
