package it.polito.tdp.imdb.model;

public class DirectorWithWeight {
	
	private Director d;
	private int peso;
	
	public DirectorWithWeight(Director d, int peso) {
		super();
		this.d = d;
		this.peso = peso;
	}

	public Director getD() {
		return d;
	}

	public void setD(Director d) {
		this.d = d;
	}

	public int getPeso() {
		return peso;
	}

	public void setPeso(int peso) {
		this.peso = peso;
	}
	
	

}
