package telran.net.games.entities;
import java.time.LocalDate;

import org.json.JSONObject;

import jakarta.persistence.*;
@Entity
@Table(name = "gamer")
public class Gamer {
	private static final String USERNAME_FIELD = "username";
	private static final String  BIRTHDATE_FIELD = "birthdate";
	@Id
	private String username;
	private LocalDate birthdate;
	public Gamer(String username, LocalDate birthdate) {
		this.username = username;
		this.birthdate = birthdate;
	}
	
	public Gamer(JSONObject jsonObject) {
		this.username = jsonObject.getString(USERNAME_FIELD);
		this.birthdate = LocalDate.parse(jsonObject.getString(BIRTHDATE_FIELD));
		
	}
	
	@Override
	public String toString() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(USERNAME_FIELD, username);
		jsonObject.put(BIRTHDATE_FIELD, birthdate);
		return jsonObject.toString();
	}

	public Gamer() {
	}
	
	public String getUsername() {
		return username;
	}
	
	public LocalDate getBirthdate() {
		return birthdate;
	}
	
	
	
	
}
