package testing;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import utils.Domain;

public class DomainTest {

	private Domain superDom, subDom;
	
	@Before
	public void setUp() throws Exception {
		superDom = new Domain(-10, 10, 0.5);
		subDom = new Domain(-5, 4, 1.0);
	}

	@Test
	public void testPartition() {
		List<Domain> subDoms = Domain.partition(superDom, -5, 0, 1, 2, 8);
		assert(subDoms.size() == 6);
		for(Domain d : subDoms) {
			assert(superDom.hasSubDomain(d));
		}
	}

	@Test
	public void testIsSubDomain() {
		assert(superDom.hasSubDomain(subDom));
		assert(superDom.hasSubDomain(new Domain(0, 10, 0.5)));
		
		assert(!superDom.hasSubDomain(new Domain(-20, 0, 1)));
		assert(!superDom.hasSubDomain(new Domain(-20, 21, 1)));
		assert(!superDom.hasSubDomain(new Domain(0, 30, 1)));
		assert(!superDom.hasSubDomain(new Domain(0, 10, 0.1)));
	}

	@Test
	public void testIterator() {
		List<Double> elems = new LinkedList<>();
		for(double d : new Domain(-1, 1, 0.2))
			elems.add(d);
		System.out.println(elems);
	}

	@Test
	public void testGetNumPoints() {
		assert(superDom.getNumPoints() == 41);
		assert(subDom.getNumPoints() == 10);
	}

}
