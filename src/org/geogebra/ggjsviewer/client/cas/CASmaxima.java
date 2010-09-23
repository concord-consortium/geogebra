package org.geogebra.ggjsviewer.client.cas;

import org.geogebra.ggjsviewer.client.kernel.arithmetic.ExpressionNode;
import org.geogebra.ggjsviewer.client.kernel.arithmetic.ExpressionValue;
import org.geogebra.ggjsviewer.client.kernel.arithmetic.Function;
import org.geogebra.ggjsviewer.client.kernel.arithmetic.ValidExpression;
import org.geogebra.ggjsviewer.client.main.Application;





public class CASmaxima extends CASgeneric {
	
	final public String RB_GGB_TO_Maxima = "/geogebra/cas/ggb2maxima";
	
	//AGprivate MaximaInteractiveProcess ggbMaxima;
	//AGprivate ResourceBundle ggb2Maxima;
	
	public CASmaxima(CASparser casParser) {
		super(casParser);
	}
	
	/**
	 * Returns whether var is a defined variable in MathPiper.
	 */
	public boolean isVariableBound(String var) {
		// TODO: implement for Maxima
		
//		StringBuilder exp = new StringBuilder("IsBound(");
//		exp.append(var);
//		exp.append(')');
//		return "True".equals(evaluateMathPiper(exp.toString()));
		return false;
	}
	
	/**
	 * Unbinds (deletes) var in MathPiper.
	 * @param var
	 * @param isFunction
	 */
	public void unbindVariable(String var) {
		// TODO: implement for Maxima
		
//		StringBuilder sb = new StringBuilder();
//		
//		// clear function variable, e.g. Retract("f", *)
//		sb.append("[Retract(\"");
//		sb.append(var);
//		sb.append("\", *);");	
//
//		// clear variable, e.g. Unbind(f)
//		sb.append("Unbind(");
//		sb.append(var);
//		sb.append(");]");
//		
//		evaluateMathPiper(sb.toString());
	}

	/**
	 * Evaluates a valid expression and returns the resulting String in GeoGebra notation.
	 * @param casInput: in GeoGebraCAS syntax
	 * @param useGeoGebraVariables: whether GeoGebra objects should be substituted before evaluation
	 * @return evaluation result
	 * @throws Throwable
	 */
	public String evaluateGeoGebraCAS(ValidExpression casInput, boolean useGeoGebraVariables) throws Throwable {
		// convert parsed input to Maxima string
		String MaximaString = toMaximaString(casInput, useGeoGebraVariables);
		
		// now done in evaluateRaw
		//if (!MaximaString.endsWith(";")) MaximaString = MaximaString + ';';
			
		// EVALUATE input in Maxima 
		String result = evaluateMaxima(MaximaString);

		// convert Maxima result back into GeoGebra syntax
		String ggbString = toGeoGebraString(result);
		
		// TODO: remove
		System.out.println("eval with Maxima: " + MaximaString);
		System.out.println("   result: " + result);
		System.out.println("   ggbString: " + ggbString);
		
		return ggbString;
	}
	
	final synchronized public String getEvaluateGeoGebraCASerror() {
		// TODO: implement for Maxima
		return null;
		
//		if (response != null)
//			return response.getExceptionMessage();
//		else 
//			return null;
	}
	
	/**
	 * Tries to parse a given Maxima string and returns a String in GeoGebra syntax.
	 */
	public synchronized String toGeoGebraString(String maximaString) throws Throwable {
		ValidExpression ve = casParser.parseMaxima(maximaString);
		return casParser.toGeoGebraString(ve);
	}
	
	/**
	 * Evaluates the given ExpressionValue and returns the result in MathPiper syntax.
	 * 
	 * @param resolveVariables: states whether variables from the GeoGebra kernel 
	 *    should be used. Note that this changes the given ExpressionValue. 
	 */
	public synchronized String toMaximaString(ValidExpression ve, boolean resolveVariables) {
		
		// resolve global variables
		if (resolveVariables) {			
			casParser.resolveVariablesForCAS(ve);
		}	
		
		// convert to Maxima String
		String MaximaStr = doToMaximaString(ve, resolveVariables);

		// handle assignments
		String veLabel = ve.getLabel();
		if (veLabel != null) {
			StringBuilder sb = new StringBuilder();
			
			if (ve instanceof Function) {
				// function, e.g. f(x) := 2*x
				Function fun = (Function) ve;
				sb.append(veLabel);
				sb.append("(" );
				sb.append(fun.getFunctionVariable());
				sb.append(") := ");
				
				// evaluate right hand side:
				// import for e.g. g(x) := Eval(D(x) x^2)
				//sb.append("Eval(");
				sb.append(MaximaStr);
				//sb.append(")");
				MaximaStr = sb.toString();
			} else {	
				// assignment, e.g. a := 5
				MaximaStr = veLabel + " := " + MaximaStr;
			}
		}
		
		// TODO: remove
		System.out.println("CASmaxima.toMaxima: " + MaximaStr);
		return MaximaStr;
	}	
	
