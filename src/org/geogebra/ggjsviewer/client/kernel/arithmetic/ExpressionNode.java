package org.geogebra.ggjsviewer.client.kernel.arithmetic;

import java.util.HashSet;
import java.util.Iterator;

import org.geogebra.ggjsviewer.client.kernel.GeoElement;
import org.geogebra.ggjsviewer.client.kernel.Kernel;


public class ExpressionNode implements ExpressionNodeConstants {
	 public boolean leaf = false;
	 public ExpressionValue left, right; 
	 public Kernel kernel;
	final public GeoElement [] getGeoElementVariables() {
	    	HashSet varset = getVariables();
	        if (varset == null) return null;
	        Iterator i = varset.iterator();        
	        GeoElement [] ret = new GeoElement[varset.size()];
	        int j=0;
	        while (i.hasNext()) {
	            ret[j++] = (GeoElement) i.next();
	        }                
	        return ret;
	    }  
	
	 final public HashSet getVariables() {   
	        if (leaf) return left.getVariables();
	        
	        HashSet leftVars = left.getVariables();
	        HashSet rightVars = right.getVariables();        
	        if (leftVars == null) {
	        	return rightVars;        		
	        } 
	        else if (rightVars == null) {
	        	return leftVars;
	        }
	        else {        	
	        	leftVars.addAll(rightVars);        	
	        	return leftVars;
	        }        	
	    }
	 
	 public ExpressionValue evaluate() {   
	    	//AGreturn kernel.getExpressionNodeEvaluator().evaluate(this);
		 return null;
	    }
}
