package org.geogebra.ggjsviewer.client.euclidian;



import java.util.HashMap;
import java.util.LinkedHashMap;

import org.geogebra.ggjsviewer.client.euclidian.DrawableList.DrawableIterator;
import org.geogebra.ggjsviewer.client.kernel.AlgoElement;
import org.geogebra.ggjsviewer.client.kernel.BaseApplication;
import org.geogebra.ggjsviewer.client.kernel.ConstructionDefaults;
import org.geogebra.ggjsviewer.client.kernel.GeoConic;
import org.geogebra.ggjsviewer.client.kernel.GeoElement;
import org.geogebra.ggjsviewer.client.kernel.GeoLine;
import org.geogebra.ggjsviewer.client.kernel.GeoNumeric;
import org.geogebra.ggjsviewer.client.kernel.GeoPoint;
import org.geogebra.ggjsviewer.client.kernel.GeoPolygon;
import org.geogebra.ggjsviewer.client.kernel.GeoRay;
import org.geogebra.ggjsviewer.client.kernel.GeoSegment;
import org.geogebra.ggjsviewer.client.kernel.Kernel;
import org.geogebra.ggjsviewer.client.kernel.View;
import org.geogebra.ggjsviewer.client.kernel.gawt.AffineTransform;
import org.geogebra.ggjsviewer.client.kernel.gawt.Arc2D;
import org.geogebra.ggjsviewer.client.kernel.gawt.BasicStroke;
import org.geogebra.ggjsviewer.client.kernel.gawt.Ellipse2D;
import org.geogebra.ggjsviewer.client.kernel.gawt.Path2D;
import org.geogebra.ggjsviewer.client.kernel.gawt.PathIterator;
import org.geogebra.ggjsviewer.client.kernel.gawt.Point;
import org.geogebra.ggjsviewer.client.kernel.gawt.QuadCurve2D;
import org.geogebra.ggjsviewer.client.kernel.gawt.Rectangle;
import org.geogebra.ggjsviewer.client.kernel.gawt.Path2D;
//import org.geogebra.ggjsviewer.client.kernel.gawt.Arc2D.Double;
import org.geogebra.ggjsviewer.client.main.Application;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.HasMouseMoveHandlers;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.HasMouseUpHandlers;
import com.google.gwt.event.dom.client.HasMouseWheelHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.widgetideas.graphics.client.Color;
import com.google.gwt.widgetideas.graphics.client.GWTCanvas;


public class EuclidianView extends GWTCanvas implements EuclidianConstants, HasMouseDownHandlers, HasMouseOverHandlers, HasMouseOutHandlers, HasMouseWheelHandlers,HasMouseUpHandlers, HasMouseMoveHandlers, View {
	
	protected static final long serialVersionUID = 1L;
	protected Application app;

	protected Kernel kernel;

	protected EuclidianController euclidianController = new EuclidianController();
	private Hits hits;
	protected static final int MIN_WIDTH = 50;
	protected static final int MIN_HEIGHT = 50;
	
	public static final int DEFAULT_HEIGHT = 350;
	public static final int DEFAULT_WIDTH = 700;
	
	protected static final String PI_STRING = "\u03c0";
	
	private static final String EXPORT1 = "Export_1"; // Points used to define corners for export (if they exist)
	private static final String EXPORT2 = "Export_2";
	// pixel per centimeter (at 72dpi)
	protected static final double PRINTER_PIXEL_PER_CM = 72.0 / 2.54;

	public static final double MODE_ZOOM_FACTOR = 1.5;

	public static final double MOUSE_WHEEL_ZOOM_FACTOR = 1.1;

	public static final double SCALE_STANDARD = 50;

	// public static final double SCALE_MAX = 10000;
	// public static final double SCALE_MIN = 0.1;
	public static final double XZERO_STANDARD = 215;

	public static final double YZERO_STANDARD = 315;

	public static final int LINE_TYPE_FULL = 0;

	public static final int LINE_TYPE_DASHED_SHORT = 10;

	public static final int LINE_TYPE_DASHED_LONG = 15;

	public static final int LINE_TYPE_DOTTED = 20;

	public static final int LINE_TYPE_DASHED_DOTTED = 30;

	public static final Integer[] getLineTypes() {
		Integer[] ret = { new Integer(LINE_TYPE_FULL),
				new Integer(LINE_TYPE_DASHED_LONG),
				new Integer(LINE_TYPE_DASHED_SHORT),
				new Integer(LINE_TYPE_DOTTED),
				new Integer(LINE_TYPE_DASHED_DOTTED) };
		return ret;
	}
	
	// need to clip just outside the viewing area when drawing eg vectors
	// as a near-horizontal thick vector isn't drawn correctly otherwise
	public static final int CLIP_DISTANCE = 5;
	
	public static final int AXES_LINE_TYPE_FULL = 0;

	public static final int AXES_LINE_TYPE_ARROW = 1;

	public static final int AXES_LINE_TYPE_FULL_BOLD = 2;

	public static final int AXES_LINE_TYPE_ARROW_BOLD = 3;

	public static final int AXES_TICK_STYLE_MAJOR_MINOR = 0;

	public static final int AXES_TICK_STYLE_MAJOR = 1;

	public static final int AXES_TICK_STYLE_NONE = 2;

	public static final int POINT_STYLE_DOT = 0;
	public static final int POINT_STYLE_CROSS = 1;
	public static final int POINT_STYLE_CIRCLE = 2;
	public static final int POINT_STYLE_PLUS = 3;
	public static final int POINT_STYLE_FILLED_DIAMOND = 4;
	public static final int POINT_STYLE_EMPTY_DIAMOND = 5;
	public static final int POINT_STYLE_TRIANGLE_NORTH = 6;
	public static final int POINT_STYLE_TRIANGLE_SOUTH = 7;
	public static final int POINT_STYLE_TRIANGLE_EAST = 8;
	public static final int POINT_STYLE_TRIANGLE_WEST = 9;
	public static final int MAX_POINT_STYLE = 9;

	// G.Sturr added 2009-9-21 
	public static final Integer[] getPointStyles() {
		Integer[] ret = { new Integer(POINT_STYLE_DOT),
				new Integer(POINT_STYLE_CROSS),
				new Integer(POINT_STYLE_CIRCLE),
				new Integer(POINT_STYLE_PLUS),
				new Integer(POINT_STYLE_FILLED_DIAMOND),
				new Integer(POINT_STYLE_EMPTY_DIAMOND),
				new Integer(POINT_STYLE_TRIANGLE_NORTH),
				new Integer(POINT_STYLE_TRIANGLE_SOUTH),
				new Integer(POINT_STYLE_TRIANGLE_EAST),
				new Integer(POINT_STYLE_TRIANGLE_WEST)	
				};
		return ret;
	}
	//end		
	
	public static final int RIGHT_ANGLE_STYLE_NONE = 0;

	public static final int RIGHT_ANGLE_STYLE_SQUARE = 1;

	public static final int RIGHT_ANGLE_STYLE_DOT = 2;

	public static final int RIGHT_ANGLE_STYLE_L = 3; // Belgian style

	public static final int DEFAULT_POINT_SIZE = 3;

	public static final int DEFAULT_LINE_THICKNESS = 2;

	public static final int DEFAULT_ANGLE_SIZE = 30;

	public static final int DEFAULT_LINE_TYPE = LINE_TYPE_FULL;

	public static final float SELECTION_ADD = .5f; //AG2.0f is too thick;

	// ggb3D 2008-10-27 : mode constants moved to EuclidianConstants.java
	
	public static final int POINT_CAPTURING_OFF = 0;
	public static final int POINT_CAPTURING_ON = 1;
	public static final int POINT_CAPTURING_ON_GRID = 2;
	public static final int POINT_CAPTURING_AUTOMATIC = 3;
	
//	 Michael Borcherds 2008-04-28 
	public static final int GRID_CARTESIAN = 0;
	public static final int GRID_ISOMETRIC = 1;
	private int gridType = GRID_CARTESIAN;
	

