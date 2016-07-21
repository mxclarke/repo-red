package mxc.demo.campus.exception;

public class ParseException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public ParseException(String msg) {
		super(msg);
	}
	public ParseException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
