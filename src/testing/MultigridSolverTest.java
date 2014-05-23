package testing;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import solvers.MultigridSolver;
import solvers.PoissonSolver;
import utils.Domain;
import utils.Function;
import utils.LazyFunction;
import utils.WellParameters;

public class MultigridSolverTest {
	
	private Domain d = new Domain(-50, 50, 1025);
	private MultigridSolver s1;
	private Function trivial, constant, well; // charge distribution functions
	private Function solT, solC, solW; // known Poisson solutions to the given distributions
	private WellParameters params;

	@Before
	public void setUp() throws Exception {
		params = WellParameters.genDummyParams(d);
		final double L = 50.0;
		trivial = Function.getZeroFcn(d);
		constant = new LazyFunction(d) {
			@Override
			public double evalAt(double x) {
				return -1.0; // for electron charge
			}
		};
		well = new LazyFunction(d) {
			@Override
			public double evalAt(double x) {
				final double tmp = Math.sin((Math.PI * x / L));
				return -Math.sqrt(2.0/L)*tmp*tmp;
			}
		};

		solT = Function.getZeroFcn(d);
		solC = new LazyFunction(d) {
			@Override
			public double evalAt(double x) {
				return .5*(x*x - L*L);
			}
		};
		final double cosCoeff = 1/(2*Math.PI*Math.PI)*Math.pow(L/2, 1.5);
		final double x2Coeff = 1/(2*Math.sqrt(2*L));
		solW = new LazyFunction(d) {
			@Override
			public double evalAt(double x) {
				return cosCoeff*Math.cos(2*Math.PI*x/L) + x2Coeff*x*x
						- x2Coeff*L*L - cosCoeff;
			}
		};
		
		s1 = new MultigridSolver(params, constant);
	}

	@Test
	public void testSolve() {
		// delete old output files
		String outdir = "tests/";
		new File(outdir + "trivial.m").delete();
		new File(outdir + "const.m").delete();
		new File(outdir + "well.m").delete();

		// setup solvers
		PoissonSolver solverT = new MultigridSolver(params, trivial);
		PoissonSolver solverC = new MultigridSolver(params, constant);
		PoissonSolver solverW = new MultigridSolver(params, well);

		// find solutions and print to files for matlab import
		System.out.println("finding trivial soln");
		Function trivialApprox = solverT.solve();
		System.out.println("soln found; printing");
		// assertArrayEquals(solT.toArray(), trivialApprox.toArray(), .05);
		printMatlab(solT, "trivialSol = ", "trivial.m");
		printMatlab(trivialApprox, "trivialApprox = ", "trivial.m");

		System.out.println("finding const soln");
		Function constantApprox = solverC.solve();
		System.out.println("soln found; printing");
		// assertArrayEquals(solC.toArray(), constantApprox.toArray(), .05);
		printMatlab(solC, "constSol = ", "const.m");
		printMatlab(constantApprox, "constApprox = ", "const.m");

		System.out.println("finding well soln");
		Function wellApprox = solverW.solve();
		System.out.println("soln found; printing");
		// assertArrayEquals(solW.toArray(), wellApprox.toArray(), .05);
		printMatlab(solW, "wellSol = ", "well.m");
		printMatlab(wellApprox, "wellApprox = ", "well.m");
	}

	@Test
	public void testLog2() {
		if(MultigridSolver.log2(32) != 5)
			fail("Should return log2(32) = 5");
		if(MultigridSolver.log2(33) != 5)
			fail("Should return log2(33) = 5");
	}

	@Test
	public void testGaussSeidel() {
		double[] zeros = new double[d.getNumPoints()];
		for(int i = 0; i < 100; i++)
			 zeros = s1.gaussSeidel(d.getDx(), zeros);
		System.out.println(Arrays.toString(zeros));
			
	}
	
	@Test
	public void testInterpolate() {
		double[] onesCoarse = {0, 1, 1, 1, 0};
		double[] onesFine = s1.interpolate(onesCoarse, .1, 4);
		double[] onesFineTrue = {0,.5,1,1,1,1,1,.5,0};
		System.out.println("onesFine = " + Arrays.toString(onesFine) + ", onesTrue = " + Arrays.toString(onesFineTrue));
	}
	
	@Test
	public void testRestrict() {
		double[] onesFine = {0,1,1,1,1,1,1,1,0};
		double[] onesCoarse = s1.restrict(onesFine, .1, 8);
		double[] onesCoarseTrue = {0, 1, 1, 1, 0};

		System.out.println("onesCoarse = " + Arrays.toString(onesCoarse) + ", onesTrue = " + Arrays.toString(onesCoarseTrue));
	}
	
	public static void printMatlab(Function f, String message, String fileName) {
		try {
			BufferedWriter file = new BufferedWriter(
					new FileWriter("tests/" + fileName, true)
					);
			file.write(message);
			file.write(Arrays.toString(f.toArray()));
			file.write(";\n");
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
