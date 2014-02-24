package testing;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import utils.Domain;
import utils.Function;
import utils.LazyFunction;


public class LazyFuntionTest {

	private Domain dom;
	
	@Before
	public void setUp() throws Exception {
		dom = new Domain(-5, 5, 0.5);
	}

	@Test
	public void testEvalAt() {
		IdFunction id = new IdFunction(dom);
		
		for(double x : id.getDomain()) {
			assertEquals(x, id.evalAt(x), 0.0001);
		}
		
<<<<<<< HEAD
		
		LazyFunction V2 = new LazyFunction(dom){//finite well					
									@Override
									public double evalAt(double x){
										if (x >= -1.0 || x <= 1.0)
											return 0.0;
										else
											return 1.0;
									}
								};
		for(double x : V2.getDomain()){
			if (x >= -1.0 || x <= 1.0){
				if (V2.evalAt(x) != 0.0)
					fail("V2(x) = 0, but we had V2(x) == " + V2.evalAt(x));
			}
			if (V2.evalAt(x) != 1.0)
				fail("V2(x) = 1, but we had V2(x) == " + V2.evalAt(x));
		}
=======
		Function sqWell = new LazyFunction(dom) {
			@Override
			public double evalAt(double x) {
				return (x < -2.5 || x >= 2.5) 
						? 1.0
						: 0.0;
			}
		};
		
		assertEquals(sqWell.evalAt(-3), 1.0, .0001);
		assertEquals(sqWell.evalAt(-1), 0.0, .0001);
		assertEquals(sqWell.evalAt(1.2), 0.0, .0001);
		assertEquals(sqWell.evalAt(2.7), 1.0, .0001);
		
		for(double x = -5; x < 5; x += .1) {
			double trueVal = x < -2.5 || x >= 2.5 
					? 1.0
					: 0.0;
			assertEquals(trueVal, sqWell.evalAt(x), 0.0001);
		}
		
>>>>>>> 036c8ac7e8cb35e0f769dd097ebb7ccc16869a82
	}

	@Test
	public void testAdd() {
		IdFunction id = new IdFunction(dom);
		Function twoX = id.add(id);
		for(double x : twoX.getDomain()) {
			assertEquals(x+x, twoX.evalAt(x), 0.0001);
		}
	}
	
	@Test
	public void testToArray() {
		Function square = new LazyFunction(dom) {
			@Override
			public double evalAt(double x) {
				return x*x;
			}
		};
		double[] squares = square.toArray();
		
		for (int i = 0; i < 5; ++i) {
			double x = dom.getValAtIndex(i);
			assertEquals(x*x, squares[i], 0.0001);
		}
	}
	
	

}
