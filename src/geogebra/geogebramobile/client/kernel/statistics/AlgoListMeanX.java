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
 * Mean of x-coords of a list of Points
 * @author Michael Borcherds
 * @version 2008-02-18
 */

public class AlgoListMeanX extends AlgoStats2D {

	private static final long serialVersionUID = 1L;

	public AlgoListMeanX(Construction cons, String label, GeoList geoListx) {
        super(cons,label,geoListx,AlgoStats2D.STATS_MEANX);
    }

    public String getClassName() {
        return "AlgoListMeanX";
    }
}