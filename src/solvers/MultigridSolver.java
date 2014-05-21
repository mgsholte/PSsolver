package solvers;

import utils.Function;
import utils.GreedyFunction;
import utils.WellParameters;


/**
 * Assumes the equation is of the form Vxx = -chgDensity (need to check implementation) - so the del^2 matrix will be symm tridiag
 * Uses log_2(numPoints) based on domain of input params - needs to be a power of 2
 * @author Matthew
 *
 */
public class MultigridSolver extends PoissonSolver {
	
	protected static double ERR_TOLERANCE = 1E-7;
	protected int fineNumPoints;

	private final Function RHS;
	
	//Used to find the number of grids to use
	public static int log2( int bits ) // returns 0 for bits=0
	{
	    int log = 0;
	    if( ( bits & 0xffff0000 ) != 0 ) { bits >>>= 16; log = 16; }
	    if( bits >= 256 ) { bits >>>= 8; log += 8; }
	    if( bits >= 16  ) { bits >>>= 4; log += 4; }
	    if( bits >= 4   ) { bits >>>= 2; log += 2; }
	    return log + ( bits >>> 1 );
	}

	public MultigridSolver(WellParameters params, Function chgDensity) {
		super(params, chgDensity);
		fineNumPoints = potential.getDomain().getNumPoints();
		RHS = potential.divide(params.getDielectric());
	}
	
	public static void setTolerance(double tol) {
		ERR_TOLERANCE = tol;
	}

	@Override
	public Function solve(double param) {
		final int numGrids = log2(fineNumPoints);
		int gridNumber = 1;
		int n = 2;//the number of intervals in the current grid
		int maxIters = 10000;//number of allowed g-s iters per step
		double h = Math.pow(2, numGrids - 1) * potential.getDomain().getDx();//current grid spacing
		double r = 0;
		double[] soln = new double[n + 1];
		double[] residual = new double[n + 1];
		do{
			int iter = 0;//number of g-s iterations
//			long startTime = System.currentTimeMillis();
			do{
				iter++;
				soln = gaussSeidel(h, soln);
			}while(iter < maxIters);
			residual = error(soln, h);
			r = maxErr(residual);

//			System.out.println("The g-s residual is " + r + ", the grid size is " + (n + 1) + " points.");
			
			//if g-s did not converge, restrict to a coarser grid
			if(r >= ERR_TOLERANCE){
				soln = vCycle(soln, residual, h, n, gridNumber);
			}
			
			//if g-s did converge, interpolate to a finer grid
			else if(r < ERR_TOLERANCE && h >= potential.getDomain().getDx() * 2) {
				soln = interpolate(soln, h, n);
				h /= 2;
				n *= 2;
				gridNumber++;
			}
			//while(System.currentTimeMillis() < 1000 + startTime){};
		}while(!(soln.length == fineNumPoints && r < ERR_TOLERANCE));
		return new GreedyFunction(potential.getDomain(), soln);
	}
	
	//performs one iteration of the gaussSeidel method of solving Au = f
	//goes from both sides and averages the result
	public double[] gaussSeidel(double gridSpacing, double[] u){
		double[] uNewLeft = new double[u.length];
		double[] uNewRight = new double[u.length];
		uNewLeft[0] = 0; uNewLeft[uNewLeft.length - 1] = 0; //boundary conditions
		uNewRight[0] = 0; uNewRight[uNewRight.length - 1] = 0; //boundary conditions
		double lb = potential.getDomain().getLB();
		double h2 = gridSpacing * gridSpacing;
		int j;
		double xl, xr;
		for(int i = 1; i < uNewLeft.length - 1; i++){
			j = uNewRight.length - 1 - i;
			xl = lb + i*gridSpacing;
			xr = lb + j*gridSpacing;
			uNewLeft[i] = h2 / 2.0 * (RHS.evalAt(xl) + uNewLeft[i - 1]/h2 + u[i + 1]/h2);
			uNewRight[j] = h2 / 2.0 * (RHS.evalAt(xr) + uNewRight[j + 1]/h2 + u[j - 1]/h2);
		}
		double[] uNew = new double[u.length];
		for(int i = 0; i < uNew.length; i++)
			uNew[i] = .5 * uNewLeft[i] + .5 * uNewRight[i];
		return uNew;
	}
	
