package function;

/**
 * Representation of discrete, contiguous subsets of R^1 with a constant spacing between elements 
 * @author Mark
 */
public class Domain {
	
	/** If a double is < this value we consider it to be zero */
	private static final double VANISH_PREC = 1.0E-14; //TODO: set to Double.MIN_NORMAL?

	/** The lower and upper bound of the 1D domain*/
	public final double lb, ub;
	
	/** The constant spacing between elements */
	public final double h;

	public Domain(double lb, double ub, double h) {
		if (h <= 0.0)
			throw new IllegalArgumentException("Step size must be positive");
		this.lb = lb; this.ub = ub; this.h = h;
	}
	
	public boolean contains(double x) {
		//TODO: maybe account for machine precision error by allowing a small error?
		return lb <= x && x <= ub;
	}
	
	public boolean isSubsetOf(Domain d) {
		return d.lb < lb && ub < d.ub && h >= d.h && Math.IEEEremainder(h, d.h)/d.h < VANISH_PREC;
	}
	
	public static double getVanishPrec(){ return VANISH_PREC; }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(h);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(lb);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(ub);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Domain))
			return false;
		Domain other = (Domain) obj;
		if (Double.doubleToLongBits(h) != Double.doubleToLongBits(other.h))
			return false;
		if (Double.doubleToLongBits(lb) != Double.doubleToLongBits(other.lb))
			return false;
		if (Double.doubleToLongBits(ub) != Double.doubleToLongBits(other.ub))
			return false;
		return true;
	}
	
}
