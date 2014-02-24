package solvers;

import utils.Function;
import utils.WellParameters;

public abstract class ODESolver {

	protected final WellParameters params;
	protected final Function potential;

	public ODESolver(WellParameters params, Function potential) {
		this.params = params; this.potential = potential;
	}

	/**
	 * Solve the ODE
	 * @return the solution
	 */
	protected abstract Function solve(double param);
	
}
