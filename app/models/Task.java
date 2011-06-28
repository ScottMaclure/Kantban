package models;

import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Task extends AuditedModel {

	// A task is always added to a story
	@Required 
	@ManyToOne(optional = false)
	public Story story;
	
	@Column(nullable = false)
	public String title;
	public String description;

	@ManyToOne(optional = true)
	@JoinColumn(name = "assigned_user")
	public User assignedUser;
	
	@Column(name = "estimated_time")
	public Integer estimatedTime; // minutes
	@Column(name = "actual_time")
	public Integer actualTime; // minutes

	/**
	 * Task creation should always be handled from the story API
	 * 
	 * @see St
	 * @param story
	 * @param type
	 * @param createdUser
	 */
	protected Task(@Nonnull Story story, @Nonnull String title, @Nonnull User createdUser) {
		super(createdUser);
		this.story = story;
		this.title = title;
	}
}
