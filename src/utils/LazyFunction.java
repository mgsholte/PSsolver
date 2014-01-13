package utils;

public abstract class LazyFunction extends Function {

	private final Function composedOp;
	
	public LazyFunction(Domain domain) {
		this(domain, null);
	}
	
	protected LazyFunction(Domain domain, Function f) {
		super(domain);
		composedOp = f;
	}
	
	protected final double evalComposedAt(double x) {
		return composedOp.evalAt(x);
	}

	@Override
	public Function add(final Function f) {
		testDomain(f);
		return new LazyFunction(domain, this) {
			@Override
			public double evalAt(double x) {
				return evalComposedAt(x) + f.evalAt(x);
			}			
		};
	}
	
	@Override
	public Function square() {
		return new LazyFunction(domain, this) {
			@Override
			public double evalAt(double x) {
				return evalComposedAt(x)*evalComposedAt(x);
			}
		};
	}

}
