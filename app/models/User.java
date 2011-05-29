package models;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;

import play.Logger;
import play.db.jpa.Model;
import utils.PasswordUtil;
import exceptions.ServerException;

@Entity
public class User extends Model {
	
	// FIXME Make this configurable
	static final String PASSWORD_SALT = "Kantban is cool!";
	
	@Column(nullable=false)
	public String emailAddress, password;
	public String name;
	
	public boolean active;

	public User(@Nonnull String emailAddress, @Nonnull String password) {
		this(emailAddress, password, null);
	}
	
	public User(@Nonnull String emailAddress, @Nonnull String password, String name) {
		this.emailAddress = emailAddress;
		this.setPassword(password);
		this.name = name;
		this.active = true;
	}
	
	public void setPassword(String password) {
		this.password = hashPassword(password);
	}
	
    public static User connect(String email, String password) {
        return find("byEmailAddressAndPassword", email, hashPassword(password)).first();
    }

    @Override
    public String toString() {
        return name != null ? name : "" + " <" + emailAddress + ">";
    }
    
    private static String hashPassword(String password) {
    	try {
    		password = PasswordUtil.hashPassword(password, PASSWORD_SALT);
    	}
    	catch (ServerException e) {
    		// This really should never happen
    		Logger.error("Something is seriously odd", e);
    		throw new RuntimeException("Caught ServerException that should not happen", e);
    	}
    	return password;
    }
}
