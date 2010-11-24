/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.geogebramobile.client.euclidian;

import geogebra.geogebramobile.client.kernel.AlgoIntegralDefinite;
import geogebra.geogebramobile.client.kernel.GeoElement;
import geogebra.geogebramobile.client.kernel.GeoFunction;
import geogebra.geogebramobile.client.kernel.GeoNumeric;
import geogebra.geogebramobile.client.kernel.arithmetic.NumberValue;

//AR import java.awt.Graphics2D;
import geogebra.geogebramobile.client.kernel.gawt.Rectangle;

/**
 * Draws definite Integral of a GeoFunction
 * @author  Markus Hohenwarter
 */
public class DrawIntegral extends Drawable {
   
    private GeoNumeric n;
    private GeoFunction f;
    private NumberValue a, b;
    private GeneralPathClipped gp;
    boolean isVisible, labelVisible;
   
    public DrawIntegral(EuclidianView view, GeoNumeric n) {
    	this.view = view; 	
    	this.n = n;
		geo = n;
		
		n.setDrawable(true);
    	
    	init();
        update();
    }
    
    private void init(){
    	AlgoIntegralDefinite algo = (AlgoIntegralDefinite) n.getDrawAlgorithm();
		f = algo.getFunction();
		a = algo.getA();
		b = algo.getB();
    }

    final public void update() {					
    	isVisible = geo.isEuclidianVisible();
        if (!isVisible) return;
		labelVisible = geo.isLabelVisible();            
		updateStrokes(n);
		if(n.isAlgoMacroOutput())
			init();
        
		// init gp
		double aRW = a.getDouble();
		double bRW = b.getDouble();

		double ax = view.toScreenCoordXd(aRW);
		double bx = view.toScreenCoordXd(bRW);
		float y0 = (float) view.yZero;
				
		// plot definite integral
		if (gp == null)
			gp = new GeneralPathClipped(view);
		else
			gp.reset(); 				
		gp.moveTo(ax, y0); 
		DrawParametricCurve.plotCurve(f, aRW, bRW, view, gp, false, false);
		gp.lineTo(bx, y0);
		gp.lineTo(ax, y0);			
		
//		 gp on screen?		
		if (!gp.intersects(0,0, view.width, view.height)) {				
			isVisible = false;
        	// don't return here to make sure that getBounds() works for offscreen points too
		}

		if (labelVisible) {
			xLabel = (int) Math.round((ax + bx) / 2) - 6;
			yLabel = (int) view.yZero - view.fontSize;
			labelDesc = geo.getLabelDescription();
			addLabelOffset();
		}		
    }
    
	final public void draw(/*AR Graphics2D g2 */) {
        if (isVisible) {        	
            if (geo.doHighlighting()) {
                view.setPaint(n.getSelColor());
                view.setStroke(selStroke);            
                //AR Drawable.drawWithValueStrokePure(gp, g2);
                view.draw(gp);//AR
            }
			//AR fill(g2, gp, true); // fill using default/hatching/image as appropriate
        	if (n.alphaValue > 0.0f) {//AR
				view.setPaint(n.getFillColor());                       
				view.fill(gp);  				
        	}
			
			if (geo.lineThickness > 0) {
				view.setPaint(n.getObjectColor());
				view.setStroke(objStroke);                                   
				//AR Drawable.drawWithValueStrokePure(gp, g2);
				view.draw(gp);//AR
			}
			
            if (labelVisible) {
				view.setFont(view.fontConic);
				view.setPaint(geo.getLabelColor());
				drawLabel(/*ARg2*/);
            }        
        }
    }
    
	final public boolean hit(int x,int y) {  
	       return gp != null && (gp.contains(x, y) || gp.intersects(x-3, y-3, 6, 6));        
    }
	
	final public boolean isInside(Rectangle rect) {  
    	return false;   
    }
	
	/**
	 * Returns the bounding box of this DrawPoint in screen coordinates.	 
	 */
	final public Rectangle getBounds() {		
		if (!geo.isDefined() || !geo.isEuclidianVisible())
			return null;
		else 
			return gp.getBounds();	
	}
    
    public GeoElement getGeoElement() {
        return geo;
    }    
    
    public void setGeoElement(GeoElement geo) {
        this.geo = geo;
    }
}
