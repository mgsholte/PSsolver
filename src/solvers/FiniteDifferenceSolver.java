package solvers;

/***Solves for energies and eigenstates using the finite difference method
 * 
 */
import utils.Function;
import utils.WellParameters;
import utils.Matrix;

public class FiniteDifferenceSolver extends SchrodingerSolver {

	
	public FiniteDifferenceSolver(WellParameters params, Function potential) {
		super(params, potential);
	}
	
	//Generate Hamiltonian using f''(a) = (f(a + h) - 2(f(a)) + f(a - h))/h^2
	private Matrix genH(WellParameters well, Function V){
		
		return null;
	}
	
	@Override
	public Function[] solve() {
		// TODO Auto-generated method stub
		return null;
	}

}
