package controllers;

import java.util.List;

import javax.annotation.Nonnull;

import models.Project;
import models.Story;
import play.mvc.Controller;

//@With(Secure.class)
public class Application extends Controller {

    public static void index() {
    	List<Project> projects = Project.all().fetch();
        render(projects);
    }
    
    public static void board(@Nonnull Long id) {
    	Project project = Project.findById(id);
    	render(project);
    }

    public static void story(@Nonnull Long id) {
    	Story story = Story.findById(id);
    	render(story);
    }

}