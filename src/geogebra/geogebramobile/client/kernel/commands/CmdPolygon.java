package geogebra.geogebramobile.client.kernel.commands;

import geogebra.geogebramobile.client.kernel.GeoElement;
import geogebra.geogebramobile.client.kernel.GeoList;
import geogebra.geogebramobile.client.kernel.GeoPoint;
import geogebra.geogebramobile.client.kernel.GeoPolygon;
import geogebra.geogebramobile.client.kernel.Kernel;
import geogebra.geogebramobile.client.kernel.arithmetic.Command;
import geogebra.geogebramobile.client.kernel.arithmetic.NumberValue;
import geogebra.geogebramobile.client.main.MyError;





/*
 * Polygon[ <GeoPoint>, ..., <GeoPoint> ]
 * Polygon[ <GeoPoint>, <GeoPoint>, <Number>] for regular polygon
 */
public class CmdPolygon extends CommandProcessor {
	
	public CmdPolygon(Kernel kernel) {
		super(kernel);
	}
	
public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    GeoElement[] arg;

    arg = resArgs(c);
    switch (n) {
    	//G.Sturr 2010-3-14
		case 1:
		if (arg[0].isGeoList())
			return kernel.Polygon(c.getLabels(), (GeoList) arg[0]);
		//END G.Sturr
		
    	case 3:        
        // regular polygon
        if (arg[0].isGeoPoint() && 
	        arg[1].isGeoPoint() &&
	        arg[2].isNumberValue())
				return kernel.RegularPolygon(c.getLabels(), (GeoPoint) arg[0], (GeoPoint) arg[1], (NumberValue) arg[2]);		
        
      //G.Sturr 2010-2-19
        // polygon operation
        if (arg[0].isGeoPolygon() && 
	        arg[1].isGeoPolygon() &&
	        arg[2].isNumberValue())
				return kernel.PolygonOperation(c.getLabels(), (GeoPolygon) arg[0], (GeoPolygon) arg[1],(NumberValue) arg[2]);		
       //END G.Sturr 
        
        
        default:
			// polygon for given points
	        GeoPoint[] points = new GeoPoint[n];
	        // check arguments
	        if(points.length==0) throw argErr(app,c.getName(),"");	//Ulven 16.03.10: To get syntax dialogue
	        for (int i = 0; i < n; i++) {
	            if (!(arg[i].isGeoPoint()))
					throw argErr(app, c.getName(), arg[i]);
				else {
	                points[i] = (GeoPoint) arg[i];
	            }
	        }
	        // everything ok
	        return kernel.Polygon(c.getLabels(), points);
		}	
}
}