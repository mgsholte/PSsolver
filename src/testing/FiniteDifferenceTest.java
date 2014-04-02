package testing;

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
	private Function V1;
	private Function V2;
	private FiniteDifferenceSolver s1;
	private FiniteDifferenceSolver s2;

	@Before
	public void setUp() throws Exception {
		d1 = new Domain(-5.0, 5.0, 50); //toy example
		p1 = WellParameters.genDummyParams(d1); //toy example
		V1 = Function.getZeroFcn(d1); //infinite well
		V2 = new LazyFunction(d1){ //finite well
			public double evalAt(double x) {
				if (x >= -1.0 && x <= 1.0)
					return 0.0;
				else
					return 5.0;
			}
		};
		s1 = new FiniteDifferenceSolver(p1, V1);
		s2 = new FiniteDifferenceSolver(p1, V2);
	}

	@Test
	public void testA0() { //see if the initial setup is correct
		//System.out.println(new SparseTridiag(s3.getDim(), s3.genHOffDiag(), s3.genHDiag(), s3.genHOffDiag()));
		//System.out.println(new SparseTridiag(s4.getDim(), s4.genHOffDiag(), s4.genHDiag(), s4.genHOffDiag()));
	}
	
	@Test
	public void testSolveSystem(){
		Function[] infWellSparsePsis = s1.solveSystem(5);
		Function[] finWellSparsePsis = s2.solveSystem(5);
		int i = 0;
		System.out.println("*****Infinite Well*****");
		System.out.println("VInf = " + Arrays.toString(V1.toArray()) + ";");
		for(Function psi : infWellSparsePsis){
			System.out.println("EInf" + i + " = " + s1.getEigenvalues()[i] + ";");
			System.out.println("PsiInf" + i + "=  " + Arrays.toString(psi.toArray()) + ";");
			i++;
		}
		System.out.println("\n*****Finite Well*****");
		System.out.println("VFin = " + Arrays.toString(V2.toArray()) + ";");
		i = 0;
		for(Function psi : finWellSparsePsis){
			System.out.println("EFin" + i + " = " + s2.getEigenvalues()[i] + ";");
			System.out.println("PsiFin" + i + "=  " + Arrays.toString(psi.toArray()) + ";");
			i++;
		}
	}

}
