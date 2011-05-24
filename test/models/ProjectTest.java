package models;
import java.util.List;

import models.Task.TaskType;

import org.junit.Test;

public class ProjectTest extends BasicModelTest {
	
    @Test
    public void newTaskTest() {
    
    	Project project = new Project("New project", getDefaultUser());
    	assertNotNull(project);
    	project.save();
    	
    	List<Project> projects = Project.findAll();
    	assertEquals(1, projects.size());
    	project = projects.get(0);
    	assertNotNull(project);
    	assertDateFresh(project.createdOn);
    	assertEquals(getDefaultUser(), project.createdUser);
    }


}
