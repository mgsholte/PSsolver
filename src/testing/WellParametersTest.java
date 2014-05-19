package testing;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import utils.ParameterReadException;
import utils.WellParameters;

public class WellParametersTest {

	@Before
	public void setUp() throws Exception {
		WellParameters.regenDefaults();
	}

	@Test
	public void test() {
		try {
			WellParameters params = new WellParameters("well_params_test.kvp");
			assertEquals(1e-6, params.getTolerance(), 0.00001);
		} catch (ParameterReadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
