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
	public Function scale(final double factor) {
		return new LazyFunction(domain, this) {
			@Override
			public double evalAt(double x) {
				return factor*evalComposedAt(x);
			}
		};
	}
	
	@Override
	public Function negate() {
		return new LazyFunction(domain, this) {
			@Override
			public double evalAt(double x) {
				return -evalComposedAt(x);
			}
		};
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

	@Override
	public double getNormalizingFactor() {
		final double dx = domain.getDx();
		double norm = 0.0, tmp;
		for(double x : domain) {
			tmp = evalAt(x);
			norm += tmp*tmp*dx;
		}
		return 1.0/Math.sqrt(norm);
	}

	@Override
	public Function offset(){
		double[] newVals = this.toArray();
		return new GreedyFunction(domain, newVals).offset();
	}
}
