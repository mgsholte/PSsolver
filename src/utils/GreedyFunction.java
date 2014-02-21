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

	@Override
	public Function add(Function f) {
		testDomain(f);
		double[] sum = new double[vals.length];
		int i = 0;
		for(double x : domain) {
			sum[i] = vals[i++] + f.evalAt(x);
		}
		return new GreedyFunction(domain, sum);
	}
	
	/*
	 * more efficient for greedy functions than the generic add(Function) method
	 */
	public GreedyFunction add(GreedyFunction f) throws DomainMismatchException {
		testDomain(f);
		double[] sum = new double[domain.getNumPoints()];
		for(int i = 0; i < vals.length; ++i)
			sum[i] = vals[i] + f.vals[i];
		return new GreedyFunction(domain, sum);
	}

	@Override
	public Function square() {
		double[] sqrVals = new double[vals.length];
		for (int i = 0; i < vals.length; ++i)
			sqrVals[i] = vals[i]*vals[i];
		return new GreedyFunction(domain, sqrVals);
	}

	@Override
	public double[] toArray() {
		return vals;
	}

}
