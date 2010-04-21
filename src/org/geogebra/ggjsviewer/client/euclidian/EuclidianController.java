package org.geogebra.ggjsviewer.client.euclidian;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import org.geogebra.ggjsviewer.client.kernel.GeoElement;
import org.geogebra.ggjsviewer.client.kernel.GeoLine;
import org.geogebra.ggjsviewer.client.kernel.GeoPoint;
import org.geogebra.ggjsviewer.client.kernel.GeoPointInterface;
import org.geogebra.ggjsviewer.client.kernel.Kernel;
import org.geogebra.ggjsviewer.client.kernel.Path;
import org.geogebra.ggjsviewer.client.kernel.Region;
import org.geogebra.ggjsviewer.client.kernel.arithmetic.MyDouble;
import org.geogebra.ggjsviewer.client.kernel.gawt.Point;
import org.geogebra.ggjsviewer.client.kernel.gawt.Point2D;
import org.geogebra.ggjsviewer.client.main.Application;

import com.google.gwt.core.client.GWT;
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



public class EuclidianController implements MouseDownHandler, MouseMoveHandler, MouseOutHandler, MouseOverHandler, MouseUpHandler, MouseWheelHandler {
	
	protected static final int MOVE_NONE = 101;

	protected static final int MOVE_POINT = 102;

	protected static final int MOVE_LINE = 103;

	protected static final int MOVE_CONIC = 104;

	protected static final int MOVE_VECTOR = 105;

	protected static final int MOVE_VECTOR_STARTPOINT = 205;

	public static final int MOVE_VIEW = 106;

	protected static final int MOVE_FUNCTION = 107;

	protected static final int MOVE_LABEL = 108;

	protected static final int MOVE_TEXT = 109;

	protected static final int MOVE_NUMERIC = 110; // for number on slider

	protected static final int MOVE_SLIDER = 111; // for slider itself

	protected static final int MOVE_IMAGE = 112;

	protected static final int MOVE_ROTATE = 113;

	protected static final int MOVE_DEPENDENT = 114;

	protected static final int MOVE_MULTIPLE_OBJECTS = 115; // for multiple objects

	protected static final int MOVE_X_AXIS = 116;
	protected static final int MOVE_Y_AXIS = 117;

	protected static final int MOVE_BOOLEAN = 118; // for checkbox moving
	protected static final int MOVE_BUTTON = 119; 

	public static final int MOVE_ROTATE_VIEW = 120; // for 3D 

	protected Application app;

	//AGprotected Kernel kernel;

	//AGprotected EuclidianViewInterface view;
	//protected EuclidianView view;

	protected Point startLoc, mouseLoc, lastMouseLoc; // current mouse location

	//protected double xZeroOld, yZeroOld;
	protected double xTemp, yTemp;

	protected Point oldLoc = new Point();

	double xRW, yRW, // real world coords of mouse location
	xRWold = Double.NEGATIVE_INFINITY, yRWold = xRWold, temp;

	// for moving conics:
	protected Point2D.Double startPoint = new Point2D.Double();

	protected boolean useLineEndPoint = false;
	protected Point2D.Double lineEndPoint = null;

	protected Point selectionStartPoint = new Point();

	//protected GeoConic tempConic;

	//protected GeoFunction tempFunction;

	// protected GeoVec2D b;

	protected GeoPoint movedGeoPoint;
	protected boolean movedGeoPointDragged = false;

	protected GeoLine movedGeoLine;

	//protected GeoSegment movedGeoSegment;

	/*protected GeoConic movedGeoConic;

	protected GeoVector movedGeoVector;

	protected GeoText movedGeoText;

	protected GeoImage oldImage, movedGeoImage;	

	protected GeoFunction movedGeoFunction;

	protected GeoNumeric movedGeoNumeric;
	protected boolean movedGeoNumericDragged = false;

	protected GeoBoolean movedGeoBoolean;

	protected GeoButton movedGeoButton;
	*/
	protected GeoElement movedLabelGeoElement;

	protected GeoElement movedGeoElement;	

	protected GeoElement rotGeoElement, rotStartGeo;
	protected GeoPoint rotationCenter;
	public GeoElement recordObject;
	protected MyDouble tempNum;
	protected double rotStartAngle;
	protected ArrayList translateableGeos;
	//AGprotected GeoVector translationVec;

	protected Hits tempArrayList = new Hits();
	protected Hits tempArrayList2 = new Hits();
	protected Hits tempArrayList3 = new Hits();
	protected ArrayList selectedPoints = new ArrayList();

	protected ArrayList selectedNumbers = new ArrayList();

	protected ArrayList selectedLines = new ArrayList();

	protected ArrayList selectedSegments = new ArrayList();

	protected ArrayList selectedConics = new ArrayList();

	protected ArrayList selectedFunctions = new ArrayList();
	protected ArrayList selectedCurves = new ArrayList();

	protected ArrayList selectedVectors = new ArrayList();

	protected ArrayList selectedPolygons = new ArrayList();

	protected ArrayList selectedGeos = new ArrayList();

	protected ArrayList selectedLists = new ArrayList();

	protected LinkedList highlightedGeos = new LinkedList();

	protected boolean selectionPreview = false;

	protected boolean TEMPORARY_MODE = false; // changed from QUICK_TRANSLATEVIEW Michael Borcherds 2007-10-08

	protected boolean DONT_CLEAR_SELECTION = false; // Michael Borcherds 2007-12-08

	protected boolean DRAGGING_OCCURED = false; // for moving objects

	protected boolean POINT_CREATED = false;

	protected boolean moveModeSelectionHandled;

	//protected MyPopupMenu popupMenu;

	protected int mode, oldMode, moveMode = MOVE_NONE;
	//AGprotected Macro macro;
	protected Class [] macroInput;

	protected int DEFAULT_INITIAL_DELAY;

	protected boolean toggleModeChangedKernel = false;

	boolean altDown=false;

	private static String defaultRotateAngle = "45\u00b0"; // 45 degrees

	
	
	
	protected Kernel kernel;
	protected EuclidianView view;
	
	public EuclidianController() {
		// TODO Auto-generated constructor stub
		//FOR DEFAULT!
		mode =EuclidianView.MODE_MOVE;
	}
	
	public EuclidianController(Kernel kernel) {
		this.kernel = kernel;
		
	}
	
	public void setApplication(Application app) {
		this.app=app;
	}

	public Application getApplication() {
		return app;
	}
	
	public void setKernel(Kernel kernel) {
		this.kernel=kernel;
	}
	
	public Kernel getKernel() {
		return kernel;
	}
	
	public void setEuclidianView(EuclidianView view) {
		this.view = view;
	}
	
