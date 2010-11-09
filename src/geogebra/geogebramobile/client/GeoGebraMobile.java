package geogebra.geogebramobile.client;

import geogebra.geogebramobile.client.gui.GgjsViewerWrapper;
import geogebra.geogebramobile.client.main.Application;
import geogebra.geogebramobile.shared.FieldVerifier;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GeoGebraMobile implements EntryPoint {
	
	
	// File format versions
	public static final String XML_FILE_FORMAT = "3.3";
	public static final String BUILD_DATE = "07 November 2010";
	public static final String VERSION_STRING = "3.9.114.0";
	public static final String SHORT_VERSION_STRING = "4.0"; // used for online archive
	public static final String GGB_XSD_FILENAME = "ggb.xsd"; // for ggb files
	public static final String GGT_XSD_FILENAME = "ggt.xsd"; // for macro files 
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		//Attach the mainwrapper to the <body>
		Application app = new Application();
		
	}
}
