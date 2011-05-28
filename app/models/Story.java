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
	
	@Required @Column(nullable = false)
	public String title;
	public String description;
	@Lob
	public String details;
	
	// A story has to be in a project
	@Required @ManyToOne(optional = false) 
	public Project project;
	
	@ManyToOne(optional = false, cascade = CascadeType.ALL)
	public State state;
	
	@OneToMany
	List<Task> tasks;
	
	@OneToMany
	List<Comment> comments;
	
    @ManyToMany(cascade=CascadeType.PERSIST)
    public Set<Tag> tags;
	
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
	protected Story(@Nonnull Project project, @Nonnull State state, @Nonnull String title, @Nonnull User createdUser) {
		super(createdUser);
		this.project = project;
		this.title = title;
		// TODO Check that state is in project
		this.state = state;	
	}

	/**
	 * Create a new storywith the default state
	 * @see #Story(Project, State, String, User)
	 */
	protected Story(@Nonnull Project project, @Nonnull String title, @Nonnull User createdUser) {
		this(project, project.states.get(0), title, createdUser);
	}
}
