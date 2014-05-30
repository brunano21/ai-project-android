package it.polito.ai.project.model;

import org.joda.time.DateTime;

public class Valutazione {

	int idInserzione;
	String categoria;
	String sottocategoria;
	float prezzo;
	DateTime dataInizio;
	DateTime dataFine;
	String descrizione;
	byte[] foto;
	
	public int getIdInserzione() {
		return idInserzione;
	}
	public void setIdInserzione(int idInserzione) {
		this.idInserzione = idInserzione;
	}
	public String getCategoria() {
		return categoria;
	}
	public Valutazione(int idInserzione, String categoria,
			String sottocategoria, float prezzo, DateTime dataInizio,
			DateTime dataFine, String descrizione, byte[] foto) {
		super();
		this.idInserzione = idInserzione;
		this.categoria = categoria;
		this.sottocategoria = sottocategoria;
		this.prezzo = prezzo;
		this.dataInizio = dataInizio;
		this.dataFine = dataFine;
		this.descrizione = descrizione;
		this.foto = foto;
	}
	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}
	public String getSottocategoria() {
		return sottocategoria;
	}
	public void setSottocategoria(String sottocategoria) {
		this.sottocategoria = sottocategoria;
	}
	public float getPrezzo() {
		return prezzo;
	}
	public void setPrezzo(float prezzo) {
		this.prezzo = prezzo;
	}
	public DateTime getDataInizio() {
		return dataInizio;
	}
	public void setDataInizio(DateTime dataInizio) {
		this.dataInizio = dataInizio;
	}
	public DateTime getDataFine() {
		return dataFine;
	}
	public void setDataFine(DateTime dataFine) {
		this.dataFine = dataFine;
	}
	public String getDescrizione() {
		return descrizione;
	}
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	public byte[] getFoto() {
		return foto;
	}
	public void setFoto(byte[] foto) {
		this.foto = foto;
	}
	
}
