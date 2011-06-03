package controllers;

import org.apache.log4j.Logger;

import models.ResponseMessage;
import models.State;
import models.Story;
import play.mvc.Controller;

public class Ajax extends Controller {
	
	private final static Logger log = Logger.getLogger(Ajax.class);

	/**
	 * Move a story to a target state, with position index
	 * 
	 * @param storyId
	 * @param StateId
	 * @param index
	 */
	public static void moveStoryToState(Long storyId, Long targetStateId, Integer index) {
		ResponseMessage r = new ResponseMessage(false);
		Story story = Story.findById(storyId);
		State state = State.findById(targetStateId);
		if (story != null && state != null) {
			state.stories.add(story);
			r.setSuccess(state.moveStory(story, index));
		}
		else {
			r.addMessage("Could not find story " + storyId + " or state " + targetStateId);
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
		State state = story.state;
		if (story != null) {
			r.setSuccess(state.moveStory(story, index));
		}
		else {
			r.addMessage("Could not find story " + storyId);
		}
		renderJSON(r);
	}
}
