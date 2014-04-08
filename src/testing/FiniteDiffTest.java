package testing;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

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
	
	Function testWell1, testWell2;
	
	@Before
	public void setUp() throws Exception {
		//init known solution
		Domain d = new Domain(-50, 50, 1000); //TODO: test on larger domain
		Domain d1 = new Domain(-250, 250, 5000);
		trueGndState = new LazyFunction(d) {
			//normalized GS wvfcn is f(x) = (1/pi)^(0.25)*e^(-0.5*x^2)
			private final double coeff = Math.pow(1.0/Math.PI, 0.25);
			@Override
			public double evalAt(double x) {
				return coeff*Math.exp(-0.5*x*x);
			}
		};
		
		double norm = 0, tmpVal;
		for(double x : trueGndState.getDomain()) {
			tmpVal = trueGndState.evalAt(x);
			norm += tmpVal*tmpVal*d.getDx();
		}
		norm = 1.0/norm;
		trueGndState.scale(norm);
		
		// init harmonic potential
		harmonicPot = new LazyFunction(d) {
			@Override
			public double evalAt(double x) {
				return 0.5*x*x;
			}
		};
		
		//initiate finite well #1
		testWell1 = new LazyFunction(d){
			@Override
			public double evalAt(double x){
				if (x < 20.0 && x > -20.0)
					return 0.0;
				else return 2.0;
			}
		};
		
		//Real size finite well
		testWell2 = new LazyFunction(d1){
			@Override
			public double evalAt(double x){
				if (x < 50.0 && x > -50.0)
					return 0.0;
				else return 2.0;
			}
		};
	}

	@Test
	public void testSolveSystem() {
		Domain d = harmonicPot.getDomain();
		SchrodingerSolver solver = 
				new FiniteDifferenceSolver(
						WellParameters.genDummyParams(d), 
						harmonicPot);

		Function approxGndState = solver.solveSystem(5)[0];
		// solution comes out normalized, but might be upside down.
		// check by looking at a point right before the middle of the well
		if( approxGndState.evalAtIdx(harmonicPot.getDomain().getNumPoints()/2 - 1) < 0.0 ) {
			approxGndState.scale(-1.0); 
		}
		assertEquals(1.0, approxGndState.getNormalizingFactor(), 0.01);
		
		// check eigenvalues
		double[] eigvals = solver.getEigenvalues();
		for(int i = 0; i < 5; ++i) {
			assertEquals(0.5+i, eigvals[i], 0.05);
		}
		
		// print eigvect for plotting in matlab
		try {
			BufferedWriter file = new BufferedWriter(new FileWriter("tests/FiniteDiffTest.m"));
			file.write("% "+d.toString()+"\n");
			file.write("psi0 = ");
			file.write(Arrays.toString(approxGndState.toArray()));
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// check eigenvects
		assertArrayEquals(trueGndState.toArray(), approxGndState.toArray(), 0.05);
	}
	
	@Test
	public void examples(){
		Domain d1 = testWell1.getDomain();
		Domain d2 = testWell2.getDomain();
		SchrodingerSolver s1 = new FiniteDifferenceSolver(WellParameters.genDummyParams(d1), testWell1);
		SchrodingerSolver s2 = new FiniteDifferenceSolver(WellParameters.genDummyParams(d2), testWell2);
		
		
		Function[] psisWell1, psisWell2;
		System.out.println("testWell1 solving...");
		psisWell1 = s1.solveSystem(3);
		System.out.println("testWell2 ground state solving...");
		psisWell2 = s2.solveSystem(3);
		
		
		System.out.println("Printing results of examples");
		for(int i = 0; i < 3; i++){
			SORTest.printMatlab(psisWell1[i], "well1Psi" + i + " = ", "FiniteDiffTest.m");
			SORTest.printMatlab(psisWell2[i], "well2Psi" + i + " = ", "FiniteDiffTest.m");
		}
	}

}
