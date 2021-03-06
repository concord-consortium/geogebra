/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.main.Application;
/**
 * Polynomial remainder
 * 
 * @author Michael Borcherds
 */
public class AlgoPolynomialMod extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoFunction f1, f2; // input
    private GeoFunction g; // output     
    
    private StringBuilder sb = new StringBuilder();
   
    public AlgoPolynomialMod(Construction cons, String label, GeoFunction f1, GeoFunction f2) {
    	super(cons);
        this.f1 = f1;            	
        this.f2 = f2;            	
    	
        g = new GeoFunction(cons);                
        setInputOutput(); // for AlgoElement        
        compute();
        g.setLabel(label);
    }
    
    public String getClassName() {
        return "AlgoPolynomialMod";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = f1;
        input[1] = f2;

        output = new GeoElement[1];
        output[0] = g;
        setDependencies(); // done by AlgoElement
    }

    public GeoFunction getResult() {
        return g;
    }

    protected final void compute() {       
        if (!f1.isDefined() || !f2.isDefined()) {
        	g.setUndefined();
        	return;
        }    

        // MathPiper version       
	    String functionIn1 = f1.getFormulaString(ExpressionNode.STRING_TYPE_MATH_PIPER, true);
	    String functionIn2 = f2.getFormulaString(ExpressionNode.STRING_TYPE_MATH_PIPER, true);

        /*
		String functionIn = f.getFunction().
		getExpression().getCASstring(ExpressionNode.STRING_TYPE_MathPiper, false);*/
		//Application.debug(functionIn);

	    sb.setLength(0);
        sb.append("Modulo(");
        sb.append(functionIn1);
        sb.append(",");
        sb.append(functionIn2);
        sb.append(")");
		String functionOut = kernel.evaluateMathPiper(sb.toString());
		
		//Application.debug("Mod input:"+functionIn1+" "+functionIn2);
		//Application.debug("Mod output:"+functionOut);
		
		boolean MathPiperError=false;
		
		if (functionOut == null || functionOut.length()==0) MathPiperError=true; // MathPiper error
		
		else if (functionOut.length()>7)
			if (functionOut.startsWith("Modulo(") || // MathPiper error
				functionOut.startsWith("Undefined") || // MathPiper error/bug eg Simplify(0.00000000000000001)
				functionOut.startsWith("FWatom(") )  // MathPiper oddity??
				MathPiperError=true;
			

		if (MathPiperError) // MathPiper error
		{
		    g.setUndefined();			
		}
		else
		{
			g.set(kernel.getAlgebraProcessor().evaluateToFunction(functionOut, false));					
		}
		
		g.setDefined(true);	
		
    }
    
    final public String toString() {
    	return getCommandDescription();
    }

}
