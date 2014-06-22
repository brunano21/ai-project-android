package it.polito.ai.project.main;

import org.joda.time.DateTime;

public class ItemHintListFragment {
	private int item_id;
	private boolean selezionato;
	private String descrizione;
	private DateTime data_fine;
	private String supermercato;
	private String prezzo;
	private String foto;
	
	public ItemHintListFragment(int item_id, boolean selezionato,
			String descrizione, DateTime data_fine, String supermercato,
			String prezzo, String foto) {
		super();
		this.item_id = item_id;
		this.selezionato = selezionato;
		this.descrizione = descrizione;
		this.data_fine = data_fine;
		this.supermercato = supermercato;
		this.prezzo = prezzo;
		this.foto = foto;
	}

	public int getItem_id() {
		return item_id;
	}

	public void setItem_id(int item_id) {
		this.item_id = item_id;
	}

	public boolean isSelezionato() {
		return selezionato;
	}

	public void setSelezionato(boolean selezionato) {
		this.selezionato = selezionato;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public DateTime getData_fine() {
		return data_fine;
	}

	public void setData_fine(DateTime data_fine) {
		this.data_fine = data_fine;
	}

	public String getSupermercato() {
		return supermercato;
	}

	public void setSupermercato(String supermercato) {
		this.supermercato = supermercato;
	}

	public String getPrezzo() {
		return prezzo;
	}

	public void setPrezzo(String prezzo) {
		this.prezzo = prezzo;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}
	
	
	
	
	
}
