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
public class Task extends AuditedModel {

	// A task is always added to a story
	@Required 
	@ManyToOne(optional = false)
	private Story story;
	
	@Column(nullable = false)
	public String title;
	public String description;

	@ManyToOne(optional = true)
	public User assignedUser;
	
	public Integer estimatedTime; // minutes
	public Integer actualTime; // minutes

	/**
	 * Task creation should always be handled from the story API
	 * 
	 * @see St
	 * @param story
	 * @param type
	 * @param createdUser
	 */
	protected Task(Story story, String title, User createdUser) {
		super(createdUser);
		this.story = story;
		this.title = title;
	}
	
	public Story getStory() {
		return story;
	}
}
