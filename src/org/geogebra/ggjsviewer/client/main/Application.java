package org.geogebra.ggjsviewer.client.main;

import java.util.ArrayList;
import java.util.Hashtable;
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
import org.geogebra.ggjsviewer.client.kernel.Macro;
import org.geogebra.ggjsviewer.client.kernel.arithmetic.ExpressionNode;
import org.geogebra.ggjsviewer.client.plugin.GgbAPI;
import org.geogebra.ggjsviewer.client.service.JsonHandler;
import org.geogebra.ggjsviewer.client.util.LowerCaseDictionary;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
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
	
	protected boolean showMenuBar = true;
	
	private ArrayList<GeoElement> selectedGeos = new ArrayList<GeoElement>();
	private GgbAPI ggbapi;
	
	
	public Application() {
		super();
		initKernel();	
		initEuclidianView();
		initXmlHandler();
		xmlhandler.parseXml("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\""+
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
		"</html>");
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
				@org.geogebra.ggjsviewer.client.euclidian.EuclidianController::navigator_iPad = true;
			}
			
			
			var canvas = $doc.getElementById('eview');
			canvas.addEventListener("touchmove",function(e) {
				if (e.targetTouches.length === 1) {
					e.preventDefault();
					e.stopPropagation();
					//console.log("move:"+e.targetTouches.length);	
					var touchevent = e.targetTouches[0]; 
					//console.log(e.clientX+", "+e.clientY);
					x.@org.geogebra.ggjsviewer.client.euclidian.EuclidianController::handleTouchMove(II)(touchevent.clientX,touchevent.clientY);
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
					x.@org.geogebra.ggjsviewer.client.euclidian.EuclidianController::handleTouchStart(II)(touchevent.clientX,touchevent.clientY);
					return false
				}
			},false);
			canvas.addEventListener("touchend",function(e) {
				e.preventDefault();
				e.stopPropagation();
				//console.log("touchend native");
				x.@org.geogebra.ggjsviewer.client.euclidian.EuclidianController::handleTouchEnd()();
				return false;
			},false);
	}
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



}
