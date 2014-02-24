package testing;

import org.junit.Before;
import org.junit.Test;

import solvers.FiniteDifferenceSolver;
import utils.Domain;
import utils.Function;
import utils.GreedyFunction;
import utils.LazyFunction;
import utils.SparseTridiag;
import utils.WellParameters;

public class FiniteDifferenceTest {
	
	private Domain d1;
	private Domain d2;
	private WellParameters p1;
	private WellParameters p2;
	private Function V1;
	private Function V2;
	private Function V3;
	private Function V4;
	private FiniteDifferenceSolver s1;
	private FiniteDifferenceSolver s2;
	private FiniteDifferenceSolver s3;
	private FiniteDifferenceSolver s4;

	@Before
	public void setUp() throws Exception {
		d1 = new Domain(-50.0, 50.0, .1);
		d2 = new Domain(-5.0, 5.0, 1.0);//toy example
		p1 = new WellParameters(d1);
		p2 = new WellParameters(d2);//toy example
		V1 = Function.getZeroFcn(d1);//infinite well
		V2 = new LazyFunction(d1){//finite well
								public double evalAt(double x){
									if (x >= -1.0 || x <= 1.0)
										return 0.0;
									else
										return 1.0;
								}
							};
		V3 = Function.getZeroFcn(d2);//infinite well
        double[] vals = new double[d2.getNumPoints()];
        for (int i = 0; i < vals.length; i++)
            vals[i] = (i < 4 || i >= 6)? 1.0 : 0.0;
        V4 = new GreedyFunction(d2, vals);
		s1 = new FiniteDifferenceSolver(p1, V1);
		s2 = new FiniteDifferenceSolver(p1, V2);
		s3 = new FiniteDifferenceSolver(p2, V3);
		s4 = new FiniteDifferenceSolver(p2, V4);
	}

	@Test
	public void testA0() {//see if the initial setup is correct
		System.out.println(new SparseTridiag(s3.getDim(), s3.genHOffDiag(), s3.genHDiag(), s3.genHOffDiag()));
		System.out.println(new SparseTridiag(s4.getDim(), s4.genHOffDiag(), s4.genHDiag(), s4.genHOffDiag()));
	}

}
