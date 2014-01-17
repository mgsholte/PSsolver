package solvers;

import utils.Function;
import utils.WellParameters;

abstract public class PoissonSolver extends ODESolver {

	public PoissonSolver(WellParameters params, Function chgDensity) {
		super(params, chgDensity);
	}

}
