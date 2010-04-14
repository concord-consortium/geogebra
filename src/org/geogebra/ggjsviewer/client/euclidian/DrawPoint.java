/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * DrawPoint.java
 *
 * Created on 11. Oktober 2001, 23:59
 */

package org.geogebra.ggjsviewer.client.euclidian;

import org.geogebra.ggjsviewer.client.kernel.AlgoElement;
import org.geogebra.ggjsviewer.client.kernel.GeoElement;
import org.geogebra.ggjsviewer.client.kernel.GeoLine;
import org.geogebra.ggjsviewer.client.kernel.GeoPoint;
import org.geogebra.ggjsviewer.client.kernel.gawt.Ellipse2D;
import org.geogebra.ggjsviewer.client.kernel.gawt.GeneralPath;
import org.geogebra.ggjsviewer.client.kernel.gawt.Line2D;
import org.geogebra.ggjsviewer.client.kernel.gawt.Rectangle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.widgetideas.graphics.client.Color;





/**
 *
 * @author  Markus
 * @version 
 */
public final class DrawPoint extends Drawable {
	 	
	private  int HIGHLIGHT_OFFSET, SELECTION_OFFSET;
	
	private static int SELECTION_DIAMETER_MIN = 20;
	       
    private GeoPoint P;    
    
	private int diameter, hightlightDiameter, selDiameter, pointSize;
    boolean isVisible, labelVisible;   
    // for dot and selection
	private Ellipse2D.Double circle = new Ellipse2D.Double();
	private Ellipse2D.Double circleHighlight = new Ellipse2D.Double();
	private Ellipse2D.Double circleSel = new Ellipse2D.Double();
	private Line2D.Double line1, line2, line3, line4;// for cross
	GeneralPath gp = null;
    
    //private static BasicStroke borderStroke = EuclidianView.getDefaultStroke();
    //private static BasicStroke [] crossStrokes = new BasicStroke[10];
    
    /** Creates new DrawPoint */
    public DrawPoint(EuclidianView view, GeoPoint P) {      
    	this.view = view;          
        this.P = P;
        geo = P;
        
        //crossStrokes[1] = new BasicStroke(1f);

        update();
    }
    
