package org.geogebra.ggjsviewer.client.gui;

import org.geogebra.ggjsviewer.client.euclidian.EuclidianView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class GgjsViewerWrapper extends Composite {

	
	private static GgjsViewerWrapperUiBinder uiBinder = GWT
			.create(GgjsViewerWrapperUiBinder.class);

	interface GgjsViewerWrapperUiBinder extends
			UiBinder<Widget, GgjsViewerWrapper> {
	}
	
	public Base64Form form = null;;

	@UiField
	EuclidianView euclidianview;
	@UiField
	static public Button button;
	public GgjsViewerWrapper(/*Possible parameters*/) {
		initWidget(uiBinder.createAndBindUi(this));
		//view.methodToCall(possibleParameters);
	}
	
	public EuclidianView getEuclidianView() {
		return euclidianview;		
	}
	
	@UiHandler("button")
	void handleClick(ClickEvent e) {
		if (form == null) { 
			form = new Base64Form();
			form.setAnimationEnabled(true);
			//int width = euclidianview.DEFAULT_WIDTH;
			//int height = euclidianview.DEFAULT_HEIGHT;
			int left = euclidianview.getAbsoluteLeft();
			int top = euclidianview.getAbsoluteTop();
			//form.setWidth(width+"px");
			//form.setHeight(height+"px");
			form.setPopupPosition(left, top);
		} 
		if (button.getText().indexOf("Open")>-1) {
				button.setText("Cancel");
				form.show();
		} else {
				button.setText("Open TextBox");
				form.hide();
				Base64Form.base64area.setText("");
		}
		
	}

}
