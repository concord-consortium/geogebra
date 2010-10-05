/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package org.geogebra.ggjsviewer.client.kernel.commands;



import java.util.HashMap;
import java.util.Set;

import org.geogebra.ggjsviewer.client.kernel.Construction;
import org.geogebra.ggjsviewer.client.kernel.GeoElement;
import org.geogebra.ggjsviewer.client.kernel.Kernel;
import org.geogebra.ggjsviewer.client.kernel.Macro;
import org.geogebra.ggjsviewer.client.kernel.arithmetic.Command;
import org.geogebra.ggjsviewer.client.main.Application;
import org.geogebra.ggjsviewer.client.main.MyError;

import com.google.gwt.core.client.GWT;


public class CommandDispatcher {
    
	private Kernel kernel;
    private Construction cons;
    private Application app;
    
    // stores (String name, CommandProcessor cmdProc) pairs   
    protected HashMap cmdTable;
    private MacroProcessor macroProc;
    
    public CommandDispatcher(Kernel kernel) {             
    	this.kernel = kernel;
    	cons = kernel.getConstruction();  
    	app = kernel.getBaseApplication();                    
    }
    
    /**
     * Returns a set with all command names available
     * in the GeoGebra input field.
     */
    public Set getPublicCommandSet() {
    	if (cmdTable == null) {
    		initCmdTable();
    	}  
    	
    	return cmdTable.keySet();
    }
    
    /**
     * @param labelOutput: specifies if output GeoElements of this command should get labels
     */
    final public GeoElement[] processCommand(Command c, boolean labelOutput)
        throws MyError {
    	
    	if (cmdTable == null) {
    		initCmdTable();
    	}    	        

        // switch on macro mode to avoid labeling of output if desired
        boolean oldMacroMode = cons.isSuppressLabelsActive();
        if (!labelOutput)
            cons.setSuppressLabelCreation(true);
        
        // cmdName
        String cmdName = c.getName();
        CommandProcessor cmdProc;
        
        // MACRO: is there a macro with this command name?        
       Macro macro = kernel.getMacro(cmdName);
        if (macro != null) {    
        	c.setMacro(macro);
        	cmdProc = macroProc;
        	GWT.log("CommandDispatcher macro 83 needed");
        } 
        // STANDARD CASE
        else {
        	// get CommandProcessor object for command name from command table
        	cmdProc = (CommandProcessor) cmdTable.get(cmdName);            
        }
                
        GeoElement[] ret = null;
        try {            
	        ret = cmdProc.process(c);	                       	        	        
        } 
        catch (MyError e) {
        	cons.setSuppressLabelCreation(oldMacroMode);
            throw e;
        } catch (Exception e) {        	  
            cons.setSuppressLabelCreation(oldMacroMode);        	  
            e.printStackTrace();
            throw new MyError(app, app.getError("UnknownCommand") + " : " + 
            		app.getCommand(c.getName()));
        }
        
        // remember macro command used:
        // this is needed when a single tool A[] is exported to find
        // all other tools that are needed for A[]
        //AGif (macro != null)
        //AG	cons.addUsedMacro(macro);
        
              		
        cons.setSuppressLabelCreation(oldMacroMode);        
        
        return ret;
    }
           
