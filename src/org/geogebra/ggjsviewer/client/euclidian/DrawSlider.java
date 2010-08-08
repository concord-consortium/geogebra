/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * DrawSlider: draws a slider to change a number continously
 */

package org.geogebra.ggjsviewer.client.euclidian;

import org.geogebra.ggjsviewer.client.kernel.GeoElement;
import org.geogebra.ggjsviewer.client.kernel.GeoNumeric;
import org.geogebra.ggjsviewer.client.kernel.GeoPoint;
import org.geogebra.ggjsviewer.client.kernel.gawt.Line2D;
import org.geogebra.ggjsviewer.client.kernel.gawt.Rectangle;

/**
 *
 * @author  Markus Hohenwarter
 * @version 
 */
public class DrawSlider extends Drawable {
   
    private GeoNumeric number;
     
    boolean isVisible, labelVisible;
    
    private double [] coordsRW = new double[2];
    private double [] coordsScreen = new double[2];
    private Line2D.Double line = new Line2D.Double();   
    private GeoPoint geoPoint;
    private DrawPoint drawPoint;
    
    public DrawSlider(EuclidianView view, GeoNumeric number) {    	
        this.view = view;
        this.number = number;
        geo = number;              
        
        // create point for slider
        geoPoint = new GeoPoint(view.getKernel().getConstruction());        
        drawPoint = new DrawPoint(view, geoPoint);
        drawPoint.setGeoElement(number);
        
        update();
    }

    final public void update() {
        isVisible = geo.isEuclidianVisible();
        if (isVisible) {         	
        	double widthRW;
        	double widthScreen;
        	boolean horizontal = number.isSliderHorizontal();
        		
        	// start point of horizontal line for slider
        	if (number.isAbsoluteScreenLocActive()) {        		
        		coordsScreen[0] = number.getSliderX();
        		coordsScreen[1] = number.getSliderY();
        		coordsRW[0] = view.toRealWorldCoordX(coordsScreen[0]);
        		coordsRW[1] = view.toRealWorldCoordY(coordsScreen[1]);
        		
        		widthScreen = number.getSliderWidth();
        		widthRW = horizontal ? widthScreen * view.invXscale :
        					widthScreen * view.invYscale;
        	} else {
        		coordsRW[0] = number.getSliderX();
        		coordsRW[1] = number.getSliderY();
        		coordsScreen[0] = view.toScreenCoordXd(coordsRW[0]);
        		coordsScreen[1] = view.toScreenCoordYd(coordsRW[1]);
        		
        		widthRW = number.getSliderWidth();
        		widthScreen = horizontal ? widthRW * view.xscale :
        						widthRW * view.yscale;
        	}
        	
        	//  point on slider that moves
        	double min = number.getIntervalMin();
        	double max = number.getIntervalMax();
        	double param =  (number.getValue() - min) / (max - min);           	
        	geoPoint.pointSize = 2 + (number.lineThickness+1) / 3;
        	labelVisible = geo.isLabelVisible();
        	geoPoint.setLabelVisible(labelVisible);
        	
        	// horiztonal slider
        	if (horizontal) {        	
        		geoPoint.setCoords(coordsRW[0] + widthRW * param, coordsRW[1], 1.0);  	
	        	drawPoint.update();      	        	
	        	if (labelVisible) {
	        		drawPoint.xLabel -= 15;
	        		drawPoint.yLabel -= 5;
	        	}
	        	
	        	// horizontal line      	    	    		
	        	line.setLine(coordsScreen[0], coordsScreen[1], coordsScreen[0]+ widthScreen, coordsScreen[1]);
        	} 
        	// vertical slider
        	else {
        		geoPoint.setCoords(coordsRW[0], coordsRW[1] + widthRW * param, 1.0);  	        		        	
	        	drawPoint.update();      	        	
	        	if (labelVisible) {
	        		drawPoint.xLabel += 5;
	        		drawPoint.yLabel += 2*geoPoint.pointSize + 4;	        		  
	        	}
	        	
	        	// vertical line      	    	    		
	        	line.setLine(coordsScreen[0], coordsScreen[1], coordsScreen[0], coordsScreen[1] - widthScreen);
        	}	        		                 	        	
                                     
            updateStrokes(number);                
        }
        
		/*AG no spreadsheetif (number == view.getEuclidianController().recordObject)
		    recordToSpreadsheet(number);
		
		if (number.getSpreadsheetTrace())
		    recordToSpreadsheet(number);
		    */

    }
    
    final public void draw(/*AGGraphics2D g2*/) {
        if (isVisible) {                        
        	// horizontal line
            view.setPaint(geo.getObjectColor());             
            view.setStroke(objStroke); 
            //AGview.draw(line);
            view.beginPath();
    		view.moveTo(line.x1, line.y1);
    		view.lineTo(line.x2, line.y2);
    		view.stroke();
                
            // point 
            drawPoint.draw(/*AGg2*/);                  
        }
    }
    
    final public boolean hit(int x,int y) {        
        return hitPoint(x, y) || hitSlider(x, y);      
    }
    
    final public boolean isInside(Rectangle rect) {
    	return drawPoint.isInside(rect); 
    }
    
    final public boolean hitPoint(int x,int y) {        
        return drawPoint.hit(x, y);      
    }
    
    public boolean hitLabel(int x, int y) {
    	return drawPoint.hitLabel(x, y);    	
    }
    
    public boolean hitSlider(int x, int y) {
    	return !number.isSliderFixed() && line.intersects(x-2, y-2, 4,4);    	
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
		if (!geo.isDefined() || ((GeoNumeric)geo).isAbsoluteScreenLocActive() || !geo.isEuclidianVisible())
			return null;
		else 
			return line.getBounds();	
	}
    
}