	// zoom rectangle colors
	protected static final Color colZoomRectangle = new Color(200, 200, 230);
	protected static final Color colZoomRectangleFill = new Color(200, 200, 230, 50);

	// STROKES
	protected static BasicStroke standardStroke = new BasicStroke(1.0f);

	protected static BasicStroke selStroke = new BasicStroke(
			1.0f + SELECTION_ADD);

	protected static BasicStroke thinStroke = new BasicStroke(1.0f);

	// axes strokes
	protected static BasicStroke defAxesStroke = new BasicStroke(1.0f,
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);

	protected static BasicStroke boldAxesStroke = new BasicStroke(2.0f, // changed from 1.8f (same as bold grid) Michael Borcherds 2008-04-12
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);

	// axes and grid stroke
	protected BasicStroke axesStroke, tickStroke, gridStroke;
	/*AG
	protected Line2D.Double tempLine = new Line2D.Double();

	protected static RenderingHints defRenderingHints = new RenderingHints(null);
	{
		defRenderingHints.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_SPEED);
		defRenderingHints.put(RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		defRenderingHints.put(RenderingHints.KEY_COLOR_RENDERING,
				RenderingHints.VALUE_COLOR_RENDER_SPEED);
		
		// This ensures fast image drawing. Note that DrawImage changes
		// this hint for scaled and sheared images to improve their quality 
		defRenderingHints.put(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);				
	}

	// FONTS
	public Font fontPoint, fontLine, fontVector, fontConic, fontCoords,
			fontAxes, fontAngle;

	int fontSize;

	// member variables
	protected Application app;
	
	protected Kernel kernel;

	protected EuclidianController euclidianController;
	*/
	AffineTransform coordTransform = new AffineTransform();

	int width, height;
	/*
	protected NumberFormat[] axesNumberFormat;

	protected NumberFormat printScaleNF;
	 */
	double xmin, xmax, ymin, ymax, invXscale, invYscale; // ratio yscale / xscale

	public double xZero;

	public double yZero;

	public double xscale;

	public double yscale;

	public double scaleRatio = 1.0;
	double xZeroOld, yZeroOld;

	protected double[] AxesTickInterval = { 1, 1 }; // for axes =

	// axesNumberingDistances /
	// 2

	boolean showGrid = false;

	protected boolean antiAliasing = true;

	boolean showMouseCoords = false;
	boolean showAxesRatio = false;
	private boolean highlightAnimationButtons = false;

	protected int pointCapturingMode; // snap to grid points

	// added by Lo�c BEGIN
	// right angle
	int rightAngleStyle = EuclidianView.RIGHT_ANGLE_STYLE_SQUARE;

	// END
	
	public int pointStyle = POINT_STYLE_DOT;
	
	int booleanSize=13;

	int mode = MODE_MOVE;
	//AG IT MUST BE SET DINAMICALLY
	protected boolean[] showAxes = { false, false };
	private boolean showAxesCornerCoords = true;
	
	protected boolean[] showAxesNumbers = { true, true };

	protected String[] axesLabels = { null, null };

	protected String[] axesUnitLabels = { null, null };

	protected boolean[] piAxisUnit = { false, false };

	protected int[] axesTickStyles = { AXES_TICK_STYLE_MAJOR,
			AXES_TICK_STYLE_MAJOR };

	// for axes labeling with numbers
	protected boolean[] automaticAxesNumberingDistances = { true, true };

	protected double[] axesNumberingDistances = { 2, 2 };

	// distances between grid lines
	protected boolean automaticGridDistance = true;
	// since V3.0 this factor is 1, before it was 0.5
	final public static double DEFAULT_GRID_DIST_FACTOR = 1;
	public static double automaticGridDistanceFactor = DEFAULT_GRID_DIST_FACTOR;

	double[] gridDistances = { 2, 2 };

	protected int gridLineStyle, axesLineType;
	
	protected boolean gridIsBold=false; // Michael Borcherds 2008-04-11

	// colors: axes, grid, background
	protected Color axesColor, gridColor, bgColor;

	protected double printingScale;

	// Map (geo, drawable) for GeoElements and Drawables
	protected HashMap DrawableMap = new HashMap(500);

	protected DrawableList allDrawableList = new DrawableList();
	// Michael Borcherds 2008-03-01
	public static final int MAX_LAYERS = 9;
	private int MAX_LAYER_USED = 0;
	public DrawableList drawLayers[]; 

	// on add: change resetLists()
	
	protected DrawableList bgImageList = new DrawableList();

	Previewable previewDrawable;
	protected Rectangle selectionRectangle;
	/*
	// temp
	// public static final int DRAW_MODE_DIRECT_DRAW = 0;
	// public static final int DRAW_MODE_BACKGROUND_IMAGE = 1;

	// or use volatile image
	// protected int drawMode = DRAW_MODE_BACKGROUND_IMAGE;
	protected BufferedImage bgImage;
	protected Graphics2D bgGraphics; // g2d of bgImage
	protected Image resetImage, playImage, pauseImage, upArrowImage, downArrowImage;
	private boolean firstPaint = true;
	
	// temp image
	protected Graphics2D g2Dtemp = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB).createGraphics();
	//public Graphics2D lastGraphics2D;
	*/
	protected StringBuilder sb = new StringBuilder();
	/*
	protected Cursor defaultCursor;*/

	public int fontSize = 12; //px
	private String canvasFont = "normal";
	public String fontPoint = "normal";
	public String fontLine = "normal";
	public String fontConic = "normal";
	
	/*Handling the text support with native canvas functions
	*/
	public native void strokeText(String text, int x, int y, String font, int fontSize) /*-{
		//if (!$wnd.eview) {
			//$wnd.alert(document.getElementById("eview"));
			//$wnd.eview = $doc.getElementById("eview");
			//$wnd.alert($wnd.eview);
			//$wnd.econt = $wnd.eview.getContext("2d");
		//}
		///if ($wnd.econt.strokeText) {
			//$wnd.econt.font = 'italic 400 12px/2 sans-serif';
			//$wnd.econt.clearRect(x-5,y-20,500,30);
			//$wnd.econt.strokeText(text,x,y);
		//}
		var eview = $doc.getElementById('eview');
		var ctx = eview.getContext('2d');
		ctx.font = font+' '+String.valueOf(fontSize)+'px/'+String.valueOf(fontSize)+'px sans-serif';
		//ctx.clearRect(x-5,y-20,500,30);
		ctx.strokeText(text,x,y);
		
	}-*/;
	
	/*end text support*/
	public EuclidianView() {
		super(DEFAULT_WIDTH,DEFAULT_HEIGHT);
		getElement().setAttribute("id", "eview");
		width = getCoordWidth();
		height = getCoordHeight();
		  drawLayers = new DrawableList[MAX_LAYERS+1];
		  for (int k=0; k <= MAX_LAYERS ; k++) {
		     drawLayers[k] = new DrawableList();
		  }
	
		initView(false);
		
		//addMouseDownHandler(euclidianController);
		
		/*euclidianController.setEuclidianView(this);
		kernel = new Kernel(new BaseApplication());
		///kernel.attach(this);
		attachView();
		euclidianController.setKernel(kernel);*/
		
		
		setLineWidth(1);
		//drawAxes();
		hits = new Hits();
		
		
		
		
	}
	
