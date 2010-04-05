package org.geogebra.ggjsviewer.client.kernel;

import org.geogebra.ggjsviewer.client.kernel.arithmetic.NumberValue;

public interface Rotateable {
	public void rotate(NumberValue r);
	public GeoElement toGeoElement();
}

