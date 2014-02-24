package solvers;

/***Solves for energies and eigenstates using the finite difference method
 * 
 */
import utils.Function;
import utils.Matrix;
import utils.RotationMatrix;
import utils.SparseTridiag;
import utils.WellParameters;

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
	public Function[] solveSystem() {
		int numStates = 5; //number of states to find, can be changed later to be more flexible
		SparseTridiag H = new SparseTridiag(dim, genHOffDiag(),  genHDiag(), genHOffDiag() );
		Matrix eigStates = diagonalize(H);
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
	
	//Takes a matrix A and uses the QR method to diagonalize: check if diagonal, generate series of rotation matrices and 
	//left multiply them repeatedly, updating Q at each step.  At the end do R*Q and loop back again
	//returns matrix containing the eigenvectors
	public Matrix diagonalize(Matrix A) {
		int n = A.getDim();
		Matrix Q = Matrix.getIdentity(n);
		while (!A.isDiagonal(.001)){//Domain.getTolerance())){//TODO:  check if tolerance will work
			Q = Matrix.getIdentity(n);
			for (int i = 0; i < n - 1; i++){//because we need n-1 rotations
				double b_k1 = A.evalAt(i + 1, i);
				double x_k = A.evalAt(i, i);
				RotationMatrix p_i = new RotationMatrix(n, i, b_k1/Math.sqrt(b_k1*b_k1 + x_k*x_k), x_k/Math.sqrt(b_k1*b_k1 + x_k*x_k));
				//parameters create a rotation matrix that gives A a zero entry directly below the entry at (i,i)
				Q.multiply(p_i.transpose());
				A.rotate(p_i);
			}
			A.multiply(Q);
		}
		return Q;
	}

}
