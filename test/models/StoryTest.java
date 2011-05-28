package models;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import org.junit.Test;

import play.Logger;

public class StoryTest extends BasicModelTest {
	
    @Test
    public void newStoryTest() {
    	String title = "Story title";
    	String description = "Some <a href=\"Link\">text</a> to go into this story";
    	
    	Project project = getDefaultProject();
    	Story story = project.newStory(title, getDefaultUser());
    	assertNotNull(story);
    	story.description = description;
    	assertThat(story.project, is(getDefaultProject()));
    	story.save();
    	
    	project = getDefaultProject();
    	assertThat(project, is(story.project));
    	assertThat(project.stories.size(), is(1));
    	
    	List<Story> stories = Story.findAll();
    	assertThat(1, is(stories.size()));
    	story = stories.get(0);
    	assertNotNull(story);
    	assertThat(title, is(story.title));
    	assertThat(description, is(story.description));
    	assertDateFresh(story.createdOn);
    	assertThat(getDefaultUser(), is(story.createdUser));
    	assertThat(getDefaultProject().states.get(0), is(story.state));
    }
}
