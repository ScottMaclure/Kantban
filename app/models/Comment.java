package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class Comment extends Model {
	
	public String comment;
	// TODO add attachment

	@Column(nullable = false)
	public Date createdOn;
	
	@ManyToOne(optional = false)
	public User createdUser;
	
	public Comment(String comment, User createdUser) {
		this.comment = comment;
		this.createdUser = createdUser;
		this.createdOn = new Date();
	}
	
}
