package models;

import static org.hamcrest.Matchers.*;
import static matchers.Matchers.recentDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
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
	
	private final static Logger log = Logger.getLogger(State.class);
	
    Calendar timeLine;		// The object to keep track of the time line.
    int elapsed;			// elapsed time in minutes
    Project project;		// The project we're working on

    @Before
	public void init() {
    	project = new Project("Statistics", getDefaultUser());
    	project.save();
    	timeLine = Calendar.getInstance();
    	// Set time to 1 Jan 2011, start of working day.
    	timeLine.set(2011, 1, 1, 9, 0);
    	elapsed = warp(0);
	}
	
    /**
     * Test that the statistics about work states are kept correctly
     */
    @Test
    public void testStatistics() {
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
    	nextDay();
    	assertThat(elapsed, is(24 * 60));
    	
    	// Add two new stories to backlog, move one up, and move one from 1 to 2.
    	for (int i = 0; i < 2; i++) {
    		createStory(project.states.get(0), "Sandbox Story " + (i + 5));
    	}
    	assertLanes(7, 2, 0, 0);
    	pullStory(project.states.get(0).stories.get(0));
    	assertLanes(6, 3, 0, 0);
    	pullStory(project.states.get(1).stories.get(0));
    	assertLanes(6, 2, 1, 0);
    	assertThat(elapsed, is(24*60 + 4*5));
    	
    	nextDay();
    	assertThat(elapsed, is(2 * 24 * 60));

    	pullStory(project.states.get(0).stories.get(0));
    	pullStory(project.states.get(0).stories.get(0));
    	pullStory(project.states.get(1).stories.get(0));
    	assertLanes(4, 3, 2, 0);
    	
    	archiveStory(project.states.get(2).stories.get(0));
    	assertLanes(4, 3, 1, 1);
    	
    	// weekend
    	nextDay(3);
    	assertThat(elapsed, is(5 * 24 * 60));
    	
    	pullStory(project.states.get(0).stories.get(0));
    	pullStory(project.states.get(1).stories.get(0));
    	archiveStory(project.states.get(2).stories.get(0));
    	archiveStory(project.states.get(0).stories.get(0));
    	assertLanes(2, 3, 1, 3);
    	
    	nextDay();

    	createStory(project.states.get(0), "Sandbox Story " + (5 + 2 + 1));
    	archiveStory(project.states.get(2).stories.get(0));
    	assertLanes(3, 3, 0, 4);

    	nextDay();

    	pullStory(project.states.get(1).stories.get(0));
    	pullStory(project.states.get(1).stories.get(0));
    	pullStory(project.states.get(0).stories.get(0));
    	pullStory(project.states.get(0).stories.get(0));
    	archiveStory(project.states.get(2).stories.get(0));
    	assertLanes(1, 3, 1, 5);
    	
    	nextDay();
    	
    	pullStory(project.states.get(1).stories.get(0));
    	pullStory(project.states.get(1).stories.get(0));
    	pullStory(project.states.get(0).stories.get(0));
    	archiveStory(project.states.get(2).stories.get(0));
    	assertLanes(0, 2, 2, 6);
    	
    	pullStory(project.states.get(1).stories.get(0));
    	pullStory(project.states.get(1).stories.get(0));
    	archiveStory(project.states.get(2).stories.get(0));
    	archiveStory(project.states.get(2).stories.get(0));
    	assertLanes(0, 0, 2, 8);
    	
    	archiveStory(project.states.get(2).stories.get(0));
    	archiveStory(project.states.get(2).stories.get(0));
    	assertLanes(0, 0, 0, 10);
    }

    // fast forward time
    private int warp(int minutes) {
    	timeLine.add(Calendar.MINUTE, minutes);
    	SystemTime.setDate(timeLine.getTime());
    	return minutes;
    }
    private void nextDay(int n) {
    	int minutesToday = elapsed % (24 * 60);
    	elapsed += warp(n * 24 * 60 - minutesToday);	
    }
    private void nextDay() {
    	nextDay(1);
    }
    
    private void createStory(State state, String title, int minutes) {
		Story story = state.newStory(title, getDefaultUser());
		assertThat(story, notNullValue());
		elapsed += warp(minutes);
    }
    private void createStory(State state, String title) {
    	createStory(state, title, 5);
    }
    
    private void pullStory(Story story, int minutes) {
    	int stateIndex = project.states.indexOf(story.getState());
    	assertThat(stateIndex, not(-1));
    	assertThat(stateIndex, lessThan(project.states.size()));
    	State toState = project.states.get(stateIndex + 1);
    	toState.addStory(story);
		elapsed += warp(minutes);
    }
    private void pullStory(Story story) {
    	pullStory(story, 5);
    }
    
    private void archiveStory(Story story, int minutes) {
    	State archive = project.states.get(project.states.size() - 1);
    	archive.addStory(story);
		elapsed += warp(minutes);
    }
    private void archiveStory(Story story) {
    	archiveStory(story, 5);
    }
    
    private void assertLanes(int l1, int l2, int l3, int last) {
    	List<State> states = project.states;
    	assertThat(states.get(0).stories.size(), is(l1));
    	assertThat(states.get(1).stories.size(), is(l2));
    	assertThat(states.get(2).stories.size(), is(l3));
    	assertThat(states.get(states.size() - 1).stories.size(), is(last));
    }
}
