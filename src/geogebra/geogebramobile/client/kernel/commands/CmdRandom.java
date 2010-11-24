package geogebra.geogebramobile.client.kernel.commands;

import geogebra.geogebramobile.client.kernel.GeoElement;
import geogebra.geogebramobile.client.kernel.Kernel;
import geogebra.geogebramobile.client.kernel.arithmetic.NumberValue;

/*
 * Random[ <Number>, <Number> ]
 */
class CmdRandom extends CmdTwoNumFunction {

	public CmdRandom(Kernel kernel) {
		super(kernel);
	}

	protected GeoElement doCommand(String a, NumberValue b, NumberValue c)
	{
		return kernel.Random(a, b, c);
	}

}
