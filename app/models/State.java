package models;

import javax.persistence.Column;
import javax.persistence.Entity;

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
	
	@Column(nullable=false)
	public String name, description;
	
	public State(String name, String description) {
		this.name = name;
		this.description = description;
	}

}
