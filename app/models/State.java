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
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;

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
	@OrderBy("rank, id")
	public List<Story> stories;
	
	public Double rank;
	
	/**
	 * A state gets created as part of a project
	 * @param project
	 * @param name
	 * @param description
	 * @param limit
	 */
	protected State(@Nonnull Project project, @Nonnull String name, @Nonnull String description, Integer limit) {
		this.project = project;
		this.name = name;
		this.description = description;
		this.limit = limit;
		stories = new ArrayList<Story>();
	}
	
	protected State(@Nonnull Project project, @Nonnull String name, @Nonnull String description) {
		this(project, name, description, null);
	}

	public Story newStory(@Nonnull String title, @Nonnull User createdUser) {
		Story story = new Story(this, title, createdUser);
		stories.add(story);
		rerankStories();
		updateStatistics();
		return story;
	}

	public void addStory(@Nonnull Story story) {
		State oldState = story.getState();
		if (oldState != this) {
			story.setState(this);
			stories.add(story);
			oldState.stories.remove(story);
			rerankStories();
			oldState.updateStatistics();
			updateStatistics();
			log.trace("Added story " + story.id + " to state " + id);
		}
	}
	
	/** 
	 * Moves a story to a given position in this state
	 * <p>
	 * If the story is currently in a different state, 
	 * this will fail. 
	 * @param story
	 * @param index
	 * @return whether the move was successful
	 */
	public boolean moveStory(@Nonnull Story story, int index) {
		boolean found = stories.remove(story);
		
		if (found) {
			story.setState(this);
			stories.add(index, story);
			rerankStories();
			log.trace("Moved story " + story.id + " to position " + index);
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

	/**
	 * Ensure that statistics are kept on each persist.
	 * <p>
	 * This is automatically called by the internals of this class,
	 * and does not need to be called explicitly outside of this class.
	 * It is public only to provide a method for data initialisation
	 * in dev and test environments.
	 */
	private void updateStatistics() {
		log.debug("Updating statistics for State " + id);
		StateStatistics stats = new StateStatistics(this, stories.size());
		stats = stats.merge();
		stats.save();
	}
}
