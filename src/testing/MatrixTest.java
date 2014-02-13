package testing;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import utils.Matrix;

public class MatrixTest {

	private Matrix m1;
	private Matrix m2;
	private Matrix m3;
	private Matrix m4;
	double[][] vals1 = {{1.0, 2.0, 3.0}, {0.0, 1.0, 0.0}, {3.0, 1.5, 2.5}};
	double[][] vals2 = {{-2.0, 1.0, 0.0}, {1.0, -2.0, 1.0}, {0.0, 1.0, -2.0}};
	double[][] vals3 = {{-2.0, 1.0, 0.0, 0.0}, {1.0, -3.0, -1.0, 0.0}, {0.0, -1.0, 1.0, 1.0}, {0.0, 0.0, 1.0, 3.0}};
	double[][] vals4 = {{-2.0, 1.0, 0.0, 0.0}, {1.0, -2.0, 1.0, 0.0}, {0.0, 1.0, -2.0, 1.0}, {0.0, 0.0, 1.0, -2.0}};
	
	@Before
	public void setUp() throws Exception {
		m1 = new Matrix(vals1);
		m2 = new Matrix(vals2);
		m3 = new Matrix(vals3);
		m4 = new Matrix(vals4);
	}

	@Test
	public void testPrint() {
		System.out.println(m1);
		System.out.println(m2);
		System.out.println(m3);
		System.out.println(m4);
		System.out.println("Should only see matrices and no memory addresses");
	}
	
	//Tests multiply and isequal
	@Test
	public void testMultiply(){
		Matrix m5 = m1.matMult(m2);
		Matrix m6 = m3.matMult(m4);
		System.out.println("Matrix 5: \n" + m5);
		System.out.println("Matrix 6: \n" + m6);
		double[][] m5Result = {{0.0, 0.0, -4.0}, {1.0, -2.0, 1.0}, {-4.5, 2.5, -3.5}};
		double[][] m6Result = {{5.0, -4.0, 1.0, 0.0}, {-5.0, 6.0, -1.0, -1.0}, {-1.0, 3.0, -2.0, -1.0}, {0.0, 1.0, 1.0, -5.0}};
		if (!m5.isEqual(new Matrix(m5Result)))
			fail("3x3 matMult did not work");
		if (!m6.isEqual(new Matrix(m6Result)))
			fail("4x4 matMult did not work");
	}

}
