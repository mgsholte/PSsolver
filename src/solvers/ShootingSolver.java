package solvers;

//import com.sun.org.apache.xalan.internal.xsltc.DOM;

import utils.Domain;
import utils.Function;
import utils.WellParameters;
import utils.GreedyFunction;

public class ShootingSolver extends SchrodingerSolver {

	public ShootingSolver(WellParameters params, Function potential) {
		super(params, potential);
	}

	@Override
	public Function[] solveSystem() {
		Domain dom = params.getProblemDomain();
		//create the function array and set up problem
		Function[] psis = new Function[5];//TODO:  maybe change to an arraylist? don't know how many eigenstates there will be
		//Start at E = 0.  Check if E and E + delE end on opposite sides of the x axis. If they do, average them and
		//of the three energy guesses solve the state for the two remaining x axis straddlers. Do until a state ends close to 0
		double eLow = 0;
		double eHi = eLow + DEL_E;
		for (int i = 0; i < psis.length; i++){
			Function shot1 = solve(eLow);
			Function shot2 = solve(eHi);
			//TODO: clean up the logic here
			do {
				if (shot1.evalAt(dom.getUB())*shot2.evalAt(dom.getUB()) < 0){
					double eAvg = (eLow + eHi)/2;
					Function shot3 = solve(eAvg);
					if (shot3.evalAt(dom.getUB())*shot2.evalAt(dom.getUB()) < 0){
						shot1 = shot3;
						eLow = eAvg;
					}
					else{
						shot2 = shot3;
						eHi = eAvg;
					}
				}
				else eHi += DEL_E;
				shot2 = solve(eHi);
			} while(shot1.evalAt(dom.getUB()) > Domain.getTolerance() && shot2.evalAt(dom.getUB()) > Domain.getTolerance());
			psis[i] = shot1.evalAt(dom.getUB()) < shot2.evalAt(dom.getUB()) ? shot1 : shot2;
		}
		
		return psis;
	}

	/**
	 * Solve the potential for 1 wavefunction based on the given guess for the eigenenergy
	 * 
	 */
	@Override
	public Function solve(double energyGuess) {
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
			double mass = params.getMass().evalAt(dom.getValAtIndex(i));
			initVals[i+1] = (4*(h*h*(potential.evalAt(dom.getValAtIndex(i)) - energyGuess) + (HBAR*HBAR) * mass)*initVals[i]
							- 2*mass * (HBAR*HBAR) * initVals[i - 1])
							/(2*mass* (HBAR*HBAR));
		}
		return new GreedyFunction(dom, initVals);
	}


}
