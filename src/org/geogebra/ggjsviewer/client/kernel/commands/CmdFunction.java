package org.geogebra.ggjsviewer.client.kernel.commands;

import org.geogebra.ggjsviewer.client.kernel.GeoElement;
import org.geogebra.ggjsviewer.client.kernel.GeoFunctionable;
import org.geogebra.ggjsviewer.client.kernel.Kernel;
import org.geogebra.ggjsviewer.client.kernel.arithmetic.Command;
import org.geogebra.ggjsviewer.client.kernel.arithmetic.NumberValue;
import org.geogebra.ggjsviewer.client.main.MyError;




/*
 * Function[ <GeoFunction>, <NumberValue>, <NumberValue> ]
 */
public class CmdFunction extends CommandProcessor {

	public CmdFunction (Kernel kernel) {
		super(kernel);
	}

	public  GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg = resArgs(c);

		switch (n) {
		case 3 :            	                
			if ((ok[0] = (arg[0] .isGeoFunctionable()))
					&& (ok[1] = (arg[1] .isNumberValue()))
					&& (ok[2] = (arg[2] .isNumberValue()))) {
				GeoElement[] ret =
				{
						kernel.Function(
								c.getLabel(),
								((GeoFunctionable) arg[0]).getGeoFunction(),
								(NumberValue) arg[1],
								(NumberValue) arg[2])
				};
				return ret;
			}                                
			else {
				if (!ok[0])
					throw argErr(app, "Function", arg[0]);
				else if (!ok[1])
					throw argErr(app, "Function", arg[1]);
				else
					throw argErr(app, "Function", arg[2]);
			}

		default :
			throw argNumErr(app, "Function", n);
		}
	}
}