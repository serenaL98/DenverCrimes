package it.polito.tdp.crimes.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	
	private EventsDao dao;
	private List<Event> eventi;
	private List<Event> reati;
	private Map<String, Event> idEvent;
	
	//grafo semplice pesato non orientato: VERTICI tipi di reato; ARCHI quartieri distinti
	private SimpleWeightedGraph<Event, DefaultWeightedEdge> grafo;
	
	public Model() {
		
		this.dao = new EventsDao();
		this.eventi = dao.listAllEvents();
		this.idEvent = new HashMap<>();
		
	}
	
	public List<String> tutteCategorie() {
		return dao.getTutteCategorie();
	}
	/*
	public List<LocalDateTime> tuttiMesi() {
		
		List<LocalDateTime> mesi = new LinkedList<>();
		List<LocalDateTime> m = new LinkedList<>();
		
		for(Event e: eventi) {
			if(!mesi.contains(e.getReported_date())) {
				mesi.add(e.getReported_date());
			}
		}
		
		for(LocalDateTime l : mesi) {
			if(!m.contains(l.getMonth())) {
				m.add(l);
			}
		}
		return m;
	}
	*/
	
	public List<String> mesi(){
		
		List<String> m = new LinkedList<>();
		
		for(int i=1; i<13; i++) {
			if(i<10) {
				m.add("0"+i);
			}else
				m.add(""+i);
		}
		
		return m;
	}
	
	public void creaGrafo(String categoria, int mese) {
		
		//riempio la mappa degli eventi
		for(Event e: eventi) {
			idEvent.put(e.getOffense_category_id(), e);
		}
		
		//inizializzo il grafo
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		//prendo la lista dei reati data quella categoria in quel mese
		this.reati = dao.reatiCategoriaMese(categoria, mese);
		
		//prendo i collegamenti: archi
		List<Collegamento> archi = dao.getCollegamenti(categoria, mese);
		
		//aggiungo i vertici
		for(Event r: reati) {
			grafo.addVertex(r);
		}
		
		for(Collegamento c: archi) {
			//se il grafo contiene entrambi i vertici
			if(grafo.containsVertex(idEvent.get(c.getE1())) && grafo.containsVertex(idEvent.get(c.getE2())) ) {
				//aggiungo il peso
				DefaultWeightedEdge e = this.grafo.getEdge(idEvent.get(c.getE1()), idEvent.get(c.getE2()));
				
				grafo.addEdge(idEvent.get(c.getE1()), idEvent.get(c.getE2()), e);
			}
		}
	}
	
}
