package models;

import static org.hamcrest.Matchers.*;
import static matchers.Matchers.recentDate;

import java.util.Calendar;
import java.util.List;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Test;

import services.SystemTime;

public class StoryTest extends BasicModelTest {
	
    /**
     * Test that we can create a new story, and that this story has the right parameters.
     */
    @Test
    public void newStoryTest() {
    	String title = "Story title";
    	String description = "Some <a href=\"Link\">text</a> to go into this story";
    	
    	Project project = getDefaultProject();
    	State state = project.defaultState();
    	Story story = state.newStory(title, getDefaultUser());
    	assertThat(story, notNullValue());
    	story.description = description;
    	assertThat(story.getState().project, is(getDefaultProject()));
    	story.save();
    	
    	project = getDefaultProject();
    	assertThat(project, is(story.getState().project));
    	assertThat(project.defaultState().stories.size(), is(1));
    	
    	List<Story> stories = Story.findAll();
    	assertThat(1, is(stories.size()));
    	story = stories.get(0);
    	assertThat(story, notNullValue());
    	assertThat(title, is(story.title));
    	assertThat(description, is(story.description));
    	assertThat(story.createdOn, is(recentDate()));
    	assertThat(story.updatedOn, is(recentDate()));
    	assertThat(getDefaultUser(), is(story.createdUser));
    	assertThat(getDefaultProject().states.get(0), is(story.getState()));
    	
    	assertFalse(story.isArchived());
    	
    }
    
    // Check a story's state
    private void checkState(Story story, Integer lead, Integer cycle, int block, int wait, int work) {
    	if (lead == null)
        	assertThat(story.getLeadTime(), nullValue());
    	else 
        	assertThat(story.getLeadTime(), is(lead));
    	if (cycle == null)
        	assertThat(story.getCycleTime(), nullValue());
    	else 
        	assertThat(story.getCycleTime(), is(cycle));
    	assertThat(story.getBlockTime(), is(block));
    	assertThat(story.getWaitTime(), is(wait));
    	assertThat(story.getWorkTime(), is(work));
    }
    
    // fast forward time
    private int warp(Calendar timeLine, int minutes) {
    	timeLine.add(Calendar.MINUTE, minutes);
    	SystemTime.setDate(timeLine.getTime());
    	return minutes;
    }
    
    /**
     * Test that the statistics about work states are kept correctly
     */
    @Test
    public void testStatistics() {
    	Calendar timeLine = Calendar.getInstance();
    	int leadTime = warp(timeLine, 0);
    	
    	Story story = getDefaultStory();
    	assertThat(story, notNullValue());
    	// test initial state
    	checkState(story, null, null, 0, 0, 0);
    	
    	// Get some states to move the story along
    	List<State> states = story.getState().project.states;
    	//State sandbox = states.get(0);
    	State analysis = states.get(1);
    	State work = states.get(2);
    	State archive = states.get(states.size() - 1);
    	
    	// after 30 minutes, move it to the analysis lane
    	leadTime += warp(timeLine, 30);
    	analysis.addStory(story);
    	assertThat(story.getState(), is(analysis));
    	checkState(story, null, null, 0, 0, 0);
    	
    	// work on it for 5 minutes, then a further 15
    	leadTime += warp(timeLine, 5);
    	checkState(story, null, null, 0, 0, 5 * 60);
    	leadTime += warp(timeLine, 15);
    	checkState(story, null, null, 0, 0, (5 + 15) * 60);
    	
    	// block it for 20 minutes
    	story.setBlocked(true);
    	leadTime += warp(timeLine, 20);
    	checkState(story, null, null, 20 * 60, 0, (5 + 15) * 60);
    	
    	// unblock it for 10 minutes
    	story.setBlocked(false);
    	checkState(story, null, null, 20 * 60, 0, (5 + 15) * 60);
    	leadTime += warp(timeLine, 10);
    	checkState(story, null, null, 20 * 60, 0, (5 + 15 + 10) * 60);
    	
    	// block for another 5
    	story.setBlocked(true);
    	leadTime += warp(timeLine, 5);
    	checkState(story, null, null, (20 + 5) * 60, 0, (5 + 15 + 10) * 60);
    	
    	// mark as ready
    	story.setReady(true);
    	assertFalse(story.isBlocked());
    	checkState(story, null, null, (20 + 5) * 60, 0, (5 + 15 + 10) * 60);
    	leadTime += warp(timeLine, 7);
    	checkState(story, null, null, (20 + 5) * 60, 7 * 60, (5 + 15 + 10) * 60);
    	
    	// block again
    	story.setBlocked(true);
    	assertFalse(story.isReady());
    	checkState(story, null, null, (20 + 5) * 60, 7 * 60, (5 + 15 + 10) * 60);
    	leadTime += warp(timeLine, 25);
    	checkState(story, null, null, (20 + 5 + 25) * 60, 7 * 60, (5 + 15 + 10) * 60);
    	
    	// mark as ready again
    	story.setReady(true);
    	leadTime += warp(timeLine, 13);
    	checkState(story, null, null, (20 + 5 + 25) * 60, (7 + 13) * 60, (5 + 15 + 10) * 60);
    	
    	// pull to next lane, and work on it for 21 minutes
    	work.addStory(story);
    	assertFalse(story.isReady());
    	checkState(story, null, null, (20 + 5 + 25) * 60, (7 + 13) * 60, (5 + 15 + 10) * 60);
    	leadTime += warp(timeLine, 21);
    	checkState(story, null, null, (20 + 5 + 25) * 60, (7 + 13) * 60, (5 + 15 + 10 + 21) * 60);
    	
    	// Now archive it
    	archive.addStory(story);
    	checkState(story, leadTime * 60, (leadTime - 30) * 60, (20 + 5 + 25) * 60, (7 + 13) * 60, (5 + 15 + 10 + 21) * 60);
    	warp(timeLine, 210);
    	checkState(story, leadTime * 60, (leadTime - 30) * 60, (20 + 5 + 25) * 60, (7 + 13) * 60, (5 + 15 + 10 + 21) * 60);
    }
}
