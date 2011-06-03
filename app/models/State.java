package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.apache.log4j.Logger;

import controllers.Application;

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
	
	private final static Logger log = Logger.getLogger(State.class);

	// A state is always associated with a project
	@Required 
	@ManyToOne(optional = false) 
	public Project project;
	
	@Column(nullable=false)
	public String name, description;
	
	@OneToMany(mappedBy = "state")
	public List<Story> stories;
	
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

	/**
	 * Re-rank a story in this state
	 * 
	 * @param index the new index of the story
	 */
	public boolean moveStory(Story story, int index) {
		boolean found = (story.state != this) ?
			stories.remove(story) :
			story.state.stories.remove(story);
		
		if (found) {
			story.state = this;
			this.project.addStory(story, null);
			stories.add(index, story);
			log.debug("Moved story " + story.id + " to position " + index);
			return true;
		}
		else {
			log.warn("Cannot move " + story.id + " to position " + index);
			return false;
		}
	}

}