	public EuclidianView getEuclidianView() {
		return view;
	}
	protected void setMouseLocation(MouseEvent e) {
		//NOT FULLY VALID IF SCROLLBAR EXISTS AG
		int screen_x = e.getClientX() - view.getAbsoluteLeft();
		int screen_y = e.getClientY() - view.getAbsoluteTop();
		//GWT.log(String.valueOf(screen_x)+" : "+String.valueOf(screen_y));
		mouseLoc = new Point(screen_x,screen_y);

		altDown = e.isAltKeyDown();

		if (mouseLoc.x < 0)
			mouseLoc.x = 0;
		else if (mouseLoc.x > view.getOffsetWidth())
			mouseLoc.x = view.getOffsetWidth();
		if (mouseLoc.y < 0)
			mouseLoc.y = 0;
		else if (mouseLoc.y > view.getOffsetHeight())
			mouseLoc.y = view.getOffsetHeight();
	}
	
	protected void calcRWcoords() {
		xRW = (mouseLoc.x - view.getXZero()) * view.getInvXscale();
		yRW = (view.getYZero() - mouseLoc.y) * view.getInvYscale();
	}
	
	protected void transformCoords() {
		// calc real world coords
		calcRWcoords();

		// if alt pressed, make sure slope is a multiple of 15 degrees
		if ((mode == EuclidianView.MODE_JOIN || mode == EuclidianView.MODE_SEGMENT
				|| mode == EuclidianView.MODE_RAY || mode == EuclidianView.MODE_VECTOR)
				&& useLineEndPoint && lineEndPoint != null) {
			xRW = lineEndPoint.x;
			yRW = lineEndPoint.y;
			return;
		}

		if (mode == EuclidianView.MODE_MOVE && 
				moveMode == MOVE_NUMERIC) return; // Michael Borcherds 2008-03-24 bugfix: don't want grid on

		//	point capturing to grid
		double pointCapturingPercentage = 1;
		switch (view.getPointCapturingMode()) {	
		case EuclidianView.POINT_CAPTURING_AUTOMATIC:				
			if (!view.isGridOrAxesShown())break;

		case EuclidianView.POINT_CAPTURING_ON:
			pointCapturingPercentage = 0.125;

		case EuclidianView.POINT_CAPTURING_ON_GRID:

			switch (view.getGridType()) {
			case EuclidianView.GRID_ISOMETRIC:

				// isometric Michael Borcherds 2008-04-28
				// iso grid is effectively two rectangular grids overlayed (offset)
				// so first we decide which one we're on (oddOrEvenRow)
				// then compress the grid by a scale factor of root3 horizontally to make it square.

				double root3=Math.sqrt(3.0);
				double isoGrid=view.getGridDistances(0);
				int oddOrEvenRow = (int)Math.round(2.0*Math.abs(yRW - Kernel.roundToScale(yRW, isoGrid))/isoGrid);

				//Application.debug(oddOrEvenRow);

				if (oddOrEvenRow == 0)
				{
					// X = (x, y) ... next grid point
					double x = Kernel.roundToScale(xRW/root3, isoGrid);
					double y = Kernel.roundToScale(yRW, isoGrid);
					// if |X - XRW| < gridInterval * pointCapturingPercentage  then take the grid point
					double a = Math.abs(x - xRW/root3);
					double b = Math.abs(y - yRW);
					if (a < isoGrid * pointCapturingPercentage
							&& b < isoGrid *  pointCapturingPercentage) {
						xRW = x*root3;
						yRW = y;
					}

				}
				else
				{
					// X = (x, y) ... next grid point
					double x = Kernel.roundToScale(xRW/root3- view.getGridDistances(0)/2, isoGrid);
					double y = Kernel.roundToScale(yRW- isoGrid/2, isoGrid);
					// if |X - XRW| < gridInterval * pointCapturingPercentage  then take the grid point
					double a = Math.abs(x - (xRW/root3- isoGrid/2));
					double b = Math.abs(y - (yRW-isoGrid/2));
					if (a < isoGrid * pointCapturingPercentage
							&& b < isoGrid *  pointCapturingPercentage) {
						xRW = (x+ isoGrid/2)*root3;
						yRW = y+ isoGrid/2;
					}

				}
				break;

			case EuclidianView.GRID_CARTESIAN:

				// X = (x, y) ... next grid point
				double x = Kernel.roundToScale(xRW, view.getGridDistances(0));
				double y = Kernel.roundToScale(yRW, view.getGridDistances(1));
				// if |X - XRW| < gridInterval * pointCapturingPercentage  then take the grid point
				double a = Math.abs(x - xRW);
				double b = Math.abs(y - yRW);
				if (a < view.getGridDistances(0) * pointCapturingPercentage
						&& b < view.getGridDistances(1) *  pointCapturingPercentage) {
					xRW = x;
					yRW = y;
				}
				break;
			}

		default:
		}
	}
	
	
	
	
	