	/**
	 * Returns the given expression as a string in Maxima syntax.
	 */
	private String doToMaximaString(ExpressionValue ev, boolean substituteVariables) {
		String MathPiperString;
		
		if (!ev.isExpressionNode()) {
			ev = new ExpressionNode(casParser.getKernel(), ev);			
		}
		
		MathPiperString = ((ExpressionNode) ev).getCASstring(ExpressionNode.STRING_TYPE_MAXIMA, !substituteVariables);		
				
		return MathPiperString;
	}

	
    /**
	 * Evaluates a Maxima expression and returns the result as a string in Maxima syntax, 
	 * e.g. evaluateMaxima("integrate (sin(x)^3, x);") returns "cos(x)^3/3-cos(x)".
	 * 
	 * @return result string (null possible)
	 */
	final synchronized public String evaluateMaxima(String exp) {
		//AGtry {
			String result;
						
			// MathPiper has problems with indices like a_3, b_{12}
			exp = casParser.replaceIndices(exp);
			
			// Maxima uses [] for lists
			while (exp.indexOf('{') > -1 ) exp = exp.replace('{', '[');
			while (exp.indexOf('}') > -1 ) exp = exp.replace('}', ']');
			
			final boolean debug = true;
			if (debug) Application.debug("Expression for Maxima: "+exp);
			
			// evaluate the MathPiper expression
			//RawMaximaSession maxima = getMaxima();
			
			//result = maxima.executeExpectingSingleOutput(exp);
			//String results[] = maxima.executeExpectingMultipleLabels(exp);			
			//result = results[results.length - 1];
			
			//String results[] = executeRaw(exp).split("\n");
			
			/*AGString res = executeRaw(exp);
			
			while (res.indexOf('\n') > -1 ) res = res.replace('\n', ' ');
			
			String results[] = res.split("\\(%[oi]\\d+\\)\\s*");
			
			result = results[results.length - 1];
			
			// if last line is empty, look for next non-empty previous line
			if (result.trim().length() == 0 && results.length > 1) {
				int i = results.length - 2;
				while (results[i].trim().length() == 0 && i > 0) i--;
				result = results[i];
			}
			
			// remove (%o1) at start
			//result = result.replaceFirst("\\(%o\\d+\\)\\s*", "").trim();
			
			
			if (debug) {
				for (int i = 0 ; i < results.length ; i++)
					System.err.println("Result "+i+": "+results[i]);
				System.out.println("result: "+result);
			}
				
			// undo special character handling
			result = casParser.insertSpecialChars(result);
			
			// replace eg [x=0,x=1] with {x=0,x=1}
			while (result.indexOf('[') > -1) result = result.replace('[','{');
			while (result.indexOf(']') > -1) result = result.replace(']','}');

			return result;
		} catch (MaximaTimeoutException e) {
			Application.debug("Timeout from Maxima, resetting");
			ggbMaxima = null;
			return "?";
		}*/
			return "evaluateMaximaExpression must be implemented";
	}