    protected void initCmdTable() {    	 
    	//AGmacroProc = new MacroProcessor(kernel);    	    	
    	
    	// external commands: visible to users    	    	
    	cmdTable = new HashMap(500);
    	cmdTable.put("UnitVector", new CmdUnitVector(kernel));	   
    	//AGcmdTable.put("SecondAxis", new CmdSecondAxis(kernel));	   
    	//AGcmdTable.put("CircleArc", new CmdCircleArc(kernel));	   
    	//AGcmdTable.put("Parameter", new CmdParameter(kernel));	   
    	//AGcmdTable.put("TurningPoint", new CmdTurningPoint(kernel));	   
    	cmdTable.put("Derivative", new CmdDerivative(kernel));	   
    	//AGcmdTable.put("Integral", new CmdIntegral(kernel));	   
    	//AGcmdTable.put("LowerSum", new CmdLowerSum(kernel));	   
    	//AGcmdTable.put("Root", new CmdRoot(kernel));	   
    	//AGcmdTable.put("Dilate", new CmdDilate(kernel));	   
    	cmdTable.put("Vector", new CmdVector(kernel));	   
    	cmdTable.put("Ellipse", new CmdEllipse(kernel));	   
    	cmdTable.put("Hyperbola", new CmdHyperbola(kernel));	   
    	//AGcmdTable.put("TaylorSeries", new CmdTaylorSeries(kernel));	   
    	//AGcmdTable.put("SecondAxisLength", new CmdSecondAxisLength(kernel));	   
    	cmdTable.put("Ray", new CmdRay(kernel));	   
    	cmdTable.put("AngularBisector", new CmdAngularBisector(kernel));	   
    	cmdTable.put("Angle", new CmdAngle(kernel));	   
    	//AGcmdTable.put("Corner", new CmdCorner(kernel));	   
    	cmdTable.put("Midpoint", new CmdMidpoint(kernel));	 
    	cmdTable.put("Polygon", new CmdPolygon(kernel));
    	//AGcmdTable.put("Direction", new CmdDirection(kernel));	   
    	//AGcmdTable.put("Polynomial", new CmdPolynomial(kernel));	   
    	//AGcmdTable.put("Tangent", new CmdTangent(kernel));	   
    	//AGcmdTable.put("UnitOrthogonalVector", new CmdUnitOrthogonalVector(kernel));	   
    	//AGcmdTable.put("Distance", new CmdDistance(kernel));	   
    	//AGcmdTable.put("Asymptote", new CmdAsymptote(kernel));	   
    	cmdTable.put("Mirror", new CmdMirror(kernel));	   
    	cmdTable.put("Center", new CmdCenter(kernel));	   
    	cmdTable.put("Directrix", new CmdDirectrix(kernel));	   
    	//AGcmdTable.put("Diameter", new CmdDiameter(kernel));
    	cmdTable.put("OrthogonalLine", new CmdOrthogonalLine(kernel));	  
    	cmdTable.put("Line", new CmdLine(kernel));	   
    	cmdTable.put("Intersect", new CmdIntersect(kernel));	   
    	//AGcmdTable.put("CircumcircleSector", new CmdCircumcircleSector(kernel));	   
    	//AGcmdTable.put("Focus", new CmdFocus(kernel));	   
    	//AGcmdTable.put("OrthogonalVector", new CmdOrthogonalVector(kernel));	   
    	//AGcmdTable.put("Length", new CmdLength(kernel));	   
    	//AGcmdTable.put("Delete", new CmdDelete(kernel));	   
    	//AGcmdTable.put("Radius", new CmdRadius(kernel));	   
    	//AGcmdTable.put("Arc", new CmdArc(kernel));	   
    	//AGcmdTable.put("CircleSector", new CmdCircleSector(kernel));	   
    	//AGcmdTable.put("Polar", new CmdPolar(kernel));	   
    	cmdTable.put("Semicircle", new CmdSemicircle(kernel));	   
    	//AGcmdTable.put("FirstAxisLength", new CmdFirstAxisLength(kernel));	   
    	cmdTable.put("Parabola", new CmdParabola(kernel));	   
    	cmdTable.put("Rotate", new CmdRotate(kernel));	   
    	//AGcmdTable.put("Function", new CmdFunction(kernel));	   
    	//AGcmdTable.put("Extremum", new CmdExtremum(kernel));	   
    	//AGcmdTable.put("CircumcircleArc", new CmdCircumcircleArc(kernel));	   
    	//AGcmdTable.put("Translate", new CmdTranslate(kernel));	   
    	// linear eccentricity (used in Germany etc) LinearExcentricity[]
    	//AGcmdTable.put("Excentricity", new CmdExcentricity(kernel));	  
    	// eccentricity
    	//AGcmdTable.put("Eccentricity", new CmdEccentricity(kernel));	   
    	//AGcmdTable.put("OrthogonalLine", new CmdOrthogonalLine(kernel));	   
    	//AGcmdTable.put("Relation", new CmdRelation(kernel));	   
    	//AGcmdTable.put("Polygon", new CmdPolygon(kernel));	   
    	cmdTable.put("Segment", new CmdSegment(kernel));	   
    	//AGcmdTable.put("Sector", new CmdSector(kernel));	   
    	//AGcmdTable.put("Locus", new CmdLocus(kernel));	   
    	//AGcmdTable.put("Centroid", new CmdCentroid(kernel));	   
    	//AGcmdTable.put("Vertex", new CmdVertex(kernel));	   
    	//AGcmdTable.put("Conic", new CmdConic(kernel));	   
    	//AGcmdTable.put("FirstAxis", new CmdFirstAxis(kernel));	   
    	cmdTable.put("Circle", new CmdCircle(kernel));	   
    	cmdTable.put("LineBisector", new CmdLineBisector(kernel));	   
    	cmdTable.put("Area", new CmdArea(kernel));	   
    	//AGcmdTable.put("Slope", new CmdSlope(kernel));	   
    	//AGcmdTable.put("Axes", new CmdAxes(kernel));	   
    	cmdTable.put("Point", new CmdPoint(kernel));	   
    	//AGcmdTable.put("UpperSum", new CmdUpperSum(kernel));    	  
    	//AGcmdTable.put("If", new CmdIf(kernel));
    	//AGcmdTable.put("Sequence", new CmdSequence(kernel));    	
    	//AGcmdTable.put("CurveCartesian", new CmdCurveCartesian(kernel));
    	
    	// Victor Franco Espino 18-04-2007: New commands
    	//AGcmdTable.put("AffineRatio", new CmdAffineRatio(kernel));
    	//AGcmdTable.put("CrossRatio", new CmdCrossRatio(kernel));
    	//AGcmdTable.put("CurvatureVector", new CmdCurvatureVector(kernel));
    	//AGcmdTable.put("Curvature", new CmdCurvature(kernel));
    	//AGcmdTable.put("OsculatingCircle", new CmdOsculatingCircle(kernel));
    	// Victor Franco Espino 18-04-2007: End new commands
    	
    	// Philipp Weissenbacher 10-04-2007
    	//AGcmdTable.put("Circumference", new CmdCircumference(kernel));
    	//AGcmdTable.put("Perimeter", new CmdPerimeter(kernel));
    	// Philipp Weissenbacher 10-04-2007
    	
    	//AGcmdTable.put("Mod", new CmdMod(kernel));
    	//AGcmdTable.put("Div", new CmdDiv(kernel));
    	//AGcmdTable.put("Min", new CmdMin(kernel));
    	//AGcmdTable.put("Max", new CmdMax(kernel));
    	//AGcmdTable.put("LCM", new CmdLCM(kernel));
    	//AGcmdTable.put("GCD", new CmdGCD(kernel));
    	//AGcmdTable.put("Sort", new CmdSort(kernel));
    	//AGcmdTable.put("First", new CmdFirst(kernel));
    	//AGcmdTable.put("Last", new CmdLast(kernel));
    	//AGcmdTable.put("Take", new CmdTake(kernel));
    	//AGcmdTable.put("RemoveUndefined", new CmdRemoveUndefined(kernel));
    	//AGcmdTable.put("Defined", new CmdDefined(kernel));
    	//AGcmdTable.put("Sum", new CmdSum(kernel));
    	//AGcmdTable.put("Product", new CmdProduct(kernel));
    	//AGcmdTable.put("Mean", new CmdMean(kernel));
    	//AGcmdTable.put("Variance", new CmdVariance(kernel));
    	//AGcmdTable.put("SD", new CmdSD(kernel));
    	//AGcmdTable.put("SampleVariance", new CmdSampleVariance(kernel));
    	//AGcmdTable.put("SampleSD", new CmdSampleSD(kernel));
    	//AGcmdTable.put("Median", new CmdMedian(kernel));
    	//AGcmdTable.put("Q1", new CmdQ1(kernel));
    	//AGcmdTable.put("Q3", new CmdQ3(kernel));
    	//AGcmdTable.put("Mode", new CmdMode(kernel));
    	//AGcmdTable.put("Reverse", new CmdReverse(kernel));
    	//AGcmdTable.put("SigmaXX", new CmdSigmaXX(kernel));
    	//AGcmdTable.put("SigmaXY", new CmdSigmaXY(kernel));
    	//AGcmdTable.put("SigmaYY", new CmdSigmaYY(kernel));
    	//AGcmdTable.put("Covariance", new CmdCovariance(kernel));
    	//AGcmdTable.put("SXY", new CmdSXY(kernel));
    	//AGcmdTable.put("SXX", new CmdSXX(kernel));
    	//AGcmdTable.put("SYY", new CmdSYY(kernel));
    	//AGcmdTable.put("MeanX", new CmdMeanX(kernel));
    	//AGcmdTable.put("MeanY", new CmdMeanY(kernel));
    	//AGcmdTable.put("PMCC", new CmdPMCC(kernel));
    	//AGcmdTable.put("FitLineY", new CmdFitLineY(kernel));
    	//AGcmdTable.put("FitLineX", new CmdFitLineX(kernel));
    	//AGcmdTable.put("FitPoly", new CmdFitPoly(kernel));
    	//AGcmdTable.put("FitExp", new CmdFitExp(kernel));
    	//AGcmdTable.put("FitLog", new CmdFitLog(kernel));
    	//AGcmdTable.put("FitPow", new CmdFitPow(kernel));
    	//AGcmdTable.put("Fit",new CmdFit(kernel));
    	//AGcmdTable.put("FitGrowth",new CmdFitGrowth(kernel));
    	//AGcmdTable.put("RandomNormal", new CmdRandomNormal(kernel));
    	//AGcmdTable.put("ConstructionStep", new CmdConstructionStep(kernel));
    	//AGcmdTable.put("Normal", new CmdNormal(kernel));
    	//AGcmdTable.put("Binomial", new CmdBinomial(kernel));
    	//AGcmdTable.put("InverseNormal", new CmdInverseNormal(kernel));
    	//AGcmdTable.put("Expand", new CmdExpand(kernel));
    	//AGcmdTable.put("Factor", new CmdFactor(kernel));
    	//AGcmdTable.put("Element", new CmdElement(kernel));
    	//AGcmdTable.put("Iteration", new CmdIteration(kernel));
    	//AGcmdTable.put("IterationList", new CmdIterationList(kernel));
    	
    	//AGcmdTable.put("Name", new CmdName(kernel));
    	
    	// cell range for spreadsheet like A1:A5
    	//AGcmdTable.put("CellRange", new CmdCellRange(kernel));  
    	
    	//AGcmdTable.put("Row", new CmdRow(kernel));    	
    	//AGcmdTable.put("Column", new CmdColumn(kernel));  
    	
    	//AGcmdTable.put("Text", new CmdText(kernel));    	
    	//AGcmdTable.put("LaTeX", new CmdLaTeX(kernel));
    	
    	//AGcmdTable.put("LetterToUnicode", new CmdLetterToUnicode(kernel));    	
    	//AGcmdTable.put("TextToUnicode", new CmdTextToUnicode(kernel));    	
    	//AGcmdTable.put("UnicodeToText", new CmdUnicodeToText(kernel));    
    	//AGcmdTable.put("UnicodeToLetter", new CmdUnicodeToLetter(kernel));    
    	
    	//AGcmdTable.put("BarChart", new CmdBarChart(kernel));    	
    	//AGcmdTable.put("BoxPlot", new CmdBoxPlot(kernel));    	
    	//AGcmdTable.put("Histogram", new CmdHistogram(kernel));   
    	//AGcmdTable.put("TrapezoidalSum", new CmdTrapezoidalSum(kernel));  
    	
    	//AGcmdTable.put("CountIf", new CmdCountIf(kernel));   
    	
    	//AGcmdTable.put("TableText", new CmdTableText(kernel)); 
    	
    	//AGcmdTable.put("Object", new CmdObject(kernel));   
    	//AGcmdTable.put("ColumnName", new CmdColumnName(kernel));   
    	
    	//AGcmdTable.put("Append", new CmdAppend(kernel));   
    	//AGcmdTable.put("Join", new CmdJoin(kernel));   
    	//AGcmdTable.put("Insert", new CmdInsert(kernel));   
    	//AGcmdTable.put("Union", new CmdUnion(kernel));   
    	//AGcmdTable.put("Intersection", new CmdIntersection(kernel)); 
    	
    	//AGcmdTable.put("IsInteger", new CmdIsInteger(kernel));
    	
    	//AGcmdTable.put("Random", new CmdRandom(kernel));   
    	//AGcmdTable.put("RandomBinomial", new CmdRandomBinomial(kernel));   
    	//AGcmdTable.put("RandomPoisson", new CmdRandomPoisson(kernel));   
    	
    	//AGcmdTable.put("FractionText", new CmdFractionText(kernel));   
    	
    	//AGcmdTable.put("KeepIf", new CmdKeepIf(kernel));  
    	
    	//AGcmdTable.put("AxisStepX", new CmdAxisStepX(kernel));   
    	//AGcmdTable.put("AxisStepY", new CmdAxisStepY(kernel));   
    	
    	//AGcmdTable.put("Invert", new CmdInvert(kernel));   
    	//AGcmdTable.put("Transpose", new CmdTranspose(kernel));   
    	//AGcmdTable.put("rref", new CmdReducedRowEchelonForm(kernel));   
    	//AGcmdTable.put("Determinant", new CmdDeterminant(kernel));   
    	
    	//AGcmdTable.put("Simplify", new CmdSimplify(kernel));   
    	
    	//AGcmdTable.put("FitSin", new CmdFitSin(kernel));   
    	//AGcmdTable.put("FitLogistic", new CmdFitLogistic(kernel));  
    	//AGcmdTable.put("SumSquaredErrors",new CmdSumSquaredErrors(kernel));
    	
    	//AGcmdTable.put("DynamicCoordinates", new CmdDynamicCoordinates(kernel));  

    	//AGcmdTable.put("TDistribution", new CmdTDistribution(kernel));  
    	//AGcmdTable.put("InverseTDistribution", new CmdInverseTDistribution(kernel));  
    	//AGcmdTable.put("FDistribution", new CmdFDistribution(kernel));  
    	//AGcmdTable.put("InverseFDistribution", new CmdInverseFDistribution(kernel));     	
    	//AGcmdTable.put("Gamma", new CmdGamma(kernel));  
    	//AGcmdTable.put("InverseGamma", new CmdInverseGamma(kernel));  
    	//AGcmdTable.put("Cauchy", new CmdCauchy(kernel));  
    	//AGcmdTable.put("InverseCauchy", new CmdInverseCauchy(kernel));  
    	//AGcmdTable.put("ChiSquared", new CmdChiSquared(kernel));  
    	//AGcmdTable.put("InverseChiSquared", new CmdInverseChiSquared(kernel));  
    	//AGcmdTable.put("Exponential", new CmdExponential(kernel));  
    	//AGcmdTable.put("InverseExponential", new CmdInverseExponential(kernel));  
    	//AGcmdTable.put("HyperGeometric", new CmdHyperGeometric(kernel));  
    	//AGcmdTable.put("InverseHyperGeometric", new CmdInverseHyperGeometric(kernel));  
    	//AGcmdTable.put("Pascal", new CmdPascal(kernel));  
    	//AGcmdTable.put("InversePascal", new CmdInversePascal(kernel));  
    	//AGcmdTable.put("Weibull", new CmdWeibull(kernel));  
    	//AGcmdTable.put("InverseWeibull", new CmdInverseWeibull(kernel));
    	//AGcmdTable.put("Zipf", new CmdZipf(kernel));  
    	//AGcmdTable.put("InverseZipf", new CmdInverseZipf(kernel));
    	
    	//AGcmdTable.put("CopyFreeObject", new CmdCopyFreeObject(kernel));
    	//AGcmdTable.put("SetColor", new CmdSetColor(kernel));
    	//AGcmdTable.put("SetDynamicColor", new CmdSetDynamicColor(kernel));
    	//AGcmdTable.put("SetConditionToShowObject", new CmdSetConditionToShowObject(kernel));
    	//AGcmdTable.put("SetFilling", new CmdSetFilling(kernel));
    	//AGcmdTable.put("SetLineThickness", new CmdSetLineThickness(kernel));
    	//AGcmdTable.put("SetLineStyle", new CmdLineStyle(kernel));
    	//AGcmdTable.put("SetPointStyle", new CmdSetPointStyle(kernel));
    	//AGcmdTable.put("SetPointSize", new CmdSetPointSize(kernel));
    	//AGcmdTable.put("SetFixed", new CmdSetFixed(kernel));
    	//AGcmdTable.put("Rename", new CmdRename(kernel));
    	//AGcmdTable.put("HideLayer", new CmdHideLayer(kernel));
    	//AGcmdTable.put("ShowLayer", new CmdShowLayer(kernel));
    	//AGcmdTable.put("SetCoords", new CmdSetCoords(kernel));
    	//AGcmdTable.put("Pan", new CmdPan(kernel));
    	//AG	cmdTable.put("ZoomIn", new CmdZoomIn(kernel));
    	//AGcmdTable.put("ZoomOut", new CmdZoomOut(kernel));
    	//AGcmdTable.put("SelectObjects", new CmdSelectObjects(kernel));
    	//AGcmdTable.put("SetLayer", new CmdSetLayer(kernel));
    	//AGcmdTable.put("SetCaption", new CmdSetCaption(kernel));
    	//AGcmdTable.put("SetLabelMode", new CmdSetLabelMode(kernel));
    	//AGcmdTable.put("SetTooltipMode", new CmdSetTooltipMode(kernel));
       	
    	//AGcmdTable.put("ParseToNumber", new CmdParseToNumber(kernel));
    	//AGcmdTable.put("ParseToFunction", new CmdParseToFunction(kernel));

    	//AGcmdTable.put("FillRow", new CmdFillRow(kernel));
    	//AGcmdTable.put("FillColumn", new CmdFillColumn(kernel));
    	//AGcmdTable.put("FillCells", new CmdFillCells(kernel));
      	
    	//AGcmdTable.put("Cell", new CmdCell(kernel));
    	//AGcmdTable.put("Factors", new CmdFactors(kernel));
    	//AGcmdTable.put("RandomUniform", new CmdRandomUniform(kernel));   
    	//AGcmdTable.put("Degree", new CmdDegree(kernel));   
    	//AGcmdTable.put("Coefficients", new CmdCoefficients(kernel));   
    	//AGcmdTable.put("Limit", new CmdLimit(kernel));   
    	//AGcmdTable.put("LimitBelow", new CmdLimitBelow(kernel));   
    	//AGcmdTable.put("LimitAbove", new CmdLimitAbove(kernel));   
    	
    	//AGcmdTable.put("PartialFractions", new CmdPartialFractions(kernel));   
    	//AGcmdTable.put("Numerator", new CmdNumerator(kernel));   
    	//AGcmdTable.put("Denominator", new CmdDenominator(kernel)); 
    	
    	//AGcmdTable.put("PointList", new CmdPointList(kernel));   
    	//AGcmdTable.put("RootList", new CmdRootList(kernel));   
 
     	                  	
    	
    	//Mathieu Blossier
    	//AGcmdTable.put("PointIn", new CmdPointIn(kernel));   
    	
    }


}
