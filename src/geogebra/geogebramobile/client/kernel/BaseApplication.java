package geogebra.geogebramobile.client.kernel;

import geogebra.geogebramobile.client.euclidian.EuclidianView;

public class BaseApplication {

	public String getPlain(String string) {
		// TODO Auto-generated method stub
		return "TODOBASEAPP";
	}

	public static void debug(String string) {
		// TODO Auto-generated method stub
		
	}

	public void setUnsaved() {
		// TODO Auto-generated method stub
		
	}

	public static void printStacktrace(String message) {
		// TODO Auto-generated method stub
		try {

			throw new Exception(message);

		} catch (Exception e) {

			e.printStackTrace();

		}

		
	}

	public Object getCommand(String cmdname) {
		// TODO Auto-generated method stub
		return cmdname+"TodoAG";
	}

	public boolean letRedefine() {
		// TODO Auto-generated method stub
		return false;
	}

	public Kernel getKernel() {
		// TODO Auto-generated method stub
		return null;
	}



	public boolean isReverseNameDescriptionLanguage() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isRightToLeftReadingOrder() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasGuiManager() {
		// TODO Auto-generated method stub
		return false;
	}

	public int getLabelingStyle() {
		// TODO Auto-generated method stub
		return 0;
	}

	

	public EuclidianView getEuclidianView() {
		// TODO Auto-generated method stub
		return null;
	}

	// Michael Borcherds 2008-03-25
	// replace "%0" by arg0
	final public String getPlain(String key, String arg0) {
		String[] ss = { arg0 };
		return getPlain(key, ss);
	}

	// Michael Borcherds 2008-03-25
	// replace "%0" by arg0, "%1" by arg1
	final public String getPlain(String key, String arg0, String arg1) {
		String[] ss = { arg0, arg1 };
		return getPlain(key, ss);
	}

	// Michael Borcherds 2008-03-30
	// replace "%0" by arg0, "%1" by arg1, "%2" by arg2
	final public String getPlain(String key, String arg0, String arg1,
			String arg2) {
		String[] ss = { arg0, arg1, arg2 };
		return getPlain(key, ss);
	}

	// Michael Borcherds 2008-03-30
	// replace "%0" by arg0, "%1" by arg1, "%2" by arg2, "%3" by arg3
	final public String getPlain(String key, String arg0, String arg1,
			String arg2, String arg3) {
		String[] ss = { arg0, arg1, arg2, arg3 };
		return getPlain(key, ss);
	}

	// Michael Borcherds 2008-03-30
	// replace "%0" by arg0, "%1" by arg1, "%2" by arg2, "%3" by arg3, "%4" by
	// arg4
	final public String getPlain(String key, String arg0, String arg1,
			String arg2, String arg3, String arg4) {
		String[] ss = { arg0, arg1, arg2, arg3, arg4 };
		return getPlain(key, ss);
	}

	// Michael Borcherds 2008-03-25
	// Markus Hohenwarter 2008-09-18
	// replace "%0" by args[0], "%1" by args[1], etc
	final public String getPlain(String key, String[] args) {
		String str = getPlain(key);

		sbPlain.setLength(0);
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (ch == '%') {
				// get number after %
				i++;
				int pos = str.charAt(i) - '0';
				if (pos >= 0 && pos < args.length)
					// success
					sbPlain.append(args[pos]);
				else
					// failed
					sbPlain.append(ch);
			} else {
				sbPlain.append(ch);
			}
		}

		return sbPlain.toString();
	}
	
	private StringBuilder sbPlain = new StringBuilder();

	public boolean freeMemoryIsCritical() {
		// TODO Auto-generated method stub
		return false;
	}

	public long freeMemory() {
		// TODO Auto-generated method stub
		return 0;
	}
	

}