	@Override
	public void onMouseDown(MouseDownEvent event) {
		/*Window.alert((event.getClientX()- view.getAbsoluteLeft())+" "+(event.getClientY()-view.getAbsoluteTop()));
		double x = event.getClientX()-view.getAbsoluteLeft();
		double y = event.getClientY()-view.getAbsoluteTop();
		view.beginPath();
		view.arc(x, y, 4, 0, Math.PI*2, false);
		view.setFillStyle(new Color(0,0,200));
		view.stroke();
		view.fill();*/
		double screen_x = event.getClientX() - view.getAbsoluteLeft();
		double screen_y = event.getClientY() - view.getAbsoluteTop();
		GWT.log("screen_x: "+String.valueOf(screen_x));
		GWT.log("sceenn_y: "+String.valueOf(screen_y));
		// TODO Auto-generated method stub
		if (mode == EuclidianView.MODE_PEN) {
			//AGhandleMousePressedForPenMode(e);
			return;
		}
		GWT.log(String.valueOf(mode));
		//GeoElement geo;
		Hits hits;
		setMouseLocation(event);
		transformCoords();	

		moveModeSelectionHandled = false;
		DRAGGING_OCCURED = false;			
		view.setSelectionRectangle(null);
		selectionStartPoint.setLocation(mouseLoc);	

		/*AGif (hitResetIcon() || view.hitAnimationButton(e)) {				
			// see mouseReleased
			return;
		}*/

		/*AGif (Application.isRightClick(e)) {			
			//ggb3D - for 3D rotation
			processRightPressFor3D();
			return;
		} 
		else if (
				app.isShiftDragZoomEnabled() && 
				(
						// MacOS: shift-cmd-drag is zoom
						(e.isShiftDown() && !Application.isControlDown(e)) // All Platforms: Shift key
						|| 
						e.isControlDown() && Application.WINDOWS // old Windows key: Ctrl key 
				)) 
		{
			// Michael Borcherds 2007-12-08 BEGIN
			// bugfix: couldn't select multiple objects with Ctrl		


			view.setHits(mouseLoc);
			hits = view.getHits();hits.removePolygons();
			if (!hits.isEmpty()) // bugfix 2008-02-19 removed this:&& ((GeoElement) hits.get(0)).isGeoPoint())
			{
				DONT_CLEAR_SELECTION=true;
			}
			//			 Michael Borcherds 2007-12-08 END
			TEMPORARY_MODE = true;
			oldMode = mode; // remember current mode	
			view.setMode(EuclidianView.MODE_TRANSLATEVIEW);				
		} 	*/	


		switch (mode) {
		// create new point at mouse location
		// this point can be dragged: see mouseDragged() and mouseReleased()
		case EuclidianView.MODE_POINT:
		case EuclidianView.MODE_POINT_IN_REGION:				
			view.setHits(mouseLoc);
			hits = view.getHits();
			// if mode==EuclidianView.MODE_POINT_INSIDE, point can be in a region
			createNewPoint(hits, true, mode==EuclidianView.MODE_POINT_IN_REGION, true, true); 
			break;
		
		case EuclidianView.MODE_SEGMENT:
		case EuclidianView.MODE_SEGMENT_FIXED:		
		case EuclidianView.MODE_JOIN:
		case EuclidianView.MODE_RAY:
		case EuclidianView.MODE_VECTOR:
		case EuclidianView.MODE_CIRCLE_TWO_POINTS:
		case EuclidianView.MODE_CIRCLE_POINT_RADIUS:
		case EuclidianView.MODE_CIRCLE_THREE_POINTS:
		case EuclidianView.MODE_ELLIPSE_THREE_POINTS:
		case EuclidianView.MODE_HYPERBOLA_THREE_POINTS:
		case EuclidianView.MODE_CIRCLE_ARC_THREE_POINTS:
		case EuclidianView.MODE_CIRCLE_SECTOR_THREE_POINTS:
		case EuclidianView.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
		case EuclidianView.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
		case EuclidianView.MODE_SEMICIRCLE:
		case EuclidianView.MODE_CONIC_FIVE_POINTS:
		case EuclidianView.MODE_POLYGON:
		case EuclidianView.MODE_REGULAR_POLYGON:	
			//hits = view.getHits(mouseLoc);
			view.setHits(mouseLoc);
			hits = view.getHits();hits.removePolygons();
			createNewPoint(hits, true, true, true); 
			break;
		/*	
		case EuclidianView.MODE_PARALLEL:
		case EuclidianView.MODE_PARABOLA: // Michael Borcherds 2008-04-08
		case EuclidianView.MODE_ORTHOGONAL:
		case EuclidianView.MODE_LINE_BISECTOR:
		case EuclidianView.MODE_ANGULAR_BISECTOR:
		case EuclidianView.MODE_TANGENTS:		
		case EuclidianView.MODE_POLAR_DIAMETER:
			//hits = view.getHits(mouseLoc);
			view.setHits(mouseLoc);
			hits = view.getHits();hits.removePolygons();
			createNewPoint(hits, false, true, true);
			break;		

		case EuclidianView.MODE_COMPASSES:		// Michael Borcherds 2008-03-13	
			//hits = view.getHits(mouseLoc);
			view.setHits(mouseLoc);
			hits = view.getHits();hits.removePolygons();
			createNewPoint(hits, false, true, true);
			break;		

		case EuclidianView.MODE_ANGLE:
			//hits = view.getTopHits(mouseLoc);
			view.setHits(mouseLoc);
			hits = view.getHits().getTopHits();
			// check if we got a polygon
			if (hits.isEmpty() || !((GeoElement) hits.get(0)).isGeoPolygon()) {
				createNewPoint(hits, false, false, true);			
			}			
			break;

		case EuclidianView.MODE_ANGLE_FIXED:
		case EuclidianView.MODE_MIDPOINT:
			//hits = view.getHits(mouseLoc);
			view.setHits(mouseLoc);
			hits = view.getHits();hits.removePolygons();
			createNewPoint(hits, false, false, true);			
			break;

		case EuclidianView.MODE_MOVE_ROTATE:
			handleMousePressedForRotateMode();
			break;

		case EuclidianView.MODE_RECORD_TO_SPREADSHEET:
			handleMousePressedForRecordToSpreadsheetMode(e);
			break;

			// move an object
			 */
		case EuclidianView.MODE_MOVE:	
		case EuclidianView.MODE_VISUAL_STYLE:	
			//handleMousePressedForMoveMode(event, false);			
			break;
		/*
			// move drawing pad or axis
		case EuclidianView.MODE_TRANSLATEVIEW:		

			mousePressedTranslatedView(e);

			break;								
		*/
		default:
			moveMode = MOVE_NONE;			 
		}
	}
	
	private boolean allowPointCreation() {
		return  mode == EuclidianView.MODE_POINT || 
		mode == EuclidianView.MODE_POINT_IN_REGION || true;
		//AG NECESSARY?app.isOnTheFlyPointCreationActive(); 
	}

	
	final protected boolean createNewPoint(Hits hits,
			boolean onPathPossible, boolean inRegionPossible, boolean intersectPossible, boolean doSingleHighlighting) {

		if (!allowPointCreation()) 
			return false;



		GeoPointInterface point = getNewPoint(hits, onPathPossible, inRegionPossible, intersectPossible, doSingleHighlighting);

		if (point != null) {

			updateMovedGeoPoint(point);

			movedGeoElement = getMovedGeoPoint();
			moveMode = MOVE_POINT;
			view.setDragCursor();
			if (doSingleHighlighting)
				doSingleHighlighting(getMovedGeoPoint());
			POINT_CREATED = true;
			return true;
		} else {
			moveMode = MOVE_NONE;
			POINT_CREATED = false;
			return false;
		}
	}
	
	protected void updateMovedGeoPoint(GeoPointInterface point){
		movedGeoPoint = (GeoPoint) point;
	}
	
	/** return the current movedGeoPoint */
	public GeoElement getMovedGeoPoint(){
		return movedGeoPoint;
	}
	
	protected void doSingleHighlighting(GeoElement geo) {
		if (geo == null) return;

		if (highlightedGeos.size() > 0) {
			setHighlightedGeos(false);
		}		

		highlightedGeos.add(geo);
		geo.setHighlighted(true); 
		kernel.notifyRepaint();					
	}
	
//	set highlighted state of all highlighted geos without repainting
	protected final void setHighlightedGeos(boolean highlight) {
		GeoElement geo;
		Iterator it = highlightedGeos.iterator();
		while (it.hasNext()) {
			geo = (GeoElement) it.next();
			geo.setHighlighted(highlight);
		}
	}

	
	protected GeoPointInterface getNewPoint(Hits hits,
			boolean onPathPossible, boolean inRegionPossible, boolean intersectPossible, 
			boolean doSingleHighlighting) {

		return updateNewPoint(false, hits, 
				onPathPossible, inRegionPossible, intersectPossible, 
				doSingleHighlighting, true);
	}
	
