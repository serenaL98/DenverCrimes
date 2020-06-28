package it.polito.tdp.crimes.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import it.polito.tdp.crimes.model.Collegamento;
import it.polito.tdp.crimes.model.Event;
import it.polito.tdp.crimes.model.Reato;


public class EventsDao {
	
	public List<Event> listAllEvents(){
		String sql = "SELECT * FROM events" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<Event> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					list.add(new Event(res.getLong("incident_id"),
							res.getInt("offense_code"),
							res.getInt("offense_code_extension"), 
							res.getString("offense_type_id"), 
							res.getString("offense_category_id"),
							res.getTimestamp("reported_date").toLocalDateTime(),
							res.getString("incident_address"),
							res.getDouble("geo_lon"),
							res.getDouble("geo_lat"),
							res.getInt("district_id"),
							res.getInt("precinct_id"), 
							res.getString("neighborhood_id"),
							res.getInt("is_crime"),
							res.getInt("is_traffic")));
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}

	public List<String> getTutteCategorie() {
		
		String sql = "SELECT distinct offense_category_id AS oci" + 
				" FROM EVENTS" + 
				" ORDER BY offense_category_id asc";
		
		List<String> eventi = new LinkedList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			
			while(res.next()) {
				eventi.add(res.getString("oci"));
			}
			
			conn.close();
			
			return eventi;
			
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	/*
	public List<Reato> reatiCategoriaMese(String categoria, int mese){
		
		String sql = "SELECT e.offense_type_id AS ot, e.offense_category_id AS oc, e.reported_date AS d, e.neighborhood_id AS n" + 
				" FROM EVENTS e" + 
				" WHERE OFFENsE_category_id= 'aggravated-assault' AND month(e.reported_date) = ? ";
		List<Reato> reati = new LinkedList<>();
		
		//leggo da input la categoria e il mese
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			st.setString(1, categoria);
			st.setInt(2, mese);
			
			while(res.next()) {
				Reato rtemp = new Reato(res.getString("ot"), res.getString("oc"), res.getDate("d"), res.getString("n"));
				
				reati.add(rtemp);
			}
			
			conn.close();
			
			return reati;
			
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	*/
	
	public List<Event> reatiCategoriaMese(String cat, int m){
		
		String sql = "SELECT e.offense_type_id AS ot, e.offense_category_id AS oc, e.reported_date AS d, e.neighborhood_id AS n" + 
				" FROM EVENTS e" + 
				" WHERE OFFENsE_category_id= ? AND month(e.reported_date) = ? ";
		List<Event> reati = new LinkedList<>();
		
		//leggo da input la categoria e il mese
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			st.setString(1, cat);
			st.setInt(2, m);
			
			while(res.next()) {
				Event etemp = new Event();
				etemp.setOffense_category_id(res.getString("oc"));
				etemp.setOffense_type_id(res.getString("ot"));
				etemp.setNeighborhood_id(res.getString("n"));
				
				reati.add(etemp);
			}
			
			conn.close();
			
			return reati;
			
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		return null;	
	}
	
	public List<Collegamento> getCollegamenti(String cat, int m){
		String sql = "select e1.offense_type_id as v1, e2.offense_type_id as v2, COUNT(DISTINCT(e1.neighborhood_id)) as peso " + 
				"from events e1, events e2 " + 
				"where e1.offense_category_id = ? " + 
				"	and e2.offense_category_id = ? " + 
				"	and Month(e1.reported_date) = ? " + 
				"	and Month(e2.reported_date) = ? " + 
				"	and e1.offense_type_id != e2.offense_type_id " + 
				"	and e1.neighborhood_id = e2.neighborhood_id " + 
				"group by e1.offense_type_id, e2.offense_type_id";
		List<Collegamento> lista = new LinkedList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, cat);
			st.setString(2, cat);
			st.setInt(3, m);
			st.setInt(4, m);
			ResultSet res = st.executeQuery();
			
			while(res.next()) {
				Collegamento ctemp = new Collegamento(res.getString("v1"), res.getString("v2"), res.getInt("peso"));
				
				lista.add(ctemp);
			}
			
			conn.close();
			
			return lista;
			
			
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
