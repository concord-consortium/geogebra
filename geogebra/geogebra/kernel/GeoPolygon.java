/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.util.MyMath;

import java.awt.Color;
import java.util.HashSet;
import java.util.Locale;

/**
 * Polygon through given points
 * 
 * @author Markus Hohenwarter
 */
public class GeoPolygon extends GeoElement implements NumberValue, Path, Region {
	
	private static final long serialVersionUID = 1L;

	public static final int POLYGON_MAX_POINTS = 100;
	
	protected GeoPointInterface [] points;
	protected GeoSegmentInterface [] segments;
	
	protected double area;
	private boolean defined = false;		
	
	/** common constructor for 2D.
	 * @param c the construction
	 * @param points vertices 
	 */
	public GeoPolygon(Construction c, GeoPointInterface[] points) {
		this(c,points,null,true);
	}
	
	/** common constructor for 3D.
	 * @param c the construction
	 * @param points vertices 
	 * @param cs for 3D stuff : 2D coord sys
	 * @param createSegments says if the polygon has to creates its edges
	 */	
	public GeoPolygon(Construction c, GeoPointInterface[] points, GeoElement cs, boolean createSegments) {
		super(c);
		setPoints(points, cs, createSegments);
		setLabelVisible(false);
		setAlphaValue(ConstructionDefaults.DEFAULT_POLYGON_ALPHA);
	}
	
	/** for 3D stuff (unused here)
	 * @param cs GeoCoordSys2D
	 */
	public void setCoordSys(GeoElement cs) {
		
	}
	
	
	

	protected String getClassName() {
		return "GeoPolygon";
	}
	
    protected String getTypeString() {
    	if (points == null) 
    		return "Polygon";
    	
    	switch (points.length) {
    		case 3:
    			return "Triangle";
    			
    		case 4:
    			return "Quadrilateral";
    			
    		case 5:
    			return "Pentagon";
    		
    		case 6:
    			return "Hexagon";
    		
    		default:
    			return "Polygon";    	
    	}    	    			
	}
    
    public int getGeoClassType() {
    	return GEO_CLASS_POLYGON;
    }
	
    
    
    
    /**
     * set the vertices to points
     * @param points the vertices
     */
    public void setPoints(GeoPointInterface [] points) {
    	setPoints(points,null,true);
    }

    	
    /**
     * set the vertices to points (cs is only used for 3D stuff)
     * @param points the vertices
     * @param cs used for 3D stuff
     * @param createSegments says if the polygon has to creates its edges
     */
    public void setPoints(GeoPointInterface [] points, GeoElement cs, boolean createSegments) {
		this.points = points;
		setCoordSys(cs);
		
		if (createSegments)
			updateSegments();
		
//		if (points != null) {
//		Application.debug("*** " + this + " *****************");
//        Application.debug("POINTS: " + points.length);
//        for (int i=0; i < points.length; i++) {
//			Application.debug(" " + i + ": " + points[i]);		     	        	     	
//		}   
//        Application.debug("SEGMENTS: " + segments.length);
//        for (int i=0; i < segments.length; i++) {
//			Application.debug(" " + i + ": " + segments[i]);		     	        	     	
//		}  
//        Application.debug("********************");
//		}
	}    
    
    
    
    ///////////////////////////////
    // ggb3D 2009-03-08 - start
    
	/** return number for points
	 * @return number for points
	 */
	public int getNumPoints(){
		return points.length;
	}
	
	
	/**
	 * return the x-coordinate of the i-th vertex
	 * @param i number of vertex
	 * @return the x-coordinate 
	 */
	public double getPointX(int i){
		return getPoint(i).inhomX;
	}
	
	/**
	 * return the y-coordinate of the i-th vertex
	 * @param i number of vertex
	 * @return the y-coordinate 
	 */
	public double getPointY(int i){
		return getPoint(i).inhomY;
	}

	
    // ggb3D 2009-03-08 - end
    ///////////////////////////////
	


