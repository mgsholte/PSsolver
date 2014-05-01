/**
 * 
 */
package utils;


/**
 * @author Mark
 *
 */
public class GreedyFunction extends Function {

	final double[] vals;
	
	public GreedyFunction(Domain domain) {
		super(domain);
		vals = new double[domain.getNumPoints()];
	}
	
	public GreedyFunction(Domain domain, double[] fvals) {
		super(domain);
		vals = fvals;
		//TODO: make deep copy?
		// vals = fvals.clone();
	}

	@Override
	public double evalAt(double x) {
		return vals[domain.getIndexOf(x)];
	}

	@Override
	public double evalAtIdx(int i) {
		return vals[i];
	}
	
	/**
	 * in-place mutation of backing array
	 */
	@Override
	public Function scale(double factor) {
		for(int i = 0; i < vals.length; ++i)
			vals[i] *= factor;
		return this;
	}

	@Override
	public Function add(Function f) {
		testDomain(f);
		double[] sum = new double[vals.length];
		for(int i = 0; i < vals.length; i++) {
			sum[i] = vals[i] + f.evalAtIdx(i);
		}
		return new GreedyFunction(domain, sum);
	}
	
	/*
	 * more efficient for greedy function arguments than the generic add(Function) method
	 */
	public GreedyFunction add(GreedyFunction f) throws DomainMismatchException {
		testDomain(f);
		double[] sum = new double[domain.getNumPoints()];
		for(int i = 0; i < vals.length; ++i)
			sum[i] = vals[i] + f.vals[i];
		return new GreedyFunction(domain, sum);
	}

	//TODO: make in-place
	@Override
	public Function square() {
		double[] sqrVals = new double[vals.length];
		for (int i = 0; i < vals.length; ++i)
			sqrVals[i] = vals[i]*vals[i];
		return new GreedyFunction(domain, sqrVals);
	}
	
	@Override
	public double getNormalizingFactor() {
		final double dx = domain.getDx();
		final int n = vals.length;
		double norm = 0.0, tmp;
		// iterate from front and back simultaneously since the biggest values are
		// in the middle and should be added last to reduce floating point error
		for(int i = 0; i < n/2; ++i) {
			tmp = vals[i];
			norm += tmp*tmp*dx;
			tmp = vals[n-1-i];
			norm += tmp*tmp*dx;
		}
		// if n is odd then we still need to update the middle point
		if ((n & 1) == 1) {
			tmp = vals[n/2]; // works b/c int division truncates and array indexing starts at 0
			norm += tmp*tmp*dx;
		}
		return 1.0/Math.sqrt(norm);
	}

	@Override
	public double[] toArray() {
		return vals.clone();
	}
	
	//This takes the function and translates it so that its minimum value is 0 - used to reset the potential function
	//assumes the function is >= 0 for its whole domain
	@Override
	public Function offset(){
		double min = vals[0];
		for(int i = 0; i < vals.length; i++){
			if (this.evalAtIdx(i) < min)
				min = this.evalAtIdx(i);
		}
		double[] newVals = new double[vals.length];
		for(int i = 0; i < vals.length; i++)
			newVals[i] = vals[i] - min;
		return new GreedyFunction(domain, newVals);
	}

	@Override
	public Function negate() {
		double[] negVals = new double[vals.length];
		for (int i = 0; i < vals.length; ++i) {
			negVals[i] = -vals[i];
		}
		return new GreedyFunction(domain, negVals);
	}

}
