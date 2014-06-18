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
	String nome_todolist;
	
	public InserzioneInScadenza(int idInserzione, float prezzo,	DateTime dataFine, 
			String descrizione, String foto, 
			String supermercato, String supermercato_indirizzo,
			String nome_todolist) {
		super();
		this.idInserzione = idInserzione;
		this.prezzo = prezzo;
		this.dataFine = dataFine;
		this.descrizione = descrizione;
		this.foto = foto;
		this.supermercato = supermercato;
		this.supermercato_indirizzo = supermercato_indirizzo;
		this.nome_todolist = nome_todolist;
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
	public String getNome_todolist() {
		return nome_todolist;
	}
	public void setNome_todolist(String nome_todolist) {
		this.nome_todolist = nome_todolist;
	}


	@Override
	public int describeContents() {
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
		dest.writeString(nome_todolist);
	}

	public static final Parcelable.Creator<InserzioneInScadenza> CREATOR = new Creator<InserzioneInScadenza>() {  
		public InserzioneInScadenza createFromParcel(Parcel source) {  
			InserzioneInScadenza val = new InserzioneInScadenza();  
			val.idInserzione = source.readInt(); 
			val.prezzo = source.readFloat();
			val.dataFine = (DateTime) source.readSerializable();
			val.foto = source.readString();
			val.supermercato = source.readString();
			val.supermercato_indirizzo = source.readString();
			val.nome_todolist = source.readString();
			return val;  
		}

		@Override
		public InserzioneInScadenza[] newArray(int size) {
			return null;
		}
	};
}
