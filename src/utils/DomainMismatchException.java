package utils;

public class DomainMismatchException extends IllegalArgumentException {

	/** auto-generated */
	private static final long serialVersionUID = 6432957432278022540L;

	public DomainMismatchException(Domain d1, Domain d2) {
		super("To add two functions together, they must have the same domain.\nThe domains were: 1) "+d1+", 2) "+d2);
	}
	
}
