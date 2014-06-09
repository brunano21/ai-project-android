package it.polito.ai.project.model;

import org.joda.time.DateTime;

import android.os.Parcel;
import android.os.Parcelable;

public class InserzioneInScadenza implements Parcelable {
	int idInserzione;
	float prezzo;
	DateTime dataFine;
	String descrizione;
	String foto; 	// codificata in base64
	String supermercato;
	String supermercato_indirizzo;

	public InserzioneInScadenza(int idInserzione, float prezzo,	DateTime dataFine, 
			String descrizione, String foto, 
			String supermercato, String supermercato_indirizzo) {
		super();
		this.idInserzione = idInserzione;
		this.prezzo = prezzo;
		this.dataFine = dataFine;
		this.descrizione = descrizione;
		this.foto = foto;
		this.supermercato = supermercato;
		this.supermercato_indirizzo = supermercato_indirizzo;
	}

	public InserzioneInScadenza() {
	}
	
	public int getIdInserzione() {
		return idInserzione;
	}
	public void setIdInserzione(int idInserzione) {
		this.idInserzione = idInserzione;
	}
	public float getPrezzo() {
		return prezzo;
	}
	public void setPrezzo(float prezzo) {
		this.prezzo = prezzo;
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
		dest.writeFloat(prezzo);
		dest.writeSerializable(dataFine);
		dest.writeString(foto);
		dest.writeString(supermercato);
		dest.writeString(supermercato_indirizzo);
	}

	public static final Parcelable.Creator<InserzioneDaValutare> CREATOR = new Creator<InserzioneDaValutare>() {  
		public InserzioneDaValutare createFromParcel(Parcel source) {  
			InserzioneDaValutare val = new InserzioneDaValutare();  
			val.idInserzione = source.readInt(); 
			val.prezzo = source.readFloat();
			val.dataFine = (DateTime) source.readSerializable();
			val.foto = source.readString();
			val.supermercato = source.readString();
			val.supermercato_indirizzo = source.readString();
			return val;  
		}

		@Override
		public InserzioneDaValutare[] newArray(int size) {
			return null;
		}
	};
}
