package models;
import java.util.List;

import models.Task.TaskType;

import org.junit.Test;

public class TaskTest extends BasicModelTest {
	
    @Test
    public void newTaskTest() {
    	Task task = new Task(TaskType.Task, getDefaultUser());
    	assertNotNull(task);
    	task.save();
    	
    	List<Task> tasks = Task.findAll();
    	assertEquals(1, tasks.size());
    	task = tasks.get(0);
    	assertNotNull(task);
    	assertEquals(TaskType.Task, task.type);
    	assertDateFresh(task.createdOn);
    	assertEquals(getDefaultUser(), task.createdUser);
    }


}
