package it.polito.tdp.imdb.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.imdb.db.ImdbDAO;

public class Model {
	
	private ImdbDAO dao;
	private Graph<Director, DefaultWeightedEdge> grafo;
	private Map<Integer, Director> idMap;
	private Map<Director, DirectorWithWeight> predecessori;
	private List<Director> affini;
	private int totPeso;
	
	public Model() {
		this.dao = new ImdbDAO();
	}
	
	public void creaGrafo(int year) {
		
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		//vertici
		this.idMap = this.dao.getDirectorsByYear(year);
		Graphs.addAllVertices(grafo, idMap.values());
		
		//archi
		for(Adiacenza a : this.dao.getEdges(year, idMap)) {
			Graphs.addEdgeWithVertices(grafo, a.getD1(), a.getD2(), a.getPeso());
		}
		
	}
	
	public int getNumVertici() {
		return this.grafo.vertexSet().size();
	}
	public int getNumArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public List<Director> getDirectors(){
		List<Director> result = new LinkedList<>(idMap.values());
		Collections.sort(result);
		return result;
	}

	public String getAdiacenti(Director d) {
		String result = "";
		List<Adiacenza> adiacenti = new LinkedList<>();
		for(DefaultWeightedEdge e : this.grafo.edgeSet()) {
			if(grafo.getEdgeSource(e).equals(d)) {
				
				adiacenti.add(new Adiacenza(grafo.getEdgeTarget(e), d, (int)grafo.getEdgeWeight(e)));
				
			}else if(grafo.getEdgeTarget(e).equals(d)) {
				
				adiacenti.add(new Adiacenza(grafo.getEdgeSource(e), d, (int)grafo.getEdgeWeight(e)));
				
			}
		}
		
		Collections.sort(adiacenti);
		for(Adiacenza a : adiacenti) {
			result += a.getD1()+" "+a.getPeso()+"\n";
		}
		
		return result;
		
	}
	
	public String getAffini(Director d, int pesoMax) {
		
		String result = "";
		
		//devo trovare i direttori raggiungibili a partire da d, lo faccio con un metodo
		this.setRaggiungibili(d);
		
		//ora la mappa predecessori contiene tutti quelli raggiungibili da d
		//devo creare un metodo ricorsivo che crei la lista di direttori
		//più lunga possibile, rispettando le condizioni di connettività
		//tra direttori e in modo che il costo non ecceda quello max
		this.affini = new LinkedList<>();
		List<Director> parziale = new LinkedList<>();
		this.totPeso = 0;
		parziale.add(d);
		this.cerca(parziale, pesoMax, 0, d);
		
		if(this.affini.isEmpty()) {
			return "Errore nella ricerca\n";
		}
		
		for(Director dir : this.affini) {
			result += dir+"\n";
		}
		result += "Numero totale di attori condivisi: "+this.totPeso+"\n";
		return result;
		
	}
	
	public void cerca(List<Director> parziale, int pesoMax, int peso, Director primo) {
		//caso terminale
		
		//caso terminale se super il pesoMax
		if(peso>pesoMax) {
			return;
		}
		
		//ogni volta, controllo se la lista parziale è più lunga della migliore attuale
		if(parziale.size()>this.affini.size()) {
			//in tal caso la migliore diventa la parziale ed aggiorno il totPeso
			this.affini = new LinkedList<>(parziale);
			this.totPeso = peso;
		}
		
		//genero sottoproblemi
		for(Director d : this.predecessori.keySet()) {

			//non deve essere il primo, il quale non ha predecessori
			if(!d.equals(primo)) {
				
				
				//posso aggiungerlo solo se il suo predecessore è l'ultimo elemento inserito
				if(parziale.get(parziale.size()-1).equals(this.predecessori.get(d).getD())) {
					
					parziale.add(d);
					this.cerca(parziale, pesoMax, peso+this.predecessori.get(d).getPeso(), primo);
					
					//backtracking
					parziale.remove(d);
				}
			}
			
		}
		
	}
	
	public void setRaggiungibili(Director d) {
		BreadthFirstIterator<Director, DefaultWeightedEdge> bfi = new BreadthFirstIterator<Director, DefaultWeightedEdge>(grafo, d);
		
		this.predecessori = new HashMap<>();
		this.predecessori.put(d, null);
		
		bfi.addTraversalListener(new TraversalListener<Director, DefaultWeightedEdge>() {

			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void edgeTraversed(EdgeTraversalEvent<DefaultWeightedEdge> e) {
				
				//devo memorizzare i vertici raggiungibili con il relativo peso
				DefaultWeightedEdge arco = e.getEdge();
				Director d1 = grafo.getEdgeSource(arco);
				Director d2 = grafo.getEdgeTarget(arco);
				int peso = (int)grafo.getEdgeWeight(arco);
				
				if(predecessori.containsKey(d1) && !predecessori.containsKey(d2)) {
					predecessori.put(d2, new DirectorWithWeight(d1, peso));
				} else{ if(predecessori.containsKey(d2) && !predecessori.containsKey(d1)) {
					predecessori.put(d1, new DirectorWithWeight(d2, peso));
				}}
			}

			@Override
			public void vertexTraversed(VertexTraversalEvent<Director> e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void vertexFinished(VertexTraversalEvent<Director> e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		while(bfi.hasNext())
			bfi.next();
	}
}
