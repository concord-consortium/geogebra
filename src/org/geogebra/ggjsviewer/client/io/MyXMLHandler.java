package org.geogebra.ggjsviewer.client.io;


import java.util.LinkedHashMap;

import org.geogebra.ggjsviewer.client.kernel.Construction;
import org.geogebra.ggjsviewer.client.kernel.GeoElement;
import org.geogebra.ggjsviewer.client.kernel.Kernel;
import org.geogebra.ggjsviewer.client.kernel.PointProperties;
import org.geogebra.ggjsviewer.client.main.Application;
import org.geogebra.ggjsviewer.client.util.Base64;


import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.xml.client.DOMException;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class MyXMLHandler  {
	
	private static final int MODE_INVALID = -1;
	private static final int MODE_GEOGEBRA = 1;
	private static final int MODE_MACRO = 50;
	private static final int MODE_EUCLIDIAN_VIEW = 100;
	protected static final int MODE_EUCLIDIAN_VIEW3D = 101; //only for 3D
	private static final int MODE_SPREADSHEET_VIEW = 150;
	private static final int MODE_CAS_VIEW = 160;
	private static final int MODE_CAS_SESSION = 161;
	private static final int MODE_CAS_CELL_PAIR = 162;
	private static final int MODE_CAS_INPUT_CELL = 163;
	private static final int MODE_CAS_OUTPUT_CELL = 164;
	private static final int MODE_KERNEL = 200;
	private static final int MODE_CONSTRUCTION = 300;
	private static final int MODE_CONST_GEO_ELEMENT = 301;
	private static final int MODE_CONST_COMMAND = 302;
	
	private static final int MODE_GUI = 400;
	private static final int MODE_GUI_PERSPECTIVES = 401; // <perspectives>
	private static final int MODE_GUI_PERSPECTIVE = 402; // <perspective>
	private static final int MODE_GUI_PERSPECTIVE_PANES = 403; // <perspective> <panes /> </perspective>
	private static final int MODE_GUI_PERSPECTIVE_VIEWS = 404; // <perspective> <views /> </perspective>

	
	
	private GeoElement geo;
	private Construction cons, origCons;
	private Kernel kernel, origKernel;
	private Application app;
	
	//private int docPointStyle; 
	
	private int mode;
	private int constMode; // submode for <construction>
	private boolean oldVal;

	public MyXMLHandler(Kernel kernel, Construction cons) {
		origKernel = kernel;
		origCons = cons;
		initKernelVars();
		
		mode = MODE_INVALID;
		constMode = MODE_CONSTRUCTION;
		// TODO Auto-generated constructor stub
	}
	
	private void initKernelVars() {
		this.kernel = origKernel;
		this.cons = origKernel.getConstruction();
	}


	public void parseXml(String text) {
		if (text.indexOf("loadBase64Unzipped") > -1) {
			String xml = Base64.decode(text.substring(text.indexOf("(")+2, text.indexOf(")")-1));
			boolean oldVal = kernel.isNotifyViewsActive();
			//clear the kernel
			kernel.clearConstruction();
			kernel.updateConstruction();
			processXml(xml);
			kernel.updateConstruction();
			kernel.setNotifyViewsActive(oldVal);	
		// TODO Auto-generated method stub
		} else {
			Window.alert("Malformed Base64 string");
		}
		
	}
	private void startElement(Node element) {
		switch (mode) {
		case MODE_GEOGEBRA: // top level mode
			startGeoGebraElement(element);
			break;
		case MODE_CONSTRUCTION:
			startConstructionElement(element);
			break;
		}
	}
	
	
	private void startGeoGebraElement(Node element) {
		if (element.hasChildNodes()) {
			NodeList children = element.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				if (children.item(i).getNodeName().equalsIgnoreCase("construction")) {
					startConstructionElement(children.item(i));
				} else if (children.item(i).getNodeName().equals("kernel")) {
					startKernelElement(children.item(i));
				} //etc
			}
		}
		// TODO Auto-generated method stub
		
	}

	private void startKernelElement(Node item) {
		GWT.log(item.getNodeName());
		// TODO Auto-generated method stub
		
	}

	private void processXml(String xmlString) {
		try {
			Document xmlDoc = XMLParser.parse(xmlString);
			//We should encounter a geogebra element first
			Node geogebraElement = xmlDoc.getFirstChild();
			if (geogebraElement.getNodeName().equals("geogebra")) {
				mode = MODE_GEOGEBRA;
				startElement(geogebraElement);
			}
			
		} catch(DOMException e) {
			Window.alert("Could not parse xml document.");
			e.printStackTrace();
		}
		
	}

	private void startConstructionElement(Node construction) {
		//For now, we are interested only in elements
		if (construction.hasChildNodes()) {
			NodeList children = construction.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				if (children.item(i).getNodeName().equals("element")) {
					startGeoElement(children.item(i));
				} else if (children.item(i).getNodeName().equals("command")) {
					startCommandElement(children.item(i));
				}
				
			}
		}
		// TODO Auto-generated method stub
		
	}

	private void startCommandElement(Node item) {
		GWT.log(item.getNodeName());
		// TODO Auto-generated method stub
		
	}

	private GeoElement startGeoElement(Node item) {
		GeoElement geo = null;
		String label = (String) item.getAttributes().getNamedItem("label").getNodeValue();
		String type = (String) 	item.getAttributes().getNamedItem("type").getNodeValue();
		if (label == null || type == null) {
			System.err.println("attributes missing in <element>");
			return geo;
		}
		geo = kernel.lookupLabel(label);
		
		if (geo == null) {
			geo = kernel.createGeoElement(cons, type);
			geo.setLoadedLabel(label);
			// independent GeoElements should be hidden by default
			// (as older versions of this file format did not
			// store show/hide information for all kinds of objects,
			// e.g. GeoNumeric)
			geo.setEuclidianVisible(false);
			//GWT.log(geo.toString());
		}
	
		//look for children
		NodeList children = item.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeName().equals("coords")) {
				handleCoords(children.item(i),geo);
			} else if (children.item(i).getNodeName().equals("show")) {
				handleShow(children.item(i),geo);
			} else if (children.item(i).getNodeName().equals("pointSize")) {
				handlePointSize(item,geo);
			} else if (children.item(i).getNodeName().equals("pointStyle")) {
				handlePointStyle(item,geo);
			}	
			
		}

		
		
		// TODO Auto-generated method stub
		return geo;
	}

	private void handlePointStyle(Node item, GeoElement geoElement) {
		if (!(geoElement instanceof PointProperties)) {
			Application.debug("wrong element type for <pointStyle>: "
					+ geoElement.getClass());
			return;
		}

		try {
			PointProperties p = (PointProperties) geoElement;
			
			int style = Integer.parseInt(item.getAttributes().getNamedItem("val").getNodeValue());
			
			/*if(style == -1) {
				style = docPointStyle;
			}*/
			p.setPointStyle(style);
			return;
		} catch (Exception e) {
			return;
		}
		
	}

	private void handlePointSize(Node item, GeoElement geoElement) {
		// TODO Auto-generated method stub
		//GWT.log("pointSize");
		if (!(geoElement instanceof PointProperties)) {
			Application.debug("wrong element type for <pointSize>: "
					+ geoElement.getClass());
			return;
		}

		try {
			PointProperties p = (PointProperties) geoElement;
			p.setPointSize(Integer.parseInt(item.getAttributes().getNamedItem("val").getNodeValue()));
			return;
		} catch (Exception e) {
			return;
		}
	}

	private void handleShow(Node item, GeoElement geoElement) {
		// TODO Auto-generated method stub
		//GWT.log("handleshow");
		
		try {
		//GWT.log(String.valueOf(parseBoolean(item.getAttributes().getNamedItem("object").getNodeValue())));
		//GWT.log(String.valueOf(parseBoolean(item.getAttributes().getNamedItem("label").getNodeValue())));
		geoElement.setEuclidianVisible(parseBoolean(item.getAttributes().getNamedItem("object").getNodeValue()));
		geoElement.setLabelVisible(parseBoolean(item.getAttributes().getNamedItem("label").getNodeValue()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void handleCoords(Node item, GeoElement geoElement) {
		GWT.log("handleChoords");
		LinkedHashMap<String, String> attrs = new LinkedHashMap<String, String>();
		attrs.put("x", item.getAttributes().getNamedItem("x").getNodeValue());
		attrs.put("y", item.getAttributes().getNamedItem("y").getNodeValue());
		attrs.put("z", item.getAttributes().getNamedItem("z").getNodeValue());
		GWT.log(item.getAttributes().getNamedItem("x").getNodeValue());
		GWT.log(item.getAttributes().getNamedItem("y").getNodeValue());
		GWT.log(item.getAttributes().getNamedItem("z").getNodeValue());
		kernel.handleCoords(geoElement, attrs);
		// TODO Auto-generated method stub
		
	}
	
	//utils
	
	protected boolean parseBoolean(String str) throws Exception {
		return "true".equals(str);
	}
	
	
}
