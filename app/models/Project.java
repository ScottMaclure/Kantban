package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.apache.commons.lang.ArrayUtils;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Project extends AuditedModel {
	
	@Required 
	@Column(nullable = false)
	public String title;
	public String description;
	
	/**
	 * This defines the possible states for this project
	 * The first and last state are fixed, and cannot be 
	 * removed or moved.
	 */
	@OneToMany(cascade = CascadeType.ALL)
	public List<State> states;
	
	/**
	 * A list of all the stories associated with this project.
	 * <p>
	 * This relationship is owned by the Story class.
	 */
	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
	List<Story> stories; 
	
	public ProjectStatus status;
	enum ProjectStatus {
		Proposed, Started, Finished, Closed
	}
	
	public Project(@Nonnull String title, @Nonnull User createdUser) {
		this(title, null, createdUser);
	}
	
	public Project(@Nonnull String title, @Nullable String description, @Nonnull User createdUser) {
		super(createdUser);
		
		this.title = title;
		this.description = description;
		this.status = ProjectStatus.Proposed;
		
		this.stories = new ArrayList<Story>();
		
		// Create a new default list of states
		states = new ArrayList();
		states.add(new State("Sandbox", "Stories that are not ready to be worked on yet."));
		states.add(new State("Backlog", "Stories that have been prioritised and estimated."));
		states.add(new State("In Progress", "Stories that are actively being worked on."));
		states.add(new State("Completed", "Stories that have been finished."));
		states.add(new State("Archive", "Stories that are no longer of interest."));
	}
	
	/**
	 * Add a new state in the given position. 
	 * <p>
	 * Because the first and the last element cannot be moved, the position will
	 * be adjusted to avoid them.
	 * 
	 * @param position
	 * @param title
	 * @param description
	 * @return the new list of states.
	 */
	public List<State> addState(int position, @Nonnull String title, @Nonnull String description) {
		if (position < 1) {
			position = 1;
		}
		if (position > states.size() - 1) {
			position = states.size() - 1;
		}
		states.add(position, new State(title, description));
		return states;
	}
	
	/**
	 * Remove the given state from this project
	 * <p>
	 * If the first or last state are specified, this is a no-op.
	 * Any stories in this state will be moved back one.
	 * @param state
	 * @return the new list of states
	 */
	public List<State> removeState(@Nonnull State state) {
		int position = states.indexOf(state);
		if (position >= 1 && position < states.size() - 1) {
			for (Story story: stories) {
				System.out.println("Checking state for story " + story.title);
				if (story.state.equals(state)) {
					System.out.println("Resetting state for story " + story.title);
					story.state = states.get(position - 1);
				}
			}
			states.remove(position);
		}
		return states;
	}
	
	/**
	 * Create a new stroy in this project.
	 * 
	 * @param title
	 * @param createdUser
	 * @return
	 */
	public Story newStory(@Nonnull String title, User createdUser) {
		Story story = new Story(this, title, createdUser);
		stories.add(story);
		return story;
	}
		
	/**
	 * Add an existing story to this project
	 * <p>
	 * This can only happen if it is already attached to another project.
	 * 
	 * @param title
	 * @param createdUser
	 */
	public void addStory(@Nonnull Story story, @Nullable State state) {
		if (story.project != this) {
			if (story.project != null) {
				story.project.removeStory(story);
			}
			story.project = this;
			story.state = state != null ? state : states.get(0);
			stories.add(story);
		}
	}
	
	private void removeStory(@Nonnull Story story) {
		stories.remove(story);
	}
	
	/**
	 * Completely delete a story
	 * @param story
	 */
	public void deleteStory(Story story) {
		stories.remove(story);
		story.delete();
	}
}
