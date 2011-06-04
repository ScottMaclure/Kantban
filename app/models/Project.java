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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
	
	@Enumerated(EnumType.STRING)
	public ProjectStatus status;
	enum ProjectStatus {
		Proposed, Started, Finished, Closed
	}
	
	private void setDefaults() {
		status = ProjectStatus.Proposed;
		states = new ArrayList();
		states.add(new State(this, "Sandbox", "Stories that are not ready to be worked on yet."));
		states.add(new State(this, "Backlog", "Stories that have been prioritised and estimated."));
		states.add(new State(this, "In Progress", "Stories that are actively being worked on."));
		states.add(new State(this, "Completed", "Stories that have been finished."));
		states.add(new State(this, "Archive", "Stories that are no longer of interest."));
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
			for (Story story: state.stories) {
				story.state = states.get(position - 1);
			}
			states.remove(position);
		}
		return states;
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
	@Deprecated
	public void moveStoryBefore(@Nonnull Story story, @Nonnull Story referenceStory) {
		List<Story> list = getSwimlane(referenceStory.state);
	}
	
	/**
	 * Get the list of stories belonging in a swimlane, i.e. state
	 * 
	 * @param state - The state representing the swimlane
	 * @return The list, ordered by rank
	 */
	@Deprecated
	public List<Story> getSwimlane(State state) {
		List<Story> list = Story.find("project = ? AND state = ? ORDER BY rank", this, state).fetch();
		return list;
	}
	
	public State defaultState() {
		return states.get(0);
	}
}
