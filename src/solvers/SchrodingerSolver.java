package solvers;

import utils.Function;
import utils.WellParameters;


public abstract class SchrodingerSolver extends ODESolver {

	public static final double KIN_ENGY_COEFF = 1/3.81; // 2*m_e/hbar^2 in units of 1/(ev*A^2)
	//TODO: move this somewhere else?
	public static final double HBAR = 6.582E-16; // in eV*s
	public static final double DEL_E = .005; // in eV
	
	//TODO: there is probably a better way to handle the eigenvals
	//TODO: initialize when used since we don't know how many evs will be found a priori
	protected double[] eigenvalues;
	
	public SchrodingerSolver(WellParameters params, Function potential) {
		super(params, potential);
	}
	
	abstract public Function[] solveSystem();
	
	/**
	 * default, do-nothing implementation
	 */
	@Override
	protected Function solve(double param) {
		return null;
	}
	
	public final double[] getEigenvalues() {
		return eigenvalues;
	}
	
}
