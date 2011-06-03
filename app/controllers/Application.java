package controllers;

import java.util.List;

import javax.annotation.Nonnull;

import org.apache.log4j.Logger;

import models.Project;
import models.Story;
import play.mvc.Controller;

//@With(Secure.class)
public class Application extends Controller {
	
	private final static Logger log = Logger.getLogger(Application.class);

    public static void index() {
    	List<Project> projects = Project.all().fetch();
        render(projects);
    }
    
    public static void board(@Nonnull Long id) {
    	Project project = Project.findById(id);
    	log.debug("Rendering board for project " + id);
    	render(project);
    }

    public static void story(@Nonnull Long id) {
    	Story story = Story.findById(id);
    	render(story);
    }

}