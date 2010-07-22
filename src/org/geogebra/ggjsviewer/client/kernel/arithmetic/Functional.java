/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package org.geogebra.ggjsviewer.client.kernel.arithmetic;

import org.geogebra.ggjsviewer.client.kernel.GeoFunction;




public interface Functional {
	public double evaluate(double x);
	public Function getFunction();
	public GeoFunction getGeoDerivative(int order);
}
