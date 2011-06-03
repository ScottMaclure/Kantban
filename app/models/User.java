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
	
	public void setPassword(String password) {
		this.password = hashPassword(password);
	}
	
    public static User connect(String email, String password) {
        return find("byEmailAddressAndPassword", email, hashPassword(password)).first();
    }

    public String getGravatarId() {
    	return DigestUtils.md5Hex(StringUtils.trim(emailAddress));
    }

    @Override
    public String toString() {
        return name != null ? name : "" + " <" + emailAddress + ">";
    }
    
    private static String hashPassword(String password) {
    	return DigestUtils.md5Hex(PASSWORD_SALT + password);
    }
    
}
