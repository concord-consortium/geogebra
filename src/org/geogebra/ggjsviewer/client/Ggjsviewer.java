package org.geogebra.ggjsviewer.client;

import org.geogebra.ggjsviewer.client.gui.GgjsViewerWrapper;
import org.geogebra.ggjsviewer.client.main.Application;
import org.geogebra.ggjsviewer.shared.FieldVerifier;
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
public class Ggjsviewer implements EntryPoint {
	
	
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		//Attach the mainwrapper to the <body>
		Application app = new Application();
		
	}
}
