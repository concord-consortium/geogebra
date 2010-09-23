/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */


package org.geogebra.ggjsviewer.client.kernel;


/**
 * 
 * @author Michael
 * @version
 */
public class GeoButton extends GeoElement implements AbsoluteScreenLocateable {			

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean buttonFixed = false;
	
	public GeoButton(Construction c) {
		super(c);			
		setEuclidianVisible(true);
	}

	public String getClassName() {
		return "GeoButton";
	}
	
    protected String getTypeString() {
		return "Button";
	}
    
    public int getGeoClassType() {
    	return GEO_CLASS_BUTTON;
    }
    
	public GeoElement copy() {
		return this;
	}
	
	public boolean isGeoButton() {
		return true;
	}

	public void resolveVariables() {     
    }
		
	public boolean showInEuclidianView() {
		return true;
	}

	public boolean showInAlgebraView() {		
		return false;
	}
	
	public boolean isFixable() {
		return true;
	}

	public void set(GeoElement geo) {
	}

	final public void setUndefined() {
	}
	
	final public void setDefined() {
	}

	final public boolean isDefined() {
		return true;
	}			
	
	// dummy implementation of mode
	final public void setMode(int mode) {
	}

	final public int getMode() {
		return -1;
	}
	
	final public String toValueString() {
		return "";
	}
	
	final public String toString() {
		StringBuilder sbToString = getSbToString();
		sbToString.setLength(0);
		sbToString.append(label);
		return sbToString.toString();
	}
	
	private StringBuilder sbToString;
	private StringBuilder getSbToString() {
		if (sbToString == null)
			sbToString = new StringBuilder();
		return sbToString;
	}

	public boolean isVectorValue() {
		return false;
	}

	public boolean isPolynomialInstance() {
		return false;
	}

	public boolean isTextValue() {
		return false;
	}

	

	public double getRealWorldLocX() {
		return 0;
	}

	public double getRealWorldLocY() {		
		return 0;
	}

	public boolean isAbsoluteScreenLocActive() {		
		return true;
	}
	public boolean isAbsoluteScreenLocateable() {
		return true;
	}

	public void setAbsoluteScreenLoc(int x, int y) {		
		if (buttonFixed) return;
		
		labelOffsetX = x;
		labelOffsetY = y;		
	}

	public int getAbsoluteScreenLocX() {	
		return labelOffsetX;
	}

	public int getAbsoluteScreenLocY() {		
		return labelOffsetY;
	}

	public void setAbsoluteScreenLocActive(boolean flag) {				
	}

	public void setRealWorldLoc(double x, double y) {				
	}

	public final boolean isButtonFixed() {
		return buttonFixed;
	}

	public final void setButtonFixed(boolean buttonFixed) {
		this.buttonFixed = buttonFixed;
	}
	
	public boolean isTextField() {
		return false;
	}
	
    // Michael Borcherds 2008-04-30
	final public boolean isEqual(GeoElement geo) {
		return false;
	}
	
	public boolean isVector3DValue() {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * Returns whether the value (e.g. equation) should be shown
	 * as part of the label description
	 */
	final public boolean isLabelValueShowable() {
		return false;
	}

	@Override
	public String getXML() {
		// TODO Auto-generated method stub
		return null;
	}


}