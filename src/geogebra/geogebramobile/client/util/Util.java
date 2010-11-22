package geogebra.geogebramobile.client.util;

public class Util {
	 private static StringBuilder sb;
	    
	 private static StringBuilder sbReplaceExp = new StringBuilder(200);
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

	    //Guy Hed 30.08.2009
	    private static boolean isRightToLeftChar( char c ) {
	    	return false; //AR(Character.getDirectionality(c) == Character.DIRECTIONALITY_RIGHT_TO_LEFT); 
	    }
	    //Guy Hed 30.08.2009

	    /**
	     * Replaces special unicode letters (e.g. greek letters)
	     * in str by LaTeX strings.
	     */
	    public static synchronized String toLaTeXString(String str,
		    boolean convertGreekLetters) {
		int length = str.length();
		sbReplaceExp.setLength(0);
		for (int i = 0; i < length; i++) {
		    char c = str.charAt(i);
		    
		    // Guy Hed 30.8.2009
		    // Fix Hebrew 'undefined' problem in Latex text.
		    if( isRightToLeftChar(c) ) {
		      int j;
		      for( j=i; j<length && (isRightToLeftChar(str.charAt(j)) || str.charAt(j)=='\u00a0'); j++ );
		      for( int k=j-1; k>=i ; k-- )
		    	  sbReplaceExp.append(str.charAt(k));
		      sbReplaceExp.append(' ');
		      i=j-1;
		      continue;
		    }
		    // Guy Hed 30.8.2009
		    
		    switch (c) {
		    /*
		     case '(':
		     sbReplaceExp.append("\\left(");
		     break;
		     
		     case ')':
		     sbReplaceExp.append("\\right)");
		     break;
		     */

		    // Exponents
			// added by Loïc Le Coq 2009/11/04
		    case '\u2070': // ^0
			sbReplaceExp.append("^0");
			break;
			
		    case '\u00b9': // ^1
		    sbReplaceExp.append("^1");
			break;
			// end Loïc
		    case '\u00b2': // ^2
				sbReplaceExp.append("^2");
				break;

		    case '\u00b3': // ^3
			sbReplaceExp.append("^3");
			break;

		    case '\u2074': // ^4
			sbReplaceExp.append("^4");
			break;

		    case '\u2075': // ^5
			sbReplaceExp.append("^5");
			break;

		    case '\u2076': // ^6
			sbReplaceExp.append("^6");
			break;
			// added by Loïc Le Coq 2009/11/04
		    case '\u2077': // ^7
		    sbReplaceExp.append("^7");
			break;
		    
		    case '\u2078': // ^8
			sbReplaceExp.append("^8");
			break;
		    
		    case '\u2079': // ^9
			sbReplaceExp.append("^9");
			break;
			// end Loïc Le Coq

		    default:
			if (!convertGreekLetters) {
			    sbReplaceExp.append(c);
			} else {
			    switch (c) {
			    // greek letters
			    case '\u03b1':
				sbReplaceExp.append("\\alpha");
				break;

			    case '\u03b2':
				sbReplaceExp.append("\\beta");
				break;

			    case '\u03b3':
				sbReplaceExp.append("\\gamma");
				break;

			    case '\u03b4':
				sbReplaceExp.append("\\delta");
				break;

			    case '\u03b5':
				sbReplaceExp.append("\\varepsilon");
				break;

			    case '\u03b6':
				sbReplaceExp.append("\\zeta");
				break;

			    case '\u03b7':
				sbReplaceExp.append("\\eta");
				break;

			    case '\u03b8':
				sbReplaceExp.append("\\theta");
				break;

			    case '\u03b9':
				sbReplaceExp.append("\\iota");
				break;

			    case '\u03ba':
				sbReplaceExp.append("\\kappa");
				break;

			    case '\u03bb':
				sbReplaceExp.append("\\lambda");
				break;

			    case '\u03bc':
				sbReplaceExp.append("\\mu");
				break;

			    case '\u03bd':
				sbReplaceExp.append("\\nu");
				break;

			    case '\u03be':
				sbReplaceExp.append("\\xi");
				break;

			    case '\u03bf':
				sbReplaceExp.append("o");
				break;

			    case '\u03c0':
				sbReplaceExp.append("\\pi");
				break;

			    case '\u03c1':
				sbReplaceExp.append("\\rho");
				break;

			    case '\u03c3':
				sbReplaceExp.append("\\sigma");
				break;

			    case '\u03c4':
				sbReplaceExp.append("\\tau");
				break;

			    case '\u03c5':
				sbReplaceExp.append("\\upsilon");
				break;

			    case '\u03c6':
				sbReplaceExp.append("\\phi");
				break;

			    case '\u03c7':
				sbReplaceExp.append("\\chi");
				break;

			    case '\u03c8':
				sbReplaceExp.append("\\psi");
				break;

			    case '\u03c9':
				sbReplaceExp.append("\\omega");
				break;

			    // GREEK upper case letters				
			    case '\u0393':
				sbReplaceExp.append("\\Gamma");
				break;

			    case '\u0394':
				sbReplaceExp.append("\\Delta");
				break;

			    case '\u0398':
				sbReplaceExp.append("\\Theta");
				break;

			    case '\u039b':
				sbReplaceExp.append("\\Lambda");
				break;

			    case '\u039e':
				sbReplaceExp.append("\\Xi");
				break;

			    case '\u03a0':
				sbReplaceExp.append("\\Pi");
				break;

			    case '\u03a3':
				sbReplaceExp.append("\\Sigma");
				break;

			    case '\u03a6':
				sbReplaceExp.append("\\Phi");
				break;

			    case '\u03a8':
				sbReplaceExp.append("\\Psi");
				break;

			    case '\u03a9':
				sbReplaceExp.append("\\Omega");
				break;

			    default:
				sbReplaceExp.append(c);
			    }

			}
		    }
		}
		return sbReplaceExp.toString();
	    }

	    /**
	     * Converts the given unicode string 
	     * to a string where special characters are
	     * converted to <code>&#encoding;</code> sequences . 
	     * The resulting string can be used in XML files.
	     */
	    public static String encodeXML(String str) {
		if (str == null)
		    return "";

		//  convert every single character and append it to sb
		StringBuilder sb = new StringBuilder();
		int len = str.length();
		for (int i = 0; i < len; i++) {
		    char c = str.charAt(i);
		    switch (c) {
		    case '>':
			sb.append("&gt;");
			break;
		    case '<':
			sb.append("&lt;");
			break;
		    case '"':
			sb.append("&quot;");
			break;
		    case '\'':
			sb.append("&apos;");
			break;
		    case '&':
			sb.append("&amp;");
			break;
		    case '\n':
			sb.append("&#10;");
			break;

		    default:
			sb.append(c);
		    }
		}
		return sb.toString();
	    }
}
