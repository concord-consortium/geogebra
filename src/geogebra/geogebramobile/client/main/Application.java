package geogebra.geogebramobile.client.main;

import geogebra.geogebramobile.client.euclidian.EuclidianController;
import geogebra.geogebramobile.client.euclidian.EuclidianView;
import geogebra.geogebramobile.client.gui.Base64Form;
import geogebra.geogebramobile.client.gui.GgjsViewerWrapper;
import geogebra.geogebramobile.client.io.MyXMLHandler;
import geogebra.geogebramobile.client.kernel.BaseApplication;
import geogebra.geogebramobile.client.kernel.GeoBoolean;
import geogebra.geogebramobile.client.kernel.GeoElement;
import geogebra.geogebramobile.client.kernel.GeoLine;
import geogebra.geogebramobile.client.kernel.GeoPoint;
import geogebra.geogebramobile.client.kernel.Kernel;
import geogebra.geogebramobile.client.kernel.Macro;
import geogebra.geogebramobile.client.kernel.arithmetic.ExpressionNode;
import geogebra.geogebramobile.client.kernel.gawt.Font;
import geogebra.geogebramobile.client.plugin.GgbAPI;
import geogebra.geogebramobile.client.service.JsonHandler;
import geogebra.geogebramobile.client.util.Base64;
import geogebra.geogebramobile.client.util.LowerCaseDictionary;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;


import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

public class Application extends BaseApplication {
	
	private Kernel kernel;
	private EuclidianView euclidianview;
	private EuclidianController euclidiancontroller;
	private MyXMLHandler xmlhandler;
	//private Hashtable translateCommandTable;
	private LowerCaseDictionary commandDict;
	private boolean showResetIcon = false;
	public boolean runningInFrame = false; // don't want to show resetIcon if running in Frame

	// determines which CAS is being used
	final public static int CAS_MATHPIPER = ExpressionNode.STRING_TYPE_MATH_PIPER;
	final public static int CAS_MAXIMA = ExpressionNode.STRING_TYPE_MAXIMA;
	public static final String unicodeComma = ".";
	
	protected boolean showMenuBar = true;
	
