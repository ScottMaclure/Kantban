package models;
import java.util.List;

import models.Story.StoryStatus;

import org.junit.Test;

public class StoryTest extends BasicModelTest {
	
    @Test
    public void newStoryTest() {
    	String title = "Story title";
    	String description = "Some <a href=\"Link\">text</a> to go into this story";
    	
    	Story story = new Story(title, getDefaultUser());
    	assertNotNull(story);
    	story.description = description;
    	story.save();
    	
    	List<Story> stories = Story.findAll();
    	assertEquals(1, stories.size());
    	story = stories.get(0);
    	assertNotNull(story);
    	assertEquals(title, story.title);
    	assertEquals(description, story.description);
    	assertEquals(StoryStatus.Planned, story.status);
    	assertDateFresh(story.createdOn);
    	assertEquals(getDefaultUser(), story.createdUser);
    }


}
