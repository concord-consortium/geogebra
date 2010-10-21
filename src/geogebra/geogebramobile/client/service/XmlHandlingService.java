package geogebra.geogebramobile.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;

public interface XmlHandlingService extends RemoteService {
	String getJavaScriptGGB(Integer index) throws IllegalArgumentException;

}
