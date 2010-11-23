package geogebra.geogebramobile.client.kernel.commands;

import geogebra.geogebramobile.client.kernel.GeoElement;
import geogebra.geogebramobile.client.kernel.GeoList;
import geogebra.geogebramobile.client.kernel.Kernel;

/*
 * Sort[ <List> ]
 */
class CmdSort extends CmdOneListFunction {

	public CmdSort(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.Sort(a, b);
	}

}
