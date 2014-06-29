package it.polito.ai.project.fragment;

import it.polito.ai.project.R;
import it.polito.ai.project.main.ItemHintListFragment;
import it.polito.ai.project.main.MainActivity;
import it.polito.ai.project.main.MyHttpClient;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;

import android.app.Dialog;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class DialogHintSingolo extends Dialog  {

	private Button _dialogButton;
	private ItemHintListFragment item;
	private ImageView foto;
	private TextView descrizione;
	private TextView data_fine;
	private TextView prezzo;
	private TextView supermercato;

	public mySingoloOnClickListener mySingoloListener;


	public DialogHintSingolo(Context context, mySingoloOnClickListener mySingoloListener, ItemHintListFragment item) {
		super(context);
		this.mySingoloListener = mySingoloListener;
		this.item = item;
	}

	// This is my interface //
	public interface mySingoloOnClickListener {
		void onButtonClick(ItemHintListFragment item);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.fragment_list_dialog_hint_single);
		setTitle("Dettagli");

			_dialogButton = (Button) findViewById(R.id.dialog_singolo_dialogButtonOK);
			data_fine = (TextView) findViewById(R.id.list_dialog_singolo_tv_data_fine);
			descrizione = (TextView) findViewById(R.id.list_dialog_singolo_tv_descrizione);
			prezzo = (TextView) findViewById(R.id.list_dialog_singolo_tv_prezzo);
			supermercato = (TextView) findViewById(R.id.list_dialog_singolo_tv_supermercato);
			foto = (ImageView) findViewById(R.id.list_dialog_singolo_iv_foto);
			
			data_fine.setText( DateTimeFormat.forPattern("dd/MM/yyyy").print( item.getData_fine()) );
			descrizione.setText(item.getDescrizione() );
			prezzo.setText(item.getPrezzo() + " €");
			supermercato.setText(item.getSupermercato() );

			if(!"".equals(item.getFoto())) {
				byte[] decodedString = Base64.decode(item.getFoto(), Base64.DEFAULT);
				Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length); 
				foto.setImageBitmap(decodedByte);
			}
			
			_dialogButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dismiss();
				}
			});

			}
		}

