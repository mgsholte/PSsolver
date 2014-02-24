package utils;

public class SparseTridiag extends Matrix{

	private double[] lower;
	private double[] diag;
	private double[] upper;
	
	
	public SparseTridiag(int n, double[] l, double[] d, double[] u){
		lower = l;
		diag = d;
		upper = u;
		dim = n;
	}

	

	@Override
	public boolean isEqual(Matrix compare) {
		if (this.getDim() != compare.getDim())
			return false;
		//TODO fix this method
		return false;
	}


	@Override
	public double evalAt(int row, int col) {
		if (row >= 0 && row < dim && col >= 0 && col < dim){
			if(row == col)
				return diag[row];
			if(row == col + 1)
				return lower[row - 1];
			if(row == col - 1)
				return upper[row];
		}
		return 0.0;
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
