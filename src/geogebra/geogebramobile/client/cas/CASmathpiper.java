package geogebra.geogebramobile.client.cas;

import geogebra.geogebramobile.client.kernel.arithmetic.ExpressionNode;
import geogebra.geogebramobile.client.kernel.arithmetic.ExpressionValue;
import geogebra.geogebramobile.client.kernel.arithmetic.Function;
import geogebra.geogebramobile.client.kernel.arithmetic.ValidExpression;

import com.google.gwt.i18n.rebind.AbstractResource.MissingResourceException;



public class CASmathpiper extends CASgeneric {
	
	final public String RB_GGB_TO_MathPiper = "/geogebra/cas/ggb2mathpiper";

	//AGprivate Interpreter ggbMathPiper;
	//AGprivate EvaluationResponse response;
	//AGprivate ResourceBundle ggb2MathPiper;
	
	public CASmathpiper(CASparser casParser) {
		super(casParser);
		//AGgetMathPiper();
	}
	
	/*AGprivate synchronized Interpreter getMathPiper() {				
		if (ggbMathPiper == null) {
			ggbMathPiper = org.mathpiper.interpreters.Interpreters.newSynchronousInterpreter();
			initMyMathPiperFunctions();
		}
		
		return ggbMathPiper;
	}	
	
	/**
	 * Resets the cas and unbinds all variable and function definitions.
	 */
	public void reset() {
		//AGggbMathPiper = null;
		//AGgetMathPiper();
	}
	
	/**
	 * Returns whether var is a defined variable in MathPiper.
	 */
	public boolean isVariableBound(String var) {
		StringBuilder exp = new StringBuilder("IsBound(");
		exp.append(var);
		exp.append(')');
		return "True".equals(evaluateMathPiper(exp.toString()));
	}
	
	/**
	 * Unbinds (deletes) var in MathPiper.
	 * @param var
	 * @param isFunction
	 */
	public void unbindVariable(String var) {
		StringBuilder sb = new StringBuilder();
		
		// clear function variable, e.g. Retract("f", *)
		sb.append("[Retract(\"");
		sb.append(var);
		sb.append("\", *);");	

		// clear variable, e.g. Unbind(f)
		sb.append("Unbind(");
		sb.append(var);
		sb.append(");]");
		
		evaluateMathPiper(sb.toString());
	}
	
	/**
	 * Evaluates a valid expression in GeoGebraCAS syntax and returns the resulting String in GeoGebra notation.
	 * @param casInput: in GeoGebraCAS syntax
	 * @param useGeoGebraVariables: whether GeoGebra objects should be substituted before evaluation
	 * @return evaluation result
	 * @throws Throwable
	 */
	public synchronized String evaluateGeoGebraCAS(ValidExpression casInput, boolean useGeoGebraVariables) throws Throwable {
		// convert parsed input to MathPiper string
		String MathPiperString = toMathPiperString(casInput, useGeoGebraVariables);
			
		// EVALUATE input in MathPiper 
		String result = evaluateMathPiper(MathPiperString);

		// convert MathPiper result back into GeoGebra syntax
		String ggbString = toGeoGebraString(result);
		
		// TODO: remove
		System.out.println("eval with MathPiper: " + MathPiperString);
		System.out.println("   result: " + result);
		System.out.println("   ggbString: " + ggbString);
		
		return ggbString;
	}
	
	/**
	 * Tries to parse a given MathPiper string and returns a String in GeoGebra syntax.
	 */
	public synchronized String toGeoGebraString(String MathPiperString) throws Throwable {
		ValidExpression ve = casParser.parseMathPiper(MathPiperString);
		return casParser.toGeoGebraString(ve);
	}
	
