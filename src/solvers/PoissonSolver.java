package solvers;

import utils.Function;
import utils.WellParameters;

abstract public class PoissonSolver extends ODESolver {

	protected static final double ERR_TOLERANCE = 1E-6;
	
	public PoissonSolver(WellParameters params, Function chgDensity) {
		super(params, chgDensity);
	}

}
