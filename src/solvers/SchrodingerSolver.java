package solvers;

import utils.Function;
import utils.WellParameters;


public abstract class SchrodingerSolver extends ODESolver {

	private static final double HBAR_SQ_2_M = 3.806; // hbar^2/(2*m_e) in units of ev*A^2 **APPROXIMATE TRUE VALUE** = 3.806
	
	protected final double kinEngyCoeff;
	
	//TODO: there is probably a better way to handle the eigenvals
	//TODO: initialize when used since we don't know how many evs will be found a priori
	protected double[] eigenvalues;
	
	public SchrodingerSolver(WellParameters params, Function potential) {
		super(params, potential);
		final double dxInv = 1.0/params.getProblemDomain().getDx();
		kinEngyCoeff = HBAR_SQ_2_M*dxInv*dxInv;
	}
	
	abstract public Function[] solveSystem(int numStates);
	
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
