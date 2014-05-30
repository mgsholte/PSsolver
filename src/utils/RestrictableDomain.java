package utils;

public class RestrictableDomain extends Domain implements Restrictable {
	
	private int curN; /// the current restricted value of N

	public RestrictableDomain(double lb, double ub, int N) {
		super(lb, ub, N);
		if (isInvalidN(N))
			throw new IllegalArgumentException("N must be 1 greater than a power of 2, not "+N);
		curN = N;
	}

	/**
	 * N must be equal to 2^i + 1 for some integer i to be restrictable.
	 * N must also be >= 3
	 * @param N
	 * @return false iff N is not a valid value for constructing a {@code RestrictableDomain}
	 */
	private static boolean isInvalidN(int N) {
		int Nm1 = N-1;
		return (Nm1 & -Nm1) != Nm1 || Nm1 < 2;
		/* original method for detecting invalid N
		if ((N & 1) == 0 || N < 3) // N must be odd and >= 3
			return true;
		boolean found1bit = false, is1bit = false;
		int mask = 1 << 1;
		// iterate over every bit (other than 1st) in N looking for a 1
		// if two 1s are found, N is invalid
		for (int i = 1; i < 32; ++i) {
			is1bit = (N & mask) != 0;
			// check if we found a 2nd 1 bit
			if (is1bit && found1bit)
				return true;
			found1bit |= is1bit;
			mask <<= 1;
		}
		return false;
		*/
	}
	
	/* (non-Javadoc)
	 * @see utils.Restrictable#restrict()
	 */
	@Override
	public boolean restrict() {
		// must be at least 3 points in the domain. cannot restrict further
		if (curN == 3)
			return false;
		curN = (curN-1)/2 + 1;
		setDX(curN);
//		dx *= 2; // might give better results? or even use Math.scalb()
		return true;
	}
	
	/* (non-Javadoc)
	 * @see utils.Restrictable#interpolate()
	 */
	@Override
	public void interpolate() {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	/**
	 * @see utils.Domain#getNumPoints()
	 */
	@Override
	public int getNumPoints() {
		return curN;
	}
	
}
