package geogebra.geogebramobile.client.kernel.commands;

import geogebra.geogebramobile.client.kernel.GeoElement;
import geogebra.geogebramobile.client.kernel.GeoList;
import geogebra.geogebramobile.client.kernel.Kernel;
import geogebra.geogebramobile.client.kernel.arithmetic.Command;
import geogebra.geogebramobile.client.main.MyError;

class CmdMeanX extends CmdOneOrTwoListsFunction {

	public CmdMeanX(Kernel kernel) {
		super(kernel);
	}
	
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoList()) {
				GeoElement[] ret = { 
						doCommand(c.getLabel(),
						(GeoList) arg[0]) };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);
		
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

	final protected GeoElement doCommand(String a, GeoList b)
	{
		return kernel.MeanX(a, b);
	}

	final protected GeoElement doCommand(String a, GeoList b, GeoList c)
	{
		throw argErr(app, a, b);
	}


}
