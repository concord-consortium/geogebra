package org.geogebra.ggjsviewer.client.kernel;

import org.geogebra.ggjsviewer.client.kernel.arithmetic.NumberValue;


public interface PointRotateable extends Rotateable {
	public void rotate(NumberValue r, GeoPoint S);
}
