package controllers;

import javax.persistence.PostPersist;

import models.ResponseMessage;
import models.Story;

import org.apache.log4j.Logger;

import play.mvc.Controller;

public class State extends Controller {
	
	private final static Logger log = Logger.getLogger(State.class);

	/**
	 * Move a story to a target state, with position index
	 * 
	 * @param storyId
	 * @param StateId
	 * @param index
	 */
	public static void moveStoryToState(Long storyId, Long stateId, Integer index) {
		log.debug("moveStoryToState(" + storyId + ", " + stateId + ", " + index + ")");
		ResponseMessage r = new ResponseMessage(false);
		Story story = Story.findById(storyId);
		models.State state = models.State.findById(stateId);
		if (story != null && state != null) {
			state.stories.add(story);
			r.setSuccess(state.moveStory(story, index));
			state.save();
		}
		else {
			r.addMessage("Could not find story " + storyId + " or state " + stateId);
		}
		renderJSON(r);
	}

	/**
	 * Move a story in the state order to position index
	 * 
	 * @param storyId
	 * @param StateId
	 * @param index
	 */
	public static void moveStory(Long storyId, Integer index) {
		log.debug("moveStory(" + storyId + ", " + index + ")");
		ResponseMessage r = new ResponseMessage(false);
		Story story = Story.findById(storyId);
		if (story != null) {
			models.State state = story.getState();
			r.setSuccess(state.moveStory(story, index));
			state.save();
		}
		else {
			r.addMessage("Could not find story " + storyId);
		}
		renderJSON(r);
	}
	
}
