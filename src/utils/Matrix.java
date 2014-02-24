package utils;

/***This is mainly written as a helper class to the FiniteDifferenceSolver.
 * As such, it only defines sparse square matrices and isn't very general to
 * any other problems in the application.
 * @author Matthew
 *
 */
public abstract class Matrix {
	
	protected int dim;

	//getters
	public int getDim(){
		return this.dim;
	}
	
	public String toString(){
		String result = "\n[";
		for (int row = 0; row < dim; row++){
			for (int col = 0; col < dim; col++)
				result += this.evalAt(row, col) + " ";
			if (row != dim - 1) result += "\n";
		}
		result += "]";
		return result;
	}
	
	//isEqual:  all vals are the same
	public abstract boolean isEqual(Matrix compare);
	
	//basic operations
	public abstract double evalAt(int row, int col);
	
	public abstract void changeVal(int row, int col, double newVal);
	
	public abstract void scalarMult(double scalar);
	
	public abstract void add(Matrix m);
	
//	//this.matMult(m) = this*m and m.matMult(this) = m*this (matMult = left multiplication)
//	public Matrix matMult(Matrix m){
//		double[][] newVals = new double[vals.length][vals.length];
//		for(int i = 0; i < newVals.length; i++){
//			for(int j = 0; j < newVals.length; j++){
//				double sum = 0;
//				for(int k = 0; k < newVals.length; k++){
//					sum += this.vals[i][k]*m.evalAt(k, j);
//				}
//				newVals[i][j] = sum;
//			}
//		}
//		return new Matrix(newVals);
//	}
}
