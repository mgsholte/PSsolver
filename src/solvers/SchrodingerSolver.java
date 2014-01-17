package solvers;

import utils.Function;
import utils.WellParameters;


public abstract class SchrodingerSolver extends ODESolver {

	public static final double KIN_ENGY_COEFF = 1/3.81; // 2*m_e/hbar^2 in units of 1/(ev*A^2)
	
	private double[] eigenvalues;
	
	public SchrodingerSolver(WellParameters params, Function potential) {
		super(params, potential);
	}
	
	public final double[] getEigenvalues() {
		return eigenvalues;
	}
	
}
