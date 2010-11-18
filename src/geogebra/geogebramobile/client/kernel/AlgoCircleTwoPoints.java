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

    protected String getClassName() {
        return "AlgoCircleTwoPoints";
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
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return bApp.getPlain("CircleThroughAwithCenterB",
        		((GeoElement) getM()).getLabel(),
        		((GeoElement) getP()).getLabel());

    }
}