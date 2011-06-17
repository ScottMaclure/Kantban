package controllers;

import javax.annotation.Nonnull;

import models.ResponseMessage;
import models.State;
import models.Story;

import org.apache.log4j.Logger;

import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Project extends Controller {
	
	private final static Logger log = Logger.getLogger(Project.class);
	
	/*
	 * This can be used by any controller that only needs the project 
	 * set in the model. 
	 * @param id the project id
	 */
	private static void genericProject(@Nonnull Long id) {
    	models.Project project = models.Project.findById(id);
    	render(project);
	}

    public static void index(@Nonnull Long id) {
    	genericProject(id);
    }

    public static void board(@Nonnull Long id) {
    	genericProject(id);
    }
    
    public static void process(@Nonnull Long id) {
    	genericProject(id);
    }

    public static void story(@Nonnull Long id) {
    	models.Story story = models.Story.findById(id);
    	models.Project project = models.Project.findById(story.state.project.id);
    	render(project, story);
    }

	public static void moveState(Long stateId, Integer index) {
		log.debug("moveState(" + stateId + ", " + index + ")");
		ResponseMessage r = new ResponseMessage(false);
		State state = State.findById(stateId);
		if (state != null) {
			models.Project project = state.project;
			r.setSuccess(project.moveState(state, index));
			project.save();
		}
		else {
			r.addMessage("Could not find state " + stateId);
		}
		renderJSON(r);
	}
	
	public static void newState(Long projectId, String name, String description, Integer limit) {
		log.debug("newState(" + projectId + ", " + name + ", " + description + ", " + limit + ")");
		ResponseMessage r = new ResponseMessage(false);
		models.Project project = models.Project.findById(projectId);
		if (project != null) {
			project.newState(name, description, limit);
			project.save();
			r.setSuccess(true);
		}
		else {
			r.addMessage("Couldn't find project with id " + projectId);
		}
		renderJSON(r);
	}
	
	public static void deleteState(Long stateId) {
		log.debug("deleteState(" + stateId + ")");
		ResponseMessage r = new ResponseMessage(false);
		State state = State.findById(stateId);
		if (state != null) {
			models.Project project = state.project;
			r.setSuccess(project.removeState(state));
			project.save();
			state.delete();
		}
		else {
			r.addMessage("Could not find state " + stateId);
		}
		renderJSON(r);
	}

}