    final public void update() {   
    	
    	if (gp != null) gp.reset(); // stop trace being left when (filled diamond) point moved
    	
        isVisible = geo.isEuclidianVisible();       				 
    	// still needs updating if it's being traced to the spreadsheet
        if (!isVisible && !P.getSpreadsheetTrace()) return;
		labelVisible = geo.isLabelVisible();
        		
        // compute lower left corner of bounding box
	    double [] coords = new double[2];
        P.getInhomCoords(coords);                    
        
        // convert to screen
		view.toScreenCoords(coords);	
		
		// point outside screen?
        if (coords[0] > view.getOffsetWidth() + P.pointSize || coords[0] < -P.pointSize ||
        	coords[1] > view.getOffsetHeight() + P.pointSize || coords[1] < -P.pointSize)  
        {
        	GWT.log(String.valueOf(coords[0] > view.getOffsetWidth()+P.pointSize)+" "+String.valueOf(coords[0])+" "+String.valueOf(view.getOffsetWidth()));
        	GWT.log(String.valueOf(coords[0] < -P.pointSize)+" "+String.valueOf(coords[0])+" "+String.valueOf(P.pointSize));
        	GWT.log(String.valueOf(coords[1] > view.getOffsetHeight() + P.pointSize)+" "+String.valueOf(coords[1])+" "+String.valueOf(view.getOffsetHeight()));
        	GWT.log(String.valueOf(coords[1] < -P.pointSize)+" "+String.valueOf(coords[1])+" "+String.valueOf(P.pointSize));
        	isVisible = false;
        	// don't return here to make sure that getBounds() works for offscreen points too
        }
        
        if (pointSize != P.pointSize) {
        	pointSize = P.pointSize;
			diameter = 2 * pointSize;
			HIGHLIGHT_OFFSET = pointSize / 2 + 1;
			//HIGHLIGHT_OFFSET = pointSize / 2 + 1;
			hightlightDiameter =  diameter + 2 * HIGHLIGHT_OFFSET ;	
			
			selDiameter = hightlightDiameter;
			
			if (selDiameter < SELECTION_DIAMETER_MIN)
				selDiameter = SELECTION_DIAMETER_MIN;
			
			SELECTION_OFFSET = (selDiameter - diameter) / 2;

        }        
         		
        double xUL = coords[0] - pointSize;
        double yUL = coords[1] - pointSize;   

    	
    	// Florian Sonner 2008-07-17
    	int pointStyle = P.getPointStyle();
    	
    	if(pointStyle == -1)
    		pointStyle = view.pointStyle;
    	
    	double root3over2;
    	
        switch (pointStyle) {	       		        
    	case EuclidianView.POINT_STYLE_FILLED_DIAMOND:        		
    	    double xR = coords[0] + pointSize;        		
    		double yB = coords[1] + pointSize;
    		
    		if (gp == null) {
    			gp = new GeneralPath();
    		}        		
    		gp.moveTo((float)(xUL+xR)/2, (float)yUL);
    		gp.lineTo((float)xUL, (float)(yB + yUL)/2);
    		gp.lineTo((float)(xUL+xR)/2, (float)yB);
    		gp.lineTo((float)xR, (float)(yB + yUL)/2);
    		gp.closePath();
    		break;
    		
    	case EuclidianView.POINT_STYLE_TRIANGLE_SOUTH:        		
    	case EuclidianView.POINT_STYLE_TRIANGLE_NORTH:
    		
    		double direction = 1.0;
    		if (pointStyle == EuclidianView.POINT_STYLE_TRIANGLE_NORTH)
    			direction = -1.0;
    		
    		if (gp == null) {
    			gp = new GeneralPath();
    		}        		
    		root3over2 = Math.sqrt(3.0) / 2.0;
    		gp.moveTo((float)coords[0], (float)(coords[1] + direction * pointSize));
    		gp.lineTo((float)(coords[0] + pointSize * root3over2), (float)(coords[1] - direction * pointSize/2));
    		gp.lineTo((float)(coords[0] - pointSize * root3over2), (float)(coords[1] - direction * pointSize/2));
    		gp.lineTo((float)coords[0], (float)(coords[1] + direction * pointSize));
    		gp.closePath();
    		break;
    		
    	case EuclidianView.POINT_STYLE_TRIANGLE_EAST:        		
    	case EuclidianView.POINT_STYLE_TRIANGLE_WEST:
    		
    		direction = 1.0;
    		if (pointStyle == EuclidianView.POINT_STYLE_TRIANGLE_WEST)
    			direction = -1.0;
    		
    		if (gp == null) {
    			gp = new GeneralPath();
    		}     
    		root3over2 = Math.sqrt(3.0) / 2.0;   		
    		gp.moveTo((float)(coords[0] + direction * pointSize), (float)coords[1]);
    		gp.lineTo((float)(coords[0] - direction * pointSize/2), (float)(coords[1] + pointSize * root3over2));
    		gp.lineTo((float)(coords[0] - direction * pointSize/2), (float)(coords[1] - pointSize * root3over2));
    		gp.lineTo((float)(coords[0] + direction * pointSize), (float)coords[1]);
    		gp.closePath();
    		break;
    		
    	case EuclidianView.POINT_STYLE_EMPTY_DIAMOND:        		
    	    xR = coords[0] + pointSize;        		
    		yB = coords[1] + pointSize;
    		
    		if (line1 == null) {
    			line1 = new Line2D.Double();
    			line2 = new Line2D.Double();
    		}        		
    		if (line3 == null) {
    			line3 = new Line2D.Double();
    			line4 = new Line2D.Double();
    		}        		
    		line1.setLine((xUL+xR)/2, yUL, xUL, (yB + yUL)/2);
    		line2.setLine(xUL, (yB + yUL)/2, (xUL+xR)/2, yB);
    		line3.setLine((xUL+xR)/2, yB, xR, (yB + yUL)/2);
    		line4.setLine(xR, (yB + yUL)/2, (xUL+xR)/2, yUL);
    		break;
    		        	
    	case EuclidianView.POINT_STYLE_PLUS:        		
    	    xR = coords[0] + pointSize;        		
    		yB = coords[1] + pointSize;
    		
    		if (line1 == null) {
    			line1 = new Line2D.Double();
    			line2 = new Line2D.Double();
    		}        		
    		line1.setLine((xUL+xR)/2, yUL, (xUL+xR)/2, yB);
    		line2.setLine(xUL, (yB + yUL)/2, xR, (yB + yUL)/2);
    		break;
    		        	
    	case EuclidianView.POINT_STYLE_CROSS:        		
    	    xR = coords[0] + pointSize;        		
    		yB = coords[1] + pointSize;
    		
    		if (line1 == null) {
    			line1 = new Line2D.Double();
    			line2 = new Line2D.Double();
    		}        		
    		line1.setLine(xUL, yUL, xR, yB);
    		line2.setLine(xUL, yB, xR, yUL); 
    		break;
    		        	
        	case EuclidianView.POINT_STYLE_CIRCLE:
        		break;
        		
        	// case EuclidianView.POINT_STYLE_DOT:
        	//default:			        		        			        		    
        }    
        
        // circle might be needed at least for tracing
        circle.setFrame(xUL, yUL, diameter, diameter);
        
        // selection area
        circleHighlight.setFrame(xUL - HIGHLIGHT_OFFSET, 
				yUL - HIGHLIGHT_OFFSET, hightlightDiameter, hightlightDiameter);
		
        circleSel.setFrame(xUL - SELECTION_OFFSET, 
				yUL - SELECTION_OFFSET, selDiameter, selDiameter);
		
        //AGif (P.spreadsheetTrace) recordToSpreadsheet(P); 
        
		//AGif (P == view.getEuclidianController().recordObject)
		    //AGrecordToSpreadsheet(P);

        
		// draw trace
		if (P.trace) {
			isTracing = true;
		//AG	Graphics2D g2 = view.getBackgroundGraphics();
			//AGif (g2 != null) drawTrace(g2);
		} else {
			if (isTracing) {
				isTracing = false;
			//AG	view.updateBackground();
			}
		}
		
		if (isVisible && labelVisible) {      
			labelDesc = geo.getLabelDescription();
			xLabel = (int) Math.round(coords[0] + 4);
			yLabel = (int)  Math.round(yUL - pointSize);    
			addLabelOffset(true);
		}    				
    }
    
