package utils;

import java.util.List;

public class ConvergenceTester {

	private final double targetErr;
			
	private Double[] oldVals, curVals;
	
	private double err, maxErr;
	
	public ConvergenceTester(double targetErr) {
		this.targetErr = targetErr;
		oldVals = curVals = null;
	}
	
	public boolean hasConverged() {
		return maxErr < targetErr;
	}
	
	public void updateCurValues(List<Double> newVals) {
		// test input
		if (curVals != null && newVals.size() != curVals.length)
			throw new IllegalArgumentException("Can only update convergence test values with a list which is the same length as the old one");
		// update references
		oldVals = (curVals == null) 
					? new Double[newVals.size()]
					: curVals;
		curVals = newVals.toArray(curVals);
		// recalc error
		maxErr = 0;
		for (int i = 0; i < curVals.length; ++i) {
			err = (curVals[i] - oldVals[i])/curVals[i];
			maxErr = (err > maxErr)
					? err 
					: maxErr;
		}
	}
	
}
