package geogebra.kernel.arithmetic;

public interface ExpressionNodeConstants {

	

	public static final int STRING_TYPE_GEOGEBRA_XML = 0;
	public static final int STRING_TYPE_GEOGEBRA = 1;
	public static final int STRING_TYPE_MAXIMA = 2;
	public static final int STRING_TYPE_MATH_PIPER = 3;
	public static final int STRING_TYPE_LATEX = 4;
	public static final int STRING_TYPE_PSTRICKS = 5;
	public static final int STRING_TYPE_PGF = 6;
	public static final int STRING_TYPE_JASYMCA = 7;
	
	public static final String UNICODE_PREFIX = "uNiCoDe";
	public static final String UNICODE_DELIMITER = "U";  
	
	public static final int NO_OPERATION = Integer.MIN_VALUE; 
    
	// boolean
	public static final int NOT_EQUAL = -15;
	public static final int NOT = -14;
	public static final int OR = -13;
    public static final int AND = -12;
    public static final int EQUAL_BOOLEAN = -11;
    public static final int LESS = -10;
    public static final int GREATER = -9;
    public static final int LESS_EQUAL = -8;
    public static final int GREATER_EQUAL = -7;    
    public static final int PARALLEL = -6;  
    public static final int PERPENDICULAR = -5;
    public static final int IS_ELEMENT_OF = -4;
    public static final int CONTAINS = -3;
    public static final int CONTAINS_STRICT = -2;
    public static final int SET_DIFFERENCE = -1;
    
    public static final String strNOT = "\u00ac";
    public static final String strAND = "\u2227";
    public static final String strOR = "\u2228";
    public static final String strLESS_EQUAL = "\u2264";
    public static final String strGREATER_EQUAL = "\u2265";
    public static final String strEQUAL_BOOLEAN = "\u225f";
    public static final String strNOT_EQUAL = "\u2260";
    public static final String strPARALLEL = "\u2225";
    public static final String strPERPENDICULAR = "\u22a5";
    public static final String strVECTORPRODUCT = "\u2297";
    public static final String strIS_ELEMENT_OF = "\u2208";
    public static final String strCONTAINS = "\u2286";
    public static final String strCONTAINS_STRICT = "\u2282";
    public static final String strSET_DIFFERENCE = "\\";
        
    // arithmetic
    public static final int PLUS = 0;
    public static final int MINUS = 1;
    
    // these next three must be adjacent
    // so that brackets work for eg a/(b/c)
    // and are removed in (a/b)/c
    // see case DIVIDE in ExpressionNode
    public static final int MULTIPLY = 2;
    public static final int DIVIDE = 3;
    public static final int POWER = 4;            
    
    
    public static final int COS = 5;   
    public static final int SIN = 6;   
    public static final int TAN = 7;   
    public static final int EXP = 8;   
    public static final int LOG = 9;   
    public static final int ARCCOS = 10;   
    public static final int ARCSIN = 11;   
    public static final int ARCTAN = 12;   
    public static final int SQRT = 13;   
    public static final int ABS = 14;   
    public static final int SGN = 15;   
    public static final int XCOORD = 16; 
    public static final int YCOORD = 17;  
    public static final int ZCOORD = 18;  
    public static final int COSH = 19;
    public static final int SINH = 20;
    public static final int TANH = 21;
    public static final int ACOSH = 22;
    public static final int ASINH = 23;
    public static final int ATANH = 24;
    public static final int CSC = 25;
    public static final int SEC = 26;
    public static final int COT = 27;
    public static final int CSCH = 28;
    public static final int SECH = 29;
    public static final int COTH = 30;
    public static final int FLOOR = 31;
    public static final int CEIL = 32;  
    public static final int FACTORIAL = 33;
    public static final int ROUND = 34;  
    public static final int GAMMA = 35;    
    public static final int LOG10 = 36;  
    public static final int LOG2 = 37; 
    public static final int CBRT = 38;   
    public static final int RANDOM = 39;
    public static final int VECTORPRODUCT = 40;
    public static final int CONJUGATE = 41;
    public static final int ARG = 42;
     
    public static final int FUNCTION = 43;
    public static final int FUNCTION_NVAR = 44;
    public static final int VEC_FUNCTION = 45;
    public static final int DERIVATIVE = 46;  
    public static final int ELEMENT_OF = 47;  
    
    
    // spreadsheet absolute reference using $ signs
    public static final int $VAR_ROW = 48;
    public static final int $VAR_COL = 49;
    public static final int $VAR_ROW_COL = 50;
	
}
