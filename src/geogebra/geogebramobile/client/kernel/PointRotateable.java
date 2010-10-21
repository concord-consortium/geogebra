package geogebra.geogebramobile.client.kernel;

import geogebra.geogebramobile.client.kernel.arithmetic.NumberValue;


public interface PointRotateable extends Rotateable {
	public void rotate(NumberValue r, GeoPoint S);
}
