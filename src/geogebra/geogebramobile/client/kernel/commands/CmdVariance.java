package geogebra.geogebramobile.client.kernel.commands;

import geogebra.geogebramobile.client.kernel.GeoElement;
import geogebra.geogebramobile.client.kernel.GeoList;
import geogebra.geogebramobile.client.kernel.Kernel;

/*
 * Variance[ list ]
 * adapted from CmdSum by Michael Borcherds 2008-02-16
 */
class CmdVariance extends CmdOneListFunction {

	public CmdVariance(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.Variance(a, b);
	}
	

}
