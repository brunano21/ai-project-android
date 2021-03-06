package it.polito.ai.project.model;

public class Supermercato {
	private int Id;
	private String Nome;
	private String Indirizzo;
	private Double Distanza;
	
	public Supermercato(int id, String nome, String indirizzo, Double distanza) {
		this.Id = id;
		this.Nome = nome;
		this.Indirizzo = indirizzo;
		this.Distanza = distanza;
	}
		
	
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public String getNome() {
		return Nome;
	}
	public void setNome(String nome) {
		Nome = nome;
	}
	public String getIndirizzo() {
		return Indirizzo;
	}
	public void setIndirizzo(String indirizzo) {
		Indirizzo = indirizzo;
	}
	public Double getDistanza() {
		return Distanza;
	}
	public void setDistanza(Double distanza) {
		Distanza = distanza;
	}
}
