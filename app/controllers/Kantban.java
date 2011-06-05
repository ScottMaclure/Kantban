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
	
	/*
	 * This can be used by any controller that only needs the project 
	 * set in the model. 
	 * @param id the project id
	 */
	private static void genericProject(@Nonnull Long id) {
    	Project project = Project.findById(id);
    	render(project);
	}

    public static void board(@Nonnull Long id) {
    	genericProject(id);
    }
    
    public static void process(@Nonnull Long id) {
    	genericProject(id);
    }

    public static void project(@Nonnull Long id) {
    	genericProject(id);
    }

    public static void story(@Nonnull Long id) {
    	genericProject(id);
    }

}