package utils;

public interface Restrictable {

	/**
	 * restrict the domain to half the number of intervals. restriction will fail if the domain 
	 * is at the minimum # of points (3) already
	 * @return true iff the restriction was successful
	 */
	public boolean restrict();

	/**
	 * the inverse operation to restrict
	 * @see Restrictable#restrict()
	 */
	public void interpolate();

}