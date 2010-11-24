package geogebra.geogebramobile.client.kernel.commands;

import geogebra.geogebramobile.client.kernel.GeoElement;
import geogebra.geogebramobile.client.kernel.Kernel;
import geogebra.geogebramobile.client.kernel.arithmetic.NumberValue;

/*
 * Binomial[ <Number>, <Number> ]
 * Michael Borcherds 2008-04-12
 */
class CmdBinomial extends CmdTwoNumFunction {

	public CmdBinomial(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, NumberValue b, NumberValue c)
	{
		return kernel.Binomial(a, b, c);
	}
}
