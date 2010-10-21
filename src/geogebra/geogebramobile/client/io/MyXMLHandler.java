package geogebra.geogebramobile.client.io;






import geogebra.geogebramobile.client.euclidian.EuclidianView;
import geogebra.geogebramobile.client.kernel.AbsoluteScreenLocateable;
import geogebra.geogebramobile.client.kernel.Construction;
import geogebra.geogebramobile.client.kernel.GeoAngle;
import geogebra.geogebramobile.client.kernel.GeoBoolean;
import geogebra.geogebramobile.client.kernel.GeoButton;
import geogebra.geogebramobile.client.kernel.GeoConic;
import geogebra.geogebramobile.client.kernel.GeoCubic;
import geogebra.geogebramobile.client.kernel.GeoElement;
import geogebra.geogebramobile.client.kernel.GeoLine;
import geogebra.geogebramobile.client.kernel.GeoNumeric;
import geogebra.geogebramobile.client.kernel.GeoPoint;
import geogebra.geogebramobile.client.kernel.GeoPointInterface;
import geogebra.geogebramobile.client.kernel.GeoVec3D;
import geogebra.geogebramobile.client.kernel.Kernel;
import geogebra.geogebramobile.client.kernel.LimitedPath;
import geogebra.geogebramobile.client.kernel.Locateable;
import geogebra.geogebramobile.client.kernel.PointProperties;
import geogebra.geogebramobile.client.kernel.TextProperties;
import geogebra.geogebramobile.client.kernel.arithmetic.Command;
import geogebra.geogebramobile.client.kernel.arithmetic.ExpressionNode;
import geogebra.geogebramobile.client.kernel.arithmetic.NumberValue;
import geogebra.geogebramobile.client.kernel.arithmetic.ValidExpression;
import geogebra.geogebramobile.client.kernel.commands.AlgebraProcessor;
import geogebra.geogebramobile.client.kernel.gawt.Color;
import geogebra.geogebramobile.client.kernel.parser.Parser;
import geogebra.geogebramobile.client.main.Application;
import geogebra.geogebramobile.client.main.MyError;
import geogebra.geogebramobile.client.util.Base64;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;



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

	
	// List of LocateableExpPair objects
	// for setting the start points at the end of the construction
	// (needed for GeoText and GeoVector)
	private LinkedList<LocateableExpPair> startPointList = new LinkedList<LocateableExpPair>();

	private LinkedList<GeoExpPair> dynamicColorList = new LinkedList<GeoExpPair>();
	private LinkedList<GeoExpPair> animationSpeedList = new LinkedList<GeoExpPair>();
	private LinkedList<GeoExpPair> showObjectConditionList = new LinkedList<GeoExpPair>();
	private int docPointStyle; 
	private double ggbFileFormat;
	
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
	
	private class LocateableExpPair {
		Locateable locateable;
		String exp; // String with expression to create point 
		GeoPointInterface point; // free point
		int number; // number of startPoint

		LocateableExpPair(Locateable g, String s, int n) {
			locateable = g;
			exp = s;
			number = n;
		}
		
		LocateableExpPair(Locateable g, GeoPointInterface p, int n) {
			locateable = g;
			point = p;
			number = n;
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
			//GWT.log(xml);
			boolean oldVal = kernel.isNotifyViewsActive();
			//clear the kernel
			kernel.clearConstruction();
			kernel.updateConstruction();
			processXml(xml);
			kernel.updateConstruction();
			kernel.setNotifyViewsActive(oldVal);	
		// TODO Auto-generated method stub
		} else if (text.indexOf("<geogebra") > -1){
			boolean oldVal = kernel.isNotifyViewsActive();
			//clear the kernel
			kernel.clearConstruction();
			kernel.updateConstruction();
			processXml(text);
			kernel.updateConstruction();
			kernel.setNotifyViewsActive(oldVal);	
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
					endConstructionElement(children.item(i));
				} else if (children.item(i).getNodeName().equals("kernel")) {
					startKernelElement(children.item(i));
				} else if (children.item(i).getNodeName().equals("euclidianView")) {
					startEuclidianViewElement(children.item(i));
				}
			}
		}
		// TODO Auto-generated method stub
		
	}
	private void endConstructionElement(Node item) {
		
		processStartPointList();
		//AGprocessLinkedGeoList();
		processShowObjectConditionList();
		processDynamicColorList();
		processAnimationSpeedList();
		//GWT.log(item.getNodeName()+" cons");
		
	}

	// ===============
	// <euclidianview>
	// ===============
	private void startEuclidianViewElement(Node item) {

		//look for children
		NodeList children = item.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			String eName = children.item(i).getNodeName();
			boolean ok = true;
			EuclidianView ev = app.getEuclidianView();
			switch (eName.charAt(0)) {
			case 'a':
				if (eName.equals("axesColor")) {
					ok = handleAxesColor(ev, children.item(i));
					break;
				} else if (eName.equals("axis")) {
					ok = handleAxis(ev, children.item(i));
					break;
				}
			case 'b':
				if (eName.equals("bgColor")) {
					ok = handleBgColor(ev, children.item(i));
					break;
				}
			case 'c':
				if (eName.equals("coordSystem")) {
					ok = handleCoordSystem(ev, children.item(i));
					break;
				}
			case 'e':
				if (eName.equals("evSettings")) {
					ok = handleEvSettings(ev, children.item(i));
					break;
				}
			case 'g':
				if (eName.equals("grid")) {
					ok = handleGrid(ev, children.item(i));
					break;
				} else if (eName.equals("gridColor")) {
					ok = handleGridColor(ev, children.item(i));
					break;
				}
			case 'l':
				if (eName.equals("lineStyle")) {
					ok = handleLineStyle(ev, children.item(i));
					break;
				}

			case 's':
				if (eName.equals("size")) {
					ok = handleEvSize(ev, children.item(i));
					break;
				}

			default:
				System.err.println("unknown tag in <euclidianView>: " + eName);
			}

			if (!ok)
				System.err.println("error in <euclidianView>: " + eName);
		}
	}
	private boolean handleGridColor(EuclidianView ev, Node item) {
		LinkedHashMap<String, String> attrs = new LinkedHashMap<String, String>();
		attrs.put("r", getNodeAttr(item.getAttributes().getNamedItem("r")));
		attrs.put("g", getNodeAttr(item.getAttributes().getNamedItem("g")));
		attrs.put("b", getNodeAttr(item.getAttributes().getNamedItem("b")));
		Color col = handleColorAttrs(attrs);
		if (col == null)
			return false;
		ev.setGridColor(col);
		return true;
	}

	private boolean handleGrid(EuclidianView ev, Node item) {
		// <grid distX="2.0" distY="4.0"/>
		try {
			double[] dists = new double[2];
			dists[0] = Double.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("distX")));
			dists[1] = Double.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("distY")));
			
			//G.Sturr
			//TODO: this is a temporary fix until Polar grid xml is done
			dists[2] = Double.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("distY")));
			ev.setGridDistances(dists);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleBgColor(EuclidianView ev, Node item) {
		LinkedHashMap<String, String> attrs = new LinkedHashMap<String, String>();
		attrs.put("r", getNodeAttr(item.getAttributes().getNamedItem("r")));
		attrs.put("g", getNodeAttr(item.getAttributes().getNamedItem("g")));
		attrs.put("b", getNodeAttr(item.getAttributes().getNamedItem("b")));
		Color col = handleColorAttrs(attrs);
		if (col == null)
			return false;
		ev.setBackground(col);
		return true;
	}

	private boolean handleEvSettings(EuclidianView ev, Node item) {
		try {
			// axes attribute was removed with V3.0, see handleAxis()
			// this code is for downward compatibility
			String strAxes = (String) getNodeAttr(item.getAttributes().getNamedItem("axes"));
			if (strAxes != null) {
				boolean showAxes = parseBoolean(strAxes);
				//ev.showAxes(showAxes, showAxes);
				ev.setShowAxes(showAxes, true);
			}

			ev.showGrid(parseBoolean((String) getNodeAttr(item.getAttributes().getNamedItem("grid"))));

			try {
				ev.setGridIsBold(parseBoolean((String) getNodeAttr(item.getAttributes().getNamedItem("gridIsBold")))); // Michael Borcherds
				// 2008-04-11
			} catch (Exception e) {
			}

			try {
				ev.setGridType(Integer.parseInt((String) getNodeAttr(item.getAttributes().getNamedItem("gridType")))); // Michael Borcherds
				// 2008-04-30
			} catch (Exception e) {
			}

			String str = (String) getNodeAttr(item.getAttributes().getNamedItem("pointCapturing"));
			if (str != null) {
				// before GeoGebra 2.7 pointCapturing was either "true" or
				// "false"
				// now pointCapturing holds an int value
				int pointCapturingMode;
				if (str.equals("false"))
					pointCapturingMode = 0;
				else if (str.equals("true"))
					pointCapturingMode = 1;
				else
					// int value
					pointCapturingMode = Integer.parseInt(str);
				ev.setPointCapturing(pointCapturingMode);
			}
			
			// if there is a point style given save it
			/*AG I'm suppose that new version will be availableif(ggbFileFormat < 3.3) {
				String strPointStyle = (String) attrs.get("pointStyle");
				if (strPointStyle != null) {
					docPointStyle = Integer.parseInt(strPointStyle);
				} else {
					docPointStyle = EuclidianView.POINT_STYLE_DOT;
				}
				
				// TODO save as default construction (F.S.)
			} else {*/
				docPointStyle = -1;
			//AG}

			// Michael Borcherds 2008-05-12
			// size of checkbox
			String strBooleanSize = (String) getNodeAttr(item.getAttributes().getNamedItem("checkboxSize"));
			if (strBooleanSize != null)
				ev.setBooleanSize(Integer.parseInt(strBooleanSize));

			// v3.0: appearance of right angle
			String strRightAngleStyle = (String) getNodeAttr(item.getAttributes().getNamedItem("rightAngleStyle"));
			if (strRightAngleStyle == null)
				// before v3.0 the default was a dot to show a right angle
				ev.setRightAngleStyle(EuclidianView.RIGHT_ANGLE_STYLE_DOT);
			else
				ev.setRightAngleStyle(Integer.parseInt(strRightAngleStyle));

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleEvSize(EuclidianView ev, Node item) {
		// TODO Auto-generated method stub
		return true;
	}

	private boolean handleLineStyle(EuclidianView ev, Node item) {
		try {
			ev.setAxesLineStyle(Integer.parseInt((String) getNodeAttr(item.getAttributes().getNamedItem("axes"))));
			ev.setGridLineStyle(Integer.parseInt((String) getNodeAttr(item.getAttributes().getNamedItem("grid"))));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleCoordSystem(EuclidianView ev, Node item) {
		try {
			double xZero = Double.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("xZero")));
			double yZero = Double.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("yZero")));
			double scale = Double.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("scale")));

			// new since version 2.5
			double yscale = scale;
			String strYscale = (String) getNodeAttr(item.getAttributes().getNamedItem("yscale"));
			if (strYscale != null) {
				yscale = Double.parseDouble(strYscale);
			}
			ev.setCoordSystem(xZero, yZero, scale, yscale, false);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleAxis(EuclidianView ev, Node item) {
		// <axis id="0" label="x" unitLabel="x" showNumbers="true"
		// tickDistance="2"/>
		try {
			int axis = Integer.parseInt((String) getNodeAttr(item.getAttributes().getNamedItem("id")));
			String strShowAxis = (String) getNodeAttr(item.getAttributes().getNamedItem("show"));
			String label = (String) getNodeAttr(item.getAttributes().getNamedItem("label"));
			String unitLabel = (String) getNodeAttr(item.getAttributes().getNamedItem("unitLabel"));
			boolean showNumbers = parseBoolean((String) getNodeAttr(item.getAttributes().getNamedItem("showNumbers")));

			// show this axis
			if (strShowAxis != null) {
				boolean showAxis = parseBoolean(strShowAxis);
				ev.setShowAxis(axis, showAxis, true);
			}

			// set label
			ev.setAxisLabel(axis, label);
			/*
			if (label != null && label.length() > 0) {
				String[] labels = ev.getAxesLabels();
				labels[axis] = label;
				ev.setAxesLabels(labels);
			}
			*/

			// set unitlabel
			if (unitLabel != null && unitLabel.length() > 0) {
				String[] unitLabels = ev.getAxesUnitLabels();
				unitLabels[axis] = unitLabel;
				ev.setAxesUnitLabels(unitLabels);
			}

			// set showNumbers
			ev.setShowAxisNumbers(axis, showNumbers);
			/*
			boolean showNums[] = ev.getShowAxesNumbers();
			showNums[axis] = showNumbers;
			ev.setShowAxesNumbers(showNums);
			*/

			// check if tickDistance is given
			String strTickDist = (String) getNodeAttr(item.getAttributes().getNamedItem("tickDistance"));
			if (strTickDist != null) {
				double tickDist = Double.parseDouble(strTickDist);
				ev.setAxesNumberingDistance(tickDist, axis);
			}

			// tick style
			String strTickStyle = (String) getNodeAttr(item.getAttributes().getNamedItem("tickStyle"));
			if (strTickStyle != null) {
				int tickStyle = Integer.parseInt(strTickStyle);
				//ev.getAxesTickStyles()[axis] = tickStyle;
				ev.setAxisTickStyle(axis, tickStyle);
			} else {
				// before v3.0 the default tickStyle was MAJOR_MINOR
				//ev.getAxesTickStyles()[axis] = EuclidianView.AXES_TICK_STYLE_MAJOR_MINOR;
				ev.setAxisTickStyle(axis, EuclidianView.AXES_TICK_STYLE_MAJOR_MINOR);
			}
			return true;
		} catch (Exception e) {
			//e.printStackTrace();
			return false;
		}
	}
	
	

	private boolean handleAxesColor(EuclidianView ev, Node item) {
		LinkedHashMap<String, String> attrs = new LinkedHashMap<String, String>();
		attrs.put("r", getNodeAttr(item.getAttributes().getNamedItem("r")));
		attrs.put("g", getNodeAttr(item.getAttributes().getNamedItem("g")));
		attrs.put("b", getNodeAttr(item.getAttributes().getNamedItem("b")));
		Color col = handleColorAttrs(attrs);
		if (col == null)
			return false;
		ev.setAxesColor(col);
		return true;
	}

	private void startKernelElement(Node item) {
		//GWT.log(item.getNodeName());
		// TODO Auto-generated method stub
		
	}

	public void processXml(String xmlString) {
		try {
			//GWT.log(xmlString);
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
				} else if (children.item(i).getNodeName().equals("expression")) {
					startExpressionElement(children.item(i));
				}
				
			}
		}
		// TODO Auto-generated method stub
		
	}
	
	// ====================================
	// <expression>
	// ====================================
	
	private void startExpressionElement(Node item) {
		String label = (String) getNodeAttr(item.getAttributes().getNamedItem("label"));
		String exp = (String) getNodeAttr(item.getAttributes().getNamedItem("exp"));
		if (exp == null)
			throw new MyError(app, "exp missing in <expression>");

		// type may be vector or point, this is important to distinguish between
		// them
		String type = (String) getNodeAttr(item.getAttributes().getNamedItem("type"));

		// parse expression and process it
		try {
			ValidExpression ve = parser.parseGeoGebraExpression(exp);
			if (label != null)
				ve.setLabel(label);

			// enforce point or vector type if it was given in attribute type
			if (type != null) {
				if (type.equals("point")) {
					((ExpressionNode) ve).setForcePoint();
				} else if (type.equals("vector")) {
					((ExpressionNode) ve).setForceVector();
				}
			}

			GeoElement[] result = kernel.getAlgebraProcessor()
					.processValidExpression(ve);

			// ensure that labels are set for invisible objects too
			if (result != null && label != null && result.length == 1) {
				result[0].setLoadedLabel(label);
			} else {
				System.err.println("error in <expression>: " + exp + ", label: "
						+ label);
			}

		} catch (Exception e) {
			String msg = "error in <expression>: label=" + label + ", exp= "
					+ exp;
			System.err.println(msg);
			e.printStackTrace();
			throw new MyError(app, msg);
		} catch (Error e) {
			String msg = "error in <expression>: label = " + label + ", exp = "
					+ exp;
			System.err.println(msg);
			e.printStackTrace();
			throw new MyError(app, msg);
		}
		
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
			String eName = children.item(i).getNodeName();
			boolean ok = true;
			switch (eName.charAt(0)) {
			case 'a':
				if (eName.equals("animation")) {
					ok = handleAnimation(children.item(i),geo);
					break;
				} else if (eName.equals("auxiliary")) {
					ok = handleAuxiliary(children.item(i),geo);
					break;
				} else if (eName.equals("absoluteScreenLocation")) {
					ok = handleAbsoluteScreenLocation(children.item(i),geo);
					break;
				} else if (eName.equals("allowReflexAngle")) {
					ok = handleAllowReflexAngle(children.item(i),geo);
					break;
				}
			case 'b':
				if (eName.equals("breakpoint")) {
					ok = handleBreakpoint(children.item(i),geo);
					break;
				}
			case 'c':
				if (eName.equals("coords")) {
					ok = handleCoords(children.item(i),geo);
					break;
				} else if (eName.equals("coordStyle")) {
					ok = handleCoordStyle(children.item(i),geo);
					break;
				} else if (eName.equals("caption")) {
					ok = handleCaption(children.item(i),geo);
					break;
				} else if (eName.equals("condition")) {
					ok = handleCondition(children.item(i),geo);
					break;
				}
			case 'e':
				if (eName.equals("eqnStyle")) {
					ok = handleEqnStyle(children.item(i),geo);
					break;
				} else if (eName.equals("eigenvectors")) {
					ok = handleEigenvectors(children.item(i),geo);
					break;
				}
			case 'f':
				if (eName.equals("fixed")) {
					ok = handleFixed(children.item(i),geo);
					break;
				} else if (eName.equals("font")) {
					ok = handleTextFont(children.item(i),geo);
					break;
				}
			case 'k':
				if (eName.equals("keepTypeOnTransform")) {
					ok = handleKeepTypeOnTransform(children.item(i),geo);
					break;
				}
			case 'l':
				if (eName.equals("labelMode")){
					ok = handleLabelMode(children.item(i),geo);
					break;
				} else if (eName.equals("labelOffset")) {
					ok = handleLabelOffset(children.item(i),geo);
					break;
				} else if (eName.equals("layer")) {
					ok = handleLayer(children.item(i),geo);
					break;
				} else if (eName.equals("lineStyle")) {
					ok = handleLineStyle(children.item(i),geo);
					break;
				}
			case 'm':
				if (eName.equals("matrix")) {
					ok = handleMatrix(children.item(i),geo);
					break;
				}
			case 'o':
				if (eName.equals("objColor")) {
					ok = handleObjColor(children.item(i), geo);
					break;
				} else if (eName.equals("outlyingIntersections")) {
					ok = handleOutlyingIntersections(children.item(i),geo);
					break;
				}
			case 'p':
				if (eName.equals("pointSize")) {
					ok = handlePointSize(children.item(i), geo);
					break;
				} else if (eName.equals("pointStyle")) {
					ok = handlePointStyle(children.item(i), geo);
					break;
				}
			case 's':
				if (eName.equals("show")) {
					ok = handleShow(children.item(i), geo);
					break;
				} else if (eName.equals("slider")) {
					ok = handleSlider(children.item(i), geo);
					break;
				} else if (eName.equals("startPoint")) {
					ok = handleStartPoint(children.item(i), geo);
					break;
				}
			case 'v':
				if (eName.equals("value")) {
					ok = handleValue(children.item(i), geo);
					break;
				}
			default:
				System.err.println("unknown tag in <element>: " + eName);
			}

			if (!ok) {
				System.err.println("error in <element>: " + eName);
			}
			
		}


		
		// TODO Auto-generated method stub
		return geo;
	}
	
	private boolean handleAllowReflexAngle(Node item, GeoElement geoElement) {
		if (!(geoElement.isGeoAngle())) {
			System.err.println("wrong element type for <allowReflexAngle>: "
					+ geoElement.getClass());
			return false;
		}

		try {
			GeoAngle angle = (GeoAngle) geoElement;
			angle.setAllowReflexAngle(parseBoolean((String) getNodeAttr(item.getAttributes().getNamedItem("val"))));
			return true;
		} catch (Exception e) {

			return false;
		}
	}

	private boolean handleBreakpoint(Node item, GeoElement geoElement) {
		try {
			geoElement.setConsProtocolBreakpoint(parseBoolean((String) getNodeAttr(item.getAttributes().getNamedItem("val"))));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleLayer(Node item, GeoElement geoElement) {

		try {
			geoElement.setLayer(Integer.parseInt((String) getNodeAttr(item.getAttributes().getNamedItem("val"))));
			return true;
		} catch (Exception e) {
			GWT.log(e.getMessage());
			return false;
		}
	}

	private boolean handleLabelOffset(Node item, GeoElement geoElement) {
		try {
			geoElement.labelOffsetX = Integer.parseInt((String) getNodeAttr(item.getAttributes().getNamedItem("x")));
			geoElement.labelOffsetY = Integer.parseInt((String) getNodeAttr(item.getAttributes().getNamedItem("y")));

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleFixed(Node item, GeoElement geoElement) {
		try {
			geoElement.setFixed(parseBoolean((String) getNodeAttr(item.getAttributes().getNamedItem("val"))));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleCondition(Node item, GeoElement geoElement) {
		try {
			// condition for visibility of object
			String strShowObjectCond = (String) getNodeAttr(item.getAttributes().getNamedItem("showObject"));
			if (strShowObjectCond != null) {
				// store (geo, epxression) values
				// they will be processed in processShowObjectConditionList()
				// later
				showObjectConditionList.add(new GeoExpPair(geoElement,
						strShowObjectCond));
			}

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleCaption(Node item, GeoElement geoElement) {
		try {
			geoElement.setCaption((String) getNodeAttr(item.getAttributes().getNamedItem("val")));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleCoordStyle(Node item, GeoElement geoElement) {
		if (!(geoElement.isGeoPoint() || geoElement.isGeoVector())) {
			System.err.println("wrong element type for <coordStyle>: "
					+ geoElement.getClass());
			return false;
		}
		GeoVec3D v = (GeoVec3D) geoElement;
		String style = (String) getNodeAttr(item.getAttributes().getNamedItem("style"));
		if (style.equals("cartesian")) {
			v.setCartesian();
		} else if (style.equals("polar")) {
			v.setPolar();
		} else if (style.equals("complex")) {
			v.setComplex();
		} else {
			System.err.println("unknown style in <coordStyle>: " + style);
			return false;
		}
		return true;
	}

	private boolean handleAbsoluteScreenLocation(Node item, GeoElement geoElement) {
		if (!(geoElement instanceof AbsoluteScreenLocateable)) {
			Application
					.debug("wrong element type for <absoluteScreenLocation>: "
							+ geoElement.getClass());
			return false;
		}

		try {
			AbsoluteScreenLocateable absLoc = (AbsoluteScreenLocateable) geoElement;
			int x = Integer.parseInt((String) getNodeAttr(item.getAttributes().getNamedItem("x")));
			int y = Integer.parseInt((String) getNodeAttr(item.getAttributes().getNamedItem("y")));
			absLoc.setAbsoluteScreenLoc(x, y);
			absLoc.setAbsoluteScreenLocActive(true);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleAuxiliary(Node item, GeoElement geoElement) {
		try {
			geoElement.setAuxiliaryObject(parseBoolean((String) getNodeAttr(item.getAttributes().getNamedItem("val"))));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleOutlyingIntersections(Node item, GeoElement geoElement) {
		if (!(geoElement instanceof LimitedPath)) {
			Application
					.debug("wrong element type for <outlyingIntersections>: "
							+ geoElement.getClass());
			return false;
		}

		try {
			LimitedPath lpath = (LimitedPath) geoElement;
			lpath.setAllowOutlyingIntersections(parseBoolean((String) getNodeAttr(item.getAttributes().getNamedItem("val"))));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleMatrix(Node item, GeoElement geoElement) {
		if (!(geoElement.isGeoConic()) && !(geoElement.isGeoCubic())) {
			System.err.println("wrong element type for <matrix>: "
					+ geoElement.getClass());
			return false;
		}
		try {
			if (geoElement.isGeoConic()) {
			GeoConic conic = (GeoConic) geoElement;
			// set matrix and classify conic now
			// <eigenvectors> should have been set earlier
			double[] matrix = { Double.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("A0"))),
					Double.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("A1"))),
					Double.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("A2"))),
					Double.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("A3"))),
					Double.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("A4"))),
					Double.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("A5"))) };
			conic.setMatrix(matrix);
			} else {
				GeoCubic cubic = (GeoCubic) geoElement;
				double[] coefficients = { Double.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("A0"))),
						Double.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("A1"))),
						Double.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("A2"))),
						Double.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("A3"))),
						Double.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("A4"))),
						Double.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("A5"))),
						Double.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("A6"))),
						Double.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("A7"))),
						Double.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("A8"))),
						Double.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("A9"))),
						Double.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("A10"))),
						Double.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("A11"))),
						Double.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("A12"))),
						Double.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("A13"))),
						Double.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("A14"))),
						Double.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("A15"))) };
				cubic.setCoeffs(coefficients);				
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleEigenvectors(Node item, GeoElement geoElement) {
		if (!(geoElement.isGeoConic())) {
			System.err.println("wrong element type for <eigenvectors>: "
					+ geoElement.getClass());
			return false;
		}
		try {
			GeoConic conic = (GeoConic) geoElement;
			// set eigenvectors, but don't classify conic now
			// classifyConic() will be called in handleMatrix() by
			// conic.setMatrix()
			conic.setEigenvectors(Double.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("x0"))),
					Double.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("y0"))), Double
							.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("z0"))), Double
							.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("x1"))), Double
							.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("y1"))), Double
							.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("z1"))));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleEqnStyle(Node item, GeoElement geoElement) {
		// line
		if (geoElement.isGeoLine()) {
			GeoLine line = (GeoLine) geoElement;
			String style = (String) getNodeAttr(item.getAttributes().getNamedItem("style"));
			if (style.equals("implicit")) {
				line.setToImplicit();
			} else if (style.equals("explicit")) {
				line.setToExplicit();
			} else if (style.equals("parametric")) {
				String parameter = (String) getNodeAttr(item.getAttributes().getNamedItem("parameter"));
				line.setToParametric(parameter);
			} else {
				System.err.println("unknown style for line in <eqnStyle>: "
						+ style);
				return false;
			}
		}
		// conic
		else if (geoElement.isGeoConic()) {
			GeoConic conic = (GeoConic) geoElement;
			String style = (String) getNodeAttr(item.getAttributes().getNamedItem("style"));
			if (style.equals("implicit")) {
				conic.setToImplicit();
			} else if (style.equals("specific")) {
				conic.setToSpecific();
			} else if (style.equals("explicit")) {
				conic.setToExplicit();
			} else {
				System.err.println("unknown style for conic in <eqnStyle>: "
						+ style);
				return false;
			}
		} else {
			System.err.println("wrong element type for <eqnStyle>: "
					+ geoElement.getClass());
			return false;
		}
		return true;
	}

	private boolean handleLineStyle(Node item, GeoElement geoElement) {
		try {
			geoElement.setLineType(Integer.parseInt((String) getNodeAttr(item.getAttributes().getNamedItem("type"))));			
			geoElement.setLineThickness(Integer.parseInt((String) getNodeAttr(item.getAttributes().getNamedItem("thickness"))));						
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleKeepTypeOnTransform(Node item, GeoElement geoElement) {
		if (!(geoElement instanceof LimitedPath)) {
			Application
					.debug("wrong element type for <outlyingIntersections>: "
							+ geoElement.getClass());
			return false;
		}

		try {
			LimitedPath lpath = (LimitedPath) geoElement;
			lpath.setKeepTypeOnGeometricTransform(parseBoolean((String) getNodeAttr(item.getAttributes().getNamedItem("val"))));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleLabelMode(Node item, GeoElement geoElement) {
		try {
			geoElement.setLabelMode(Integer.parseInt((String) getNodeAttr(item.getAttributes().getNamedItem("val"))));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleTextFont(Node item, GeoElement geoElement) {
		if (!(geoElement instanceof TextProperties)) {
			System.err.println("wrong element type for <font>: "
					+ geoElement.getClass());
			return false;
		}

		try {
			TextProperties text = (TextProperties) geoElement;
			text.setSerifFont(parseBoolean((String) getNodeAttr(item.getAttributes().getNamedItem("serif"))));
			text.setFontSize(Integer.parseInt((String) getNodeAttr(item.getAttributes().getNamedItem("size"))));
			text.setFontStyle(Integer.parseInt((String) getNodeAttr(item.getAttributes().getNamedItem("style"))));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleAnimation(Node item, GeoElement geoElement) {
		try {
			geoElement.setAnimationStep(Double.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("step"))));
			
			String strSpeed = (String) getNodeAttr(item.getAttributes().getNamedItem("speed"));
			if (strSpeed != null) {
				// store speed expression to be processed later
				animationSpeedList.add(new GeoExpPair(geoElement, strSpeed));			
			}
				
			String type = (String) getNodeAttr(item.getAttributes().getNamedItem("type"));
			if (type != null)
				geoElement.setAnimationType(Integer.parseInt(type));
			
			
			geoElement.setAnimating(parseBoolean((String) getNodeAttr(item.getAttributes().getNamedItem("playing"))));
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handlePointStyle(Node item, GeoElement geoElement) {
		if (!(geoElement instanceof PointProperties)) {
			Application.debug("wrong element type for <pointStyle>: "
					+ geoElement.getClass());
			return false;
		}

		try {
			PointProperties p = (PointProperties) geoElement;
			
			int style = Integer.parseInt(item.getAttributes().getNamedItem("val").getNodeValue());
			
			/*if(style == -1) {
				style = docPointStyle;
			}*/
			p.setPointStyle(style);
			return true;
		} catch (Exception e) {
			return false;
		}
		
	}
	
	private boolean handleObjColor(Node item, GeoElement geoElement) {
	//	GWT.log(geoElement.toString());
		LinkedHashMap<String, String> attrs = new LinkedHashMap<String, String>();
		attrs.put("r", getNodeAttr(item.getAttributes().getNamedItem("r")));
		attrs.put("g", getNodeAttr(item.getAttributes().getNamedItem("g")));
		attrs.put("b", getNodeAttr(item.getAttributes().getNamedItem("b")));
		attrs.put("alpha", getNodeAttr(item.getAttributes().getNamedItem("alpha")));
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
	
	
	

	private boolean handlePointSize(Node item, GeoElement geoElement) {
		// TODO Auto-generated method stub
		//GWT.log("pointSize");
		if (!(geoElement instanceof PointProperties)) {
			Application.debug("wrong element type for <pointSize>: "
					+ geoElement.getClass());
			return false;
		}

		try {
			PointProperties p = (PointProperties) geoElement;
			p.setPointSize(Integer.parseInt(getNodeAttr(item.getAttributes().getNamedItem("val"))));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean handleShow(Node item, GeoElement geoElement) {
		// TODO Auto-generated method stub
		//GWT.log("handleshow");
		
		try {
		//GWT.log(String.valueOf(parseBoolean(item.getAttributes().getNamedItem("object").getNodeValue())));
		//GWT.log(String.valueOf(parseBoolean(item.getAttributes().getNamedItem("label").getNodeValue())));
		geoElement.setEuclidianVisible(parseBoolean(getNodeAttr(item.getAttributes().getNamedItem("object"))));
		geoElement.setLabelVisible(parseBoolean(getNodeAttr(item.getAttributes().getNamedItem("label"))));
		return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean handleCoords(Node item, GeoElement geoElement) {
		//GWT.log("handleChoords");
		LinkedHashMap<String, String> attrs = new LinkedHashMap<String, String>();
		attrs.put("x", getNodeAttr(item.getAttributes().getNamedItem("x")));
		attrs.put("y", getNodeAttr(item.getAttributes().getNamedItem("y")));
		attrs.put("z", getNodeAttr(item.getAttributes().getNamedItem("z")));
		//GWT.log(item.getAttributes().getNamedItem("x").getNodeValue());
		//GWT.log(item.getAttributes().getNamedItem("y").getNodeValue());
		//GWT.log(item.getAttributes().getNamedItem("z").getNodeValue());
		return kernel.handleCoords(geoElement, attrs);
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
	
	private boolean handleValue(Node item, GeoElement geoElement) {
		boolean isBoolean = geoElement.isGeoBoolean();
		boolean isNumber = geoElement.isGeoNumeric();
		boolean isButton = geoElement.isGeoButton();

		if (!(isNumber || isBoolean || isButton)) {
			Application.debug("wrong element type for <value>: "
					+ geoElement.getClass());
			return false;
		}

		try {
			String strVal = (String) getNodeAttr(item.getAttributes().getNamedItem("val"));
			if (isNumber) {
				GeoNumeric n = (GeoNumeric) geoElement;
				n.setValue(Double.parseDouble(strVal));
			} else if (isBoolean) {
				GeoBoolean bool = (GeoBoolean) geoElement;
				bool.setValue(parseBoolean(strVal));
			} else if (isButton) {
				GeoButton button = (GeoButton) geoElement;
				button.setJavaScript(strVal);
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	
//////////////////////////////EJ from here
	/**
	 * Start Points have to be handled at the end of the construction, because
	 * they could depend on objects that are defined after this GeoElement.
	 * 
	 * So we store all (geo, startpoint expression) pairs and process them at
	 * the end of the construction.
	 * 
	 * @see processStartPointList
	 */
	private boolean handleStartPoint(Node item, GeoElement geoElement) {
		if (!(geoElement instanceof Locateable)) {
			System.err.println("wrong element type for <startPoint>: "
					+ geoElement.getClass());
			return false;
		}
		Locateable loc = (Locateable) geoElement;

		// relative start point (expression or label expected)
		String exp = (String) getNodeAttr(item.getAttributes().getNamedItem("exp"));
		if (exp == null) // try deprecated attribute
			exp = (String) getNodeAttr(item.getAttributes().getNamedItem("label"));

		// for corners a number of the startPoint is given
		int number = 0;
		try {
			number = Integer.parseInt((String) getNodeAttr(item.getAttributes().getNamedItem("number")));
		} catch (Exception e) {
		}

		if (exp != null) {
			// store (geo, epxression, number) values
			// they will be processed in processStartPoints() later
			startPointList.add(new LocateableExpPair(loc, exp, number));	
			loc.setWaitForStartPoint();
		}
		else {
			// absolute start point (coords expected)
			try {
				/*
				double x = Double.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("x")));
				double y = Double.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("y")));
				double z = Double.parseDouble((String) getNodeAttr(item.getAttributes().getNamedItem("z")));
				GeoPoint p = new GeoPoint(cons);
				p.setCoords(x, y, z);
				*/
				
				LinkedHashMap<String, String> attrs = new LinkedHashMap<String, String>();
				attrs.put("x", getNodeAttr(item.getAttributes().getNamedItem("x")));
				attrs.put("y", getNodeAttr(item.getAttributes().getNamedItem("y")));
				attrs.put("z", getNodeAttr(item.getAttributes().getNamedItem("z")));
				
				GeoPointInterface p = handleAbsoluteStartPoint(attrs);
				
				if (number == 0) {
					// set first start point right away
					loc.setStartPoint(p);
				} else {
					// set other start points later
					// store (geo, point, number) values
					// they will be processed in processStartPoints() later
					startPointList.add(new LocateableExpPair(loc, p, number));	
					loc.setWaitForStartPoint();
				}				
			} catch (Exception e) {
				return false;
			}
		}
		
		return true;
	}
	
	/** create absolute start point (coords expected) */
	protected GeoPointInterface handleAbsoluteStartPoint(LinkedHashMap<String, String> attrs) {
		double x = Double.parseDouble((String) attrs.get("x"));
		double y = Double.parseDouble((String) attrs.get("y"));
		double z = Double.parseDouble((String) attrs.get("z"));
		GeoPoint p = new GeoPoint(cons);
		p.setCoords(x, y, z);
		return p;
	}
	
	public void setApplication(Application app) {
		this.app = app;
	}
///////////////////////////////////////////////////////EJ ends here	
	//utils
	
	protected boolean parseBoolean(String str) throws Exception {
		return "true".equals(str);
	}
	
	private void processStartPointList() {
		try {
			Iterator it = startPointList.iterator();
			AlgebraProcessor algProc = kernel.getAlgebraProcessor();

			while (it.hasNext()) {
				LocateableExpPair pair = (LocateableExpPair) it.next();
				GeoPointInterface P = pair.point != null ? pair.point : 
								algProc.evaluateToPoint(pair.exp);
				pair.locateable.setStartPoint(P, pair.number);
				
				//Application.debug("locateable : "+ ((GeoElement) pair.locateable).getLabel() + ", startPoint : "+((GeoElement) P).getLabel());
			}
		} catch (Exception e) {
			startPointList.clear();
			e.printStackTrace();
			throw new MyError(app, "processStartPointList: " + e.toString());
		}
		startPointList.clear();
	}
	
	private void processShowObjectConditionList() {
		try {
			Iterator it = showObjectConditionList.iterator();
			AlgebraProcessor algProc = kernel.getAlgebraProcessor();

			while (it.hasNext()) {
				GeoExpPair pair = (GeoExpPair) it.next();
				GeoBoolean condition = algProc.evaluateToBoolean(pair.exp);
				pair.geo.setShowObjectCondition(condition);
			}
		} catch (Exception e) {
			showObjectConditionList.clear();
			e.printStackTrace();
			throw new MyError(app, "processShowObjectConditionList: "
					+ e.toString());
		}
		showObjectConditionList.clear();
	}
	

	private void processAnimationSpeedList() {
		try {
			Iterator it = animationSpeedList.iterator();
			AlgebraProcessor algProc = kernel.getAlgebraProcessor();

			while (it.hasNext()) {
				GeoExpPair pair = (GeoExpPair) it.next();
				NumberValue num = algProc.evaluateToNumeric(pair.exp, false);
				pair.geo.setAnimationSpeedObject(num);
			}
		} catch (Exception e) {
			animationSpeedList.clear();
			e.printStackTrace();
			throw new MyError(app, "processAnimationSpeedList: " + e.toString());
		}
		animationSpeedList.clear();
	}
	
	/*AGprivate void processLinkedGeoList() {
		try {
			Iterator<GeoExpPair> it = linkedGeoList.iterator();

			while (it.hasNext()) {
				GeoExpPair pair = (GeoExpPair) it.next();
				
				((GeoTextField)pair.geo).setLinkedGeo(kernel.lookupLabel(pair.exp));
			}
		} catch (Exception e) {
			linkedGeoList.clear();
			e.printStackTrace();
			throw new MyError(app, "processlinkedGeoList: " + e.toString());
		}
		linkedGeoList.clear();
	}*/
	
	// Michael Borcherds 2008-05-18
	private void processDynamicColorList() {
		try {
			Iterator it = dynamicColorList.iterator();
			AlgebraProcessor algProc = kernel.getAlgebraProcessor();

			while (it.hasNext()) {
				GeoExpPair pair = (GeoExpPair) it.next();
				pair.geo.setColorFunction(algProc.evaluateToList(pair.exp));
			}
		} catch (Exception e) {
			dynamicColorList.clear();
			e.printStackTrace();
			throw new MyError(app, "dynamicColorList: " + e.toString());
		}
		dynamicColorList.clear();
	}



	
}
