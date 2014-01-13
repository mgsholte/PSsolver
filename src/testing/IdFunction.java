package testing;

import utils.Domain;
import utils.LazyFunction;

class IdFunction extends LazyFunction {

	public IdFunction(Domain domain) {
		super(domain);
	}

	@Override
	public double evalAt(double x) {
		return x;
	}

}
