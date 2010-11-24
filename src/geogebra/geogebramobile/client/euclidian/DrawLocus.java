/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.geogebramobile.client.euclidian;

import geogebra.geogebramobile.client.kernel.GeoElement;
import geogebra.geogebramobile.client.kernel.GeoLocus;
import geogebra.geogebramobile.client.kernel.MyPoint;
import geogebra.geogebramobile.client.kernel.gawt.Rectangle;
//AR import java.awt.Graphics2D;
import java.util.ArrayList;


public final class DrawLocus extends Drawable {
	
 
	private GeoLocus locus;    	
    
    boolean isVisible, labelVisible;   
	private GeneralPathClipped gp;	
	private double [] lastPointCoords;
	    
    public DrawLocus(EuclidianView view, GeoLocus locus) {      
    	this.view = view;          
        this.locus = locus;
        geo = locus;                          
   
        update();
    }
    
    final public void update() {    	
        isVisible = geo.isEuclidianVisible(); 
        if (!isVisible) return;	
            
		buildGeneralPath(locus.getMyPointList());
		
		 // line on screen?		
		if (!gp.intersects(0, 0, view.width, view.height)) {				
			isVisible = false;
        	// don't return here to make sure that getBounds() works for offscreen points too
		}		
		updateStrokes(geo);
				
		labelVisible = geo.isLabelVisible();
		if (labelVisible) {								
			labelDesc = geo.getLabelDescription();			
			xLabel = (int) (lastPointCoords[0] - 5);
			yLabel = (int) (lastPointCoords[1] + 4 + view.fontSize);   
			addLabelOffset();           
		}
		
		// draw trace
		if (locus.trace) {
			isTracing = true;
			//AR Graphics2D g2 = view.getBackgroundGraphics();
			//AR if (g2 != null) drawTrace(g2);
			drawTrace();
		} else {
			if (isTracing) {
				isTracing = false;
				view.updateBackground();
			}
		}			

   }
    
	final void drawTrace(/*AR Graphics2D g2*/) {
    	if (isVisible) {
    		view.setPaint(geo.getObjectColor());
    		view.setStroke(objStroke);
            //AR Drawable.drawWithValueStrokePure(gp, g2);
    		view.draw(gp);//AR
    	}
	}
    

    
    private void buildGeneralPath(ArrayList pointList) {    
    	if (gp == null)
    		gp = new GeneralPathClipped(view);
    	else
    		gp.reset();     	  
    	double [] coords = new double[2];
  	
    	int size = pointList.size();
		for (int i=0; i < size; i++) {
			MyPoint p = (MyPoint) pointList.get(i);    		
    		coords[0] = p.x;
    		coords[1] = p.y;
    		view.toScreenCoords(coords);      		    		
    		
    		if (p.lineTo) {
				gp.lineTo(coords[0], coords[1]);					
			} else {					
				gp.moveTo(coords[0], coords[1]);	   						
			}           	 	    		
        }
    	
    	lastPointCoords = coords;    	
    }      

    final public void draw(/*AR Graphics2D g2*/) {   
    	if (isVisible) {    			    	
            if (geo.doHighlighting()) {
                // draw locus              
                view.setPaint(geo.getSelColor());
                view.setStroke(selStroke);
                //AR Drawable.drawWithValueStrokePure(gp, g2);
                view.draw(gp);
            }      
        	
            // draw locus         
            view.setPaint(geo.getObjectColor());
            view.setStroke(objStroke);
            //AR Drawable.drawWithValueStrokePure(gp, g2);
            view.draw(gp);
                        
            // label
            if (labelVisible) {
				view.setFont(view.fontLine);
				view.setPaint(geo.getLabelColor());	//AR view.setColor(geo.getLabelColor());
				drawLabel(/*AR g2*/);
            }                        
        }
    }     
    	
    
   
    /**
     * was this object clicked at? (mouse pointer
     * location (x,y) in screen coords)
     */
    final public boolean hit(int x, int y) {

    	if (gp == null) return false; // hasn't been drawn yet (hidden)

    	if (strokedShape == null) {
			strokedShape = objStroke.createStrokedShape(gp);
		}    		
		return strokedShape.intersects(x-3,y-3,6,6); 
    	
    	/*
        return gp.intersects(x-2,y-2,4,4)
				&& !gp.contains(x-2,y-2,4,4);
				*/        
    }
    
    final public boolean isInside(Rectangle rect) {
    	return rect.contains(gp.getBounds());  
    }
    
    final public GeoElement getGeoElement() {
        return geo;
    }    
    
    final public void setGeoElement(GeoElement geo) {
        this.geo = geo;
    } 
    
	/**
	 * Returns the bounding box of this DrawPoint in screen coordinates.	 
	 */
	final public Rectangle getBounds() {		
		if (!geo.isDefined() || !locus.isClosedPath() || !geo.isEuclidianVisible())
			return null;
		else 
			return gp.getBounds();	
	}

}


