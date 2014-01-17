package main;

import solvers.ODESolver;
import solvers.PoissonSolver;
import solvers.SORSolver;
import solvers.SchrodingerSolver;
import solvers.ShootingSolver;
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
	
	public static final double DEFAULT_TOLERANCE = 1E-6;

	public static void main(String[] args) {
		// ensure that there is at least one input file
		//TODO should warn on extraneous args 
		if (args.length < 2) {
			System.err.println("Error: must specify an input file with the well parameters. Terminating.");
			usage(args[0]);
			System.exit(1);
			return;
		}

		// read in the parameters that control the problem. Quit on failure
		WellParameters params;
		try {
			params = new WellParameters(args[1]);
		} catch (ParameterReadException e) {
			System.err.println(e.getMessage());
			usage(args[0]);
			System.exit(2);
			return;
		}
		Domain domain = params.getProblemDomain();
		
		// approximation to electron contribution to potential. starts as zero fcn
		Function electronPotential = Function.getZeroFcn(domain);
		Function totalPotential;
		ConvergenceTester convTester = new ConvergenceTester(params.getErrTolerance());
		//TODO: set condition to break based on error tolerance
		do {
			// solve schrodingers eqn
			totalPotential = electronPotential.add(params.getBgPotential());
			ODESolver solver = new ShootingSolver(params, totalPotential);
			Function[] psis = solver.solve();
			// get areal chg density
			Function rho = Function.getZeroFcn(domain);
			for(Function psi : psis) {
				rho = psi.square().add(rho);
			}
			// update eigenvals to test for convergence
			convTester.updateCurValues( ((SchrodingerSolver) solver).getEigenvalues() );
			// solve poissons eqn
			solver = new SORSolver(params, rho);
			// implicitly scaled by electron charge, which is 1
			electronPotential = solver.solve()[0];
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
	
	public static void usage(String progName) {
		System.out.println("\n"+versionStr);
		System.out.println("Usage:");
		System.out.println(progName+" "+usageStr);
	}
	
	private static final String versionStr = 
			"Version: "+MAJOR_VER_NUM+"."+MINOR_VER_NUM+"\n"
			+ "Author: Mark Sholte\n"
			+ "Date: 20 December 2013";
	
	private static final String usageStr = 
			"<parameter_file>\n"
			+ "parameter_file should be a plaintext file that describes the parameters describing the well. "
			+ "The parameters should include the widths and bandgaps of each layer in the well, the step size, "
			+ "the effective masses, the relative dielectric constants, and the relative error tolerance for "
			+ "to determine when the self-consistent cycle has converged.";

}