	protected void initView(boolean repaint) {
		// preferred size
		//AGsetPreferredSize(null);
		
		// init grid's line type
		//AGsetGridLineStyle(LINE_TYPE_DASHED_SHORT);
		//AGsetAxesLineStyle(AXES_LINE_TYPE_ARROW);
		//AGsetAxesColor(Color.BLACK); // Michael Borcherds 2008-01-26 was darkgray
		//AGsetGridColor(Color.LIGHTGREY);
		//AGsetBackground(Color.WHITE);

		// showAxes = true;
		// showGrid = false;
		showMouseCoords = true;
		pointCapturingMode = POINT_CAPTURING_AUTOMATIC;
		pointStyle = POINT_STYLE_DOT;
		
		booleanSize=13; // Michael Borcherds 2008-05-12

		// added by Lo�c BEGIN
		rightAngleStyle = EuclidianView.RIGHT_ANGLE_STYLE_SQUARE;
		// END
			
		showAxesNumbers[0] = true;
		showAxesNumbers[1] = true;
		axesLabels[0] = null;
		axesLabels[1] = null;
		axesUnitLabels[0] = null;
		axesUnitLabels[1] = null;
		piAxisUnit[0] = false;
		piAxisUnit[1] = false;
		axesTickStyles[0] = AXES_TICK_STYLE_MAJOR;
		axesTickStyles[1] = AXES_TICK_STYLE_MAJOR;

		// for axes labeling with numbers
		automaticAxesNumberingDistances[0] = true;
		automaticAxesNumberingDistances[1] = true;
		//register listneres
		
		// distances between grid lines
		automaticGridDistance = true;
		
		setStandardCoordSystem(repaint);
	}
	
	public double getXZero() {
		return xZero;
	}

	/**
	 * Returns y coordinate of axes origin.
	 */
	public double getYZero() {
		return yZero;
	}
	
	public double getInvXscale(){
		return invXscale;
	}
	
	public double getInvYscale(){
		return invYscale;
	}
	
	final public int getPointCapturingMode() {
		return pointCapturingMode;
	}
	
	public Previewable getPreviewDrawable(){
		return previewDrawable;
	}
	
	public void updatePreviewable(){
		Point mouseLoc = getEuclidianController().mouseLoc;
		getPreviewDrawable().updateMousePos(mouseLoc.x, mouseLoc.y);
	}
	public boolean getShowMouseCoords(){
		return showMouseCoords;
	}

	public final boolean isGridOrAxesShown() {
		return showAxes[0] || showAxes[1] || showGrid;
	}	
	
	/**
	 * Set capturing of points to the grid.
	 */
	public void setPointCapturing(int mode) {
		pointCapturingMode = mode;
	}

	/**
	 * Returns grid type.
	 */
	final public int getGridType() {
		return gridType;
	}

	/**
	 * Set grid type.
	 */
	public void setGridType(int type) {
		gridType = type;
	}
	
	public double getGridDistances(int i){
		return gridDistances[i];
	}
	
	public void setSelectionRectangle(Rectangle selectionRectangle) {
		this.selectionRectangle = selectionRectangle;		
	}
	
	private void setStandardCoordSystem(boolean repaint) {
		setCoordSystem(XZERO_STANDARD, YZERO_STANDARD, SCALE_STANDARD,
				SCALE_STANDARD, repaint);
	}
	
	public void setStandardCoordSystem() {
		setStandardCoordSystem(true);
	}
	
	final public void setCoordSystem(double xZero, double yZero, double xscale,
			double yscale) {
		setCoordSystem(xZero, yZero, xscale, yscale, true);
	}
	
	public void setCoordSystem(double xZero, double yZero, double xscale,
			double yscale, boolean repaint) {
		if (Double.isNaN(xscale) || xscale < Kernel.MAX_DOUBLE_PRECISION || xscale > Kernel.INV_MAX_DOUBLE_PRECISION)
			return;
		if (Double.isNaN(yscale) || yscale < Kernel.MAX_DOUBLE_PRECISION || yscale > Kernel.INV_MAX_DOUBLE_PRECISION)
			return;

		this.xZero = xZero;
		this.yZero = yZero;
		this.xscale = xscale;
		this.yscale = yscale;
		scaleRatio = yscale / xscale;
		invXscale = 1.0d / xscale;
		invYscale = 1.0d / yscale;
		
		// set transform for my coord system:
		// ( xscale 0 xZero )
		// ( 0 -yscale yZero )
		// ( 0 0 1 )
		coordTransform.setTransform(xscale, 0.0d, 0.0d, -yscale, xZero, yZero);

		// real world values
		setRealWorldBounds();
		
		// if (drawMode == DRAW_MODE_BACKGROUND_IMAGE)
		if (repaint) {
			//AGupdateBackgroundImage();
			//AGupdateAllDrawables(repaint);
			//app.updateStatusLabelAxesRatio();
		}
	}
	
	/**
	 * convert real world coordinate x to screen coordinate x
	 * 
	 * @param xRW
	 * @return
	 */
	final public int toScreenCoordX(double xRW) {
		return (int) Math.round(xZero + xRW * xscale);
	}

	/**
	 * convert real world coordinate y to screen coordinate y
	 * 
	 * @param yRW
	 * @return
	 */
	final public int toScreenCoordY(double yRW) {
		return (int) Math.round(yZero - yRW * yscale);
	}

	/**
	 * convert real world coordinate x to screen coordinate x
	 * 
	 * @param xRW
	 * @return
	 */
	final public double toScreenCoordXd(double xRW) {
		return xZero + xRW * xscale;
	}

	/**
	 * convert real world coordinate y to screen coordinate y
	 * 
	 * @param yRW
	 * @return
	 */
	final public double toScreenCoordYd(double yRW) {
		return yZero - yRW * yscale;
	}
	
	final public boolean toScreenCoords(double[] inOut) {
		// convert to screen coords
		inOut[0] = xZero + inOut[0] * xscale;
		inOut[1] = yZero - inOut[1] * yscale;
		
		// check if (x, y) is on screen
		boolean onScreen = true;
		
		// note that java drawing has problems for huge coord values
		// so we use FAR_OFF_SCREEN for clipping
		if (Double.isNaN(inOut[0]) || Double.isInfinite(inOut[0])) {
			inOut[0] = Double.NaN;
			onScreen = false;
		}
		else if (inOut[0] < 0 ) { // x left of screen
			//inOut[0] = Math.max(inOut[0], -MAX_SCREEN_COORD);
			onScreen = false;
		}
		else if (inOut[0] > width) { // x right of screen
			//inOut[0] = Math.min(inOut[0], width + MAX_SCREEN_COORD);
			onScreen = false;
		}
		
		// y undefined
		if (Double.isNaN(inOut[1]) || Double.isInfinite(inOut[1])) {
			inOut[1] = Double.NaN;
			onScreen = false;
		}
		else if (inOut[1] < 0) { // y above screen
			//inOut[1] = Math.max(inOut[1], -MAX_SCREEN_COORD);
			onScreen = false;
		}
		else if (inOut[1] > height) { // y below screen
			//inOut[1] = Math.min(inOut[1], height + MAX_SCREEN_COORD);
			onScreen = false;
		}
			
		return onScreen;
	}
	
	protected void calcPrintingScale() {
		double unitPerCM = PRINTER_PIXEL_PER_CM / xscale;
		int exp = (int) Math.round(Math.log(unitPerCM) / Math.log(10));
		printingScale = Math.pow(10, -exp);
	}

	
	final protected void setRealWorldBounds() {
		xmin = -xZero * invXscale;
		xmax = (width - xZero) * invXscale;
		ymax = yZero * invYscale;
		ymin = (yZero - height) * invYscale;
		
		setAxesIntervals(xscale, 0);
		setAxesIntervals(yscale, 1);
		calcPrintingScale();
		
		// tell kernel
		//kernel.setEuclidianViewBounds(xmin, xmax, ymin, ymax, xscale, yscale);
	}
	
