package testing;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import utils.Matrix;
import utils.DenseMatrix;
import utils.SparseTridiag;
import utils.RotationMatrix;

public class MatrixTest {

	private DenseMatrix m1;
	private Matrix m2;
	private Matrix m3;
	private Matrix m4;
	private Matrix m5;
	double[][] vals1 = {{1.0, 2.0, 3.0}, {0.0, 1.0, 0.0}, {3.0, 1.5, 2.5}};
	double[][] vals2 = {{-2.0, 1.0, 0.0}, {1.0, -2.0, 1.0}, {0.0, 1.0, -2.0}};
	double[][] vals3 = {{-2.0, 1.0, 0.0, 0.0}, {1.0, -3.0, -1.0, 0.0}, {0.0, -1.0, 1.0, 1.0}, {0.0, 0.0, 1.0, 3.0}};
	double[] finDiff1 = {-2.0, -2.0, -2.0, -2.0};
	double[] finDiff2 = {1.0, 1.0, 1.0};
	
	@Before
	public void setUp() throws Exception {
		m1 = new DenseMatrix(3, vals1);
		m2 = new DenseMatrix(3, vals2);
		m3 = new DenseMatrix(4, vals3);
		m4 = new SparseTridiag(4, finDiff2, finDiff1, finDiff2.clone());
	}

	@Test
	public void testPrint() {
		System.out.println(m1);
		System.out.println(m2);
		System.out.println(m3);
		System.out.println(m4);
		System.out.println("Should only see matrices and no memory addresses");
	}
	
	@Test
	public void testChangeVal(){
		m3.changeVal(2, 3, 58.0);
		//m4.changeVal(1, 0, 74.0);
		assertEquals(58.0, m3.evalAt(2, 3), .0001);
		//assertEquals(74.0, m4.evalAt(1, 0), .0001);
	}
	
	@Test
	public void testIdentityMatrix(){
		Matrix A = Matrix.getIdentity(5);
		for (int row = 0; row < 5; row++)
			for(int col = 0; col < 5; col++)
				if (row == col){
					assertEquals(A.evalAt(row, col), 1.0, .0001);
				} else assertEquals(A.evalAt(row, col), 0.0, .0001);
	}
	
	@Test
	public void testIsDiagonal(){
		Matrix A = Matrix.getIdentity(5);
		assertTrue(A.isDiagonal(.0001));
	}
	
	@Test
	public void testTranspose(){
		RotationMatrix p = new RotationMatrix(5, 2, .5, .5);
		RotationMatrix pT = p.transpose();
		for (int row = 0; row < 5; row++)
			for (int col = 0; col < 5; col++)
				assertEquals(p.evalAt(row, col), pT.evalAt(col, row), .0001);
	}
	
	@Test
	public void testMultiply(){
		RotationMatrix p0 = new RotationMatrix(4, 0, .5, .5);
		RotationMatrix p1 = new RotationMatrix(4, 1, .5, .5);
		RotationMatrix p2 = new RotationMatrix(4, 2, .5, .5);
//		m4.multiply(m3);
//		double[][] result1 = {{5.0, -5.0, -1.0, 0.0}, {-4.0, 6.0, 3.0, 1.0}, {1.0, -1.0, -2.0, 1.0}, {0.0, -1.0, -1.0, -5.0}};
//		assertTrue(m4.isEqual(new DenseMatrix(m4.getDim(), result1), .0001));
		m3.multiply(p0);
		double[][] result2 = {{-1.5, -.5, 0.0, 0.0}, {2.0, -1.0, -1.0, 0.0}, {.5, -.5, 1.0, 1.0}, {0.0, 0.0, 1.0, 3.0}};
		assertTrue(m3.isEqual(new DenseMatrix(m3.getDim(), result2), .0001));
		m3.multiply(p1);
		double[][] result3 = {{-1.5, -.25, -.25, 0.0}, {2.0, 0.0, -1.0, 0.0}, {.5, -.75, .25, 1.0}, {0.0, -.5, .5, 3.0}};
		assertTrue(m3.isEqual(new DenseMatrix(m3.getDim(), result3), .0001));
		m3.multiply(p2);
		double[][] result4 = {{-1.5, -.25, -.125, -.125}, {2.0, 0.0, -.5, -.5}, {.5, -.75, -.375, .625}, {0.0, -.5, -1.25, 1.75}};
		assertTrue(m3.isEqual(new DenseMatrix(m3.getDim(), result4), .0001));
		
	}
	
	@Test
	public void testRotate(){
		RotationMatrix p0 = new RotationMatrix(4, 0, .5, .5);
		RotationMatrix p1 = new RotationMatrix(3, 1, .5, .5);
		RotationMatrix p2 = new RotationMatrix(4, 2, .5, .5);
		System.out.println(m4);
		System.out.println(p0);
		m4.rotate(p0);
		System.out.println(m4);
		double[][] result0 = {{-.5, -.5, .5, 0.0}, {1.5, -1.5, .5, 0.0}, {0.0, 1.0, -2.0, 1.0}, {0.0, 0.0, 1.0, -2.0}};
		assertTrue(m4.isEqual(new DenseMatrix(m4.getDim(), result0), .0001));
	}

}
