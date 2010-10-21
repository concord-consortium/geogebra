package geogebra.geogebramobile.client.kernel;

import geogebra.geogebramobile.client.kernel.arithmetic.NumberValue;

public interface Rotateable {
	public void rotate(NumberValue r);
	public GeoElement toGeoElement();
}

