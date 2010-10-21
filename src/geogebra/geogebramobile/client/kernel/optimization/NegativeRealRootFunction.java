package geogebra.geogebramobile.client.kernel.optimization;

import geogebra.geogebramobile.client.kernel.roots.RealRootFunction;


public class NegativeRealRootFunction implements RealRootFunction {

	private RealRootFunction f;
	
	public NegativeRealRootFunction(RealRootFunction f) {
		this.f = f;
	}

	final public double evaluate(double x) {
		return -f.evaluate(x);
	}		

}
