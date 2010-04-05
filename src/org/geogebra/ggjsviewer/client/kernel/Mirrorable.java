package org.geogebra.ggjsviewer.client.kernel;

public interface Mirrorable {
	public void mirror(GeoPoint Q);
	public void mirror(GeoLine g);	
	public GeoElement toGeoElement();
}
