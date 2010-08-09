package org.geogebra.ggjsviewer.client.kernel.gawt;

import com.google.gwt.widgetideas.graphics.client.GWTCanvas;

public class BasicStroke {
	
	//Implement similar to the original java class, but works in gwtcanvas
	
	
	public static final String CAP_BUTT = GWTCanvas.BUTT;
	public static final String CAP_ROUND = GWTCanvas.ROUND;
	public static final String CAP_SQUARE = GWTCanvas.SQUARE;
	
	public static final String JOIN_MITER = GWTCanvas.MITER;
	public static final String JOIN_ROUND = GWTCanvas.ROUND;
	public static final String JOIN_BEVEL = GWTCanvas.BEVEL;
	public static final Integer CAP_BUTT_INT = 0;
	public static final Integer CAP_ROUND_INT =1;
	public static final Integer CAP_SQUARE_INT = 2;
	
	private float lineWidth  = 1;
	private int miterLimit;
	private String lineCap;
	private String lineJoin;
	
	
	public BasicStroke() {
		setLineWidth(1);
		setLineCap(GWTCanvas.BUTT);
		setLineJoin(GWTCanvas.MITER);
	}
	
	public BasicStroke(float width) {
		setLineWidth(width);
		setLineCap(GWTCanvas.BUTT);
		setLineJoin(GWTCanvas.MITER);
	}
	
	public BasicStroke(float width, String lineCap) {
		setLineWidth(width);
		setLineCap(lineCap);
		setLineJoin(GWTCanvas.MITER);
	}
	
	public BasicStroke(float width, String lineCap, String lineJoin) {
		setLineWidth(width);
		setLineCap(lineCap);
		setLineJoin(lineJoin);
	}

	public BasicStroke(float width, int endCap, String lineJoin,
			Integer miterLimit2, float[] dash, float f) {
		// TODO Auto-generated constructor stub
		this.lineWidth = width;
		if (endCap == 0)
			setLineCap(BasicStroke.CAP_BUTT);
		else if (endCap == 1)
			setLineCap(BasicStroke.CAP_ROUND);
		else 
			setLineCap(BasicStroke.CAP_SQUARE);
		
	}

	public void setLineWidth(float lineWidth) {
		this.lineWidth = lineWidth;
	}

	public float getLineWidth() {
		return lineWidth;
	}

	public void setLineCap(String lineCap) {
		this.lineCap = lineCap;
	}

	public String getLineCap() {
		//return lineCap;
		return GWTCanvas.ROUND;
	}

	public void setLineJoin(String lineJoin) {
		this.lineJoin = lineJoin;
	}

	public String getLineJoin() {
		//return lineJoin;
		return GWTCanvas.ROUND;
	}

	public Shape createStrokedShape(Shape shape) {
		// TODO Auto-generated method stub
		return shape;
	}

	public Integer getEndCap() {
		// TODO Auto-generated method stub
		if (lineCap.equalsIgnoreCase(BasicStroke.CAP_BUTT))
			return 0;
		else if (lineCap.equalsIgnoreCase(BasicStroke.CAP_ROUND))
			return 1;
		else 
			return 2;
	}

	public Integer getMiterLimit() {
		// TODO Auto-generated method stub
		return miterLimit;
	}

	public void setMiterLimit(int miterLimit) {
		this.miterLimit = miterLimit;
	}
	
	
	
}