    /**
	 * Evaluates a MathPiper expression and returns the result as a string in MathPiper syntax, 
	 * e.g. evaluateMathPiper("D(x) (x^2)") returns "2*x".
	 * 
	 * @return result string (null possible)
	 */
	final synchronized public String evaluateMathPiper(String exp) {
	/*AG	try {
			String result;

			// MathPiper has problems with indices like a_3, b_{12}
			exp = casParser.replaceIndices(exp);
//			
//			final boolean debug = true;
//			if (debug) Application.debug("Expression for mathPiper: "+exp);
			
			// evaluate the MathPiper expression
			//AGfinal Interpreter mathpiper = getMathPiper();
			
			
			 //AGEvaluationResponse response;


			 // timeout needed for eg Limit((Sin(1/x)*x^2-x*Cos(1/x))/x^2,-Infinity)
		       //AGfinal Timer timer = new Timer();

		       //AGtimer.schedule(new TimerTask() {
		           public void run() {
		        	  //AG mathpiper.haltEvaluation();
		            //AG   timer.cancel();
		           }

		       }, GeoGebraCAS.CAS_TIMEOUT * 1000); //Time out after three seconds.
		       
		       response = mathpiper.evaluate(exp);
		       timer.cancel();
		    	          
			
			if (response.isExceptionThrown())
			{
				System.err.println("evaluateMathPiper: "+exp+"\n  Exception: "+response.getExceptionMessage());
				return "?";
			}
			result = response.getResult();
			
			//if (debug) System.out.println("Result: "+result);
					
			// undo special character handling
			result = casParser.insertSpecialChars(result);

			return result;
		} catch (Throwable th) {
			//MathPiper.Evaluate("restart;");
			th.printStackTrace();
			return null;
		}*/
		return "evalutemathpiper must be implemented";
	}
/*AG	
	final synchronized public String getEvaluateGeoGebraCASerror() {
		if (response != null)
			return response.getExceptionMessage();
		else 
			return null;
	}
*/	
	/**
	 * Returns the MathPiper command for the given key (from ggb2MathPiper.properties)
	 */ 
	public synchronized String getTranslatedCASCommand(String key) {
	/*AG	if (ggb2MathPiper == null) {
			ggb2MathPiper = MyResourceBundle.loadSingleBundleFile(RB_GGB_TO_MathPiper);
		}
		
		String ret;
		try {
			ret =  ggb2MathPiper.getString(key);
		} catch (MissingResourceException e) {
			ret = null;
		}*/

		return "getTranslatedCASCommand must be implemented";
	}
	
	/**
	 * Evaluates the given ExpressionValue and returns the result in MathPiper syntax.
	 * 
	 * @param resolveVariables: states whether variables from the GeoGebra kernel 
	 *    should be used. Note that this changes the given ExpressionValue. 
	 */
	public synchronized String toMathPiperString(ValidExpression ve, boolean resolveVariables) {
		
		// resolve global variables
		if (resolveVariables) {			
			casParser.resolveVariablesForCAS(ve);
		}	
		
		// convert to MathPiper String
		String MathPiperStr = doToMathPiperString(ve, resolveVariables);

		// handle assignments
		String veLabel = ve.getLabel();
		boolean assignment = veLabel != null;
		if (assignment) {
			StringBuilder sb = new StringBuilder();
			
			if (ve instanceof Function) {
				// function, e.g. f(x) := 2*x
				Function fun = (Function) ve;
				sb.append(veLabel);
				sb.append("(" );
				sb.append(fun.getFunctionVariable());
				sb.append(") := ");
				sb.append(MathPiperStr);
				MathPiperStr = sb.toString();
			} else {	
				// assignment, e.g. a := 5
				MathPiperStr = veLabel + " := " + MathPiperStr;
			}
		}
		
		//System.out.println("GeoGebraCAS.toMathPiperString: " + MathPiperStr);
		return MathPiperStr;
	}
	
	
	/**
	 * Returns the given expression as a string in MathPiper syntax.
	 */
	private String doToMathPiperString(ExpressionValue ev, boolean substituteVariables) {
		String MathPiperString;
		if (!ev.isExpressionNode()) {
			ev = new ExpressionNode(casParser.getKernel(), ev);			
		}
		
		MathPiperString = ((ExpressionNode) ev).getCASstring(ExpressionNode.STRING_TYPE_MATH_PIPER, !substituteVariables);			
		return MathPiperString;
	}

