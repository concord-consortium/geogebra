package org.geogebra.ggjsviewer.client.main;

import java.util.LinkedHashMap;

import org.geogebra.ggjsviewer.client.euclidian.EuclidianController;
import org.geogebra.ggjsviewer.client.euclidian.EuclidianView;
import org.geogebra.ggjsviewer.client.gui.GgjsViewerWrapper;
import org.geogebra.ggjsviewer.client.kernel.BaseApplication;
import org.geogebra.ggjsviewer.client.kernel.GeoElement;
import org.geogebra.ggjsviewer.client.kernel.GeoLine;
import org.geogebra.ggjsviewer.client.kernel.GeoPoint;
import org.geogebra.ggjsviewer.client.kernel.Kernel;

import com.google.gwt.user.client.ui.RootPanel;

public class Application extends BaseApplication {
	
	private Kernel kernel;
	private EuclidianView euclidianview;
	private EuclidianController euclidiancontroller;
	
	
	public Application() {
		super();
		initKernel();	
		initEuclidianView();
		
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
	
	private void initEuclidianView() {
		GgjsViewerWrapper wrapper = new GgjsViewerWrapper();
		RootPanel.get().add(wrapper);
		euclidiancontroller = new EuclidianController(kernel);
		euclidianview = wrapper.getEuclidianView();
		kernel.notifyAddAll(euclidianview);
		kernel.attach(euclidianview);
		euclidiancontroller.setEuclidianView(euclidianview);
		euclidianview.setEuclidianController(euclidiancontroller);
		euclidianview.addMouseDownHandler(euclidiancontroller);
	
		//CONNECTIONS MUST BE MADE
	}

}
