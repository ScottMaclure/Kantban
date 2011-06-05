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
import javax.persistence.OrderBy;

import org.apache.log4j.Logger;

import play.data.validation.Required;

@Entity
public class Project extends AuditedModel {
	
	private final static Logger log = Logger.getLogger(State.class);

	@Required 
	@Column(nullable = false)
	public String title;
	public String description;
	
	/**
	 * This defines the possible states for this project
	 * The first and last state are fixed, and cannot be 
	 * removed or moved.
	 */
	@OrderBy("rank, id")
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
	
	// TODO Can we make this only change ranks that need changing?
	private void rerankStates() {
		double rank = 10.0d;
		for (State state: states) {
			state.rank = rank;
			rank += 10.0d;
		}
	}
	
	private void addState(int position, @Nonnull State state) {
		states.add(position, state);
		rerankStates();
	}

	/**
	 * Add a new state in the given position. 
	 * <p>
	 * Because the first and the last element cannot be moved, the position will
	 * be adjusted to avoid them.
	 * 
	 * @param position
	 * @param name
	 * @param description
	 * @return the new list of states.
	 */
	public List<State> addState(int position, @Nonnull String name, @Nonnull String description, Integer limit) {
		if (position < 1) {
			position = 1;
		}
		if (position > states.size() - 1) {
			position = states.size() - 1;
		}
		addState(position, new State(this, name, description, limit));
		return states;
	}
	
	public void newState(@Nonnull String name, @Nonnull String description, Integer limit) {
		log.debug("Creating new state for project " + id);
		addState(1, new State(this, name, description, limit));
	}
	
	/**
	 * Remove the given state from this project
	 * <p>
	 * If the first or last state are specified, this is a no-op.
	 * Any stories in this state will be moved back one.
	 * @param state
	 * @return the new list of states
	 */
	public boolean removeState(@Nonnull State state) {
		int position = states.indexOf(state);
		if (position >= 1 && position < states.size() - 1) {
			log.debug("Removing state " + state.id);
			for (Story story: state.stories) {
				story.state = states.get(position - 1);
			}
			states.remove(position);
			return true;
		}
		return false;
	}
	
	public boolean moveState(@Nonnull State state, int index) {
		if (states.remove(state)) {
			states.add(index, state);
			rerankStates();
			return true;
		}
		return false;
	}
	
	public State defaultState() {
		return states.get(0);
	}
}
