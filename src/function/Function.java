package function;

/**
 * The length of the domain (size) of the function needs to be an integer
 * multiple of the stepSize.
 * 
 * @author Matthew Butcher
 * @version Version 1, 10/14/13
 */
public class Function {

	private final double[] vals;
	private final Domain domain;

	/**
	 *  Create a scalar function of 1 variable from a pre-filled array of values using a 
	 *  constant spacing between values of the independent variable.
	 *  
	 * @param function - the array of function values
	 * @param lb - the smallest value of the indep var (i.e., at function[0])
	 * @param ub - the largest value of the indep var (i.e., at function[length-1])
	 * @param stepSize - the uniform spacing between indep var values
	 */
	public Function(double[] fvals, double lb, double ub, double stepSize) {
		vals = fvals;
		domain = new Domain(lb, ub, stepSize);
	}
	
	public Function(double[] fvals, Domain d) {
		vals = fvals;
		domain = d;
	}
	
	public Function(double[] fileData) {
		vals = new double[fileData.length - 3];
		for (int index = 3; index < fileData.length; index++)
			vals[index] = fileData[index];
		domain = new Domain(fileData[0], fileData[1], fileData[2]);
	}

	/// getter methods
	public double getLB() { return domain.lb; }
	public double getUB() { return domain.ub; }
	public double getH()  { return domain.h; }
	public Domain getDomain() { return domain; }
	public double[] getVals() { return vals; }
	public int getSize() { return vals.length; }

	// /**standard string conversion - probably not necessary
	// *
	// */
	// public String toString(){
	// return "stuff";//not done here yet
	// }
	
	public double evalAt(double x) {
		if (!domain.contains(x)) 
			throw new NotInDomainException(x);
		int idx = (int) Math.round((x-domain.lb)/domain.h);
		return vals[idx];
	}
		
	/**
	 * adds two function objects
	 * 
	 */
	public Function plus(Function f) {
		if (!domain.equals(f.domain))
			throw new DomainMismatchException();
		int n = vals.length;
		double[] sum = new double[n];
		for (int i = 0; i < n; ++i) {
			sum[i] = this.vals[i] + f.vals[i];
		}
		return new Function(sum, domain);
	}
	
	public double compare(Function f){
		if (!domain.equals(f.domain))
			throw new DomainMismatchException();
		double[] f1Square = this.times(this).getVals();
		double[] f2Square = f.times(f).getVals();
		double diff = 0;
		for (int index = 0; index < this.getVals().length; index++)
			diff += f1Square[index] - f2Square[index];
		return diff;
	}

	/**
	 * multiplies two function objects
	 * 
	 */
	public Function times(Function f) {
		if (!domain.equals(f.domain))
			throw new DomainMismatchException();
		int n = vals.length;
		double[] product = new double[n];
		for (int i = 0; i < n; ++i) {
			product[i] = this.vals[i] * f.vals[i];
		}
		return new Function(product, domain);
	}

	/** Perform a definite integral using the given bounds */
	public double integrate(double lowBd, double upBd) {
		Domain iDom = new Domain(lowBd, upBd, domain.h);
		if (!iDom.isSubsetOf(domain))
			throw new FunctionException(
					"Tried to integrate a function on the interval "+iDom+", "
					+"but it is only defined on "+domain
					);
		/// NB: endIdx is 1 past the last value that should be included in the integral
		int startIdx = (int) Math.floor((lowBd-domain.lb)/domain.h), 
			endIdx = (int) Math.ceil((upBd-domain.ub)/domain.h) + vals.length;
		
		double sum = 0;
		for (int i = startIdx; i < endIdx; ++i) {
			sum += vals[i]; // factored out 'h' since it is constant
		}
		return domain.h*sum;
	}
	
	/** Convenience function to integrate over the entire domain */
	public double integrate() {
		return integrate(getLB(), getUB());
	}

	/**
	 * This requires f to have the same size and step size as our fOfx so it
	 * mostly is built specifically for computing the norm of a wavefunction
	 */
	public double innerProduct(Function f) {
		if (!domain.equals(f.domain))
			throw new DomainMismatchException();
		return this.times(f).integrate();
	}
}
