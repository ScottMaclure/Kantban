package models;

import java.util.Date;

import static org.hamcrest.CoreMatchers.*;

import org.junit.After;
import org.junit.Before;

import play.test.Fixtures;
import play.test.UnitTest;
import services.SystemTime;

public abstract class BasicModelTest extends UnitTest {
    
    @Before
    public void clean() {
        //System.out.println("Flushing");
        Fixtures.deleteDatabase();
    }
    
	@After
	public void resetAll() {
		SystemTime.reset();
	}

	protected User getDefaultUser() {
        User user = User.find("byName", "Default User").first();
        if (user == null) {
            user = new User("default@user.com", "password", "Default User");
            user.save();
        }
        return user;
    }
    
    protected Project getDefaultProject() {
        Project project = Project.find("byTitle", "Default Project").first();
        if (project == null) {
            project = new Project("Default Project", getDefaultUser());
            project.save();
        }
        return project;
    }
    
    protected Story getDefaultStory() {
        Project project = getDefaultProject();
        Story story = Story.find("byTitle", "Default Story").first();
        if (story == null) {
        	State state = project.states.get(0);
        	assertThat(state, notNullValue());
        	story = state.newStory("Default Story", getDefaultUser());
            story.save();
        }
        return story;
    }
}
