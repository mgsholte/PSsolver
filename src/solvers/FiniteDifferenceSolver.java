package solvers;

/***Solves for energies and eigenstates using the finite difference method
 * 
 */
import java.util.Map;

import no.uib.cipr.matrix.DenseVectorSub;
import no.uib.cipr.matrix.SymmTridiagMatrix;
import no.uib.cipr.matrix.sparse.ArpackSym;
import utils.Function;
import utils.GreedyFunction;
import utils.WellParameters;

public class FiniteDifferenceSolver extends SchrodingerSolver {

	public FiniteDifferenceSolver(WellParameters params, Function potential) {
		super(params, potential);
		eigenvalues = new double[params.getProblemDomain().getNumPoints()]; // bigger than needed
	}
	
	@Override
	public Function[] solveSystem() {
		// minimally define Hamiltonian matrix (it is symmetric and tridiagonal)
		double[] diag = potential.toArray();
		final int N = diag.length;
		double[] offDiag = new double[N]; // 1 extra length needed by called library code
		
		// initialize diag and subDiag
		for(int i = 0; i < N; ++i) {
			// diag elems == 2*K_E_C + potential at that point
			diag[i] +=  2*KIN_ENGY_COEFF;
			offDiag[i] = -KIN_ENGY_COEFF;
		}
		
		// solve for the specified number of smallest eigenpairs
		int numEvsDesired = 5;
		Map<Double, DenseVectorSub> eigenpairs = 
				new ArpackSym(new SymmTridiagMatrix(diag, offDiag)).solve(numEvsDesired, ArpackSym.Ritz.SM);
		//
		// recover eigenpairs in a form compatible with the rest of the program
		//
		// eigenvalues 1st
		Double[] tmp_eigvals = (Double[]) eigenpairs.keySet().toArray();
		eigenvalues = new double[tmp_eigvals.length];
		for(int i = 0; i < tmp_eigvals.length; ++i) {
			eigenvalues[i] = tmp_eigvals[i].doubleValue();
		}
		// now eigenvects
		Function[] ans = new GreedyFunction[eigenvalues.length];
		double[] tmp_eigvect = new double[N];
		for(int i = 0; i < ans.length; ++i) {
			// convert DenseVectorSub to double[]
			DenseVectorSub eigvect = eigenpairs.get(eigenvalues[i]);
			for(int j = 0; j < tmp_eigvect.length; ++j) {
				tmp_eigvect[i] = eigvect.get(j);
			}
			// convert double[] to Function and add to solution array
			ans[i] = new GreedyFunction(potential.getDomain(), tmp_eigvect);
		}
		
		return ans;
	}
	
}
