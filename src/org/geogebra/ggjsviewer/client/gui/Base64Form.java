package org.geogebra.ggjsviewer.client.gui;

import org.geogebra.ggjsviewer.client.io.MyXmlHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class Base64Form extends PopupPanel  {

	private static Base64FormUiBinder uiBinder = GWT
			.create(Base64FormUiBinder.class);

	interface Base64FormUiBinder extends UiBinder<Widget, Base64Form> {
	}

	@UiField
	Button base64submit;
	@UiField
	TextArea base64area;

	public Base64Form() {
		setWidget(uiBinder.createAndBindUi(this));
		//base64submit.setText(firstName);
	}

	@UiHandler("base64submit")
	void onClick(ClickEvent e) {
		if (base64area.getText().trim()!="") {
			MyXmlHandler.parseXml(base64area.getText());
			this.hide();
			GgjsViewerWrapper.button.setText("Open TextBox");
		} else {
			Window.alert("TextArea is empty");
		}
	}

	
	
	

}
