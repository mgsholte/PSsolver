package solvers;

import utils.Function;
import utils.WellParameters;

abstract public class PoissonSolver extends ODESolver {
	
	protected final static double VAC_PERM = .00553; //This is vacuum permittivity in units of (electron charge)/(Volt * Angstrom)
			
	public PoissonSolver(WellParameters params, Function chgDensity) {
		super(params, chgDensity.scale(4 * Math.PI));//.scale(1/VAC_PERM));
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
