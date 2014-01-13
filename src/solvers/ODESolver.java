package solvers;

import utils.Domain;
import utils.Function;
import utils.WellParameters;

public abstract class ODESolver {

	protected final WellParameters params;
	protected final Function potential;
	protected final Domain domain;

	public ODESolver(WellParameters params, Function potential) {
		this.params = params; this.potential = potential;
		domain = params.getProblemDomain();
	}

	/**
	 * Solve the ODE
	 * @return the solutions 
	 */
	abstract public Function[] solve();

}
