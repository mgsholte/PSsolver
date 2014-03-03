package solvers;

/***Solves for energies and eigenstates using the finite difference method
 * 
 */
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;





import main.Main;
import utils.DenseMatrix;
//import no.uib.cipr.matrix.DenseVectorSub;
//import no.uib.cipr.matrix.SymmTridiagMatrix;
//import no.uib.cipr.matrix.sparse.ArpackSym;
import utils.Function;
import utils.Matrix;
import utils.RotationMatrix;
import utils.SparseTridiag;
import utils.GreedyFunction;
import utils.WellParameters;
import utils.Domain;

public class FiniteDifferenceSolver extends SchrodingerSolver {

	private final int dim;
	private final double delX;
	
	public FiniteDifferenceSolver(WellParameters params, Function potential) {
		super(params, potential);
		dim = params.getProblemDomain().getNumPoints();
		delX = params.getProblemDomain().getDx();
		eigenvalues = new double[dim];
	}
	
	public int getDim(){
		return dim;
	}
	
	public Function getBGPotential(){
		return potential;
	}
	
	//Steps:  Using the domain and potential function, generate a finite difference matrix
	//Then, use the QR method to diagonalize, giving the energy eigenvalues.  Then find some
	//predetermined number of lowest energy eigenstates and return an array containing them
	@Override
	public Function[] solveSystem(int numStates) {
		if(numStates > dim)
			throw new IllegalArgumentException("Domain resolution too low to solve for " + numStates + " states.");
		SparseTridiag H = new SparseTridiag(dim, genHOffDiag(),  genHDiag(), genHOffDiag() );
		Matrix eigStates = diagonalize(H, numStates);
		Function[] psis = new Function[numStates];
		for (int i = 0; i < numStates; i++)
			psis[i] = new GreedyFunction(params.getProblemDomain(), eigStates.getCol(dim - 1 - i));
		return psis;
	}
	
	//TODO include effective mass and not just standard electron mass, change these to private
	//Generate Hamiltonian using f''(a) = (f(a + h) - 2(f(a)) + f(a - h))/h^2
	public double[] genHDiag(){
		//hbar^2/2m = 1 / KIN_ENGY_COEFF
		double[] diag = new double[dim];
		for (int i = 0; i < dim; i++)
			diag[i] = 2.0 / (KIN_ENGY_COEFF*delX*delX) + potential.evalAtIdx(i);
		return diag;
	}
	
	public double[] genHOffDiag(){
		double[] offDiag = new double[dim - 1];
		for (int i = 0; i < dim - 1; i++)
			offDiag[i] = (-1.0) / (KIN_ENGY_COEFF*delX*delX);
		return offDiag;
	}
	
	//Takes a matrix A and uses the QR method to diagonalize: check if diagonal, generate series of rotation matrices and 
	//left multiply them repeatedly, updating Q at each step.  At the end do R*Q and loop back again
	//returns matrix containing the eigenvectors
	public Matrix diagonalize(SparseTridiag A, int numStates) {
		int n = A.getDim();
		int numIters = 0;
		Matrix Q = Matrix.getIdentity(n);//Q_temp is reinitialized for each iteration, Q builds the eigenvectors
		DenseMatrix Q_temp = new DenseMatrix(n);
//		while (!A.isDiagonal(Domain.getTolerance())){//TODO:  check if tolerance will work
		while (!isConverged(A, numStates)) {	
			Q_temp = Matrix.getIdentity(n);
			for (int i = 0; i < n - 1; i++){//because we need n-1 rotations
				double b_k1 = A.evalAt(i + 1, i);
				double x_k = A.evalAt(i, i);
				RotationMatrix p_i = new RotationMatrix(n, i, b_k1/Math.sqrt(b_k1*b_k1 + x_k*x_k), x_k/Math.sqrt(b_k1*b_k1 + x_k*x_k));
				//parameters create a rotation matrix that gives A a zero entry directly below the entry at (i,i)
				Q.multiply(p_i.transpose());
				Q_temp.multiply(p_i.transpose());
				A.rotate(p_i);
			}
			A.multiply(Q_temp);
			++numIters;
		}
		double[] rvrseOrderEigs = A.getDiag(); //eigVals going from high to low
		for(int i = 0; i < dim; i++)
			eigenvalues[i] = rvrseOrderEigs[dim - 1 - i];
		// TODO: this should work for getting the eigVals in reverse order
//		eigenvalues = A.getDiag();
//		Collections.reverse(Arrays.asList(eigenvalues));
		return Q;
	}

	private boolean isConverged(SparseTridiag a, int numStates) {
		final double tolerance = 1E-20; 
		boolean ans = true;
		int n = a.getDim();
		for(int i = 0; i < numStates; ++i) {
			double[] col = a.getCol(n-1 - i);
			ans &= (Math.abs(col[n-2-i]) < tolerance); // check superdiag
			col = a.getCol(n-2-i);
			ans &= (Math.abs(col[n-i-1]) < tolerance); // check subdiag
		}
		return ans;
	}

//=======
//	public FiniteDifferenceSolver(WellParameters params, Function potential) {
//		super(params, potential);
//		eigenvalues = new double[params.getProblemDomain().getNumPoints()]; // bigger than needed
//	}
//	
//	@Override
//	public Function[] solveSystem() {
//		// minimally define Hamiltonian matrix (it is symmetric and tridiagonal)
//		double[] diag = potential.toArray();
//		final int N = diag.length;
//		double[] offDiag = new double[N]; // 1 extra length needed by called library code
//		
//		// initialize diag and subDiag
//		for(int i = 0; i < N; ++i) {
//			// diag elems == 2*K_E_C + potential at that point
//			diag[i] +=  2*KIN_ENGY_COEFF;
//			offDiag[i] = -KIN_ENGY_COEFF;
//		}
//		
//		// solve for the specified number of smallest eigenpairs
//		int numEvsDesired = 5;
//		Map<Double, DenseVectorSub> eigenpairs = 
//				new ArpackSym(new SymmTridiagMatrix(diag, offDiag)).solve(numEvsDesired, ArpackSym.Ritz.SM);
//		//
//		// recover eigenpairs in a form compatible with the rest of the program
//		//
//		// eigenvalues 1st
//		Double[] tmp_eigvals = (Double[]) eigenpairs.keySet().toArray();
//		eigenvalues = new double[tmp_eigvals.length];
//		for(int i = 0; i < tmp_eigvals.length; ++i) {
//			eigenvalues[i] = tmp_eigvals[i].doubleValue();
//		}
//		// now eigenvects
//		Function[] ans = new GreedyFunction[eigenvalues.length];
//		double[] tmp_eigvect = new double[N];
//		for(int i = 0; i < ans.length; ++i) {
//			// convert DenseVectorSub to double[]
//			DenseVectorSub eigvect = eigenpairs.get(eigenvalues[i]);
//			for(int j = 0; j < tmp_eigvect.length; ++j) {
//				tmp_eigvect[i] = eigvect.get(j);
//			}
//			// convert double[] to Function and add to solution array
//			ans[i] = new GreedyFunction(potential.getDomain(), tmp_eigvect);
//		}
//		
//		return ans;
//	}
//	
//>>>>>>> 036c8ac7e8cb35e0f769dd097ebb7ccc16869a82
}
