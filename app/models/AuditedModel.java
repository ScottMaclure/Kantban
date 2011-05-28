package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import play.db.jpa.Model;

@MappedSuperclass
public abstract class AuditedModel extends Model {
	@Column(nullable = false)
	public Date createdOn;
	@ManyToOne(optional = false)
	public User createdUser;
	
	protected AuditedModel(User createdUser) {
		this.createdUser = createdUser;
		this.createdOn = new Date();
	}
}
