package org.geogebra.ggjsviewer.client.dummyclasses;

import java.util.HashSet;

import org.geogebra.ggjsviewer.client.kernel.GeoVec2D;
import org.geogebra.ggjsviewer.client.kernel.Kernel;
import org.geogebra.ggjsviewer.client.kernel.arithmetic.ExpressionValue;
import org.geogebra.ggjsviewer.client.kernel.arithmetic.VectorValue;

public class MyVecNode implements VectorValue {

	public MyVecNode(Kernel kernel) {
		// TODO Auto-generated constructor stub
	}

	public MyVecNode(Kernel kernel, ExpressionValue x, ExpressionValue y) {
		// TODO Auto-generated constructor stub
	}

	public void setPolarCoords(ExpressionValue r, ExpressionValue phi) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getMode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public GeoVec2D getVector() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMode(int mode) {
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
	public boolean isGeoElement() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInTree() {
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
	public boolean isVariable() {
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
	public void setInTree(boolean flag) {
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