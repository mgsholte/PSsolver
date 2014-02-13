package utils;

import main.Main;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Domains are contiguous subsets of the real numbers
 * @author Mark
 *
 */
public class Domain implements Iterable<Double> {
	
	/** 
	 * Tolerance for determining if a number is close enough to be 
	 * a number in the domain to be considered in the domain. Default
	 * controlled by {@link Main#DEFAULT_TOLERANCE} */
	private static double TOLERANCE = Main.DEFAULT_TOLERANCE;
	
	public static final double getTolerance() {
		return TOLERANCE;
	}

	public static final void setTolerance(double newTolerance) {
		if (newTolerance > 0 && newTolerance < 1)
			TOLERANCE = newTolerance;
		else
			System.err.println("Warning (Domain.setTolerance): cannot set tolerance to be outside the range (0,1). Ignoring attempt");
	}

	/** the lower and upper bounds of the domain */
	private double lb, ub;
	/** the uniform spacing between points in the domain */
	private double dx;
	
	//TODO: Can we change this so that it always sets the upper bound to be the higher x value?
	public Domain(double lb, double ub, double dx) {
		// if lb > ub, then dx < 0 should be true
		this.lb = lb; this.ub = ub; this.dx = dx;
	}
	
	 public double getDx() { return dx; }
	 public double getUB() { return ub; }
	 public double getLB() { return lb; }

	/**
	 * Partition a domain into contiguous, non-overlapping sub-domains. The number of new domains
	 * to be created is determined by how many new upper bounds are supplied as variadic args. The
	 * number of sub-domains will be this number plus one.
	 * @param dom - the domain to be partitioned
	 * @param ubs - the values of the upper bounds of the sub-domains to be created. 
	 * 	They should be sorted in ascending order and the first value should be greater than the {@code lb}
	 * 	of {@code domain}. The last value can be greater than the {@code ub} of the {@code domain},
	 * 	but the next to last value cannot.
	 * @return a list of the newly created sub-domains. The list is sorted in the sense that
	 * 	the upper bound of a domain is {@code dx} less than the lower bound of the next in the list
	 */
	public static List<Domain> partition(Domain dom, double... ubs) {
		return Domain.partition(dom.lb, dom.ub, dom.dx, ubs);
	}
	
	/** 
	 * Creates a Domain with the specified {@code lb}, {@code ub}, and {@code dx}
	 * and feeds it to {@link #partition(Domain, double...)} 
	 */
	public static List<Domain> partition(double lb, double ub, double dx, double... ubs) {
		if (ubs.length == 0)
			throw new IllegalArgumentException("Must pass at least one new upper bound when partitioning a Domain");
		
		List<Domain> subDoms = new LinkedList<Domain>();
		double lbItr = lb;
		for(double ubItr : ubs) {
			ubItr = Math.min(ubItr, ub);
			subDoms.add(new Domain(lbItr, ubItr, dx));
			if (ubItr == ub)
				break;
			lbItr = ubItr + dx;
		}
		return subDoms;
	}
	
	/**
	 * Test to see if {@code x} is in this domain. Note: does not test if 
	 * {@code x} is aligned on a multiple of {@code dx} from {@code lb}
	 * @param x - the value being tested
	 * @return true iff {@code lb} < {@code x} < {@code ub}
	 */
	public boolean contains(double x) {
		return lb < x && x < ub;
	}
	
	/**
	 * @param subD - the potential sub-domain
	 * @return true iff {@code subD} is a sub-domain of this
	 */
	public boolean hasSubDomain(Domain subD) {
		// check that subD falls inside bounds of this
		if (subD.lb < lb || subD.ub > ub || subD.dx < dx)
			return false;
		// check that subD.lb is aligned on a multiple of dx from this.lb
		double offset = (lb - subD.lb)/dx;
		if ( Math.abs(Math.round(offset) - offset) > TOLERANCE )
			return false;
		// check that subD.dx is an int multiple of dx
		double ratio = subD.dx/dx;
		return Math.abs(Math.round(ratio) - ratio) <= TOLERANCE;
	}

	public int getNumPoints() {
		return (int) Math.floor((ub-lb)/dx);
	}
	
	/**
	 * Note: Note: does not test if {@code x} is aligned on a multiple of {@code dx} from {@code lb}
	 * @param x - the value whose index is desired
	 * @return the number of multiples of {@code dx} by which {@code x} is greater than {@code lb}
	 */
	public int getIndexOf(double x) {
		if (!this.contains(x))
			throw new IllegalArgumentException("the value "+x+" is not in the domain "+this.toString());
		return (int) Math.round((x - lb)/dx); 
	}
	
	public double getValAtIndex(int i) {
		return lb + i*dx;
	}

	@Override
	public String toString() {
		return "Domain#[lb=" + lb + ", ub=" + ub + ", dx=" + dx + "]";
	}

	@Override
	public Iterator<Double> iterator() {
		return new DomainIterator();
	}
	
	private class DomainIterator implements Iterator<Double> {
		double curPosition; 
		
		public DomainIterator() {
			curPosition = lb;
		}

		@Override
		public boolean hasNext() {
			return curPosition <= ub;
		}

		@Override
		public Double next() {
			double ans = curPosition;
			curPosition += dx;
			return ans;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Can't remove elements from the domain");
		}
		
	}

}