    /**
     * Inits the labels of this polygon, its segments and its points.
     * labels[0] for polygon itself, labels[1..n] for segments,
     * labels[n+1..2n-2] for points (only used for regular polygon)
     * @param labels
     */
    public void initLabels(String [] labels) {     	
    	// Application.debug("INIT LABELS");
    	
    	// label polygon
    	if (labels == null || labels.length == 0) {    		
        	// Application.debug("no labels given");
        	
             setLabel(null);
             if (segments != null) {
            	 defaultSegmentLabels();
             }
             return;
    	}
    	
    	// label polygon              
        // first label for polygon itself
        setLabel(labels[0]);        
    	
    	// label segments and points
    	if (points != null && segments != null) {
    		
    		// additional labels for the polygon's segments
    		// poly + segments + points - 2 for AlgoPolygonRegular
    		if (labels.length == 1 + segments.length + points.length - 2) {
    			//Application.debug("labels for segments and points");
    			
	            int i=1;
    			for (int k=0; k < segments.length; k++, i++) {
	                segments[k].setLabel(labels[i]);
	            }		            
    			for (int k=2; k < points.length; k++, i++) {
	                points[k].setLabel(labels[i]);
	            }
	        } 
    		    		
    		// additional labels for the polygon's segments
    		// poly + segments for AlgoPolygon
    		else if (labels.length == 1 + segments.length) {
    			//Application.debug("labels for segments");
    			
            	int i=1;
    			for (int k=0; k < segments.length; k++, i++) {
	                segments[k].setLabel(labels[i]);
	            }		            	           	            
	        } 
    		
	        else { 
	        	//Application.debug("label for polygon (autoset segment labels)");     	
	        	defaultSegmentLabels();
	        }
    	}    	        
    }
    
    private void defaultSegmentLabels() {
    	//  no labels for segments specified
        //  set labels of segments according to point names
        if (points.length == 3) {          
           setLabel(segments[0], points[2]);
           setLabel(segments[1], points[0]);
           setLabel(segments[2], points[1]); 
        } else {
           for (int i=0; i < points.length; i++) {
               setLabel(segments[i], points[i]);
           }
        }
    }
    
    private void setLabel(GeoSegmentInterface s, GeoPointInterface geoPoint) {
        if (!geoPoint.isLabelSet() || geoPoint.getLabel() == null) 
        	s.setLabel(null);
        else 
        	s.setLabel(geoPoint.getLabel().toLowerCase(Locale.US));
    }
	
    /**
     * Updates all segments of this polygon for its point array.
     * Note that the point array may be changed: this method makes
     * sure that segments are reused if possible.
     */
	 private void updateSegments() {  	
		 if (points == null) return;
		 
		GeoSegmentInterface [] oldSegments = segments;				
		segments = new GeoSegmentInterface[points.length]; // new segments
				
		if (oldSegments != null) {
			// reuse or remove old segments
			for (int i=0; i < oldSegments.length; i++) {        	
	        	if (i < segments.length &&
	        		oldSegments[i].getStartPointAsGeoElement() == points[i] && 
	        		oldSegments[i].getEndPointAsGeoElement() == points[(i+1) % points.length]) 
	        	{		
        			// reuse old segment
        			segments[i] = oldSegments[i];        		
         		} 
	        	else {
        			// remove old segment
        			//((AlgoJoinPointsSegment) oldSegments[i].getParentAlgorithm()).removeSegmentOnly();
        			removeSegment(oldSegments[i]);
	        	}	        	
			}
		}			
		
		// create missing segments
        for (int i=0; i < segments.length; i++) {
        	GeoPointInterface startPoint = points[i];
        	GeoPointInterface endPoint = points[(i+1) % points.length];
        	
        	if (segments[i] == null) {
         		segments[i] = createSegment(startPoint, endPoint);
        	}     
        }         
        
    }
	 
	 

	 /**
	  * remove an old segment
	  * @param oldSegment the old segment 
	  */
	 public void removeSegment(GeoSegmentInterface oldSegment){
		 ((AlgoJoinPointsSegment) ((GeoSegment) oldSegment).getParentAlgorithm()).removeSegmentOnly();
	 }
	 
	 
	 
