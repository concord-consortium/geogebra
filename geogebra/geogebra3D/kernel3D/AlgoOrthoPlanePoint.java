/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/


package geogebra3D.kernel3D;

import geogebra.Matrix.GgbMatrix4x4;
import geogebra.Matrix.GgbVector;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.main.Application;


/**
 * Compute a plane through a point and orthogonal to a line (or segment, ...)
 *
 * @author  matthieu
 * @version 
 */
public class AlgoOrthoPlanePoint extends AlgoElement3D {

 
	private GeoPoint3D point; // input
    private GeoCoordSys cs; // input
    private GeoPlane3D plane; // output       


    AlgoOrthoPlanePoint(Construction cons, String label, GeoPoint3D point, GeoCoordSys cs) {
        super(cons);
        this.point = point;
        this.cs = cs;
        plane = new GeoPlane3D(cons);
        //g.setStartPoint(P);
        
        setInputOutput(new GeoElement[] {point, (GeoElement) cs}, new GeoElement[] {plane});

        // compute plane 
        compute();
        plane.setLabel(label);
    }

    protected String getClassName() {
        return "AlgoOrthoPlanePoint";
    }


    GeoPlane3D getPlane() {
        return plane;
    }


  
    protected final void compute() {
		//recompute the coord sys
		plane.resetCoordSys();
		
		//if cs has "no" direction vector, set undefined and return
		if (cs.getCoordSys().getMatrix().getColumn(1).equalsForKernel(0, Kernel.STANDARD_PRECISION)){
			plane.setUndefined();
			return;
		}
			
		
		//creates an ortho matrix with coord sys direction vector
		GgbMatrix4x4 m = new GgbMatrix4x4(cs.getCoordSys().getMatrix());

		//Application.debug(m.toString());

		plane.addPointToCoordSys(point.getCoords(), true, true);
		//TODO addVectorToCoordSys
		plane.addPointToCoordSys((GgbVector) point.getCoords().add(m.getVy()), true, true);
		plane.addPointToCoordSys((GgbVector) point.getCoords().add(m.getVz()), true, true);
		//cs direction for normal vector
		plane.addPointToCoordSys((GgbVector) point.getCoords().add(m.getVx()), true, true);
		

		

        
    }

    final public String toString() {
    	return app.getPlain("PlaneThroughAPerpendicularToB",point.getLabel(),((GeoElement) cs).getLabel());

    }
}