	protected void setAxesIntervals(double scale, int axis) {
		double maxPix = 100; // only one tick is allowed per maxPix pixels
		double units = maxPix / scale;
		int exp = (int) Math.floor(Math.log(units) / Math.log(10));
		int maxFractionDigtis = Math.max(-exp,0 /*kernel.getPrintDecimals()*/);
		
		if (automaticAxesNumberingDistances[axis]) {
			if (piAxisUnit[axis]) {
				axesNumberingDistances[axis] = Math.PI;
			} else {
				double pot = Math.pow(10, exp);
				double n = units / pot;

				if (n > 5) {
					axesNumberingDistances[axis] = 5 * pot;
				} else if (n > 2) {
					axesNumberingDistances[axis] = 2 * pot;
				} else {
					axesNumberingDistances[axis] = pot;
				}
			}
		}
		AxesTickInterval[axis] = axesNumberingDistances[axis] / 2.0;		

		// set axes number format
		/*AGif (axesNumberFormat[axis] instanceof DecimalFormat) {
			//DecimalFormat df = (DecimalFormat) axesNumberFormat[axis];

			// display large and small numbers in scienctific notation
			if (axesNumberingDistances[axis] < 10E-6 || axesNumberingDistances[axis] > 10E6) {
				df.applyPattern("0.##E0");	
				// avoid  4.00000000000004E-11 due to rounding error when computing
				// tick mark numbers
				maxFractionDigtis = Math.min(14, maxFractionDigtis);
			} else {
				df.applyPattern("###0.##");					
			}
		}		
		//AGaxesNumberFormat[axis].setMaximumFractionDigits(maxFractionDigtis);
		*/

		if (automaticGridDistance) {			
			gridDistances[axis] = axesNumberingDistances[axis] * automaticGridDistanceFactor;
		}
	}
	
	
	public EuclidianView(EuclidianController ec,boolean[] showAxis, boolean showGrid) {
		
	}
	
	public Kernel getKernel() {
		return kernel;
	}
	
	/**
	 * convert screen coordinate x to real world coordinate x
	 * 
	 * @param x
	 * @return
	 */
	final public double toRealWorldCoordX(double x) {
		return (x - xZero) * invXscale;
	}

