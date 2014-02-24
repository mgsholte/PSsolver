package utils;

public class DenseMatrix extends Matrix{
	
	private double[][] vals;

	public DenseMatrix(int n, double[][] vals) {
		this.vals = vals;
		this.dim = n;
	}
	
	//generates a zero matrix
	public DenseMatrix(int n){
		this(n, new double[n][n]);
	}

	@Override
	public boolean isEqual(Matrix compare, double tolerance) {
		return super.isEqual(compare, tolerance);
	}

	@Override
	public double evalAt(int row, int col) {
		return vals[row][col];
	}

	@Override
	public void changeVal(int row, int col, double newVal) {
		vals[row][col] = newVal;		
	}

	@Override
	public void scalarMult(double scalar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void add(Matrix m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Matrix transpose() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void multiply(Matrix m){
		// TODO Auto-generated method stub
	}
	
	//Overload of multiply method, much faster for rotations
	@Override
	public void multiply(RotationMatrix p) {
		int k = p.getCorner();//location of the upper left corner of the rotation matrix
		for (int row = 0; row < dim; row++){
			double newVal1 = this.evalAt(row, k)*p.evalAt(k, k) + this.evalAt(row, k + 1)*p.evalAt(k + 1, k);
			double newVal2 = this.evalAt(row, k)*p.evalAt(k, k + 1) + this.evalAt(row, k + 1)*p.evalAt(k + 1, k + 1);
			this.changeVal(row, k, newVal1);
			this.changeVal(row, k + 1, newVal2);
		}
	}

	@Override
	public void rotate(RotationMatrix p) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean isTridiag(){
		for(int row = 0; row < dim; row++){
			for (int col = 0; col < dim; col++){
				if (Math.abs(row - col) > 1)
					if (Math.abs(this.evalAt(row, col)) > .000001 )
						return false;
			}
		}
		return true;
	}
	
	public SparseTridiag toTridiag(){
		double[] lower = new double[dim - 1];
		double[] diag = new double[dim];
		double[] upper = new double[dim - 1];
		throw new UnsupportedOperationException("Work in progress");
	}

}
