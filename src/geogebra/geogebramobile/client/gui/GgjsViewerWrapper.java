package geogebra.geogebramobile.client.gui;

import geogebra.geogebramobile.client.euclidian.EuclidianView;
import geogebra.geogebramobile.client.main.Application;
import geogebra.geogebramobile.client.service.JsonHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
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
import com.google.gwt.user.client.ui.TextBox;
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
	@UiField
	EuclidianView euclidianview;
	@UiField
	static public Button button;
	@UiField
	static public TextBox commandSuggestBox;
	@UiField
	static public Button exm1;
	@UiField
	static public Button exm2;
	@UiField
	static public Button exm3;
	//@UiField
	//static public Button exm4;
	//AG@UiField Nice but crashes in mobile safari
	//AGstatic public ListBox examplesList;
	
	public GgjsViewerWrapper(/*Possible parameters*/) {
		initWidget(uiBinder.createAndBindUi(this));
		commandSuggestBox.setTitle("Type a command (No suggestions yet)");
		//Populate listbox
		//AGexamplesList.addItem("Euler line version 2", "0");
		//AGexamplesList.addItem("Circles","1");
		//AGexamplesList.addItem("Circle around a triangle","2");
		//view.methodToCall(possibleParameters);
		
		exm1.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				handleExampleButtons(event);
				
			}
		});
		exm2.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				handleExampleButtons(event);
				
			}
		});
		exm3.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				handleExampleButtons(event);
				
			}
		});
		/*exm4.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				handleExampleButtons(event);
				
			}
		});*/
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
	@UiHandler("commandSuggestBox")
	void handleKeyPress(KeyPressEvent e){
		if (e.getCharCode() == KeyCodes.KEY_ENTER) {
			String command = commandSuggestBox.getValue();
			if (!command.trim().isEmpty()) {
				euclidianview.getApplication().getGgbApi().evalCommand(command);
			}
			
		}
		
	}
	
	void handleExampleButtons(ClickEvent e) {
		Button sender = (Button) e.getSource();
		if (sender == exm1) 
			jsonHandler.getXmlfromExamplesList("0");
		else if (sender == exm2)
			jsonHandler.getXmlfromExamplesList("1");
		else if (sender == exm3)
			jsonHandler.getXmlfromExamplesList("2");
		/*else if (sender == exm4)
			jsonHandler.getXmlfromExamplesList("3");*/
	}
	
	
	/*AG@UiHandler("examplesList")
	void handleListChange(ChangeEvent e) {
		jsonHandler.getXmlfromExamplesList(examplesList.getValue(examplesList.getSelectedIndex()));
	}*/
	

}
