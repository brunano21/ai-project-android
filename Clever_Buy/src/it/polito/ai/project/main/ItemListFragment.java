package it.polito.ai.project.main;

import org.apache.http.Header;
import org.json.JSONArray;

import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ItemListFragment {



	private int id_lista_desideri;
	private int id_elemento;
	private String descrizione;
	private String quantita;
	private boolean acquistato;

	private ItemHintListFragment inserzione;

	// l'attributo seguente è true solo se inserzione è != 0
	private boolean hint_is_present;

	public ItemListFragment(int id_lista_desideri, int id_elemento, String descrizione,
			String quantita, boolean acquistato, ItemHintListFragment inserzione) {
		super();
		this.id_lista_desideri = id_lista_desideri;
		this.id_elemento = id_elemento;
		this.descrizione = descrizione;
		this.quantita = quantita;
		this.acquistato = acquistato;

		this.setInserzione(inserzione);
	}



	public int getId_lista_desideri() {
		return id_lista_desideri;
	}



	public void setId_lista_desideri(int id_lista_desideri) {
		this.id_lista_desideri = id_lista_desideri;
	}



	public int getId_elemento() {
		return id_elemento;
	}

	public void setId_elemento(int id_elemento) {
		this.id_elemento = id_elemento;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getQuantita() {
		return quantita;
	}

	public void setQuantita(String quantita) {
		this.quantita = quantita;
	}

	public boolean isAcquistato() {
		return acquistato;
	}

	public void setAcquistato(boolean acquistato) {
		this.acquistato = acquistato;
	}

	public ItemHintListFragment getInserzione() {
		return inserzione;
	}

	public void setInserzione(ItemHintListFragment inserzione) {
		this.inserzione = inserzione;
		if(inserzione!=null){
			this.descrizione = inserzione.getDescrizione();

		}
		if(inserzione==null)
			this.hint_is_present=false;
		else
			this.hint_is_present=true;
	}



	public void setInserzione(ItemHintListFragment inserzione, boolean sendToServer) {
		setInserzione(inserzione);
		if(sendToServer)
		{
			sendToServer();
		}
	}

	public void sendToServer()
	{
		sendModificaDescrizione();

		sendModificaId_inserzione();

	}

	public void sendModificaDescrizione()
	{
		RequestParams param = new RequestParams();
		param.put("cmd","modificaDescrizioneElemento");
		param.put("id_lista_desideri",Integer.toString(this.id_lista_desideri));
		param.put("id_elemento",Integer.toString(this.id_elemento));
		param.put("descrizione",this.inserzione.getDescrizione() );
		MyHttpClient.post("/todolist", param, new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				Log.v("ERROR" , "onFailure error : " + throwable.getMessage() + " \n content : " + responseString);
			}
		});
	}

	public void sendModificaId_inserzione()
	{

		RequestParams param = new RequestParams();
		param.put("cmd","modificaDescrizioneElemento");
		param.put("id_lista_desideri",Integer.toString(this.id_lista_desideri));
		param.put("id_elemento",Integer.toString(this.id_elemento));
		param.put("id_inserzione",Integer.toString(this.inserzione.getItem_id()));

		MyHttpClient.post("/todolist", param, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				Log.v("ERROR" , "onFailure error : " + throwable.getMessage() + " \n content : " + responseString);
			}
		});
	}
	public boolean isHint_is_present() {
		return hint_is_present;
	}

	public void setHint_is_present(boolean hint_is_present) {
		this.hint_is_present = hint_is_present;
	}




}
