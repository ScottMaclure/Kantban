package models;

import static org.hamcrest.Matchers.*;
import static matchers.Matchers.recentDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import services.SystemTime;

/**
 * This class tests the management of statistics on State objects.
 * <p>
 * Note that it does not simply test the StateStatistics class, 
 * but rather all functionality using it.
 *  
 * @author mgjv
 */
public class StateStatisticsTest extends BasicModelTest {
	
    Calendar timeLine;		// The object to keep track of the time line.
    int elapsed;			// elapsed time in minutes
    Project project;		// The project we're working on

    @Before
	public void init() {
    	project = new Project("Statistics", getDefaultUser());
    	project.save();
	}
	
    /**
     * Test that the statistics about work states are kept correctly
     */
    @Test
    public void testStatistics() {
    	timeLine = Calendar.getInstance();
    	// Set time to 1 Jan 2011, start of working day.
    	timeLine.set(2011, 1, 1, 9, 0);
    	elapsed = warp(timeLine, 0);
    	// Create some new stories on the first day. Each takes two minutes
    	for (int i = 0; i < 5; i++) {
    		createStory(project.states.get(0), "Sandbox Story " + i);
    	}
    	assertLanes(5, 0, 0, 0);
    	assertThat(elapsed, is(25));
    	
    	// Create two stories in analysis
    	for (int i = 0; i < 2; i++) {
    		createStory(project.states.get(1), "Analysis Story " + i);
    	}
    	assertLanes(5, 2, 0, 0);
    	assertThat(elapsed, is(35));
    	
    	// End of day 1, go to next working day
    	elapsed += warp(timeLine, 24 * 60 - elapsed);
    	assertThat(elapsed, is(24 * 60));
    	
    	// TODO Much more
    	
    }

    // fast forward time
    private int warp(Calendar timeLine, int minutes) {
    	timeLine.add(Calendar.MINUTE, minutes);
    	SystemTime.setDate(timeLine.getTime());
    	return minutes;
    }
    
    private void createStory(State state, String title, int minutes) {
		Story story = state.newStory(title, getDefaultUser());
		assertThat(story, notNullValue());
		elapsed += warp(timeLine, minutes);
    }
    private void createStory(State state, String title) {
    	createStory(state, title, 5);
    }
    
    private void assertLanes(int l1, int l2, int l3, int last) {
    	List<State> states = project.states;
    	assertThat(states.get(0).stories.size(), is(l1));
    	assertThat(states.get(1).stories.size(), is(l2));
    	assertThat(states.get(2).stories.size(), is(l3));
    	assertThat(states.get(states.size() - 1).stories.size(), is(last));
    }
}
