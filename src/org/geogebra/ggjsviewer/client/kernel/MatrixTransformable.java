package org.geogebra.ggjsviewer.client.kernel;


public interface MatrixTransformable {
	
	public void matrixTransform(GeoList matrix);
	public GeoElement toGeoElement();

}
