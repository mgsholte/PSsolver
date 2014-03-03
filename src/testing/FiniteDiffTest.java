package testing;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import solvers.FiniteDifferenceSolver;
import solvers.SchrodingerSolver;
import utils.Domain;
import utils.Function;
import utils.LazyFunction;
import utils.WellParameters;

/**
 * Test Shrodinger solver on the harmonic well potential. if U(x) = 1/2*x^2 and hbar = 1,
 * then the eigenenergies should be simply 1/2, 3/2, 5/2, ... 
 * @author mark
 *
 */
public class FiniteDiffTest {

	Function trueGndState, harmonicPot;
	
	@Before
	public void setUp() throws Exception {
		//init known solution
		Domain d = new Domain(-50, 50, 100); //TODO: test on larger domain
		trueGndState = new LazyFunction(d) {
			//normalized GS wvfcn is f(x) = (1/pi)^(0.25)*e^(-0.5*x^2)
			private final double coeff = Math.pow(1.0/Math.PI, 0.25);
			@Override
			public double evalAt(double x) {
				return coeff*Math.exp(-0.5*x*x);
			}
		};
		
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

		Function approxGndState = solver.solveSystem(6)[0];
		
		// check eigenvalues
		double[] eigvals = solver.getEigenvalues();
		for(int i = 0; i < 6; ++i) {
			assertEquals(0.5+i, eigvals[i], 0.05);
		}
		
		// check eigenvects
		assertArrayEquals(trueGndState.toArray(), approxGndState.toArray(), 0.1);
	}

}