	public GeoPointInterface updateNewPoint(
			boolean forPreviewable,
			Hits hits,
			boolean onPathPossible, boolean inRegionPossible, boolean intersectPossible,
			boolean doSingleHighlighting, boolean chooseGeo) {

		// create hits for region
		//Hits regionHits = hits.getHits(Region.class, tempArrayList);

		// only keep polygon in hits if one side of polygon is in hits too
		if (!hits.isEmpty())
			hits.keepOnlyHitsForNewPointMode();

		Path path = null;	
		Region region = null;
		boolean createPoint = true;
		if (hits.containsGeoPoint()){
			createPoint = false;
			if (forPreviewable){
				createNewPoint((GeoPointInterface) hits.getHits(GeoPoint.GEO_CLASS_POINT, tempArrayList).get(0));				
			}
		}


		GeoPointInterface point = null;



		//	try to get an intersection point
		if (createPoint && intersectPossible) {
			GeoPointInterface intersectPoint = getSingleIntersectionPoint(hits);
			if (intersectPoint != null) {
				if (!forPreviewable){
					point = intersectPoint;
					// we don't use an undefined or infinite
					// intersection point
					if (!point.showInEuclidianView()) {
						point.remove();
					} else
						createPoint = false;
				}else{
					createNewPointIntersection(intersectPoint);
					createPoint = false;
				}
			}
		}



		// check for paths and regions
		if (createPoint) {

			// check if point lies in a region and if we are allowed to place a point
			// in a region
			/*AGif (!regionHits.isEmpty()) {
				if (inRegionPossible) {
					if (chooseGeo)
						region = (Region) chooseGeo(regionHits, true);
					else
						region = (Region) regionHits.get(0);
					createPoint = region != null;
				} else {
					createPoint = true;
					// if inRegionPossible is false, the point is created as a free point
				}*/

			}


			//check if point lies on path and if we are allowed to place a point
			// on a path			
			/*AGHits pathHits = hits.getHits(Path.class, tempArrayList);
			if (!pathHits.isEmpty()) {
				if (onPathPossible) {
					if (chooseGeo)
						path = (Path) chooseGeo(pathHits, true);
					else
						path = (Path) regionHits.get(0);
					createPoint = path != null;
				} else {
					createPoint = false;
				}
			}

		}*/



		//Application.debug("createPoint 3 = "+createPoint);

		if (createPoint) {
			transformCoords(); // use point capturing if on
			if (path == null) {
				if (region == null){
					//point = kernel.Point(null, xRW, yRW);
					point = createNewPoint(forPreviewable);
					view.setShowMouseCoords(true);
				} else {
					//Application.debug("in Region : "+region);
					point = createNewPoint(forPreviewable, region);
				}
			} else {
				//point = kernel.Point(null, path, xRW, yRW);
				point = createNewPoint(forPreviewable, path);
			}
		}

		return point;
	}
	
	protected void createNewPoint(GeoPointInterface sourcePoint){


	}
	
	protected GeoPointInterface createNewPoint(boolean forPreviewable){
		GeoPointInterface ret = kernel.Point(null, xRW, yRW);
		return ret;
	}
	
	/**
	 * Shows dialog to choose one object out of hits[] that is an instance of
	   specified class (note: subclasses are included)
	 * 
	 */
	private GeoElement chooseGeo(Hits hits, int geoid) {
		return chooseGeo(hits.getHits(geoid, tempArrayList), true);
	}
	
	protected GeoElement chooseGeo(ArrayList geos, boolean includeFixed) {
		if (geos == null)
			return null;

		GeoElement ret = null;
		GeoElement retFree = null;
		GeoElement retPath = null;
		GeoElement retIndex = null;

		switch (geos.size()) {
		case 0:
			ret =  null;
			break;

		case 1:
			ret =  (GeoElement) geos.get(0);
			break;

		default:	

			int maxLayer = -1;

			int layerCount = 0;

			// work out max layer, and
			// count no of objects in max layer
			for (int i = 0 ; i < geos.size() ; i++) {
				GeoElement geo = (GeoElement)(geos.get(i));
				int layer = geo.getLayer();

				if (layer > maxLayer && (includeFixed || !geo.isFixed())) {
					maxLayer = layer;
					layerCount = 1;
					ret = geo;
				}
				else if (layer == maxLayer)
				{
					layerCount ++;
				}

			}

			//Application.debug("maxLayer"+maxLayer);
			//Application.debug("layerCount"+layerCount);

			// only one object in top layer, return it.
			if (layerCount == 1) return ret;


			int pointCount = 0;
			int freePointCount = 0;
			int pointOnPathCount = 0;
			int maxIndex = -1;

			// count no of points in top layer
			for (int i = 0 ; i < geos.size() ; i++) {
				GeoElement geo = (GeoElement)(geos.get(i));
				if (geo.isGeoPoint() && geo.getLayer() == maxLayer
						&& (includeFixed || !geo.isFixed())) {
					pointCount ++;
					ret = geo;

					// find point with the highest construction index
					int index = geo.getConstructionIndex();
					if (index > maxIndex) {
						maxIndex = index;
						retIndex = geo;
					}

					// find point-on-path with the highest construction index
					if (((GeoPointInterface)geo).isPointOnPath()) {
						pointOnPathCount ++;
						if (retPath == null) {
							retPath = geo;
						}
						else
						{
							if (geo.getConstructionIndex() > retPath.getConstructionIndex()) retPath = geo;
						}
					}

					// find free point with the highest construction index
					if (geo.isIndependent()) {
						freePointCount ++;
						if (retFree == null) {
							retFree = geo;
						}
						else
						{
							if (geo.getConstructionIndex() > retFree.getConstructionIndex()) retFree = geo;						
						}
					}
				}
			}
			//Application.debug("pointOnPathCount"+pointOnPathCount);
			//Application.debug("freePointCount"+freePointCount);
			//Application.debug("pointCount"+pointCount);

			// return point-on-path with highest index
			if (pointOnPathCount > 0) return retPath;

			// return free-point with highest index
			if (freePointCount > 0) return retFree;

			// only one point in top layer, return it
			if (pointCount == 1) return ret;

			// just return the most recently created point
			if (pointCount > 1) return retIndex;

			/*
			try {
				throw new Exception("choose");
			} catch (Exception e) {
				e.printStackTrace();

			}
			 */


			// remove fixed objects (if there are some not fixed)
			if (!includeFixed && geos.size() > 1) {

				boolean allFixed = true;
				for (int i = 0 ; i < geos.size() ; i++)
					if (!((GeoElement)geos.get(i)).isFixed())
						allFixed = false;

				if (!allFixed)
					for (int i = geos.size() - 1 ; i >= 0 ; i--) {
						GeoElement geo = (GeoElement)geos.get(i);
						if (geo.isFixed()) geos.remove(i);		
					}

				if (geos.size() == 1)
					return (GeoElement)geos.get(0);
			}

			// no points selected, multiple objects selected
			// popup a menu to choose from
			/*AGToolTipManager ttm = ToolTipManager.sharedInstance();		
			ttm.setEnabled(false);			
			ListDialog dialog = new ListDialog((JPanel) view, geos, null);
			if (app.areChooserPopupsEnabled()) ret = dialog.showDialog((JPanel) view, mouseLoc);			
			ttm.setEnabled(true);*/				
		}
		return ret;	

	}
	
