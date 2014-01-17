package solvers;

import utils.ConvergenceTester;
import utils.Function;
import utils.GreedyFunction;
import utils.WellParameters;

public class SORSolver extends PoissonSolver {
	
	protected static final double ERR_TOLERANCE = 1E-6;

	/** parameter controlling the successive over-relaxation mixing*/
	private final double SORParam;

	public SORSolver(WellParameters params, Function chgDensity) {
		super(params, chgDensity);
		SORParam = 2.0/(1.0 + Math.sin(Math.PI*params.getProblemDomain().getDx()));
	}

	@Override
	public Function[] solve() {
		final int n = potential.getDomain().getNumPoints();
		// use zero-everywhere as initial guess for the electric potential.
		// this has the bonus of correctly initializing the bdry conds
		double[] soln = new double[n];
		double err, maxErr = 0;
		//TODO: use ConvergenceTester class to determine convergence
		//ConvergenceTester tester = new ConvergenceTester(ERR_TOLERANCE);
		
		do {
			// note: don't update end-points since they are fixed bdry conds
			for(int i = 1; i < n/2; ++i) {
				err = stencil(i, soln);
				maxErr = err > maxErr ? err : maxErr;
				
				err = stencil(n-i-1, soln);
				maxErr = err > maxErr ? err : maxErr;
			}
			// if n is odd then we still need to update the middle point
			if ((n & 1) == 1) {
				err = stencil(n/2, soln);
				maxErr = err > maxErr ? err : maxErr;
			}
		} while(maxErr > ERR_TOLERANCE);
		return new Function[] {new GreedyFunction(potential.getDomain(), soln)};
	}

	/**
	 * perform update stencil operation on the array at index {@code i}.
	 * NOTE: mutates {@code vals}
	 * @param i - index at which to perform the update
	 * @param vals - the array which is being iteratively updated
	 * @return the relative difference between the old value and the updated value
	 */
	private double stencil(int i, double[] vals) {
		final double oldVal = vals[i], dx = potential.getDomain().getDx(), x = potential.getDomain().getValAtIndex(i), 
				RHS = potential.evalAt(x)/params.getDielectric().evalAt(x);
		//TODO: is this right? Fink & Mathews seems to confirm it is
		vals[i] = (1-SORParam)*oldVal + SORParam*(vals[i-1] + vals[i+1] + dx*dx*RHS)/2;
		return (vals[i] - oldVal)/vals[i];
	}

}
