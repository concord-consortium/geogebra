package org.geogebra.ggjsviewer.client.euclidian;



import java.util.HashMap;
import java.util.LinkedHashMap;

import org.geogebra.ggjsviewer.client.euclidian.DrawableList.DrawableIterator;
import org.geogebra.ggjsviewer.client.kernel.AlgoElement;
import org.geogebra.ggjsviewer.client.kernel.BaseApplication;
import org.geogebra.ggjsviewer.client.kernel.ConstructionDefaults;
import org.geogebra.ggjsviewer.client.kernel.GeoAngle;
import org.geogebra.ggjsviewer.client.kernel.GeoBoolean;
import org.geogebra.ggjsviewer.client.kernel.GeoConic;
import org.geogebra.ggjsviewer.client.kernel.GeoConicPart;
import org.geogebra.ggjsviewer.client.kernel.GeoElement;
import org.geogebra.ggjsviewer.client.kernel.GeoLine;
import org.geogebra.ggjsviewer.client.kernel.GeoText;
import org.geogebra.ggjsviewer.client.kernel.GeoVec2D;
import org.geogebra.ggjsviewer.client.kernel.GeoVector;
import org.geogebra.ggjsviewer.client.kernel.GeoNumeric;
import org.geogebra.ggjsviewer.client.kernel.GeoPoint;
import org.geogebra.ggjsviewer.client.kernel.GeoPolygon;
import org.geogebra.ggjsviewer.client.kernel.GeoRay;
import org.geogebra.ggjsviewer.client.kernel.GeoSegment;
import org.geogebra.ggjsviewer.client.kernel.HasTimerAction;
import org.geogebra.ggjsviewer.client.kernel.Kernel;
import org.geogebra.ggjsviewer.client.kernel.View;
import org.geogebra.ggjsviewer.client.kernel.gawt.AffineTransform;
import org.geogebra.ggjsviewer.client.kernel.gawt.Arc2D;
import org.geogebra.ggjsviewer.client.kernel.gawt.BasicStroke;
import org.geogebra.ggjsviewer.client.kernel.gawt.Ellipse2D;
import org.geogebra.ggjsviewer.client.kernel.gawt.Font;
import org.geogebra.ggjsviewer.client.kernel.gawt.Line2D;
import org.geogebra.ggjsviewer.client.kernel.gawt.Path2D;
import org.geogebra.ggjsviewer.client.kernel.gawt.PathIterator;
import org.geogebra.ggjsviewer.client.kernel.gawt.Point;
import org.geogebra.ggjsviewer.client.kernel.gawt.QuadCurve2D;
import org.geogebra.ggjsviewer.client.kernel.gawt.Rectangle;
import org.geogebra.ggjsviewer.client.kernel.gawt.Path2D;
import org.geogebra.ggjsviewer.client.kernel.gawt.Shape;
import org.geogebra.ggjsviewer.client.kernel.gawt.Timer;
//import org.geogebra.ggjsviewer.client.kernel.gawt.Arc2D.Double;
import org.geogebra.ggjsviewer.client.main.Application;



import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.HasMouseMoveHandlers;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.HasMouseUpHandlers;
import com.google.gwt.event.dom.client.HasMouseWheelHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
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
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.widgetideas.graphics.client.Color;
import com.google.gwt.widgetideas.graphics.client.GWTCanvas;
import com.google.gwt.widgetideas.graphics.client.ImageLoader;


public class EuclidianView extends GWTCanvas implements EuclidianConstants, HasMouseDownHandlers, HasMouseOverHandlers, HasMouseOutHandlers, HasMouseWheelHandlers,HasMouseUpHandlers, HasMouseMoveHandlers, View {
	
	protected static final long serialVersionUID = 1L;
	protected Application app;

	protected Kernel kernel;

	protected EuclidianController euclidianController = new EuclidianController();
	private Hits hits;
	protected static final int MIN_WIDTH = 50;
	protected static final int MIN_HEIGHT = 50;
	
	public static final int DEFAULT_HEIGHT = 600;
	public static final int DEFAULT_WIDTH = 800;
	
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
	public static final int GRID_POLAR = 2;
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
	
	protected Line2D.Double tempLine = new Line2D.Double();
	protected Ellipse2D.Double circle = new Ellipse2D.Double(); //polar grid circles
	/*AG
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
	private static final int DEFAULT_FONT_SIZE = 12;
	private int MAX_LAYER_USED = 0;
	public DrawableList drawLayers[]; 

	// on add: change resetLists()
	// axis control vars 
	private double[] axisCross = {0,0};
	private boolean[] positiveAxes = {false, false};
	private boolean[] drawBorderAxes = {false,false};
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
	protected ImageElement pauseImage, upArrowImage, downArrowImage;
	protected LinkedHashMap<String, ImageElement> prefetchedImages = new LinkedHashMap<String, ImageElement>();
	public int fontSize = 12; //px
	private Font canvasFont = new Font("normal");
	public Font fontPoint = new Font("normal");
	public Font fontLine = new Font("normal");
	public Font fontConic =new Font("normal");
	public Font fontVector = new Font("normal");
	public Font fontAngle = new Font("normal");
	private Font cursorFont = new Font("normal");
	private Font fontAxes = new Font("normal") {
		{
			setFontSize(-2);
		}
	};
	
	/*Handling the text support with native canvas functions
	*/
	public native void fillText(String text, int x, int y, String fullFontString) /*-{
		if (!@org.geogebra.ggjsviewer.client.euclidian.EuclidianController::navigator_iPad) {	
			var eview = $doc.getElementById('eview');
			var ctx = eview.getContext('2d');
			ctx.font = fullFontString;
			ctx.fillText(text,x,y);
		}
	}-*/;
	
