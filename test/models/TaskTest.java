package models;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static models.Matchers.*;
import org.junit.Test;

public class TaskTest extends BasicModelTest {
	
    @Test
    public void newTaskTest() {
    	Task task = new Task(getDefaultStory(), "Task title", getDefaultUser());
    	assertThat(task, notNullValue());
    	task.description = "This is the description for the task";
    	task.save();
    	
    	List<Task> tasks = Task.findAll();
    	assertThat(tasks.size(), is(1));
    	task = tasks.get(0);
    	assertThat(task, notNullValue());
    	assertThat(task.title, is("Task title"));
    	assertThat(task.description, is("This is the description for the task"));
    	assertThat(task.createdOn, recentDate());
    	assertThat(task.createdUser, is(getDefaultUser()));
    }


}
