package main;

import solvers.FiniteDifferenceSolver;
import solvers.PoissonSolver;
import solvers.SORSolver;
import solvers.SchrodingerSolver;
import utils.ConvergenceTester;
import utils.Domain;
import utils.Function;
import utils.ParameterReadException;
import utils.WellParameters;

/**
 * @units
 * 	units used are:
 * 		Length - Angstroms
 * 		Energy - ev
 * 		Charge - electron charge = 1
 * @author Mark
 *
 */
public final class Main {

	private static final int 
		MAJOR_VER_NUM = 0,
		MINOR_VER_NUM = 1;
	
	public static final double DEFAULT_TOLERANCE = 1E-5;

	public static void main(String[] args) {
		// ensure that there is at least one input file
		//TODO should warn on extraneous args 
		if (args.length < 1) {
			System.err.println("Error: must specify an input file with the well parameters. Terminating.");
			usage("PSsolver");
			System.exit(1);
			return;
		}

		// read in the parameters that control the problem. Quit on failure
		WellParameters params;
		try {
			params = new WellParameters(args[0]);
		} catch (ParameterReadException e) {
			System.err.println(e.getMessage());
			usage("PSsolver");
			System.exit(2);
			return;
		}
		Domain domain = params.getProblemDomain();
		
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
			PoissonSolver pSolver = new SORSolver(params, rho, electronPotential);
			// implicitly scaled by electron charge, which is 1
			electronPotential = pSolver.solve();
		} while (!convTester.hasConverged());
		
		/* TODO
		 * 1. Parse input for: // delegated to WellParameters class
		 * 		*) units?
		 * 		-a) well profile (layer widths, pot. height)
		 * 		-b) step size
		 * 		-c) effective mass in each layer
		 * 		-d) relative dielectric constants
		 * 		-e) max residual for determining convergence
		 * 2. Solve Schrodinger eqn.
		 * 		a) bdry cond on left and right is psi -> 0
		 * 		b) shoot to middle from each edge (if symmetry is present)
		 * 		c) start by finding highest E state by assuming E ~ V0
		 * 		d) assume highest state is even, check for a higher odd state afterward
		 * 		e) work down to ground state
		 * 3. Solve Poisson's eqn
		 * 		a) get charge density from all bound states (assuming T=0) and bg ion density
		 * 		-b) use SOR or multigrid method (http://www.physics.buffalo.edu/phy410-505/2011/topic3/app1/index.html)
		 * 		    or FFT or Harrison's method to get phi
		 * 4. Update potential
		 * 		-a) turn phi into a potential energy (mult. by electron charge)
		 * 		-b) add phi to the original (not previous!) well potential
		 * 5. Check convergence
		 * 		a) calculate residual in energy eigenvalues
		 * 		b) test based on maximum residual 
		 */
	}
	
	//TODO: java doesn't have progName as arg[0] so this shouldn't take an argument
	public static void usage(String progName) {
		System.out.println("\n"+versionStr);
		System.out.println("Usage: "+progName+" "+usageStr);
	}
	
	//TODO: credit lapack if we end up using the 'dstemr' routine
	private static final String versionStr = 
			"Version: "+MAJOR_VER_NUM+"."+MINOR_VER_NUM+"\n"
			+ "Authors: Mark Sholte, Matthew Butcher\n"
			+ "Date: 17 February 2014";
	
	private static final String usageStr = 
			"<parameter_file>\n"
			+ "\t<parameter_file> should be a plaintext file containing the parameters describing the well in "
			+ "key-value pair format. The parameters should include the widths and bandgaps of each layer in "
			+ "the well, the step size, the effective masses, the relative dielectric constants, and the "
			+ "relative error tolerance to determine when the self-consistent cycle has converged.";

}
