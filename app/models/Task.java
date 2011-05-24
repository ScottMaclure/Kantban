package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class Task extends Model {

	public Date createdOn;
	
	@ManyToOne(optional = false)
	public User createdUser;
	@ManyToOne(optional = true)
	public User assignedUser;
	
	@Column(nullable = false)
	public TaskType type;
	
	@OneToMany
	List<Comment> comments;
	
	public Integer estimatedTime; // minutes
	public Integer actualTime; // minutes

	enum TaskType {
		Bug, Task	
	}
	
	public Task(TaskType type, User createdUser) {
		this.type = type;
		this.createdUser = createdUser;
		this.createdOn = new Date();
	}
}
