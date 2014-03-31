package testing;

import static org.junit.Assert.*;
import utils.Domain;
import utils.Function;
import utils.GreedyFunction;
import utils.LazyFunction;
import utils.WellParameters;

import org.junit.Before;
import org.junit.Test;

import solvers.SORSolver;

/**
 * Tests the SOR solver for a few different charge distributions:  0, constant, infinite well first excited state wavefunction (squared)
 * All are centered about the y axis.  Going to use units where epsilon is 1
 * @author Matthew
 *
 */

public class SORTest {

	private Function trivial, constant, well;//charge distribution functions
	private Function solT, solC, solW;//known Poisson solutions to the given distributions
	private Domain d = new Domain(-50, 50, 2000);
	private WellParameters params = WellParameters.genDummyParams(d);
	
	@Before
	public void setUp() throws Exception {
		final double L = 50.0;
		trivial = Function.getZeroFcn(d);
		constant = new LazyFunction(d) {
			@Override
			public double evalAt(double x) {
				return -1.0;//for electron charge
			}
		};
		well = new LazyFunction(d){
			@Override
			public double evalAt(double x){
				return -((Math.sqrt(2.0/L)) * Math.sin((Math.PI * x/L))*Math.sin((Math.PI * x/L)));
			}
		};

		solT = Function.getZeroFcn(d);
		solC = new LazyFunction(d){
			@Override
			public double evalAt(double x){
				return .5*(x*x - L*L);
			}
		};
		final double cosCoeff = 1/(2 * Math.PI * Math.PI) * Math.pow(L/2, 1.5);
		final double x2Coeff = 1/(2 * Math.sqrt(2*L));
		solW = new LazyFunction(d){
			@Override
			public double evalAt(double x){
				return cosCoeff * Math.cos(2 * Math.PI * x / L) + x2Coeff * x * x - x2Coeff * L * L - cosCoeff;
			}
		};

	}
	
	
	@Test
	public void testSolve() {
		SORSolver solverT = new SORSolver(params, trivial);
		SORSolver solverC = new SORSolver(params, constant);
		SORSolver solverW = new SORSolver(params, well);
		Function trivialApprox = solverT.solve();
		//assertArrayEquals(solT.toArray(), trivialApprox.toArray(), .05);
		printMatlab(solT, "%*****Trivial*****\n\ntrivialSol = ");
		printMatlab(trivialApprox, "trivialApprox = ");
		
		Function constantApprox = solverC.solve();
		//assertArrayEquals(solC.toArray(), constantApprox.toArray(), .05);
		printMatlab(solC, "%*****Constant*****\n\nconstSol = ");
		printMatlab(constantApprox, "constApprox = ");
		
		Function wellApprox = solverW.solve();
		//assertArrayEquals(solW.toArray(), wellApprox.toArray(), .05);
		printMatlab(solW, "%*****Well*****\n\nwellSol = ");
		printMatlab(wellApprox, "wellApprox = ");
		
	
		
	}

	public static void printMatlab(Function f, String message){
		System.out.print(message);
		System.out.print("[ ");
		for (double x : f.getDomain()){
			if(x != f.getDomain().getValAtIndex(f.getDomain().getNumPoints() - 1))
				System.out.print(f.evalAt(x) + ", ");
			else
				System.out.print(f.evalAt(x));
		}
		System.out.println(" ];\n\n");
		
	}
}
