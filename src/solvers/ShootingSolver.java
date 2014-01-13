package solvers;

import utils.Domain;
import utils.Function;
import utils.WellParameters;

public class ShootingSolver extends SchrodingerSolver {

	public ShootingSolver(WellParameters params, Function potential) {
		super(params, potential);
	}

	@Override
	public Function[] solve() {
		// TODO Auto-generated method stub
		return null;
	}

	protected Function solveOneState(double energyGuess) {
		Domain dom = params.getProblemDomain();
		double[] initVals = new double[dom.getNumPoints()];
		double h = dom.getDx();
		/* 
		 initial conditions. guess at second value. guess determines normalizing factor
		 small guess used so that wvfcn doesn't blow up at max value and the spacing 
		 between successive double values is small 
		*/
		//TODO: determine if 2nd value guess is appropriate at 1E-30
		initVals[0] = 0; initVals[1] = 1E-30;
		for (int i = 1; i < dom.getNumPoints()-1; ++i) {
			// initVals[i+1] = (4*(h*h*()))
		}
		return null;
	}


}
