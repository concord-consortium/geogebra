/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.geogebramobile.client.euclidian;

import geogebra.geogebramobile.client.kernel.ConstructionDefaults;
import geogebra.geogebramobile.client.kernel.GeoElement;
import geogebra.geogebramobile.client.kernel.GeoPoint;
import geogebra.geogebramobile.client.kernel.GeoPolygon;
import geogebra.geogebramobile.client.kernel.gawt.Rectangle;
import geogebra.geogebramobile.client.main.Application;

import java.util.ArrayList;




/**
 *
 * @author  Markus Hohenwarter
 * @version 
 */
public class DrawPolygon extends Drawable
implements Previewable {
   
    private GeoPolygon poly;            
    boolean isVisible, labelVisible;
    
    private GeneralPathClipped gp;
    private double [] coords = new double[2];
	private ArrayList points;              
      
    public DrawPolygon(EuclidianView view, GeoPolygon poly) {
		this.view = view; 
		this.poly = poly;		
		geo = poly;

		update();
    }
    
    /**
     * Creates a new DrawPolygon for preview.     
     */
	DrawPolygon(EuclidianView view, ArrayList points) {
		this.view = view; 
		this.points = points;

		updatePreview();
	} 

	final public void update() {
        isVisible = geo.isEuclidianVisible();
        if (isVisible) { 
			labelVisible = geo.isLabelVisible();       
			updateStrokes(poly);
			
            // build general path for this polygon
			addPointsToPath(poly.getPoints());
			gp.closePath();
        	
        	 // polygon on screen?		
    		if (!gp.intersects(0,0, view.width, view.height)) {				
    			isVisible = false;
            	// don't return here to make sure that getBounds() works for offscreen points too
    		}             
        }
    }
	
	private void addPointsToPath(GeoPoint[] points) {
		if (gp == null)
			gp = new GeneralPathClipped(view);
		else
			gp.reset();
		
		// first point
		points[0].getInhomCoords(coords);
		view.toScreenCoords(coords);			
        gp.moveTo(coords[0], coords[1]);   
		
		// for centroid calculation (needed for label pos)
		double xsum = coords[0];
		double ysum = coords[1];
        
        for (int i=1; i < points.length; i++) {
			points[i].getInhomCoords(coords);
			view.toScreenCoords(coords);	
			if (labelVisible) {
				xsum += coords[0];
				ysum += coords[1];
			}			
        	gp.lineTo(coords[0], coords[1]);                  	
        }

		if (labelVisible) {
			labelDesc = geo.getLabelDescription();  
			xLabel = (int) (xsum / points.length);
			yLabel = (int) (ysum / points.length);
			addLabelOffset();                                       
		}  
	}
        
	final public void draw(/*AGGraphics2D g2*/) {
        if (isVisible) {
        	if (poly.alphaValue > 0.0f) {
				view.setPaint(poly.getFillColor());                       
				view.fill(gp);  				
        	}        	        	
        	//Application.printStacktrace(poly.toString());	
            if (geo.doHighlighting()) {
                view.setPaint(poly.getSelColor());
                view.setStroke(selStroke);            
                view.draw(gp);                
            }        
        	
            // polygons (e.g. in GeoLists) that don't have labeled segments
            // should also draw their border
            else if (!poly.wasInitLabelsCalled() && poly.lineThickness > 0) {
        		 view.setPaint(poly.getObjectColor());
                 view.setStroke(objStroke);            
                 view.draw(gp); 
                 
        	}
        	                   
            if (labelVisible) {
				view.setPaint(poly.getLabelColor());
				view.setFont(view.fontPoint);
				drawLabel(/*AGg2*/);
            }			
        }
    }
    
	final public void updatePreview() {
		int size = points.size();
		isVisible = size > 0;
		
		if (isVisible) { 		
			GeoPoint[] pointsArray = new GeoPoint[size];
			for (int i=0; i < size; i++) {
				pointsArray[i] = (GeoPoint) points.get(i);
			}
			addPointsToPath(pointsArray);								              
		}	
	}
	
	final public void updateMousePos(double xRW, double yRW) {	
		if (isVisible) {
			gp.lineTo(view.toScreenCoordX(xRW), view.toScreenCoordY(yRW));
		}
	}
    
	final public void drawPreview(/*AGGraphics2D g2*/) {
    	if (isVisible) {
			view.setPaint(ConstructionDefaults.colPreviewFill);                       
			view.fill(gp);  			
		  			            						
			view.setPaint(ConstructionDefaults.colPreview);             
			view.setStroke(objStroke);            
			view.draw(gp);
    	}		            	
    }
	
	public void disposePreview() {	
	}
    
	final public boolean hit(int x,int y) {		
       return gp != null && (gp.contains(x, y) || gp.intersects(x-3, y-3, 6, 6));        
    }
	
    final public boolean isInside(Rectangle rect) {
    	return gp != null && rect.contains(gp.getBounds());  
    }
    
    public GeoElement getGeoElement() {
        return geo;
    }    
    
    public void setGeoElement(GeoElement geo) {
        this.geo = geo;
    }
    
    /**
	 * Returns the bounding box of this Drawable in screen coordinates.	 
	 */
	final public Rectangle getBounds() {		
		if (!geo.isDefined() || !geo.isEuclidianVisible())
			return null;
		else 
			return gp.getBounds();	
	}

    
}
