/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoCircleTwoPoints.java
 *
 * Created on 15. November 2001, 21:37
 */

package geogebra.geogebramobile.client.kernel;

import geogebra.geogebramobile.client.euclidian.EuclidianConstants;
import geogebra.geogebramobile.client.kernel.kernelND.GeoQuadricND;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoCircleTwoPoints extends AlgoSphereNDTwoPoints {

     public AlgoCircleTwoPoints(
        Construction cons,
        GeoPoint M,
        GeoPoint P) {
        super(cons,M,P);
    }
    
    AlgoCircleTwoPoints(
            Construction cons,
            String label,
            GeoPoint M,
            GeoPoint P) {
         super(cons, label,M, P);
    }
    
    protected GeoQuadricND createSphereND(Construction cons){
    	GeoConic circle = new GeoConic(cons);
        circle.addPointOnConic((GeoPoint) getP()); //TODO do this in AlgoSphereNDTwoPoints
        return circle;
    }

    public String getClassName() {
        return "AlgoCircleTwoPoints";
    }

    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_CIRCLE_TWO_POINTS;
    }
    
    
    public GeoConic getCircle() {
        return (GeoConic) getSphereND();
    }
    /*
    GeoPoint getM() {
        return M;
    }
    GeoPoint getP() {
        return P;
    }
    */

    // compute circle with midpoint M and radius r
    /*
    protected final void compute() {
        circle.setCircle(M, P);
    }
    */

    final public String toString() {

        return bApp.getPlain("CircleThroughAwithCenterB",
        		((GeoElement) getP()).getLabel(),
        		((GeoElement) getM()).getLabel());

    }
}