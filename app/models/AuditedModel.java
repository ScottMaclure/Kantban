package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import play.Logger;
import play.db.jpa.Model;

@MappedSuperclass
public abstract class AuditedModel extends Model {

	@Column(name = "created_on", nullable = false)
	public Date createdOn;

	@Column(name = "updated_on", nullable = false)
	public Date updatedOn;

	@JoinColumn(name = "created_user")
	@ManyToOne(optional = false)
	public User createdUser;

	// FIXME replace this with something real!
	/**
	 * This should try to work out what the 'current' user is.
	 * @return the default user for this environment
	 */
	protected static User getDefaultUser() {
		User user = User.find("byName", "Default User").first();
		if (user == null) {
			user = new User("default@user.com", "password", "Default User");
	    	user.save();
		}
    	return user;
	}

	protected AuditedModel() {
		createdOn = new Date();
		updatedOn = this.createdOn;
	}
	protected AuditedModel(User createdUser) {
		this();
		this.createdUser = createdUser;
	}
	
	/**
	 * Update the time stamps.
	 * <p>
	 * This should never be called by the application directly. 
	 * This is managed by the JPA entity manager. 
	 */
	@SuppressWarnings("unused")
	@PreUpdate
	@PrePersist
	private void setTimeStamps() {
	    updatedOn = new Date();
	    if (createdOn == null) {
	      createdOn = updatedOn;
	    }
	}
}
