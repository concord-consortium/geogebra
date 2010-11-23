package geogebra.geogebramobile.client.kernel.commands;

import geogebra.geogebramobile.client.kernel.GeoElement;
import geogebra.geogebramobile.client.kernel.GeoList;
import geogebra.geogebramobile.client.kernel.Kernel;

/*
 * Reverse[ <List> ]
 * Michael Borcherds 2008-02-16
 */
class CmdReverse extends CmdOneListFunction {

	public CmdReverse(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.Reverse(a, b);
	}


}
