package geogebra.geogebramobile.client.euclidian;

import geogebra.geogebramobile.client.kernel.GeoElement;
import geogebra.geogebramobile.client.kernel.gawt.Point;
import geogebra.geogebramobile.client.kernel.gawt.Rectangle;

import java.util.ArrayList;



/**
 * 
 * Interface between EuclidianView (2D or 3D) and EuclidianController (2D or 3D)
 * 
 * (TODO) see EuclidianView for detail of methods
 * 
 */

public interface EuclidianViewInterface {

	
	/** reference to x axis*/
	public static final int AXIS_X = 0; 
	/** reference to y axis*/	
	public static final int AXIS_Y = 1; 

	
	/*AG
	public void updateSize();
	public void repaintEuclidianView();


	/**
	 * Zooms around fixed point (px, py)
	 */
	//AGpublic void zoom(double px, double py, double zoomFactor, int steps, boolean storeUndo);



	// ??
	/*AGboolean hitAnimationButton(MouseEvent e);
	void setPreview(Previewable previewDrawable);
	public Drawable getDrawableFor(GeoElement geo);
	public DrawableND getDrawableND(GeoElement geo);
	public DrawableND createDrawableND(GeoElement geo);
	void setToolTipText(String plain);

	/**
	 * Updates highlighting of animation buttons. 
	 * @return whether status was changed
	 */
	/*AGboolean setAnimationButtonsHighlighted(boolean hitAnimationButton);

	/**
	 * Returns point capturing mode.
	 */
	int getPointCapturingMode();

	
	// selection rectangle
	/*AGpublic void setSelectionRectangle(Rectangle selectionRectangle);
	public Rectangle getSelectionRectangle();

	
	
	
	// cursor
	void setMoveCursor();
	void setDragCursor();
	void setDefaultCursor();
	void setHitCursor();
	

	
	
	// mode
	/**
	 * clears all selections and highlighting
	 */
	/*AGvoid resetMode();
	void setMode(int modeMove);

	
	// screen coordinate to real world coordinate
	/** convert screen coordinate x to real world coordinate x */
	/*AGpublic double toRealWorldCoordX(double minX);
	/** convert screen coordinate y to real world coordinate y */	
	/*AGpublic double toRealWorldCoordY(double maxY);

	public int toScreenCoordX(double minX);
	public int toScreenCoordY(double maxY);
	/**
	 * Sets real world coord system using min and max values for both axes in
	 * real world values.
	 */
	/*AGpublic void setAnimatedRealWorldCoordSystem(double xmin, double xmax,
			double ymin, double ymax, int steps, boolean storeUndo);





	
	
	
	
	//hits	
	/**get the hits recorded */
	Hits getHits();
	/** set the hits regarding to the mouse location */
	void setHits(Point p);
	
	
	/**
	 * sets array of GeoElements whose visual representation is inside of
	 * the given screen rectangle
	 */
	public void setHits(Rectangle rect);	
	
	GeoElement getLabelHit(Point mouseLoc);
	

	
	//////////////////////////////////////////////////////
	// AXIS, GRID, ETC.
	//////////////////////////////////////////////////////	
	
	
	/** sets the visibility of x and y axis
	 * @param showXaxis 
	 * @param showYaxis
	 * @deprecated use {@link EuclidianViewInterface#setShowAxes(boolean, boolean)} 
	 * or {@link EuclidianViewInterface#setShowAxis(int, boolean, boolean)} instead
	 */
	//void showAxes(boolean showXaxis, boolean showYaxis);
	
	
	
	
	boolean getShowXaxis();
	boolean getShowYaxis();
	
	
	
	boolean isGridOrAxesShown();
	int getGridType();
	void setCoordSystem(double x, double y, double xscale, double yscale);
	
	/**
	 * sets showing flag of the axis
	 * @param axis id of the axis
	 * @param flag show/hide
	 * @param update update (or not) the background image
	 */
	public void setShowAxis(int axis, boolean flag, boolean update);
	
	/**
	 * sets showing flag of all axes
	 * @param flag show/hide
	 * @param update update (or not) the background image
	 */	
	public void setShowAxes(boolean flag, boolean update);
	
	
	/**
	 * sets the axis label to axisLabel
	 * @param axis
	 * @param axisLabel
	 */
	public void setAxisLabel(int axis, String axisLabel);
	
	
	/** sets if numbers are shown on this axis
	 * @param axis
	 * @param showAxisNumbers
	 */
	public void setShowAxisNumbers(int axis, boolean showAxisNumbers);
	
	
	/** sets the tickstyle of this axis
	 * @param axis
	 * @param tickStyle
	 */
	public void setAxisTickStyle(int axis, int tickStyle);
	
	
	/** Sets coord system from mouse move */
	public void setCoordSystemFromMouseMove(int dx, int dy, int mode);
	void setAnimatedCoordSystem(double ox, double oy, double newScale,int steps, boolean storeUndo);


	//setters and getters	
	public void setShowMouseCoords(boolean b);
	public boolean getShowMouseCoords();
	double getXZero();
	double getYZero();
	public double getInvXscale();
	public double getInvYscale();
	double getXscale();
	double getYscale();
	public void setShowAxesRatio(boolean b);
	public Previewable getPreviewDrawable();
	public int getViewWidth();
	public int getViewHeight();
	public double getGridDistances(int i);
	public String[] getAxesLabels();
	public void setAxesLabels(String[] labels);
	public String[] getAxesUnitLabels();
	public void setShowAxesNumbers(boolean[] showNums);
	public void setAxesUnitLabels(String[] unitLabels);
	public boolean[] getShowAxesNumbers();
	public void setAxesNumberingDistance(double tickDist, int axis);
	public int[] getAxesTickStyles();
	
	
	/** remembers the origins values (xzero, ...) */
	public void rememberOrigins();
	
	
	

	/////////////////////////////////////////
	// previewables

	/**
	 * create a previewable for line construction
	 * @param selectedPoints points
	 * @return the line previewable
	 */
	public Previewable createPreviewLine(ArrayList selectedPoints);
	
	/**
	 * create a previewable for segment construction
	 * @param selectedPoints points
	 * @return the segment previewable
	 */	
	public Previewable createPreviewSegment(ArrayList selectedPoints);
	
	
	/**
	 * create a previewable for ray construction
	 * @param selectedPoints points
	 * @return the ray previewable
	 */	
	public Previewable createPreviewRay(ArrayList selectedPoints);

	
	/**
	 * create a previewable for vector construction
	 * @param selectedPoints points
	 * @return the ray previewable
	 */	
	public Previewable createPreviewVector(ArrayList selectedPoints);

	/**
	 * create a previewable for polygon construction
	 * @param selectedPoints points
	 * @return the polygon previewable
	 */		
	public Previewable createPreviewPolygon(ArrayList selectedPoints);
	


	public void updatePreviewable();
	
	
	public void mouseEntered();
	public void mouseExited();
	public Previewable createPreviewParallelLine(ArrayList selectedPoints,
			ArrayList selectedLines);
	public Previewable createPreviewPerpendicularLine(ArrayList selectedPoints,
			ArrayList selectedLines);
	public Previewable createPreviewPerpendicularBisector(ArrayList selectedPoints);
	public Previewable createPreviewAngleBisector(ArrayList selectedPoints);
	
	



	
	

	
}