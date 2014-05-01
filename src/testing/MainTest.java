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
		bgPotential = new LazyFunction(domain) {
			@Override
			public double evalAt(double x){
				if (x > -50.0 && x < 50.0)
					return 0;
				else 
					return 0.16;
			};
		};
		params = WellParameters.genDummyParams(domain);
		SORSolver.setTolerance(1E-6);
	}

	@Test
	public void test() {		
		// approximation to electron contribution to potential. starts as zero fcn
		Function electronPotential = Function.getZeroFcn(domain);
		Function totalPotential;
		ConvergenceTester convTester = new ConvergenceTester(params.getErrTolerance());
		Function rho;
		int iters = 0;
		do {
			// solve schrodingers eqn
			totalPotential = electronPotential.add(bgPotential);
			SchrodingerSolver sSolver = new FiniteDifferenceSolver(params, totalPotential);
			psis = sSolver.solveSystem(15); // TODO: decide how many states to find
			// get areal chg density
			//TODO: take into account N_i
			double[] nPerE = fillEnergies(sSolver.getEigenvalues());
			rho = genRho(psis, nPerE);
			// update eigenvals to test for convergence
			convTester.updateCurValues(sSolver.getEigenvalues());
			// solve poissons eqn
			if(iters == 0) {
				final double curv = 0.5*rho.evalAt(0)/params.getDielectric().evalAt(0);
				electronPotential = new LazyFunction(domain) {
					final double L2 = params.getWidths()[1]/2;
					@Override
					public double evalAt(double x) {
						return (x > -50 && x < 50) ?
								curv*(x - L2)*(x + L2) - params.getDofZ().evalAt(x)*L2 :
								0;
					};
				};
				SORTest.printMatlab(electronPotential, "initGuess =", "initGuess.m");
//				electronPotential.offset();
			}
			PoissonSolver pSolver = new SORSolver(params, rho, electronPotential.negate());
			// implicitly scaled by electron charge, which is 1
			electronPotential = pSolver.solve().negate();
			iters++;
			System.out.println("Iter = " + iters);
			System.out.println("EigVals = " + Arrays.toString(sSolver.getEigenvalues()));
		} while (!convTester.hasConverged() && iters < 30);
		System.out.println( (iters<30) ? "Solution Converged" : "Solution failed to converge in 30 iterations");
		
		double[] domainArr = new double[domain.getNumPoints()];
		for(int i = 0; i < domainArr.length; i++)
			domainArr[i] = i * domain.getDx();
		Function domainFunc = new GreedyFunction(domain, domainArr);
		String outfile = "MainOutputs.m";
		new File("tests/"+outfile).delete();
		SORTest.printMatlab(domainFunc, "x = ", outfile);
		SORTest.printMatlab(electronPotential, "elecPot = ", outfile);
		SORTest.printMatlab(totalPotential, "totalPot =", outfile);
		SORTest.printMatlab(rho, "rho =", outfile);
		for(int i = 0; i < 3; i++)
			SORTest.printMatlab(psis[i], "psi" + i + " = ", outfile);
		
	}
	
	//convenience for testing - will have to fix this for the real version
	public double[] fillEnergies(double[] energies){
		double[] nPerE = new double[energies.length];
		nPerE[0] = (params.getDofZ().evalAt(0) * params.getLz()/5)/2;
		nPerE[1] = (params.getDofZ().evalAt(0) * params.getLz()/5)/2;
		return nPerE;
	}
	
	//gives the charge density within the sample, scaled by the dielectric constant for Poisson's equation
	public Function genRho(Function[] psis, double[] nPerE){
		double[] rhoVals = new double[domain.getNumPoints()];
		Function psiSum = Function.getZeroFcn(domain);
		for (int i = 0; i < psis.length; i++){
			Function temp = psis[i].square().scale(nPerE[i]);
			psiSum = psiSum.add(temp);
		}
		psiSum = psiSum.scale(1/(params.getLx()*params.getLy()));//scale psi to correspond to a volume density
		for (int i = 0; i < rhoVals.length; i++){
			rhoVals[i] =   psiSum.evalAtIdx(i) - params.getDofZ().evalAtIdx(i);
		}
		return new GreedyFunction(domain, rhoVals);
	}

}
