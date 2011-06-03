package models;
import java.util.ArrayList;
import java.util.List;

public class ResponseMessage {
	private boolean success;
	private List<String> messages;
	
	public ResponseMessage(boolean success) {
		this.success = success;
		this.messages = new ArrayList<String>();
	}
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	public List<String> getMessages() {
		return messages;
	}
	public void addMessage(String message) {
		messages.add(message);
	}
}
