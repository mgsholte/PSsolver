package solvers;

import utils.Function;
import utils.WellParameters;

abstract public class PoissonSolver extends ODESolver {
<<<<<<< HEAD
	
	protected final static double VAC_PERM = .00553; //This is vacuum permittivity in units of (electron charge)/(Volt * Angstrom)
			
=======

	protected static final double eps0 = 1.0; // true val = 0.00552
	
>>>>>>> refs/remotes/origin/master
	public PoissonSolver(WellParameters params, Function chgDensity) {
<<<<<<< HEAD
		super(params, chgDensity.scale(4 * Math.PI));//.scale(1/VAC_PERM));
=======
		super(params, chgDensity.scale(1.0/eps0));
>>>>>>> refs/remotes/origin/master
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
