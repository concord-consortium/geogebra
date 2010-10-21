package geogebra.geogebramobile.server;

import geogebra.geogebramobile.client.service.XmlHandlingService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class XmlHandlingServiceImpl extends RemoteServiceServlet implements
		XmlHandlingService {

	
	@Override
	public String getJavaScriptGGB(Integer index)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return "looks like works well "+index.toString();
	}
}
