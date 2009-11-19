package geogebra.kernel;

public class GeoTextField extends GeoButton {

	public GeoTextField(Construction c) {
		
		super(c);
	}
	protected String getClassName() {
		return "GeoTextField";
	}
	
    protected String getTypeString() {
		return "TextField";
	}
    
    public int getGeoClassType() {
    	return GEO_CLASS_TEXTFIELD;
    }
    
	public boolean isTextField() {
		return true;
	}
	


}