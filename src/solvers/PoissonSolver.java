package solvers;

import utils.Function;
import utils.WellParameters;

abstract public class PoissonSolver extends ODESolver {

	public PoissonSolver(WellParameters params, Function chgDensity) {
		super(params, chgDensity);
	}

	/**
	 * convenience function for calling {@code solve(double)} without specifying the
	 * (currently) unused double argument
	 * @return the same as {@code solve(1.0)}
	 */
	public Function solve() {
		return solve(1.0);
	}

}
