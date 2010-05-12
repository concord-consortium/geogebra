package org.geogebra.ggjsviewer.client.io;


import org.geogebra.ggjsviewer.client.util.Base64;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

public class MyXmlHandler  {

	public static void parseXml(String text) {
		if (text.indexOf("loadBase64Unzipped") > -1) {
			GWT.log(Base64.decode(text.substring(text.indexOf("(")+2, text.indexOf(")")-1)));
		// TODO Auto-generated method stub
		} else {
			Window.alert("Malformed Base64 string");
		}
		
	}
	
	
}
