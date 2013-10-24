package function;

public class NotInDomainException extends FunctionException {

	private static final long serialVersionUID = 8550985103762854482L;

	public NotInDomainException(double x) {
		//TODO: provide info on the function that threw the exception
		super("The value "+x+" is not in the domain of f");
	}
	
}
