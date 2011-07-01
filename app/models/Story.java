package models;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.Required;
import play.db.jpa.Model;
import services.SystemTime;

/**
 * A Story
 * <p>
 * 
 * @author mgjv
 * @todo Add (event) tracking of each state and status change, rather than just summaries.
 */
@Entity
public class Story extends AuditedModel {
	
	// This should be protected
	@Required 
	@ManyToOne(optional = false) 
	private State state;
	
	@Required 
	@Column(nullable = false)
	public String title;
	public String description;
	@Lob
	public String details;
	
	@ManyToOne(optional = true)
	public User owner;
	
	/**
	 * Rank is used to order stories relative to each other.
	 */
	public Double rank;
	
	@OneToMany(mappedBy = "story")
	List<Task> tasks;
	
	@OneToMany(mappedBy = "story")
	List<Comment> comments;
	
    @ManyToMany(cascade=CascadeType.PERSIST)
    public Set<Tag> tags;
    
    // FIXME
    //@Column(nullable = false)
    public String colour;
	// TODO this can be removed once we make it non-nullable.
	public String getColour() {
		return colour != null ? colour : "grey";
	}
	
    /*
     * Story metrics: All in seconds spent in a work state
     * 
     * A work state is defined by whether the story is in the 
     * backlog, archive, another lane, and whether it's ready 
     * and/or blocked.   
     */
	@Column(name = "cycle_start")
	private Date cycleStart;		// When pulled out of backlog
	@Column(name = "archived_on")
	private Date archivedOn;   		// When moved into archive
	@Column(name = "ready_on")
	private Date readyOn;			// When marked as ready
	@Column(name = "wait_time")
	private int waitTime;			// How long spent in 'ready' state, not counting current state
	@Column(name = "blocked_on")
	private Date blockedOn;			// When blocked
	@Column(name = "block_time")
    private int blockTime;			// How long spent in 'blocked' state not counting current state

	
    /**
     * The time a story has spent in work lanes
     * <p>
     * This is started when the story is pulled from the 
     * backlog for the first time, and ends when the story is archived.
     * @return seconds
     */
    public Integer getCycleTime() {
    	if (cycleStart != null && archivedOn != null) {
    		Long l = (archivedOn.getTime() - cycleStart.getTime())/1000;
    		return l.intValue();
    	}
    	else {
    		return null;
    	}
    }
    
    /**
     * The time elapsed between creation and archival of a story.
     * @return seconds
     */
    public Integer getLeadTime() {
    	return archivedOn != null ? dateDiff(createdOn, archivedOn) : null;
    }
    
    /**
     * The time spent in 'ready' state
     * @return seconds
     */
    public int getWaitTime() {
    	return readyOn != null 
    			? dateDiff(readyOn, SystemTime.asMillis()) + waitTime
    			: waitTime;
    }
    /**
     * The time spent in 'blocked' state
     * @return seconds
     */
    public int getBlockTime() {
    	return blockedOn != null 
    			? dateDiff(blockedOn, SystemTime.asMillis()) + blockTime
    			: blockTime;
	}
    
    /*
     * Return the difference between two dates or epoch times
     */
    private int dateDiff(Date date1, Date date2) {
		return dateDiff(date1.getTime(), date2.getTime());
    }
    /*
    private int dateDiff(long time1, Date date2) {
		return dateDiff(time1, date2.getTime());
    }
    */
    private int dateDiff(Date date1, long time2) {
		return dateDiff(date1.getTime(), time2);
    }
    private int dateDiff(long time1, long time2) {
		return (int) ((time2 - time1)/1000);
    }
    
    /**
     * Work time spent on story.
     * <p>
     * This is calculated by taking the cycle time, and 
     * subtracting all non-productive time, i.e. blocking and waiting.
     * <p>
     * If the story is not archived yet, it will not use archival but now.
     * @return
     */
    public int getWorkTime() {
    	if (cycleStart != null) {
    		Date refDate = archivedOn != null ? archivedOn : SystemTime.asDate();
    		return dateDiff(cycleStart, refDate) - getBlockTime() - getWaitTime();
    	}
    	else {
    		return 0;
    	}
	}
    
    // TODO Special handling for move to archive. We need to unblock?
    public void setState(State state) {
    	if (state != this.state) {
	    	setReady(false);
	   		if (this.state == getSandBox() && cycleStart == null) {
	   			cycleStart = SystemTime.asDate();
	    	}
	   		if (state == getArchive()) {
	   			archivedOn = SystemTime.asDate();
	   		}
			this.state = state;
    	}
	}
    public State getState() {
		return state;
	}
    
    public void setReady(boolean ready) {
    	if (ready != isReady()) {
    		if (ready) {
    			setBlocked(false);
    			readyOn = SystemTime.asDate();
    		}
    		else {
    			waitTime += dateDiff(readyOn, SystemTime.asMillis());
    			readyOn = null;
    		}
    	}
	}    
    public boolean isReady() {
		return readyOn != null;
	}
    
    public void setBlocked(boolean blocked) {
    	if (blocked != isBlocked()) {
    		if (blocked) {
    			setReady(false);
    			blockedOn = SystemTime.asDate();
    		}
    		else {
    			blockTime += dateDiff(blockedOn, SystemTime.asMillis());
    			blockedOn = null;
    		}
    	}
	}
    public boolean isBlocked() {
		return blockedOn != null;
	}
       
	/**
	 * Create a new story
	 * <p>
	 * Story creation and deletion is entirely managed through
	 * the Project model interface.
	 * <p>
	 * <em>Do not construct a story directly.</em>
	 *  
	 * @param project
	 * @param state
	 * @param title
	 * @param createdUser
	 */
	protected Story(@Nonnull State state, @Nonnull String title, @Nonnull User createdUser) {
		super(createdUser);
		this.title = title;
		// TODO Check that state is in project
		this.state = state;
	}

	/**
	 * Create a new story with the default state
	 * @see #Story(State, String, User)
	 */
	protected Story(@Nonnull Project project, @Nonnull String title, @Nonnull User createdUser) {
		this(project.states.get(0), title, createdUser);
	}
	
	/**
	 * Add a comment to this story
	 * @param text
	 * @param createdUser
	 * @return the newly created comment.
	 */
	public Comment newComment(@Nonnull String text, @Nonnull User createdUser) {
		Comment comment = new Comment(this, text, createdUser);
		comments.add(comment);
		return comment;
	}
	
	/**
	 * Add a task to this story
	 * @param title
	 * @param createdUser
	 * @return the newly created task
	 */
	public Task newTask(@Nonnull String title, @Nonnull User createdUser) {
		Task task = new Task(this, title, createdUser);
		tasks.add(task);
		return task;
	}
	
	public boolean isArchived() {
		return state == getArchive();
	}

	// In the below two we need to be careful, because of object 
	// initialisation. setState() gets called immediately
	// after the empty constructor is called.
	private State getSandBox() {
		if (state != null) {
			return state.project.states.get(0);
		}
		else {
			return null;
		}
	}
	private State getArchive() {
		if (state != null) {
			List<State> states = state.project.states;
			return states.get(states.size() - 1);
		}
		else {
			return null;
		}
	}
	
}