	protected GeoPointInterface getSingleIntersectionPoint(Hits hits) {
		if (hits.isEmpty() || hits.size() != 2)
			return null;

		GeoElement a = (GeoElement) hits.get(0);
		GeoElement b = (GeoElement) hits.get(1);

		// first hit is a line
		if (a.isGeoLine()) {
			if (b.isGeoLine())
				if (!((GeoLine) a).linDep((GeoLine) b))
					return kernel.IntersectLines(null, (GeoLine) a, (GeoLine) b);
				else 
					return null;
			/*AGelse if (b.isGeoConic())
				return kernel.IntersectLineConicSingle(null, (GeoLine) a,
						(GeoConic) b, xRW, yRW);
			else if (b.isGeoFunctionable()) {
				// line and function
				GeoFunction f = ((GeoFunctionable) b).getGeoFunction();
				if (f.isPolynomialFunction(false))
					return kernel.IntersectPolynomialLineSingle(null, f,
							(GeoLine) a, xRW, yRW);
				else {
					GeoPoint startPoint = new GeoPoint(kernel.getConstruction());
					startPoint.setCoords(xRW, yRW, 1.0);
					return kernel.IntersectFunctionLine(null, f, (GeoLine) a,
							startPoint);
				}*/
			/*AG}*/ else
				return null;
		/*AG}
		//	first hit is a conic
		else if (a.isGeoConic()) {
			if (b.isGeoLine())
				return kernel.IntersectLineConicSingle(null, (GeoLine) b,
						(GeoConic) a, xRW, yRW);
			else if (b.isGeoConic())
				return kernel.IntersectConicsSingle(null, (GeoConic) a,
						(GeoConic) b, xRW, yRW);
			else
				return null;
		}
		// first hit is a function
		else if (a.isGeoFunctionable()) {
			GeoFunction aFun = (GeoFunction) a;
			if (b.isGeoFunctionable()) {
				GeoFunction bFun = ((GeoFunctionable) b).getGeoFunction();
				if (aFun.isPolynomialFunction(false) && bFun.isPolynomialFunction(false))
					return kernel.IntersectPolynomialsSingle(null, aFun, bFun,
							xRW, yRW);
				else {
					GeoPoint startPoint = new GeoPoint(kernel.getConstruction());
					startPoint.setCoords(xRW, yRW, 1.0);
					return kernel.IntersectFunctions(null, aFun, bFun,
							startPoint);
				}
			} else if (b.isGeoLine()) {
				// line and function
				GeoFunction f = (GeoFunction) a;
				if (f.isPolynomialFunction(false))
					return kernel.IntersectPolynomialLineSingle(null, f,
							(GeoLine) b, xRW, yRW);
				else {
					GeoPoint startPoint = new GeoPoint(kernel.getConstruction());
					startPoint.setCoords(xRW, yRW, 1.0);
					return kernel.IntersectFunctionLine(null, f, (GeoLine) b,
							startPoint);
				}
			} else
				return null;
				*/
		} else
			return null;
	}
	
	final protected boolean createNewPoint(Hits hits,
			boolean onPathPossible, boolean intersectPossible, boolean doSingleHighlighting) {

		return createNewPoint(hits,onPathPossible, false, intersectPossible,  doSingleHighlighting);		
	}
	
	protected GeoPointInterface createNewPoint(boolean forPreviewable, Region region){
		GeoPointInterface ret = kernel.PointIn(null,region,xRW, yRW);
		return ret;
	}
	
	protected GeoPointInterface createNewPoint(boolean forPreviewable, Path path){
		GeoPointInterface ret = kernel.Point(null, path, xRW, yRW);
		return ret;
	}

	
	/** only used in 3D */
	protected void createNewPointIntersection(GeoPointInterface intersectionPoint){
	}

	//JUST FOR DEBUG
 

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		setMouseLocation(event);
		processMouseMoved(event);
		
