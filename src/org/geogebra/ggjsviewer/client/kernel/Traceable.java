package org.geogebra.ggjsviewer.client.kernel;

/**
 * @author Markus Hohenwarter
 */
public interface Traceable {
	
	public boolean getTrace();
	public void setTrace(boolean flag);
	public void updateRepaint();

}