	public native int measureText(String text,String fullFontString) /*-{
		var eview = $doc.getElementById('eview');
		var ctx = eview.getContext('2d');
		var oldFont = ctx.font;
		ctx.font = fullFontString;
		var dim = ctx.measureText(text);
		ctx.font = oldFont;
		//console.log(dim.width);
		return dim.width;
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
		drawAxes();
		hits = new Hits();
		
		
		
		
	}
	
	final protected void clearBackground(/*AGGraphics2D g*/) {
		setColor(bgColor);
		fillRect(0, 0, width, height);
		//clear();
	}
	
	final protected void drawBackground(/*AGGraphics2D g,*/ boolean clear) {
		if (clear) {
			clearBackground(/*AGg*/);
		}

		//setAntialiasing(g);
		if (showGrid) {
			drawGrid(/*AGg*/);
		}
		if (showAxes[0] || showAxes[1])
			drawAxes(/*AGg*/);

		/*AGif (app.showResetIcon() && app.isApplet()) {
			// need to use getApplet().width rather than width so that
			// it works with applet rescaling
			int w = app.onlyGraphicsViewShowing() ? app.getApplet().width : width + 2;
			g.drawImage(getResetImage(), w - 18, 2, null);
		}*/
	}	
	
	private void drawBackgroundWithImages(/*AGGraphics2D g*/) {
		drawBackgroundWithImages(/*AGg, */false);
	}
	
	private void drawBackgroundWithImages(/*AGGraphics2D g,*/ boolean transparency) {
		if(!transparency)
			clearBackground(/*AGg*/);
		
		//AGbgImageList.drawAll(g); 
		drawBackground(/*AGg,*/ false);
	}
	
	protected void initView(boolean repaint) {
		// preferred size
		//AGsetPreferredSize(null);
		
		// init grid's line type
		setGridLineStyle(LINE_TYPE_DASHED_SHORT);
		setAxesLineStyle(AXES_LINE_TYPE_ARROW);
		setAxesColor(Color.BLACK); // Michael Borcherds 2008-01-26 was darkgray
		setGridColor(Color.LIGHTGREY);
		setBackground(Color.WHITE);

		//showAxes = true;
		//showGrid = false;
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
	
	private void setBackground(Color color) {
		if (color != null)
			this.bgColor = color;
		// TODO Auto-generated method stub
		
	}

	private void setGridColor(Color color) {
		if (color != null)
			this.gridColor = color;
		// TODO Auto-generated method stub
		
	}

	public void setGridColor(org.geogebra.ggjsviewer.client.kernel.gawt.Color  gridColor) {
		this.gridColor =new Color(gridColor.getRed(),gridColor.getGreen(),gridColor.getBlue());		
	}

	private void setAxesColor(Color axesColor) {
		if (axesColor != null)
			this.axesColor = axesColor;
		
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
			updateAllDrawables(repaint);
			//app.updateStatusLabelAxesRatio();
		}
	}
	
	final void drawGrid(/*AGGraphics2D g2*/) {
		setColor(gridColor);
		setStroke(gridStroke);

		// vars for handling positive-only axes
		double xCrossPix =  this.xZero + axisCross[1] * xscale;
		double yCrossPix =  this.yZero - axisCross[0] * yscale;
		int yAxisEnd = positiveAxes[1] ? (int) yCrossPix : height;		
		int xAxisStart = positiveAxes[0] ? (int) xCrossPix : 0;
		
		// set the clipping region to the region defined by the axes
		//AGShape oldClip = g2.getClip(); no such a simple clipping for <canvas> we must implement it if necessary
		//AG it won't do anything for nowif(gridType != GRID_POLAR) // don't do this for polar grids
			//AGg2.setClip(xAxisStart, 0, width, yAxisEnd);
		
		
		switch (gridType) {
		
		case GRID_CARTESIAN:

			// vertical grid lines
			double tickStep = xscale * gridDistances[0];
			double start = xZero % tickStep;
			double pix = start;	

			for (int i=0; pix <= width; i++) {	
				//int val = (int) Math.round(i);
				//g2.drawLine(val, 0, val, height);
				tempLine.setLine(pix, 0, pix, height);
				draw(tempLine);

				pix = start + i * tickStep;
			}

			// horizontal grid lines
			tickStep = yscale * gridDistances[1];
			start = yZero % tickStep;
			pix = start;

			for (int j=0; pix <= height; j++) {
				//int val = (int) Math.round(j);
				//g2.drawLine(0, val, width, val);
				tempLine.setLine(0, pix, width, pix);
				draw(tempLine);
				
				pix = start + j * tickStep;			
			}	

		break;
		
		
		case GRID_ISOMETRIC:
					
			double tickStepX = xscale * gridDistances[0] * Math.sqrt(3.0);
			double startX = xZero % (tickStepX);
			double startX2 = xZero % (tickStepX/2);
			double tickStepY = yscale * gridDistances[0];
			double startY = yZero % tickStepY;
			
			// vertical
			pix = startX2;
			for (int j=0; pix <= width; j++) {
				tempLine.setLine(pix, 0, pix, height);
				draw(tempLine);
				pix = startX2 + j * tickStepX/2.0;			
			}		

			// extra lines needed because it's diagonal
			int extra = (int)(height*xscale/yscale * Math.sqrt(3.0) / tickStepX)+3;
			
			// positive gradient
			pix = startX + -(extra+1) * tickStepX;			
			for (int j=-extra; pix <= width; j+=1) {
				tempLine.setLine(pix, startY-tickStepY, pix + (height+tickStepY) * Math.sqrt(3)*xscale/yscale, startY-tickStepY + height+tickStepY);
				draw(tempLine);
				pix = startX + j * tickStepX;			
			}						
			
			// negative gradient
			pix = startX;
			for (int j=0; pix <= width + (height*xscale/yscale+tickStepY) * Math.sqrt(3.0); j+=1) 
			//for (int j=0; j<=kk; j+=1)
			{
				tempLine.setLine(pix, startY-tickStepY, pix - (height+tickStepY) * Math.sqrt(3)*xscale/yscale, startY-tickStepY + height+tickStepY);
				draw(tempLine);
				pix = startX + j * tickStepX;			
			}						
			
			break;
			
			
		case GRID_POLAR:   //G.Sturr 2010-8-13
			
			// find minimum grid radius  
			double min;
			if(xZero > 0 && xZero < width &&  yZero > 0 && yZero < height){
				// origin onscreen: min = 0
				min = 0;
			}else{
				// origin offscreen: min = distance to closest screen border
				double minW = Math.min(Math.abs(xZero), Math.abs(xZero - width));
				double minH = Math.min(Math.abs(yZero), Math.abs(yZero - height));
				min = Math.min(minW, minH);
			}
					
			// find maximum grid radius
			// max =  max distance of origin to screen corners 	
			double d1 = GeoVec2D.length(xZero, yZero);  // upper left
			double d2 = GeoVec2D.length(xZero, yZero-height); // lower left
			double d3 = GeoVec2D.length(xZero-width, yZero); // upper right
			double d4 = GeoVec2D.length(xZero-width, yZero-height); // lower right		
			double max = Math.max(Math.max(d1, d2), Math.max(d3, d4));
			
			
			// draw the grid circles
			// note: x tick intervals are used for the radius intervals, 
			//       it is assumed that the x/y scaling ratio is 1:1
			double tickStepR = xscale * gridDistances[0];
			double r = min - min  % tickStepR;
			while (r <= max) {			
				circle.setFrame(xZero-r, yZero-r, 2*r, 2*r);	
				draw(circle);
				r = r + tickStepR;	
			
			}
			
			// draw the radial grid lines
			double angleStep = gridDistances[2];
			double y1, y2, m;
			
			// horizontal axis
			tempLine.setLine(0, yZero, width, yZero);
			draw(tempLine);
				
			// radial lines
			for(double a = angleStep ; a < Math.PI ; a = a + angleStep){
				
				if(Math.abs(a - Math.PI/2) < 0.0001){
					//vertical axis
					tempLine.setLine(xZero, 0, xZero, height);
				}else{
					m = Math.tan(a);
					y1 = m*(xZero) + yZero;	 
					y2 = m*(xZero - width) + yZero;
					tempLine.setLine(0, y1, width, y2);
				}
				draw(tempLine);
			}
			
			break;		
		}
		
		// reset the clipping region
		//AGg2.setClip(oldClip);
	}
	
	private void setColor(Color color) {
		if (color != null) {
			setStroke(color);
			setPaint(color);
		}
	}

	final protected void updateAllDrawables(boolean repaint) {
		allDrawableList.updateAll();
		if (repaint)
			repaintView();
	}
	
	public int getAxesLineStyle() {
		return axesLineType;
	}

	public void setAxesLineStyle(int axesLineStyle) {
		this.axesLineType = axesLineStyle;
	}
	

	public int getGridLineStyle() {
		return gridLineStyle;
	}

	public void setGridLineStyle(int gridLineStyle) {
		this.gridLineStyle = gridLineStyle;
		gridStroke = getStroke(gridIsBold?2f:1f, gridLineStyle); // Michael Borcherds 2008-04-11 added gridisbold
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
	// new global vars to control axes (should be set from options menu)
	private double xCross = 0.0;
	private boolean positiveY = false;
	private Object bgGraphics; //AG For images later
	
	final void drawAxes() {
		//-------------------------------------------------
		// add these local vars:
		
		// local xZero determines the x value at which the y-axis cross the x-axis
		// (should refactor to less confusing name)
		double xZero =  this.xZero + xCross * xscale;
		
		// height of yAxis: either line with full screen height 
		// or an upward ray starting from the x-axis
		int yAxisHeight = positiveY ? (int) yZero : height;		
		
		//--------------------------------------------------
		
		
		
		
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
		int fontsize = Integer.parseInt(fontAxes.getFontSize());
		int arrowSize = fontsize / 3;
		setPaint(axesColor);
		setStroke(axesColor);

		if (bold) {
			axesStroke = boldAxesStroke;
			tickStroke = boldAxesStroke;
			ySmall2++;
			xSmall2--;
			arrowSize += 1;
		} else {
			axesStroke = defAxesStroke;
			tickStroke = defAxesStroke;
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
				//AGTextLayout layout = new TextLayout(axesLabels[0], fontLine, frc);
				
				fillText(axesLabels[0], (int) (width - 10 - measureText(axesLabels[0], fontLine.getFullFontString())), (int) (yZero - 4),fontLine.getFullFontString());
			}

			// numbers
			double rw = xmin - (xmin % axesNumberingDistances[0]);
			
			
			// G.Sturr
			//-------- need global xZero here -------
			double pix = this.xZero + rw * xscale;    
			// -------------------------------------
			
			
			double axesStep = xscale * axesNumberingDistances[0]; // pixelstep
			double smallTickPix;
			double tickStep = axesStep / 2;
			if (pix < SCREEN_BORDER) {
				// big tick
				if (drawMajorTicks[0]) {
					setStroke(tickStroke);					
					tempLine.setLine(pix, yZeroTick, pix, yBig);
					draw(tempLine);
				}
				pix += axesStep;
				rw += axesNumberingDistances[0];
			}
			int maxX = width - SCREEN_BORDER;
			int prevTextEnd = -3;
			for (; pix < width; rw += axesNumberingDistances[0], pix += axesStep) {
				if (pix <= maxX) {
					if (showAxesNumbers[0]) {
						//AGString strNum = kernel.formatPiE(rw,
							//AG must be implemented	axesNumberFormat[0]);
						String strNum = kernel.format(rw);
						boolean zero = strNum.equals("0");

						sb.setLength(0);
						sb.append(strNum);
						if (axesUnitLabels[0] != null && !piAxisUnit[0])
							sb.append(axesUnitLabels[0]);

						//AGTextLayout layout = new TextLayout(sb.toString(),
							//AG	fontAxes, frc);
						int x, y = (int) (yZero + yoffset);
						if (zero && showAxes[1]) {
							x = (int) (pix + 6);
						} else {
							x = (int) (pix + xoffset - measureText(sb.toString(), fontAxes.getFullFontString()) / 2);
						}
												
						// make sure we don't print one string on top of the other
						if (x > prevTextEnd + 5) {
							prevTextEnd = (int) (x + measureText(sb.toString(), fontAxes.getFullFontString())); 
							fillText(sb.toString(), x, y,fontAxes.getFullFontString());
						}
					}

					// big tick
					if (drawMajorTicks[0]) {
						setStroke(tickStroke);
						tempLine.setLine(pix, yZeroTick, pix, yBig);
						draw(tempLine);
					}
				} else if (drawMajorTicks[0] && !drawArrows) {
					// draw last tick if there is no arrow
					tempLine.setLine(pix, yZeroTick, pix, yBig);
					draw(tempLine);
				}

				// small tick
				smallTickPix = pix - tickStep;
				if (drawMinorTicks[0]) {
					setStroke(tickStroke);
					tempLine.setLine(smallTickPix, ySmall1, smallTickPix,
							ySmall2);
					draw(tempLine);
				}
			}
			// last small tick
			smallTickPix = pix - tickStep;
			if (drawMinorTicks[0] && (!drawArrows || smallTickPix <= maxX)) {
				setStroke(tickStroke);
				tempLine.setLine(smallTickPix, ySmall1, smallTickPix, ySmall2);
				draw(tempLine);
			}

			// x-Axis
			setStroke(axesStroke);
			tempLine.setLine(0, yZero, width, yZero);
			draw(tempLine);

			if (drawArrows) {
				// tur antialiasing on
//				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//						antiAliasValue);

				// draw arrow for x-axis
				tempLine.setLine(width - 1, yZero, width - 1 - arrowSize, yZero
						- arrowSize);
				draw(tempLine);
				tempLine.setLine(width - 1, yZero, width - 1 - arrowSize, yZero
						+ arrowSize);
				draw(tempLine);

				//g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				//		RenderingHints.VALUE_ANTIALIAS_OFF);
			}
		}

		// Y-AXIS
		
		// ----------- axis may not cross at zero anymore
		//if (showAxes[1] && xmin < 0 && xmax > 0) {
		if (showAxes[1] && xmin < xCross && xmax > xCross) {
			
			if (showGrid) {
				xoffset = -2 - fontsize / 4;
				yoffset = -2;
			} else {
				xoffset = -4 - fontsize / 4;
				yoffset = fontsize / 2 - 1;
			}

			// label of y axis
			if (axesLabels[1] != null) {
				//AGTextLayout layout = new TextLayout(axesLabels[1], fontLine, frc);
				fillText(axesLabels[1], (int) (xZero + 5),
						(int) (5 + measureText(axesLabels[1], fontLine.getFullFontString())),fontLine.getFullFontString());
			}

			// numbers
			double rw = ymax - (ymax % axesNumberingDistances[1]);
			double pix = yZero - rw * yscale;
			double axesStep = yscale * axesNumberingDistances[1]; // pixelstep
			double tickStep = axesStep / 2;

			// first small tick
			double smallTickPix = pix - tickStep;
			if (drawMinorTicks[1]
					&& (!drawArrows || smallTickPix > SCREEN_BORDER)) {
				setStroke(tickStroke);
				tempLine.setLine(xSmall1, smallTickPix, xSmall2, smallTickPix);
				draw(tempLine);
			}

			// don't get too near to the top of the screen
			if (pix < SCREEN_BORDER) {
				if (drawMajorTicks[1] && !drawArrows) {
					// draw tick if there is no arrow
					setStroke(tickStroke);
					tempLine.setLine(xBig, pix, xZeroTick, pix);
					draw(tempLine);
				}
				smallTickPix = pix + tickStep;
				if (drawMinorTicks[1] && smallTickPix > SCREEN_BORDER) {
					setStroke(tickStroke);
					tempLine.setLine(xSmall1, smallTickPix, xSmall2,
							smallTickPix);
					draw(tempLine);
				}
				pix += axesStep;
				rw -= axesNumberingDistances[1];
			}
			
			// draw all of the remaining ticks and labels
			
			
			// G.Sturr
			// -------------------------------------------
			// added some adjustments here to handle drawing 
			// y-axis ticks and labels on a positive ray
			// ------------------------------------------
			
			//int maxY = height - SCREEN_BORDER;
			int maxY = positiveY ? (int) yZero : height - SCREEN_BORDER;
			
			//for (; pix <= height; rw -= axesNumberingDistances[1], pix += axesStep) {
			
			for (; pix <= yAxisHeight; rw -= axesNumberingDistances[1], pix += axesStep) {
				if (pix <= maxY) {
					if (showAxesNumbers[1]) {
						//AGString strNum = kernel.formatPiE(rw,
							//AG find out something	axesNumberFormat[1]);
						String strNum = kernel.format(rw);
						boolean zero = strNum.equals("0");

						sb.setLength(0);
						sb.append(strNum);
						if (axesUnitLabels[1] != null && !piAxisUnit[1])
							sb.append(axesUnitLabels[1]);

						//AGTextLayout layout = new TextLayout(sb.toString(),
						//AG		fontAxes, frc);
						int x = (int) (xZero + xoffset - measureText(sb.toString(), fontAxes.getFullFontString()));
						int y;
						if (zero && showAxes[0]) {
							y = (int) (yZero - 2);
						} else {
							y = (int) (pix + yoffset);
						}
						fillText(sb.toString(), x, y,fontAxes.getFullFontString());
					}
				}

				// big tick
				if (drawMajorTicks[1]) {
					setStroke(tickStroke);
					tempLine.setLine(xBig, pix, xZeroTick, pix);
					draw(tempLine);
				}

				smallTickPix = pix + tickStep;
				if (drawMinorTicks[1]) {
					setStroke(tickStroke);
					tempLine.setLine(xSmall1, smallTickPix, xSmall2,
							smallTickPix);
					draw(tempLine);
				}
			}

			// y-Axis
			
			//G.Sturr ---- use yAxisHeight instead of height so that 
			// yAxis can be drawn as a ray
			
			//tempLine.setLine(xZero, 0, xZero, height);
			tempLine.setLine(xZero, 0, xZero, yAxisHeight);
			
			
			draw(tempLine);

			if (drawArrows && xmin < 0 && xmax > 0) {
				// draw arrow for y-axis
				tempLine.setLine(xZero, 0, xZero - arrowSize, arrowSize);
				draw(tempLine);
				tempLine.setLine(xZero, 0, xZero + arrowSize, arrowSize);
				draw(tempLine);
			}								
		}
		
	
		// if one of the axes is not visible, show upper left and lower right corner coords
		if (showAxesCornerCoords) {
			if (xmin > 0 || xmax < 0 || ymin > 0 || ymax < 0) {
				// uper left corner								
				sb.setLength(0);
				sb.append('(');			
				//AGsb.append(kernel.formatPiE(xmin, axesNumberFormat[0]));
				sb.append(xmin);
				sb.append(Application.unicodeComma);
				sb.append(" ");
				//sb.append(kernel.formatPiE(ymax, axesNumberFormat[1]));
				sb.append(kernel.format(ymax));
				sb.append(')');
				
				int textHeight = 2 + Integer.parseInt(fontAxes.getFontSize());
				//AGg2.setFont(fontAxes);			
				fillText(sb.toString(), 5, textHeight,fontAxes.getFullFontString());
				
				// lower right corner
				sb.setLength(0);
				sb.append('(');			
				//AGsb.append(kernel.formatPiE(xmax, axesNumberFormat[0]));
				sb.append(kernel.format(xmax));
				sb.append(Application.unicodeComma);
				sb.append(" ");
				sb.append(kernel.format(ymin));
				sb.append(')');
				
				//TextLayout layout = new TextLayout(sb.toString(), fontAxes, frc);	
				//AGlayout.draw(g2, (int) (width - 5 - layout.getAdvance()), 
					//AG					height - 5);
				fillText(sb.toString(), width - 5 - measureText(sb.toString(), fontAxes.getFullFontString()), height - 5, fontAxes.getFullFontString());
			}	
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
			d = new DrawBoolean(this, (GeoBoolean) geo);			
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

		case GeoElement.GEO_CLASS_ANGLE:
			if (geo.isIndependent()) {
				// independent number may be shown as slider
				d = new DrawSlider(this, (GeoNumeric) geo);
			} else {
				d = new DrawAngle(this, (GeoAngle) geo);
				if (geo.isDrawable()) {
					if (!geo.isColorSet()) {
						org.geogebra.ggjsviewer.client.kernel.gawt.Color col = geo.getConstruction()
								.getConstructionDefaults().getDefaultGeo(
										ConstructionDefaults.DEFAULT_ANGLE)
								.getObjectColor();
						geo.setObjColor(col);
					}
				}
			}
			break;
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
		
		case GeoElement.GEO_CLASS_VECTOR:
			d = new DrawVector(this, (GeoVector) geo);
			break;
			
		case GeoElement.GEO_CLASS_CONICPART:
			d = new DrawConicPart(this, (GeoConicPart) geo);
			break;
	
		case GeoElement.GEO_CLASS_CONIC:
			d = new DrawConic(this, (GeoConic) geo);
			break;
		/*
		case GeoElement.GEO_CLASS_FUNCTION:
		case GeoElement.GEO_CLASS_FUNCTIONCONDITIONAL:
			d = new DrawParametricCurve(this, (ParametricCurve) geo);
			break;
		*/
		case GeoElement.GEO_CLASS_TEXT:
			GeoText text = (GeoText) geo;
			d = new DrawText(this, text);				
			break;
/*AG
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
		Drawable d = (Drawable) DrawableMap.get(geo);
		int layer = geo.getLayer();

		if (d != null) {
			switch (geo.getGeoClassType()) {
			//case GeoElement.GEO_CLASS_BOOLEAN:
				//drawLayers[layer].remove(d);
				// remove checkbox
				// not needed now it's not drawn by the view
				//((DrawBoolean) d).remove();
				//break;
			
			case GeoElement.GEO_CLASS_BUTTON:
				drawLayers[layer].remove(d);
				// remove button
				//AG((DrawButton) d).remove();
				break;
				
			case GeoElement.GEO_CLASS_TEXTFIELD:
				drawLayers[layer].remove(d);
				// remove button
				//AG((DrawTextField) d).remove();
				break;
			
			default:
				drawLayers[layer].remove(d);
				break;

			}

			allDrawableList.remove(d);

			DrawableMap.remove(geo);
			repaintView();
		}
		
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
		//clear();
		paint();
		//GWT.log("megvagy");
		
	}

	private void paint() {
		//clear();
		// BACKGROUND
		// draw background image (with axes and/or grid)
		//AGif (bgImage == null) {
			//AGif (firstPaint) {
				//AGupdateSize();
				//AGg2.drawImage(bgImage, 0, 0, null);
				//AGfirstPaint = false;				
			//AG} else {
				drawBackgroundWithImages(/*AGg2*/);
		//AG	}
		//AG} else {
			// draw background image
			//AGg2.drawImage(bgImage, 0, 0, null);
		//AG}

		
		
		
		drawObjects();
		
		if (showMouseCoords /*AG test && (showAxes[0] || showAxes[1] || showGrid)*/)
			drawMouseCoords();
		// TODO Auto-generated method stub
		if (kernel.needToShowAnimationButton()) {
			drawAnimationButtons();
		}
		
	}
	
	final protected void drawAnimationButtons(/*Graphics2D g2*/) {
		int x = 6;
		int y = height - 22;
				
		if (highlightAnimationButtons) {
			// draw filled circle to highlight button
			//AGg2.setColor(Color.darkGray);
			setStroke(Color.GREY);
		} else {
			setStroke(Color.LIGHTGREY);
			//AGg2.setColor(Color.lightGray);			
		}
		
		//AGg2.setStroke(EuclidianView.getDefaultStroke());
		setStroke(EuclidianView.getDefaultStroke());
		// draw pause or play button
		//g2.drawRect(x-2, y-2, 18, 18);
		rect(x-2,y-2,18,18);
		//Image img = kernel.isAnimationRunning() ? getPauseImage() : getPlayImage();			
		//g2.drawImage(img, x, y, null);
		String img = kernel.isAnimationRunning() ? getPauseImage() : getPlayImage();			
		drawPrefetchedImage(img, x, y);
	}
	
	
	public void drawPrefetchedImage(final String imageName,final int x,final int y) {
		//this should be written in the euclidianview, and the getImage() methods sould return wit strings
		//in this method the original drawImage should be called with fetched imageelements
		String[] url = new String[] {imageName};
	    if (!prefetchedImages.containsKey(imageName)) {
		    ImageLoader.loadImages(url, new ImageLoader.CallBack() {
				@Override
				public void onImagesLoaded(ImageElement[] imageElements) {	
					
					//drawImage here.
					prefetchedImages.put(imageName, imageElements[0]);
					drawImage(imageElements[0],x,y);
					
				}	      
		    });
	    } else {
	    	drawImage(prefetchedImages.get(imageName),x,y);
	    }
	}
	
	
	private String getResetImage() {
		return app.getRefreshViewImage();
	}
	
	private String getPlayImage() {
			return app.getPlayImage();
	}
	
	
	private String getPauseImage() {
			return app.getPauseImage();
	}
	
	public final boolean hitAnimationButton(MouseEvent e) {
		return kernel.needToShowAnimationButton() && (e.getX() <= 20) && (e.getY() >= height - 20);		
	}
	
	/**
	 * Updates highlighting of animation buttons. 
	 * @return whether status was changed
	 */
	public final boolean setAnimationButtonsHighlighted(boolean flag) {
		if (flag == highlightAnimationButtons) 
			return false;
		else {
			highlightAnimationButtons = flag;
			return true;
		}
	}
	
	// Michael Borcherds 2008-02-29
	public void changeLayer(GeoElement geo, int oldlayer, int newlayer)
	{
		updateMaxLayerUsed(newlayer);
		//Application.debug(drawLayers[oldlayer].size());
		drawLayers[oldlayer].remove((Drawable) DrawableMap.get(geo));
		//Application.debug(drawLayers[oldlayer].size());
		drawLayers[newlayer].add((Drawable) DrawableMap.get(geo));
		
	}
	
	public void updateMaxLayerUsed(int layer)
	{
		if (layer > MAX_LAYERS) layer=MAX_LAYERS;
		if (layer > MAX_LAYER_USED) MAX_LAYER_USED=layer;
	}

	public int getMaxLayerUsed()
	{
		return MAX_LAYER_USED;
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
		setFillStyle(Color.GREY);
		fillText(coordsToShow, pos.x + 15, pos.y + 15,cursorFont.getFullFontString());
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
		this.setFillStyle(new Color(color.getRed(),color.getGreen(),color.getBlue(),color.getAlpha()/255));	
		//AGGWT.log(String.valueOf(color.getAlpha()));
		//Alfa value will needed to implement properly
	}

	public void setStroke(
			org.geogebra.ggjsviewer.client.kernel.gawt.Color color) {
		this.setStrokeStyle(new Color(color.getRed(),color.getGreen(),color.getBlue(),color.getAlpha()/255));
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

	public Font getFont() {
		// The active font of the Canvas
		return canvasFont;
	}

	public void setFont(Font font) {
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

	public void fill(Shape shape) {
		// TODO Auto-generated method stub
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
		this.fill();
		
		
	}

	public void draw(Shape shape) {
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
	
	/**
	 * Returns the ratio yscale / xscale of this view. The scale is the number
	 * of pixels in screen space that represent one unit in user space.
	 */
	public double getScaleRatio() {
		return yscale / xscale;
	}

	public int getDefaultFontSize() {
		// TODO Auto-generated method stub
		return EuclidianView.DEFAULT_FONT_SIZE;
	}

	public void setPaint(Color color) {
		setFillStyle(color);
		// TODO Auto-generated method stub
		
	}

	public void setAxesColor(
			org.geogebra.ggjsviewer.client.kernel.gawt.Color axesColor) {
		if (axesColor != null)
			this.axesColor = new Color(axesColor.getRed(),axesColor.getGreen(),axesColor.getBlue());
	}

	public void setShowAxis(int axis, boolean flag, boolean update) {
		if (flag == showAxes[axis])
			return;
		
		showAxes[axis] = flag;
		
		if (update)
			updateBackgroundImage();
			
		
	}
	
	/**
	 * sets the axis label to axisLabel
	 * @param axis
	 * @param axisLabel
	 */
	public void setAxisLabel(int axis, String axisLabel){
		if (axisLabel != null && axisLabel.length() == 0) 
			axesLabels[axis] = null;
		else
			axesLabels[axis] = axisLabel;
	}
	

	public String[] getAxesUnitLabels() {
		return axesUnitLabels;
	}
	
	public void setAxesUnitLabels(String[] axesUnitLabels) {
		this.axesUnitLabels = axesUnitLabels;

		// check if pi is an axis unit
		for (int i = 0; i < 2; i++) {
			piAxisUnit[i] = axesUnitLabels[i] != null
					&& axesUnitLabels[i].equals(PI_STRING);
		}
		setAxesIntervals(xscale, 0);
		setAxesIntervals(yscale, 1);
	}

	public int[] getAxesTickStyles() {
		return axesTickStyles;
	}
	
	public void setAxisTickStyle(int axis, int tickStyle){
		axesTickStyles[axis]=tickStyle;
	}

	public void setAxesTickStyles(int[] axesTickStyles) {
		this.axesTickStyles = axesTickStyles;
	}

	public void setShowAxesNumbers(boolean[] showAxesNumbers) {
		this.showAxesNumbers = showAxesNumbers;
	}
	
	public void setShowAxisNumbers(int axis, boolean showAxisNumbers){
		showAxesNumbers[axis]=showAxisNumbers;
	}
	/**
	 * 
	 * @param x
	 * @param axis:
	 *            0 for xAxis, 1 for yAxis
	 */
	public void setAxesNumberingDistance(double dist, int axis) {
		axesNumberingDistances[axis] = dist;
		setAutomaticAxesNumberingDistance(false, axis);
	}
	
	public void setAutomaticAxesNumberingDistance(boolean flag, int axis) {
		automaticAxesNumberingDistances[axis] = flag;
		if (axis == 0)
			setAxesIntervals(xscale, 0);
		else
			setAxesIntervals(yscale, 1);
	}
	
	public void setShowAxes(boolean flag, boolean update){
		setShowAxis(AXIS_X, flag, false);
		setShowAxis(AXIS_Y, flag, true);
	}
	
	public void showGrid(boolean show) {
		if (show == showGrid)
			return;
		showGrid = show;
		updateBackgroundImage();
	}




	private void updateBackgroundImage() {
		// TODO Auto-generated method stub
		if (bgGraphics != null) {
			drawBackgroundWithImages(/*AGbgGraphics*/);
		}
		
	}
	
//	 Michael Borcherds 2008-04-11
	public void setGridIsBold(boolean gridIsBold ) {
		if (this.gridIsBold == gridIsBold) return;
		
		this.gridIsBold=gridIsBold;
		setGridLineStyle(gridLineStyle);
		
		updateBackgroundImage();
	}
	
	/**
	 * Sets the global size for checkboxes.
	 * Michael Borcherds 2008-05-12
	 */
	public void setBooleanSize(int size) {

		// only 13 and 26 currently allowed
		booleanSize = (size == 13) ? 13 : 26;
		
		updateAllDrawables(true);
	}
	
	/**
	 * Sets the global style for rightAngle drawing.
	 */
	public void setRightAngleStyle(int style) {
		rightAngleStyle = style;
		updateAllDrawables(true);
	}

	final public int getRightAngleStyle() {
		return rightAngleStyle;
	}
	
	final public int getBooleanSize() {
		return booleanSize;
	}

	public void drawLine(int x1, int y1, int x2, int y2) {
		beginPath();
		moveTo(x1, y1);
		lineTo(x2, y2);
		stroke();
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Sets coord system of this view. Just like setCoordSystem but with
	 * previous animation.
	 * 
	 * @param ox:
	 *            x coord of new origin
	 * @param oy:
	 *            y coord of new origin
	 * @param newscale
	 */
	final public void setAnimatedCoordSystem(double ox, double oy, double newScale,
			int steps, boolean storeUndo) {
		if (!kernel.isEqual(xscale, newScale)) {
			// different scales: zoom back to standard view
			double factor = newScale / xscale;
			zoom((ox - xZero * factor) / (1.0 - factor), (oy - yZero * factor)
					/ (1.0 - factor), factor, steps, storeUndo);
		} else {
			// same scales: translate view to standard origin
			// do this with the following action listener
			if (mover == null)
				mover = new MyMover();
			mover.init(ox, oy, storeUndo);
			mover.startAnimation();
		}
	}
	
	// used for animated moving of euclidian view to standard origin
	protected class MyMover implements HasTimerAction /*AGActionListener*/ {
		protected double dx, dy, add;

		protected int counter;

		protected double ox, oy; // new origin

		protected Timer timer;

		protected long startTime;

		protected boolean storeUndo;

		public MyMover() {
			timer = new Timer(MyZoomer.DELAY, this);
		}

		public void init(double ox, double oy, boolean storeUndo) {
			this.ox = ox;
			this.oy = oy;
			this.storeUndo = storeUndo;
		}

		public synchronized void startAnimation() {
			dx = xZero - ox;
			dy = yZero - oy;
			if (kernel.isZero(dx) && kernel.isZero(dy))
				return;

			// setDrawMode(DRAW_MODE_DIRECT_DRAW);
			add = 1.0 / MyZoomer.MAX_STEPS;
			counter = 0;

			startTime = System.currentTimeMillis();
			timer.start();
		}

		protected synchronized void stopAnimation() {
			timer.stop();
			// setDrawMode(DRAW_MODE_BACKGROUND_IMAGE);
			setCoordSystem(ox, oy, xscale, yscale);
			if (storeUndo)
				app.storeUndoInfo();
		}

		public synchronized void actionPerformed(/*AGActionEvent e*/) {
			counter++;
			long time = System.currentTimeMillis() - startTime;
			if (counter == MyZoomer.MAX_STEPS || time > MyZoomer.MAX_TIME) { // end
				// of
				// animation
				stopAnimation();
			} else {
				double factor = 1.0 - counter * add;
				setCoordSystem(ox + dx * factor, oy + dy * factor, xscale,
						yscale);
			}
		}
	}

	
	protected MyMover mover;
	
	/**
	 * Zooms around fixed point (px, py)
	 */
	public final void zoom(double px, double py, double zoomFactor, int steps,
			boolean storeUndo) {
		if (zoomer == null)
			zoomer = new MyZoomer();
		zoomer.init(px, py, zoomFactor, steps, storeUndo);
		zoomer.startAnimation();
		
		
	}
	
	protected class MyZoomer implements HasTimerAction /*AGimplements ActionListener*/ {
		static final int MAX_STEPS = 15; // frames

		static final int DELAY = 10;

		static final int MAX_TIME = 400; // millis

		protected Timer timer; // for animation

		protected double px, py; // zoom point

		protected double factor;

		protected int counter, steps;

		protected double oldScale, newScale, add, dx, dy;

		protected long startTime;

		protected boolean storeUndo;

		public MyZoomer() {
			timer = new Timer(DELAY, this);
		}

		public void init(double px, double py, double zoomFactor, int steps,
				boolean storeUndo) {
			this.px = px;
			this.py = py;
			// this.zoomFactor = zoomFactor;
			this.storeUndo = storeUndo;

			oldScale = xscale;
			newScale = xscale * zoomFactor;
			this.steps = Math.min(MAX_STEPS, steps);
		}

		public synchronized void startAnimation() {
			if (timer == null)
				return;
			// setDrawMode(DRAW_MODE_DIRECT_DRAW);
			add = (newScale - oldScale) / steps;
			dx = xZero - px;
			dy = yZero - py;
			counter = 0;

			startTime = System.currentTimeMillis();
			timer.start();
		}

		protected synchronized void stopAnimation() {
			timer.stop();
			// setDrawMode(DRAW_MODE_BACKGROUND_IMAGE);
			factor = newScale / oldScale;
			setCoordSystem(px + dx * factor, py + dy * factor, newScale,
					newScale * scaleRatio);

			if (storeUndo)
				app.storeUndoInfo();
		}

		public synchronized void actionPerformed(/*AGActionEvent e*/) {
			counter++;
			long time = System.currentTimeMillis() - startTime;
			if (counter == steps || time > MAX_TIME) { // end of animation
				stopAnimation();
			} else {
				factor = 1.0 + (counter * add) / oldScale;
				setCoordSystem(px + dx * factor, py + dy * factor, oldScale
						* factor, oldScale * factor * scaleRatio);
			}
		}
	}	
	
	protected MyZoomer zoomer;

	public void setBackground(
			org.geogebra.ggjsviewer.client.kernel.gawt.Color bgColor) {
		if (bgColor != null)
			this.bgColor = new Color(bgColor.getRed(),bgColor.getGreen(),bgColor.getBlue());
		
	}
	
	public void setGridDistances(double[] dist) {
		gridDistances = dist;
		setAutomaticGridDistance(false);
	}
	
	public void setAutomaticGridDistance(boolean flag) {
		automaticGridDistance = flag;
		setAxesIntervals(xscale, 0);
		setAxesIntervals(yscale, 1);
		if(flag)
			gridDistances[2] = Math.PI/6;
	}
	
	
	
	

	

}