	 /**
	  * return a segment joining startPoint and endPoint
	  * @param startPoint the start point
	  * @param endPoint the end point
	  * @return the segment
	  */
	 public GeoSegmentInterface createSegment(GeoPointInterface startPoint, GeoPointInterface endPoint){
		 GeoSegmentInterface segment;

		 AlgoJoinPointsSegment algoSegment = new AlgoJoinPointsSegment(cons, (GeoPoint) startPoint, (GeoPoint) endPoint, this);            
		 cons.removeFromConstructionList(algoSegment);               

		 segment = algoSegment.getSegment(); 
		 // refresh color to ensure segments have same color as polygon:
		 segment.setObjColor(getObjectColor()); 

		 return segment;
	 }

	 

	/**
	 * The copy of a polygon is a number (!) with
	 * its value set to the polygons current area
	 */      
	public GeoElement copy() {
		return new GeoNumeric(cons, getArea());        
	}    
	
	public GeoElement copyInternal(Construction cons) {						
		GeoPolygon ret = new GeoPolygon(cons, null); 
		ret.points = GeoElement.copyPoints(cons, (GeoPoint[]) points);		
		ret.set(this);
				
		return ret;		
	} 		
	
	public void set(GeoElement geo) {
		GeoPolygon poly = (GeoPolygon) geo;		
		area = poly.area;
		defined = poly.defined;	
		
		for (int i=0; i < points.length; i++) {				
			((GeoPoint) points[i]).set((GeoPoint) poly.points[i]);
		}	
		updateSegments();
	}
	
	
	/**
	 * Returns the i-th point of this polygon.
	 * Note that this array may change dynamically.
	 * @param i number of point
	 * @return the i-th point
	 */
	public GeoPoint getPoint(int i) {
		return (GeoPoint) points[i];
	}

	/**
	 * Returns the points of this polygon.
	 * Note that this array may change dynamically.
	 */
	public GeoPoint [] getPoints() {
		return (GeoPoint []) points;
	}
	
	/**
	 * Returns the segments of this polygon.
	 * Note that this array may change dynamically.
	 */
	public GeoSegmentInterface [] getSegments() {
		return segments;
	}

	public boolean isFillable() {
		return true;
	}
	
//	Michael Borcherds 2008-01-26 BEGIN
	/** 
	 * Calculates this polygon's area . This method should only be called by
	 * its parent algorithm of type AlgoPolygon
	 */
	public void calcArea() {
		area = calcAreaWithSign(getPoints());	
		defined = !(Double.isNaN(area) || Double.isInfinite(area));
	}
	
	public double getArea() {
		if (defined)
			return Math.abs(area);				        
		else 
			return Double.NaN;			        	
	}
	
	public double getDirection() { // clockwise=-1/anticlockwise=+1/no area=0
		if (defined)
			return MyMath.sgn(kernel, area);				        
		else 
			return Double.NaN;			        	
	}	

	/**
	 * Returns the area of a polygon given by points P
	 */	
	final static public double calcArea(GeoPoint [] P) {
	    return Math.abs(calcAreaWithSign(P));
	}
	/**
	 * Returns the area of a polygon given by points P, negative if clockwise
	 * changed name from calcArea as we need the sign when calculating the centroid Michael Borcherds 2008-01-26
	 * TODO Does not work if polygon is self-entrant
	 */	
	final static public double calcAreaWithSign(GeoPoint[] points2) {
		if (points2 == null || points2.length < 2)
			return Double.NaN;
		
	   int i = 0;   
	   for (; i < points2.length; i++) {
		   if (points2[i].isInfinite())
			return Double.NaN;
	   }
    
	   // area = 1/2 | det(P[i], P[i+1]) |
	   int last = points2.length - 1;
	   double sum = 0;                     
	   for (i=0; i < last; i++) {
			sum += GeoPoint.det(points2[i], points2[i+1]);
	   }
	   sum += GeoPoint.det(points2[last], points2[0]);
	   return sum / 2.0;  // positive (anticlockwise points) or negative (clockwise)
   }   
	
