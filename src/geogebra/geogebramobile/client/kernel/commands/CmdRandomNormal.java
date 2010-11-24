package geogebra.geogebramobile.client.kernel.commands;

import geogebra.geogebramobile.client.kernel.GeoElement;
import geogebra.geogebramobile.client.kernel.Kernel;
import geogebra.geogebramobile.client.kernel.arithmetic.NumberValue;

/*
 * RandomNormal[ <Number>, <Number> ]
 */
class CmdRandomNormal extends CmdTwoNumFunction {

	public CmdRandomNormal(Kernel kernel) {
		super(kernel);
	}

	protected GeoElement doCommand(String a, NumberValue b, NumberValue c)
	{
		return kernel.RandomNormal(a, b, c);
	}

}
