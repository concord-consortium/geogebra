package geogebra.geogebramobile.client.kernel.kernelND;

import geogebra.geogebramobile.client.kernel.CircularDefinitionException;

/**
 * Simple common interface for GeoVector and GeoVector3D
 * 
 * @author ggb3D
 *
 */
public interface GeoVectorND {

	void setLabel(String label);

	void setStartPoint(GeoPointND p) throws CircularDefinitionException;

	GeoPointND getStartPoint();

	void setUndefined();
	
	public void setCoords(double[] c);

}
