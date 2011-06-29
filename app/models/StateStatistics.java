package models;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import play.db.jpa.GenericModel;

public class StateStatistics extends GenericModel {
	
	@Id	@ManyToOne(optional = false)
	public State state;
	
	@Id	@Temporal(TemporalType.DATE)
	public Date day;
	
	public int number = 0;
	
	StateStatistics(State state, int number) {
		this.day = new Date();
		this.state = state;
		this.number = number;
	}
}