	/**
	 * convert screen coordinate y to real world coordinate y
	 * 
	 * @param y
	 * @return
	 */
	final public double toRealWorldCoordY(double y) {
		return (yZero - y) * invYscale;
	}
	
final public void setHits(Point p){
		
		hits.init();
				
		DrawableIterator it = allDrawableList.getIterator();
		while (it.hasNext()) {
			Drawable d = it.next();
			if (d.hit(p.x, p.y) || d.hitLabel(p.x, p.y)) {
				GeoElement geo = d.getGeoElement();
				if (geo.isEuclidianVisible()) {
					//strokeText(geo.toString(),30,30);
					hits.add(geo);
					//GWT.log(geo.toString());
				}
			}
			
		}
		
		// look for axis
		if (hits.getImageCount() == 0) {
			if (showAxes[0] && Math.abs(yZero - p.y) < 3) {
				hits.add(kernel.getXAxis());
			}
			if (showAxes[1] && Math.abs(xZero - p.x) < 3) {
				hits.add(kernel.getYAxis());
			}
		}
		
		// remove all lists and  images if there are other objects too
		if (hits.size() - (hits.getListCount() + hits.getImageCount()) > 0) {
			for (int i = 0; i < hits.size(); ++i) {
				GeoElement geo = (GeoElement) hits.get(i);
				if (geo.isGeoList() || geo.isGeoImage())
					hits.remove(i);
			}
		}
		
		
	}
	
	
	protected static int SCREEN_BORDER = 10;
	
	
	final void drawAxes() {
		//temp!
		
		// for axes ticks
		double yZeroTick = yZero;
		double xZeroTick = xZero;
		double yBig = yZero + 4;
		double xBig = xZero - 4;
		double ySmall1 = yZero + 0;
		double ySmall2 = yZero + 2;
		double xSmall1 = xZero - 0;
		double xSmall2 = xZero - 2;
		int xoffset, yoffset;
		boolean bold = axesLineType == AXES_LINE_TYPE_FULL_BOLD
						|| axesLineType == AXES_LINE_TYPE_ARROW_BOLD;
		boolean drawArrows = axesLineType == AXES_LINE_TYPE_ARROW
								|| axesLineType == AXES_LINE_TYPE_ARROW_BOLD;

		// AXES_TICK_STYLE_MAJOR_MINOR = 0;
		// AXES_TICK_STYLE_MAJOR = 1;
		// AXES_TICK_STYLE_NONE = 2;
		boolean[] drawMajorTicks = { axesTickStyles[0] <= 1,
				axesTickStyles[1] <= 1 };
		boolean[] drawMinorTicks = { axesTickStyles[0] == 0,
				axesTickStyles[1] == 0 };

		//FontRenderContext frc = g2.getFontRenderContext();
		//g2.setFont(fontAxes);
		int fontsize = 10;//fontAxes.getSize();
		int arrowSize = fontsize / 3;
		//TEMP!!!!AG
		axesColor = Color.BLACK;
		setStrokeStyle(axesColor);
		

		if (bold) {
			/*axesStroke = boldAxesStroke;
			tickStroke = boldAxesStroke;*/
			
			ySmall2++;
			xSmall2--;
			arrowSize += 1;
		} else {
		   /*	axesStroke = defAxesStroke;
			tickStroke = defAxesStroke;*/
		}

		// turn antialiasing off
//		Object antiAliasValue = g2
//				.getRenderingHint(RenderingHints.KEY_ANTIALIASING);	
//		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//				RenderingHints.VALUE_ANTIALIAS_OFF);

		// X - AXIS
		if (showAxes[0] && ymin < 0 && ymax > 0) {
			if (showGrid) {
				yoffset = fontsize + 4;
				xoffset = 10;
			} else {
				yoffset = fontsize + 4;
				xoffset = 1;
			}

			// label of x axis
			if (axesLabels[0] != null) {
				/*TextLayout layout = new TextLayout(axesLabels[0], fontLine, frc);
				g2.drawString(axesLabels[0], (int) (width - 10 - layout
						.getAdvance()), (int) (yZero - 4));*/
			}

			// numbers
			double rw = xmin - (xmin % axesNumberingDistances[0]);
			double pix = xZero + rw * xscale;
			double axesStep = xscale * axesNumberingDistances[0]; // pixelstep
			double smallTickPix;
			double tickStep = axesStep / 2;
			if (pix < SCREEN_BORDER) {
				// big tick
				if (drawMajorTicks[0]) {
					setLineCap(GWTCanvas.BUTT);
					setLineJoin(GWTCanvas.MITER);
					beginPath();
					/*tempLine.setLine(pix, yZeroTick, pix, yBig);
					g2.draw(tempLine);*/
					moveTo(pix, yZeroTick);
					lineTo(pix, yBig);
					stroke();
				}
				pix += axesStep;
				rw += axesNumberingDistances[0];
			}
			int maxX = width - SCREEN_BORDER;
			int prevTextEnd = -3;
			for (; pix < width; rw += axesNumberingDistances[0], pix += axesStep) {
				if (pix <= maxX) {
					if (showAxesNumbers[0]) {
						/*String strNum = kernel.formatPiE(rw,
								axesNumberFormat[0]);
						boolean zero = strNum.equals("0");

						sb.setLength(0);
						sb.append(strNum);
						if (axesUnitLabels[0] != null && !piAxisUnit[0])
							sb.append(axesUnitLabels[0]);

						TextLayout layout = new TextLayout(sb.toString(),
								fontAxes, frc);*/
						/*int x, y = (int) (yZero + yoffset);
						if (zero && showAxes[1]) {
							x = (int) (pix + 6);
						} else {
							x = (int) (pix + xoffset - layout.getAdvance() / 2);
						}
												
						// make sure we don't print one string on top of the other
						if (x > prevTextEnd + 5) {
							prevTextEnd = (int) (x + layout.getAdvance()); 
							g2.drawString(sb.toString(), x, y);
						}*/
					}

					// big tick
					if (drawMajorTicks[0]) {
						setLineCap(GWTCanvas.BUTT);
						setLineJoin(GWTCanvas.MITER);
						//g2.setStroke(tickStroke);
						//tempLine.setLine(pix, yZeroTick, pix, yBig);
						//g2.draw(tempLine);
						beginPath();
						moveTo(pix, yZeroTick);
						lineTo(pix, yBig);
						stroke();
					}
				} else if (drawMajorTicks[0] && !drawArrows) {
					// draw last tick if there is no arrow
					//tempLine.setLine(pix, yZeroTick, pix, yBig);
					//g2.draw(tempLine);
					beginPath();
					moveTo(pix, yZeroTick);
					lineTo(pix, yBig);
					stroke();
				}

				// small tick
				smallTickPix = pix - tickStep;
				if (drawMinorTicks[0]) {
					//g2.setStroke(tickStroke);
					//tempLine.setLine(smallTickPix, ySmall1, smallTickPix,
					//		ySmall2);
					//g2.draw(tempLine);
					setLineCap(GWTCanvas.BUTT);
					setLineJoin(GWTCanvas.MITER);
					beginPath();
					moveTo(smallTickPix, ySmall1);
					lineTo(smallTickPix, ySmall2);
					stroke();
				}
			}
			// last small tick
			smallTickPix = pix - tickStep;
			if (drawMinorTicks[0] && (!drawArrows || smallTickPix <= maxX)) {
				setLineCap(GWTCanvas.BUTT);
				setLineJoin(GWTCanvas.MITER);
				beginPath();
				moveTo(smallTickPix,ySmall1);
				lineTo(smallTickPix,ySmall2);
				stroke();
				//g2.setStroke(tickStroke);
				//tempLine.setLine(smallTickPix, ySmall1, smallTickPix, ySmall2);
				//g2.draw(tempLine);
			}

			// x-Axis
			beginPath();
			moveTo(0, yZero);
			lineTo(width,yZero);
			stroke();
			//tempLine.setLine(0, yZero, width, yZero);
			//g2.draw(tempLine);

			if (drawArrows) {
				// tur antialiasing on
//				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//						antiAliasValue);

				// draw arrow for x-axis
				beginPath();
				moveTo(width-1,yZero);
				lineTo(width-1-arrowSize,yZero-arrowSize);
				//tempLine.setLine(width - 1, yZero, width - 1 - arrowSize, yZero
				//		- arrowSize);
				//g2.draw(tempLine);
				moveTo(width-1, yZero);
				lineTo(width-1-arrowSize,yZero+arrowSize);
				//tempLine.setLine(width - 1, yZero, width - 1 - arrowSize, yZero
				//		+ arrowSize);
				//g2.draw(tempLine);

				//g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				//		RenderingHints.VALUE_ANTIALIAS_OFF);
				stroke();
			}
		}

		// Y-AXIS
		if (showAxes[1] && xmin < 0 && xmax > 0) {
			if (showGrid) {
				xoffset = -2 - fontsize / 4;
				yoffset = -2;
			} else {
				xoffset = -4 - fontsize / 4;
				yoffset = fontsize / 2 - 1;
			}

			// label of y axis
			/*if (axesLabels[1] != null) {
				TextLayout layout = new TextLayout(axesLabels[1], fontLine, frc);
				g2.drawString(axesLabels[1], (int) (xZero + 5),
						(int) (5 + layout.getAscent()));
			}*/

			// numbers
			double rw = ymax - (ymax % axesNumberingDistances[1]);
			double pix = yZero - rw * yscale;
			double axesStep = yscale * axesNumberingDistances[1]; // pixelstep
			double tickStep = axesStep / 2;

			// first small tick
			double smallTickPix = pix - tickStep;
			if (drawMinorTicks[1]
					&& (!drawArrows || smallTickPix > SCREEN_BORDER)) {
				//g2.setStroke(tickStroke);
				setLineCap(GWTCanvas.BUTT);
				setLineJoin(GWTCanvas.MITER);
				beginPath();
				moveTo(xSmall1,smallTickPix);
				lineTo(xSmall2,smallTickPix);
				stroke();
				//tempLine.setLine(xSmall1, smallTickPix, xSmall2, smallTickPix);
				//g2.draw(tempLine);
			}

			// don't get too near to the top of the screen
			if (pix < SCREEN_BORDER) {
				if (drawMajorTicks[1] && !drawArrows) {
					// draw tick if there is no arrow
					setLineCap(GWTCanvas.BUTT);
					setLineJoin(GWTCanvas.MITER);
					beginPath();
					moveTo(xBig,pix);
					lineTo(xZeroTick,pix);
					stroke();
					//g2.setStroke(tickStroke);
					//tempLine.setLine(xBig, pix, xZeroTick, pix);
					//g2.draw(tempLine);
				}
				smallTickPix = pix + tickStep;
				if (drawMinorTicks[1] && smallTickPix > SCREEN_BORDER) {
					setLineCap(GWTCanvas.BUTT);
					setLineJoin(GWTCanvas.MITER);
					beginPath();
					moveTo(xSmall1,smallTickPix);
					lineTo(xSmall2, smallTickPix);
					stroke();
					//g2.setStroke(tickStroke);
					//tempLine.setLine(xSmall1, smallTickPix, xSmall2,
					//		smallTickPix);
					//g2.draw(tempLine);
				}
				pix += axesStep;
				rw -= axesNumberingDistances[1];
			}
			int maxY = height - SCREEN_BORDER;
			for (; pix <= height; rw -= axesNumberingDistances[1], pix += axesStep) {
				if (pix <= maxY) {
					if (showAxesNumbers[1]) {
					/*	String strNum = kernel.formatPiE(rw,
								axesNumberFormat[1]);
						boolean zero = strNum.equals("0");

						sb.setLength(0);
						sb.append(strNum);
						if (axesUnitLabels[1] != null && !piAxisUnit[1])
							sb.append(axesUnitLabels[1]);

						TextLayout layout = new TextLayout(sb.toString(),
								fontAxes, frc);
						int x = (int) (xZero + xoffset - layout.getAdvance());
						int y;
						if (zero && showAxes[0]) {
							y = (int) (yZero - 2);
						} else {
							y = (int) (pix + yoffset);
						}
						g2.drawString(sb.toString(), x, y);*/
					}
				}

				// big tick
				if (drawMajorTicks[1]) {
					setLineCap(GWTCanvas.BUTT);
					setLineJoin(GWTCanvas.MITER);
					beginPath();
					moveTo(xBig,pix);
					lineTo(xZeroTick,pix);
					stroke();
					//g2.setStroke(tickStroke);
					//tempLine.setLine(xBig, pix, xZeroTick, pix);
					//g2.draw(tempLine);
				}

				smallTickPix = pix + tickStep;
				if (drawMinorTicks[1]) {
					setLineCap(GWTCanvas.BUTT);
					setLineJoin(GWTCanvas.MITER);
					beginPath();
					moveTo(xSmall1,smallTickPix);
					lineTo(xSmall2,smallTickPix);
					stroke();
					//g2.setStroke(tickStroke);
					//tempLine.setLine(xSmall1, smallTickPix, xSmall2,
					//		smallTickPix);
					//g2.draw(tempLine);
				}
			}

			// y-Axis
			beginPath();
			moveTo(xZero,0);
			lineTo(xZero,height);
			stroke();
			//tempLine.setLine(xZero, 0, xZero, height);
			//g2.draw(tempLine);

			if (drawArrows && xmin < 0 && xmax > 0) {
				// draw arrow for y-axis
				beginPath();
				moveTo(xZero,0);
				lineTo(xZero - arrowSize,arrowSize);
				moveTo(xZero,0);
				lineTo(xZero+arrowSize,arrowSize);
				stroke();
				//tempLine.setLine(xZero, 0, xZero - arrowSize, arrowSize);
				//g2.draw(tempLine);
				//tempLine.setLine(xZero, 0, xZero + arrowSize, arrowSize);
				//g2.draw(tempLine);
			}								
		}
		
	
		// if one of the axes is not visible, show upper left and lower right corner coords
		if (showAxesCornerCoords) {
			/*if (xmin > 0 || xmax < 0 || ymin > 0 || ymax < 0) {
				// uper left corner								
				sb.setLength(0);
				sb.append('(');			
				sb.append(kernel.formatPiE(xmin, axesNumberFormat[0]));
				sb.append(", ");
				sb.append(kernel.formatPiE(ymax, axesNumberFormat[1]));
				sb.append(')');
				
				int textHeight = 2 + fontAxes.getSize();
				g2.setFont(fontAxes);			
				g2.drawString(sb.toString(), 5, textHeight);
				
				// lower right corner
				sb.setLength(0);
				sb.append('(');			
				sb.append(kernel.formatPiE(xmax, axesNumberFormat[0]));
				sb.append(", ");
				sb.append(kernel.formatPiE(ymin, axesNumberFormat[1]));
				sb.append(')');
				
				TextLayout layout = new TextLayout(sb.toString(), fontAxes, frc);	
				layout.draw(g2, (int) (width - 5 - layout.getAdvance()), 
										height - 5);					
			}	*/
		}
	}

