package geogebra.geogebramobile.client.plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import geogebra.geogebramobile.client.euclidian.EuclidianView;
import geogebra.geogebramobile.client.kernel.Construction;
import geogebra.geogebramobile.client.kernel.GeoElement;
import geogebra.geogebramobile.client.kernel.GeoPoint;
import geogebra.geogebramobile.client.kernel.Kernel;
import geogebra.geogebramobile.client.kernel.PointProperties;
import geogebra.geogebramobile.client.kernel.Traceable;
import geogebra.geogebramobile.client.kernel.commands.AlgebraProcessor;
import geogebra.geogebramobile.client.kernel.gawt.Color;
import geogebra.geogebramobile.client.main.Application;

public class GgbAPI {
	
	  private Application         app=                null;   //References ...
	  private Kernel              kernel=             null;
	  private Construction        construction=       null;
	  private AlgebraProcessor    algebraprocessor=   null;

	public GgbAPI(Application application) {
		app = application;
		kernel=app.getKernel();
        algebraprocessor=kernel.getAlgebraProcessor();
        construction=kernel.getConstruction();
		// TODO Auto-generated constructor stub
	}
	
	private String [] objNames;
	public int lastGeoElementsIteratorSize = 0;		//ulven 29.05.08: Had to change to public, used by applet
	
	/**
	 * 
	 * @return
	 */
	public String [] getObjNames() {			//ulven 29.05.08: Had to change to public, used by applet

		Construction cons = kernel.getConstruction();
		TreeSet geoSet =  cons.getGeoSetConstructionOrder();
		int size = geoSet.size();
		
		/* removed Michael Borcherds 2009-02-09
		 * BUG!
		 *
		// don't build objNames if nothing changed
		if (size == lastGeoElementsIteratorSize)
			return objNames;		
			*/
		
		// build objNames array
		lastGeoElementsIteratorSize = size;		
		objNames = new String[size];
				
		int i=0; 
		Iterator it = geoSet.iterator();
		while (it.hasNext()) {
			GeoElement geo = (GeoElement) it.next();
			objNames[i] = geo.getLabel();
			i++;
		}
		return objNames;
		
	}
	
	/**
	 * Returns an array with all object names.
	 */
	public synchronized String [] getAllObjectNames() {			
		return getObjNames();
	}	
	
	/**
	 * Returns an array with the names of all selected objects.
	 */
	public synchronized String [] getSelectedObjectNames() {			
		ArrayList selGeos = app.getSelectedGeos();
		String [] objNames = new String[selGeos.size()];
		
		for (int i=0; i < selGeos.size(); i++) {
			GeoElement geo = (GeoElement) selGeos.get(i);
			objNames[i] = geo.getLabel();
		}
		return objNames;
	}	
	
	/**
	 * Returns the number of objects in the construction.
	 */
	public synchronized int getObjectNumber() {					
		return getObjNames().length;			
	}	
	
	/**
	 * Returns the name of the n-th object of this construction.
	 */
	public synchronized String getObjectName(int i) {					
		String [] names = getObjNames();
					
		try {
			return names[i];
		} catch (Exception e) {
			return "";
		}
	}
	
	

	/**
	 * Evaluates the given string as if it was entered into GeoGebra's 
	 * input text field. 	 
	 */
	public synchronized boolean evalCommand(String cmdString) {
		
		//Application.debug("evalCommand called..."+cmdString);
		GeoElement [] result;
		
		if (cmdString.indexOf('\n') == -1) {
			result = kernel.getAlgebraProcessor().processAlgebraCommand(cmdString, false);
			// return success
			return result != null;
			
		}

		boolean ret = true;
		String[] cmdStrings = cmdString.split("[\\n]+");
		for (int i = 0 ; i < cmdStrings.length ; i++) {
			result = kernel.getAlgebraProcessor().processAlgebraCommand(cmdStrings[i], false);
			ret = ret & (result != null);
		}
		
		return ret;
	}
	
