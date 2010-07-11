package org.geogebra.ggjsviewer.client.gui;

import org.geogebra.ggjsviewer.client.euclidian.EuclidianView;
import org.geogebra.ggjsviewer.client.main.Application;
import org.geogebra.ggjsviewer.client.service.JsonHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
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
	public JsonHandler jsonHandler = new JsonHandler();
	private final MultiWordSuggestOracle commands = new MultiWordSuggestOracle();

	@UiField
	EuclidianView euclidianview;
	@UiField
	static public Button button;
	@UiField(provided=true)
	static public SuggestBox commandSuggestBox;
	@UiField
	static public ListBox examplesList;
	
	public GgjsViewerWrapper(/*Possible parameters*/) {
		commandSuggestBox = new SuggestBox(commands);	
		initWidget(uiBinder.createAndBindUi(this));
		commandSuggestBox.setTitle("Not working yet...");
		//Populate listbox
		examplesList.addItem("Euler line version 2", "0");
		examplesList.addItem("Circles","1");
		examplesList.addItem("Circle around a triangle","2");
		getCommands();
		//view.methodToCall(possibleParameters);
	}
	
	private void getCommands() {
		// TODO Auto-generated method stub
		commands.add("Not Working Yet");
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
	
	@UiHandler("examplesList")
	void handleListChange(ChangeEvent e) {
		jsonHandler.getXmlfromExamplesList(examplesList.getValue(examplesList.getSelectedIndex()));
	}
	

}
