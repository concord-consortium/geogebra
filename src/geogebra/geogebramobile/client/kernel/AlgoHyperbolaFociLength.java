/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoHyperbolaFociLength.java
 *
 * Created on 15. November 2001, 21:37
 */

package geogebra.geogebramobile.client.kernel;

import geogebra.geogebramobile.client.kernel.arithmetic.NumberValue;


/**
 * Hyperbola for given foci and first semi-axis length
 * @author  Markus
 * @version 
 */
public class AlgoHyperbolaFociLength extends AlgoConicFociLength {       

    AlgoHyperbolaFociLength(
        Construction cons,
        String label,
        GeoPoint A,
        GeoPoint B,
        NumberValue a) {
        super(cons, label, A, B, a);
    }

    protected String getClassName() {
        return "AlgoHyperbolaFociLength";
    }

	final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return bApp.getPlain("HyperbolaWithFociABandFirstAxisLengthC",A.getLabel(),B.getLabel(),a.toGeoElement().getLabel()); 
    }
}
