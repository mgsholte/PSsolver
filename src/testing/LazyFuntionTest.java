package testing;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.Domain;
import utils.Function;


public class LazyFuntionTest {

	private Domain dom;
	
	@Before
	public void setUp() throws Exception {
		dom = new Domain(-5, 5, 0.5);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testEvalAt() {
		IdFunction id = new IdFunction(dom);
		
		for(double x : id.getDomain()) {
			if (id.evalAt(x) != x)
				fail("id(x) == x, but we had id(x) == "+id.evalAt(x)+", and x == "+x);
		}
	}

	@Test
	public void testAdd() {
		IdFunction id = new IdFunction(dom);
		Function twoX = id.add(id);
		for(double x : twoX.getDomain()) {
			if (twoX.evalAt(x) != x+x)
				fail("2*x =="+(x+x)+", but twoX(x) =="+twoX.evalAt(x));
		}
		
		
	}

}