    private Drawable drawable;
    
    private void drawClippedSection(GeoElement geo/* Graphics2D g2*/) {
   	
    	switch (geo.getGeoClassType()) {
    	case GeoElement.GEO_CLASS_LINE:
    		drawable = new DrawLine(view, (GeoLine)geo);
    		break;
    	/*AGcase GeoElement.GEO_CLASS_SEGMENT:
    		drawable = new DrawSegment(view, (GeoSegment)geo);
    		break;
    	case GeoElement.GEO_CLASS_CONIC:
    		drawable = new DrawConic(view, (GeoConic)geo);
    		break;
    	case GeoElement.GEO_CLASS_FUNCTION:
    		drawable = new DrawParametricCurve(view, (GeoFunction)geo);
    		break;
    	case GeoElement.GEO_CLASS_AXIS:
    		drawable = null;
    		break;
    	case GeoElement.GEO_CLASS_CONICPART:
    		drawable = new DrawConicPart(view, (GeoConicPart)geo);
    		break;
    		
    		default:
    			drawable = null;
    			Application.debug("unsupported type for restriced drawing "+geo.getClass()+"");
			*/
    	}
   		
    	if (drawable != null) {
    		double [] coords = new double[2];
            P.getInhomCoords(coords);                    
    		
            view.toScreenCoords(coords);
            
    		Ellipse2D.Float circle = new Ellipse2D.Float((int)coords[0] - 30, (int)coords[1] - 30, 60, 60);
    		//AG MUST BE IMPLEMENTEDg2.clip((Shape)circle);
			geo.forceEuclidianVisible(true);
			drawable.update();
			drawable.draw(/*g2*/);
			geo.forceEuclidianVisible(false);
			//AGg2.setClip(null);
    	}
    	
    }

