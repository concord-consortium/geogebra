package geogebra.geogebramobile.client.plugin;

import geogebra.geogebramobile.client.kernel.Construction;
import geogebra.geogebramobile.client.kernel.GeoElement;
import geogebra.geogebramobile.client.kernel.Kernel;
import geogebra.geogebramobile.client.kernel.commands.AlgebraProcessor;
import geogebra.geogebramobile.client.main.Application;

public class GgbAPI {
	
	  private Application         app=                null;   //References ...
	  private Kernel              kernel=             null;
	  private Construction        construction=       null;
	  private AlgebraProcessor    algebraprocessor=   null;

	public GgbAPI(Application application) {
		app = application;
		kernel=app.getKernel();
        algebraprocessor=kernel.getAlgebraProcessor();
        construction=kernel.getConstruction();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Evaluates the given string as if it was entered into GeoGebra's 
	 * input text field. 	 
	 */
	public synchronized boolean evalCommand(String cmdString) {
		
		//Application.debug("evalCommand called..."+cmdString);
		GeoElement [] result;
		
		if (cmdString.indexOf('\n') == -1) {
			result = kernel.getAlgebraProcessor().processAlgebraCommand(cmdString, false);
			// return success
			return result != null;
			
		}

		boolean ret = true;
		String[] cmdStrings = cmdString.split("[\\n]+");
		for (int i = 0 ; i < cmdStrings.length ; i++) {
			result = kernel.getAlgebraProcessor().processAlgebraCommand(cmdStrings[i], false);
			ret = ret & (result != null);
		}
		
		return ret;
	}
}
