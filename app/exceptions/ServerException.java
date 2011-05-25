package exceptions;

/**
 * An exception representing a server fault.
 * May be caused by a database failure, or
 * a configuration fault of some kind.  
 *  
 * @author Jason Butler
 */
public class ServerException extends Exception {

	private static final long serialVersionUID = 1L;

	public ServerException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServerException(String message) {
		super(message);
	}	
	
}
