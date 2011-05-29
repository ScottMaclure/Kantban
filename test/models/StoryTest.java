package models;

import static models.Matchers.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.List;

import org.junit.Test;

public class StoryTest extends BasicModelTest {
	
    @Test
    public void newStoryTest() {
    	String title = "Story title";
    	String description = "Some <a href=\"Link\">text</a> to go into this story";
    	
    	Project project = getDefaultProject();
    	Story story = project.newStory(title, getDefaultUser());
    	assertThat(story, notNullValue());
    	story.description = description;
    	assertThat(story.project, is(getDefaultProject()));
    	story.save();
    	
    	project = getDefaultProject();
    	assertThat(project, is(story.project));
    	assertThat(project.stories.size(), is(1));
    	
    	List<Story> stories = Story.findAll();
    	assertThat(1, is(stories.size()));
    	story = stories.get(0);
    	assertThat(story, notNullValue());
    	assertThat(title, is(story.title));
    	assertThat(description, is(story.description));
    	assertThat(story.createdOn, is(recentDate()));
    	assertThat(getDefaultUser(), is(story.createdUser));
    	assertThat(getDefaultProject().states.get(0), is(story.state));
    }
}
