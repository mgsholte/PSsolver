package utils;

public class ParameterReadException extends Exception {

	/// auto-generated
	private static final long serialVersionUID = -8429611035502294126L;

	public ParameterReadException(String errorMsg) {
		super(errorMsg);
	}
	
	public ParameterReadException(String errorMsg, Throwable cause) {
		this(errorMsg);
		initCause(cause);
	}
	
}
