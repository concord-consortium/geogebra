/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoAxisFirstLength.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.geogebramobile.client.kernel;



/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoAxisFirstLength extends AlgoElement {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoConic c;  // input
    private GeoNumeric num;     // output                  
    
    AlgoAxisFirstLength(Construction cons, String label,GeoConic c) {      
        super(cons);  
        this.c = c;                                                              
        num = new GeoNumeric(cons);                
        setInputOutput(); // for AlgoElement                
        compute();              
        num.setLabel(label);            
    }   
    
    public String getClassName() {
        return "AlgoAxisFirstLength";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = c;        
        
        output = new GeoElement[1];
        output[0] = num;
        setDependencies(); // done by AlgoElement
    }    
    
    GeoNumeric getLength() { return num; }    
    GeoConic getConic() { return c; }        
    
    // set excentricity
    protected final void compute() {  
        switch (c.type) {
            case GeoConic.CONIC_CIRCLE:                               
            case GeoConic.CONIC_HYPERBOLA:
            case GeoConic.CONIC_ELLIPSE:
                num.setValue(c.halfAxes[0]);
                break;
                            
            default:
                num.setUndefined();            
        }        
    }
    
    final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return bApp.getPlain("FirstAxisLengthOfA",c.getLabel());

    }
}
