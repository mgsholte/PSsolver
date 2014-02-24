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
<<<<<<< HEAD
import utils.Matrix;
import utils.SparseTridiag;
import utils.SparseRMatrix;

public class FiniteDifferenceSolver extends SchrodingerSolver {

	private final int dim;
	private final double delX;
	
	public FiniteDifferenceSolver(WellParameters params, Function potential) {
		super(params, potential);
		dim = params.getProblemDomain().getNumPoints();
		delX = params.getProblemDomain().getDx();
	}
	
	public int getDim(){
		return dim;
	}
	
	//Steps:  Using the domain and potential function, generate a finite difference matrix
	//Then, use the QR method to diagonalize, giving the energy eigenvalues.  Then find some
	//predetermined number of lowest energy eigenstates and return an array containing them
	@Override
	public Function[] solve() {
		int numStates = 5; //number of states to find, can be changed later to be more flexible
		SparseTridiag A0 = new SparseTridiag(dim, genHOffDiag(),  genHDiag(), genHOffDiag() );
		return null;
	}
	
	//TODO include effective mass and not just standard electron mass, change these to private
	//Generate Hamiltonian using f''(a) = (f(a + h) - 2(f(a)) + f(a - h))/h^2
	public double[] genHDiag(){
		//hbar^2/2m = 1 / KIN_ENGY_COEFF
		double[] diag = new double[dim];
		for (int i = 0; i < dim; i++)
			diag[i] = 2.0 / (KIN_ENGY_COEFF*delX*delX) + potential.evalAt(potential.getDomain().getValAtIndex(i));
		return diag;
	}
	
	public double[] genHOffDiag(){
		double[] offDiag = new double[dim - 1];
		for (int i = 0; i < dim - 1; i++)
			offDiag[i] = (-1.0) / (KIN_ENGY_COEFF*delX*delX);
		return offDiag;
	}
	


=======

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
	
>>>>>>> 036c8ac7e8cb35e0f769dd097ebb7ccc16869a82
}
