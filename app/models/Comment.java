package models;

import java.util.Date;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Comment extends AuditedModel {
	
	// A comment is always made on a story
	@Required 
	@ManyToOne(optional = false)
	public Story story;

	@Required
	@Lob
	@Column(nullable = false)
	public String text;

	// TODO add optional attachment
	
	public Comment(@Nonnull Story story, @Nonnull String text, @Nonnull User createdUser) {
		super(createdUser);
		this.story = story;
		this.text = text;
	}
}
