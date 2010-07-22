package org.geogebra.ggjsviewer.client.util;

public class Util {
	 private static StringBuilder sb;
	    /*
	     * returns a string with n instances of s
	     * eg string("hello",2) -> "hellohello";
	     */
	    public static String string(String s, int n) {
	    	
	    	if (n == 1) return s; // most common, check first
	    	if (n < 1) return "";
	    	
	    	if (sb == null)
	    		sb = new StringBuilder(); 
	    	
	    	sb.setLength(0);
	    	
	    	for (int i = 0 ; i < n ; i++) {
	    		sb.append(s);
	    	}
	    	
	    	return sb.toString();
	    }
}
