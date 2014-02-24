package utils;

public abstract class Function {
	
	public static final Function getZeroFcn(Domain domain) {
		return new LazyFunction(domain) {
			@Override
			public double evalAt(double x) {
				return 0;
			}
		};
	}

	protected final Domain domain;
	
	public Function(Domain domain) {
		this.domain = domain;
	}

	final public Domain getDomain() {
		return domain;
	}
	
	/**
	 * Evaluate the function at the given point
	 * @param x
	 * @return
	 */
	abstract public double evalAt(double x);
	
	/**
	 * Evaluate the function at the given index. That is, at the x value of
	 * the i-th point in the domain. <br><br> 
	 * 
	 * Default implementation gets the value 
	 * in the domain at the given index and passes that to {@link#evalAt(double)}
	 * 
	 * @param i - the index at which to evaluate the function
	 * @return the value of the function, f(x), where x = {@code domain.getValAtIndex(i)}
	 */
	public double evalAtIdx(int i) {
		return evalAt(domain.getValAtIndex(i));
	}
	
	abstract public Function add(Function f) throws DomainMismatchException;
	
	final protected void testDomain(Function f) throws DomainMismatchException {
		if ( !domain.equals(f.domain) )
			throw new DomainMismatchException(domain, f.domain);
	}
	
	/**
	 * 
	 * @param intBnds - the bounds of integration. Step size must be an integer multiple 
	 * 	of the fcn step size
	 * @return the function integrated over the indicated domain
	 */
	public double integrate(Domain intBnds) {
		//TODO: implement
		throw new UnsupportedOperationException("TODO: implement Function.integrate()");
	}

	//TODO: getNormalizingFactor() and normalize() methods need to be added
	
	abstract public Function square();

	public double[] toArray() {
		double[] ans = new double[domain.getNumPoints()];
		int i = 0;
		for (double x : domain)
			ans[i++] = evalAt(x);
		return ans;
	}
	
}
