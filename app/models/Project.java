package models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import play.data.validation.Required;

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
	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
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
	
	private void setDefaults() {
		status = ProjectStatus.Proposed;
		stories = new ArrayList<Story>();
		states = new ArrayList();
		states.add(new State(this, "Sandbox", "Stories that are not ready to be worked on yet."));
		states.add(new State(this, "Backlog", "Stories that have been prioritised and estimated."));
		states.add(new State(this, "In Progress", "Stories that are actively being worked on."));
		states.add(new State(this, "Completed", "Stories that have been finished."));
		states.add(new State(this, "Archive", "Stories that are no longer of interest."));
	}
	
	// Do not use
	@SuppressWarnings("unused")
	private Project() {
		super();
		setDefaults();
	}
	
	public Project(@Nonnull String title, @Nonnull User createdUser) {
		this(title, null, createdUser);
	}
	
	public Project(@Nonnull String title, @Nullable String description, @Nonnull User createdUser) {
		super(createdUser);
		setDefaults();
		this.title = title;
		this.description = description;
		this.status = ProjectStatus.Proposed;
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
		states.add(position, new State(this, title, description));
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
				if (story.state.equals(state)) {
					story.state = states.get(position - 1);
				}
			}
			states.remove(position);
		}
		return states;
	}
	
	/**
	 * Create a new story in this project.
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
	
	/**
	 * Move a story to this project, with the given state and rank
	 * <p>
	 * If the story currently belongs to another project, it will be moved.
	 * 
	 * @param story The story to move
	 * @param state The target state for the story
	 * @param rank The target rank for the story
	 */
	protected void moveStory(@Nonnull Story story, @Nonnull State state, double rank) {
		if (story.project != this) {
			if (story.project != null) {
				story.project.removeStory(story);
			}
			story.project = this;
		}
		story.state = state;
		story.rank = rank;
	}
	
	/*
	 * Recalculate all ranks in a swimlane. While this is much
	 * simpler than recalculating only the ones that need 
	 * recalculation, this also results in all stories 
	 * needing persistence writes.
	 */
	private void recalculateAllRanks(List<Story> swimlane) {
		double rank = 0.0d;
		for (Story story: swimlane) {
			story.rank = rank;
			rank += 10.0d;
		}
	}
	
	/*
	 * This recalculates the ranks for a 'swimlane' story list.
	 * any stories that have a rank of null will be calculated, 
	 * based on their position in the list. 
	 * 
	 * TODO: On certain criteria, like ranks getting too close, 
	 * all ranks will be recalculated.  
	 */
	private void recalculateRanks(List<Story> swimlane) {
		ListIterator<Story> it = swimlane.listIterator();
		double previousRank = 0.0d;
		while (it.hasNext()) {
			Story story = it.next();
			if (story.rank == null) {
				double nextRank = it.hasNext() ? swimlane.get(it.nextIndex()).rank : 20.0d;
				story.rank = (previousRank + nextRank) / 2.0d;
			}
			previousRank = story.rank;
		}
	}
	
	/**
	 * Move a story just before another story.
	 * <p>
	 * State and rank will be determined from the given story.
	 * 
	 * @param story
	 * @param referenceStory
	 */
	public void moveStoryBefore(@Nonnull Story story, @Nonnull Story referenceStory) {
		List<Story> list = getSwimlane(referenceStory.state);
	}
	
	/**
	 * Get the list of stories belonging in a swimlane, i.e. state
	 * 
	 * @param state - The state representing the swimlane
	 * @return The list, ordered by rank
	 */
	public List<Story> getSwimlane(State state) {
		List<Story> list = Story.find("project = ? AND state = ? ORDER BY rank", this, state).fetch();
		return list;
	}
}
