package org.geogebra.ggjsviewer.client.kernel;

import org.geogebra.ggjsviewer.client.kernel.gawt.Color;

public class EuclidianViewConstants {
	protected static final long serialVersionUID = 1L;

	protected static final int MIN_WIDTH = 50;
	protected static final int MIN_HEIGHT = 50;
	
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

	public static final float SELECTION_ADD = 2.0f;

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


}
