package testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import utils.Domain;

public class DomainTest {

	private Domain superDom, subDom, domN;
	
	@Before
	public void setUp() throws Exception {
		superDom = new Domain(-10, 10, 0.5);
		subDom = new Domain(-5, 4, 1.0);
		domN = new Domain(-6, 8, 25);
	}

	@Test
	public void testPartition() {
		List<Domain> subDoms = Domain.partition(superDom, -5.0, 0.0, 1.0, 2.0, 8.0);
		assertEquals(5, subDoms.size());
		for(Domain d : subDoms) {
			assertTrue(superDom.hasAsSubDomain(d));
		}
	}

	@Test
	public void testHasAsSubDomain() {
		assertTrue(superDom.hasAsSubDomain(subDom));
		assertTrue(superDom.hasAsSubDomain(new Domain(0, 10, 0.5)));
		
		assertFalse(superDom.hasAsSubDomain(new Domain(-20, 0, 1.0)));
		assertFalse(superDom.hasAsSubDomain(new Domain(-20, 21, 1.0)));
		assertFalse(superDom.hasAsSubDomain(new Domain(0, 30, 1.0)));
		assertFalse(superDom.hasAsSubDomain(new Domain(0, 10, 0.1)));
	}

	@Test
	public void testIterator() {
		List<Double> elems = new LinkedList<>();
		for(double d : new Domain(-1, 1, 0.2))
			elems.add(d);
		
		assertEquals(11, elems.size());
		System.out.println(elems);
		
	}
	
	@Test 
	public void testConstructors() {
		assertEquals(-6.0, domN.getLB(), 0.0001);
		assertTrue(domN.getNumPoints() == 25);
		System.out.println(domN);
		
		// insure both constructors produce (roughly) the same results with equivalent input
		Domain dom1 = new Domain(0, 1, 2);
		Domain dom2 = new Domain(0, 1, 1.0);
		Domain dom3 = new Domain(-1, 1, 0.2);
		
		assertEquals(-1.0, dom3.getLB(), 0.0001);
		assertEquals(1.0, dom3.getUB(), 0.0001);
		assertEquals(0.2, dom3.getDx(), 0.0001);
		assertTrue(dom3.getNumPoints() == 11);
		
		assertTrue(dom1.getNumPoints() == dom2.getNumPoints());
		assertEquals(dom1.getDx(), dom2.getDx(), 0.001);
		
		System.out.println(dom1);
		System.out.println(dom2);
	}

	@Test
	public void testGetNumPoints() {
		assertEquals(41, superDom.getNumPoints());
		assertEquals(10, subDom.getNumPoints());
		assertEquals(11, (new Domain(-1,1,0.2)).getNumPoints());
		
		List<Double> elems = new LinkedList<>();
		for(double d : domN)
			elems.add(d);
		System.out.println(elems);
		assertTrue(domN.getNumPoints() == elems.size());
	}

}
