/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

/*
 * AlgoLinePointVector.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoLinePointVector extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPoint P; // input
    private GeoVector v; // input
    private GeoLine g; // output       

    /** Creates new AlgoJoinPoints */
    AlgoLinePointVector(
        Construction cons,
        String label,
        GeoPoint P,
        GeoVector v) {
        super(cons);
        this.P = P;
        this.v = v;
        g = new GeoLine(cons);
        g.setStartPoint(P);
        setInputOutput(); // for AlgoElement

        // compute line through P, Q
        compute();
        g.setLabel(label);
    }

    String getClassName() {
        return "AlgoLinePointVector";
    }

    // for AlgoElement
    void setInputOutput() {
        input = new GeoElement[2];
        input[0] = P;
        input[1] = v;

        output = new GeoElement[1];
        output[0] = g;
        setDependencies(); // done by AlgoElement
    }

    GeoLine getLine() {
        return g;
    }
    GeoPoint getP() {
        return P;
    }
    GeoVector getv() {
        return v;
    }

    // calc the line g through P and Q    
    final void compute() {
        // g = cross(P, v)
        GeoVec3D.lineThroughPointVector(P, v, g);
    }

    final public String toString() {
        StringBuffer sb = new StringBuffer();
        if (!app.isReverseLanguage()) { //FKH 20040906
            sb.append(app.getPlain("LineThrough"));
            sb.append(" ");
            sb.append(P.getLabel());
            sb.append(" ");
        }
        sb.append(app.getPlain("withDirection"));
        sb.append(" ");
        sb.append(v.getLabel());
        if (app.isReverseLanguage()) { //FKH 20040906
            sb.append(" ");
            sb.append(app.getPlain("LineThrough"));
            sb.append(" ");
            sb.append(P.getLabel());
        }
        return sb.toString();
    }

}
