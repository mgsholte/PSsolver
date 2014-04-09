package testing;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import solvers.FiniteDifferenceSolver;
import solvers.PoissonSolver;
import solvers.SORSolver;
import solvers.SchrodingerSolver;
import utils.ConvergenceTester;
import utils.Domain;
import utils.Function;
import utils.LazyFunction;
import utils.GreedyFunction;
import utils.WellParameters;

public class MainTest {

	WellParameters params;
	Domain domain;
	Function bgPotential;
	Function[] psis;
	
	@Before
	public void setUp() throws Exception {
		domain = new Domain(-250.0, 250.0, 1000);
		bgPotential = new LazyFunction(domain){
			@Override
			public double evalAt(double x){
				if (x < 50.0 && x > -50.0)
					return 0;
				else return 1;
			};
		};
		params = WellParameters.genDummyParams(domain);
		
	}

	@Test
	public void test() {		
		// approximation to electron contribution to potential. starts as zero fcn
		Function electronPotential = Function.getZeroFcn(domain);
		Function totalPotential;
		ConvergenceTester convTester = new ConvergenceTester(params.getErrTolerance());
		int iters = 0;
		do {
			// solve schrodingers eqn
			totalPotential = electronPotential.add(bgPotential).offset();
			SchrodingerSolver sSolver = new FiniteDifferenceSolver(params, totalPotential);
			psis = sSolver.solveSystem(5); // TODO: decide how many states to find
			// get areal chg density
			//TODO: take into account N_i
			Function rho = Function.getZeroFcn(domain);
			for(Function psi : psis) {
				rho = psi.square().add(rho);
			}
			// update eigenvals to test for convergence
			convTester.updateCurValues(sSolver.getEigenvalues());
			// solve poissons eqn
			PoissonSolver pSolver = new SORSolver(params, rho, electronPotential);
			// implicitly scaled by electron charge, which is 1
			electronPotential = pSolver.solve();
			iters++;
			System.out.println("Iter = " + iters);
		} while (!convTester.hasConverged() && iters < 100);
		System.out.println("hi");
		
		double[] domainArr = new double[domain.getNumPoints()];
		for(int i = 0; i < domainArr.length; i++)
			domainArr[i] = i * domain.getDx();
		Function domainFunc = new GreedyFunction(domain, domainArr);
		new File("MainOutputs.m").delete();
		SORTest.printMatlab(domainFunc, "domain = ", "MainOutputs.m");
		SORTest.printMatlab(electronPotential, "elecPot = ", "MainOutputs.m");
		SORTest.printMatlab(totalPotential, "totalPot =", "MainOutputs.m");
		for(int i = 0; i < psis.length; i++)
			SORTest.printMatlab(psis[i], "psi" + i + " = ", "MainOutputs.m");
		
	}

}