	private ArrayList<GeoElement> selectedGeos = new ArrayList<GeoElement>();
	private GgbAPI ggbapi;
	
	
	public Application() {
		super();
		int w = EuclidianView.DEFAULT_WIDTH;
		int h = EuclidianView.DEFAULT_HEIGHT;
		String decodedBase64String = "";
		String defaultJsString = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\""+
				"\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"+
		"<html \"xmlns=\"http://www.w3.org/1999/xhtml\">"+
		"<head>"+
		"<title>"+
		"</title>"+
		"<body>"+
		"<script type=\"text/javascript\">"+
		"loadBase64Unzipped('PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4KPGdlb2dlYnJhIGZvcm1hdD0iMy4zIiB4c2k6bm9OYW1lc3BhY2VTY2hlbWFMb2NhdGlvbj0iaHR0cDovL3d3dy5nZW9nZWJyYS5vcmcvZ2diLnhzZCIgeG1sbnM9IiIgeG1sbnM6eHNpPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxL1hNTFNjaGVtYS1pbnN0YW5jZSIgPgo8Z3VpPgoJPHdpbmRvdyB3aWR0aD0iODAwIiBoZWlnaHQ9IjYwMCIgLz4JPHBlcnNwZWN0aXZlcz4KPHBlcnNwZWN0aXZlIGlkPSJ0bXAiPgoJPHBhbmVzPgoJCTxwYW5lIGxvY2F0aW9uPSIiIGRpdmlkZXI9IjAuMjUiIG9yaWVudGF0aW9uPSIxIiAvPgoJPC9wYW5lcz4KCTx2aWV3cz4KCQk8dmlldyBpZD0iMSIgdmlzaWJsZT0idHJ1ZSIgaW5mcmFtZT0iZmFsc2UiIGxvY2F0aW9uPSIxIiBzaXplPSI1ODgiIHdpbmRvdz0iMTAwLDEwMCw2MDAsNDAwIiAvPgoJCTx2aWV3IGlkPSIyIiB2aXNpYmxlPSJ0cnVlIiBpbmZyYW1lPSJmYWxzZSIgbG9jYXRpb249IjMiIHNpemU9IjIwMCIgd2luZG93PSIxMDAsMTAwLDI1MCw0MDAiIC8+CgkJPHZpZXcgaWQ9IjQiIHZpc2libGU9ImZhbHNlIiBpbmZyYW1lPSJmYWxzZSIgbG9jYXRpb249IiIgc2l6ZT0iMzAwIiB3aW5kb3c9IjEwMCwxMDAsNjAwLDQwMCIgLz4KCQk8dmlldyBpZD0iOCIgdmlzaWJsZT0iZmFsc2UiIGluZnJhbWU9ImZhbHNlIiBsb2NhdGlvbj0iIiBzaXplPSIzMDAiIHdpbmRvdz0iMTAwLDEwMCw2MDAsNDAwIiAvPgoJPC92aWV3cz4KCTx0b29sYmFyPjAgMzkgNTkgfHwgMSA1MDEgNSAxOSB8IDIgMTUgNDUgMTggLCA3IDM3IHwgNCAzIDggOSAsIDEzIDQ0ICwgNTggLCA0NyB8fCAxNiA1MSB8IDEwIDM0IDUzIDExICwgMjQgIDIwIDIyICwgMjEgMjMgfCA1NSA1NiA1NyAsIDEyIHx8IDM2IDQ2ICwgMzggNDkgNTAgfCAzMCAyOSA1NCAzMiAzMSAzMyB8IDI1IDUyIDYwIDYxICwgNjIgLCA2MyAsIDE3IDI2ICwgMTQgfHwgNDAgNDEgNDIgLCAyNyAyOCAzNSAsIDY8L3Rvb2xiYXI+Cgk8c2hvdyBheGVzPSJ0cnVlIiBncmlkPSJmYWxzZSIgLz4KCTxpbnB1dCBzaG93PSJ0cnVlIiBjbWQ9InRydWUiIHRvcD0iZmFsc2UiIC8+CjwvcGVyc3BlY3RpdmU+Cgk8L3BlcnNwZWN0aXZlcz4KPC9ndWk+CjxldWNsaWRpYW5WaWV3PgoJPHNpemUgIHdpZHRoPSI1ODgiIGhlaWdodD0iNDQxIi8+Cgk8Y29vcmRTeXN0ZW0geFplcm89IjIxNS4wIiB5WmVybz0iMzE1LjAiIHNjYWxlPSI1MC4wIiB5c2NhbGU9IjUwLjAiLz4KCTxldlNldHRpbmdzIGF4ZXM9InRydWUiIGdyaWQ9ImZhbHNlIiBncmlkSXNCb2xkPSJmYWxzZSIgcG9pbnRDYXB0dXJpbmc9IjMiIHJpZ2h0QW5nbGVTdHlsZT0iMSIgY2hlY2tib3hTaXplPSIxMyIgZ3JpZFR5cGU9IjAiLz4KCTxiZ0NvbG9yIHI9IjI1NSIgZz0iMjU1IiBiPSIyNTUiLz4KCTxheGVzQ29sb3Igcj0iMCIgZz0iMCIgYj0iMCIvPgoJPGdyaWRDb2xvciByPSIxOTIiIGc9IjE5MiIgYj0iMTkyIi8+Cgk8bGluZVN0eWxlIGF4ZXM9IjEiIGdyaWQ9IjEwIi8+Cgk8YXhpcyBpZD0iMCIgc2hvdz0idHJ1ZSIgbGFiZWw9IiIgdW5pdExhYmVsPSIiIHRpY2tTdHlsZT0iMSIgc2hvd051bWJlcnM9InRydWUiLz4KCTxheGlzIGlkPSIxIiBzaG93PSJ0cnVlIiBsYWJlbD0iIiB1bml0TGFiZWw9IiIgdGlja1N0eWxlPSIxIiBzaG93TnVtYmVycz0idHJ1ZSIvPgo8L2V1Y2xpZGlhblZpZXc+CjxrZXJuZWw+Cgk8Y29udGludW91cyB2YWw9ImZhbHNlIi8+Cgk8ZGVjaW1hbHMgdmFsPSIyIi8+Cgk8YW5nbGVVbml0IHZhbD0iZGVncmVlIi8+Cgk8YWxnZWJyYVN0eWxlIHZhbD0iMCIvPgoJPGNvb3JkU3R5bGUgdmFsPSIwIi8+Cjwva2VybmVsPgo8Y29uc3RydWN0aW9uIHRpdGxlPSIiIGF1dGhvcj0iIiBkYXRlPSIiPgo8ZWxlbWVudCB0eXBlPSJwb2ludCIgbGFiZWw9IkEiPgoJPHNob3cgb2JqZWN0PSJ0cnVlIiBsYWJlbD0idHJ1ZSIvPgoJPG9iakNvbG9yIHI9IjAiIGc9IjAiIGI9IjI1NSIgYWxwaGE9IjAuMCIvPgoJPGxheWVyIHZhbD0iMCIvPgoJPGxhYmVsTW9kZSB2YWw9IjAiLz4KCTxhbmltYXRpb24gc3RlcD0iMC4xIiBzcGVlZD0iMSIgdHlwZT0iMCIgcGxheWluZz0iZmFsc2UiLz4KCTxjb29yZHMgeD0iMy4wIiB5PSIzLjAiIHo9IjEuMCIvPgoJPHBvaW50U2l6ZSB2YWw9IjMiLz4KCTxwb2ludFN0eWxlIHZhbD0iMCIvPgo8L2VsZW1lbnQ+CjxlbGVtZW50IHR5cGU9InBvaW50IiBsYWJlbD0iQiI+Cgk8c2hvdyBvYmplY3Q9InRydWUiIGxhYmVsPSJ0cnVlIi8+Cgk8b2JqQ29sb3Igcj0iMCIgZz0iMCIgYj0iMjU1IiBhbHBoYT0iMC4wIi8+Cgk8bGF5ZXIgdmFsPSIwIi8+Cgk8bGFiZWxNb2RlIHZhbD0iMCIvPgoJPGFuaW1hdGlvbiBzdGVwPSIwLjEiIHNwZWVkPSIxIiB0eXBlPSIwIiBwbGF5aW5nPSJmYWxzZSIvPgoJPGNvb3JkcyB4PSItMC4xOCIgeT0iNC40IiB6PSIxLjAiLz4KCTxwb2ludFNpemUgdmFsPSIzIi8+Cgk8cG9pbnRTdHlsZSB2YWw9IjAiLz4KPC9lbGVtZW50Pgo8ZWxlbWVudCB0eXBlPSJwb2ludCIgbGFiZWw9IkMiPgoJPHNob3cgb2JqZWN0PSJ0cnVlIiBsYWJlbD0idHJ1ZSIvPgoJPG9iakNvbG9yIHI9IjAiIGc9IjAiIGI9IjI1NSIgYWxwaGE9IjAuMCIvPgoJPGxheWVyIHZhbD0iMCIvPgoJPGxhYmVsTW9kZSB2YWw9IjAiLz4KCTxhbmltYXRpb24gc3RlcD0iMC4xIiBzcGVlZD0iMSIgdHlwZT0iMCIgcGxheWluZz0iZmFsc2UiLz4KCTxjb29yZHMgeD0iLTEuMDIiIHk9Ii0wLjQ0IiB6PSIxLjAiLz4KCTxwb2ludFNpemUgdmFsPSIzIi8+Cgk8cG9pbnRTdHlsZSB2YWw9IjAiLz4KPC9lbGVtZW50Pgo8Y29tbWFuZCBuYW1lPSJQb2x5Z29uIj4KCTxpbnB1dCBhMD0iQSIgYTE9IkIiIGEyPSJDIi8+Cgk8b3V0cHV0IGEwPSJwb2x5MSIgYTE9ImMiIGEyPSJhIiBhMz0iYiIvPgo8L2NvbW1hbmQ+CjxlbGVtZW50IHR5cGU9InBvbHlnb24iIGxhYmVsPSJwb2x5MSI+Cgk8c2hvdyBvYmplY3Q9InRydWUiIGxhYmVsPSJmYWxzZSIvPgoJPG9iakNvbG9yIHI9IjE1MyIgZz0iNTEiIGI9IjAiIGFscGhhPSIwLjEiLz4KCTxsYXllciB2YWw9IjAiLz4KCTxsYWJlbE1vZGUgdmFsPSIwIi8+CjwvZWxlbWVudD4KPGVsZW1lbnQgdHlwZT0ic2VnbWVudCIgbGFiZWw9ImMiPgoJPHNob3cgb2JqZWN0PSJ0cnVlIiBsYWJlbD0idHJ1ZSIvPgoJPG9iakNvbG9yIHI9IjE1MyIgZz0iNTEiIGI9IjAiIGFscGhhPSIwLjAiLz4KCTxsYXllciB2YWw9IjAiLz4KCTxsYWJlbE1vZGUgdmFsPSIwIi8+Cgk8Y29vcmRzIHg9Ii0xLjQwMDAwMDAwMDAwMDAwMDQiIHk9Ii0zLjE4IiB6PSIxMy43NDAwMDAwMDAwMDAwMDIiLz4KCTxsaW5lU3R5bGUgdGhpY2tuZXNzPSIyIiB0eXBlPSIwIi8+Cgk8ZXFuU3R5bGUgc3R5bGU9ImltcGxpY2l0Ii8+Cgk8b3V0bHlpbmdJbnRlcnNlY3Rpb25zIHZhbD0iZmFsc2UiLz4KCTxrZWVwVHlwZU9uVHJhbnNmb3JtIHZhbD0idHJ1ZSIvPgo8L2VsZW1lbnQ+CjxlbGVtZW50IHR5cGU9InNlZ21lbnQiIGxhYmVsPSJhIj4KCTxzaG93IG9iamVjdD0idHJ1ZSIgbGFiZWw9InRydWUiLz4KCTxvYmpDb2xvciByPSIxNTMiIGc9IjUxIiBiPSIwIiBhbHBoYT0iMC4wIi8+Cgk8bGF5ZXIgdmFsPSIwIi8+Cgk8bGFiZWxNb2RlIHZhbD0iMCIvPgoJPGNvb3JkcyB4PSI0Ljg0MDAwMDAwMDAwMDAwMSIgeT0iLTAuODQwMDAwMDAwMDAwMDAwMSIgej0iNC41NjcyMDAwMDAwMDAwMDEiLz4KCTxsaW5lU3R5bGUgdGhpY2tuZXNzPSIyIiB0eXBlPSIwIi8+Cgk8ZXFuU3R5bGUgc3R5bGU9ImltcGxpY2l0Ii8+Cgk8b3V0bHlpbmdJbnRlcnNlY3Rpb25zIHZhbD0iZmFsc2UiLz4KCTxrZWVwVHlwZU9uVHJhbnNmb3JtIHZhbD0idHJ1ZSIvPgo8L2VsZW1lbnQ+CjxlbGVtZW50IHR5cGU9InNlZ21lbnQiIGxhYmVsPSJiIj4KCTxzaG93IG9iamVjdD0idHJ1ZSIgbGFiZWw9InRydWUiLz4KCTxvYmpDb2xvciByPSIxNTMiIGc9IjUxIiBiPSIwIiBhbHBoYT0iMC4wIi8+Cgk8bGF5ZXIgdmFsPSIwIi8+Cgk8bGFiZWxNb2RlIHZhbD0iMCIvPgoJPGNvb3JkcyB4PSItMy40NCIgeT0iNC4wMiIgej0iLTEuNzQiLz4KCTxsaW5lU3R5bGUgdGhpY2tuZXNzPSIyIiB0eXBlPSIwIi8+Cgk8ZXFuU3R5bGUgc3R5bGU9ImltcGxpY2l0Ii8+Cgk8b3V0bHlpbmdJbnRlcnNlY3Rpb25zIHZhbD0iZmFsc2UiLz4KCTxrZWVwVHlwZU9uVHJhbnNmb3JtIHZhbD0idHJ1ZSIvPgo8L2VsZW1lbnQ+Cjxjb21tYW5kIG5hbWU9IkxpbmVCaXNlY3RvciI+Cgk8aW5wdXQgYTA9IkIiIGExPSJBIi8+Cgk8b3V0cHV0IGEwPSJkIi8+CjwvY29tbWFuZD4KPGVsZW1lbnQgdHlwZT0ibGluZSIgbGFiZWw9ImQiPgoJPHNob3cgb2JqZWN0PSJ0cnVlIiBsYWJlbD0idHJ1ZSIvPgoJPG9iakNvbG9yIHI9IjAiIGc9IjAiIGI9IjAiIGFscGhhPSIwLjAiLz4KCTxsYXllciB2YWw9IjAiLz4KCTxsYWJlbE1vZGUgdmFsPSIwIi8+Cgk8Y29vcmRzIHg9Ii0zLjE4IiB5PSIxLjQwMDAwMDAwMDAwMDAwMDQiIHo9Ii0wLjY5NjIwMDAwMDAwMDAwMTkiLz4KCTxsaW5lU3R5bGUgdGhpY2tuZXNzPSIyIiB0eXBlPSIwIi8+Cgk8ZXFuU3R5bGUgc3R5bGU9ImltcGxpY2l0Ii8+CjwvZWxlbWVudD4KPGNvbW1hbmQgbmFtZT0iTGluZUJpc2VjdG9yIj4KCTxpbnB1dCBhMD0iQSIgYTE9IkMiLz4KCTxvdXRwdXQgYTA9ImUiLz4KPC9jb21tYW5kPgo8ZWxlbWVudCB0eXBlPSJsaW5lIiBsYWJlbD0iZSI+Cgk8c2hvdyBvYmplY3Q9InRydWUiIGxhYmVsPSJ0cnVlIi8+Cgk8b2JqQ29sb3Igcj0iMCIgZz0iMCIgYj0iMCIgYWxwaGE9IjAuMCIvPgoJPGxheWVyIHZhbD0iMCIvPgoJPGxhYmVsTW9kZSB2YWw9IjAiLz4KCTxjb29yZHMgeD0iNC4wMiIgeT0iMy40NCIgej0iLTguMzgzIi8+Cgk8bGluZVN0eWxlIHRoaWNrbmVzcz0iMiIgdHlwZT0iMCIvPgoJPGVxblN0eWxlIHN0eWxlPSJpbXBsaWNpdCIvPgo8L2VsZW1lbnQ+Cjxjb21tYW5kIG5hbWU9IkxpbmVCaXNlY3RvciI+Cgk8aW5wdXQgYTA9IkIiIGExPSJDIi8+Cgk8b3V0cHV0IGEwPSJmIi8+CjwvY29tbWFuZD4KPGVsZW1lbnQgdHlwZT0ibGluZSIgbGFiZWw9ImYiPgoJPHNob3cgb2JqZWN0PSJ0cnVlIiBsYWJlbD0idHJ1ZSIvPgoJPG9iakNvbG9yIHI9IjAiIGc9IjAiIGI9IjAiIGFscGhhPSIwLjAiLz4KCTxsYXllciB2YWw9IjAiLz4KCTxsYWJlbE1vZGUgdmFsPSIwIi8+Cgk8Y29vcmRzIHg9IjAuODQwMDAwMDAwMDAwMDAwMSIgeT0iNC44NDAwMDAwMDAwMDAwMDEiIHo9Ii05LjA3OTIwMDAwMDAwMDAwNCIvPgoJPGxpbmVTdHlsZSB0aGlja25lc3M9IjIiIHR5cGU9IjAiLz4KCTxlcW5TdHlsZSBzdHlsZT0iaW1wbGljaXQiLz4KPC9lbGVtZW50Pgo8Y29tbWFuZCBuYW1lPSJJbnRlcnNlY3QiPgoJPGlucHV0IGEwPSJlIiBhMT0iZiIvPgoJPG91dHB1dCBhMD0iRCIvPgo8L2NvbW1hbmQ+CjxlbGVtZW50IHR5cGU9InBvaW50IiBsYWJlbD0iRCI+Cgk8c2hvdyBvYmplY3Q9InRydWUiIGxhYmVsPSJ0cnVlIi8+Cgk8b2JqQ29sb3Igcj0iNjQiIGc9IjY0IiBiPSI2NCIgYWxwaGE9IjAuMCIvPgoJPGxheWVyIHZhbD0iMCIvPgoJPGxhYmVsTW9kZSB2YWw9IjAiLz4KCTxjb29yZHMgeD0iOS4zNDEyNzE5OTk5OTk5OSIgeT0iMjkuNDU2NjY0MDAwMDAwMDEiIHo9IjE2LjU2NzIiLz4KCTxwb2ludFNpemUgdmFsPSIzIi8+Cgk8cG9pbnRTdHlsZSB2YWw9IjAiLz4KPC9lbGVtZW50Pgo8Y29tbWFuZCBuYW1lPSJDaXJjbGUiPgoJPGlucHV0IGEwPSJEIiBhMT0iQiIvPgoJPG91dHB1dCBhMD0iZyIvPgo8L2NvbW1hbmQ+CjxlbGVtZW50IHR5cGU9ImNvbmljIiBsYWJlbD0iZyI+Cgk8c2hvdyBvYmplY3Q9InRydWUiIGxhYmVsPSJ0cnVlIi8+Cgk8b2JqQ29sb3Igcj0iMCIgZz0iMCIgYj0iMCIgYWxwaGE9IjAuMCIvPgoJPGxheWVyIHZhbD0iMCIvPgoJPGxhYmVsTW9kZSB2YWw9IjAiLz4KCTxsaW5lU3R5bGUgdGhpY2tuZXNzPSIyIiB0eXBlPSIwIi8+Cgk8ZWlnZW52ZWN0b3JzICB4MD0iMS4wIiB5MD0iMC4wIiB6MD0iMS4wIiB4MT0iLTAuMCIgeTE9IjEuMCIgejE9IjEuMCIvPgoJPG1hdHJpeCBBMD0iMS4wIiBBMT0iMS4wIiBBMj0iLTMuOTQ4ODg1OTkxNTk3ODUyIiBBMz0iMC4wIiBBND0iLTAuNTYzODQxMzI1MDI3NzY1MSIgQTU9Ii0xLjc3ODAxMTAwOTcwNTkyNTYiLz4KCTxlcW5TdHlsZSBzdHlsZT0ic3BlY2lmaWMiLz4KPC9lbGVtZW50Pgo8L2NvbnN0cnVjdGlvbj4KPC9nZW9nZWJyYT4=');"+
		"</script>"+
		"</body>"+
		"</html>";
		try {
			Element container = RootPanel.get("geogebramobile_div").getElement();
			Style style = container.getStyle();
			w = Integer.parseInt(style.getWidth().substring(0,style.getWidth().length()-2));
			h = Integer.parseInt(style.getHeight().substring(0, style.getHeight().length()-2));
			Element noscript = RootPanel.get("ggbappletwrapper").getElement();
			HTML applet = new HTML(noscript.getInnerText());
			NodeList<com.google.gwt.dom.client.Element> params = applet.getElement().getElementsByTagName("param");
			for (int i=0; i<params.getLength();i++) {
				if (params.getItem(i).getAttribute("name").equalsIgnoreCase("ggbbase64")) {
					decodedBase64String = params.getItem(i).getAttribute("value");
					break;
				}
			}
			GWT.log(decodedBase64String);
		} catch (Exception e) {
			// TODO: handle exception
		}
		initKernel();	
		initEuclidianView(w,h);
		initXmlHandler();
		registerFileDropHandlers(xmlhandler);
		xmlhandler.parseXml(defaultJsString);
	}
	
