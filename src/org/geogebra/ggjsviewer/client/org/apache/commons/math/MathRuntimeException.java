/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geogebra.ggjsviewer.client.org.apache.commons.math;

import java.util.MissingResourceException;

/**
* Base class for commons-math unchecked exceptions.
*
* @version $Revision: 822850 $ $Date: 2009-10-07 14:56:42 -0400 (Wed, 07 Oct 2009) $
* @since 2.0
*/
public class MathRuntimeException extends RuntimeException {

    /** Serializable version identifier. */
    private static final long serialVersionUID = -5128983364075381060L;

    /**
     * Pattern used to build the message.
     */
    private final String pattern;

    /**
     * Arguments used to build the message.
     */
    private final Object[] arguments;

    /**
     * Constructs a new <code>MathRuntimeException</code> with specified
     * formatted detail message.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param pattern format specifier
     * @param arguments format arguments
     */
    public MathRuntimeException(final String pattern, final Object ... arguments) {
        this.pattern   = pattern;
        this.arguments = (arguments == null) ? new Object[0] : arguments;
    }

    /**
     * Constructs a new <code>MathRuntimeException</code> with specified
     * nested <code>Throwable</code> root cause.
     *
     * @param rootCause  the exception or error that caused this exception
     *                   to be thrown.
     */
    public MathRuntimeException(final Throwable rootCause) {
        super(rootCause);
        this.pattern   = getMessage();
        this.arguments = new Object[0];
    }

    /**
     * Constructs a new <code>MathRuntimeException</code> with specified
     * formatted detail message and nested <code>Throwable</code> root cause.
     * Message formatting is delegated to {@link java.text.MessageFormat}.
     * @param rootCause the exception or error that caused this exception
     * to be thrown.
     * @param pattern format specifier
     * @param arguments format arguments
     */
    public MathRuntimeException(final Throwable rootCause,
                                final String pattern, final Object ... arguments) {
        super(rootCause);
        this.pattern   = pattern;
        this.arguments = (arguments == null) ? new Object[0] : arguments;
    }

	public static IllegalArgumentException createIllegalArgumentException(String pattern, Object ... arguments) {
		// TODO Auto-generated method stub
		return new IllegalArgumentException();
	}

	public static Exception createIllegalStateException(String string) {
		// TODO Auto-generated method stub
		return new Exception(string);
	}

  
}
