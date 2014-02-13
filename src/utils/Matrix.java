package utils;

/***This is mainly written as a helper class to the FiniteDifferenceSolver.
 * As such, it only defines sparse square matrices and isn't very general to
 * any other problems in the application.
 * @author Matthew
 *
 */
public class Matrix {
	
	private double[][] vals;
	private int dim;

	public Matrix(double[][] vals){
		this.vals = vals;
		dim = vals.length;
	}
	
	//getters
	public double[][] getVals(){
		return this.vals;
	}
	
	public int getDim(){
		return this.dim;
	}
	
	//toString
	public String toString(){
		String result = "[\n";
		for(int i = 0; i < vals.length; i++){
			for (int j = 0; j < vals.length; j++){
				result += this.evalAt(i,j) + " ";
			}
			result += "\n";
		}
		result += " ]";
		return result;
	}
	
	//isEqual:  all vals are the same
	public boolean isEqual(Matrix compare){
		if (this.dim != compare.getDim())
			return false;
		else
			for (int i = 0; i < this.dim; i++){
				for (int j = 0; j < this.dim; j++){
					if (this.vals[i][j] != compare.evalAt(i, j))
						return false;
				}
			}
			return true;
	}
	
	//basic operations
	public double evalAt(int row, int col){
		return vals[row][col];
	}
	
	public void changeVal(int row, int col, double newVal){
		vals[row][col] = newVal;
	}
	
	public void scalarMult(double scalar){
		for (int i = 0; i < vals.length; i++)
			for (int j = 0; j < vals.length; j++)
				vals[i][j] *= scalar;
	}
	
	public void add(Matrix m){
		for (int i = 0; i < vals.length; i++)
			for (int j = 0; j < vals.length; j++)
				this.vals[i][j] += m.evalAt(i, j);
	}
	
	//this.matMult(m) = this*m and m.matMult(this) = m*this (matMult = left multiplication)
	public Matrix matMult(Matrix m){
		double[][] newVals = new double[vals.length][vals.length];
		for(int i = 0; i < newVals.length; i++){
			for(int j = 0; j < newVals.length; j++){
				double sum = 0;
				for(int k = 0; k < newVals.length; k++){
					sum += this.vals[i][k]*m.evalAt(k, j);
				}
				newVals[i][j] = sum;
			}
		}
		return new Matrix(newVals);
	}
}
