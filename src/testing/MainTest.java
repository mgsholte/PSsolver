package testing;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import solvers.FiniteDifferenceSolver;
import solvers.PoissonSolver;
import solvers.SORSolver;
import solvers.SchrodingerSolver;
import utils.ConvergenceTester;
import utils.Domain;
import utils.Function;
import utils.WellParameters;

public class MainTest {

	WellParameters params;
	Domain domain;
	
	@Before
	public void setUp() throws Exception {
		
	}

	@Test
	public void test() {		
		// approximation to electron contribution to potential. starts as zero fcn
		Function electronPotential = Function.getZeroFcn(domain);
		Function totalPotential;
		ConvergenceTester convTester = new ConvergenceTester(params.getErrTolerance());
		do {
			// solve schrodingers eqn
			totalPotential = electronPotential.add(params.getBgPotential());
			SchrodingerSolver sSolver = new FiniteDifferenceSolver(params, totalPotential);
			Function[] psis = sSolver.solveSystem(5); // TODO: decide how many states to find
			// get areal chg density
			//TODO: take into account N_i
			Function rho = Function.getZeroFcn(domain);
			for(Function psi : psis) {
				rho = psi.square().add(rho);
			}
			// update eigenvals to test for convergence
			convTester.updateCurValues(sSolver.getEigenvalues());
			// solve poissons eqn
			PoissonSolver pSolver = new SORSolver(params, rho);
			// implicitly scaled by electron charge, which is 1
			electronPotential = pSolver.solve();
		} while (!convTester.hasConverged());
	}

}
