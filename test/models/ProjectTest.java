package models;
import java.util.List;

import models.Task.TaskType;

import static org.hamcrest.CoreMatchers.*;
import org.junit.Test;

public class ProjectTest extends BasicModelTest {
	
    @Test
    public void newProjectTest() {
    
    	Project project = new Project("New project", getDefaultUser());
    	assertNotNull(project);
    	project.save();
    	
    	List<Project> projects = Project.findAll();
    	assertThat(projects.size(), is(1));
    	project = projects.get(0);
    	assertNotNull(project);
    	// FIXME Should do this as a Matcher
    	assertDateFresh(project.createdOn);
    	assertThat(project.createdUser, is(getDefaultUser()));
    }
    
    @Test
    public void addAndRemoveStatesTest() {
    	Project project = new Project("New project", getDefaultUser());
    	assertNotNull(project);
    	assertThat(project.states.size(), is(5));
    	project.save();
    	
    	project = Project.all().first();
    	
    	// Ensure that the order of the default states is preserved
    	assertThat(project.states.get(0).name, is("Sandbox"));
    	assertThat(project.states.get(1).name, is("Backlog"));
    	assertThat(project.states.get(2).name, is("In Progress"));
    	assertThat(project.states.get(3).name, is("Completed"));
    	assertThat(project.states.get(4).name, is("Archive"));
    	
    	// Try to insert a new state, and remove it
    	project.addState(1, "Test state", "This is a test State");
    	project.save();
    	project = Project.all().first();
    	assertThat(project.states.get(1).name, is("Test state"));
    	assertThat(project.states.get(1).description, is("This is a test State"));
       	assertThat(project.states.size(), is(6));
       	project.removeState(project.states.get(1));
    	project.save();
    	project = Project.all().first();
       	assertThat(project.states.get(1).name, is(not("Test State")));
       	assertThat(project.states.size(), is(5));
       	
       	// Try to insert a new state at the start and observe it being at position 1
    	project.addState(0, "Test state", "This is a test State");
    	assertThat(project.states.get(0).name, is("Sandbox"));
    	assertThat(project.states.get(1).name, is("Test state"));
       	project.removeState(project.states.get(1));

       	// Try to insert a new state at the end and observe it being at position size() - 2
    	project.addState(5, "Test state", "This is a test State");
    	assertThat(project.states.get(5).name, is("Archive"));
    	assertThat(project.states.get(4).name, is("Test state"));
       	project.removeState(project.states.get(3));

       	// Try to remove the first and the last element
    	project.removeState(project.states.get(0));
       	assertThat(project.states.size(), is(5));
    	project.removeState(project.states.get(4));
       	assertThat(project.states.size(), is(5));
    }
    
    /**
     * Remove a project state that has stories in it.
     * Ensure the stories end up with another state
     */
    @Test
    public void removeStateWithStory() {
    	Project project = getDefaultProject();
    	Story story = project.newStory("Story 1", getDefaultUser());
    	assertThat(project.stories.size(), is(1));
    	project.addState(3, "Temp state", "Just a temporary state");
    	assertThat(project.states.get(3).name, is("Temp state"));
    	State state = project.states.get(3);
    	story.state = state;
    	story.save();
    	
    	project.removeState(state);
    	assertThat(story.state, is(not(state)));
    	assertThat(story.state, is(project.states.get(2)));
    	project.save();
    }

}
