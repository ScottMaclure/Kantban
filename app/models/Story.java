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

@Entity
public class Story extends AuditedModel {
	
	// This should be protected
	@Required 
	@ManyToOne(optional = false) 
	public State state;
	
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
    
    /* 
     * Story metrics: All in seconds spent in a state
     * 
     *  Lead time: total time spent 
     */
	@Column(name = "cycle_started_on")
    Date cycleStartedOn;    // When pulled out of backlog
	@Column(name = "archived_on")
    Date archivedOn;   		// When moved into archive
	@Column(name = "work_time")
    Integer workTime;		// How long this spent in work
	@Column(name = "wait_time")
    Integer waitTime;		// How long this spent in 'ready' state
	@Column(name = "block_time")
    Integer blockTime;		// How long spent in 'blocked' state
    
    public Integer getCycleTime() {
    	if (cycleStartedOn != null && archivedOn != null) {
    		Long l = (archivedOn.getTime() - cycleStartedOn.getTime())/1000;
    		return l.intValue();
    	}
    	else {
    		return null;
    	}
    }
    
    public Integer getLeadTime() {
    	if (archivedOn != null) {
    		Long l = (archivedOn.getTime() - createdOn.getTime())/1000;
    		return l.intValue();
    	}
    	else {
    		return null;
    	}
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
	
	public Comment newComment(@Nonnull String text, @Nonnull User createdUser) {
		Comment comment = new Comment(this, text, createdUser);
		comments.add(comment);
		return comment;
	}
	
	public Task newTask(@Nonnull String title, @Nonnull User createdUser) {
		Task task = new Task(this, title, createdUser);
		tasks.add(task);
		return task;
	}
	
	public String getColour() {
		return colour != null ? colour : "grey";
	}
}
