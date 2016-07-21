package mxc.demo.campus.exception;

/**
 * Thrown in response to a request for a user (student, lecturer, etc) that does
 * not exist according to the model.
 */
public class NoSuchUserException extends RuntimeException {
	
	private static final long serialVersionUID = 7868132870863697903L;
	
	public NoSuchUserException(String msg) {
		super(msg);
	}
	public NoSuchUserException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
