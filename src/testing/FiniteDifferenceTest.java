package testing;

import java.io.File;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import solvers.FiniteDifferenceSolver;
import utils.Domain;
import utils.Function;
import utils.LazyFunction;
import utils.WellParameters;

public class FiniteDifferenceTest {
	
	private Domain d1;
	private WellParameters p1;
	//private Function V1;
	private Function V2;
	//private FiniteDifferenceSolver s1;
	private FiniteDifferenceSolver s2;

	@Before
	public void setUp() throws Exception {
		d1 = new Domain(-250.0, 250.0, 1292); //Maximum number of points that will give a convergent result
		p1 = WellParameters.genDummyParams(d1); //toy example
		//V1 = Function.getZeroFcn(d1); //infinite well
		V2 = new LazyFunction(d1){ //finite well
			public double evalAt(double x) {
				if (x >= -50.0 && x <= 50.0)
					return 0;
				else
					return 0;
			}
		};
		//s1 = new FiniteDifferenceSolver(p1, V1);
		s2 = new FiniteDifferenceSolver(p1, V2);
	}

	@Test
	public void testA0() { //see if the initial setup is correct
		//System.out.println(new SparseTridiag(s3.getDim(), s3.genHOffDiag(), s3.genHDiag(), s3.genHOffDiag()));
		//System.out.println(new SparseTridiag(s4.getDim(), s4.genHOffDiag(), s4.genHDiag(), s4.genHOffDiag()));
	}
	
	@Test
	public void testSolveSystem(){
		//Function[] infWellSparsePsis = s1.solveSystem(5);
		Function[] finWellSparsePsis = s2.solveSystem(15);
		new File("FiniteDifferenceTest.m").delete();
		for(int i = 0; i < finWellSparsePsis.length; i ++){
			SORTest.printMatlab(finWellSparsePsis[i], "Psi" + i + " = ", "FiniteDifferenceTest.m");
		}
		System.out.println(Arrays.toString(s2.getEigenvalues()));

	}

}
