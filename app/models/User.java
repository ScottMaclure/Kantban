package models;

import javax.persistence.Column;
import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class User extends Model {
	
	@Column(nullable=false)
	public String emailAddress, password;
	public String name;
	
	public boolean active;

	public User(String emailAddress, String password) {
		this(emailAddress, password, null);
	}
	
	public User(String emailAddress, String password, String name) {
		this.emailAddress = emailAddress;
		this.password = password;
		this.name = name;
		this.active = true;
	}
	
    public static User connect(String email, String password) {
        return find("byEmailAddressAndPassword", email, password).first();
    }

    @Override
    public String toString() {
        return name != null ? name : "" + " <" + emailAddress + ">";
    }
}
