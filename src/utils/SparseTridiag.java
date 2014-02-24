package utils;

/**
 * This class denotes a sparse tridiagonal matrix as used in the QR
 * diagonalization method. It actually has a second array above the first upper
 * off-diagonal to store values that occur during the QR algorithm
 * 
 * @author Matthew
 * 
 */
public class SparseTridiag extends Matrix {

	private double[] lower;
	private double[] diag;
	private double[] upper;
	private double[] rPortion;// this only has nonzero values in mid-step during
								// the QR method

	public SparseTridiag(int n) {
		this(n, new double[n - 1], new double[n], new double[n - 1]);
	}

	public SparseTridiag(int n, double[] l, double[] d, double[] u) {
		lower = l;
		diag = d;
		upper = u;
		dim = n;
		rPortion = new double[n - 2];
	}

	@Override
	public boolean isEqual(Matrix compare, double tolerance) {
		return super.isEqual(compare, tolerance);
	}

	@Override
	public double evalAt(int row, int col) {
		if (!(row >= 0 && row < dim && col >= 0 && col < dim)) {
			throw new IndexOutOfBoundsException();
		}
		if (row == col)
			return diag[row];
		if (row == col + 1)
			return lower[col];
		if (row == col - 1)
			return upper[row];
		if (row == col - 2)
			return rPortion[row];
		else
			return 0.;
	}

	@Override
	public void changeVal(int row, int col, double newVal) {
		if (row == col + 1 && col >= 0 && row < dim)
			lower[col] = newVal;
		else if (row == col && row >= 0 && row < dim)
			diag[row] = newVal;
		else if (row == col - 1 && row >= 0 && col < dim)
			upper[row] = newVal;
		else if (row == col - 2 && row >= 0 && col < dim)
			rPortion[row] = newVal;
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
	// THIS ONLY WORKS IF YOU'RE EXPECTING TO GET A SYMMETRIC TRIDIAGONAL AS A
	// RESULT
	public void multiply(Matrix m) {
		double[] upper1 = upper;
		double[] diag1 = diag;
		for (int i = 0; i < dim - 2; i++) {
			upper1[i] = diag[i] * m.evalAt(i, i + 1) + upper[i]
					* m.evalAt(i + 1, i + 1) * rPortion[i]
					* m.evalAt(i + 2, i + 1);
			diag1[i] = diag[i] * m.evalAt(i, i) + upper[i] * m.evalAt(i + 1, i)
					* rPortion[i] * m.evalAt(i + 2, i);
		}
		upper1[dim - 2] = diag[dim - 2] * m.evalAt(dim - 2, dim - 1)
				+ upper[dim - 2] * m.evalAt(dim - 1, dim - 1);
		diag1[dim - 2] = diag[dim - 2] * m.evalAt(dim - 2, dim - 2)
				+ upper[dim - 2] * m.evalAt(dim - 1, dim - 2);
		diag1[dim - 1] = diag[dim - 1] * m.evalAt(dim - 1, dim - 1);
		lower = upper1;
		diag = diag1;
		upper = upper1;
		rPortion = new double[dim - 2];
	}

	@Override
	public void rotate(RotationMatrix p) {
		int k = p.getCorner();// upper left corner location of rotation matrix
		for (int col = k; ((col < k + 3) && (col < p.getDim())); col++) {
			double valRowK = p.evalAt(k, k) * this.evalAt(k, col)
					+ p.evalAt(k, k + 1) * this.evalAt(k + 1, col);
			double valRowK1 = p.evalAt(k + 1, k) * this.evalAt(k, col)
					+ p.evalAt(k + 1, k + 1) * this.evalAt(k + 1, col);
			System.out.println(valRowK + " " + valRowK1);
			this.changeVal(k, col, valRowK);
			this.changeVal(k + 1, col, valRowK1);
		}
	}

	@Override
	public void multiply(RotationMatrix p) {
		// TODO Auto-generated method stub

	}

}
