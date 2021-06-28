package it.polito.tdp.imdb.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.tdp.imdb.model.Actor;
import it.polito.tdp.imdb.model.Adiacenza;
import it.polito.tdp.imdb.model.Director;
import it.polito.tdp.imdb.model.Movie;

public class ImdbDAO {
	
	public List<Actor> listAllActors(){
		String sql = "SELECT * FROM actors";
		List<Actor> result = new ArrayList<Actor>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Actor actor = new Actor(res.getInt("id"), res.getString("first_name"), res.getString("last_name"),
						res.getString("gender"));
				
				result.add(actor);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Movie> listAllMovies(){
		String sql = "SELECT * FROM movies";
		List<Movie> result = new ArrayList<Movie>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Movie movie = new Movie(res.getInt("id"), res.getString("name"), 
						res.getInt("year"), res.getDouble("rank"));
				
				result.add(movie);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public Map<Integer, Director> getDirectorsByYear(int year){
		String sql = "SELECT distinct d.id, d.first_name, d.last_name "
				+ "FROM movies m, movies_directors md, directors d "
				+ "WHERE m.id = md.movie_id AND m.year = ? "
				+ "AND d.id = md.director_id";
		Map<Integer, Director> result = new HashMap<Integer, Director>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, year);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Director director = new Director(res.getInt("d.id"), res.getString("d.first_name"), res.getString("d.last_name"));
				
				result.put(director.getId(), director);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Adiacenza> getEdges(int year, Map<Integer, Director> idMap){
		String sql = "SELECT md1.director_id as d1, md2.director_id as d2, count(distinct r1.actor_id) as peso "
				+ "FROM movies_directors md1, roles r1, movies_directors md2, roles r2, movies m1, movies m2 "
				+ "WHERE md1.movie_id = r1.movie_id AND md2.movie_id=r2.movie_id "
				+ "AND m1.id=md1.movie_id AND m2.id=md2.movie_id "
				+ "AND m1.year=? AND m2.year=m1.year "
				+ "AND md1.director_id<md2.director_id AND r1.actor_id=r2.actor_id "
				+ "GROUP BY md1.director_id, md2.director_id";
		List<Adiacenza> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, year);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				if(idMap.containsKey(res.getInt("d1")) && idMap.containsKey(res.getInt("d2"))) {
					Director d1 = idMap.get(res.getInt("d1"));
					Director d2 = idMap.get(res.getInt("d2"));
					result.add(new Adiacenza(d1, d2, res.getInt("peso")));
				}
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	
	
	
	
	
	
}
