package geogebra.geogebramobile.client.kernel.commands;

import geogebra.geogebramobile.client.kernel.GeoElement;
import geogebra.geogebramobile.client.kernel.GeoList;
import geogebra.geogebramobile.client.kernel.Kernel;

class CmdCovariance extends CmdOneOrTwoListsFunction {

	public CmdCovariance(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.Covariance(a, b);
	}

	final protected GeoElement doCommand(String a, GeoList b, GeoList c)
	{
		return kernel.Covariance(a, b, c);
	}


}
