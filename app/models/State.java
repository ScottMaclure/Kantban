package models;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

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
	
	@Column(nullable = false)
	public String name, description;
	
	@Column(name = "story_limit") // avoid syntax error
	public Integer limit;
	
	@OneToMany(mappedBy = "state", cascade = CascadeType.PERSIST)
	@OrderBy("rank")
	public List<Story> stories;
	
	/**
	 * A state gets created as part of a project
	 * @param project
	 * @param name
	 * @param description
	 */
	protected State(@Nonnull Project project, @Nonnull String name, @Nonnull String description) {
		this.project = project;
		this.name = name;
		this.description = description;
		stories = new ArrayList<Story>();
	}
	
	public Story newStory(@Nonnull String title, @Nonnull User createdUser) {
		Story story = new Story(this, title, createdUser);
		stories.add(story);
		rerankStories();
		return story;
	}

	public void addStory(@Nonnull Story story) {
		story.state = this;
		stories.add(story);
		rerankStories();
	}
	
	public boolean moveStory(@Nonnull Story story, int index) {
		boolean found = (story.state != this) ?
			stories.remove(story) :
			story.state.stories.remove(story);
		
		if (found) {
			story.state = this;
			stories.add(index, story);
			rerankStories();
			log.debug("Moved story " + story.id + " to position " + index);
			return true;
		}
		else {
			log.warn("Cannot move " + story.id + " to position " + index);
			return false;
		}
	}
	
	// TODO Can we make this only change ranks that need changing?
	private void rerankStories() {
		double rank = 10.0d;
		for (Story story: stories) {
			story.rank = rank;
			rank += 10.0d;
		}
	}

}