	@Override
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		// TODO Auto-generated method stub
		return addDomHandler(handler, MouseDownEvent.getType());
	}

	public EuclidianController getEuclidianController() {
		return euclidianController;
	}
	
	/**get the hits recorded */
	public Hits getHits(){
		return hits;
	}

	public void setDragCursor() {
		// TODO Auto-generated method stub
		//POSSIBLE TO IMPLEMENT WITH CSS
		
	}
	
	public void setShowMouseCoords(boolean b){
		showMouseCoords=b;
	}
	
	final Drawable getDrawable(GeoElement geo) {
		return (Drawable) DrawableMap.get(geo);
	}
	
	final public Drawable getDrawableFor(GeoElement geo) {
		return (Drawable) DrawableMap.get(geo);
	}
	

	@Override
	public void add(GeoElement geo) {
		// check if there is already a drawable for geo
		Drawable d = getDrawable(geo);
		if (d != null)
			return;

		d = createDrawable(geo);
		if (d != null) {
			addToDrawableLists(d);
			repaintView();			
		}
	}
	
	public void attachView() {
		kernel.notifyAddAll(this);
		kernel.attach(this);
	}
	
	protected void addToDrawableLists(Drawable d) {
		if (d == null) return;
		
		GeoElement geo = d.getGeoElement();
		int layer = geo.getLayer();

		switch (geo.getGeoClassType()) {
		case GeoElement.GEO_CLASS_BOOLEAN:			
			drawLayers[layer].add(d);
			break;
		
		case GeoElement.GEO_CLASS_BUTTON:			
			drawLayers[layer].add(d);
			break;
		
		case GeoElement.GEO_CLASS_TEXTFIELD:			
			drawLayers[layer].add(d);
			break;
		
		case GeoElement.GEO_CLASS_POINT:
			drawLayers[layer].add(d);
			break;					

		case GeoElement.GEO_CLASS_SEGMENT:
			drawLayers[layer].add(d);
			break;

		case GeoElement.GEO_CLASS_RAY:
			drawLayers[layer].add(d);
			break;

		case GeoElement.GEO_CLASS_LINE:
			drawLayers[layer].add(d);
			break;

		case GeoElement.GEO_CLASS_POLYGON:
			drawLayers[layer].add(d);
			break;

		case GeoElement.GEO_CLASS_ANGLE:
			if (geo.isIndependent()) {				
				drawLayers[layer].add(d);
			} else {				
				if (geo.isDrawable()) {					
					drawLayers[layer].add(d);
				} 
				else 
					d = null;
			}
			break;

		case GeoElement.GEO_CLASS_NUMERIC:			
			drawLayers[layer].add(d);
			break;

		case GeoElement.GEO_CLASS_VECTOR:			
			drawLayers[layer].add(d);
			break;

		case GeoElement.GEO_CLASS_CONICPART:
			drawLayers[layer].add(d);
			break;

		case GeoElement.GEO_CLASS_CONIC:
			drawLayers[layer].add(d);
			break;

		case GeoElement.GEO_CLASS_FUNCTION:
		case GeoElement.GEO_CLASS_FUNCTIONCONDITIONAL:
			drawLayers[layer].add(d);
			break;

		case GeoElement.GEO_CLASS_TEXT:
			drawLayers[layer].add(d);
			break;

		case GeoElement.GEO_CLASS_IMAGE:
			if (!bgImageList.contains(d))
				drawLayers[layer].add(d);
			break;

		case GeoElement.GEO_CLASS_LOCUS:
			drawLayers[layer].add(d);
			break;

		case GeoElement.GEO_CLASS_CURVE_CARTESIAN:
			drawLayers[layer].add(d);
			break;

		case GeoElement.GEO_CLASS_LIST:
			drawLayers[layer].add(d);
			break;
		}

		if (d != null) {
			allDrawableList.add(d);			
		}
	}
	
	protected Drawable createDrawable(GeoElement geo) {
		Drawable d = null;

		switch (geo.getGeoClassType()) {
		case GeoElement.GEO_CLASS_BOOLEAN:
		//AG	d = new DrawBoolean(this, (GeoBoolean) geo);			
			break;
		
		case GeoElement.GEO_CLASS_BUTTON:

		//AG	d = new DrawButton(this, (GeoButton) geo);			
			break;
		
		case GeoElement.GEO_CLASS_TEXTFIELD:

		//AG	d = new DrawTextField(this, (GeoButton) geo);	
			break;
		
		case GeoElement.GEO_CLASS_POINT:
			d = new DrawPoint(this, (GeoPoint) geo);			
			break;					

		case GeoElement.GEO_CLASS_SEGMENT:
		d = new DrawSegment(this, (GeoSegment) geo);
			break;

		case GeoElement.GEO_CLASS_RAY:
			d = new DrawRay(this, (GeoRay) geo);
			break;

		case GeoElement.GEO_CLASS_LINE:
			d = new DrawLine(this, (GeoLine) geo);
			break;

		case GeoElement.GEO_CLASS_POLYGON:
			d = new DrawPolygon(this, (GeoPolygon) geo);
			break;

		/*case GeoElement.GEO_CLASS_ANGLE:
			if (geo.isIndependent()) {
				// independent number may be shown as slider
				d = new DrawSlider(this, (GeoNumeric) geo);
			} else {
				d = new DrawAngle(this, (GeoAngle) geo);
				if (geo.isDrawable()) {
					if (!geo.isColorSet()) {
						Color col = geo.getConstruction()
								.getConstructionDefaults().getDefaultGeo(
										ConstructionDefaults.DEFAULT_ANGLE)
								.getObjectColor();
						geo.setObjColor(col);
					}
				}
			}
			break;
		*/
		case GeoElement.GEO_CLASS_NUMERIC:
			AlgoElement algo = geo.getParentAlgorithm();
			if (algo == null) {
				// independent number may be shown as slider
				d = new DrawSlider(this, (GeoNumeric) geo);
			/*AG} else if (algo instanceof AlgoSlope) {
				d = new DrawSlope(this, (GeoNumeric) geo);
			} else if (algo instanceof AlgoIntegralDefinite) {
				d = new DrawIntegral(this, (GeoNumeric) geo);
			} else if (algo instanceof AlgoIntegralFunctions) {
				d = new DrawIntegralFunctions(this, (GeoNumeric) geo);
			} else if (algo instanceof AlgoFunctionAreaSums) {
				d = new DrawUpperLowerSum(this, (GeoNumeric) geo);*/
			}
			if (d != null) {
				if (!geo.isColorSet()) {
					ConstructionDefaults consDef = geo.getConstruction()
							.getConstructionDefaults();
					if (geo.isIndependent()) {
						org.geogebra.ggjsviewer.client.kernel.gawt.Color col = consDef.getDefaultGeo(
								ConstructionDefaults.DEFAULT_NUMBER).getObjectColor();
						geo.setObjColor(col);
					} else {
						org.geogebra.ggjsviewer.client.kernel.gawt.Color col = consDef.getDefaultGeo(
								ConstructionDefaults.DEFAULT_POLYGON)
								.getObjectColor();
						geo.setObjColor(col);
					}
				}			
			}
			break;
		/*	AG
		case GeoElement.GEO_CLASS_VECTOR:
			d = new DrawVector(this, (GeoVector) geo);
			break;
			*/
		case GeoElement.GEO_CLASS_CONICPART:
			//AGd = new DrawConicPart(this, (GeoConicPart) geo);
			break;
	
		case GeoElement.GEO_CLASS_CONIC:
			d = new DrawConic(this, (GeoConic) geo);
			break;
		/*
		case GeoElement.GEO_CLASS_FUNCTION:
		case GeoElement.GEO_CLASS_FUNCTIONCONDITIONAL:
			d = new DrawParametricCurve(this, (ParametricCurve) geo);
			break;

		case GeoElement.GEO_CLASS_TEXT:
			GeoText text = (GeoText) geo;
			d = new DrawText(this, text);				
			break;

		case GeoElement.GEO_CLASS_IMAGE:
			d = new DrawImage(this, (GeoImage) geo);
			break;

		case GeoElement.GEO_CLASS_LOCUS:
			d = new DrawLocus(this, (GeoLocus) geo);
			break;

		case GeoElement.GEO_CLASS_CURVE_CARTESIAN:
			d = new DrawParametricCurve(this, (GeoCurveCartesian) geo);
			break;

		case GeoElement.GEO_CLASS_LIST:
			d = new DrawList(this, (GeoList) geo);
			break;*/
		}
		if (d != null) {			
			DrawableMap.put(geo, d);
		}

		return d;
	}	
	
	/**
	 * returns GeoElement whose label is at screen coords (x,y).
	 */
	final public GeoElement getLabelHit(Point p) {
		if (!app.isLabelDragsEnabled()) return null;
		DrawableIterator it = allDrawableList.getIterator();
		while (it.hasNext()) {
			Drawable d = it.next();
			if (d.hitLabel(p.x, p.y)) {
				GeoElement geo = d.getGeoElement();
				if (geo.isEuclidianVisible())
					return geo;
			}
		}
		return null;
	}
	
	
	@Override
	public void clearView() {
		clear();
		resetLists();
		initView(false);
		// TODO Auto-generated method stub
		
	}

	private void resetLists() {
		DrawableMap.clear();
		allDrawableList.clear();
		bgImageList.clear();
		
		for (int i=0 ; i<=MAX_LAYER_USED ; i++) drawLayers[i].clear(); // Michael Borcherds 2008-02-29

		//AGsetToolTipText(null);
	}

	@Override
	public void remove(GeoElement geo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rename(GeoElement geo) {
		// TODO Auto-generated method stub
		
	}
	
	final public void repaintEuclidianView(){
		repaintView();
	}
	

	@Override
	public void repaintView() {
		clear();
		paint();
		//GWT.log("megvagy");
		
	}

	private void paint() {
		//clear();
		drawObjects();
		
		if (showMouseCoords /*AG test && (showAxes[0] || showAxes[1] || showGrid)*/)
			drawMouseCoords();
		// TODO Auto-generated method stub
		
	}

	final protected void drawMouseCoords() {
		Point pos = euclidianController.mouseLoc;
		if (pos == null)
			return;
		//We have some bug with stringbuilders.
		/*AGsb.setLength(0);
		sb.append('(');
		sb.append(kernel.format(euclidianController.xRW));
		if (kernel.getCoordStyle() == Kernel.COORD_STYLE_AUSTRIAN)
			sb.append(" | ");
		else
			sb.append(", ");
		sb.append(kernel.format(euclidianController.yRW));
		sb.append(')');*/
		if (kernel == null)
			return;
		String coordsToShow = "";
		coordsToShow += "("+kernel.format(euclidianController.xRW);
		if (kernel.getCoordStyle() == kernel.COORD_STYLE_AUSTRIAN) {
			coordsToShow +=" | ";
		} else {
			coordsToShow +=", ";
		}
		coordsToShow +=kernel.format(euclidianController.yRW);
		coordsToShow +=")";
		setStroke(Color.BLACK);
		strokeText(coordsToShow, pos.x + 15, pos.y + 15,this.getFont(),this.getFontSize());
	}
	
	public int getFontSize() {
		// TODO Auto-generated method stub
		return fontSize;
	}

	private void drawObjects() {
		// TODO Auto-generated method stub
		drawGeometricObjects();
		
		if (previewDrawable != null ) {
			previewDrawable.drawPreview();
		}		
	}

	private void drawGeometricObjects() {
		// TODO Auto-generated method stub
		int layer;
		
		for (layer=0 ; layer<=MAX_LAYER_USED ; layer++) // only draw layers we need
		{
			//if (isSVGExtensions) ((geogebra.export.SVGExtensions)g2).startGroup("layer "+layer);
			drawLayers[layer].drawAll();
			//if (isSVGExtensions) ((geogebra.export.SVGExtensions)g2).endGroup("layer "+layer);
		}
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(GeoElement geo) {
		// TODO Auto-generated method stub		
		Object d = DrawableMap.get(geo);
		if (d != null) {
			((Drawable) d).update();
		}
	}

	@Override
	public void updateAuxiliaryObject(GeoElement geo) {
		// TODO Auto-generated method stub
		
	}

	public void setEuclidianController(EuclidianController ec) {
		euclidianController = ec;
		// TODO Auto-generated method stub
		
	}

	@Override
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		// TODO Auto-generated method stub
		return addDomHandler(handler, MouseOverEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		// TODO Auto-gener
		return addDomHandler(handler, MouseOutEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
		// TODO Auto-generated method stub
		return addDomHandler(handler, MouseWheelEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
		// TODO Auto-generated method stub
		return addDomHandler(handler, MouseUpEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
		// TODO Auto-generated method stub
		return addDomHandler(handler, MouseMoveEvent.getType());
	}
	
	public void setApplication(Application a) {
		app = a;
	}
	
	public Application getApplication() {
		return app;
	}
	
	/**
	 * Returns xscale of this view. The scale is the number of pixels in screen
	 * space that represent one unit in user space.
	 */
	public double getXscale() {
		return xscale;
	}

	/**
	 * Returns the yscale of this view. The scale is the number of pixels in
	 * screen space that represent one unit in user space.
	 */
	public double getYscale() {
		return yscale;
	}

	public void setDecoStroke() {
		// TODO Auto-generated method stub
		
	}

	public void setSelStroke() {
		// TODO Auto-generated method stub
		
	}

	public void setObjStroke() {
		// TODO Auto-generated method stub
		
	}

	public void setPaint(
			org.geogebra.ggjsviewer.client.kernel.gawt.Color color) {
		this.setFillStyle(new Color(color.getRed(),color.getGreen(),color.getBlue(),color.getAlpha()));	
		//AGGWT.log(String.valueOf(color.getAlpha()));
		//Alfa value will needed to implement properly
	}

	public void setStroke(
			org.geogebra.ggjsviewer.client.kernel.gawt.Color color) {
		this.setStrokeStyle(new Color(color.getRed(),color.getGreen(),color.getBlue(),color.getAlpha()));
	}
	
	public void setStroke(BasicStroke stroke) {
		if (stroke != null) {
			this.setLineWidth(stroke.getLineWidth());
			this.setLineCap(stroke.getLineCap());
			this.setLineJoin(stroke.getLineJoin());
		}
	}
	
	
	
	/*public void setMode(int mode) {
		this.mode = mode;
		//AGinitCursor();
		euclidianController.setMode(mode);
		setSelectionRectangle(null);
	}*/

	static public BasicStroke getDefaultStroke() {
		return standardStroke;
	}

	public void setStroke(Color color) {
		this.setStrokeStyle(color);
		// TODO Auto-generated method stub		
	}

	public void setKernel(Kernel k) {
		kernel = k;
		// TODO Auto-generated method stub
		
	}

	public String getFont() {
		// The active font of the Canvas
		return canvasFont;
	}

	public void setFont(String font) {
		canvasFont = font;
		
	}

	public static BasicStroke getDefaultSelectionStroke() {
		// TODO Auto-generated method stub
		return selStroke;
	}


	public void drawEllipse(Ellipse2D.Double ellipse) {
		double kappa = .5522848;
		double ox = (ellipse.width / 2) * kappa;
		double oy = (ellipse.height / 2) * kappa;
		double xe = ellipse.x + ellipse.width;
		double ye = ellipse.y + ellipse.height;
		double xm = ellipse.x + ellipse.width / 2;
		double ym = ellipse.y + ellipse.height /2;
		
		this.beginPath();
		this.moveTo(ellipse.x, ym);
		this.cubicCurveTo(ellipse.x, ym - oy, xm - ox, ellipse.y, xm, ellipse.y);
		this.cubicCurveTo(xm + ox, ellipse.y, xe, ym - oy, xe, ym);
		this.cubicCurveTo(xe, ym + oy, xm + ox, ye, xm, ye);
		this.cubicCurveTo(xm - ox, ye, ellipse.x, ym + oy, ellipse.x, ym);
		this.closePath();
		this.stroke();
	}

	public void drawEllipse(Arc2D.Double arc) {
		double kappa = .5522848;
		double ox = (arc.width / 2) * kappa;
		double oy = (arc.height / 2) * kappa;
		double xe = arc.x + arc.width;
		double ye = arc.y + arc.height;
		double xm = arc.x + arc.width / 2;
		double ym = arc.y + arc.height /2;
		
		this.beginPath();
		this.moveTo(arc.x, ym);
		this.cubicCurveTo(arc.x, ym - oy, xm - ox, arc.y, xm, arc.y);
		this.cubicCurveTo(xm + ox, arc.y, xe, ym - oy, xe, ym);
		this.cubicCurveTo(xe, ym + oy, xm + ox, ye, xm, ye);
		this.cubicCurveTo(xm - ox, ye, arc.x, ym + oy, arc.x, ym);
		this.closePath();
		this.stroke();
		
		// TODO Auto-generated method stub
		
	}
	
	public void drawEllipse(Path2D.Double shape) {
		this.beginPath();
		PathIterator it = shape.getPathIterator(null);
		double[] coords = new double[6];
		while (!it.isDone()) {
			int cu = it.currentSegment(coords);
			switch (cu) {
			case PathIterator.SEG_MOVETO:
				this.moveTo(coords[0], coords[1]);
				break;
			case PathIterator.SEG_LINETO:
				this.lineTo(coords[0], coords[1]);
				break;
			case PathIterator.SEG_CUBICTO: 
				this.cubicCurveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
				break;
			case PathIterator.SEG_QUADTO:			
				this.quadraticCurveTo(coords[0], coords[1], coords[2], coords[3]);
				break;
			case PathIterator.SEG_CLOSE:
				this.closePath();
			default:
				break;
			}
			it.next();
		}
		//this.closePath();
		this.stroke();
		// TODO Auto-generated method stub
		
	}
	
	/*public void drawEllipse(Path2D.Double arc) {
		double kappa = .5522848;
		double ox = (arc.width / 2) * kappa;
		double oy = (arc.height / 2) * kappa;
		double xe = arc.x + arc.width;
		double ye = arc.y + arc.height;
		double xm = arc.x + arc.width / 2;
		double ym = arc.y + arc.height /2;
		
		this.beginPath();
		this.moveTo(arc.x, ym);
		this.cubicCurveTo(arc.x, ym - oy, xm - ox, arc.y, xm, arc.y);
		this.cubicCurveTo(xm + ox, arc.y, xe, ym - oy, xe, ym);
		this.cubicCurveTo(xe, ym + oy, xm + ox, ye, xm, ye);
		this.cubicCurveTo(xm - ox, ye, arc.x, ym + oy, arc.x, ym);
		this.closePath();
		this.stroke();
		
		// TODO Auto-generated method stub
		
	}*/
	/**
	 * Creates a stroke with thickness width, dashed according to line style
	 * type.
	 */
	public static BasicStroke getStroke(float width, int type) {
		float[] dash;

		switch (type) {
		case EuclidianView.LINE_TYPE_DOTTED:
			dash = new float[2];
			dash[0] = width; // dot
			dash[1] = 3.0f; // space
			break;

		case EuclidianView.LINE_TYPE_DASHED_SHORT:
			dash = new float[2];
			dash[0] = 4.0f + width;
			// short dash
			dash[1] = 4.0f; // space
			break;

		case EuclidianView.LINE_TYPE_DASHED_LONG:
			dash = new float[2];
			dash[0] = 8.0f + width; // long dash
			dash[1] = 8.0f; // space
			break;

		case EuclidianView.LINE_TYPE_DASHED_DOTTED:
			dash = new float[4];
			dash[0] = 8.0f + width; // dash
			dash[1] = 4.0f; // space before dot
			dash[2] = width; // dot
			dash[3] = dash[1]; // space after dot
			break;

		default: // EuclidianView.LINE_TYPE_FULL
			dash = null;
		}

		int endCap = dash != null ? BasicStroke.CAP_BUTT_INT : standardStroke
				.getEndCap();

		return new BasicStroke(width, endCap, standardStroke.getLineJoin(),
				standardStroke.getMiterLimit(), dash, 0.0f);
	}

	public void drawParabola(QuadCurve2D.Double shape) {
		this.beginPath();
		this.moveTo(shape.getX1(), shape.getY1());
		this.quadraticCurveTo(shape.getCtrlX(), shape.getCtrlY(), shape.getX2(), shape.getY2());
		this.closePath();
		this.stroke();
	}

	public void drawHyperbola(GeneralPathClipped shape) {
		this.beginPath();
		PathIterator it = shape.getPathIterator(null);
		double[] coords = new double[6];
		while (!it.isDone()) {
			int cu = it.currentSegment(coords);
			switch (cu) {
			case PathIterator.SEG_MOVETO:
				this.moveTo(coords[0], coords[1]);
				break;
			case PathIterator.SEG_LINETO:
				this.lineTo(coords[0], coords[1]);
				break;
			case PathIterator.SEG_CUBICTO: 
				this.cubicCurveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
				break;
			case PathIterator.SEG_QUADTO:			
				this.quadraticCurveTo(coords[0], coords[1], coords[2], coords[3]);
				break;
			case PathIterator.SEG_CLOSE:
				this.closePath();
			default:
				break;
			}
			it.next();
		}
		//this.closePath();
		this.stroke();
		
	}

	public void fill(GeneralPathClipped gp) {
		// TODO Auto-generated method stub
		this.beginPath();
		PathIterator it = gp.getPathIterator(null);
		double[] coords = new double[6];
		while (!it.isDone()) {
			int cu = it.currentSegment(coords);
			switch (cu) {
			case PathIterator.SEG_MOVETO:
				this.moveTo(coords[0], coords[1]);
				break;
			case PathIterator.SEG_LINETO:
				this.lineTo(coords[0], coords[1]);
				break;
			case PathIterator.SEG_CUBICTO: 
				this.cubicCurveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
				break;
			case PathIterator.SEG_QUADTO:			
				this.quadraticCurveTo(coords[0], coords[1], coords[2], coords[3]);
				break;
			case PathIterator.SEG_CLOSE:
				this.closePath();
			default:
				break;
			}
			it.next();
		}
		//this.closePath();
		this.fill();
		
		
		
	}

	public void draw(GeneralPathClipped gp) {
		// TODO Auto-generated method stub
		this.beginPath();
		PathIterator it = gp.getPathIterator(null);
		double[] coords = new double[6];
		while (!it.isDone()) {
			int cu = it.currentSegment(coords);
			switch (cu) {
			case PathIterator.SEG_MOVETO:
				this.moveTo(coords[0], coords[1]);
				break;
			case PathIterator.SEG_LINETO:
				this.lineTo(coords[0], coords[1]);
				break;
			case PathIterator.SEG_CUBICTO: 
				this.cubicCurveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
				break;
			case PathIterator.SEG_QUADTO:			
				this.quadraticCurveTo(coords[0], coords[1], coords[2], coords[3]);
				break;
			case PathIterator.SEG_CLOSE:
				this.closePath();
			default:
				break;
			}
			it.next();
		}
		//this.closePath();
		this.stroke();
		
	}
	
	

	

}