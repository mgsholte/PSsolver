package testing;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

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
