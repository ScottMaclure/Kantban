package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class Task extends AuditedModel {

	@ManyToOne(optional = true)
	public User assignedUser;
	
	@Column(nullable = false)
	public TaskType type;
	
	public Integer estimatedTime; // minutes
	public Integer actualTime; // minutes

	enum TaskType {
		Bug, Task	
	}
	
	public Task(TaskType type, User createdUser) {
		super(createdUser);
		this.type = type;
	}
}