    final public void draw(/*AGGraphics2D g2*/) {   
        if (isVisible) { 
        	if (geo.doHighlighting()) {           
    		 	//AGg2.setPaint(geo.getSelColor());		
    		 	//AGg2.fill(circleHighlight);
        		view.beginPath();
        		view.arc(circleHighlight.x, circleHighlight.y, circleHighlight.height/2, 0, Math.PI*2, false);
        		view.setFillStyle(new Color(geo.getSelColor().getRed(),geo.getSelColor().getBlue(),geo.getSelColor().getGreen(),geo.getAlphaValue()));
        		view.fill();
            }
        	
        	
        	 // option "show trimmed intersecting lines" 
        	 if (geo.getShowTrimmedIntersectionLines()) {
	        	AlgoElement algo = geo.getParentAlgorithm();
	        	
	        	/*AGif ( algo instanceof AlgoIntersectAbstract ){
	        		GeoElement[] geos = algo.getInput();
	        		drawClippedSection(geos[0], g2);
	        		if (geos.length > 1)
	        			drawClippedSection(geos[1], g2);
	        	} */
        	 }
        	
        	// Florian Sonner 2008-07-17
        	int pointStyle = P.getPointStyle();
        	
        	if(pointStyle == -1)
        		pointStyle = view.pointStyle;
        	
            switch (pointStyle) {
        	case EuclidianView.POINT_STYLE_PLUS:            		                     
        	case EuclidianView.POINT_STYLE_CROSS:            		                     
         		// draw cross like: X or +     
               /* g2.setPaint(geo.getObjectColor());
                g2.setStroke(getCrossStroke(pointSize));            
                g2.draw(line1);                              
                g2.draw(line2);*/  
        		view.setStrokeStyle(new Color(geo.getObjectColor().getRed(),geo.getObjectColor().getBlue(),geo.getObjectColor().getGreen(),geo.getObjectColor().getAlpha()));
        		view.beginPath();
        		view.moveTo(line1.x1, line2.x2);
        		view.lineTo(line1.x2, line1.y2);
        		view.moveTo(line2.x1, line2.y2);
        		view.lineTo(line2.x2, line2.y2);
        		break;
        		
        	case EuclidianView.POINT_STYLE_EMPTY_DIAMOND:            		                     
         		// draw diamond    
               /* g2.setPaint(geo.getObjectColor());
                g2.setStroke(getCrossStroke(pointSize));            
                g2.draw(line1);                              
                g2.draw(line2);             		
                g2.draw(line3);                              
                g2.draw(line4);*/   
        		view.setStrokeStyle(new Color(geo.getObjectColor().getRed(),geo.getObjectColor().getBlue(),geo.getObjectColor().getGreen(),geo.getObjectColor().getAlpha()));
        		view.moveTo(line1.x1, line1.y1);
        		view.lineTo(line1.x2, line1.y2);
        		view.moveTo(line2.x1, line2.y1);
        		view.lineTo(line2.x2, line2.y2);
        		view.moveTo(line2.x1, line2.y1);
        		view.lineTo(line3.x2, line3.y2);
        		view.moveTo(line3.x1, line3.y1);
        		view.lineTo(line4.x2, line4.y2);
        		view.moveTo(line4.x1, line4.y1);
        		break;
        		
        	case EuclidianView.POINT_STYLE_FILLED_DIAMOND:            		                     
        	case EuclidianView.POINT_STYLE_TRIANGLE_NORTH:            		                     
        	case EuclidianView.POINT_STYLE_TRIANGLE_SOUTH:            		                     
        	case EuclidianView.POINT_STYLE_TRIANGLE_EAST:            		                     
        	case EuclidianView.POINT_STYLE_TRIANGLE_WEST:            		                     
         		// draw diamond    
               /* g2.setPaint(geo.getObjectColor());
                g2.setStroke(getCrossStroke(pointSize));  
                drawWithValueStrokePure(gp, g2);
				g2.fill(gp);*/
        		view.setStrokeStyle(new Color(geo.getObjectColor().getRed(),geo.getObjectColor().getBlue(),geo.getObjectColor().getGreen(),geo.getObjectColor().getAlpha()));
        		//Must be implemented AG!
        		break;
        		

        		
            	case EuclidianView.POINT_STYLE_CIRCLE:
            		// draw a circle            		
        	/*		g2.setPaint(geo.getObjectColor());	
        			g2.setStroke(getCrossStroke(pointSize));
        			g2.draw(circle);*/
            		view.setStrokeStyle(new Color(geo.getObjectColor().getRed(),geo.getObjectColor().getGreen(),geo.getObjectColor().getBlue(),geo.getObjectColor().getAlpha()));
            		view.beginPath();
            		view.arc(circle.x+circle.width/2, circle.y+circle.height/2, circle.height/2,  0, Math.PI*2, false);
            		view.stroke();
           		break;
            	
           		// case EuclidianView.POINT_STYLE_CIRCLE:
            	default:
            		// draw a dot            			
            		
            		view.beginPath();
            		view.arc(circle.x+circle.width/2, circle.y+circle.height/2, circle.height/2,  0, Math.PI*2, false);                    
                    // black stroke   
            		view.setFillStyle(new Color(geo.getObjectColor().getRed(),geo.getObjectColor().getGreen(),geo.getObjectColor().getBlue()/*geo.getObjectColor().getAlpha()*/));
                    view.setStrokeStyle(Color.BLACK);
                   //AG dont forget g2.setStroke(borderStroke);
                    view.stroke();
                    view.fill();
        			//g2.draw(circle);          			
            }    		        	
                  

            // label   
           /* if (labelVisible) {
				g2.setFont(view.fontPoint);
				g2.setPaint(geo.getLabelColor());
				drawLabel(g2);
            }*/                         
        }
    }
    
