/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.geogebramobile.client.kernel.statistics;

import geogebra.geogebramobile.client.kernel.Construction;
import geogebra.geogebramobile.client.kernel.GeoList;

/**
 * Sum of Squares of a list
 * @author Michael Borcherds
 * @version 2008-02-18
 */

public class AlgoSigmaXX extends AlgoStats1D {

	private static final long serialVersionUID = 1L;

	public AlgoSigmaXX(Construction cons, String label, GeoList geoList) {
        super(cons,label,geoList,AlgoStats1D.STATS_SIGMAXX);
    }

    public String getClassName() {
        return "AlgoSigmaXX";
    }
}
