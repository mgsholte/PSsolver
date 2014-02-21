package testing;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import solvers.FiniteDifferenceSolver;
import solvers.SchrodingerSolver;
import utils.Domain;
import utils.Function;
import utils.GreedyFunction;
import utils.LazyFunction;
import utils.WellParameters;

/**
 * Test Shrodinger solver on the harmonic well potential. if U(x) = 1/2*x^2 and hbar = 1,
 * then the eigenenergies should be simply 1/2, 3/2, 5/2, ... 
 * @author mark
 *
 */
public class FiniteDiffTest {

	Function groundState, harmonicPot;
	
	@Before
	public void setUp() throws Exception {
		//init known solution
		Domain d = new Domain(-250, 250, .5);
		double[] gndStateVals = new double[d.getNumPoints()];
		for(int i = 0; i < d.getNumPoints(); ++i) {
			// TODO: init backing array
			gndStateVals[i] = 0;
		}
		groundState = new GreedyFunction(d, gndStateVals);
		
		// init harmonic potential
		harmonicPot = new LazyFunction(d) {
			@Override
			public double evalAt(double x) {
				return 0.5*x*x;
			}
		};
	}

	@Test
	public void testSolveSystem() {
		SchrodingerSolver solver = 
				new FiniteDifferenceSolver(
						WellParameters.genDummyParams(harmonicPot.getDomain()), 
						harmonicPot);

		Function approxGndState = solver.solveSystem()[0];
		
		// check eigenvalues
		double[] eigvals = solver.getEigenvalues();
		for(int i = 0; i < eigvals.length; ++i) {
			assertEquals(0.5+i, eigvals[i], 0.01);
		}
		
		// check eigenvects
		assertArrayEquals(groundState.toArray(), approxGndState.toArray(), 0.1);
		
	}

}
