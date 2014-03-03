package utils;

/***This is mainly written as a helper class to the FiniteDifferenceSolver.
 * As such, it only defines sparse square matrices and isn't very general to
 * any other problems in the application.
 * @author Matthew
 *
 */
public abstract class Matrix {
	
	protected int dim;
	
	//return the Identity as a dense matrix
	public static DenseMatrix getIdentity(int dim){
		double[][] matParam = new double[dim][dim];
		for(int row = 0; row < dim; row++){
			for(int col = 0; col < dim; col++){
				if(row == col)
					matParam[row][col] = 1.0;
				else
					matParam[row][col] = 0.0;
			}
		}
		return new DenseMatrix(dim, matParam);
	}

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
	
	public double[] getCol(int col){
		double[] colVals = new double[dim];
		for(int i = 0; i < dim; i++)
			colVals[i] = this.evalAt(i, col);
		return colVals;
	}
	
	//tests the matrix to see if off-diagonal entries are zero to within a given tolerance
	public boolean isDiagonal(double tolerance){
		for(int row = 0; row < dim; row++){
			for(int col = 0; col < dim; col++){
				if(row != col && Math.abs(this.evalAt(row, col)) > tolerance)
					return false;
			}
		}
		return true;
	}
	
	//isEqual:  all vals are the same
	public boolean isEqual(Matrix compare, double tolerance){
		if (dim != compare.getDim()) return false;
		for (int row = 0; row < dim; row++)
			for (int col = 0; col < dim; col++)
				if (Math.abs(this.evalAt(row, col) - compare.evalAt(row, col)) > tolerance)
					return false;
		return true;
	}
	
	//basic operations
	public abstract Matrix transpose();
	
	public abstract double evalAt(int row, int col);
	
	public abstract void changeVal(int row, int col, double newVal);
	
	public abstract void scalarMult(double scalar);
	
	public abstract void add(Matrix m);
	
	public abstract void multiply(Matrix m);
	
	public abstract void rotate(RotationMatrix p);

	public void multiply(RotationMatrix p) {
		// TODO Auto-generated method stub
		
	}
	
}
