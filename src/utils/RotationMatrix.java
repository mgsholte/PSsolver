package utils;

public class RotationMatrix extends Matrix{
	
	private int topLeftCorner;
	private double sinTheta;
	private double cosTheta;

	public RotationMatrix(int dim, int topLeftCorner, double sinTheta, double cosTheta) {
		assert(topLeftCorner < dim - 1);
		this.dim = dim;
		this.topLeftCorner = topLeftCorner;
		this.sinTheta = sinTheta;
		this.cosTheta = cosTheta;
	}
	
	//getters
	public int getCorner(){
		return topLeftCorner;
	}

	@Override
	public boolean isEqual(Matrix compare, double tolerance) {
		return super.isEqual(compare, tolerance);
	}

	@Override
	public double evalAt(int row, int col) {
		if (row == col){
			if (row == topLeftCorner || row == topLeftCorner + 1)
				return cosTheta;
			else
				return 1.0;
		}
		else if (row == topLeftCorner && col == topLeftCorner + 1)
			return sinTheta;
		else if (row == topLeftCorner + 1 && col == topLeftCorner)
			return -sinTheta;
		return 0;
	}
	
	@Override
	public RotationMatrix transpose(){
		return new RotationMatrix(dim, topLeftCorner, (-1.0)*sinTheta, cosTheta);
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

	@Override
	public void multiply(Matrix m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rotate(RotationMatrix p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void multiply(RotationMatrix p) {
		// TODO Auto-generated method stub
		
	}

}
