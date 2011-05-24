package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Story extends Model {
	
	@Required @Column(nullable = false)
	public String title;
	public String description;
	
	@Column(nullable = false)
	public StoryStatus status;
	
	@OneToMany
	List<Task> tasks;
	
	@OneToMany
	List<Comment> comments;
	
	public Date createdOn;

	@ManyToOne(optional = false)
	public User createdUser;
	
	enum StoryStatus {
		Planned, Scheduled, InProgress, InReview, Finished
	}
	
	public Story(String title, User createdUser) {
		this.title = title;
		this.createdUser = createdUser;
		this.createdOn = new Date();
		this.status = StoryStatus.Planned;
	}
}