	private void initKernel() {
		kernel = new Kernel(this);
	}
	
	private void initXmlHandler() {
		xmlhandler = kernel.newMyXMLHandler(kernel.getConstruction());
		Base64Form.setApplication(this);
		xmlhandler.setApplication(this);
	}
	
	private void initEuclidianView(int w, int h) {
		//AGGgjsViewerWrapper wrapper = new GgjsViewerWrapper();
		euclidianview = new EuclidianView(w,h);
		RootPanel.get("geogebramobile_div").add(euclidianview);
		//AGRootPanel.get().add(wrapper);
		euclidiancontroller = new EuclidianController(kernel);
		euclidiancontroller.setApplication(this);
		//AGeuclidianview = wrapper.getEuclidianView();
		kernel.notifyAddAll(euclidianview);
		kernel.attach(euclidianview);
		euclidianview.setKernel(kernel);
		euclidianview.setApplication(this);
		//AG from EuclidianView's initView
		euclidianview.setStandardCoordSystem(false);
		JsonHandler.setApplication(this);
		euclidiancontroller.setEuclidianView(euclidianview);	
		euclidianview.setEuclidianController(euclidiancontroller);
		euclidianview.addMouseDownHandler(euclidiancontroller);
		euclidianview.addMouseUpHandler(euclidiancontroller);
		euclidianview.addMouseWheelHandler(euclidiancontroller);
		euclidianview.addMouseOverHandler(euclidiancontroller);
		euclidianview.addMouseOutHandler(euclidiancontroller);
		euclidianview.addMouseMoveHandler(euclidiancontroller);
		registerTouchHandlers(euclidiancontroller);
		
		
	
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

	public String translateCommand(String localname) {
		if (localname == null)
			return null;
		else 
			return localname;
		/*AGif (translateCommandTable == null)
			return localname;

		// note: lookup lower case of command name!
		Object value = translateCommandTable.get(localname.toLowerCase());		
		if (value == null)
			return localname;
		else
			return (String) value;*/
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
		
		return key;
	}
	
	/**
	 * PluginManager gets API with this H-P Ulven 2008-04-16
	 */
	public GgbAPI getGgbApi() {
		if (ggbapi == null) {
			ggbapi = new GgbAPI(this);
		}

		return ggbapi;
	}
	
	public Kernel getKernel() {
		return kernel;
	}

	public void storeUndoInfo() {
		// TODO Auto-generated method stub
		
	}

	public void removeMacroCommands() {
		// TODO Auto-generated method stub
		if (commandDict == null || kernel == null || !kernel.hasMacros())
			return;

		ArrayList macros = kernel.getAllMacros();
		for (int i = 0; i < macros.size(); i++) {
			String cmdName = ((Macro) macros.get(i)).getCommandName();
			commandDict.removeEntry(cmdName);
		}
		
	}

	public void showError(MyError e) {
		String command = e.getcommandName();
		GWT.log(command+" Application.showError");

	}

	public String getMenu(String string) {
		// TODO Auto-generated method stub
		return "getMenu needed Application 268"+string;
	}
	
	public static native void registerTouchHandlers(EuclidianController x) /*-{
	if ((navigator.platform && (navigator.platform == "iPad" || 
		navigator.platform == "iPod" || navigator.platform == "iPhone")) || 
		($wnd.navigator.userAgent.indexOf("Android") > -1)) {
		
			if (navigator.platform && navigator.platform == "iPad") {
				@geogebra.geogebramobile.client.euclidian.EuclidianController::navigator_iPad = true;
			}
			
			
			var canvas = $doc.getElementById('eview');
			canvas.addEventListener("touchmove",function(e) {
				if (e.targetTouches.length === 1) {
					e.preventDefault();
					e.stopPropagation();
					//console.log("move:"+e.targetTouches.length);	
					var touchevent = e.targetTouches[0]; 
					//console.log(e.clientX+", "+e.clientY);
					x.@geogebra.geogebramobile.client.euclidian.EuclidianController::handleTouchMove(II)(touchevent.clientX,touchevent.clientY);
					return false;
				}
			},false);
		canvas.addEventListener("touchstart",function(e) {
				if (e.targetTouches.length === 1) {
					e.preventDefault();
					e.stopPropagation();
					//console.log("start:"+e.targetTouches.length);
					var touchevent = e.targetTouches[0];
					//console.log("start:"+touchevent.clientX+", "+touchevent.clientY); 
					x.@geogebra.geogebramobile.client.euclidian.EuclidianController::handleTouchStart(II)(touchevent.clientX,touchevent.clientY);
					return false
				}
			},false);
			canvas.addEventListener("touchend",function(e) {
				e.preventDefault();
				e.stopPropagation();
				//console.log("touchend native");
				x.@geogebra.geogebramobile.client.euclidian.EuclidianController::handleTouchEnd()();
				return false;
			},false);
	}
}-*/;
	
	public static native void registerFileDropHandlers(MyXMLHandler x) /*-{
		//====JSXGRAPH UTIL CLASS THANKS FOR THE UNZIPPING FUNCTIONALITY======//
		JXG = {};
		
		JXG.Util = {};
		
		JXG.Util.Unzip = function (barray){
		    var outputArr = [],
		        output = "",
		        debug = false,
		        gpflags,
		        files = 0,
		        unzipped = [],
		        crc,
		        buf32k = new Array(32768),
		        bIdx = 0,
		        modeZIP=false,
		
		        CRC, SIZE,
		    
		        bitReverse = [
		        0x00, 0x80, 0x40, 0xc0, 0x20, 0xa0, 0x60, 0xe0,
		        0x10, 0x90, 0x50, 0xd0, 0x30, 0xb0, 0x70, 0xf0,
		        0x08, 0x88, 0x48, 0xc8, 0x28, 0xa8, 0x68, 0xe8,
		        0x18, 0x98, 0x58, 0xd8, 0x38, 0xb8, 0x78, 0xf8,
		        0x04, 0x84, 0x44, 0xc4, 0x24, 0xa4, 0x64, 0xe4,
		        0x14, 0x94, 0x54, 0xd4, 0x34, 0xb4, 0x74, 0xf4,
		        0x0c, 0x8c, 0x4c, 0xcc, 0x2c, 0xac, 0x6c, 0xec,
		        0x1c, 0x9c, 0x5c, 0xdc, 0x3c, 0xbc, 0x7c, 0xfc,
		        0x02, 0x82, 0x42, 0xc2, 0x22, 0xa2, 0x62, 0xe2,
		        0x12, 0x92, 0x52, 0xd2, 0x32, 0xb2, 0x72, 0xf2,
		        0x0a, 0x8a, 0x4a, 0xca, 0x2a, 0xaa, 0x6a, 0xea,
		        0x1a, 0x9a, 0x5a, 0xda, 0x3a, 0xba, 0x7a, 0xfa,
		        0x06, 0x86, 0x46, 0xc6, 0x26, 0xa6, 0x66, 0xe6,
		        0x16, 0x96, 0x56, 0xd6, 0x36, 0xb6, 0x76, 0xf6,
		        0x0e, 0x8e, 0x4e, 0xce, 0x2e, 0xae, 0x6e, 0xee,
		        0x1e, 0x9e, 0x5e, 0xde, 0x3e, 0xbe, 0x7e, 0xfe,
		        0x01, 0x81, 0x41, 0xc1, 0x21, 0xa1, 0x61, 0xe1,
		        0x11, 0x91, 0x51, 0xd1, 0x31, 0xb1, 0x71, 0xf1,
		        0x09, 0x89, 0x49, 0xc9, 0x29, 0xa9, 0x69, 0xe9,
		        0x19, 0x99, 0x59, 0xd9, 0x39, 0xb9, 0x79, 0xf9,
		        0x05, 0x85, 0x45, 0xc5, 0x25, 0xa5, 0x65, 0xe5,
		        0x15, 0x95, 0x55, 0xd5, 0x35, 0xb5, 0x75, 0xf5,
		        0x0d, 0x8d, 0x4d, 0xcd, 0x2d, 0xad, 0x6d, 0xed,
		        0x1d, 0x9d, 0x5d, 0xdd, 0x3d, 0xbd, 0x7d, 0xfd,
		        0x03, 0x83, 0x43, 0xc3, 0x23, 0xa3, 0x63, 0xe3,
		        0x13, 0x93, 0x53, 0xd3, 0x33, 0xb3, 0x73, 0xf3,
		        0x0b, 0x8b, 0x4b, 0xcb, 0x2b, 0xab, 0x6b, 0xeb,
		        0x1b, 0x9b, 0x5b, 0xdb, 0x3b, 0xbb, 0x7b, 0xfb,
		        0x07, 0x87, 0x47, 0xc7, 0x27, 0xa7, 0x67, 0xe7,
		        0x17, 0x97, 0x57, 0xd7, 0x37, 0xb7, 0x77, 0xf7,
		        0x0f, 0x8f, 0x4f, 0xcf, 0x2f, 0xaf, 0x6f, 0xef,
		        0x1f, 0x9f, 0x5f, 0xdf, 0x3f, 0xbf, 0x7f, 0xff
		    ],
		    
		    cplens = [
		        3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 15, 17, 19, 23, 27, 31,
		        35, 43, 51, 59, 67, 83, 99, 115, 131, 163, 195, 227, 258, 0, 0
		    ],
		
		    cplext = [
		        0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2,
		        3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 0, 99, 99
		    ], 
		
		    cpdist = [
		        0x0001, 0x0002, 0x0003, 0x0004, 0x0005, 0x0007, 0x0009, 0x000d,
		        0x0011, 0x0019, 0x0021, 0x0031, 0x0041, 0x0061, 0x0081, 0x00c1,
		        0x0101, 0x0181, 0x0201, 0x0301, 0x0401, 0x0601, 0x0801, 0x0c01,
		        0x1001, 0x1801, 0x2001, 0x3001, 0x4001, 0x6001
		    ],
		
		    cpdext = [
		        0,  0,  0,  0,  1,  1,  2,  2,
		        3,  3,  4,  4,  5,  5,  6,  6,
		        7,  7,  8,  8,  9,  9, 10, 10,
		        11, 11, 12, 12, 13, 13
		    ],
		    
		    border = [16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2, 14, 1, 15],
		    
		    bA = barray,
		
		    bytepos=0,
		    bitpos=0,
		    bb = 1,
		    bits=0,
		    
		    NAMEMAX = 256,
		    
		    nameBuf = [],
		    
		    fileout;
		    
		    function readByte(){
		        bits+=8;
		        if (bytepos<bA.length){
		            
		            return bA[bytepos++];
		        } else
		            return -1;
		    };
		
		    function byteAlign(){
		        bb = 1;
		    };
		    
		    function readBit(){
		        var carry;
		        bits++;
		        carry = (bb & 1);
		        bb >>= 1;
		        if (bb==0){
		            bb = readByte();
		            carry = (bb & 1);
		            bb = (bb>>1) | 0x80;
		        }
		        return carry;
		    };
		
		    function readBits(a) {
		        var res = 0,
		            i = a;
		    
		        while(i--) {
		            res = (res<<1) | readBit();
		        }
		        if(a) {
		            res = bitReverse[res]>>(8-a);
		        }
		        return res;
		    };
		        
		    function flushBuffer(){
		      
		        bIdx = 0;
		    };
		    function addBuffer(a){
		        SIZE++;
		       
		        buf32k[bIdx++] = a;
		        outputArr.push(String.fromCharCode(a));
		      
		        if(bIdx==0x8000){
		            
		            bIdx=0;
		        }
		    };
		    
		    function HufNode() {
		        this.b0=0;
		        this.b1=0;
		        this.jump = null;
		        this.jumppos = -1;
		    };
		
		    var LITERALS = 288;
		    
		    var literalTree = new Array(LITERALS);
		    var distanceTree = new Array(32);
		    var treepos=0;
		    var Places = null;
		    var Places2 = null;
		    
		    var impDistanceTree = new Array(64);
		    var impLengthTree = new Array(64);
		    
		    var len = 0;
		    var fpos = new Array(17);
		    fpos[0]=0;
		    var flens;
		    var fmax;
		    
		    function IsPat() {
		        while (1) {
		            if (fpos[len] >= fmax)
		                return -1;
		            if (flens[fpos[len]] == len)
		                return fpos[len]++;
		            fpos[len]++;
		        }
		    };
		
		    function Rec() {
		        var curplace = Places[treepos];
		        var tmp;
		        if (debug)
		    		document.write("<br>len:"+len+" treepos:"+treepos);
		        if(len==17) { //war 17
		            return -1;
		        }
		        treepos++;
		        len++;
		    	
		        tmp = IsPat();
		        if (debug)
		        	document.write("<br>IsPat "+tmp);
		        if(tmp >= 0) {
		            curplace.b0 = tmp;    
		            if (debug)
		            	document.write("<br>b0 "+curplace.b0);
		        } else {
		       
		        curplace.b0 = 0x8000;
		        if (debug)
		        	document.write("<br>b0 "+curplace.b0);
		        if(Rec())
		            return -1;
		        }
		        tmp = IsPat();
		        if(tmp >= 0) {
		            curplace.b1 = tmp;   
		            if (debug)
		            	document.write("<br>b1 "+curplace.b1);
		            curplace.jump = null;    
		        } else {
		           
		            curplace.b1 = 0x8000;
		            if (debug)
		            	document.write("<br>b1 "+curplace.b1);
		            curplace.jump = Places[treepos];
		            curplace.jumppos = treepos;
		            if(Rec())
		                return -1;
		        }
		        len--;
		        return 0;
		    };
		
		    function CreateTree(currentTree, numval, lengths, show) {
		        var i;
		       
		        if (debug)
		        	document.write("currentTree "+currentTree+" numval "+numval+" lengths "+lengths+" show "+show);
		        Places = currentTree;
		        treepos=0;
		        flens = lengths;
		        fmax  = numval;
		        for (i=0;i<17;i++)
		            fpos[i] = 0;
		        len = 0;
		        if(Rec()) {
		           
		            if (debug)
		            	alert("invalid huffman tree\n");
		            return -1;
		        }
		        if (debug){
		        	document.write('<br>Tree: '+Places.length);
		        	for (var a=0;a<32;a++){
		            	document.write("Places["+a+"].b0="+Places[a].b0+"<br>");
		            	document.write("Places["+a+"].b1="+Places[a].b1+"<br>");
		        	}
		        }
		    
		       
		        return 0;
		    };
		    
		    function DecodeValue(currentTree) {
		        var len, i,
		            xtreepos=0,
		            X = currentTree[xtreepos],
		            b;
		
		        
		        while(1) {
		            b=readBit();
		            if (debug)
		            	document.write("b="+b);
		            if(b) {
		                if(!(X.b1 & 0x8000)){
		                	if (debug)
		                    	document.write("ret1");
		                    return X.b1;    
		                }
		                X = X.jump;
		                len = currentTree.length;
		                for (i=0;i<len;i++){
		                    if (currentTree[i]===X){
		                        xtreepos=i;
		                        break;
		                    }
		                }
		                //xtreepos++;
		            } else {
		                if(!(X.b0 & 0x8000)){
		                	if (debug)
		                    	document.write("ret2");
		                    return X.b0;    
		                }
		                //X++; //??????????????????
		                xtreepos++;
		                X = currentTree[xtreepos];
		            }
		        }
		        if (debug)
		        	document.write("ret3");
		        return -1;
		    };
		    
		    function DeflateLoop() {
		    var last, c, type, i, len;
		
		    do {
		       
		        last = readBit();
		        type = readBits(2);
		        switch(type) {
		            case 0:
		            	if (debug)
		                	alert("Stored\n");
		                break;
		            case 1:
		            	if (debug)
		                	alert("Fixed Huffman codes\n");
		                break;
		            case 2:
		            	if (debug)
		                	alert("Dynamic Huffman codes\n");
		                break;
		            case 3:
		            	if (debug)
		                	alert("Reserved block type!!\n");
		                break;
		            default:
		            	if (debug)
		                	alert("Unexpected value %d!\n", type);
		                break;
		        }
		
		        if(type==0) {
		            var blockLen, cSum;
		
		            // Stored 
		            byteAlign();
		            blockLen = readByte();
		            blockLen |= (readByte()<<8);
		
		            cSum = readByte();
		            cSum |= (readByte()<<8);
		
		            if(((blockLen ^ ~cSum) & 0xffff)) {
		                document.write("BlockLen checksum mismatch\n");
		            }
		            while(blockLen--) {
		                c = readByte();
		                addBuffer(c);
		            }
		        } else if(type==1) {
		            var j;
		
		           
		            while(1) {
		            
		
		            j = (bitReverse[readBits(7)]>>1);
		            if(j > 23) {
		                j = (j<<1) | readBit();    
		
		                if(j > 199) {    
		                    j -= 128;    
		                    j = (j<<1) | readBit();        
		                } else {        
		                    j -= 48;    
		                    if(j > 143) {
		                        j = j+136;    
		                       
		                    }
		                }
		            } else {    
		                j += 256;    
		            }
		            if(j < 256) {
		                addBuffer(j);
		                //document.write("out:"+String.fromCharCode(j));
		               
		            } else if(j == 256) {
		               
		                break;
		            } else {
		                var len, dist;
		
		                j -= 256 + 1;   
		                len = readBits(cplext[j]) + cplens[j];
		
		                j = bitReverse[readBits(5)]>>3;
		                if(cpdext[j] > 8) {
		                    dist = readBits(8);
		                    dist |= (readBits(cpdext[j]-8)<<8);
		                } else {
		                    dist = readBits(cpdext[j]);
		                }
		                dist += cpdist[j];
		
		               
		                for(j=0;j<len;j++) {
		                    var c = buf32k[(bIdx - dist) & 0x7fff];
		                    addBuffer(c);
		                }
		            }
		            } // while
		        } else if(type==2) {
		            var j, n, literalCodes, distCodes, lenCodes;
		            var ll = new Array(288+32);    // "static" just to preserve stack
		    
		            // Dynamic Huffman tables 
		    
		            literalCodes = 257 + readBits(5);
		            distCodes = 1 + readBits(5);
		            lenCodes = 4 + readBits(4);
		            //document.write("<br>param: "+literalCodes+" "+distCodes+" "+lenCodes+"<br>");
		            for(j=0; j<19; j++) {
		                ll[j] = 0;
		            }
		    
		            // Get the decode tree code lengths
		    
		            //document.write("<br>");
		            for(j=0; j<lenCodes; j++) {
		                ll[border[j]] = readBits(3);
		                //document.write(ll[border[j]]+" ");
		            }
		            //fprintf(errfp, "\n");
		            //document.write('<br>ll:'+ll);
		            len = distanceTree.length;
		            for (i=0; i<len; i++)
		                distanceTree[i]=new HufNode();
		            if(CreateTree(distanceTree, 19, ll, 0)) {
		                flushBuffer();
		                return 1;
		            }
		            if (debug){
		            	document.write("<br>distanceTree");
		            	for(var a=0;a<distanceTree.length;a++){
		                	document.write("<br>"+distanceTree[a].b0+" "+distanceTree[a].b1+" "+distanceTree[a].jump+" "+distanceTree[a].jumppos);
		                	
		            	}
		            }
		            //document.write('<BR>tree created');
		    
		            //read in literal and distance code lengths
		            n = literalCodes + distCodes;
		            i = 0;
		            var z=-1;
		            if (debug)
		            	document.write("<br>n="+n+" bits: "+bits+"<br>");
		            while(i < n) {
		                z++;
		                j = DecodeValue(distanceTree);
		                if (debug)
		                	document.write("<br>"+z+" i:"+i+" decode: "+j+"    bits "+bits+"<br>");
		                if(j<16) {    // length of code in bits (0..15)
		                       ll[i++] = j;
		                } else if(j==16) {    // repeat last length 3 to 6 times 
		                       var l;
		                    j = 3 + readBits(2);
		                    if(i+j > n) {
		                        flushBuffer();
		                        return 1;
		                    }
		                    l = i ? ll[i-1] : 0;
		                    while(j--) {
		                        ll[i++] = l;
		                    }
		                } else {
		                    if(j==17) {        // 3 to 10 zero length codes
		                        j = 3 + readBits(3);
		                    } else {        // j == 18: 11 to 138 zero length codes 
		                        j = 11 + readBits(7);
		                    }
		                    if(i+j > n) {
		                        flushBuffer();
		                        return 1;
		                    }
		                    while(j--) {
		                        ll[i++] = 0;
		                    }
		                }
		            }
		         
		            // Can overwrite tree decode tree as it is not used anymore
		            len = literalTree.length;
		            for (i=0; i<len; i++)
		                literalTree[i]=new HufNode();
		            if(CreateTree(literalTree, literalCodes, ll, 0)) {
		                flushBuffer();
		                return 1;
		            }
		            len = literalTree.length;
		            for (i=0; i<len; i++)
		                distanceTree[i]=new HufNode();
		            var ll2 = new Array();
		            for (i=literalCodes; i <ll.length; i++){
		                ll2[i-literalCodes]=ll[i];
		            }    
		            if(CreateTree(distanceTree, distCodes, ll2, 0)) {
		                flushBuffer();
		                return 1;
		            }
		            if (debug)
		           		document.write("<br>literalTree");
		            while(1) {
		                j = DecodeValue(literalTree);
		                if(j >= 256) {        // In C64: if carry set
		                    var len, dist;
		                    j -= 256;
		                    if(j == 0) {
		                        // EOF
		                        break;
		                    }
		                    j--;
		                    len = readBits(cplext[j]) + cplens[j];
		    
		                    j = DecodeValue(distanceTree);
		                    if(cpdext[j] > 8) {
		                        dist = readBits(8);
		                        dist |= (readBits(cpdext[j]-8)<<8);
		                    } else {
		                        dist = readBits(cpdext[j]);
		                    }
		                    dist += cpdist[j];
		                    while(len--) {
		                        var c = buf32k[(bIdx - dist) & 0x7fff];
		                        addBuffer(c);
		                    }
		                } else {
		                    addBuffer(j);
		                }
		            }
		        }
		    } while(!last);
		    flushBuffer();
		
		    byteAlign();
		    return 0;
		};
		
		JXG.Util.Unzip.prototype.unzipFile = function(name) {
		    var i;
			this.unzip();
			//alert(unzipped[0][1]);
			for (i=0;i<unzipped.length;i++){
				if(unzipped[i][1]==name) {
					return unzipped[i][0];
				}
			}
			
		  };
		    
		    
		JXG.Util.Unzip.prototype.unzip = function() {
			//convertToByteArray(input);
			if (debug)
				alert(bA);
			
			//alert(bA);
			nextFile();
			return unzipped;
		  };
		    
		 function nextFile(){
		 	if (debug)
		 		alert("NEXTFILE");
		 	outputArr = [];
		 	var tmp = [];
		 	modeZIP = false;
			tmp[0] = readByte();
			tmp[1] = readByte();
			if (debug)
				alert("type: "+tmp[0]+" "+tmp[1]);
			if (tmp[0] == parseInt("78",16) && tmp[1] == parseInt("da",16)){ //GZIP
				if (debug)
					alert("GEONExT-GZIP");
				DeflateLoop();
				if (debug)
					alert(outputArr.join(''));
				unzipped[files] = new Array(2);
		    	unzipped[files][0] = outputArr.join('');
		    	unzipped[files][1] = "geonext.gxt";
		    	files++;
			}
			if (tmp[0] == parseInt("1f",16) && tmp[1] == parseInt("8b",16)){ //GZIP
				if (debug)
					alert("GZIP");
				//DeflateLoop();
				skipdir();
				if (debug)
					alert(outputArr.join(''));
				unzipped[files] = new Array(2);
		    	unzipped[files][0] = outputArr.join('');
		    	unzipped[files][1] = "file";
		    	files++;
			}
			if (tmp[0] == parseInt("50",16) && tmp[1] == parseInt("4b",16)){ //ZIP
				modeZIP = true;
				tmp[2] = readByte();
				tmp[3] = readByte();
				if (tmp[2] == parseInt("3",16) && tmp[3] == parseInt("4",16)){
					//MODE_ZIP
					tmp[0] = readByte();
					tmp[1] = readByte();
					if (debug)
						alert("ZIP-Version: "+tmp[1]+" "+tmp[0]/10+"."+tmp[0]%10);
					
					gpflags = readByte();
					gpflags |= (readByte()<<8);
					if (debug)
						alert("gpflags: "+gpflags);
					
					var method = readByte();
					method |= (readByte()<<8);
					if (debug)
						alert("method: "+method);
					
					readByte();
					readByte();
					readByte();
					readByte();
					
					var crc = readByte();
					crc |= (readByte()<<8);
					crc |= (readByte()<<16);
					crc |= (readByte()<<24);
					
					var compSize = readByte();
					compSize |= (readByte()<<8);
					compSize |= (readByte()<<16);
					compSize |= (readByte()<<24);
					
					var size = readByte();
					size |= (readByte()<<8);
					size |= (readByte()<<16);
					size |= (readByte()<<24);
					
					if (debug)
						alert("local CRC: "+crc+"\nlocal Size: "+size+"\nlocal CompSize: "+compSize);
					
					var filelen = readByte();
					filelen |= (readByte()<<8);
					
					var extralen = readByte();
					extralen |= (readByte()<<8);
					
					if (debug)
						alert("filelen "+filelen);
					i = 0;
					nameBuf = [];
					while (filelen--){ 
						var c = readByte();
						if (c == "/" | c ==":"){
							i = 0;
						} else if (i < NAMEMAX-1)
							nameBuf[i++] = String.fromCharCode(c);
					}
					if (debug)
						alert("nameBuf: "+nameBuf);
					
					//nameBuf[i] = "\0";
					if (!fileout)
						fileout = nameBuf;
					
					var i = 0;
					while (i < extralen){
						c = readByte();
						i++;
					}
						
					CRC = 0xffffffff;
					SIZE = 0;
					
					if (size = 0 && fileOut.charAt(fileout.length-1)=="/"){
						//skipdir
						if (debug)
							alert("skipdir");
					}
					if (method == 8){
						DeflateLoop();
						if (debug)
							alert(outputArr.join(''));
						unzipped[files] = new Array(2);
						unzipped[files][0] = outputArr.join('');
						//console.log(outputArr.join(''));
		    			unzipped[files][1] = nameBuf.join('');
		    			files++;
						//return outputArr.join('');
					}
					skipdir();
				}
			}
		 };
			
		function skipdir(){
		    var crc, 
		        tmp = [],
		        compSize, size, os, i, c;
		    
			if ((gpflags & 8)) {
				tmp[0] = readByte();
				tmp[1] = readByte();
				tmp[2] = readByte();
				tmp[3] = readByte();
				
				if (tmp[0] == parseInt("50",16) && 
		            tmp[1] == parseInt("4b",16) && 
		            tmp[2] == parseInt("07",16) && 
		            tmp[3] == parseInt("08",16))
		        {
		            crc = readByte();
		            crc |= (readByte()<<8);
		            crc |= (readByte()<<16);
		            crc |= (readByte()<<24);
				} else {
					crc = tmp[0] | (tmp[1]<<8) | (tmp[2]<<16) | (tmp[3]<<24);
				}
				
				compSize = readByte();
				compSize |= (readByte()<<8);
				compSize |= (readByte()<<16);
				compSize |= (readByte()<<24);
				
				size = readByte();
				size |= (readByte()<<8);
				size |= (readByte()<<16);
				size |= (readByte()<<24);
				
				if (debug)
					alert("CRC:");
			}
		
			if (modeZIP)
				nextFile();
			
			tmp[0] = readByte();
			if (tmp[0] != 8) {
				if (debug)
					alert("Unknown compression method!");
		        return 0;	
			}
			
			gpflags = readByte();
			if (debug){
				if ((gpflags & ~(parseInt("1f",16))))
					alert("Unknown flags set!");
			}
			
			readByte();
			readByte();
			readByte();
			readByte();
			
			readByte();
			os = readByte();
			
			if ((gpflags & 4)){
				tmp[0] = readByte();
				tmp[2] = readByte();
				len = tmp[0] + 256*tmp[1];
				if (debug)
					alert("Extra field size: "+len);
				for (i=0;i<len;i++)
					readByte();
			}
			
			if ((gpflags & 8)){
				i=0;
				nameBuf=[];
				while (c=readByte()){
					if(c == "7" || c == ":")
						i=0;
					if (i<NAMEMAX-1)
						nameBuf[i++] = c;
				}
				//nameBuf[i] = "\0";
				if (debug)
					alert("original file name: "+nameBuf);
			}
				
			if ((gpflags & 16)){
				while (c=readByte()){
					//FILE COMMENT
				}
			}
			
			if ((gpflags & 2)){
				readByte();
				readByte();
			}
			
			DeflateLoop();
			
			crc = readByte();
			crc |= (readByte()<<8);
			crc |= (readByte()<<16);
			crc |= (readByte()<<24);
			
			size = readByte();
			size |= (readByte()<<8);
			size |= (readByte()<<16);
			size |= (readByte()<<24);
			
			if (modeZIP)
				nextFile();
			
		};
		
		};
		
		
		JXG.Util.Base64 = {
		
		    // private property
		    _keyStr : "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",
		
		    // public method for encoding
		    encode : function (input) {
		        var output = [],
		            chr1, chr2, chr3, enc1, enc2, enc3, enc4,
		            i = 0;
		
		        input = JXG.Util.Base64._utf8_encode(input);
		
		        while (i < input.length) {
		
		            chr1 = input.charCodeAt(i++);
		            chr2 = input.charCodeAt(i++);
		            chr3 = input.charCodeAt(i++);
		
		            enc1 = chr1 >> 2;
		            enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
		            enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
		            enc4 = chr3 & 63;
		
		            if (isNaN(chr2)) {
		                enc3 = enc4 = 64;
		            } else if (isNaN(chr3)) {
		                enc4 = 64;
		            }
		
		            output.push([this._keyStr.charAt(enc1),
		                         this._keyStr.charAt(enc2),
		                         this._keyStr.charAt(enc3),
		                         this._keyStr.charAt(enc4)].join(''));
		        }
		
		        return output.join('');
		    },
		
		    // public method for decoding
		    decode : function (input, utf8) {
		        var output = [],
		            chr1, chr2, chr3,
		            enc1, enc2, enc3, enc4,
		            i = 0;
		
		        input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");
		
		        while (i < input.length) {
		
		            enc1 = this._keyStr.indexOf(input.charAt(i++));
		            enc2 = this._keyStr.indexOf(input.charAt(i++));
		            enc3 = this._keyStr.indexOf(input.charAt(i++));
		            enc4 = this._keyStr.indexOf(input.charAt(i++));
		
		            chr1 = (enc1 << 2) | (enc2 >> 4);
		            chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
		            chr3 = ((enc3 & 3) << 6) | enc4;
		
		            output.push(String.fromCharCode(chr1));
		
		            if (enc3 != 64) {
		                output.push(String.fromCharCode(chr2));
		            }
		            if (enc4 != 64) {
		                output.push(String.fromCharCode(chr3));
		            }
		        }
		        
		        output = output.join(''); 
		        
		        if (utf8) {
		            output = JXG.Util.Base64._utf8_decode(output);
		        }
		        return output;
		
		    },
		
		    // private method for UTF-8 encoding
		    _utf8_encode : function (string) {
		        string = string.replace(/\r\n/g,"\n");
		        var utftext = "";
		
		        for (var n = 0; n < string.length; n++) {
		
		            var c = string.charCodeAt(n);
		
		            if (c < 128) {
		                utftext += String.fromCharCode(c);
		            }
		            else if((c > 127) && (c < 2048)) {
		                utftext += String.fromCharCode((c >> 6) | 192);
		                utftext += String.fromCharCode((c & 63) | 128);
		            }
		            else {
		                utftext += String.fromCharCode((c >> 12) | 224);
		                utftext += String.fromCharCode(((c >> 6) & 63) | 128);
		                utftext += String.fromCharCode((c & 63) | 128);
		            }
		
		        }
		
		        return utftext;
		    },
		
		    // private method for UTF-8 decoding
		    _utf8_decode : function (utftext) {
		        var string = [],
		            i = 0,
		            c = 0, c2 = 0, c3 = 0;
		
		        while ( i < utftext.length ) {
		            c = utftext.charCodeAt(i);
		            if (c < 128) {
		                string.push(String.fromCharCode(c));
		                i++;
		            }
		            else if((c > 191) && (c < 224)) {
		                c2 = utftext.charCodeAt(i+1);
		                string.push(String.fromCharCode(((c & 31) << 6) | (c2 & 63)));
		                i += 2;
		            }
		            else {
		                c2 = utftext.charCodeAt(i+1);
		                c3 = utftext.charCodeAt(i+2);
		                string.push(String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63)));
		                i += 3;
		            }
		        }
		        return string.join('');
		    },
		    
		    _destrip: function (stripped, wrap){
		        var lines = [], lineno, i,
		            destripped = [];
		        
		        if (wrap==null) 
		            wrap = 76;
		            
		        stripped.replace(/ /g, "");
		        lineno = stripped.length / wrap;
		        for (i = 0; i < lineno; i++)
		            lines[i]=stripped.substr(i * wrap, wrap);
		        if (lineno != stripped.length / wrap)
		            lines[lines.length]=stripped.substr(lineno * wrap, stripped.length-(lineno * wrap));
		            
		        for (i = 0; i < lines.length; i++)
		            destripped.push(lines[i]);
		        return destripped.join('\n');
		    },
		    
		    decodeAsArray: function (input){
		        var dec = this.decode(input),
		            ar = [], i;
		        for (i=0;i<dec.length;i++){
		            ar[i]=dec.charCodeAt(i);
		        }
		        return ar;
		    },
		    
		    decodeGEONExT : function (input) {
		        return decodeAsArray(destrip(input),false);
		    }
		};
	
		
		
		JXG.Util.asciiCharCodeAt = function(str,i){
			var c = str.charCodeAt(i);
			if (c>255){
		    	switch (c) {
					case 8364: c=128;
			    	break;
			    	case 8218: c=130;
			    	break;
			    	case 402: c=131;
			    	break;
			    	case 8222: c=132;
			    	break;
			    	case 8230: c=133;
			    	break;
			    	case 8224: c=134;
			    	break;
			    	case 8225: c=135;
			    	break;
			    	case 710: c=136;
			    	break;
			    	case 8240: c=137;
			    	break;
			    	case 352: c=138;
			    	break;
			    	case 8249: c=139;
			    	break;
			    	case 338: c=140;
			    	break;
			    	case 381: c=142;
			    	break;
			    	case 8216: c=145;
			    	break;
			    	case 8217: c=146;
			    	break;
			    	case 8220: c=147;
			    	break;
			    	case 8221: c=148;
			    	break;
			    	case 8226: c=149;
			    	break;
			    	case 8211: c=150;
			    	break;
			    	case 8212: c=151;
			    	break;
			    	case 732: c=152;
			    	break;
			    	case 8482: c=153;
			    	break;
			    	case 353: c=154;
			    	break;
			    	case 8250: c=155;
			    	break;
			    	case 339: c=156;
			    	break;
			    	case 382: c=158;
			    	break;
			    	case 376: c=159;
			    	break;
			    	default:
			    	break;
			    }
			}
			return c;
		};
		
	
		JXG.Util.utf8Decode = function(utftext) {
		  var string = [];
		  var i = 0;
		  var c = 0, c1 = 0, c2 = 0;
		
		  while ( i < utftext.length ) {
		    c = utftext.charCodeAt(i);
		
		    if (c < 128) {
		      string.push(String.fromCharCode(c));
		      i++;
		    } else if((c > 191) && (c < 224)) {
		      c2 = utftext.charCodeAt(i+1);
		      string.push(String.fromCharCode(((c & 31) << 6) | (c2 & 63)));
		      i += 2;
		    } else {
		      c2 = utftext.charCodeAt(i+1);
		      c3 = utftext.charCodeAt(i+2);
		      string.push(String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63)));
		      i += 3;
		    }
		  };
		  return string.join('');
		};
		
		JXG.GeogebraReader = new function() {
			this.utf8replace = function(exp) {
		  //exp = (exp.match(/\u03C0/)) ? exp.replace(/\u03C0/g, 'PI') : exp;
		  //exp = (exp.match(/\u00B2/)) ? exp.replace(/\u00B2/g, '^2') : exp;
		  //exp = (exp.match(/\u00B3/)) ? exp.replace(/\u00B3/g, '^3') : exp;
		  //exp = (exp.match(/\u225F/)) ? exp.replace(/\u225F/g, '==') : exp;
		  //exp = (exp.match(/\u2260/)) ? exp.replace(/\u2260/g, '!=') : exp;
		  //exp = (exp.match(/\u2264/)) ? exp.replace(/\u2264/g, '<=') : exp;
		  //exp = (exp.match(/\u2265/)) ? exp.replace(/\u2265/g, '>=') : exp;
		  //exp = (exp.match(/\u2227/)) ? exp.replace(/\u2227/g, '&&') : exp;
		  //exp = (exp.match(/\u2228/)) ? exp.replace(/\u2228/g, '//') : exp;
		  return exp;
		};
		}	
		
		
		
		//====JSXGRAPH UTIL CLASS END======================================//
		
		
		
		
		var canvas = $doc.getElementById('eview');
		canvas.addEventListener("dragover",function(e) {
			e.preventDefault();
			e.stopPropagation();
			canvas.style.borderColor="#ff0000";
		},false);
		canvas.addEventListener("dragenter",function(e) {
			e.preventDefault();
			e.stopPropagation();
		},false);
		canvas.addEventListener("drop",function(e) {
			e.preventDefault();
			e.stopPropagation();
			canvas.style.borderColor="#000000";
			var dt = e.dataTransfer;
			if (dt.files.length) {
				var fileToHandle = dt.files[0];
				//console.log(fileToHandle.name);
				var reader = new FileReader();
				reader.onloadend = function(e) {
					if (reader.readyState === reader.DONE) {
						var fileStr = reader.result;
						//console.log(fileStr);
						var bA = [];
        				var len = fileStr.length;
        				for (i=0;i<len;i++) 
            				bA[i]=JXG.Util.asciiCharCodeAt(fileStr,i);
            			//console.log(bA.join(';'));
        				
        				// Unzip
        				fileStr = (new JXG.Util.Unzip(bA)).unzipFile("geogebra.xml");
        				fileStr = JXG.Util.utf8Decode(fileStr);
    					fileStr = JXG.GeogebraReader.utf8replace(fileStr);
						//console.log(fileStr);
						x.@geogebra.geogebramobile.client.io.MyXMLHandler::parseXml(Ljava/lang/String;)(fileStr);
					}
				};
				//reader.readAsText(fileToHandle);
				reader.readAsBinaryString(fileToHandle);
				//reader.readAsDataURL(fileToHandle);
				
			}
		},false);
		$doc.body.addEventListener("dragover",function(e) {
			e.preventDefault();
			e.stopPropagation();
			canvas.style.borderColor="#000000";
		},false);
		$doc.body.addEventListener("drop",function(e) {
			e.preventDefault();
			e.stopPropagation();
		},false);
		
	}-*/;

	public String getRefreshViewImage() {
		// TODO Auto-generated method stub;
		return "../images/main/view-refresh.png"; 
	}

	public String getPlayImage() {
		return "../images/main/nav_play.png";
	}

	public String getPauseImage() {
		return "../images/main/nav_pause.png";
	}
	
	final public boolean showResetIcon() {
		return showResetIcon && !runningInFrame;
	}

	public void reset() {
		// TODO Auto-generated method stub
		
	}

	public Font getFontCanDisplay(String textString, boolean serifFont,
			int fontStyle, int fontSize) {
		// TODO Auto-generated method stub
		return new Font("normal");
	}
	
	public EuclidianView getEuclidianView() {
		// TODO Auto-generated method stub
		return euclidianview;
	}
	
	final public void removeSelectedGeo(GeoElement geo, boolean repaint) {
		if (geo == null)
			return;

		selectedGeos.remove(geo);
		geo.setSelected(false);
		if (repaint)
			kernel.notifyRepaint();
		updateSelection();
	}

	public void removeSelectedGeo(GeoBoolean geo) {
		removeSelectedGeo(geo, true);
		
	}
	
	



}
