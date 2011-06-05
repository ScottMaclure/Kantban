package models;
import static models.Matchers.recentDate;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.util.List;

import org.junit.Test;

public class ProjectTest extends BasicModelTest {
	
    @Test
    public void newProjectTest() {
    
    	Project project = new Project("New project", getDefaultUser());
    	assertThat(project, notNullValue());
    	project.save();
    	
    	List<Project> projects = Project.findAll();
    	assertThat(projects.size(), is(1));
    	project = projects.get(0);
    	assertThat(project, notNullValue());
    	// FIXME Should do this as a Matcher
    	assertThat(project.createdOn, recentDate());
    	assertThat(project.createdUser, is(getDefaultUser()));
    }
    
    @Test
    public void addAndRemoveStatesTest() {
    	Project project = new Project("New project", getDefaultUser());
    	assertThat(project, notNullValue());
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
    	Story story = project.defaultState().newStory("Story 1", getDefaultUser());
    	assertThat(project.defaultState().stories.size(), is(1));
    	project.addState(3, "Temp state", "Just a temporary state");
    	project.save();
    	assertThat(project.states.get(3).name, is("Temp state"));
    	State state = project.states.get(3);
    	state.addStory(story);
    	state.save();
    	
    	project.removeState(state);
    	assertThat(story.state, is(not(state)));
    	assertThat(story.state, is(project.states.get(2)));
    	project.save();
    }
    
     /* FIXME
    private void assertLaneSizes(Project project, int start, int second, int end) {
    	List<Story> sl;

    	sl = project.getSwimlane(project.states.get(0));
    	assertThat(sl.size(), is(start));
    	sl = project.getSwimlane(project.states.get(1));
    	assertThat(sl.size(), is(second));
    	sl = project.getSwimlane(project.states.get(project.states.size() - 1));
    	assertThat(sl.size(), is(end));
    }

    @Test
    public void testSwimlanes() {
    	// Set up the project
    	Project project = getDefaultProject();
    	User createdUser = getDefaultUser();
    	
    	List<Story> refStories = new ArrayList<Story>();
    	for (int i = 0; i < 5; i++) {
    		refStories.add(project.newStory("Story " + i, createdUser));
    	}
    	assertThat(project.stories.size(), is(5));
    	project.save();
    	
    	// Test the swim lanes
    	assertLaneSizes(project, 5, 0, 0);
    	project.moveStory(refStories.get(0), project.states.get(1), 1.0d);
    	project.save();
    	assertLaneSizes(project, 4, 1, 0);
    	project.moveStory(refStories.get(1), project.states.get(1), 1.0d);
    	project.moveStory(refStories.get(0), project.states.get(project.states.size() - 1), 1.0d);
    	project.moveStory(refStories.get(2), project.states.get(1), 1.0d);
    	project.save();
    	assertLaneSizes(project, 2, 2, 1);
    	project.deleteStory(refStories.get(3));
    	project.save();
    	assertThat(project.stories.size(), is(4));
    	assertLaneSizes(project, 1, 2, 1);
    }
	*/
}
