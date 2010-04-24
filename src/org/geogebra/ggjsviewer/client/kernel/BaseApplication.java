package org.geogebra.ggjsviewer.client.kernel;

public class BaseApplication {

	public String getPlain(String string) {
		// TODO Auto-generated method stub
		return "TODOBASEAPP";
	}

	public static void debug(String string) {
		// TODO Auto-generated method stub
		
	}

	public void setUnsaved() {
		// TODO Auto-generated method stub
		
	}

	public static void printStacktrace(String string) {
		// TODO Auto-generated method stub
		
	}

	public Object getCommand(String cmdname) {
		// TODO Auto-generated method stub
		return cmdname+"TodoAG";
	}

	public boolean letRedefine() {
		// TODO Auto-generated method stub
		return false;
	}

	public Object getKernel() {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeSelectedGeo(GeoElement geoElement, boolean b) {
		// TODO Auto-generated method stub
		
	}

	public boolean isReverseNameDescriptionLanguage() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isRightToLeftReadingOrder() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasGuiManager() {
		// TODO Auto-generated method stub
		return false;
	}

	public int getLabelingStyle() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getPlain(String string, String label, String label2) {
		// TODO Auto-generated method stub
		return "Line through: "+string+" Point 1: "+label+"Point 2: "+label2+" must be implemented AlgoJoinPoints";
	}

	public String getPlain(String string, String label, String label2,
			String nameDescription) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPlain(String string, String label) {
		// TODO Auto-generated method stub
		return null;
	}

}