	@Override
	public String getEvaluateGeoGebraCASerror() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Initialize special commands needed in our ggbMathPiper instance,e.g.
	 * getPolynomialCoeffs(exp,x).
	 */
	/*AGprivate synchronized boolean initMyMathPiperFunctions() {		
// Expand expression and get polynomial coefficients using MathPiper:
//		getPolynomialCoeffs(expr,x) :=
//			       If( CanBeUni(expr),
//			           [
//							Coef(MakeUni(expr,x),x, 0 .. Degree(expr,x));			           ],
//			           {};
//			      );
		String strGetPolynomialCoeffs = "getPolynomialCoeffs(expr,x) := If( CanBeUni(expr),[ Coef(MakeUni(expr,x),x, 0 .. Degree(expr,x));],{});";
		EvaluationResponse resp = ggbMathPiper.evaluate(strGetPolynomialCoeffs);
		if (resp.isExceptionThrown()) {
			return false;
		}
		
		// define constant for Degree
		response = ggbMathPiper.evaluate("Degree := 180/Pi;");
		
		// set default numeric precision to 16 significant figures
		ggbMathPiper.evaluate("BuiltinPrecisionSet(16);");
		
		// Rules for equation manipulation
		// allow certain commands for equations
		ggbMathPiper.evaluate("NotEqu(exp) := Not( IsEquation(exp));");
		
		//ggbMathPiper.evaluate("CheckInput( (x_NotEqu == y_NotEqu) + z_NotEqu ) <-- Subst(a, x) Subst(b, y) Subst(c, z) (Hold(a + c) ==  Hold(b + c)) ;");
		
		
		// standard commands for equations
		ggbMathPiper.evaluate("Simplify(x_NotEqu == y_NotEqu)  <-- Simplify(x) == Simplify(y);");
		ggbMathPiper.evaluate("Factor(x_NotEqu == y_NotEqu)  <-- Factor(x) == Factor(y);");
		ggbMathPiper.evaluate("Expand(x_NotEqu == y_NotEqu)  <-- Expand(x) == Expand(y);");
		ggbMathPiper.evaluate("ExpandBrackets(x_NotEqu == y_NotEqu)  <-- ExpandBrackets(x) == ExpandBrackets(y);");
		ggbMathPiper.evaluate("Sqrt(x_NotEqu == y_NotEqu)  <-- Sqrt(x) == Sqrt(y);");
		ggbMathPiper.evaluate("Exp(x_NotEqu == y_NotEqu)  <-- Exp(x) == Exp(y);");
		ggbMathPiper.evaluate("Ln(x_NotEqu == y_NotEqu)  <-- Ln(x) == Ln(y);");
		
		// arithmetic for equations and scalars
		ggbMathPiper.evaluate("(x_NotEqu == y_NotEqu) + z_NotEqu <-- x + z == y + z;");
		ggbMathPiper.evaluate("z_NotEqu + (x_NotEqu == y_NotEqu) <-- z + x == z + y;");
		ggbMathPiper.evaluate("(x_NotEqu == y_NotEqu) - z_NotEqu <-- x - z == y - z;");
		ggbMathPiper.evaluate("z_NotEqu - (x_NotEqu == y_NotEqu) <-- z - x == z - y;");
		ggbMathPiper.evaluate("(x_NotEqu == y_NotEqu) * z_NotEqu <-- x * z == y * z;");
		ggbMathPiper.evaluate("z_NotEqu * (x_NotEqu == y_NotEqu) <-- z * x == z * y;");
		ggbMathPiper.evaluate("(x_NotEqu == y_NotEqu) / z_NotEqu <-- x / z == y / z;");
		ggbMathPiper.evaluate("z_NotEqu / (x_NotEqu == y_NotEqu) <-- z / x == z / y;");
		ggbMathPiper.evaluate("(x_NotEqu == y_NotEqu) ^ z_NotEqu <-- x ^ z == y ^ z;");
		ggbMathPiper.evaluate("z_NotEqu ^ (x_NotEqu == y_NotEqu) <-- z ^ x == z ^ y;");
		
		// arithmetic for two equations
		ggbMathPiper.evaluate("(a_NotEqu == b_NotEqu) + (c_NotEqu == d_NotEqu) <-- a + c == b + d;");
		ggbMathPiper.evaluate("(a_NotEqu == b_NotEqu) - (c_NotEqu == d_NotEqu) <-- a - c == b - d;");
		ggbMathPiper.evaluate("(a_NotEqu == b_NotEqu) * (c_NotEqu == d_NotEqu) <-- a * c == b * d;");
		ggbMathPiper.evaluate("(a_NotEqu == b_NotEqu) / (c_NotEqu == d_NotEqu) <-- a / c == b / d;");
		
		
		//ggbMathPiper.evaluate("CheckInput(x_IsAtom)  <-- Simplify(x) == Simplify(y);");
		
		
		return true;
	}*/
}