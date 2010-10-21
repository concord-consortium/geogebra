package geogebra.geogebramobile.client.kernel;

import geogebra.geogebramobile.client.kernel.arithmetic.NumberValue;

public interface Dilateable {
	public void dilate(NumberValue r, GeoPoint S);
	public GeoElement toGeoElement();
}
