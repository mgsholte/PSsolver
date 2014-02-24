package utils;

public class SparseRMatrix extends Matrix{

	private double[] diag;
	private double[] diag1;
	private double[] diag2;
	private int dim;
	
	
	public SparseRMatrix(int n){
		diag = new double[n];
		diag1 = new double[n - 1];
		diag2 = new double[n - 2];
		dim = n;
	}

	@Override
	public boolean isEqual(Matrix compare) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double evalAt(int row, int col) {
		// TODO Auto-generated method stub
		return 0;
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
