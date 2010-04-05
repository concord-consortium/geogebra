package org.geogebra.ggjsviewer.client.kernel;

import org.geogebra.ggjsviewer.client.kernel.arithmetic.NumberValue;

public interface Dilateable {
	public void dilate(NumberValue r, GeoPoint S);
	public GeoElement toGeoElement();
}
