package org.geogebra.ggjsviewer.client.io;


import org.geogebra.ggjsviewer.client.kernel.Construction;
import org.geogebra.ggjsviewer.client.kernel.GeoElement;
import org.geogebra.ggjsviewer.client.kernel.Kernel;
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
	
	private GeoElement geo;
	private Construction cons, origCons;
	private Kernel kernel, origKernel;
	private Application app;

	public MyXMLHandler(Kernel kernel, Construction cons) {
		origKernel = kernel;
		origCons = cons;
		initKernelVars();
		// TODO Auto-generated constructor stub
	}
	
	private void initKernelVars() {
		this.kernel = origKernel;
		this.cons = origKernel.getConstruction();
	}


	public void parseXml(String text) {
		if (text.indexOf("loadBase64Unzipped") > -1) {
			String xml = Base64.decode(text.substring(text.indexOf("(")+2, text.indexOf(")")-1));
			processXml(xml);
		// TODO Auto-generated method stub
		} else {
			Window.alert("Malformed Base64 string");
		}
		
	}
	
	private void processXml(String xmlString) {
		try {
			Document xmlDoc = XMLParser.parse(xmlString);
			//For now, we need only elements
			//at first we will try only points
			Node construction = xmlDoc.getElementsByTagName("construction").item(0);
			
			if (construction != null) {
				handleConstructionElement(construction);			
			}
			
		} catch(DOMException e) {
			Window.alert("Could not parse xml document.");
			e.printStackTrace();
		}
		
	}

	private void handleConstructionElement(Node construction) {
		//For now, we are interested only in elements
		NodeList consChildren = construction.getChildNodes();
		for (int i=0; i<consChildren.getLength();i++) {
			String current = consChildren.item(i).getNodeName();
			// <element> encountered
			if (current.equalsIgnoreCase("element")) {
				geo = startGeoElement(consChildren.item(i));
			
			}
		}
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
			GWT.log(geo.toString());
		}

		
		
		// TODO Auto-generated method stub
		return geo;
	}
	
	
}
