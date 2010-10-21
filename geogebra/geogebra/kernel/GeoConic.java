/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * GeoConic.java
 *
 * Created on 10. September 2001, 08:52
 */

package geogebra.kernel;

import geogebra.Matrix.GgbVector;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.FunctionVariable;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.util.MyMath;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;

/**
 * Conics in 2D
 */
public class GeoConic extends GeoConicND
implements Path, Region, Traceable, ConicMirrorable,
Translateable, PointRotateable, Mirrorable, Dilateable, LineProperties, MatrixTransformable
{
	
	private static final long serialVersionUID = 1L;
	// avoid very large and small coefficients for numerical stability	
	private static final double MAX_COEFFICIENT_SIZE = 100000;
	private static final double MIN_COEFFICIENT_SIZE = 1;

	/** mode for equations like ax^2+bxy+cy^2+dx+ey+f=0 */
	public static final int EQUATION_IMPLICIT = 0;
	/** mode for equations like y=ax^2+bx+c */
	public static final int EQUATION_EXPLICIT = 1;
	/** mode for equations like (x-m)^2/a^2+(y-n)^2/b^2=1 */
	public static final int EQUATION_SPECIFIC = 2;

	private static String[] vars = { "x\u00b2", "x y", "y\u00b2", "x", "y" };
	private static String[] varsLateX = { "x^{2}", "x y", "y^{2}", "x", "y" };
	
	// enable negative sign of first coefficient in implicit equations
	private static boolean KEEP_LEADING_SIGN = false;

	/** single point type*/    
	public static final int CONIC_SINGLE_POINT = QUADRIC_SINGLE_POINT;
	/** intersecting lines type*/
	public static final int CONIC_INTERSECTING_LINES = QUADRIC_INTERSECTING_LINES;
	/** ellipse type*/
	public static final int CONIC_ELLIPSE = QUADRIC_ELLIPSOID;
	/** circle type*/
	public static final int CONIC_CIRCLE = QUADRIC_SPHERE;
	/** hyperbola type*/
	public static final int CONIC_HYPERBOLA = QUADRIC_HYPERBOLOID;
	/** empty conic type*/
	public static final int CONIC_EMPTY = QUADRIC_EMPTY;
	/** double line type*/
	public static final int CONIC_DOUBLE_LINE = QUADRIC_DOUBLE_LINE;
	/** parallel lines type */
	public static final int CONIC_PARALLEL_LINES = QUADRIC_PARALLEL_LINES;
	/** parabola type */
	public static final int CONIC_PARABOLA = QUADRIC_PARABOLOID;
	/** line type */
	public static final int CONIC_LINE = QUADRIC_LINE;

	/* 
	 *               ( A[0]  A[3]    A[4] )
	 *      matrix = ( A[3]  A[1]    A[5] )
	 *               ( A[4]  A[5]    A[2] )
	 */

	//int type = -1; // of conic
	private double maxCoeffAbs; // maximum absolute value of coeffs in matrix A[]
	private AffineTransform transform, oldTransform;
	/** true if should be traced */
	public boolean trace;	

	/**
	 * (eigenvecX, eigenvecY) are coords of currently calculated first eigenvector
	 * (eigenvecX, eigenvecY) is not a unit vector
	 */
	double eigenvecX;
	/** @see #eigenvecX	 */
	double eigenvecY;

	/** translation vector (midpoint, vertex) */    
	GeoVec2D b = new GeoVec2D(kernel);
	/** lines of which this conic consists in case it's degenerate */
	GeoLine[] lines;
	private GeoPoint singlePoint;
	private GeoPoint [] startPoints;
	//private boolean defined = true;
	private ArrayList<GeoPoint> pointsOnConic;
	
	private EquationSolver eqnSolver;
	
	// for classification
	transient private double detS, length, temp, temp1, temp2, nx, ny, lambda;
	private int index = 0;
	private double[] eigenval = new double[3];
	private double[] mu = new double[2];
	private GeoVec2D c = new GeoVec2D(kernel);	

	

	/**
	 * Creates a conic
	 * @param c construction
	 */
	public GeoConic(Construction c) {
		super(c,2);	
		eqnSolver = c.getEquationSolver();
		toStringMode = EQUATION_IMPLICIT;
	}		


	
	/** 
	 * Creates new GeoConic with Coordinate System for 3D 
	 * @param c construction
	 * @param label label
	 * @param coeffs coefficients
	 */
	protected GeoConic(Construction c, String label, double[] coeffs) {

		this(c);
		setCoeffs(coeffs);
		setLabel(label);		
	}	
	
	
	/**
	 * Creates copy of conic in construction of conic
	 * @param conic conic to be copied
	 */
	public GeoConic(GeoConic conic) {
		this(conic.cons);
		set(conic);
	}
	
	public String getClassName() {
		return "GeoConic";
	}
	
    public int getGeoClassType() {
    	return GEO_CLASS_CONIC;
    }
	
	protected String getTypeString() { 
		switch (type) {
			case GeoConic.CONIC_CIRCLE: 
				return "Circle";
			case GeoConic.CONIC_DOUBLE_LINE: 
				return "DoubleLine";
			case GeoConic.CONIC_ELLIPSE: 
				return "Ellipse"; 
			case GeoConic.CONIC_EMPTY: 
				return "EmptySet";
			case GeoConic.CONIC_HYPERBOLA: 
				return "Hyperbola";
			case GeoConic.CONIC_INTERSECTING_LINES: 
				return "IntersectingLines"; 
			case GeoConic.CONIC_LINE: 
				return "Line"; 
			case GeoConic.CONIC_PARABOLA: 
				return "Parabola"; 
			case GeoConic.CONIC_PARALLEL_LINES: 
				return "ParallelLines"; 
			case GeoConic.CONIC_SINGLE_POINT: 
				return "Point"; 
            
			default:
				return "Conic";
		}                       
	} 

	public GeoElement copy() {
		return new GeoConic(this);
	}
	
	public boolean isFillable() {
		return true;
	}
	
	public boolean isTraceable() {
		return true;
	}

	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	public boolean getTrace() {
		return trace;
	}
	
	public boolean isPath() {
		return true;
	}
	
	/**
	 * Returns a list of points that this conic passes through.
	 * May return null.
	 * @return list of points that this conic passes through.
	 */
	final ArrayList<GeoPoint> getPointsOnConic() {
		return pointsOnConic;
	}
	
	/**
	 * Sets a list of points that this conic passes through.
	 * This method should only be used by AlgoMacro.
	 * @param points list of points that this conic passes through
	 */
	final void setPointsOnConic(ArrayList<GeoPoint> points) {
		pointsOnConic = points;
	}
	
	/**
	 * Adds a point to the list of points that this conic passes through.
	 */
	protected final void addPointOnConic(GeoPointInterface p) {
		if (pointsOnConic == null)
			pointsOnConic = new ArrayList<GeoPoint>();
		pointsOnConic.add((GeoPoint)p);				
	}
	
	/**
	 * Removes a point from the list of points that this conic passes through.
	 * @param p Point to be removed
	 */
	final void removePointOnConic(GeoPoint p) {
		if (pointsOnConic != null)
			pointsOnConic.remove(p);
	}

	/** geo is expected to be a conic. make deep copy of
	 * member vars of geo.
	 */
	public void set(GeoElement geo) {
		GeoConic co =(GeoConic) geo;
	
		// copy everything
		toStringMode = co.toStringMode;
		type = co.type;
		for (int i = 0; i < 6; i++)
			matrix[i] = co.matrix[i]; // flat matrix A   
		
		if (co.transform != null) {
			AffineTransform transform = getAffineTransform();
			transform.setTransform(co.transform);
		}
		
		eigenvec[0].setCoords(co.eigenvec[0]);
		eigenvec[1].setCoords(co.eigenvec[1]);
		//b.setCoords(co.b);
		setMidpoint(co.getMidpoint().get());
		halfAxes[0] = co.halfAxes[0];
		halfAxes[1] = co.halfAxes[1];
		linearEccentricity = co.linearEccentricity;
		eccentricity = co.eccentricity;
		p = co.p;
		mu[0] = co.mu[0];
		mu[1] = co.mu[1];
		if (co.lines != null) {
			if (lines == null) {
				lines = new GeoLine[2];
				lines[0] = new GeoLine(cons);
				lines[1] = new GeoLine(cons);
			}
			lines[0].setCoords(co.lines[0]);
			lines[1].setCoords(co.lines[1]);
		}
		if (co.singlePoint != null) {
			if (singlePoint == null)
				singlePoint = new GeoPoint(cons);
			singlePoint.setCoords(co.singlePoint);
		}
		defined = co.defined;		
	}		
	
	/**
	 * Updates this conic. If the transform has changed, we call makePathParametersInvalid()
	 * to force an update of all path parameters of all points on this conic.
	 */
	public void update() {			
		makePathParametersInvalid();
		super.update();        				
	}

	/**
	 * Sets equation mode to specific, implicit or explicit
	 * @param mode equation mode (one of EQUATION_* constants)
	 */
	final public void setToStringMode(int mode) {
		switch (mode) {
			case EQUATION_SPECIFIC :				
				this.toStringMode = EQUATION_SPECIFIC;
				break;
				
			case EQUATION_EXPLICIT:				
				this.toStringMode = EQUATION_EXPLICIT;
				break;

			default :
				this.toStringMode = EQUATION_IMPLICIT;
		}						
	}

	/**
	 * Returns equation mode  (specific, implicit or explicit)
	 * @return equation mode (one of EQUATION_* constants)
	 */
	final public int getToStringMode() {
		return toStringMode;
	}
	

	/**
	 * returns true if this conic is a circle 
	 * Michael Borcherds 2008-03-23
	 * @return true iff  this conic is circle
	 */
	public boolean isCircle() {
		return (type == CONIC_CIRCLE);
	}

	/** Changes equation mode to Specific */
	final public void setToSpecific() {
		setToStringMode(EQUATION_SPECIFIC);
	}

	/** Changes equation mode to Implicit */
	final public void setToImplicit() {
		setToStringMode(EQUATION_IMPLICIT);
	}
	
	/** Changes equation mode to Explicit */
	final public void setToExplicit() {
		setToStringMode(EQUATION_EXPLICIT);
	}

	/**
	 * Returns whether specific equation representation is possible.    
	 * @return true iff specific equation representation is possible. 
	 */
	final public boolean isSpecificPossible() {
		switch (type) {
			case CONIC_CIRCLE :
			case CONIC_DOUBLE_LINE :
			case CONIC_INTERSECTING_LINES :
			case CONIC_PARALLEL_LINES :
				return true;

			case CONIC_ELLIPSE :
			case CONIC_HYPERBOLA :
				//	xy vanished 
				return (Kernel.isZero(matrix[3]));

			case CONIC_PARABOLA :
				// x\u00b2 or y\u00b2 vanished
				return Kernel.isZero(matrix[0]) || Kernel.isZero(matrix[1]);

			default :
			case CONIC_LINE :
				return false;
		}
	}
	
	/**
	 * Returns wheter explicit parabola equation representation (y = a x\u00b2 + b x + c) 
	 * is possible. 
	 * @return true iff explicit equation is possible
	 */
	final public boolean isExplicitPossible() {
		if (type == CONIC_LINE) return false;
		return !Kernel.isZero(matrix[5]) && Kernel.isZero(matrix[3]) && Kernel.isZero(matrix[1]);
	}





	/**
	 * returns false if conic's matrix is the zero matrix
	 * or has infinite or NaN values
	 */
	final private boolean checkDefined() {
		boolean allZero = true;
		maxCoeffAbs = 0;		
		
		for (int i = 0; i < 6; i++) {
			if (Double.isNaN(matrix[i]) || Double.isInfinite(matrix[i])) {
				return false;
			}
				
			double abs = Math.abs(matrix[i]);			
			if (abs > Kernel.STANDARD_PRECISION) allZero = false;
			maxCoeffAbs = maxCoeffAbs > abs ? maxCoeffAbs : abs;			
		}
		if (allZero) {
			return false;		
		}
		
		// huge or tiny coefficients?
		double factor = 1.0;
		if (maxCoeffAbs < MIN_COEFFICIENT_SIZE) {
			factor = 2;
			while (maxCoeffAbs * factor < MIN_COEFFICIENT_SIZE) factor *= 2;					
		}		
		else if (maxCoeffAbs > MAX_COEFFICIENT_SIZE) {			
			factor = 0.5;
			while (maxCoeffAbs * factor > MAX_COEFFICIENT_SIZE) factor *= 0.5;					
		}	
		
		// multiply matrix with factor to avoid huge and tiny coefficients
		if (factor != 1.0) {
			maxCoeffAbs *= factor;
			for (int i=0; i < 6; i++) {
				matrix[i] *= factor;
			}
		}
		return true;
	}

	final protected boolean showInEuclidianView() {
		return defined && (type != CONIC_EMPTY);
	}

	public final boolean showInAlgebraView() {
		//return defined;
		return true;
	}

	/**
	 * Returns whether this conic consists of lines
	 * @return true for line conics
	 */
	final public boolean isLineConic() {
		switch (type) {
			case CONIC_DOUBLE_LINE :
			case CONIC_PARALLEL_LINES :
			case CONIC_INTERSECTING_LINES :
			case CONIC_LINE :
				return true;

			default :
				return false;
		}
	}

	/**
	 * Returns whether this conic is degenerate
	 * @return true iff degenerate
	 */
	final public boolean isDegenerate() {
		switch (type) {
			case CONIC_CIRCLE :
			case CONIC_ELLIPSE :
			case CONIC_HYPERBOLA :
			case CONIC_PARABOLA :
				return false;

			default :
				return true;
		}
	}

	/**
	 *  sets conic's matrix from coefficients of equation
	 *  from array
	 *  @param coeffs Array of coefficients
	 */  
	final public void setCoeffs(double[] coeffs) {
		setCoeffs(
			coeffs[0],
			coeffs[1],
			coeffs[2],
			coeffs[3],
			coeffs[4],
			coeffs[5]);
	}

	/**
	 *  sets conic's matrix from coefficients of equation
	 *  a x\u00b2 + b xy + c y\u00b2 + d x + e y + f = 0
	 * @param a 
	 * @param b 
	 * @param c 
	 * @param d 
	 * @param e 
	 * @param f 
	 */
	final public void setCoeffs(
		double a,
		double b,
		double c,
		double d,
		double e,
		double f) {
		matrix[0] = a; // x\u00b2
		matrix[1] = c; // y\u00b2
		matrix[2] = f; // constant
		matrix[3] = b / 2.0; // xy
		matrix[4] = d / 2.0; // x
		matrix[5] = e / 2.0; // y         

		classifyConic();
	}

	

	private double[] coeffs = new double[6];	
	
	
	
	protected StringBuilder buildValueString() {
		sbToValueString().setLength(0);
	       if (!isDefined()) {
	    	   sbToValueString.append("?");
	    	   return sbToValueString;
	       }
		coeffs[0] = matrix[0]; // x\u00b2
		coeffs[2] = matrix[1]; // y\u00b2
		coeffs[5] = matrix[2]; // constant
		coeffs[1] = 2 * matrix[3]; // xy        
		coeffs[3] = 2 * matrix[4]; // x
		coeffs[4] = 2 * matrix[5]; // y  
		
						
		
		if (type == CONIC_LINE) {
			sbToValueString.append(lines[0].toStringLHS());
			sbToValueString.append(" = 0");
			return sbToValueString;
		}
		String squared = (kernel.getCASPrintForm() == ExpressionNode.STRING_TYPE_LATEX) ? "^{2}" : "\u00b2";
		switch (toStringMode) {
			case EQUATION_SPECIFIC :
				if (!isSpecificPossible())
					return kernel.buildImplicitEquation(coeffs, (kernel.getCASPrintForm() == ExpressionNode.STRING_TYPE_LATEX) ? varsLateX : vars, KEEP_LEADING_SIGN, true, '=');						
				
				switch (type) {					
					case CONIC_CIRCLE :		
						buildSphereNDString();
						return sbToValueString;

					case CONIC_ELLIPSE :					
						if (Kernel.isZero(coeffs[1])) { // xy coeff = 0
							double coeff0, coeff1;
							// we have to check the first eigenvector: it could be (1,0) or (0,1)
							// if it is (0,1) we have to swap the coefficients of x^2 and y^2
							if (eigenvec[0].y == 0.0) {
								coeff0 = halfAxes[0];
								coeff1 = halfAxes[1];
							} else {
								coeff0 = halfAxes[1];
								coeff1 = halfAxes[0];
							}
							
							if (Kernel.isZero(b.x)) {
								sbToValueString.append("x");
								sbToValueString.append(squared);
							} else {
								sbToValueString.append("(x ");
								sbToValueString.append(kernel.formatSigned(-b.x));
								sbToValueString.append(")");
								sbToValueString.append(squared);
							}
							sbToValueString.append(" / ");
							sbToValueString.append(kernel.format(coeff0 * coeff0));
							sbToValueString.append(" + ");
							if (Kernel.isZero(b.y)) {
								sbToValueString.append("y");
								sbToValueString.append(squared);
							} else {
								sbToValueString.append("(y ");
								sbToValueString.append(kernel.formatSigned(-b.y));
								sbToValueString.append(")");
								sbToValueString.append(squared);
							}
							sbToValueString.append(" / ");
							sbToValueString.append(kernel.format(coeff1 * coeff1));
							sbToValueString.append(" = 1");
													
							return sbToValueString;
						} else
							return kernel.buildImplicitEquation(								
								coeffs,
								(kernel.getCASPrintForm() == ExpressionNode.STRING_TYPE_LATEX) ? varsLateX : vars, 
								KEEP_LEADING_SIGN, true, '=');

					case CONIC_HYPERBOLA :
						if (Kernel.isZero(coeffs[1])) { // xy coeff = 0	
							char firstVar, secondVar;
							double b1, b2;
							// we have to check the first eigenvector: it could be (1,0) or (0,1)
							// if it is (0,1) we have to swap the x and y
							if (eigenvec[0].y == 0.0) {
								firstVar = 'x';
								secondVar = 'y';			
								b1 = b.x;
								b2 = b.y;
							} else {
								firstVar = 'y';
								secondVar = 'x';
								b1 = b.y;
								b2 = b.x;
							}
							
							if (Kernel.isZero(b1)) {		
								sbToValueString.append(firstVar);
								sbToValueString.append(squared);
							} else {
								sbToValueString.append('(');
								sbToValueString.append(firstVar);
								sbToValueString.append(' ');
								sbToValueString.append(kernel.formatSigned(-b1));
								sbToValueString.append(")");
								sbToValueString.append(squared);
							}
							sbToValueString.append(" / ");
							sbToValueString.append(kernel.format(halfAxes[0] * halfAxes[0]));
							sbToValueString.append(" - ");
							if (Kernel.isZero(b2)) {
								sbToValueString.append(secondVar);
								sbToValueString.append(squared);
							} else {
								sbToValueString.append('(');
								sbToValueString.append(secondVar);
								sbToValueString.append(' ');
								sbToValueString.append(kernel.formatSigned(-b2));
								sbToValueString.append(")");
								sbToValueString.append(squared);
							}
							sbToValueString.append(" / ");
							sbToValueString.append(kernel.format(halfAxes[1] * halfAxes[1]));
							sbToValueString.append(" = 1");													
							
							return sbToValueString;
						} else
							return kernel.buildImplicitEquation(
								coeffs,
								(kernel.getCASPrintForm() == ExpressionNode.STRING_TYPE_LATEX) ? varsLateX : vars,
								KEEP_LEADING_SIGN,
								true, '=');

					case CONIC_PARABOLA :
						if (!Kernel.isZero(coeffs[2]))
							return kernel.buildExplicitConicEquation(
								coeffs,
								(kernel.getCASPrintForm() == ExpressionNode.STRING_TYPE_LATEX) ? varsLateX : vars,
								2,
								KEEP_LEADING_SIGN);
						else if (!Kernel.isZero(coeffs[0]))
							return kernel.buildExplicitConicEquation(
								coeffs,
								(kernel.getCASPrintForm() == ExpressionNode.STRING_TYPE_LATEX) ? varsLateX : vars,
								0,
								KEEP_LEADING_SIGN);
						else
							return kernel.buildImplicitEquation(
								coeffs,
								(kernel.getCASPrintForm() == ExpressionNode.STRING_TYPE_LATEX) ? varsLateX : vars,
								KEEP_LEADING_SIGN,
								true, '=');

					case CONIC_DOUBLE_LINE :
						sbToValueString.append('(');
						sbToValueString.append(lines[0].toStringLHS());
						sbToValueString.append(")");
						sbToValueString.append(squared);
						sbToValueString.append(" = 0");
						return sbToValueString;

					case CONIC_PARALLEL_LINES :
					case CONIC_INTERSECTING_LINES :
						sbToValueString.append('(');
						sbToValueString.append(lines[0].toStringLHS());
						sbToValueString.append(") (");
						sbToValueString.append(lines[1].toStringLHS());
						sbToValueString.append(") = 0");
						return sbToValueString;
						
				}
				
			case EQUATION_EXPLICIT:
				if (isExplicitPossible())
					return kernel.buildExplicitConicEquation(coeffs, (kernel.getCASPrintForm() == ExpressionNode.STRING_TYPE_LATEX) ? varsLateX : vars, 4, KEEP_LEADING_SIGN); 

			default : //implicit
				return kernel.buildImplicitEquation(coeffs, (kernel.getCASPrintForm() == ExpressionNode.STRING_TYPE_LATEX) ? varsLateX : vars, KEEP_LEADING_SIGN, true, '=');
		}
	}
	
	/**
	 * Returns the halfaxes
	 * @return lengths of halfaxes
	 */
	final public double[] getHalfAxes() {
		return halfAxes;
	}
	/**
	 * for intersecting lines, parallel lines
	 * @return lines the conic consists of
	 */
	final public GeoLine[] getLines() {
		return lines;
	}
	/**
	 * Returns the point (in case this conic is a single point)
	 * @return the single point
	 */
	final public GeoPoint getSinglePoint() {
		return singlePoint;
	}

	/**
	 * Returns the eigenvector-real worl transformation
	 * @return eigenvector-real worl transformation
	 */
	final public AffineTransform getAffineTransform() {
		if (transform == null)
			transform = new AffineTransform();
		return transform;
	}

	final protected void setAffineTransform() {	
		AffineTransform transform = getAffineTransform();	
		
		/*      ( v1x   v2x     bx )
		 *      ( v1y   v2y     by )
		 *      (  0     0      1  )   */			
		transform.setTransform(
			eigenvec[0].x,
			eigenvec[0].y,
			eigenvec[1].x,
			eigenvec[1].y,
			b.x,
			b.y);
	}

	/**
	 * Returns midpoint or vertex
	 * @return midpoint or vertex
	 */
	final public GeoVec2D getTranslationVector() {
		return b;
	}
	
	/**
	 * return the radius of the circle (if the conic is a circle)
	 * @return the radius of the circle
	 */
	final public double getCircleRadius(){
		return halfAxes[0];
	}
	
	/**
	 * Transforms coords of point P from Eigenvector space to real world space.
	 * Note: P.setCoords() is not called here!
	 * @param P point in EV coords
	 */
	final void coordsEVtoRW(GeoPoint P) {
		// rotate by alpha
		double px = P.x;
		P.x = px * eigenvec[0].x + P.y * eigenvec[1].x;
		P.y = px * eigenvec[0].y + P.y * eigenvec[1].y; 
	
		// translate by b
		P.x = P.x + P.z *  b.x;
		P.y = P.y + P.z * b.y;
	}
	
	/**
	 * Transforms coords of point P from real world space to Eigenvector space. 
	 * Note: P.setCoords() is not called here!
	 */
	private void coordsRWtoEV(GeoPoint P) {
		// translate by -b
		P.x = P.x - P.z * b.x;
		P.y = P.y - P.z * b.y;
		
		// rotate by -alpha
		double px = P.x;	
		P.x = px * eigenvec[0].x + P.y * eigenvec[0].y;
		P.y = px * eigenvec[1].x + P.y * eigenvec[1].y;
	}
	
	/** @return copy of flat matrix 	 */
	final public double[] getMatrix() {
		double[] ret = { matrix[0], matrix[1], matrix[2], matrix[3], matrix[4], matrix[5] };
		return ret;
	}

	/** set out with flat matrix of this conic 
	 * @param out array in which the flat matrix should be stored*/
	final public void getMatrix(double[] out) {
		for (int i = 0; i < 6; i++) {
			out[i] = matrix[i];
		}
	}

	/** set conic's matrix from flat matrix      
	 * @param matrix array from which the flat matrix should be read
	 */
	final public void setMatrix(double[] matrix) {
		for (int i = 0; i < 6; i++) {
			this.matrix[i] = matrix[i];
		}
		classifyConic();
	}	

	/** 
	 * Set conic's matrix from flat matrix (array of length 6).	 
	 * @param matrix array from which the flat matrix should be read
	 */
	final public void setDegenerateMatrixFromArray(double[] matrix) {
		for (int i = 0; i < 6; i++) {
			this.matrix[i] = matrix[i];
		}				
		classifyConic(true);						
	}

	/** 
	 * set conic's matrix from 3x3 matrix (not necessarily be symmetric).
	 * @param C matrix   
	 */
	final public void setMatrix(double[][] C) {
		matrix[0] = C[0][0];
		matrix[1] = C[1][1];
		matrix[2] = C[2][2];
		matrix[3] = (C[0][1] + C[1][0]) / 2.0;
		matrix[4] = (C[0][2] + C[2][0]) / 2.0;
		matrix[5] = (C[1][2] + C[2][1]) / 2.0;                              		
		classifyConic();
	}
	
	
	

	/**
	 * makes this conic a circle with midpoint M and radius BC
	 *  Michael Borcherds 2008-03-13	
	 * @param M midpoint
	 * @param B first radius endpoint
	 * @param C second radius endpoint
	 */
	final public void setCircle(GeoPoint M, GeoPoint B, GeoPoint C) {
		defined = M.isDefined() && !M.isInfinite() &&
		B.isDefined() && !B.isInfinite() &&
		C.isDefined() && !C.isInfinite(); 
		
		double r=B.distance(C);
		
		// check radius
		if (Kernel.isZero(r)) {
			r = 0;
		} 
		else if (r < 0) {
			defined = false;
		}					

		if (defined) {
			setCircleMatrix(M, r);
			setAffineTransform();
		} 		
	}
	
	
	
	public void setSphereND(GeoPointInterface M, GeoSegmentInterface segment){
		setCircle((GeoPoint) M, (GeoSegment) segment);
	}
	

	/**
	 * makes this conic a circle with midpoint M and radius geoSegment
	 *  Michael Borcherds 2008-03-13	
	 *  @param M center of circle
	 *  @param geoSegment length of geoSegment is radius of the circle
	 */
	final public void setCircle(GeoPoint M, GeoSegment geoSegment) {
		defined = M.isDefined() && !M.isInfinite() &&
		geoSegment.isDefined(); 
		
		double r=geoSegment.getLength();
		
		// check radius
		if (Kernel.isZero(r)) {
			r = 0;
		} 
		else if (r < 0) {
			defined = false;
		}					

		if (defined) {
			setCircleMatrix(M, r);
			setAffineTransform();
		} 		
	}

	
	public void setSphereND(GeoPointInterface M, GeoPointInterface P){
		setCircle((GeoPoint) M, (GeoPoint) P);
	}
	
	/**
	 * makes this conic a circle with midpoint M through Point P
	 */
	final public void setCircle(GeoPoint M, GeoPoint P) {
		defined = M.isDefined() && P.isDefined() && !P.isInfinite();
		if (!defined) {			
			return;
		}

		if (M.isInfinite()) {
			// midpoint at infinity -> parallelLines
			// one through P, the other through infinite point M 
			/*
			b.x = P.inhomX;
			b.y = P.inhomY;
			*/
			double[] coords = new double[3];
			P.getCoords(coords);
			setMidpoint(coords);
			// M is normalvector of double line            
			eigenvecX = -M.y;
			eigenvecY = M.x;
			setEigenvectors();
			halfAxes[0] = Double.POSITIVE_INFINITY;
			halfAxes[1] = Double.POSITIVE_INFINITY;
			mu[0] = 0.0; // line at infinity is not drawn
			parallelLines(mu);
			// set line at infinity 0 = 1
			lines[1].x = Double.NaN;
			lines[1].y = Double.NaN;
			lines[1].z = Double.NaN;
			// set degenerate matrix
			matrix[0] = 0.0d;
			matrix[1] = 0.0d;
			matrix[2] = lines[0].z;
			matrix[3] = 0.0d;
			matrix[4] = lines[0].x / 2.0;
			matrix[5] = lines[0].y / 2.0;
		} else {
			setCircleMatrix(M, M.distance(P));
		}
		setAffineTransform();
	}

	final private void setCircleMatrix(GeoPoint M, double r) {
		
		setSphereNDMatrix(M, r);
	}

	/**
	 *  set Parabola from focus and line
	 *  @param F focus
	 *  @param g line
	 */
	final public void setParabola(GeoPoint F, GeoLine g) {
		defined = F.isDefined() && !F.isInfinite() && g.isDefined();

		if (!defined)
			return;

		// set parabola's matrix
		double fx = F.inhomX;
		double fy = F.inhomY;

		matrix[0] = g.y * g.y;
		matrix[1] = g.x * g.x;
		double lsq = matrix[0] + matrix[1];
		matrix[2] = lsq * (fx * fx + fy * fy) - g.z * g.z;
		matrix[3] = -g.x * g.y;
		matrix[4] = - (lsq * fx + g.x * g.z);
		matrix[5] = - (lsq * fy + g.y * g.z);
				
		classifyConic();
	}

	/**
	 * set the matrix of ellipse or hyperbola 
	 * @param B first focus
	 * @param C second focus
	 * @param a first half axis
	 */
	final public void setEllipseHyperbola(
		GeoPoint B,
		GeoPoint C,
		double a) {
			
		if (B.isInfinite() || C.isInfinite() || a < -kernel.getEpsilon()) {
			defined = false;
			return;
		}
							
		// set conics's matrix
		double b1 = B.inhomX;
		double b2 = B.inhomY;
		double c1 = C.inhomX;
		double c2 = C.inhomY;

		// precalculations
		double diff1 = b1 - c1;
		double diff2 = b2 - c2;
		double sqsumb = b1 * b1 + b2 * b2;
		double sqsumc = c1 * c1 + c2 * c2;
		double sqsumdiff = sqsumb - sqsumc;
		double a2 = 2.0 * a;
		double asq4 = a2 * a2;
		double asq = a * a;
		double afo = asq * asq;

		matrix[0] = 4.0 * (a2 - diff1) * (a2 + diff1);
		matrix[3] = -4.0 * diff1 * diff2;
		matrix[1] = 4.0 * (a2 - diff2) * (a2 + diff2);
		matrix[4] = -2.0 * (asq4 * (b1 + c1) - diff1 * sqsumdiff);
		matrix[5] = -2.0 * (asq4 * (b2 + c2) - diff2 * sqsumdiff);
		matrix[2] =
			-16.0 * afo - sqsumdiff * sqsumdiff + 8.0 * asq * (sqsumb + sqsumc);

		// set eigenvectors' directions (B -> C and normalvector)
		// this is needed, so that setEigenvectors() (called by classifyConic)
		// will surely take the right direction
		// normalizing is not needed at this point
		eigenvec[0].x = c1 - b1;
		eigenvec[0].y = c2 - b2;
		eigenvec[1].x = -eigenvec[0].y;
		eigenvec[1].y = eigenvec[0].x;
		
		classifyConic();
		
		// check if we got an ellipse or hyperbola
		if (!(type == CONIC_HYPERBOLA || type == CONIC_ELLIPSE || type == CONIC_CIRCLE))
		{
			defined = false;
		}
	}

	/*************************************
	 * MOVEMENTS
	 *************************************/

	/**
	 * translate conic by vector v
	 */
	final public void translate(GgbVector v) {
		doTranslate(v.getX(), v.getY());

		//classifyConic();
		setAffineTransform();
		updateDegenerates(); // for degenerate conics            
	}
	
	final public boolean isTranslateable() {
		return true;
	}

	/**
	 * translate this conic by vector (vx, vy)
	 * @param vx x-coord of translation vector
	 * @param vy y-coord of translation vector
	 */
	final public void translate(double vx, double vy) {
		doTranslate(vx, vy);

		setAffineTransform();
		updateDegenerates(); // for degenerate conics      
	}
	
	final private void doTranslate(double vx, double vy) {
		// calc translated matrix   
		matrix[2] =
			matrix[2]
				+ vx * (matrix[0] * vx - 2.0 * matrix[4])
				+ vy * (matrix[1] * vy - 2.0 * matrix[5] + 2.0 * matrix[3] * vx);
		matrix[4] = matrix[4] - matrix[0] * vx - matrix[3] * vy;
		matrix[5] = matrix[5] - matrix[3] * vx - matrix[1] * vy;
		
		// avoid classification and set changes by hand:   
		setMidpoint(getMidpoint().add(new GgbVector(new double[] {vx,vy,0})).get());
		/*
		b.x += vx;
		b.y += vy;
		*/
	}		

	/**
	 * rotate this conic by angle phi around (0,0)
	 */
	 final public void rotate(NumberValue phiVal) {
    	double phi = phiVal.getDouble();
		rotate(phi);

		setAffineTransform();
		updateDegenerates(); // for degenerate conics     	
	}

	/**
	 * rotate this conic by angle phi around Q
	 */
	final public void rotate(NumberValue phiVal, GeoPoint Q) {
		double phi = phiVal.getDouble();
		double qx = Q.inhomX;
		double qy = Q.inhomY;

		// first translate to new origin Q
		doTranslate(-qx, -qy);
		// rotate around new origin Q
		rotate(phi);
		// translate back to old origin (0,0)
		doTranslate(qx, qy);
		
		setAffineTransform();
		updateDegenerates(); // for degenerate conics
	}

	public boolean isMatrixTransformable() {
		return true;
	}

	final public void matrixTransform(double a, double b, double c, double d) {
		double det = a * d - b * c;
		double det2 = det * det;
		
		double A0 = d * (d * matrix[0] - c * matrix[3]) - c * ( d * matrix[3] - c * matrix[1]);
		double A3 = a * (d * matrix[3] - c * matrix[1]) - b * ( d * matrix[0] - c * matrix[3]);
		double A1 = a * (a * matrix[1] - b * matrix[3]) - b * ( a * matrix[3] - b * matrix[0]);
		double A4 = d * matrix[4] - c * matrix[5];
		matrix[5] = (a * matrix[5] - b * matrix[4]) / det;
		matrix[0] = A0 / det2;
		matrix[1] = A1 / det2;
		matrix[3] = A3 / det2;
		matrix[4] = A4 / det;
		
		classifyConic();
	}
	
	/**
	 * rotate this conic by angle phi around (0,0)
	 * [ cos    -sin    0 ]
	 * [ sin    cos     0 ]
	 * [ 0      0       1 ]
	 */
	final private void rotate(double phi) {
		// set rotated matrix
		double sum = matrix[0] + matrix[1];
		double diff = matrix[0] - matrix[1];
		double cos = Math.cos(phi);
		double sin = Math.sin(phi);
		// cos(2 phi) = cos(phi)\u00b2 - sin(phi)\u00b2 = (cos + sin)*(cos - sin)
		double cos2 = (cos + sin) * (cos - sin);
		// cos(2 phi) = 2 cos sin
		double sin2 = 2.0 * cos * sin;

		double temp = diff * cos2 - 2.0 * matrix[3] * sin2;
		double A0 = (sum + temp) / 2.0;
		double A1 = (sum - temp) / 2.0;
		double A3 = matrix[3] * cos2 + diff * cos * sin;
		double A4 = matrix[4] * cos - matrix[5] * sin;
		matrix[5] = matrix[5] * cos + matrix[4] * sin;
		matrix[0] = A0;
		matrix[1] = A1;
		matrix[3] = A3;
		matrix[4] = A4;

		// avoid classification: make changes by hand
		eigenvec[0].rotate(phi);
		eigenvec[1].rotate(phi);
		b.rotate(phi);	
		setMidpoint(new double[] {b.x,b.y});
	}
	
	/**
	 * dilate this conic from point S by factor r
	 */
	 final public void dilate(NumberValue rval, GeoPoint S) {  
	    double r = rval.getDouble();		    	    	    
	 	double sx = S.inhomX;
		double sy = S.inhomY;
		
		// remember Eigenvector orientation
		boolean oldOrientation = hasPositiveEigenvectorOrientation();
		
		// translate -S
		doTranslate(-sx, -sy);
		// do dilation
		doDilate(r);
		// translate +S
		doTranslate(sx, sy);	
				
		// classify as type may have change
		classifyConic();        
		
		// make sure we preserve old Eigenvector orientation
		setPositiveEigenvectorOrientation(oldOrientation);
	}
	
	final private void doDilate(double factor) {
		// calc dilated matrix
		double r = 1d/factor;
		double r2 = r*r;
		matrix[0] *= r2;
		matrix[1] *= r2;		
		matrix[3] *= r2;
		matrix[4] *= r;
		matrix[5] *= r;
	}	

	/**
	 * Invert circle in or line in circle
	 * @version 2010-01-21
	 * @author Michael Borcherds 
	 * @param c Circle used as mirror
	 */
	    final public void mirror(GeoConic c) {
	    	if (c.isCircle() && this.isCircle() )
	    	{ // Mirror point in circle
	    		double r1 =  c.getHalfAxes()[0];
	    		GeoVec2D midpoint1=c.getTranslationVector();
	    		double x1=midpoint1.x;
	    		double y1=midpoint1.y;
	    		
	    		double r2 =  getHalfAxes()[0];
	    		GeoVec2D midpoint2=getTranslationVector();
	    		double x2=midpoint2.x;
	    		double y2=midpoint2.y;
	    		
	    		// distance between centres
	    		double p = Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
	    		
	    		// does circle being inverted pass through center of the other?
	    		if (Kernel.isEqual(p, r2)) { 
	    			AlgoIntersectConics intersect = new AlgoIntersectConics(cons, this, c);
	    			cons.removeFromConstructionList(intersect);
	    			
	    			GeoPoint [] points = intersect.getIntersectionPoints();
	    			
	    			if (lines == null) {
	    				lines = new GeoLine[2];
	    				lines[0] = new GeoLine(cons);
	    				lines[1] = new GeoLine(cons);
	    			}
	    				
	    			type = GeoConic.CONIC_LINE;
	    			GeoVec3D.lineThroughPoints(points[0], points[1], lines[0]);
	    			lines[1].setUndefined();

	    			return;
	    		}
	    		
	    		double x = r1*r1 / (p - r2);
	    		double y = r1*r1 / (p + r2);
	    		
	    		// radius of new circle
	    		double r3 = Math.abs(y - x) / 2.0;
	    		double centerX = x1 + (x2-x1) * (Math.min(x,y)+r3) / p; 
	    		double centerY = y1 + (y2-y1) * (Math.min(x,y)+r3) / p; 
	    		
	    		//double sf=r1*r1/((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
	            //setCoords( x1+sf*(x2-x1), y1+sf*(y2-y1) ,1.0);
	    		GeoPoint temp = new GeoPoint(cons,null,centerX,centerY,1.0);
	    		setCircleMatrix(temp, r3);
	    		temp.removeOrSetUndefinedIfHasFixedDescendent();
	    	}
	    	else if (c.isCircle() && (this.getType() == GeoConic.CONIC_LINE || this.getType() == GeoConic.CONIC_PARALLEL_LINES))
	    	{ // Mirror point in circle
	    		
	    			
	    			if (c.getType()==GeoConic.CONIC_CIRCLE)
	    	    	{ // Mirror point in circle
	    	    		double r =  c.getHalfAxes()[0];
	    	    		GeoVec2D midpoint=c.getTranslationVector();
	    	    		double a=midpoint.x;
	    	    		double b=midpoint.y;
	    	    		double lx = (getLines()[0]).x;
	    	    		double ly = (getLines()[0]).y;
	    	    		double lz = (getLines()[0]).z;
	    	    		double perpY,perpX;
	    	    		
	    	    		if(lx == 0){
	    	    			perpX = a;
	    	    			perpY = -ly/lz;
	    	    		}else{
	    	    			perpY = -(lx*ly*a-lx*lx*b+ly*lz)/(lx*lx+ly*ly);
	    	    			perpX = (-lz-ly*perpY)/lx;
	    	    		
	    	    		}
	    				double dist2 = ((perpX-a)*(perpX-a)+(perpY-b)*(perpY-b));
	    	    		//if line goes through center, we keep it
	    	    		if(!Kernel.isZero(dist2)){
	    	    		double sf=r*r/dist2;
	    	            //GeoPoint p =new GeoPoint(cons,null,a+sf*(perpX-a), b+sf*(perpY-b) ,1.0);
	    	            GeoPoint m =new GeoPoint(cons);
	    	            m.setCoords(a+sf*(perpX-a)/2, b+sf*(perpY-b)/2 ,1.0);
	    	            setSphereND(m,sf/2*Math.sqrt(((perpX-a)*(perpX-a)+(perpY-b)*(perpY-b))));
	    	    		}else type = GeoConic.CONIC_LINE;
	    	    	}
	    	    	else
	    	    	{
	    	    		setUndefined();
	    	    	}		
	    		}
	    		
	    		
	    		
	    	
			setAffineTransform();
			//updateDegenerates(); // for degenerate conics
	    }
	    
	/**
	 * mirror this conic at point Q
	 */
	final public void mirror(GeoPoint Q) {
		double qx = Q.inhomX;
		double qy = Q.inhomY;

		matrix[2] =
			4.0
				* (qy * qy * matrix[1]
					+ qx * (qx * matrix[0] + 2.0 * qy * matrix[3] + matrix[4])
					+ qy * matrix[5])
				+ matrix[2];
		matrix[4] = -2.0 * (qx * matrix[0] + qy * matrix[3]) - matrix[4];
		matrix[5] = -2.0 * (qx * matrix[3] + qy * matrix[1]) - matrix[5];

		// change eigenvectors' orientation
		eigenvec[0].mult(-1.0);
		eigenvec[1].mult(-1.0);
		
		// mirror translation vector b
		b.mirror(Q);	
		setMidpoint(new double[] {b.x,b.y});
		

		setAffineTransform();
		updateDegenerates(); // for degenerate conics
	}

	/**
	 * mirror this point at line g 
	 */
	final public void mirror(GeoLine g) {
		// Y = S(phi).(X - Q) + Q
		// where Q is a point on g, S(phi) is the mirror transform
		// and phi/2 is the line's slope angle

		// get arbitrary point of line
		double qx, qy;
		if (Math.abs(g.x) > Math.abs(g.y)) {
			qx = -g.z / g.x;
			qy = 0.0d;
		} else {
			qx = 0.0d;
			qy = -g.z / g.y;
		}

		// translate -Q
		doTranslate(-qx, -qy);
		// do mirror transform
		mirror(2.0 * Math.atan2(-g.x, g.y));
		// translate +Q
		doTranslate(qx, qy);
	
		setAffineTransform();
		updateDegenerates(); // for degenerate conics		
	}

	/**
	* mirror transform with angle phi
	* [ cos    sin     0 ]
	* [ sin    -cos    0 ]
	* [ 0      0       1 ]
	*/
	final private void mirror(double phi) {
		// set rotated matrix
		double sum = matrix[0] + matrix[1];
		double diff = matrix[0] - matrix[1];
		double cos = Math.cos(phi);
		double sin = Math.sin(phi);
		// cos(2 phi) = cos(phi)\u00b2 - sin(phi)\u00b2 = (cos + sin)*(cos - sin)
		double cos2 = (cos + sin) * (cos - sin);
		// cos(2 phi) = 2 cos sin
		double sin2 = 2.0 * cos * sin;

		double temp = diff * cos2 + 2.0 * matrix[3] * sin2;
		double A0 = (sum + temp) / 2.0;
		double A1 = (sum - temp) / 2.0;
		double A3 = -matrix[3] * cos2 + diff * cos * sin;
		double A4 = matrix[4] * cos + matrix[5] * sin;
		matrix[5] = -matrix[5] * cos + matrix[4] * sin;
		matrix[0] = A0;
		matrix[1] = A1;
		matrix[3] = A3;
		matrix[4] = A4;

		// avoid classification: make changes by hand
		eigenvec[0].mirror(phi);
		eigenvec[1].mirror(phi);
					
		b.mirror(phi);	
		setMidpoint(new double[] {b.x,b.y});
	}

	// to avoid classification in movements 
	// this method is called to update
	// the lines (point) of degenerate conics
	final private void updateDegenerates() {
		// update lines of degenerate conic        
		switch (type) {
			case CONIC_SINGLE_POINT :
				singlePoint();
				break;

			case CONIC_INTERSECTING_LINES :
				intersectingLines(mu); // coefficient mu unchanged
				break;

			case CONIC_DOUBLE_LINE :
				doubleLine();
				break;

			case CONIC_PARALLEL_LINES :
				parallelLines(mu); // coefficient mu unchanged
				break;
		}
	}

	/*************************************
	 * CONIC CLASSIFICATION
	 *************************************/
	
	/**
	 * Sets both eigenvectors to e0, e1 on file load.
	 * (note: needed for "near-to-relationship" after loading a file)  
	 * @param x0 homogenous x-coord of e0
	 * @param y0 homogenous y-coord of e0
	 * @param z0 homogenous z-coord of e0
	 * @param x1 homogenous x-coord of e1
	 * @param y1 homogenous y-coord of e1
	 * @param z1 homogenous z-coord of e1
	 *  
	 */
	final public void setEigenvectors(
		double x0,
		double y0,
		double z0,
		double x1,
		double y1,
		double z1) {
				
		eigenvec[0].x = x0 / z0;
		eigenvec[0].y = y0 / z0;
		eigenvec[1].x = x1 / z1;
		eigenvec[1].y = y1 / z1;
		eigenvectorsSetOnLoad = true;
	}
	private boolean eigenvectorsSetOnLoad = false;
	
	
	protected void setFirstEigenvector(double[] coords){
		eigenvecX = coords[0];
		eigenvecY = coords[1];
	}
	
	final protected void setEigenvectors() {
		// newly calculated first eigenvector = (eigenvecX, eigenvecY)
		// old eigenvectors: eigenvec[0], eigenvec[1]        
		// set direction of eigenvectors with respect to old values:
		// take inner product >= 0 (small angle change)

		// make (eigenvecX, eigenvecY) a unit vector
		length = GeoVec2D.length(eigenvecX, eigenvecY);
		if (length != 1.0d) {
			eigenvecX = eigenvecX / length;
			eigenvecY = eigenvecY / length;
		}
		
		if (kernel.isContinuous()) {		
			// first eigenvector
			if (eigenvec[0].x * eigenvecX < -eigenvec[0].y * eigenvecY) {
				eigenvec[0].x = -eigenvecX;
				eigenvec[0].y = -eigenvecY;
			} else {
				eigenvec[0].x = eigenvecX;
				eigenvec[0].y = eigenvecY;
			}
			
			// second eigenvector (compared to normalvector (-eigenvecY, eigenvecX)
			if (eigenvec[1].y * eigenvecX < eigenvec[1].x * eigenvecY) {
				eigenvec[1].x = eigenvecY;
				eigenvec[1].y = -eigenvecX;
			} else {
				eigenvec[1].x = -eigenvecY;
				eigenvec[1].y = eigenvecX;
			}
		} 	
		// non-continous
		else if (!eigenvectorsSetOnLoad ){								
			eigenvec[0].x = eigenvecX;
			eigenvec[0].y = eigenvecY;
			eigenvec[1].x = -eigenvecY;
			eigenvec[1].y = eigenvecX;
		}		
		
		eigenvectorsSetOnLoad = false;
	}

	final private void setParabolicEigenvectors() {
		// fist eigenvector of parabola must not be changed

		// newly calculated first eigenvector = (eigenvecX, eigenvecY)
		// old eigenvectors: eigenvec[0], eigenvec[1]        
		// set direction of eigenvectors with respect to old values:
		// take inner product >= 0 (small angle change)

		// make (eigenvecX, eigenvecY) a unit vector
		length = GeoVec2D.length(eigenvecX, eigenvecY);
		if (length != 1.0d) {
			eigenvecX = eigenvecX / length;
			eigenvecY = eigenvecY / length;
		}

		// first eigenvector
		eigenvec[0].x = eigenvecX;
		eigenvec[0].y = eigenvecY;

		if (kernel.isContinuous()) {
			// second eigenvector (compared to normalvector (-eigenvecY, eigenvecX)
			if (eigenvec[1].y * eigenvecX < eigenvec[1].x * eigenvecY) {
				eigenvec[1].x = eigenvecY;
				eigenvec[1].y = -eigenvecX;
			} else {
				eigenvec[1].x = -eigenvecY;
				eigenvec[1].y = eigenvecX;
			}
		} 
		else if (!eigenvectorsSetOnLoad ){		
			// non-continous
			eigenvec[1].x = -eigenvecY;
			eigenvec[1].y = eigenvecX;
		}
		
		eigenvectorsSetOnLoad = false;
	}
	
	/**
	 * Makes all path parameters of points on this conic invalid if the 
	 * Eigenvectors have changed. This will force recalculation of the path parameters
	 * on the next call of pointChanged().
	 */
	private void makePathParametersInvalid() {		
		if (pointsOnConic == null) return;

		// eigenvectors have changed: we need to force an update of the
		// path parameters of all points on this conic.		
		getAffineTransform();
		if (oldTransform == null)
			oldTransform = new AffineTransform();
		boolean eigenVectorsSame = 
			Kernel.isEqual(transform.getScaleX(), oldTransform.getScaleX(), Kernel.MIN_PRECISION) ||
			Kernel.isEqual(transform.getScaleY(), oldTransform.getScaleY(), Kernel.MIN_PRECISION) ||
			Kernel.isEqual(transform.getShearX(), oldTransform.getShearX(), Kernel.MIN_PRECISION) ||
			Kernel.isEqual(transform.getShearY(), oldTransform.getShearY(), Kernel.MIN_PRECISION);

		if (!eigenVectorsSame) {				
			// updated old transform
			oldTransform.setTransform(transform);
													
			int size = pointsOnConic.size();
			for (int i=0; i < size; i++) {
				GeoPoint point = (GeoPoint) pointsOnConic.get(i);
				if (point.getPath() == this) {					
					point.getPathParameter().t = Double.NaN;				
				}
			}	
		}
	}
	

	
	private void classifyConic() {
		classifyConic(false);
	}
	
	private void classifyConic(boolean degenerate) {		
		defined = degenerate || checkDefined();		
		if (!defined)
			return;

		// det of S lets us distinguish between
		// parabolic and midpoint conics
		// det(S) = A[0] * A[1] - A[3] * A[3]
		if (isDetSzero()) {
			classifyParabolicConic(degenerate);
		} else {
			// det(S) = A[0] * A[1] - A[3] * A[3]
			detS = matrix[0] * matrix[1] - matrix[3] * matrix[3];
			classifyMidpointConic(degenerate);
		}		
		setAffineTransform();		
		
		// Application.debug("conic: " + this.getLabel() + " type " + getTypeString() );
		// Application.debug("           detS: " + (A0A1 - A3A3));ELLIPSE
	}
	
	
	/**
	 * Returns whether det(S) = A[0] * A[1] - A[3] * A[3] is zero.
	 * This method takes care of possibly large coefficients and adapts the precision
	 * used for the zero test automatically.    
	 */								
	private boolean isDetSzero() {	
		// get largest abs of A0, A1, A3
		double maxAbs = Math.abs(matrix[0]);
		double abs = Math.abs(matrix[1]);
		if (abs > maxAbs) maxAbs = abs;
		abs = Math.abs(matrix[3]);
		if (abs > maxAbs) maxAbs = abs;
				
		// det(S) = 0
		// A[0] * A[1] = A[3] * A[3]  
		// normalized: A[0]/maxAbs * A[1]/maxAbs = A[3]/maxAbs * A[3]/maxAbs 
		// use precision: eps * maxAbs^2	
		double eps;
		if (maxAbs > 1) {
			eps = kernel.getEpsilon() * maxAbs * maxAbs;
		} else {
			eps = kernel.getEpsilon();
		}
		return Kernel.isEqual(matrix[0]*matrix[1], matrix[3]*matrix[3], eps);				
	}

	/*************************************
	* midpoint conics
	*************************************/

	final private void classifyMidpointConic(boolean degenerate) {
		// calc eigenvalues and eigenvectors
		if (Kernel.isZero(matrix[3])) {
			// special case: submatrix S is allready diagonal
			eigenval[0] = matrix[0];
			eigenval[1] = matrix[1];
			eigenvecX = 1.0d;
			eigenvecY = 0.0d;
		} else {
			// eigenvalues are solutions of 
			// 0 = det(S - x E) = x\u00b2 - spurS x + detS                                   
			// detS was computed in classifyConic()            
			// init array for solver
			eigenval[0] = detS;
			eigenval[1] = - (matrix[0] + matrix[1]); // -spurS
			eigenval[2] = 1.0d;
			eqnSolver.solveQuadratic(eigenval, eigenval);
	
			// set first eigenvector
			eigenvecX = -matrix[3];
			eigenvecY = -eigenval[0] + matrix[0];
		}

		// calc translation vector b = midpoint
		// b = -Inverse[S] . a, where a = (A[4], A[5])    
		/*
		b.x = (matrix[3] * matrix[5] - matrix[1] * matrix[4]) / detS;
		b.y = (matrix[3] * matrix[4] - matrix[0] * matrix[5]) / detS;
		*/
		setMidpoint(new double[]{
				(matrix[3] * matrix[5] - matrix[1] * matrix[4]) / detS,
				(matrix[3] * matrix[4] - matrix[0] * matrix[5]) / detS
		});

		// beta = a . b + alpha, where alpha = A[2]
		double beta = matrix[4] * b.x + matrix[5] * b.y + matrix[2];

		// beta lets us distinguish between Ellipse, Hyperbola,
		// single singlePoint and intersecting lines
		//  if (Kernel.isZero(beta)) {
		if (degenerate || Kernel.isZero(beta)) {
			setEigenvectors();
			// single point or intersecting lines
			mu[0] = eigenval[0] / eigenval[1];
			if (Kernel.isZero(mu[0])) {
				mu[0] = 0.0;
				intersectingLines(mu);
			} else if (mu[0] < 0.0d) {
				mu[0] = Math.sqrt(-mu[0]);
				intersectingLines(mu);
			} else {												
				singlePoint();
			}
		} else {
			// Hyperbola, Ellipse, empty            
			mu[0] = -eigenval[0] / beta;
			mu[1] = -eigenval[1] / beta;
			if (detS < 0) {
				hyperbola(mu);
			} else {
				if (mu[0] > 0 && mu[1] > 0) {
					ellipse(mu);
				} else {
					empty();
				}
			}
		}
	}

	final protected void singlePoint() {
		type = GeoConic.CONIC_SINGLE_POINT;

		if (singlePoint == null)
			singlePoint = new GeoPoint(cons);
		singlePoint.setCoords(b.x, b.y, 1.0d);
		//Application.debug("singlePoint : " + b);
	}

	final private void intersectingLines(double[] mu) {
		type = GeoConic.CONIC_INTERSECTING_LINES;

		// set intersecting lines
		if (lines == null) {
			lines = new GeoLine[2];
			lines[0] = new GeoLine(cons);
			lines[1] = new GeoLine(cons);
		}
		// n = T . (-mu, 1)
		temp1 = eigenvec[0].x * mu[0];
		temp2 = eigenvec[0].y * mu[0];
		nx = eigenvec[1].x - temp1;
		ny = eigenvec[1].y - temp2;

		// take line with smallest change of direction
		if (Math.abs(nx * lines[0].x + ny * lines[0].y)
			< Math.abs(nx * lines[1].x + ny * lines[1].y))
			index = 1;
		else
			index = 0;

		lines[index].x = nx;
		lines[index].y = ny;
		lines[index].z = - (nx * b.x + ny * b.y);

		// n = T . (mu, 1)
		nx = eigenvec[1].x + temp1;
		ny = eigenvec[1].y + temp2;
		index = 1 - index;
		lines[index].x = nx;
		lines[index].y = ny;
		lines[index].z = - (nx * b.x + ny * b.y);
		
		setStartPointsForLines();
		//Application.debug("intersectingLines: " + lines[0] + ", " + lines[1]);
	}

	final private void ellipse(double[] mu) {
		if (mu[0] > mu[1]) {
			// swap eigenvectors and mu            
			temp = mu[0];
			mu[0] = mu[1];
			mu[1] = temp;

			// rotate eigenvector 90�
			temp = eigenvecX;
			eigenvecX = -eigenvecY;
			eigenvecY = temp;
		}
		setEigenvectors();

		// circle 
		if (Kernel.isEqual(mu[0], mu[1])) {
			type = GeoConic.CONIC_CIRCLE;
			halfAxes[0] = Math.sqrt(1.0d / mu[0]);
			halfAxes[1] = halfAxes[0];
			linearEccentricity = 0.0d;
			eccentricity = 0.0d;
			//Application.debug("circle: M = " + b + ", r = " + halfAxes[0]);    
		} else { // elipse
			type = GeoConic.CONIC_ELLIPSE;
			mu[0] = 1.0d / mu[0];
			mu[1] = 1.0d / mu[1];
			halfAxes[0] = Math.sqrt(mu[0]);
			halfAxes[1] = Math.sqrt(mu[1]);
			linearEccentricity = Math.sqrt(mu[0] - mu[1]);
			eccentricity = linearEccentricity / mu[0];

			/*
			Application.debug("Ellipse");            
			Application.debug("a : " + halfAxes[0]);
			Application.debug("b : " + halfAxes[1]);
			Application.debug("e : " + excent);                                  
			*/
		}
	}

	final private void hyperbola(double[] mu) {
		type = GeoConic.CONIC_HYPERBOLA;
		if (mu[0] < 0) {
			// swap eigenvectors and mu            
			temp = mu[0];
			mu[0] = mu[1];
			mu[1] = temp;

			// rotate eigenvector 90�
			temp = eigenvecX;
			eigenvecX = -eigenvecY;
			eigenvecY = temp;
		}
		setEigenvectors();

		mu[0] = 1.0d / mu[0];
		mu[1] = -1.0d / mu[1];
		halfAxes[0] = Math.sqrt(mu[0]);
		halfAxes[1] = Math.sqrt(mu[1]);
		linearEccentricity = Math.sqrt(mu[0] + mu[1]);
		eccentricity = linearEccentricity / mu[0];

		/*
		Application.debug("Hyperbola");            
		Application.debug("a : " + halfAxes[0]);
		Application.debug("b : " + halfAxes[1]);
		Application.debug("e : " + excent);                  
		 */
	}

	/*
	final private void empty() {
		type = GeoConic.CONIC_EMPTY;
		// Application.debug("empty conic");
	}
	*/

	/*************************************
	* parabolic conics
	*************************************/

	final private void classifyParabolicConic(boolean degenerate) {			
		// calc eigenvalues and first eigenvector               
		if (Kernel.isZero(matrix[3])) {						
			// special cases: submatrix S is allready diagonal
			// either A[0] or A[1] have to be zero (due to detS = 0)
			if (Kernel.isZero(matrix[0])) {

				// special case: the submatrix S is zero!!!
				if (Kernel.isZero(matrix[1])) {
					handleSzero();
					return;
				}
								
				// else
				lambda = matrix[1];
				//	set first eigenvector
				eigenvecX = 1.0d; 
				eigenvecY = 0.0d;	
				// c = a . T = a, 
				// where T is the matrix of the eigenvectors and a = (A[4], A[5])                
				c.x = matrix[4];
				c.y = matrix[5];
			} else { // A[1] is zero                
				lambda = matrix[0];
				eigenvecX = 0.0d; // set first eigenvector
				eigenvecY = 1.0d;
				// c = a . T, 
				// where T is the matrix of the eigenvectors and a = (A[4], A[5])                
				c.x = matrix[5];
				c.y = -matrix[4];
			}
		} 
		else { // A[3] != 0			
			// eigenvalues are solutions of 
			// 0 = det(S - x E) = x^2 - spurS x + detS = x (x - spurS)                                    
			lambda = matrix[0] + matrix[1]; // spurS                         
			// set first eigenvector as a unit vector (needed fo computing vector c)
			length = GeoVec2D.length(matrix[3], matrix[0]);
			eigenvecX = matrix[3] / length;
			eigenvecY = -matrix[0] / length;
			// c = a . T, 
			// where T is the matrix of the eigenvectors and a = (A[4], A[5])                
			c.x = matrix[4] * eigenvecX + matrix[5] * eigenvecY;
			c.y = matrix[5] * eigenvecX - matrix[4] * eigenvecY;
		}		

		if (degenerate || Kernel.isZero(c.x)) {
			setEigenvectors();
			// b = T . (0, -c.y/lambda)
			temp = c.y / lambda;
			/*
			b.x = temp * eigenvecY;
			b.y = -temp * eigenvecX;
			*/
			setMidpoint(new double[]{
					temp * eigenvecY,
					-temp * eigenvecX
			});
			mu[0] = -temp * temp + matrix[2] / lambda;
			if (Kernel.isZero(mu[0])) {			
				doubleLine();
			} else if (mu[0] < 0) {			
				mu[0] = Math.sqrt(-mu[0]);				
				parallelLines(mu);
			} else {
				empty();
			}
		} else { // parabola						
			parabola();
		}

	}

	final private void doubleLine() {
		type = GeoConic.CONIC_DOUBLE_LINE;

		// set double line
		if (lines == null) {
			lines = new GeoLine[2];
			lines[0] = new GeoLine(cons);
			lines[1] = new GeoLine(cons);
		}
		nx = -eigenvec[0].y;
		ny = eigenvec[0].x;
		lines[0].x = nx;
		lines[0].y = ny;
		lines[0].z = - (b.x * nx + b.y * ny);

		lines[1].x = lines[0].x;
		lines[1].y = lines[0].y;
		lines[1].z = lines[0].z;
		
		//setStartPointsForLines();
		//Application.debug("double line : " + lines[0]);
	}
	
	/**
	 * Change this conic to double line
	 */
	final public void enforceDoubleLine() {
		defined = true;
		doubleLine();
	}
	
	// if S is the zero matrix, set conic as double line or empty
	final private void handleSzero() {			
		// conic is line 2*A[4] * x +  2*A[5] * y + A[2] = 0				
	    if (Kernel.isZero(matrix[4])) {
	    	if (Kernel.isZero(matrix[5])) {	    		
	    		empty();
	    		return;
	    	} 
	    	
	    	// A[5] not zero
	    	// make b a point on the line
	    	/*
	    	b.x = 0;
	    	b.y = -matrix[2] / (2*matrix[5]);
	    	*/	    	
	    	setMidpoint(new double[]{
	    			0,
	    			-matrix[2] / (2*matrix[5])
	    	});
	    } else { 
	    	// A[4] not zero
	    	// make b a point on the line
	    	/*
	    	b.x = -matrix[2] / (2*matrix[4]);
	    	b.y = 0;	  
	    	*/  
	    	setMidpoint(new double[]{
	    			-matrix[2] / (2*matrix[4]),
	    			0
	    	});
	    }
	    
	    eigenvecX =  matrix[5];
    	eigenvecY = -matrix[4];
    	setEigenvectors();
    	    	
	    doubleLine();    			
	}	

	final private void parallelLines(double[] mu) {
		type = GeoConic.CONIC_PARALLEL_LINES;

		// set double line
		if (lines == null) {
			lines = new GeoLine[2];
			lines[0] = new GeoLine(cons);
			lines[1] = new GeoLine(cons);
		}
		nx = -eigenvec[0].y;
		ny = eigenvec[0].x;
		temp1 = b.x * nx + b.y * ny;
		lines[0].x = nx;
		lines[0].y = ny;
		lines[1].x = nx;
		lines[1].y = ny;
		// smallest change: 
		temp2 = mu[0] - temp1;
		if (Math.abs(lines[0].z - temp2) < Math.abs(lines[1].z - temp2)) {
			lines[0].z = temp2;
			lines[1].z = -temp1 - mu[0];
		} else {
			lines[0].z = -temp1 - mu[0];
			lines[1].z = temp2;
		}
		
		setStartPointsForLines();
		
		// Application.debug("parallel lines : " + lines[0] + ", " + lines[1]);
		// Application.debug("coeff : " + mu[0]);
	}
	
	private void setStartPointsForLines() {
		// make sure we have a start point to compute line parameter	
		if (startPoints == null) {
			startPoints = new GeoPoint[2];
			for (int i=0; i < 2; i++) {
				startPoints[i] = new GeoPoint(cons);
			}
		}
		
		// update start points
		for (int i=0; i < 2; i++) {		
			lines[i].setStartPoint(null);
			lines[i].getPointOnLine(startPoints[i]);
			lines[i].setStartPoint(startPoints[i]);
		}		
	}

	final private void parabola() {
		type = GeoConic.CONIC_PARABOLA;

		// calc vertex = b
		// b = T . ((c.y\u00b2/lambda - A2)/(2 c.x) , -c.y/lambda)
		temp2 = c.y / lambda;
		temp1 = (c.y * temp2 - matrix[2]) / (2 * c.x);
		/*
		b.x = eigenvecY * temp2 + eigenvecX * temp1;
		b.y = eigenvecY * temp1 - eigenvecX * temp2;
		*/
    	setMidpoint(new double[]{
    			eigenvecY * temp2 + eigenvecX * temp1,
    			eigenvecY * temp1 - eigenvecX * temp2
    	});
		setParabolicEigenvectors();

		// parameter p of parabola
		p = -c.x / lambda;
		if (p < 0) { // change orientation of first eigenvector
			eigenvec[0].x = -eigenvec[0].x;
			eigenvec[0].y = -eigenvec[0].y;
			p = -p;
		}

		linearEccentricity = p / 2;
		eccentricity = 1;

		/*
		Application.debug("parabola");
		Application.debug("Vertex: " + b);
		Application.debug("p = " + p);
		*/
	}
	

	/**********************************************************
	 * CACLCULATIONS ON CONIC (det, evaluate, intersect, ...)
	 **********************************************************/

	/** 
	 * Computes the determinant of a conic's 3x3 matrix.
	 * @param matrix flat matrix of conic section 
	 * @return matrix determinant
	 */
	public static double det(double [] matrix) {
		return matrix[0] * (matrix[1] * matrix[2] - matrix[5] * matrix[5])
			- matrix[2] * matrix[3] * matrix[3]
			- matrix[1] * matrix[4] * matrix[4]
			+ 2 * matrix[3] * matrix[4] * matrix[5];
	}
		
	
	/**
	 * Returns true iff the determinant of 2x2 matrix of eigenvectors is
	 * positive. 
	 * @return true iff the determinant of 2x2 matrix of eigenvectors is
	 * positive. 
	 */
	final boolean hasPositiveEigenvectorOrientation() {
		//return eigenvec[0].x * eigenvec[1].y - eigenvec[0].y * eigenvec[1].x > 0;
		return eigenvec[0].x * eigenvec[1].y > eigenvec[0].y * eigenvec[1].x;
	}

	/**
	 * Sets orientation of eigenvectors to positive or negative
	 * @param flag true for positive, false for negative
	 */
	final void setPositiveEigenvectorOrientation(boolean flag) {
			if (flag != hasPositiveEigenvectorOrientation()) {
				eigenvec[1].x = -eigenvec[1].x;
				eigenvec[1].y = -eigenvec[1].y;
				
				setAffineTransform();
			}
	}
	
	
	/** 
	 * states wheter P lies on this conic or not 
	 * @return true iff P lies on this conic
	 * @param P
	 * @param eps precision
	 */
	 public boolean isIntersectionPointIncident(GeoPoint P, double eps) {		
		return isOnFullConic(P, eps);
	 }
	 
	 /** 
	 * states wheter P lies on this conic or not. Note: this method
	 * is not overwritten by subclasses like isIntersectionPointIncident()
	 * @return true P lies on this conic
	 * @param P
	 * @param eps precision
	 */
	final boolean isOnFullConic(GeoPoint P, double eps) {						
		switch (type) {	
			 case GeoConic.CONIC_SINGLE_POINT:                         
	            return P.distance(singlePoint) < eps;                             
	            
	        case GeoConic.CONIC_INTERSECTING_LINES:  
	        case GeoConic.CONIC_DOUBLE_LINE: 
	        case GeoConic.CONIC_PARALLEL_LINES:                
	            return lines[0].isOnFullLine(P, eps) || lines[1].isOnFullLine(P, eps);	
	            
	        case GeoConic.CONIC_LINE:                
	            return lines[0].isOnFullLine(P, eps);	    
	        
	        case GeoConic.CONIC_EMPTY:
	        	return false;
		}        
		
		// if we get here let's handle the remaining cases
				
     	// remember coords of P
		double Px = P.x;
		double Py = P.y;
		double Pz = P.z;
														
		// convert P to eigenvector coord system
		coordsRWtoEV(P);	
		double px = P.x / P.z;
		double py = P.y / P.z;
		
		boolean result = false;			
		switch (type) {	
			case GeoConic.CONIC_CIRCLE:
			  	// x^2 + y^2 = r^2
				double radius2 = halfAxes[0]*halfAxes[0];
			  	result = Kernel.isEqual(px*px/radius2 + py*py/radius2, 1, eps);
				break;		   					
		  	
			case GeoConic.CONIC_ELLIPSE:
          		// x^2/a^2 + y^2/b^2 = 1
			  	result = Kernel.isEqual(px*px / (halfAxes[0]*halfAxes[0]) + py*py / (halfAxes[1]*halfAxes[1]), 1, eps);
				break;	
				
			case GeoConic.CONIC_HYPERBOLA:   
	          	// 	x^2/a^2 - y^2/b^2 = 1
			  	result = Kernel.isEqual(px*px / (halfAxes[0]*halfAxes[0]), 1 + py*py / (halfAxes[1]*halfAxes[1]), eps);
				break;	
				
			case GeoConic.CONIC_PARABOLA: 
          		// y^2 = 2 p x								               
                result = Kernel.isEqual(py*py, 2*p*px, eps);
				break;	
		}
			
		// restore coords of P
		P.x = Px; 
		P.y = Py; 
		P.z = Pz;
		return result;				
	}

	/**
	 * return wheter this conic represents the same conic as c 
	 * (this = lambda * c).
	 */
	public boolean isEqual(GeoElement geo) {

		if (!geo.isGeoConic()) return false;
		
		GeoConic c = (GeoConic)geo;
		double[] B = c.matrix;

		double lambda = 0.0;
		boolean aZero, bZero, equal = true;
		for (int i = 0; i < 6; i++) {
			aZero = Kernel.isZero(matrix[i]);
			bZero = Kernel.isZero(B[i]);

			// A[i] == 0 and B[i] != 0  => not equal
			if (aZero && !bZero)
				equal = false;
			// B[i] == 0 and A[i] != 0  => not equal
			else if (bZero && !aZero)
				equal = false;
			// A[i] != 0 and B[i] != 0
			else if (!aZero && !bZero) {
				// init lambda?
				if (lambda == 0.0)
					lambda = matrix[i] / B[i];
				// check equality
				else
					equal = Kernel.isEqual(matrix[i], lambda * B[i]);
			}
			// leaf loop
			if (!equal)
				break;
		}
		return equal;
	}

	/**
	 * evaluates P . A . P
	 * @param P point for the conic to be evaluated at
	 * @return 0 iff P lies on conic
	 */
	final public double evaluate(GeoPoint P) {
		return P.x * (matrix[0] * P.x + matrix[3] * P.y + matrix[4] * P.z)
			+ P.y * (matrix[3] * P.x + matrix[1] * P.y + matrix[5] * P.z)
			+ P.z * (matrix[4] * P.x + matrix[5] * P.y + matrix[2] * P.z);
	}

	/**
	 * evaluates (p.x, p.y, 1) . A . (p.x, p.y, 1)
	 * @param p inhomogenous coords of a point
	 * @return 0 iff (p.x, p.y, 1) lies on conic
	 */
	public final double evaluate(GeoVec2D p) {
		return matrix[2]
			+ matrix[4] * p.x
			+ matrix[5] * p.y
			+ p.y * (matrix[5] + matrix[3] * p.x + matrix[1] * p.y)
			+ p.x * (matrix[4] + matrix[0] * p.x + matrix[3] * p.y);
	}

	/**
	 * evaluates (x, y, 1) . A . (x, y, 1)
	 * @param x inhomogenous x-coord of a point
	 * @param y inhomogenous y-coord of a point
	 * @return 0 iff (p.x, p.y, 1) lies on conic
	 */
	final double evaluate(double x, double y) {
		return matrix[2]
			+ matrix[4] * x
			+ matrix[5] * y
			+ y * (matrix[5] + matrix[3] * x + matrix[1] * y)
			+ x * (matrix[4] + matrix[0] * x + matrix[3] * y);
	}

	/**
	 *  Sets the GeoLine polar to A.P, the polar line of P relativ to this conic.
	 * @param P point to which we want the polar
	 * @param polar GeoLine in which the result should be stored 
	 */
	final public void polarLine(GeoPoint P, GeoLine polar) {
		//<Zbynek Konecny, 2010-03-15>
		if(!isDefined()){
			polar.setUndefined();
		}
		else{
		//</Zbynek>	
			polar.x = matrix[0] * P.x + matrix[3] * P.y + matrix[4] * P.z;
			polar.y = matrix[3] * P.x + matrix[1] * P.y + matrix[5] * P.z;
			polar.z = matrix[4] * P.x + matrix[5] * P.y + matrix[2] * P.z;
		}
	}

	/**
	 * Sets the GeoLine diameter to X.S.v + a.v (v is a direction), 
	 * the diameter line parallel to v relativ to this conic.
	 * @param v direction of diameter
	 * @param diameter GeoLine for storing the result
	 */
	final public void diameterLine(GeoVector v, GeoLine diameter) {
		diameter.x = matrix[0] * v.x + matrix[3] * v.y;
		diameter.y = matrix[3] * v.x + matrix[1] * v.y;
		diameter.z = matrix[4] * v.x + matrix[5] * v.y;
	}

	/**
	 * returns all class-specific xml tags for saveXML
	 */
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);
		//	line thickness and type  
		getLineStyleXML(sb);

		sb.append("\t<eigenvectors ");
		sb.append(" x0=\"" + eigenvec[0].x + "\"");
		sb.append(" y0=\"" + eigenvec[0].y + "\"");
		sb.append(" z0=\"1.0\"");
		sb.append(" x1=\"" + eigenvec[1].x + "\"");
		sb.append(" y1=\"" + eigenvec[1].y + "\"");
		sb.append(" z1=\"1.0\"");
		sb.append("/>\n");

		// matrix must be saved after eigenvectors
		// as only <matrix> will cause a call to classifyConic()
		// see geogebra.io.MyXMLHandler: handleMatrix() and handleEigenvectors()
		sb.append("\t<matrix");
		for (int i = 0; i < 6; i++)
			sb.append(" A" + i + "=\"" + matrix[i] + "\"");
		sb.append("/>\n");

		// implicit or specific mode
		switch (toStringMode) {
			case GeoConic.EQUATION_SPECIFIC :
				sb.append("\t<eqnStyle style=\"specific\"/>\n");
				break;

			case GeoConic.EQUATION_EXPLICIT :
				sb.append("\t<eqnStyle style=\"explicit\"/>\n");
				break;
				
			default :
				sb.append("\t<eqnStyle style=\"implicit\"/>\n");
		}

	}

	/**
	 * Returns description of current specific equation
	 * @return description of current specific equation
	 */
	public String getSpecificEquation() {
		  String ret = null;
		  switch (type) {
			 case GeoConic.CONIC_CIRCLE:
				 ret = app.getPlain("CircleEquation");
				 break;

			 case GeoConic.CONIC_ELLIPSE:
				 ret = app.getPlain("EllipseEquation");
				 break;

			 case GeoConic.CONIC_HYPERBOLA:
				 ret = app.getPlain("HyperbolaEquation");
				 break;
 
			 case GeoConic.CONIC_PARABOLA:
				 ret = app.getPlain("ParabolaEquation");
				 break;        
    
			 case GeoConic.CONIC_DOUBLE_LINE:
				 ret = app.getPlain("DoubleLineEquation");
				 break;      
    
			 case GeoConic.CONIC_PARALLEL_LINES:
			 case GeoConic.CONIC_INTERSECTING_LINES:
				 ret = app.getPlain("ConicLinesEquation");
				 break;                  
				 
			 case GeoConic.CONIC_LINE:
				 ret = app.getPlain("DoubleLineEquation");
				 break;                  

		  }
		 return ret;
	  }

   

	/* 
	 * Path interface
	 */
	 
	public void pointChanged(GeoPointInterface PI) {
		
		GeoPoint P = (GeoPoint) PI;
		
		double px, py;		
		PathParameter pp = P.getPathParameter();
		pp.pathType = type;
			
		switch (type) {
			case CONIC_EMPTY:
				P.x = Double.NaN;
				P.y = Double.NaN;
				P.z = Double.NaN;
			break;
			
			case CONIC_SINGLE_POINT:
				P.x = singlePoint.x;
				P.y = singlePoint.y;
				P.z = singlePoint.z;
			break;
			
			case CONIC_INTERSECTING_LINES:
			case CONIC_PARALLEL_LINES:
				/* 
				 * For line conics, we use the parameter ranges 
				 *   first line: t = (-1, 1)
				 *   second line: t = (1, 3)
				 * and convert this to s = (-inf, inf) using		
				 *   first line: s = t /(1 - abs(t)) 
				 *   second line:  s = (t-2) /(1 - abs(t-2))
				 * which allows us to use the line's path parameter s
				 */
				
				// choose closest line
				boolean firstLine = lines[0].distanceHom(P) <= lines[1].distanceHom(P);
				GeoLine line = firstLine ? lines[0] : lines[1];
				
				// compute line path parameter
				line.pointChanged(P);
							
				// convert line parameter to (-1,1)
				pp.t = PathMoverGeneric.inverseInfFunction(pp.t);
				if (!firstLine) {
					pp.t = pp.t + 2;// convert from (-1,1) to (1,3)									
				}				
			break;
			
			case CONIC_LINE:
			case CONIC_DOUBLE_LINE:
				lines[0].pointChanged(P);
			break;
			
			case CONIC_CIRCLE:
			case CONIC_ELLIPSE:			
				//	transform to eigenvector coord-system
				coordsRWtoEV(P);				
				
				// calc parameter 
				px = P.x / P.z;
				py = P.y / P.z;		
				
				// relation between the internal parameter t and the angle theta:
				// t = atan(a/b tan(theta)) where tan(theta) = py / px
				pp.t = Math.atan2(halfAxes[0]*py, halfAxes[1]*px);											
				
				// calc Point on conic using this parameter
				P.x = halfAxes[0] * Math.cos(pp.t);	
				P.y = halfAxes[1] * Math.sin(pp.t);												
				P.z = 1.0;
				
				//	transform back to real world coord system
				coordsEVtoRW(P);				
			break;			
			
			case CONIC_HYPERBOLA:
				/* 
				 * For hyperbolas, we use the parameter ranges 
				 *   right branch: t = (-1, 1)
				 *   left branch: t = (1, 3)
				 * and get this from  s = (-inf, inf) using		
				 *   right branch: s = t /(1 - abs(t)) 
				 * where we use the parameter form
				 *   (a*cosh(s), b*sinh(s))
				 * for the right branch of the hyperbola.
				 */ 
				
				// transform to eigenvector coord-system
				coordsRWtoEV(P);
				px = P.x / P.z;
				py = P.y / P.z;
				
				// calculate s in (-inf, inf) and keep py				
				double s = MyMath.asinh(py / halfAxes[1]);
				P.x = halfAxes[0] * MyMath.cosh(s);	
				P.y = py;
				P.z = 1.0;

				// compute t in (-1,1) from s in (-inf, inf)
				pp.t = PathMoverGeneric.inverseInfFunction(s);	
				if (px < 0) { // left branch									
					pp.t = pp.t + 2; // convert (-1,1) to (1,3)
					P.x = -P.x;
				}		

				// transform back to real world coord system
				coordsEVtoRW(P);													
			break;																			
			
			case CONIC_PARABOLA:
				//	transform to eigenvector coord-system
				coordsRWtoEV(P);
				
				// keep py
				py = P.y / P.z;
				pp.t = py / p;
				P.x = p * pp.t  * pp.t  / 2.0;
				P.y = py;
				P.z = 1.0; 									
				
				// transform back to real world coord system
				coordsEVtoRW(P);		
			break;
		}		
	}
	
	public void pathChanged(GeoPointInterface PI) {
		
		GeoPoint P = (GeoPoint) PI;
		
		// if type of path changed (other conic) then we
		// have to recalc the parameter with pointChanged()
		PathParameter pp = P.getPathParameter();		
		if (pp.pathType != type || Double.isNaN(pp.t)) {		
			pointChanged(P);
			return;
		}
		
		switch (type) {
			case CONIC_EMPTY:
				P.x = Double.NaN;
				P.y = Double.NaN;
				P.z = Double.NaN;
				break;
			
			case CONIC_SINGLE_POINT:
				P.x = singlePoint.x;
				P.y = singlePoint.y;
				P.z = singlePoint.z;
				break;
			
			case CONIC_INTERSECTING_LINES:
			case CONIC_PARALLEL_LINES:
				/* 
				 * For line conics, we use the parameter ranges 
				 *   first line: t = (-1, 1)
				 *   second line: t = (1, 3)
				 * and convert this to s = (-inf, inf) using		
				 *   first line: s = t /(1 - abs(t)) 
				 *   second line:  s = (t-2) /(1 - abs(t-2))
				 * which allows us to use the line's path parameter s
				 */ 
				double pathParam = pp.t;
				boolean leftBranch = pathParam > 1;
				pp.t = leftBranch ? pathParam - 2 : pathParam;
				// convert from (-1,1) to (-inf, inf) line path parameter
				pp.t = pp.t /(1 - Math.abs(pp.t));
				if (leftBranch) {
					lines[1].pathChanged(P);					 
				} else {
					lines[0].pathChanged(P);										
				}
						
				// set our path parameter again
				pp.t = pathParam;
				break;
				
			case CONIC_LINE:
			case CONIC_DOUBLE_LINE:
				lines[0].pathChanged(P);	
				break;
			
			case CONIC_CIRCLE:
			case CONIC_ELLIPSE:						
				// calc Point on conic using this parameter (in eigenvector space)
				P.x = halfAxes[0] * Math.cos(pp.t);	
				P.y = halfAxes[1] * Math.sin(pp.t);
				P.z = 1.0;		
				
				// transform back to real world coord system
				coordsEVtoRW(P);
				break;			
			
			case CONIC_HYPERBOLA:			
				/* 
				 * For hyperbolas, we use the parameter ranges 
				 *   right branch: t = (-1, 1)
				 *   left branch: t = (1, 3)
				 * and convert this to s = (-inf, inf) using		
				 *   right branch: s = t /(1 - abs(t)) 
				 *   left branch:  s = (t-2) /(1 - abs(t-2))
				 * which allows us to use the parameter form
				 *   (a*cosh(s), b*sinh(s))
				 * for the right branch of the hyperbola.
				 */ 
				leftBranch = pp.t > 1;
				double t = leftBranch ? pp.t - 2 : pp.t;
				double s = t /(1 - Math.abs(t));
				
				P.x = halfAxes[0] * MyMath.cosh(s);
				P.y = halfAxes[1] * MyMath.sinh(s);
				P.z = 1.0;				
				if (leftBranch) P.x = -P.x;
				
				// transform back to real world coord system
				coordsEVtoRW(P);
				break;																			
			
			case CONIC_PARABOLA:
				P.y = p * pp.t;				
				P.x = P.y * pp.t  / 2.0;				
				P.z = 1.0;	
				
				// transform back to real world coord system
				coordsEVtoRW(P);
				break;
		}
	}
	
	public boolean isOnPath(GeoPointInterface PI, double eps) {
		
		GeoPoint P = (GeoPoint) PI;
		
		if (P.getPath() == this)
			return true;
		
		return isOnFullConic(P, eps);
	}
	
	/**
	 * Returns the smallest possible parameter value for this path
	 * @return the smallest possible parameter value for this
	 * path (may be Double.NEGATIVE_INFINITY)
	 */
	public double getMinParameter() {
		switch (type) {		
			case CONIC_PARABOLA:
			case CONIC_DOUBLE_LINE:
			case CONIC_LINE:
				return Double.NEGATIVE_INFINITY;
				
			case CONIC_HYPERBOLA:
			case CONIC_INTERSECTING_LINES:
			case CONIC_PARALLEL_LINES:
				// For hyperbolas and line conics, we use the parameter ranges 
				//   right branch: t = (-1, 1)
				//   left branch: t = (1, 3)
				return -1;
															
			case CONIC_EMPTY:										
			case CONIC_SINGLE_POINT:
			case CONIC_CIRCLE:
			case CONIC_ELLIPSE:
			default:
				return 0;		
		}		
	}
	
	/**
	 * Returns the largest possible parameter value for this path
	 * @return the largest possible parameter value for this
	 * path (may be Double.POSITIVE_INFINITY)
	 */
	public double getMaxParameter() {
		switch (type) {
			case CONIC_DOUBLE_LINE:			
			case CONIC_PARABOLA:
			case CONIC_LINE:
				return Double.POSITIVE_INFINITY;
									
			case CONIC_CIRCLE:
			case CONIC_ELLIPSE:
				return Kernel.PI_2;
				
			case CONIC_HYPERBOLA:
			case CONIC_INTERSECTING_LINES:
			case CONIC_PARALLEL_LINES:
				// For hyperbolas and line conics, we use the parameter ranges 
				//   right branch: t = (-1, 1)
				//   left branch: t = (1, 3)
				return 3;
				
			case CONIC_EMPTY:										
			case CONIC_SINGLE_POINT:
			default:
				return 0;		
		}		
	}
	
	public PathMover createPathMover() {
		return new PathMoverGeneric(this);
	}		
	
	public boolean isClosedPath() {
		switch (type) {			
			case CONIC_CIRCLE:
			case CONIC_ELLIPSE:
				return true;
	
			default:
				return false;		
		}		
	}
	
	public boolean isNumberValue() {
		return false;
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
	
	final public boolean isGeoConic() {
		return true;
	}
	
	public void setZero() {
		setCoeffs(1,0,1,0,0,0);
	}

	public boolean isVector3DValue() {
		return false;
	}
	
	
	
	protected void setMidpoint(double[] coords){
		b.x = coords[0];
		b.y = coords[1];
		
		//GeoQuadridND compatibility
		super.setMidpoint(coords);

	}
	
	 public String getAssignmentOperator() {
		 return ": ";
	 }


	/*
	* Region interface implementation
	*/
		
	public boolean isRegion() {
		return true;
	}
		

	
	public boolean isInRegion(GeoPointInterface PI) {
		double x0 = PI.getX2D();
		double y0 = PI.getY2D();
		
		return isInRegion(x0,y0);
		
	}
	
	/**
	 * Returns true if point is in circle/ellipse. Coordinates of PointInterface
	 * are used directly to avoid rounding errors.
	 * @author Michael Borcherds
	 * @version 2010-05-17
	 * Last change Zbynek Konecny 
	 */
	public boolean isInRegion(double x0, double y0) {
		
		switch (type) {
		case CONIC_CIRCLE:
			return (x0 - b.x) * (x0 - b.x) + (y0 - b.y) * (y0 - b.y) <= halfAxes[0] * halfAxes[0];
			
		case CONIC_ELLIPSE: //multiplication by matrix[0] for c:1-x^2-2y^2=0
			return matrix[0]*(matrix[0] * x0 * x0 + matrix[1] * y0 * y0 + matrix[2] +
			2 * (matrix[3] * x0 * y0 + matrix[4] * x0 + matrix[5] * y0)) <= 0;
			default:
				return false;
		}
	}
	

	/**
	 * Point's parameters are set to its EV coordinates
	 * @version 2010-07-30
	 * Last change: Zbynek Konecny
	 */
	
	public void pointChangedForRegion(GeoPointInterface PI) {
		PI.updateCoords2D();

		RegionParameters rp = PI.getRegionParameters();

		if (!isInRegion(PI)){

			pointChanged(PI);
			rp.setIsOnPath(true);
		}else{
			GeoPoint P=(GeoPoint)PI;
			rp.setIsOnPath(false);
				
			coordsRWtoEV(P);
			rp.setT1(P.x/this.halfAxes[0]);
			rp.setT2(P.y/this.halfAxes[1]);
			coordsEVtoRW(P);
		}
	}


	/**
	 * When elipse is moved, the points moves as well
	 * and its EV coordinates remain the same
	 * @version 2010-07-30
	 * Last change: Zbynek Konecny
	 */
	
	public void regionChanged(GeoPointInterface PI) {
		//GeoPoint P = (GeoPoint) PI;
		RegionParameters rp = PI.getRegionParameters();
		
		if (rp.isOnPath())
			pathChanged(PI);
		else{
			//pointChangedForRegion(P);
			GeoPoint P=(GeoPoint)PI;
			P.isDefined();
			P.x=rp.getT1()*halfAxes[0];
			P.y=rp.getT2()*halfAxes[1];
			P.z = 1.0;
			coordsEVtoRW(P);
		}
	}
	
	/**
	 * Sets curve to this conic
	 * @param curve curve for storing this conic
	 */
	public void toGeoCurveCartesian(GeoCurveCartesian curve) {
		FunctionVariable fv = new FunctionVariable(kernel,"t");
		ExpressionNode evX=null,evY=null;
		double min=0,max=0;
		if(type == CONIC_CIRCLE){
			evX = new ExpressionNode(kernel, 
					new ExpressionNode(kernel,fv,ExpressionNode.COS,null),
					ExpressionNode.MULTIPLY,
					new MyDouble(kernel,halfAxes[0]));
			evY = new ExpressionNode(kernel, 
					new ExpressionNode(kernel,fv,ExpressionNode.SIN,null),
					ExpressionNode.MULTIPLY,
					new MyDouble(kernel,halfAxes[1]));
			ExpressionNode rwX = new ExpressionNode(kernel, evX,
					ExpressionNode.PLUS,
					new MyDouble(kernel,b.x));
			ExpressionNode rwY = new ExpressionNode(kernel,evY,
					ExpressionNode.PLUS,
					new MyDouble(kernel,b.y));
			curve.setFunctionX(new Function(rwX,fv));
			curve.setFunctionY(new Function(rwY,fv));
			curve.setInterval(0, 2*Math.PI);	
			return;
			
		}
		if(type == CONIC_ELLIPSE){
			evX = new ExpressionNode(kernel, 
					new ExpressionNode(kernel,fv,ExpressionNode.COS,null),
					ExpressionNode.MULTIPLY,
					new MyDouble(kernel,halfAxes[0]));
			evY = new ExpressionNode(kernel, 
					new ExpressionNode(kernel,fv,ExpressionNode.SIN,null),
					ExpressionNode.MULTIPLY,
					new MyDouble(kernel,halfAxes[1]));
			min = 0;
			max = 2*Math.PI;
			
		}
		else if(type == CONIC_HYPERBOLA){
			evX = new ExpressionNode(kernel, 
					new ExpressionNode(kernel,fv,ExpressionNode.COSH,null),
					ExpressionNode.MULTIPLY,
					new MyDouble(kernel,halfAxes[0]));
			evY = new ExpressionNode(kernel, 
					new ExpressionNode(kernel,fv,ExpressionNode.SINH,null),
					ExpressionNode.MULTIPLY,
					new MyDouble(kernel,halfAxes[1]));
			min = -2*Math.PI;
			max = 2*Math.PI;
		}
		else if(type == CONIC_PARABOLA){
			evY = new ExpressionNode(kernel,new ExpressionNode(kernel, fv),ExpressionNode.MULTIPLY,new MyDouble(kernel,Math.sqrt(2*p)));
			evX = new ExpressionNode(kernel, 
					fv,
					ExpressionNode.MULTIPLY,
					fv);
			min = app.getEuclidianView().getXminForFunctions();
			max = app.getEuclidianView().getXmaxForFunctions();
			}
		else return;
		ExpressionNode rwX = new ExpressionNode(kernel,new ExpressionNode(kernel, 
				new ExpressionNode(kernel,evX,ExpressionNode.MULTIPLY,new MyDouble(kernel,eigenvec[0].x)),
				ExpressionNode.PLUS,
				new ExpressionNode(kernel,evY,ExpressionNode.MULTIPLY,new MyDouble(kernel,eigenvec[0].y))),
				ExpressionNode.PLUS,
				new MyDouble(kernel,b.x));
		ExpressionNode rwY = new ExpressionNode(kernel,new ExpressionNode(kernel, 
				new ExpressionNode(kernel,evX,ExpressionNode.MULTIPLY,new MyDouble(kernel,eigenvec[0].y)),
				ExpressionNode.PLUS,
				new ExpressionNode(kernel,evY,ExpressionNode.MULTIPLY,new MyDouble(kernel,-eigenvec[0].x))),
				ExpressionNode.PLUS,
				new MyDouble(kernel,b.y));
		curve.setFunctionX(new Function(rwX,fv));
		curve.setFunctionY(new Function(rwY,fv));
		curve.setInterval(min, max);			
		
	}



	/**
	 * Some ellipses might be circles by accident.
	 * This method tells us whether we can rely on this conic being circle after some points are moved.
	 * @return true iff the conic will remain circle after changing parent inputs
	 */
	public boolean keepsType() {
		if(getParentAlgorithm()==null)
			return true;
		if(getParentAlgorithm() instanceof AlgoConicFivePoints)
			return false;
		if(getParentAlgorithm() instanceof AlgoEllipseFociPoint)
			return false;
		if(getParentAlgorithm() instanceof AlgoEllipseFociLength)
			return false;
		return true;
	}

}
