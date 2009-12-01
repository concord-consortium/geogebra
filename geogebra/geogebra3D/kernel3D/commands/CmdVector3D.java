package geogebra3D.kernel3D.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.commands.CmdVector;
import geogebra.main.MyError;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.Kernel3D;



/*
 * Vector[ <GeoPoint3D>, <GeoPoint3D> ] or CmdVector
 */
public class CmdVector3D extends CmdVector {
	
	Kernel3D kernel3D;

	public CmdVector3D(Kernel kernel) {
		super(kernel);
		
		
	}
	
	public CmdVector3D(Kernel3D kernel3D) {
		this( (Kernel) kernel3D);
		this.kernel3D = kernel3D;
		
		
	}	
	
	
	public GeoElement[] process(Command c) throws MyError {	


		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;



		switch (n) {
		case 2 :
			arg = resArgs(c);
			if (arg[0].isGeoElement3D() && arg[1].isGeoElement3D()){
				if ((ok[0] = (arg[0] .isGeoPoint()))
						&& (ok[1] = (arg[1] .isGeoPoint()))) {
					GeoElement[] ret =
					{
							kernel3D.Vector3D(
									c.getLabel(),
									(GeoPoint3D) arg[0],
									(GeoPoint3D) arg[1])};
					return ret;
				} else {
					if (!ok[0])
						throw argErr(app, "Vector", arg[0]);
					else
						throw argErr(app, "Vector", arg[1]);
				}
			}
		default :

		}


		return super.process(c);
	}

}