	/**
	 * Calculates the centroid of this polygon and writes
	 * the result to the given point.
	 * algorithm at http://local.wasp.uwa.edu.au/~pbourke/geometry/polyarea/
	 * TODO Does not work if polygon is self-entrant
	 */
	public void calcCentroid(GeoPoint centroid) {
		if (!defined) {
			centroid.setUndefined();
			return;
		}
	
		double xsum = 0;
		double ysum = 0;
		double factor=0;
		for (int i=0; i < points.length; i++) {
			factor=pointsClosedX(i)*pointsClosedY(i+1)-pointsClosedX(i+1)*pointsClosedY(i);
			xsum+=(pointsClosedX(i)+pointsClosedX(i+1)) * factor;
			ysum+=(pointsClosedY(i)+pointsClosedY(i+1)) * factor;
		}
		centroid.setCoords(xsum, ysum, 6.0*getAreaWithSign()); // getArea includes the +/- to compensate for clockwise/anticlockwise
	}
	
	private double pointsClosedX(int i)
	{
		// pretend array has last element==first element
		if (i==points.length) //return points[0].inhomX; else return points[i].inhomX;
			return getPointX(0); else return getPointX(i);
	}
	 	
	private double pointsClosedY(int i)
	{
		// pretend array has last element==first element
		if (i==points.length) //return points[0].inhomY; else return points[i].inhomY;
			return getPointY(0); else return getPointY(i);
	}
	 	
	public double getAreaWithSign() {
		if (defined)
			return area;				        
		else 
			return Double.NaN;			        	
	}	
//	Michael Borcherds 2008-01-26 END

	/*
	 * overwrite methods
	 */
	public boolean isDefined() {
		return defined;
   }	
   
   public void setDefined() {
   		defined = true;
   }
   
   public void setUndefined() {
		   defined = false;
	}
        
   public final boolean showInAlgebraView() {	   
	   //return defined;
	   return true;
   }
   
   
   /** 
	* Yields true if the area of this polygon is equal to the
	* area of polygon p.
	*/
	// Michael Borcherds 2008-04-30
	final public boolean isEqual(GeoElement geo) {
		// return false if it's a different type
		if (geo.isGeoPolygon()) return kernel.isEqual(getArea(), ((GeoPolygon)geo).getArea());
		else return false;
	}

   public void setEuclidianVisible(boolean visible) {
		super.setEuclidianVisible(visible);
		if (segments != null) {
			for (int i=0; i < segments.length; i++) {
				segments[i].setEuclidianVisible(visible);			
				segments[i].update();
			}
		}		
   }  

   public void setObjColor(Color color) {
   		super.setObjColor(color);
   		if (segments != null) {
   			for (int i=0; i < segments.length; i++) {
   				segments[i].setObjColor(color);
   				segments[i].update();
   			}
   		}	
   }
   
   public void setLineType(int type) {
		super.setLineType(type);
		if (segments != null) {
			for (int i=0; i < segments.length; i++) {
				segments[i].setLineType(type);	
				segments[i].update();
			}
		}	
   }
   
   public void setLineThickness(int th) {
		super.setLineThickness(th);
		if (segments != null) {
			for (int i=0; i < segments.length; i++) {
				segments[i].setLineThickness(th);
				segments[i].update();
			}
		}	
   }
	
   final public String toString() {
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(" = ");
		sbToString.append(kernel.format( getArea() ));
	    return sbToString.toString();
   }      
   private StringBuffer sbToString = new StringBuffer(50);
   
   final public String toValueString() {
	   return kernel.format(getArea());
   }

	
	 /**
     * interface NumberValue
     */    
    public MyDouble getNumber() {    	
        return new MyDouble(kernel,  getArea() );
    }     
    
    final public double getDouble() {
        return getArea();
    }   
        
    final public boolean isConstant() {
        return false;
    }
    
    final public boolean isLeaf() {
        return true;
    }
    
    final public HashSet getVariables() {
        HashSet varset = new HashSet();        
        varset.add(this);        
        return varset;          
    }                   
    
    final public ExpressionValue evaluate() { return this; }	

	public void setMode(int mode) {
		// dummy		
	}

	public int getMode() {
		// dummy
		return 0;
	}

	protected boolean showInEuclidianView() {
		return defined;
	}    
	
