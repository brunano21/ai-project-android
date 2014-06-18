package it.polito.ai.project.model;

import org.joda.time.DateTime;

public class MiaInserzione {
	
	private int idInserzione;
	private String categoria;
	private String sottocategoria;
	private float prezzo;
	private DateTime dataInizio;
	private DateTime dataFine;
	private String descrizione;
	private String foto;
	private String valutazioniPositive;
	private String valutazioniNegative;

	public MiaInserzione(int idInserzione, String categoria, String sottocategoria, float prezzo, 
			DateTime dataInizio, DateTime dataFine, String descrizione, 
			String foto, String valutazioniPositive, String valutazioniNegative) {
		super();
		this.idInserzione = idInserzione;
		this.categoria = categoria;
		this.sottocategoria = sottocategoria;
		this.prezzo = prezzo;
		this.dataInizio = dataInizio;
		this.dataFine = dataFine;
		this.descrizione = descrizione;
		this.foto = foto;
		this.valutazioniPositive = valutazioniPositive;
		this.valutazioniNegative = valutazioniNegative;
		
	}

	public int getIdInserzione() {
		return idInserzione;
	}

	public void setIdInserzione(int idInserzione) {
		this.idInserzione = idInserzione;
	}

	public String getCategoria() {
		return categoria;
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

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public String getValutazioniPositive() {
		return valutazioniPositive;
	}

	public void setValutazioniPositive(String valutazioniPositive) {
		this.valutazioniPositive = valutazioniPositive;
	}

	public String getValutazioniNegative() {
		return valutazioniNegative;
	}

	public void setValutazioniNegative(String valutazioniNegative) {
		this.valutazioniNegative = valutazioniNegative;
	}
}
