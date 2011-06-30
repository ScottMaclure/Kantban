package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import play.db.jpa.GenericModel;
import services.SystemTime;

/**
 * Daily statistics for states
 * <p>
 * The only number kept in here is the number of stories in the state.
 * @author mgjv
 */
@Entity
@IdClass(StateStatisticsPK.class)
@Table(name="state_statistics")
public class StateStatistics extends GenericModel {
	
	@Id	@ManyToOne(optional = false)
	public State state;
	
	@Id	@Temporal(TemporalType.DATE)
	public Date day;
	
	public int number = 0;
	
	StateStatistics(State state, int number) {
		this.day = SystemTime.asDate();
		this.state = state;
		this.number = number;
	}	
}
