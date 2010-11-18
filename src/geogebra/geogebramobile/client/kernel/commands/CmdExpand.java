package geogebra.geogebramobile.client.kernel.commands;

import geogebra.geogebramobile.client.kernel.CasEvaluableFunction;
import geogebra.geogebramobile.client.kernel.GeoElement;
import geogebra.geogebramobile.client.kernel.GeoFunction;
import geogebra.geogebramobile.client.kernel.GeoFunctionNVar;
import geogebra.geogebramobile.client.kernel.Kernel;
import geogebra.geogebramobile.client.kernel.arithmetic.Command;
import geogebra.geogebramobile.client.main.MyError;

class CmdExpand extends CommandProcessor {
	
	public CmdExpand (Kernel kernel) {
		super(kernel);
	}
	
final public GeoElement[] process(Command c) throws MyError {
     int n = c.getArgumentNumber();
     boolean[] ok = new boolean[n];
     GeoElement[] arg;
     arg = resArgs(c);
     
     switch (n) {
         case 1 :             
             if (ok[0] = (arg[0] instanceof CasEvaluableFunction)) {
	                 GeoElement[] ret =
	                 { kernel.Expand(c.getLabel(), (CasEvaluableFunction) arg[0] )};
	             return ret;                
	         }    
              else
            	 throw argErr(app, c.getName(), arg[0]);         
			 
	     // more than one argument
         default :
            	 throw argNumErr(app, c.getName(), n);
     }
 }    
}