	public double[] gaussSeidel(double gridSpacing, double[] u, double[] rhs){
		double[] uNewLeft = new double[u.length];
		double[] uNewRight = new double[u.length];
		uNewLeft[0] = 0; uNewLeft[uNewLeft.length - 1] = 0; //boundary conditions
		uNewRight[0] = 0; uNewRight[uNewRight.length - 1] = 0; //boundary conditions
		double h2 = gridSpacing * gridSpacing;
		int j;
		for(int i = 1; i < uNewLeft.length - 1; i++){
			j = uNewRight.length - 1 - i;
			uNewLeft[i] = h2 / 2.0 * (rhs[i] + uNewLeft[i - 1]/h2 + u[i + 1]/h2);
			uNewRight[j] = h2 / 2.0 * (rhs[j] + uNewRight[j + 1]/h2 + u[j - 1]/h2);
		}
		double[] uNew = new double[u.length];
		for(int i = 0; i < uNew.length; i++)
			uNew[i] = .5 * uNewLeft[i] + .5 * uNewRight[i];
		return uNew;
	}
	
	public double[] error(double[] u, double h){
		double[] residual = new double[u.length];
		double lb = potential.getDomain().getLB();
		double h2 = h * h;
		double x;
		for(int i = 1; i < residual.length - 1; i++) {
			x = lb + i * h;
			residual[i] = RHS.evalAt(x) + 1/h2 * (u[i - 1] + u[i + 1] - 2 * u[i]);
		}
		return residual;
	}
	
	protected double maxErr(double[] residual){
		double err = 0;
		for(int i = 1; i < residual.length - 1; i++){//because I know it's 0 at the ends
			double newErr = Math.abs(residual[i]);
			if(newErr > err) err = newErr;
		}
		return err;
	}
	
	public double[] restrict(double[] u, double spacing, int intervals){
		if(intervals % 2 != 0 || intervals == 2)
			throw new IllegalStateException("Cannot restrict to fewer than 1 inner grid point");
		double[] newVals = new double[intervals/2 + 1];
		newVals[0] = newVals[newVals.length - 1] = 0;//boundary conditions
		int j;
		for(int i = 1; i < newVals.length - 1; i++){
			j = 2 * i;
			newVals[i] = .25 * u[j - 1] + .5 * u[j] + .25 * u[j + 1];
		}
		return newVals;
	}
	
	//linear interpolation 
	public double[] interpolate(double[] u, double spacing, int intervals){
		double[] newVals = new double[u.length * 2 - 1];
		newVals[0] = newVals[newVals.length - 1] = 0;//boundary conditions
		for(int i = 1; i < newVals.length - 1; i++){
			if((i + 1) % 2 == 0)
				newVals[i] = .5 * u[(i - 1)/2] + .5 * u[(i + 1)/2];
			else
				newVals[i] = u[i/2];
		}
		return newVals;
	}
	
	//complete a v-cycle.  This will start by restricting the error term
	//and advance recursively to correct the solution
	public double[] vCycle(double[] uH, double[] rH, double h, int gridIntervals, int gridNumber){
		int numGS = 100;
		rH = restrict(rH, h, gridIntervals);
		h *= 2;
		gridIntervals /= 2;
		gridNumber--;
		double[] e2H = new double[rH.length];
		for(int i = 0; i < numGS; i++){
			e2H = gaussSeidel(h, e2H, rH);
		}
		if(gridNumber >= 2){
//			uH = restrict(uH, h, gridIntervals);
//			uH = interpolate(vCycle(uH, rH, h, gridIntervals, gridNumber), h, gridIntervals);
		}
		else{
			double[] eH = interpolate(e2H, h, gridIntervals);
			uH = addVect(eH, uH);
		}
		return uH;
	}
	
	public double[] addVect(double[] vect1, double[] vect2){
		if (vect1.length != vect2.length)
			throw new IllegalArgumentException("Cannot add two vectors of different length: Vect1 length is " 
												+ vect1.length + " while Vect2 length is " + vect2.length);
		double[] result = new double[vect1.length];
		for(int i = 0; i < result.length; i++)
			result[i] = vect1[i] + vect2[i];
		return result;
	}

}
