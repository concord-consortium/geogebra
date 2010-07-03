package org.geogebra.ggjsviewer.client.dummyclasses;

import java.util.HashSet;

import org.geogebra.ggjsviewer.client.kernel.Kernel;
import org.geogebra.ggjsviewer.client.kernel.arithmetic.ExpressionNode;
import org.geogebra.ggjsviewer.client.kernel.arithmetic.ExpressionValue;
import org.geogebra.ggjsviewer.client.kernel.arithmetic.FunctionVariable;
import org.geogebra.ggjsviewer.client.kernel.arithmetic.ValidExpression;

public class Function extends ValidExpression implements ExpressionValue {

	public Function(ExpressionNode en) {
		// TODO Auto-generated constructor stub
	}

	public Function(ExpressionNode rhs, FunctionVariable fVar) {
		// TODO Auto-generated constructor stub
	}

	public void setLabel(String label) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean contains(ExpressionValue ev) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ExpressionValue deepCopy(Kernel kernel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExpressionValue evaluate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashSet getVariables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isBooleanValue() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConstant() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isExpressionNode() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isLeaf() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isListValue() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNumberValue() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPolynomialInstance() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isTextValue() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isVector3DValue() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isVectorValue() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void resolveVariables() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toLaTeXString(boolean symbolic) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toValueString() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