	/**
	 * Returns current construction in XML format. May be used for saving.
	 */
	public String getXML() {
		return app.getXML();
	}
	
	/**
	 * Returns the GeoGebra XML string for the given GeoElement object, 
	 * i.e. only the <element> tag is returned. 
	 */
	public synchronized String getXML(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) 
			return "";	
		else {
			//if (geo.isIndependent()) removed as we want a way to get the <element> tag for all objects
				return geo.getXML();
			//else
			//	return "";
		}
	}
	
	/**
	 * For a dependent GeoElement objName the XML string of 
	 * the parent algorithm and all its output objects is returned. 
	 * For a free GeoElement objName "" is returned.
	 */
	public synchronized String getAlgorithmXML(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) 
			return "";	
		else {
			if (geo.isIndependent())
				return "";
			else
				return geo.getParentAlgorithm().getXML();
		}
	}	
	

	/**
	 * Sets the fixed state of the object with the given name.
	 */
	public synchronized void setFixed(String objName, boolean flag) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo != null && geo.isFixable()) {		
			geo.setFixed(flag);
			geo.updateRepaint();
		}
	}
	
	/**
	 * Shows or hides the object with the given name in the geometry window.
	 */
	public synchronized void setVisible(String objName, boolean visible) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;		
		geo.setEuclidianVisible(visible);
		geo.updateRepaint();
	}
	
	/**
	 * Shows or hides the object with the given name in the geometry window.
	 */
	public synchronized boolean getVisible(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return false;		
		return (geo.isEuclidianVisible());
	}
	
	/**
	 * Sets the layer of the object with the given name in the geometry window.
	 * Michael Borcherds 2008-02-27
	 */
	public synchronized void setLayer(String objName, int layer) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;		
		geo.setLayer(layer);		
		geo.updateRepaint();
	}
	
	/**
	 * Returns the layer of the object with the given name in the geometry window.
	 * returns layer, or -1 if object doesn't exist
	 * Michael Borcherds 2008-02-27
	 */
	public synchronized int getLayer(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return -1;		
		return geo.getLayer();		
	}
	
	/**
	 * Shows or hides a complete layer
	 * Michael Borcherds 2008-02-27
	 */
	public synchronized void setLayerVisible(int layer, boolean visible) {
		if (layer<0 || layer > EuclidianView.MAX_LAYERS) return;
		String [] names = getObjNames();
		for (int i=0 ; i < names.length ; i++)
		{
			GeoElement geo = kernel.lookupLabel(names[i]);
			if (geo != null) if (geo.getLayer() == layer)
			{
				geo.setEuclidianVisible(visible);		
				geo.updateRepaint();
			}
		}	
	}
	
	/**
	 * Turns the trace of the object with the given name on or off.
	 */
	public synchronized void setTrace(String objName, boolean flag) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo != null && geo.isTraceable()) {		
			((Traceable)geo).setTrace(flag);
			geo.updateRepaint();
		}
	}
	
	/**
	 * Shows or hides the label of the object with the given name in the geometry window.
	 */
	public synchronized void setLabelVisible(String objName, boolean visible) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;		
		geo.setLabelVisible(visible);		
		geo.updateRepaint();
	}
	
	/**
	 * Sets the label style of the object with the given name in the geometry window.
	 * Possible label styles are NAME = 0, NAME_VALUE = 1 and VALUE = 2.
	 */
	public synchronized void setLabelStyle(String objName, int style) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;		
		geo.setLabelMode(style);		
		geo.updateRepaint();
	}
	
	/**
	 * Shows or hides the label of the object with the given name in the geometry window.
	 */
	public synchronized void setLabelMode(String objName, boolean visible) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;		
		geo.setLabelVisible(visible);
		geo.updateRepaint();
	}
	
	/**
	 * Sets the color of the object with the given name.
	 */
	public synchronized void setColor(String objName, int red, int green, int blue) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;		
		Color col = new Color(red, green, blue);		
		geo.setObjColor(col);
		geo.updateRepaint();
	}	
	
	/**
	 * Starts/stops an object animating
	 */
	public void setAnimating(String objName, boolean animate) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo != null) 
			geo.setAnimating(animate);					
	}
	
	/**
	 * Sets the animation speed of an object
	 */
	public void setAnimationSpeed(String objName, double speed) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo != null) {
			geo.setAnimationSpeed(speed);
		}
	}
	

	
	/**
	 * Returns the color of the object as an hex string. Note that the hex-string 
	 * starts with # and uses upper case letters, e.g. "#FF0000" for red.
	 */
	public synchronized String getColor(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return "";		
		return "#" + geogebra.geogebramobile.client.util.Util.toHexString(geo.getObjectColor());		
	}	
	
	public synchronized int getLineThickness(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return -1;		
		return geo.getLineThickness();		
	}	
	
	public synchronized void setLineThickness(String objName, int thickness) {
		if (thickness == -1) thickness = EuclidianView.DEFAULT_LINE_THICKNESS;
		if (thickness < 1 || thickness > 13) return;
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;		
		geo.setLineThickness(thickness);		
		geo.updateRepaint();
	}	
	
	public synchronized int getPointStyle(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return -1;		
		if (geo.isGeoPoint())
			return ((GeoPoint) geo).getPointStyle();	
		else
			return -1;
	}	
	
	public synchronized void setPointStyle(String objName, int style) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;	
		if (geo instanceof PointProperties) {
			((PointProperties) geo).setPointStyle(style);		
			geo.updateRepaint();
		}
	}	
	
	public synchronized int getPointSize(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return -1;		
		if (geo.isGeoPoint())
			return ((GeoPoint) geo).getPointSize();	
		else
			return -1;
	}	
	
	public synchronized void setPointSize(String objName, int style) {
		if (style < 1 || style > 9) return;
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;	
		if (geo.isGeoPoint()) {
			((GeoPoint) geo).setPointSize(style);
			geo.updateRepaint();
		}
	}	
	
	public synchronized double getFilling(String objName) {
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return -1;		
		return geo.getAlphaValue();		
	}	
	
	public synchronized void setFilling(String objName, double filling) {
		if (filling < 0.0 || filling > 1.0)
			return;
		
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;		

		geo.setAlphaValue((float)filling);
		geo.updateRepaint();
	}	
	
	
	public synchronized void setLineStyle(String objName, int style) {
		Integer[] types = EuclidianView.getLineTypes();
		
		if (style < 0 || style >= types.length)
			return;
		
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;		
		
		geo.setLineType(types[style].intValue());
		geo.updateRepaint();
	}	
	
	/**
	 * Deletes the object with the given name.
	 */
	public synchronized void deleteObject(String objName) {			
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) return;		
		geo.remove();
		kernel.notifyRepaint();
	}	
	
	/**
	 * Renames an object from oldName to newName.
	 * @return whether renaming worked
	 */
	public synchronized boolean renameObject(String oldName, String newName) {		
		GeoElement geo = kernel.lookupLabel(oldName);
		if (geo == null) 
			return false;
		
		// try to rename
		boolean success = geo.rename(newName);
		kernel.notifyRepaint();
		
		return success;
	}	
	
	/**
	 * Returns true if the object with the given name exists.
	 */
	public synchronized boolean exists(String objName) {			
		GeoElement geo = kernel.lookupLabel(objName);
		return (geo != null);				
	}	
	
	/**
	 * Returns true if the object with the given name has a vaild
	 * value at the moment.
	 */
	public synchronized boolean isDefined(String objName) {			
		GeoElement geo = kernel.lookupLabel(objName);
		if (geo == null) 
			return false;
		else
			return geo.isDefined();
	}	

}
