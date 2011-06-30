package models;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Embeddable;

/**
 * State stats primary key
 * <p>
 * This class is only here to function as the primary 
 * key for StateStatistics.
 * 
 * @author mgjv
 */
//@Embeddable
public class StateStatisticsPK implements Serializable {
	
	private State state;
	private Date day;
	
	public StateStatisticsPK(State state, Date day) {
		this.state = state;
		this.day = day;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((day == null) ? 0 : day.hashCode());
		result = prime * result + ((state == null) ? 0 : state.id.intValue());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		StateStatisticsPK other = (StateStatisticsPK) obj;
		if (day == null) {
			if (other.day != null)
				return false;
		} 
		else if (!day.equals(other.day))
			return false;
		
		if (state == null) {
			if (other.state != null)
				return false;
		} 
		else if (state.id != other.state.id)
			return false;
		
		return true;
	}
}
