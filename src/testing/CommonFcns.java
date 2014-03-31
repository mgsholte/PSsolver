package testing;

import utils.Domain;
import utils.Function;
import utils.LazyFunction;

/**
 * Easily obtain common, useful functions
 * @author mark
 *
 */
final class CommonFcns {
	
	static public Function getId(Domain d) {
		return new LazyFunction(d) {
			@Override
			public double evalAt(double x) {
				return x;
			}
		};
	}
	
	static public Function genLinear(Domain d, final double a, final double b) {
		return new LazyFunction(d) {
			@Override
			public double evalAt(double x) {
				return a*x + b;
			}
		};
	}

}
