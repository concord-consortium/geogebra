package geogebra.geogebramobile.client.service;

import geogebra.geogebramobile.client.main.Application;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

public class JsonHandler {
	private static final String JSON_URL = GWT.getModuleBaseURL() + "../xmlhandler.php?example=";
	
	private JSONObject jsonObject = null;
	private JSONValue parsedJSON = null;
	static private Application application;
	
	static public void setApplication(Application app) {
		application = app;
	}
	
	public String getXmlfromExamplesList(String n) {
		if (n == null || n == "") {
			n = "0";
		}	
	
		String xmlBase64String = "";
		
		String url = JsonHandler.JSON_URL+n;
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
	
	    try {
	      Request request = builder.sendRequest(null, new RequestCallback() {
	        public void onError(Request request, Throwable exception) {
	          Window.alert("Couldn't retrieve JSON");
	        }
	
	        public void onResponseReceived(Request request, Response response) {
	          if (200 == response.getStatusCode()) {
	             parsedJSON = JSONParser.parse(response.getText());
	             if (parsedJSON != null) {
	     	    	jsonObject = parsedJSON.isObject();
	     	    	if (jsonObject != null) {
	     	    		application.getMyXmlHandler().parseXml(jsonObject.get("jsxml").isString().stringValue());
	     	    	}
	     	    }
	          } else {
	            Window.alert("Couldn't retrieve JSON (" + response.getStatusText()
	                + ")");
	          }
	        }
	      });
	    } catch (RequestException e) {
	      Window.alert("Couldn't retrieve JSON");
	    }
	    
		return xmlBase64String;
	    
	}
}
