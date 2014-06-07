package it.polito.ai.project.model;

import org.joda.time.DateTime;

import android.os.Parcel;
import android.os.Parcelable;

public class Valutazione implements Parcelable{

	int idInserzione;
	String categoria;
	String sottocategoria;
	float prezzo;
	DateTime dataInizio;
	DateTime dataFine;
	String descrizione;
	String foto;
	String codiceBarre;
	String supermercato;
	String supermercato_indirizzo;

	public Valutazione(int idInserzione, String categoria,
			String sottocategoria, float prezzo, DateTime dataInizio,
			DateTime dataFine, String descrizione, String foto, String codiceBarre, String supermercato, String supermercato_indirizzo) {
		super();
		this.idInserzione = idInserzione;
		this.categoria = categoria;
		this.sottocategoria = sottocategoria;
		this.prezzo = prezzo;
		this.dataInizio = dataInizio;
		this.dataFine = dataFine;
		this.descrizione = descrizione;
		this.foto = foto;
		this.codiceBarre = codiceBarre;
		this.supermercato = supermercato;
		this.supermercato_indirizzo = supermercato_indirizzo;
	}
	public Valutazione() {
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
	public String getCodiceBarre() {
		return codiceBarre;
	}
	public void setCodiceBarre(String codiceBarre) {
		this.codiceBarre = codiceBarre;
	}
	public String getSupermercato() {
		return supermercato;
	}
	public void setSupermercato(String supermercato) {
		this.supermercato = supermercato;
	}
	public String getSupermercato_indirizzo() {
		return supermercato_indirizzo;
	}
	public void setSupermercato_indirizzo(String supermercato_indirizzo) {
		this.supermercato_indirizzo = supermercato_indirizzo;
	}



	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(idInserzione);
		dest.writeString(categoria);
		dest.writeString(sottocategoria);
		dest.writeFloat(prezzo);
		dest.writeSerializable(dataInizio);
		dest.writeSerializable(dataFine);
		dest.writeString(foto);
		dest.writeString(codiceBarre);
		dest.writeString(supermercato);
		dest.writeString(supermercato_indirizzo);

	}

	public static final Parcelable.Creator<Valutazione> CREATOR = new Creator<Valutazione>() {  
		public Valutazione createFromParcel(Parcel source) {  
			Valutazione val = new Valutazione();  
			val.idInserzione = source.readInt(); 
			val.categoria = source.readString();
			val.sottocategoria = source.readString();
			val.prezzo = source.readFloat();
			val.dataInizio = (DateTime) source.readSerializable();
			val.dataFine = (DateTime) source.readSerializable();
			val.foto = source.readString();
			val.codiceBarre = source.readString();
			val.supermercato = source.readString();
			val.supermercato_indirizzo = source.readString();
			return val;  
		}

		@Override
		public Valutazione[] newArray(int size) {
			return null;
		}
	};
}
