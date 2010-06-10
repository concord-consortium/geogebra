package org.geogebra.ggjsviewer.client.main;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.geogebra.ggjsviewer.client.euclidian.EuclidianController;
import org.geogebra.ggjsviewer.client.euclidian.EuclidianView;
import org.geogebra.ggjsviewer.client.gui.Base64Form;
import org.geogebra.ggjsviewer.client.gui.GgjsViewerWrapper;
import org.geogebra.ggjsviewer.client.io.MyXMLHandler;
import org.geogebra.ggjsviewer.client.kernel.BaseApplication;
import org.geogebra.ggjsviewer.client.kernel.GeoElement;
import org.geogebra.ggjsviewer.client.kernel.GeoLine;
import org.geogebra.ggjsviewer.client.kernel.GeoPoint;
import org.geogebra.ggjsviewer.client.kernel.Kernel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;

public class Application extends BaseApplication {
	
	private Kernel kernel;
	private EuclidianView euclidianview;
	private EuclidianController euclidiancontroller;
	private MyXMLHandler xmlhandler;
	
	protected boolean showMenuBar = true;
	
	private ArrayList<GeoElement> selectedGeos = new ArrayList<GeoElement>();
	
	
	public Application() {
		super();
		initKernel();	
		initEuclidianView();
		initXmlHandler();
		
		/*Try to draw some basic objects*/
		GeoElement geo_point1 = null;
		GeoElement geo_point2 = null;
		GeoElement geo_line = null;
		String label1 = "A";
		String label2 = "B";
		
		LinkedHashMap<String, String> attributes = new LinkedHashMap<String, String>();
		attributes.put("x", "-2.0813120395521345");
		attributes.put("y", "4.8832399507533975");
		attributes.put("z", "1.0");
		geo_point1 = kernel.createGeoElement(kernel.getConstruction(), "point");
		geo_point1.setLoadedLabel(label1);
		kernel.handleCoords(geo_point1, attributes);
		attributes.clear();
		geo_point2 = kernel.createGeoElement(kernel.getConstruction(), "point");
		geo_point2.setLoadedLabel(label2);
		attributes.put("x", "6.0813120395521345");
		attributes.put("y", "0.8832399507533975");
		attributes.put("z", "1.0");
		kernel.handleCoords(geo_point2, attributes);
		geo_line = kernel.Line("a", (GeoPoint) geo_point1, (GeoPoint) geo_point2);
		kernel.updateConstruction();
		kernel.setNotifyViewsActive(true);
		kernel.notifyRepaint();
	}
	
	private void initKernel() {
		kernel = new Kernel(this);
	}
	
	private void initXmlHandler() {
		xmlhandler = kernel.newMyXMLHandler(kernel.getConstruction());
		Base64Form.setApplication(this);
	}
	
	private void initEuclidianView() {
		GgjsViewerWrapper wrapper = new GgjsViewerWrapper();
		RootPanel.get().add(wrapper);
		euclidiancontroller = new EuclidianController(kernel);
		euclidiancontroller.setApplication(this);
		euclidianview = wrapper.getEuclidianView();
		kernel.notifyAddAll(euclidianview);
		kernel.attach(euclidianview);
		euclidianview.setKernel(kernel);
		euclidianview.setApplication(this);
		euclidiancontroller.setEuclidianView(euclidianview);	
		euclidianview.setEuclidianController(euclidiancontroller);
		euclidianview.addMouseDownHandler(euclidiancontroller);
		euclidianview.addMouseUpHandler(euclidiancontroller);
		euclidianview.addMouseWheelHandler(euclidiancontroller);
		euclidianview.addMouseOverHandler(euclidiancontroller);
		euclidianview.addMouseOutHandler(euclidiancontroller);
		euclidianview.addMouseMoveHandler(euclidiancontroller);
		
	
		//CONNECTIONS MUST BE MADE
	}
	
	

	public boolean isLabelDragsEnabled() {
		// TODO Auto-generated method stub
		return true;
	}
	

	final public void clearSelectedGeos() {
		clearSelectedGeos(true);
		updateSelection();
	}
	
	public MyXMLHandler getMyXmlHandler() {
		return xmlhandler;
	}

	public void clearSelectedGeos(boolean repaint) {
		int size = selectedGeos.size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				GeoElement geo = (GeoElement) selectedGeos.get(i);
				geo.setSelected(false);
			}
			selectedGeos.clear();
			if (repaint)
				kernel.notifyRepaint();
		}
		updateSelection();
	}
	
	private void updateSelection() {
		if (!showMenuBar || !hasGuiManager())
			return;

		/*AG I don't know that we need this...getGuiManager().updateMenubarSelection();
		if (getEuclidianView().getMode() == EuclidianView.MODE_VISUAL_STYLE) {
			if (selectedGeos.size() > 0) {
				
				EuclidianController ec = getEuclidianView().getEuclidianController();
				
				for (int i = 0 ; i < selectedGeos.size() ; i++) {
					ec.setProperties(((GeoElement)(selectedGeos.get(i))));
				}
				
			}
		}*/
	}
	
	final public int selectedGeosSize() {
		return selectedGeos.size();
	}

	final public ArrayList getSelectedGeos() {
		return selectedGeos;
	}

	final public GeoElement getLastCreatedGeoElement() {
		return kernel.getConstruction().getLastGeoElement();
	}
	
	final public void toggleSelectedGeo(GeoElement geo) {
		toggleSelectedGeo(geo, true);
	}

	final public void toggleSelectedGeo(GeoElement geo, boolean repaint) {
		if (geo == null)
			return;

		boolean contains = selectedGeos.contains(geo);
		if (contains) {
			selectedGeos.remove(geo);
			geo.setSelected(false);
		} else {
			selectedGeos.add(geo);
			geo.setSelected(true);
		}

		if (repaint)
			kernel.notifyRepaint();
		updateSelection();
	}

	public void addSelectedGeo(GeoElement geo) {
		// TODO Auto-generated method stub
		addSelectedGeo(geo, true);		
	}

	final public void addSelectedGeo(GeoElement geo, boolean repaint) {
		if (geo == null || selectedGeos.contains(geo))
			return;

		selectedGeos.add(geo);
		geo.setSelected(true);
		if (repaint)
			kernel.notifyRepaint();
		updateSelection();
	}

	public String translateCommand(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getError(String message) {
		// TODO Auto-generated method stub
		return message+"must be implemented Application.java getError()";
	}

	public void showError(String message) {
		GWT.log(message);
		// TODO Auto-generated method stub
		
	}
	
	final public String getCommand(String key) {
		/*AGinitTranslatedCommands();		

		try {
			return rbcommand.getString(key);
		} catch (Exception e) {
			return key;
		}*/
		return "getCommand needed";
	}


}
