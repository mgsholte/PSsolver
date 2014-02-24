package utils;

public class DenseMatrix extends Matrix{
	
	private double[][] vals;

	public DenseMatrix(int n, double[][] vals) {
		this.vals = vals;
		this.dim = n;
	}

	@Override
	public boolean isEqual(Matrix compare) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double evalAt(int row, int col) {
		return vals[row][col];
	}

	@Override
	public void changeVal(int row, int col, double newVal) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void scalarMult(double scalar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void add(Matrix m) {
		// TODO Auto-generated method stub
		
	}

}
