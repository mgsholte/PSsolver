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
	
	final protected void testDomain(Function f) throws DomainMismatchException {
		if ( !domain.equals(f.domain) )
			throw new DomainMismatchException(domain, f.domain);
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
	
	abstract public Function scale(double factor);
	
	abstract public Function negate();
	
	abstract public Function add(Function f) throws DomainMismatchException;
	
	abstract public Function square();
	
	/**
	 * Scaling the function by this number will make it normalized in
	 * the usual sense of square integrable functions.
	 * 
	 * @return 1/norm({@code this})
	 */
	abstract public double getNormalizingFactor();
	
	public Function normalize() {
		return scale(getNormalizingFactor());
	}

	public double[] toArray() {
		double[] ans = new double[domain.getNumPoints()];
		int i = 0;
		for (double x : domain)
			ans[i++] = evalAt(x);
		return ans;
	}

	abstract public Function offset();
	
	public Function divide(Function f){
		double[] vals = new double[domain.getNumPoints()];
		for(int i = 0; i < vals.length; i++){
			vals[i] = this.evalAtIdx(i)/f.evalAtIdx(i);
		}
		return new GreedyFunction(domain, vals);
	}
	
	public Function multiply(Function f){
		double[] vals = new double[domain.getNumPoints()];
		for(int i = 0; i < vals.length; i++){
			vals[i] = this.evalAtIdx(i)*f.evalAtIdx(i);
		}
		return new GreedyFunction(domain, vals);
	}
	
	public double integrate(double lb, double ub){
		if (!domain.contains(lb) || !domain.contains(ub))
			throw new IllegalArgumentException("Cannot integrate on the given interval - bound(s) outside domain");
		double result = 0;
		for(int i = domain.getIndexOf(lb); i < domain.getIndexOf(ub); i++)
			result += evalAtIdx(i) * domain.getDx();
		return result;
	}


}
