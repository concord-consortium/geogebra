package org.geogebra.ggjsviewer.client.gui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class GgjsViewerWrapper extends Composite {

	private static GgjsViewerWrapperUiBinder uiBinder = GWT
			.create(GgjsViewerWrapperUiBinder.class);

	interface GgjsViewerWrapperUiBinder extends
			UiBinder<Widget, GgjsViewerWrapper> {
	}

	@UiField
	EuclidianView euclidianview;

	public GgjsViewerWrapper(/*Possible parameters String firstName*/) {
		initWidget(uiBinder.createAndBindUi(this));
		//view.methodToCall(possibleParameters);
	}
	/*Eventhandlers will be assigned in EuclidianView and
	Handled by EuclidianController
	@UiHandler("euclidianview")
	void onClick(ClickEvent e) {
		Window.alert("Hello!");
	}*/

}
