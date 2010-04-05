package org.geogebra.ggjsviewer.client.kernel;

public interface PointProperties {
	public void setPointSize(int size);
	public int getPointSize();
	public void setPointStyle(int type);
	public int getPointStyle();
	public void updateRepaint();
}