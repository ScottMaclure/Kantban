package models;

import static org.hamcrest.Matchers.*;
import static matchers.Matchers.recentDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Test;

import services.SystemTime;

// TODO Test addition and moving of stories.
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
}
