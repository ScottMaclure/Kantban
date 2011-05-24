package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class User extends Model {
	
	public String emailAddress;
	public String name;
	
	public boolean active;

	public User(String emailAddress, String name) {
		this.emailAddress = emailAddress;
		this.name = name;
		this.active = true;
	}
}
