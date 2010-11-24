package geogebra.geogebramobile.client.kernel.commands;

import geogebra.geogebramobile.client.kernel.GeoElement;
import geogebra.geogebramobile.client.kernel.GeoList;
import geogebra.geogebramobile.client.kernel.Kernel;

class CmdSXY extends CmdOneOrTwoListsFunction {

	public CmdSXY(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.SXY(a, b);
	}

	final protected GeoElement doCommand(String a, GeoList b, GeoList c)
	{
		return kernel.SXY(a, b, c);
	}

}
