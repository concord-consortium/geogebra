package org.geogebra.ggjsviewer.client.io;






import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import org.geogebra.ggjsviewer.client.kernel.Construction;
import org.geogebra.ggjsviewer.client.kernel.GeoElement;
import org.geogebra.ggjsviewer.client.kernel.GeoNumeric;
import org.geogebra.ggjsviewer.client.kernel.Kernel;
import org.geogebra.ggjsviewer.client.kernel.PointProperties;
import org.geogebra.ggjsviewer.client.kernel.arithmetic.Command;
import org.geogebra.ggjsviewer.client.kernel.arithmetic.ExpressionNode;
import org.geogebra.ggjsviewer.client.kernel.gawt.Color;
import org.geogebra.ggjsviewer.client.kernel.parser.Parser;
import org.geogebra.ggjsviewer.client.main.Application;
import org.geogebra.ggjsviewer.client.main.MyError;
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
	private Parser parser, origParser;
	private Kernel kernel, origKernel;
	private Application app;
	private Command cmd;
	private GeoElement[] cmdOutput;
	
	private LinkedList<GeoExpPair> dynamicColorList = new LinkedList<GeoExpPair>();
	//private int docPointStyle; 
	
	private int mode;
	private int constMode; // submode for <construction>
	private boolean oldVal;

	private class GeoExpPair {
		GeoElement geo;
		String exp;

		GeoExpPair(GeoElement g, String exp) {
			geo = g;
			this.exp = exp;
		}
	}
	
	
	public MyXMLHandler(Kernel kernel, Construction cons) {
		origKernel = kernel;
		origCons = cons;
		origParser = kernel.getParser();
		initKernelVars();
		
		mode = MODE_INVALID;
		constMode = MODE_CONSTRUCTION;
		// TODO Auto-generated constructor stub
	}
	
	private void initKernelVars() {
		this.kernel = origKernel;
		this.cons = origKernel.getConstruction();
		this.parser = origParser;
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
	
	private String getNodeAttr(Node nodeToQuery) {
		if (nodeToQuery != null) {
			return nodeToQuery.getNodeValue();
		}
		return null;
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
				cmd = 	getCommand(children.item(i));
				if (cmd != null && children.item(i).hasChildNodes()) {
					startCommandElement(children.item(i).getChildNodes());
				}
				
				}
				
			}
		}
		// TODO Auto-generated method stub
		
	}
	
	private void startCommandElement(NodeList childNodes) {
		boolean ok = true;
		  for (int i=0; i<childNodes.getLength();i++) {
			  String eName = childNodes.item(i).getNodeName();
			  if (!eName.equals("#text")) {
				  NamedNodeMap attributes = childNodes.item(i).getAttributes();
				  LinkedHashMap<String, String> attrs = new LinkedHashMap<String, String>();
				  for (int j=0; j<attributes.getLength();j++)
					  attrs.put(attributes.item(j).getNodeName(), attributes.item(j).getNodeValue());
					if (eName.equals("input")) {
						if (cmd == null)
							throw new MyError(app, "no command set for <input>");
						ok = handleCmdInput(attrs);
					} else if (eName.equals("output")) {
						ok = handleCmdOutput(attrs);
					} else
						System.err.println("unknown tag in <command>: " + eName);
			
					if (!ok)
						System.err.println("error in <command>: " + eName);
				}
		  }
	}

	private boolean handleCmdInput(LinkedHashMap<String, String> attrs) {
		GeoElement geo;
		ExpressionNode en;
		String arg = null;

		Collection values = attrs.values();
		Iterator it = values.iterator();
		while (it.hasNext()) {
			// parse argument expressions
			try {
				arg = (String) it.next();

				// for downward compatibility: lookup label first
				// as this could be some weird name that can't be parsed
				// e.g. "1/2_{a,b}" could be a label name
				geo = kernel.lookupLabel(arg);
				
				//Application.debug("input : "+geo.getLabel());

				// arg is a label and does not conatin $ signs (e.g. $A1 in
				// spreadsheet)
				if (geo != null && arg.indexOf('$') < 0) {
					en = new ExpressionNode(kernel, geo);
				} else {
					// parse argument expressions
					en = parser.parseCmdExpression(arg);
					
				}
				cmd.addArgument(en);
			} catch (Exception e) {
				e.printStackTrace();
				throw new MyError(app, "unknown command input: " + arg);
			} catch (Error e) {
				e.printStackTrace();
				throw new MyError(app, "unknown command input: " + arg);
			}
		}
		return true;
	}

	private boolean handleCmdOutput(LinkedHashMap attrs) {
		try {
			// set labels for command processing
			String label;
			Collection values = attrs.values();
			Iterator it = values.iterator();
			int countLabels = 0;
			while (it.hasNext()) {
				label = (String) it.next();
				if ("".equals(label))
					label = null;
				else
					countLabels++;
				cmd.addLabel(label);
			}

			// it is possible that we get a command that has been saved
			// where NONE of its output objects had a label
			// (e.g. intersection that never produced any points).
			// Such a command should not be processed as it might
			// use up labels that are needed later on.
			// For example, since v3.0 every intersection command shows
			// at least one labeled (and possibly undefined) point
			// whereas in v2.7 the label was not set before an intersection
			// point became defined for the first time.
			// THUS: let's not process commands with no labels for their output
			if (countLabels == 0)
				return true;

			// process the command
			cmdOutput = kernel.getAlgebraProcessor().processCommand(cmd, true);
			String cmdName = cmd.getName();
			if (cmdOutput == null)
				throw new MyError(app, "processing of command " + cmd
						+ " failed");
			cmd = null;

			// ensure that labels are set for invisible objects too
			if (attrs.size() != cmdOutput.length) {
				Application
						.debug("error in <output>: wrong number of labels for command "
								+ cmdName);
				System.err.println("   cmdOutput.length = " + cmdOutput.length
						+ ", labels = " + attrs.size());
				return false;
			}
			// enforce setting of labels
			// (important for invisible objects like intersection points)
			it = values.iterator();
			int i = 0;
			while (it.hasNext()) {
				label = (String) it.next();
				if ("".equals(label))
					label = null;

				if (label != null && cmdOutput[i] != null) {
					cmdOutput[i].setLoadedLabel(label);
				}
				i++;
			}
			return true;
		} catch (MyError e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new MyError(app, "processing of command: " + cmd);
		}
	}

	private Command getCommand(Node item) {
		Command cmd = null;
		String name = (String) item.getAttributes().getNamedItem("name").getNodeValue();

		//Application.debug(name);
		if (name != null)
			cmd = new Command(kernel, name, false); // do not translate name
		else
			throw new MyError(app, "name missing in <command>");
		return cmd;
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
				handlePointSize(children.item(i),geo);
			} else if (children.item(i).getNodeName().equals("pointStyle")) {
				handlePointStyle(children.item(i),geo);
			}	else if (children.item(i).getNodeName().equals("objColor")) {
				handleObjColor(children.item(i),geo);
			}	else if (children.item(i).getNodeName().equals("slider")) {
				handleSlider(children.item(i),geo);
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
	
	private boolean handleObjColor(Node item, GeoElement geoElement) {
		GWT.log(geoElement.toString());
		LinkedHashMap<String, String> attrs = new LinkedHashMap<String, String>();
		attrs.put("r", getNodeAttr(item.getAttributes().getNamedItem("r")));
		attrs.put("g", getNodeAttr(item.getAttributes().getNamedItem("g")));
		attrs.put("b", getNodeAttr(item.getAttributes().getNamedItem("b")));
		attrs.put("dynamicr", getNodeAttr(item.getAttributes().getNamedItem("dynamicr")));
		attrs.put("dynamicg", getNodeAttr(item.getAttributes().getNamedItem("dynamicg")));
		attrs.put("dynamicb", getNodeAttr(item.getAttributes().getNamedItem("dynamicb")));
		//GWT.log(attrs.get("r"));
		//GWT.log(attrs.get("g"));
		//GWT.log(attrs.get("b"));

		Color col = handleColorAttrs(attrs);
		if (col == null)
			return false;
		geoElement.setObjColor(col);

		// Dynamic colors
		// Michael Borcherds 2008-04-02
		String red = "";
		String green = "";
		String blue = "";
		red = (String) attrs.get("dynamicr");
		green = (String) attrs.get("dynamicg");
		blue = (String) attrs.get("dynamicb");

		if (red != null && green != null && blue != null)
			try {
				if (!red.equals("") || !green.equals("") || !blue.equals("")) {
					if (red.equals(""))
						red = "0";
					if (green.equals(""))
						green = "0";
					if (blue.equals(""))
						blue = "0";

					// geo.setColorFunction(kernel.getAlgebraProcessor().evaluateToList("{"+red
					// + ","+green+","+blue+"}"));
					// need to to this at end of construction (dependencies!)
					dynamicColorList.add(new GeoExpPair(geoElement, "{" + red + ","
							+ green + "," + blue + "}"));

				}
			} catch (Exception e) {
				System.err.println("Error loading Dynamic Colors");
			}

		String alpha = (String) attrs.get("alpha");
		if (alpha != null ) // ignore alpha value for lists prior to GeoGebra 3.2
			geoElement.setAlphaValue(Float.parseFloat(alpha));
		return true;
	}
	
	private Color handleColorAttrs(LinkedHashMap<String, String> attrs) {
		try {
			int red = Integer.parseInt((String) attrs.get("r"));
			int green = Integer.parseInt((String) attrs.get("g"));
			int blue = Integer.parseInt((String) attrs.get("b"));
			return new Color(red, green, blue);
		} catch (Exception e) {
			return null;
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
			p.setPointSize(Integer.parseInt(getNodeAttr(item.getAttributes().getNamedItem("val"))));
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
		geoElement.setEuclidianVisible(parseBoolean(getNodeAttr(item.getAttributes().getNamedItem("object"))));
		geoElement.setLabelVisible(parseBoolean(getNodeAttr(item.getAttributes().getNamedItem("label"))));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void handleCoords(Node item, GeoElement geoElement) {
		//GWT.log("handleChoords");
		LinkedHashMap<String, String> attrs = new LinkedHashMap<String, String>();
		attrs.put("x", getNodeAttr(item.getAttributes().getNamedItem("x")));
		attrs.put("y", getNodeAttr(item.getAttributes().getNamedItem("y")));
		attrs.put("z", getNodeAttr(item.getAttributes().getNamedItem("z")));
		//GWT.log(item.getAttributes().getNamedItem("x").getNodeValue());
		//GWT.log(item.getAttributes().getNamedItem("y").getNodeValue());
		//GWT.log(item.getAttributes().getNamedItem("z").getNodeValue());
		kernel.handleCoords(geoElement, attrs);
		// TODO Auto-generated method stub
		
	}
	
	private boolean handleSlider(Node item, GeoElement geoElement) {
		if (!(geoElement.isGeoNumeric())) {
			System.err.println("wrong element type for <slider>: "
					+ geoElement.getClass());
			return false;
		}

		try {
			// don't create sliders in macro construction
			if (geoElement.getKernel().isMacroKernel())
				return true;

			GeoNumeric num = (GeoNumeric) geoElement;

			String str = (String) getNodeAttr(item.getAttributes().getNamedItem("min"));
			if (str != null) {
				num.setIntervalMin(Double.parseDouble(str));
			}

			str = (String) getNodeAttr(item.getAttributes().getNamedItem("max"));
			if (str != null) {
				num.setIntervalMax(Double.parseDouble(str));
			}

			str = (String) getNodeAttr(item.getAttributes().getNamedItem("absoluteScreenLocation"));
			if (str != null) {
				num.setAbsoluteScreenLocActive(parseBoolean(str));
			} else {
				num.setAbsoluteScreenLocActive(false);
			}

			double x = Double.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("x")));
			double y = Double.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("y")));
			num.setSliderLocation(x, y);
			num.setSliderWidth(Double.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("width"))));
			num.setSliderFixed(parseBoolean((String) getNodeAttr(item.getAttributes().getNamedItem("fixed"))));
			num.setSliderHorizontal(parseBoolean((String) getNodeAttr(item.getAttributes().getNamedItem("horizontal"))));

			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	//utils
	
	protected boolean parseBoolean(String str) throws Exception {
		return "true".equals(str);
	}
	
	
}
