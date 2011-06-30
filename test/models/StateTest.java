package models;

import static org.hamcrest.Matchers.*;
import static matchers.Matchers.recentDate;

import java.util.Calendar;
import java.util.List;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Test;

import services.SystemTime;

public class StateTest extends BasicModelTest {
	
    @Test
    public void newStateTest() {
    	String name = "test state";
    	String description = "this is a test state";
    	int limit = 10;
    	
    	Project project = getDefaultProject();
    	
    	State state = new State(project, name, description);
    	assertThat(state, notNullValue());
    	assertThat(state.name, is(name));
    	assertThat(state.description, is(description));
    	assertThat(state.limit, nullValue());

    	state = new State(project, name, description, limit);
    	assertThat(state, notNullValue());
    	assertThat(state.name, is(name));
    	assertThat(state.description, is(description));
    	assertThat(state.limit, is(limit));
    }
    
    // TODO Test addition and moving of stories.
    
    // fast forward time
    private int warp(Calendar timeLine, int days) {
    	timeLine.add(Calendar.DAY_OF_YEAR, days);
    	SystemTime.setDate(timeLine.getTime());
    	return days;
    }
    
    /**
     * Test that the statistics about work states are kept correctly
     */
    @Test
    public void testStatistics() {
    	Project project = new Project("Statistics", getDefaultUser());
    	State sandbox = project.states.get(0);
    	State analyse = project.states.get(1);
    	State work = project.states.get(2);
    	State archive = project.states.get(project.states.size() - 1);
    	
    	// Create some new stories
    }
}
