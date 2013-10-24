package function;

public class DomainMismatchException extends FunctionException {

	private static final long serialVersionUID = -8250481087182627955L;

	public DomainMismatchException() {
		super("Cannot perform operations on functions with different domains");
	}


}
