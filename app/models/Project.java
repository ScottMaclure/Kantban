package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Project extends Model {
	
	@Required @Column(nullable = false)
	public String title;
	public String description;
	
	@Column(nullable = false)
	public Date createdOn;
	
	@ManyToOne(optional = false)
	public User createdUser;
	
	@OneToMany
	List<Story> stories;
	
	enum ProjectStatus {
		Proposed, Started, Finished, Closed
	}
	
	public Project(String title, User createdUser) {
		this.title = title;
		this.createdUser = createdUser;
		this.createdOn = new Date();
	}
}
