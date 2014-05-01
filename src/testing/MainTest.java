package testing;

import java.io.File;
import java.util.Arrays;

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
				else return 5;
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
			psis = sSolver.solveSystem(15); // TODO: decide how many states to find
			// get areal chg density
			//TODO: take into account N_i
			long[] nPerE = fillEnergies(sSolver.getEigenvalues());
			Function rho = genRho(psis, nPerE);
			// update eigenvals to test for convergence
			convTester.updateCurValues(sSolver.getEigenvalues());
			// solve poissons eqn
			PoissonSolver pSolver = new SORSolver(params, rho, electronPotential);
			// implicitly scaled by electron charge, which is 1
			electronPotential = pSolver.solve().offset();
			iters++;
			System.out.println("Iter = " + iters);
			System.out.println("EigVals = " + Arrays.toString(sSolver.getEigenvalues()));
		} while (!convTester.hasConverged() && iters < 30);
		System.out.println("Solution Converged");
		
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
	
	//convenience for testing - will have to fix this for the real version
	public long[] fillEnergies(double[] energies){
		long[] nPerE = new long[energies.length];
		nPerE[0] = (long) ((params.getDofZ() * params.getLx() * params.getLy() * params.getLz())/2);
		nPerE[1] = (long) ((params.getDofZ() * params.getLx() * params.getLy() * params.getLz())/2);
		return nPerE;
	}
	
	//gives the charge density within the sample, scaled by the dielectric constant for Poisson's equation
	public Function genRho(Function[] psis, long[] nPerE){
		double[] rhoVals = new double[domain.getNumPoints()];
		Function psiSum = Function.getZeroFcn(domain);
		for (int i = 0; i < psis.length; i++){
			Function temp = psis[i].square().scale(nPerE[i]);
			psiSum = psiSum.add(temp);
		}
		psiSum = psiSum.scale(1/(params.getLx()*params.getLy()));//scale psi to correspond to a volume density
		for (int i = 0; i < rhoVals.length; i++){
			rhoVals[i] = -(params.getDofZ() - psiSum.evalAtIdx(i))/params.getDielectric().evalAtIdx(i);
		}
		return new GreedyFunction(domain, rhoVals);
	}

}
