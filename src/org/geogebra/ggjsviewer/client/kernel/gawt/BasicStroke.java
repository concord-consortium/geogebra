package org.geogebra.ggjsviewer.client.kernel.gawt;

import com.google.gwt.widgetideas.graphics.client.GWTCanvas;

public class BasicStroke {
	
	//Implement similar to the original java class, but works in gwtcanvas
	
	
	public static final String CAP_BUTT = GWTCanvas.BUTT;
	public static final String JOIN_MITER = GWTCanvas.MITER;
	private float lineWidth;
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
		return lineCap;
	}

	public void setLineJoin(String lineJoin) {
		this.lineJoin = lineJoin;
	}

	public String getLineJoin() {
		return lineJoin;
	}
	
	
	
}
