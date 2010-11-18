/* {{{ License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */ //}}}

// :indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:folding=explicit:collapseFolds=0:

package org.mathpiper.builtin.functions;

import org.mathpiper.builtin.BuiltinFunctionInitialize;
import org.mathpiper.lisp.Atom;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.ConsPointer;

/**
 *
 *  
 */
public class Time extends BuiltinFunctionInitialize
{

    public void eval(Environment aEnvironment, int aStackTop) throws Exception
    {
        long starttime = System.currentTimeMillis();
        ConsPointer res = new ConsPointer();
        aEnvironment.iEvaluator.evaluate(aEnvironment, res, argumentPointer(aEnvironment, aStackTop, 1));
        long endtime = System.currentTimeMillis();
        double timeDiff;
        timeDiff = endtime - starttime;
        timeDiff /= 1000.0;
        result(aEnvironment, aStackTop).setCons(Atom.getInstance(aEnvironment, "" + timeDiff));
    }
}