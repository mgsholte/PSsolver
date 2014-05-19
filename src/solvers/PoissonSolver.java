package solvers;

import utils.Function;
import utils.WellParameters;

abstract public class PoissonSolver extends ODESolver {

	protected static final double eps0 = 1.0; // true val = 0.00552
	
	public PoissonSolver(WellParameters params, Function chgDensity) {
		super(params, chgDensity.scale(1.0/eps0));
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
