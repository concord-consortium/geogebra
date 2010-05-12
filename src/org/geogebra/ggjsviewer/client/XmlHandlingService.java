package org.geogebra.ggjsviewer.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("xmlhandler")
public interface XmlHandlingService extends RemoteService {
	String xmlhandlingServer(String name) throws IllegalArgumentException;
}