	@Override
	public String getTranslatedCASCommand(String command) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Returns the Maxima command for the given key (from ggb2Maxima.properties)
	 */ 
	/*AGpublic synchronized String getTranslatedCASCommand(String key) {
		if (ggb2Maxima == null) {
			ggb2Maxima = MyResourceBundle.loadSingleBundleFile(RB_GGB_TO_Maxima);
		}
		
		String ret;
		try {
			ret =  ggb2Maxima.getString(key);
		} catch (MissingResourceException e) {
			ret = null;
		}

		return ret;
	}
	
	
	private synchronized MaximaInteractiveProcess getMaxima() {
		if (ggbMaxima == null) {
			MaximaConfiguration configuration = casParser.getKernel().getApplication().maximaConfiguration;
			
			if (configuration == null) configuration = JacomaxAutoConfigurator.guessMaximaConfiguration();
			
			MaximaProcessLauncher launcher = new MaximaProcessLauncher(configuration);
			ggbMaxima = launcher.launchInteractiveProcess();
			try {
				initMyMaximaFunctions();
				System.out.println(ggbMaxima.executeCall("1+2;"));
			} catch (MaximaTimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//process.terminate();		
		}
		

				
				
				

			
		
	
		return ggbMaxima;
	}
	
	/**
	 * Resets the cas and unbinds all variable and function definitions.
	 */
	/*AGpublic void reset() {
		ggbMaxima = null;
		getMaxima();
	}
	
	private void initMyMaximaFunctions() throws MaximaTimeoutException, geogebra.cas.jacomax.MaximaTimeoutException {
	
		// set line length of "terminal"
		// we don't want lines broken
	    ggbMaxima.executeCall("linel:1000000;");
	    
		// make sure results are returned
	    ggbMaxima.executeCall("display2d:false;");
	    
	    // make sure integral(1/x) = log(abs(x))
	    ggbMaxima.executeCall("logabs:true;");
	    
	    // make sure algsys (solve) doesn't return complex roots
	    ggbMaxima.executeCall("realonly:true;");
	    
	    // eg x^-1 displayed as 1/x
	    ggbMaxima.executeCall("exptdispflag:true;");
	    
	    // suppresses the printout of the message informing the user of the conversion of floating point numbers to rational numbers
	    ggbMaxima.executeCall("ratprint:false;");
	    
	    // When true, r some rational number, and x some expression, %e^(r*log(x)) will be simplified into x^r . It should be noted that the radcan command also does this transformation, and more complicated transformations of this ilk as well. The logcontract command "contracts" expressions containing log. 
	    ggbMaxima.executeCall("%e_to_numlog:true;");
	    
	    
	    // define custom functions
	    ggbMaxima.executeCall("log10(x) := log(x) / log(10);");
	    ggbMaxima.executeCall("log2(x) := log(x) / log(2);");
	    ggbMaxima.executeCall("cbrt(x) := x^(1/3);");
	    
	    // needed to define lcm()
	    ggbMaxima.executeCall("load(functs)$");
	    
	    // needed for degree()
	    ggbMaxima.executeCall("load(powers)$");
	       
	    // needed for ???
	    ggbMaxima.executeCall("load(format)$");
	       
	    // turn {x=3} into {3} etc
	    ggbMaxima.executeCall("stripequals(ex):=block(" +
	    		 "if atom(ex) then return(ex)" +
	    		 "else if op(ex)=\"=\" then return(stripequals(rhs(ex)))" +
	    		 "else apply(op(ex),map(stripequals,args(ex)))" +
	    		")$");
	    
	    /* This function takes an expression ex and returns a list of coefficients of v */
	  /*AG  ggbMaxima.executeCall("coefflist(ex,v):= block([deg,kloop,cl]," +
	    		"cl:[]," +
	      "ex:ev(expand(ex),simp)," +
	      "deg:degree(ex,v)," +
	      "ev(for kloop:0 thru deg do\n" +
	      "cl:append(cl,[coeff(ex,v,kloop)]),simp)," +
	      "cl" +
	      ")$");
	   
	    /*
	     * eg integrate(x^n,x) asks if n+1 is zero
	     * this disables the interactivity
	     * but we get:
	     * if equal(n+1,0) then log(abs(x)) else x^(n+1)/(n+1)
	     * TODO: change to ggb syntax
	     */
	   //AG ggbMaxima.executeCall("load(\"noninteractive\");");

	    
	    // define Degree
	    //AGggbMaxima.executeCall("Degree:180/%pi;");

	//AG}

	/*AGprivate String executeRaw(String maximaInput) throws MaximaTimeoutException, geogebra.cas.jacomax.MaximaTimeoutException {
        char lastChar = maximaInput.charAt(maximaInput.length() - 1);
        if (lastChar != ';' && lastChar != '$' && !maximaInput.startsWith(":lisp")) {
        	maximaInput += ";";
        }    
       
        return getMaxima().executeCall(maximaInput);

	}*/
}