		// TODO Auto-generated method stub
		//JUST FOR DEBUG PURPOSES AG (FOR NOW
		
		
	}

	protected void processMouseMoved(MouseMoveEvent event) {
		// TODO Auto-generated method stub
		//GWT.log("move");
		//view.strokeText(String.valueOf(mouseLoc.x)+" "+String.valueOf(mouseLoc.y), 30, 30);
		boolean repaintNeeded = false; //AG or false should be here?
		// standard handling
		Hits hits = new Hits();
		boolean noHighlighting = false;		
		altDown=event.isAltKeyDown();		
		
		if (moveMode(mode)) {
			GeoElement geo = view.getLabelHit(mouseLoc);
			if (geo != null) {				
				//Application.debug("hop");
				noHighlighting = true;
				tempArrayList.clear();
				tempArrayList.add(geo);
				hits = tempArrayList;				
			}
		}
		else if (mode == EuclidianView.MODE_POINT || mode == EuclidianView.MODE_POINT_IN_REGION) {
			// include polygons in hits
			view.setHits(mouseLoc);
			hits = view.getHits();
		}
		if (hits.isEmpty()){
			view.setHits(mouseLoc);
			hits = view.getHits();switchModeForRemovePolygons(hits);

		}
		if (hits.isEmpty()) {
			//AG Not implemented yet view.setToolTipText(null);
			//AG Not implemented yet view.setDefaultCursor();	
		}	
		//AGelse
			//AG not implemented yet view.setHitCursor();
		
		repaintNeeded = noHighlighting ?  refreshHighlighting(null) : refreshHighlighting(hits) 
				|| repaintNeeded;
		
		// set tool tip text
		// the tooltips are only shown if algebra view is visible
		//if (app.isUsingLayout() && app.getGuiManager().showAlgebraView()) {
			//hits = view.getTopHits(hits);
			/*AG isn't implemented yet hits = hits.getTopHits();
			if (!hits.isEmpty()) {
				String text = GeoElement.getToolTipDescriptionHTML(hits,
						true, true);				
				view.setToolTipText(text);
			} else
				view.setToolTipText(null);*/
		// update previewable
		if (view.getPreviewDrawable() != null) {			
			view.updatePreviewable();
			repaintNeeded = true;
		}
		
		// show Mouse coordinates
		if (view.getShowMouseCoords()) {
			transformCoords();
			repaintNeeded = true;
		}		

		if (repaintNeeded) {
			kernel.notifyRepaint();
		}

		
	}
	
	final boolean refreshHighlighting(Hits hits) {
		boolean repaintNeeded = false;

		//	clear old highlighting
		if (highlightedGeos.size() > 0) {
			setHighlightedGeos(false);
			repaintNeeded = true;
		}

		// find new objects to highlight
		highlightedGeos.clear();	
		selectionPreview = true; // only preview selection, see also
		// mouseReleased()
		processMode(hits, null); // build highlightedGeos List
		selectionPreview = false; // reactivate selection in mouseReleased()

		// set highlighted objects
		if (highlightedGeos.size() > 0) {
			setHighlightedGeos(true); 
			repaintNeeded = true;
		}		
		return repaintNeeded;
	}
	
	protected boolean move(Hits hits) {		
		addSelectedGeo(hits.getMoveableHits(), 1, false);	
		return false;
	}
	
	// dummy function for highlighting:
	// used only in preview mode, see mouseMoved() and selectionPreview
	final protected boolean point(Hits hits) {
//AG		addSelectedGeo(hits.getHits(Path.class, tempArrayList), 1, false);
		return false;
	}
	
	final protected int addSelectedGeo(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedGeos,GeoElement.GEO_CLASS_ALL);
	}		
	
	protected int handleAddSelected(Hits hits, int max, boolean addMore, ArrayList list, int geoid) {	
		
		
		if (selectionPreview)
			return addToHighlightedList(list, hits.getHits(geoid, handleAddSelectedArrayList) , max);
		else
			return addToSelectionList(list, hits.getHits(geoid, handleAddSelectedArrayList), max, addMore);
	}
	protected Hits handleAddSelectedArrayList = new Hits();

	// selectionList may only contain max objects
	// a choose dialog will be shown if not all objects can be added
	// @param addMoreThanOneAllowed: it's possible to add several objects
	// without choosing
	final protected int addToSelectionList(ArrayList selectionList,
			ArrayList geos, int max, boolean addMoreThanOneAllowed) {
		
		if (geos == null)
			return 0;
		//GeoElement geo;

		// ONLY ONE ELEMENT
		if (geos.size() == 1)
			return addToSelectionList(selectionList, (GeoElement) geos.get(0), max);

		//	SEVERAL ELEMENTS
		// here nothing should be removed
		//  too many objects -> choose one
		if (!addMoreThanOneAllowed || geos.size() + selectionList.size() > max){
			return addToSelectionList(selectionList, chooseGeo(geos, true), max);
		}

		// already selected objects -> choose one
		boolean contained = false;
		for (int i = 0; i < geos.size(); i++) {
			if (selectionList.contains(geos.get(i)))
				contained = true;
		}
		if (contained)
			return addToSelectionList(selectionList, chooseGeo(geos, true), max);

		// add all objects to list
		int count = 0;
		for (int i = 0; i < geos.size(); i++) {
			count += addToSelectionList(selectionList, (GeoElement) geos.get(i), max);
		}
		return count;
	}

	//	selectionList may only contain max objects
	// an already selected objects is deselected
	final protected int addToSelectionList(ArrayList selectionList,
			GeoElement geo, int max) {
		if (geo == null)
			return 0;

		int ret = 0;
		if (selectionList.contains(geo)) { // remove from selection
			selectionList.remove(geo);
			if (selectionList != selectedGeos)
				selectedGeos.remove(geo);
			ret =  -1;
		} else { // new element: add to selection
			if (selectionList.size() < max) {
				selectionList.add(geo);
				if (selectionList != selectedGeos)
					selectedGeos.add(geo);
				ret = 1;
			} 
		}
		if (ret != 0) app.toggleSelectedGeo(geo);
		return ret;
	}
	
	
	// selectionList may only contain max objects
	final protected int addToHighlightedList(ArrayList selectionList,
			ArrayList geos, int max) {
		if (geos == null)
			return 0;

		Object geo;
		int ret = 0;
		for (int i = 0; i < geos.size(); i++) {
			geo = geos.get(i);
			if (selectionList.contains(geo)) {
				ret = (ret == 1) ? 1 : -1;
			} else {
				if (selectionList.size() < max) {
					highlightedGeos.add(geo); // add hit
					ret = 1;
				}
			}
		}
		//Application.printStacktrace("addtohighlightedlist");
		return ret;
	}
	final protected int addSelectedPoint(Hits hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedPoints, GeoPoint.GEO_CLASS_POINT);
		//ggb3D 2009-06-26 //return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedPoints, GeoPoint.class);
	}
	
	// get two points and create line through them
	final protected boolean join(Hits hits) {
		if (hits.isEmpty())
			return false;

		// points needed
		addSelectedPoint(hits, 2, false);
		//Application.debug("addSelectedPoint : "+hits+"\nselectedPoints = "+selectedPoints);
		if (selPoints() == 2) {
			// fetch the two selected points
			join();

			return true;
		}
		return false;
	}
	
	protected final int selPoints() {
		return selectedPoints.size();
	}

	// fetch the two selected points
	protected void join(){
		GeoPoint[] points = getSelectedPoints();
		GeoLine line = kernel.Line(null, points[0], points[1]);
	}
	
	final protected GeoPoint[] getSelectedPoints() {		

		GeoPoint[] ret = new GeoPoint[selectedPoints.size()];
		getSelectedPointsInterface(ret);

		return ret;

	}
	
	final protected void getSelectedPointsInterface(GeoPointInterface[] result) {	

		for (int i = 0; i < selectedPoints.size(); i++) {		
			result[i] = (GeoPointInterface) selectedPoints.get(i);
		}
		clearSelection(selectedPoints);

	}
	
	final protected void clearSelection(ArrayList selectionList) {
		// unselect
		selectionList.clear();
		selectedGeos.clear();
		app.clearSelectedGeos();	
		view.repaintEuclidianView();
	}
	
	
