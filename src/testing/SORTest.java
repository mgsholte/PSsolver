package testing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import solvers.SORSolver;
import utils.Domain;
import utils.Function;
import utils.LazyFunction;
import utils.WellParameters;

/**
 * Tests the SOR solver for a few different charge distributions: 0, constant,
 * infinite well first excited state wavefunction (squared) All are centered
 * about the y axis. Going to use units where epsilon is 1
 * 
 * @author Matthew
 * 
 */

public class SORTest {

	private Function trivial, constant, well; // charge distribution functions
	private Function solT, solC, solW; // known Poisson solutions to the given distributions
	private Domain d = new Domain(-50, 50, 500);
	private WellParameters params = WellParameters.genDummyParams(d);

	@Before
	public void setUp() throws Exception {
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

	}

	@Test
	public void testSolve() {
		// delete old output files
		String outdir = "tests/";
		new File(outdir + "trivial.m").delete();
		new File(outdir + "const.m").delete();
		new File(outdir + "well.m").delete();

		// setup solvers
		SORSolver.setTolerance(5E-7);
		SORSolver solverT = new SORSolver(params, trivial, Function.getZeroFcn(d));
		SORSolver solverC = new SORSolver(params, constant,Function.getZeroFcn(d));
		SORSolver solverW = new SORSolver(params, well,Function.getZeroFcn(d));

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