    final void drawTrace(/*Graphics2D g2*/) {
    	//AGg2.setPaint(geo.getObjectColor());
		view.setStrokeStyle(new Color(geo.getObjectColor().getRed(),geo.getObjectColor().getBlue(),geo.getObjectColor().getGreen(),geo.getObjectColor().getAlpha()));

    	
    	// Florian Sonner 2008-07-17
    	int pointStyle = P.getPointStyle();
    	
		switch (pointStyle) {
	     	case EuclidianView.POINT_STYLE_CIRCLE:
	 		/*	g2.setStroke(getCrossStroke(pointSize));
	 			g2.draw(circle);*/
        	view.setStrokeStyle(new Color(geo.getObjectColor().getRed(),geo.getObjectColor().getBlue(),geo.getObjectColor().getGreen(),geo.getObjectColor().getAlpha()));
        	view.beginPath();
    		view.arc(circle.x, circle.y, circle.height/2,  0, Math.PI*2, false);                         	
            view.stroke();
	    		break;
	     	
	     	case EuclidianView.POINT_STYLE_CROSS:
	     	default: // case EuclidianView.POINT_STYLE_CIRCLE:	     		
	 			//AGg2.fill(circle);
	     		view.arc(circle.x, circle.y, circle.height/2,  0, Math.PI*2, false);                         	
	            view.fill();
	     }    		       
    }

    /**
     * was this object clicked at? (mouse pointer
     * location (x,y) in screen coords)
     */
    final public boolean hit(int x, int y) {
        return circleSel.contains(x, y);        
    }
    
    final public boolean isInside(Rectangle rect) {
    	return rect.contains(circleSel.getBounds());  
    }
    
    /**
	 * Returns the bounding box of this DrawPoint in screen coordinates.	 
	 */
	final public Rectangle getBounds() {				
		// return selection circle's bounding box
		if (!geo.isEuclidianVisible())
			return null;
		else 
			return circleSel.getBounds();		
	}
    
    final public GeoElement getGeoElement() {
        return geo;
    }    
    
    final public void setGeoElement(GeoElement geo) {
        this.geo = geo;
    } 
    
    /*
     * pointSize can be more than 9 (set from JavaScript, SetPointSize[])
     */
   /* final private BasicStroke getCrossStroke(int pointSize) {
    	
    	if (pointSize > 9)
    		return new BasicStroke(pointSize/2f); 
    	
		if (crossStrokes[pointSize] == null)
			crossStrokes[pointSize] = new BasicStroke(pointSize/2f); 
		
		return crossStrokes[pointSize];

    }*/

}


