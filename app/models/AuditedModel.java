package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import play.db.jpa.Model;

@MappedSuperclass
public abstract class AuditedModel extends Model {

	@Column(name = "created_on", nullable = false)
	public Date createdOn;

	@JoinColumn(name = "created_user")
	@ManyToOne(optional = false)
	public User createdUser;
	
	// FIXME replace this with something real!
	protected static User getDefaultUser() {
		User user = User.find("byName", "Default User").first();
		if (user == null) {
			user = new User("default@user.com", "password", "Default User");
	    	user.save();
		}
    	return user;
	}

	protected AuditedModel() {
		this.createdOn = new Date();
	}
	protected AuditedModel(User createdUser) {
		this();
		this.createdUser = createdUser;
	}
}
