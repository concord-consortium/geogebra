/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;



/**
 * Integral of a function (GeoFunction)
 * 
 * @author Markus Hohenwarter
 */
public class AlgoIntegral extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoFunction f; // input
    private GeoFunction g; // output g = integral(f(x), x)      

    public AlgoIntegral(Construction cons, String label, GeoFunction f) {
        super(cons);
        this.f = f;        
        g = (GeoFunction) f.copyInternal(cons);  // output
        setInputOutput(); // for AlgoElement        
        compute();
        g.setLabel(label);
    }
    
    protected String getClassName() {
        return "AlgoIntegral";
    }   

    // for AlgoElement
    void setInputOutput() {
        input = new GeoElement[1];
        input[0] = f;

        output = new GeoElement[1];
        output[0] = g;
        setDependencies(); // done by AlgoElement
    }

    public GeoFunction getIntegral() {
        return g;
    }
    
    final void compute() {  
        g.setIntegral(f);
    }

    final public String toString() {
        StringBuffer sb = new StringBuffer();
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        sb.append(app.getPlain("IntegralOfA",f.getLabel()));
        
        /*
         if(!app.isReverseLanguage()){//FKH 20040906
            sb.append(app.getPlain("Integral"));
            sb.append(' ');
            sb.append(app.getPlain("of"));
            sb.append(' ');
        }
        sb.append(f.getLabel());
        if(app.isReverseLanguage()){//FKH 20040906
            sb.append(' ');
            sb.append(app.getPlain("of"));
            sb.append(' ');
            sb.append(app.getPlain("Integral"));
        }*/
        
        if (!f.isIndependent()) { // show the symbolic representation too
            sb.append(": ");
            sb.append(g.getLabel());
            sb.append("(x) = ");
            sb.append(g.toSymbolicString());
        }
        

        return sb.toString();
    }

}
