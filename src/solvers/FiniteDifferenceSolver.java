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
	public Function[] solveSystem(int numStates) {
		// minimally define Hamiltonian matrix (it is symmetric and tridiagonal)
		double[] diag = potential.toArray();
		final int N = diag.length;
		double[] offDiag = new double[N-1];
		
		//TODO: initialize diag and subDiag by using LazyFunctions and calling toArray()?
		// initialize diag and subDiag
		for(int i = 0; i < N-1; ++i) {
			// diag elems == 2*K_E_C + potential at that point
			diag[i] += 2*kinEngyCoeff/params.getMass().evalAtIdx(i);
			offDiag[i] = -kinEngyCoeff/params.getMass().evalAtIdx(i);
		}
		diag[N-1] += 2*kinEngyCoeff/params.getMass().evalAtIdx(N - 1); // diag has 1 more element than offDiag
		
		// solve for the specified number of smallest eigenpairs
		Map<Double, DenseVectorSub> eigenpairs =
				new ArpackSym(new SymmTridiagMatrix(diag, offDiag)).solve(numStates, ArpackSym.Ritz.SM);
		//
		// recover eigenpairs in a form compatible with the rest of the program
		//
		// eigenvalues 1st
		Double[] tmp_eigvals = eigenpairs.keySet().toArray(new Double[0]);
		if (tmp_eigvals.length != numStates) {
			System.err.println("MTJ solver somehow didn't find the correct number of states");
			return null;
		}
		eigenvalues = new double[numStates];
		for(int i = 0; i < numStates; ++i) {
			// eigvals reported by library call are in descending order
			eigenvalues[i] = tmp_eigvals[numStates-1-i].doubleValue();
		}
		// now eigenvects
		Function[] ans = new GreedyFunction[eigenvalues.length];
		double[] tmp_eigvect = new double[N];
		for(int i = 0; i < ans.length; ++i) {
			// convert DenseVectorSub to double[]
			DenseVectorSub eigvect = eigenpairs.get(eigenvalues[i]);
			for(int j = 0; j < tmp_eigvect.length; ++j) {
				tmp_eigvect[j] = eigvect.get(j);
			}
			// convert double[] to Function and add to solution array
			ans[i] = new GreedyFunction(potential.getDomain(), tmp_eigvect.clone()).normalize();
		}

		return ans;
	}
	
}
