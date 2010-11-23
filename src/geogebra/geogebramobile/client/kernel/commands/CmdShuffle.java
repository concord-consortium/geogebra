package geogebra.geogebramobile.client.kernel.commands;

import geogebra.geogebramobile.client.kernel.GeoElement;
import geogebra.geogebramobile.client.kernel.GeoList;
import geogebra.geogebramobile.client.kernel.Kernel;

/*
 * Shuffle[ <List> ]
 */
class CmdShuffle extends CmdOneListFunction {

	public CmdShuffle(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.Shuffle(a, b);
	}


}
