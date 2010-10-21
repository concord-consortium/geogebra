package geogebra.geogebramobile.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface XmlHandlingServiceAsync {
	void getJavaScriptGGB(Integer index, AsyncCallback<String> callBack) throws IllegalArgumentException;

}
