package org.geogebra.ggjsviewer.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;

public interface XmlHandlingService extends RemoteService {
	String getJavaScriptGGB(Integer index) throws IllegalArgumentException;

}