	public boolean isNumberValue() {
		return true;
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
	
	public boolean isGeoPolygon() {
		return true;
	}
	
	/*
	 * Path interface implementation
	 */
	
	public boolean isPath() {
		return true;
	}

	public PathMover createPathMover() {
		return new PathMoverGeneric(this);
	}

	public double getMaxParameter() {
		return segments.length;
	}

	public double getMinParameter() {		
		return 0;
	}

	public boolean isClosedPath() {
		return true;
	}

	public boolean isOnPath(GeoPointInterface PI, double eps) {
		
		GeoPoint P = (GeoPoint) PI;
		
		if (P.getPath() == this)
			return true;
		
		// check if P is on one of the segments
		for (int i=0; i < segments.length; i++) {
			if (segments[i].isOnPath(P, eps))
				return true;
		}				
		return false;
	}

	public void pathChanged(GeoPointInterface PI) {		
		
		GeoPoint P = (GeoPoint) PI;
		
		// parameter is between 0 and segment.length,
		// i.e. floor(parameter) gives the segment index
		
		PathParameter pp = P.getPathParameter();
		pp.t = pp.t % segments.length;
		if (pp.t < 0) 
			pp.t += segments.length;
		int index = (int) Math.floor(pp.t) ;		
		GeoSegmentInterface seg = segments[index];
		double segParameter = pp.t - index;
		
		// calc point for given parameter
		/*
		P.x = seg.startPoint.inhomX + segParameter * seg.y;
		P.y = seg.startPoint.inhomY - segParameter * seg.x;
		*/
		P.x = seg.getPointX(segParameter);
		P.y = seg.getPointY(segParameter);
		P.z = 1.0;	
	}

	public void pointChanged(GeoPointInterface PI) {
		
		GeoPoint P = (GeoPoint) PI;
		
		double qx = P.x/P.z;
		double qy = P.y/P.z;
		double minDist = Double.POSITIVE_INFINITY;
		double resx=0, resy=0, resz=0, param=0;
		
		// find closest point on each segment
		PathParameter pp = P.getPathParameter();
		for (int i=0; i < segments.length; i++) {
			P.x = qx;
			P.y = qy;
			P.z = 1;
			segments[i].pointChanged(P);
			
			double x = P.x/P.z - qx; 
			double y = P.y/P.z - qy;
			double dist = x*x + y*y;			
			if (dist < minDist) {
				minDist = dist;
				// remember closest point
				resx = P.x;
				resy = P.y;
				resz = P.z;
				param = i + pp.t;
			}
		}				
			
		P.x = resx;
		P.y = resy;
		P.z = resz;
		pp.t = param;	
	}	
	
	
	
	
	
	/*
	 * Region interface implementation
	 */
	
	public boolean isInRegion(GeoPoint P){
		
		double x0 = P.x/P.z;
		double y0 = P.y/P.z;
		
		double x1,y1,x2,y2;
		int numPoints = points.length;
		//x1=points[numPoints-1].getInhomX()-x0;
		//y1=points[numPoints-1].getInhomY()-y0;
		x1=getPointX(numPoints-1)-x0;
		y1=getPointY(numPoints-1)-y0;
		
		boolean ret=false;
		for (int i=0;i<numPoints;i++){
			//x2=points[i].getInhomX()-x0;
			//y2=points[i].getInhomY()-y0;
			x2=getPointX(i)-x0;
			y2=getPointY(i)-y0;
			ret = ret ^ intersectOx(x1, y1, x2, y2);
			x1=x2;
			y1=y2;
		}
			
		return ret;
	}
	
	
	public void regionChanged(GeoPoint P){
		pointChangedForRegion(P);
	}
	
	
	public void pointChangedForRegion(GeoPoint P){
		if (!isInRegion(P))
			pointChanged(P);
	}
	
	
	/** returns true if the segment ((x1,y1),(x2,y2)) intersects [Ox) */
	private boolean intersectOx(double x1, double y1, double x2, double y2){
		if (y1*y2>0) //segment totally above or under
			return false;
		else{ 
			if (y1>y2){ //first point under (Ox)
				double y=y1; y1=y2; y2=y;
				double x=x1; x1=x2; x2=x;
			}
			if (y2==0) //half-plane
				return false;
			else if ((x1<0) && (x2<0)) //segment totally on the left
				return false;
			else if ((x1>0) && (x2>0)) //segment totally on the right
				return true;
			else if (x2*y1<=x1*y2) //angle >= 0�
				return true;
			else
				return false;
		}
	}

	public boolean isVector3DValue() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	

}
