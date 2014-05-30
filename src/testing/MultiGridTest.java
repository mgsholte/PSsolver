package testing;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import utils.RestrictableDomain;

public class MultiGridTest {
	
	double lb, ub;
	int maxN;
	int[] validNs;
	RestrictableDomain d;

	@Before
	public void setUp() throws Exception {
		lb = -50; ub = 50;
		maxN = 10_000;
		// build array of all possible valid N values for the given max N
		validNs = new int[(int) Math.round(Math.log(maxN)/Math.log(2))];
		for(int i = 0, powof2 = 2; powof2 < maxN; ++i, powof2 *= 2) {
			validNs[i] = powof2 + 1;
		}
	}

	@Test
	public void testRestrictableDomain() {
		// test for success on valid N
		for(int N : validNs) {
			d = new RestrictableDomain(lb, ub, N);
		}
		// test for failure on some bad N
		Random rng = new Random();
		int N = 2;
		for(int i = 0; i < 1000; ++i) {
			try {
				d = new RestrictableDomain(lb, ub, N);
			} catch(IllegalArgumentException e) {
				// get next key, but ensure it is not one of the valid N values
				do {
					N = rng.nextInt(maxN);
				} while(Arrays.binarySearch(validNs, N) >= 0);
				continue;
			}
			fail("Allowed contruction of an invalid domain. It had "+N+" points");
		}
	}

	@Test
	public void testRestrict() {
		d = new RestrictableDomain(lb, ub, validNs[validNs.length-1]);
		do {
			System.out.println("number of points in the domain is: "+d.getNumPoints());
			if (d.getNumPoints() < 10) {
				System.out.println("the points are:");
				for(double x : d) {
					System.out.print(x+", ");
				}
				System.out.println();
			}
		} while(d.restrict());
	}

}
