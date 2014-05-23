package solvers;

import utils.Function;
import utils.WellParameters;

abstract public class PoissonSolver extends ODESolver {

	protected static final double eps0 = 0.00552; // true val = 0.00552
	protected static double ERR_TOLERANCE = 1E-7;
	
	protected final Function RHS;
	
	public static void setTolerance(double tol) {
		ERR_TOLERANCE = tol;
	}

	public PoissonSolver(WellParameters params, Function chgDensity) {
		super(params, chgDensity);
		RHS = chgDensity.divide(params.getDielectric());
	}

	/**
	 * convenience function for calling {@code solve(double)} without specifying the
	 * (currently) unused double argument
	 * @return the same as {@code solve(1.0)}
	 */
	public Function solve() {
		return solve(Double.NaN);
	}

}
