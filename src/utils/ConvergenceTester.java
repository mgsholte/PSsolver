package utils;


public class ConvergenceTester {

	private final double targetErr;
			
	private double[] oldVals, curVals;
	
	private double err, maxErr;
	
	public ConvergenceTester(double targetErr) {
		this.targetErr = targetErr;
		oldVals = curVals = null;
	}
	
	public boolean hasConverged() {
		return maxErr < targetErr;
	}
	
	public void initCycle(int numPoints) {
		//oldVals = (curVals == null) 
		//			? new double[numPoints]
		//			: curVals;
		//need to make curVals not null if neither one has already been initialized
		if (curVals == null){
			oldVals = new double[numPoints];
			curVals = new double[numPoints];
		}
		else
			oldVals = curVals.clone();
		maxErr = 0;
	}
	
	/**
	 * convenience function for calling updateValAtIdx for all values at once.
	 * no need to call initCycle when using this method
	 * @param newVals - the array of updated values. must have all updates (i.e., 
	 * 		no partial updates)
	 */
	public void updateCurValues(double[] newVals) {
		// test input
		if (curVals != null && newVals.length != curVals.length)
			throw new IllegalArgumentException("Can only update convergence test values with a list which is the same length as the old one");
		// update references
		initCycle(newVals.length);
		curVals = newVals;
		// recalc error
		for (int i = 0; i < curVals.length; ++i) {
			calcErrAtIdx(i);
		}
	}
	
	/**
	 * Important: Must call initCycle when starting from index 0
	 * @param newVal - the updated value
	 * @param i - the index to update
	 */
	public void updateValAtIdx(double newVal, int i) {
		curVals[i] = newVal;
		calcErrAtIdx(i);
	}

	private void calcErrAtIdx(int i) {
		err = (curVals[i] - oldVals[i])/curVals[i];
		maxErr = (err > maxErr)
				? err 
				: maxErr;
	}
	
}
