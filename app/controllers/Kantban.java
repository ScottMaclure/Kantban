package controllers;

import javax.annotation.Nonnull;

import models.Project;
import models.Story;

import org.apache.log4j.Logger;

import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Kantban extends Controller {
	
	private final static Logger log = Logger.getLogger(Kantban.class);

    public static void board(@Nonnull Long id) {
    	Project project = Project.findById(id);
    	log.debug("Rendering board for project " + id);
    	render(project);
    }
    
    public static void process(@Nonnull Long id) {
    	Project project = Project.findById(id);
    	log.debug("Rendering process for project " + id);
    	render(project);
    }

    public static void story(@Nonnull Long id) {
    	Story story = Story.findById(id);
    	render(story);
    }

}