protected boolean switchModeForProcessMode(Hits hits, MouseEvent e){
		
		boolean changedKernel = false;
		
		switch (mode) {
		case EuclidianView.MODE_VISUAL_STYLE:
		case EuclidianView.MODE_MOVE:
			// move() is for highlighting and selecting
			if (selectionPreview) {		
				move(hits.getTopHits());				
			} else {
				if (DRAGGING_OCCURED && app.selectedGeosSize() == 1)
					app.clearSelectedGeos();

			}
			break;			

		case EuclidianView.MODE_MOVE_ROTATE:
			// moveRotate() is a dummy function for highlighting only
			if (selectionPreview) {				
		//AG		moveRotate(hits.getTopHits());
			}
			break;

		case EuclidianView.MODE_RECORD_TO_SPREADSHEET:
			//if (selectionPreview) 
		{
			//AGchangedKernel = record(hits.getTopHits(), e);
		}
		break;

		case EuclidianView.MODE_POINT:
		case EuclidianView.MODE_POINT_IN_REGION:
			// point() is dummy function for highlighting only
			if (selectionPreview) {
				if (mode==EuclidianView.MODE_POINT)
					hits.keepOnlyHitsForNewPointMode();
				point(hits);
			}
			break;

			// copy geo to algebra input
		case EuclidianView.MODE_SELECTION_LISTENER:
			//AGboolean addToSelection = e != null && (Application.isControlDown(e));
			//AGgeoElementSelected(hits.getTopHits(), addToSelection);
			break;

			// new line through two points
		case EuclidianView.MODE_JOIN:
			changedKernel = join(hits);
			break;

			// new segment through two points
		case EuclidianView.MODE_SEGMENT:
			//AGchangedKernel = segment(hits);
			break;

			// segment for point and number
		case EuclidianView.MODE_SEGMENT_FIXED:
			//AGchangedKernel = segmentFixed(hits);
			break;

			//	angle for two points and number
		case EuclidianView.MODE_ANGLE_FIXED:
			//AGchangedKernel = angleFixed(hits);
			break;

		case EuclidianView.MODE_MIDPOINT:
			//AGchangedKernel = midpoint(hits);
			break;

			// new ray through two points or point and vector
		case EuclidianView.MODE_RAY:
			//AGchangedKernel = ray(hits);
			break;

			// new polygon through points
		case EuclidianView.MODE_POLYGON:
			//AGchangedKernel = polygon(hits);
			break;

			// new vector between two points
		case EuclidianView.MODE_VECTOR:
			//AGchangedKernel = vector(hits);
			break;

			// intersect two objects
		case EuclidianView.MODE_INTERSECT:
			//AGchangedKernel = intersect(hits);
			break;

			// new line through point with direction of vector or line
		case EuclidianView.MODE_PARALLEL:
			//AGchangedKernel = parallel(hits);
			break;

			// Michael Borcherds 2008-04-08
		case EuclidianView.MODE_PARABOLA:
			//AGchangedKernel = parabola(hits);
			break;

			// new line through point orthogonal to vector or line
		case EuclidianView.MODE_ORTHOGONAL:
			//AGchangedKernel = orthogonal(hits);
			break;

			// new line bisector
		case EuclidianView.MODE_LINE_BISECTOR:
			//AGchangedKernel = lineBisector(hits);
			break;

			// new angular bisector
		case EuclidianView.MODE_ANGULAR_BISECTOR:
			//AGchangedKernel = angularBisector(hits);
			break;

			// new circle (2 points)
		case EuclidianView.MODE_CIRCLE_TWO_POINTS:
			// new semicircle (2 points)
		case EuclidianView.MODE_SEMICIRCLE:
			//AGchangedKernel = circleOrSphere2(hits, mode);
			break;

		case EuclidianView.MODE_LOCUS:
			//AGchangedKernel = locus(hits);
			break;

			// new circle (3 points)
		case EuclidianView.MODE_CIRCLE_THREE_POINTS:
		case EuclidianView.MODE_ELLIPSE_THREE_POINTS:
		case EuclidianView.MODE_HYPERBOLA_THREE_POINTS:
		case EuclidianView.MODE_CIRCLE_ARC_THREE_POINTS:
		case EuclidianView.MODE_CIRCLE_SECTOR_THREE_POINTS:
		case EuclidianView.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
		case EuclidianView.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
			//AGchangedKernel = threePoints(hits, mode);
			break;

			// new conic (5 points)
		case EuclidianView.MODE_CONIC_FIVE_POINTS:
			//AGchangedKernel = conic5(hits);
			break;

			// relation query
		case EuclidianView.MODE_RELATION:
			//AGrelation(hits.getTopHits());			
			break;

			// new tangents
		case EuclidianView.MODE_TANGENTS:
			//AGchangedKernel = tangents(hits.getTopHits());
			break;

		case EuclidianView.MODE_POLAR_DIAMETER:
			//AGchangedKernel = polarLine(hits.getTopHits());
			break;

			// delete selected object
		case EuclidianView.MODE_DELETE:
			//AGchangedKernel = delete(hits.getTopHits());
			break;

		case EuclidianView.MODE_SHOW_HIDE_OBJECT:
			//AGif (showHideObject(hits.getTopHits()))
				//AGtoggleModeChangedKernel = true;
			break;

		case EuclidianView.MODE_SHOW_HIDE_LABEL:
			//AGif (showHideLabel(hits.getTopHits()))
				//AGtoggleModeChangedKernel = true;
			break;

		case EuclidianView.MODE_COPY_VISUAL_STYLE:
			//AGif (copyVisualStyle(hits.getTopHits()))
				//AGtoggleModeChangedKernel = true;
			break;

			//  new text or image
		case EuclidianView.MODE_TEXT:
		case EuclidianView.MODE_IMAGE:
			//AGchangedKernel = textImage(hits.getOtherHits(GeoImage.class, tempArrayList), mode, altDown); //e.isAltDown());
			break;

			// new slider
		case EuclidianView.MODE_SLIDER:
			//AGchangedKernel = slider();
			break;			

		case EuclidianView.MODE_MIRROR_AT_POINT:
			//AGchangedKernel = mirrorAtPoint(hits.getTopHits());
			break;

		case EuclidianView.MODE_MIRROR_AT_LINE:
			//AGchangedKernel = mirrorAtLine(hits.getTopHits());
			break;

		case EuclidianView.MODE_MIRROR_AT_CIRCLE: // Michael Borcherds 2008-03-23
			//AGchangedKernel = mirrorAtCircle(hits.getTopHits());
			break;

		case EuclidianView.MODE_TRANSLATE_BY_VECTOR:
			//AGchangedKernel = translateByVector(hits.getTopHits());
			break;

		case EuclidianView.MODE_ROTATE_BY_ANGLE:
			//AGchangedKernel = rotateByAngle(hits.getTopHits());
			break;

		case EuclidianView.MODE_DILATE_FROM_POINT:
			//AGchangedKernel = dilateFromPoint(hits.getTopHits());
			break;

		case EuclidianView.MODE_FITLINE:
			//AGchangedKernel = fitLine(hits);
			break;

		case EuclidianView.MODE_CIRCLE_POINT_RADIUS:
			//AGchangedKernel = circleOrSpherePointRadius(hits);
			break;				

		case EuclidianView.MODE_ANGLE:
			//AGchangedKernel = angle(hits.getTopHits());
			break;

		case EuclidianView.MODE_VECTOR_FROM_POINT:
			//AGchangedKernel = vectorFromPoint(hits);
			break;

		case EuclidianView.MODE_DISTANCE:
			//AGchangedKernel = distance(hits, e);
			break;	

		case EuclidianView.MODE_MACRO:			
			//AGchangedKernel = macro(hits);
			break;

		case EuclidianView.MODE_AREA:
			//AGchangedKernel = area(hits, e);
			break;	

		case EuclidianView.MODE_SLOPE:
			//AGchangedKernel = slope(hits);
			break;

		case EuclidianView.MODE_REGULAR_POLYGON:
			//AGchangedKernel = regularPolygon(hits);
			break;

		case EuclidianView.MODE_SHOW_HIDE_CHECKBOX:
			//AGchangedKernel = showCheckBox(hits);
			break;

		case EuclidianView.MODE_BUTTON_ACTION:
			//AGchangedKernel = button(false);
			break;

		case EuclidianView.MODE_TEXTFIELD_ACTION:
			//AGchangedKernel = button(true);
			break;

		case EuclidianView.MODE_PEN:
			//AGchangedKernel = pen();
			break;

			// Michael Borcherds 2008-03-13	
		case EuclidianView.MODE_COMPASSES:
			//AGchangedKernel = compasses(hits);
			break;

		default:
			// do nothing
		}
		
		return changedKernel;

	}
	
	final boolean processMode(Hits hits, MouseEvent e) {
		boolean changedKernel = false;

		if (hits==null)
			hits = new Hits();

		changedKernel = switchModeForProcessMode(hits, e);

		// update preview
		if (view.getPreviewDrawable() != null) {
			view.getPreviewDrawable().updatePreview();
			if (mouseLoc != null)
				view.getPreviewDrawable().updateMousePos(mouseLoc.x, mouseLoc.y);			
			view.repaintEuclidianView();
		}

		return changedKernel;
	}

	
	/**
	 * for some modes, polygons are not to be removed
	 * @param hits
	 */
	protected void switchModeForRemovePolygons(Hits hits){
		hits.removePolygons();
	}

	
	@Override
	public void onMouseOut(MouseOutEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseWheel(MouseWheelEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	public void setLineEndPoint(Point2D.Double point) {
		lineEndPoint = point;
		useLineEndPoint = true;
	}
	
	private boolean moveMode(int mode) {
		if (mode == EuclidianView.MODE_MOVE ||
				mode == EuclidianView.MODE_VISUAL_STYLE)
			return true;
		
		else return false;
	}
	
	/*public void setMode(int newMode) {
		endOfMode(mode);

		if (EuclidianView.usesSelectionRectangleAsInput(newMode))
		{
			initNewMode(newMode);
			processSelectionRectangle(null);			
		}
		else
		{
			if (!TEMPORARY_MODE) app.clearSelectedGeos(false);
			initNewMode(newMode);			
		}

		kernel.notifyRepaint();
	}
	
	protected void initNewMode(int mode) {
		this.mode = mode;
		initShowMouseCoords();
		// Michael Borcherds 2007-10-12
		//clearSelections();
		if (!TEMPORARY_MODE) clearSelections();
		//		 Michael Borcherds 2007-10-12
		moveMode = MOVE_NONE;
		
		closeMiniPropertiesPanel();

		Previewable previewDrawable = null;
		// init preview drawables
		switch (mode) {
		
		case EuclidianView.MODE_PEN:
		case EuclidianView.MODE_VISUAL_STYLE:
			
			openMiniPropertiesPanel();
			
			break;

		case EuclidianView.MODE_JOIN: // line through two points
			useLineEndPoint = false;
			previewDrawable = view.createPreviewLine(selectedPoints);
			break;

		case EuclidianView.MODE_SEGMENT:
			useLineEndPoint = false;
			previewDrawable = view.createPreviewSegment(selectedPoints);
			break;

		case EuclidianView.MODE_RAY:
			useLineEndPoint = false;
			previewDrawable = view.createPreviewRay(selectedPoints);
			break;

		case EuclidianView.MODE_VECTOR:
			useLineEndPoint = false;
			previewDrawable = view.createPreviewVector(selectedPoints);
			break;

		case EuclidianView.MODE_POLYGON:
			previewDrawable = view.createPreviewPolygon(selectedPoints);
			break;

		case EuclidianView.MODE_CIRCLE_TWO_POINTS:
		case EuclidianView.MODE_CIRCLE_THREE_POINTS:
		case EuclidianView.MODE_ELLIPSE_THREE_POINTS:
		case EuclidianView.MODE_HYPERBOLA_THREE_POINTS:		
			previewDrawable = new DrawConic((EuclidianView) view, mode, selectedPoints);
			break;

			// preview for compass: radius first
		case EuclidianView.MODE_COMPASSES:
			previewDrawable = new DrawConic((EuclidianView) view, mode, selectedPoints, selectedSegments, selectedConics);
			break;

			// preview for arcs and sectors
		case EuclidianView.MODE_SEMICIRCLE:
		case EuclidianView.MODE_CIRCLE_ARC_THREE_POINTS:
		case EuclidianView.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
		case EuclidianView.MODE_CIRCLE_SECTOR_THREE_POINTS:
		case EuclidianView.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
			previewDrawable = new DrawConicPart((EuclidianView) view, mode, selectedPoints);
			break;										

		case EuclidianView.MODE_SHOW_HIDE_OBJECT:
			// select all hidden objects			
			Iterator it = kernel.getConstruction().getGeoSetConstructionOrder().iterator();
			while (it.hasNext()) {
				GeoElement geo = (GeoElement) it.next();
				// independent numbers should not be set visible
				// as this would produce a slider
				if (!geo.isSetEuclidianVisible() && 
						!(
								(geo.isNumberValue() || geo.isBooleanValue()) && geo.isIndependent())
				) 
				{
					app.addSelectedGeo(geo);
					geo.setEuclidianVisible(true);					
					geo.updateRepaint();										
				}
			}	
			break;

		case EuclidianView.MODE_COPY_VISUAL_STYLE:
			movedGeoElement = null; // this will be the active geo template
			break;

		case EuclidianView.MODE_MOVE_ROTATE:		
			rotationCenter = null; // this will be the active geo template
			break;

		case EuclidianView.MODE_RECORD_TO_SPREADSHEET:		
			recordObject = null; 
			break;

		default:
			previewDrawable = null;

			// macro mode?
			if (mode >= EuclidianView.MACRO_MODE_ID_OFFSET) {
				// get ID of macro
				int macroID = mode - EuclidianView.MACRO_MODE_ID_OFFSET;
				macro = kernel.getMacro(macroID);
				macroInput = macro.getInputTypes();
				this.mode = EuclidianView.MODE_MACRO;								
			}		
			break;
		}

		view.setPreview(previewDrawable);	
		toggleModeChangedKernel = false;
	}	
	
	protected void endOfMode(int mode) {
		switch (mode) {
		case EuclidianView.MODE_SHOW_HIDE_OBJECT:				
			// take all selected objects and hide them
			Collection coll = 	app.getSelectedGeos();				
			Iterator it = coll.iterator();
			while (it.hasNext()) {
				GeoElement geo = (GeoElement) it.next();					
				geo.setEuclidianVisible(false);
				geo.updateRepaint();								
			}				
			break;
		}

		if (recordObject != null) recordObject.setSelected(false);
		recordObject = null;

		if (toggleModeChangedKernel)
			app.storeUndoInfo();
	}*/


}
