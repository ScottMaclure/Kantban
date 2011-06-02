package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * The current State of a story
 * 
 * Each project has a process associated which controls how a 
 * Story flows from inception to completion. This process is 
 * expressed as a list of states that a Story has to go 
 * through in series.
 * 
 * @author mgjv
 */
@Entity
public class State extends Model {
	
	// A state is always associated with a project
	@Required 
	@ManyToOne(optional = false) 
	public Project project;
	
	@Column(nullable=false)
	public String name, description;
	
	@OneToMany(mappedBy = "state")
	List<Story> stories;
	
	/**
	 * States are always managed through the Project API
	 * @param project
	 * @param name
	 * @param description
	 */
	protected State(Project project, String name, String description) {
		this.project = project;
		this.name = name;
		this.description = description;
	}

}
