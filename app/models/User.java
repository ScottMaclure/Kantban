package models;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import play.db.jpa.Model;

@Entity
public class User extends Model {
	
	// FIXME Make this configurable
	static final String PASSWORD_SALT = "Kantban is cool!";
	
	@Column(nullable = false, name = "email_address")
	public String emailAddress;
	@Column(nullable = false)
	public String password;
	@Column(nullable = false)
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
	
	public void setPassword(@Nonnull String password) {
		this.password = hashPassword(password);
	}
	
    public static User connect(@Nonnull String email, @Nonnull String password) {
        return find("byEmailAddressAndPassword", email, hashPassword(password)).first();
    }

    @Override
    public String toString() {
        return name != null ? name : "" + " <" + emailAddress + ">";
    }
    
    private static String hashPassword(@Nonnull String password) {
    	return DigestUtils.md5Hex(PASSWORD_SALT + password);
    }
    
}
