package solvers;

import utils.ConvergenceTester;
import utils.Function;
import utils.GreedyFunction;
import utils.WellParameters;

public class SORSolver extends PoissonSolver {
	
	protected static double ERR_TOLERANCE = 5E-8;
	
	public static void setTolerance(double tol) {
		ERR_TOLERANCE = tol;
	}

	/** parameter controlling the successive over-relaxation mixing */
	private final double SORParam;

	public SORSolver(WellParameters params, Function chgDensity) {
		super(params, chgDensity);
		SORParam = 2.0/(1.0 + Math.sin(Math.PI*params.getProblemDomain().getDx()));
	}

	/**
	 * TODO: could use {@code param} to scale solution by electric chg, but e = 1 is 
	 * hard-coded currently so there is no reason to do this <br><br>
	 * TODO: could also use {@code param} as a specified error tolerance that overrides
	 * the default. <br><br>
	 * either way, will need to change {@code solve()} method in {@code PoissonSolver}
	 * to make this work properly. also will need to make public rather than protected
	 */
	@Override
	protected Function solve(double param) {
		final int n = potential.getDomain().getNumPoints();
		// use zero-everywhere as initial guess for the electric potential.
		// this has the bonus of correctly initializing the bdry conds
		double[] soln = new double[n];
		ConvergenceTester tester = new ConvergenceTester(ERR_TOLERANCE);
		do {
			tester.initCycle(n);
			// note: don't update end-points since they are fixed bdry conds
			int j; // work from right-to-left simultaneously
			for(int i = 1; i < n/2; ++i) {
				tester.updateValAtIdx(stencil(i, soln), i);
				j = n-i-1;
				tester.updateValAtIdx(stencil(j, soln), j);
			}
			// if n is odd then we still need to update the middle point
			if ((n & 1) == 1) {
				j = n/2; // works b/c int division truncates and array indexing starts at 0
				tester.updateValAtIdx(stencil(j, soln), j);
			}
		} while(!tester.hasConverged());
		
		return new GreedyFunction(potential.getDomain(), soln);
	}

	/**
	 * perform update stencil operation on the array at index {@code i}.
	 * NOTE: mutates {@code vals}
	 * @param i - index at which to perform the update
	 * @param vals - the array which is being iteratively updated
	 * @return the updated value
	 */
	private double stencil(int i, double[] vals) {
		//TODO: missing a negative sign on RHS? or is it taken into account by making q_e positive?
		final double dx = potential.getDomain().getDx(), x = potential.getDomain().getValAtIndex(i), 
				RHS = potential.evalAt(x)/params.getDielectric().evalAt(x);
		//TODO: is this right? Fink & Mathews seems to confirm it is
		vals[i] = (1-SORParam)*vals[i] + SORParam*(vals[i-1] + vals[i+1] + dx*dx*RHS)/2;
		return vals[i];
	}

}
