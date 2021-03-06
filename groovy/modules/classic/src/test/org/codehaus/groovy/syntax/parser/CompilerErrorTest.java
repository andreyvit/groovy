/*
 * $Id$
 * 
 * Copyright 2003 (C) James Strachan and Bob Mcwhirter. All Rights Reserved.
 * 
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *  1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *  2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  3. The name "groovy" must not be used to endorse or promote products
 * derived from this Software without prior written permission of The Codehaus.
 * For written permission, please contact info@codehaus.org.
 *  4. Products derived from this Software may not be called "groovy" nor may
 * "groovy" appear in their names without prior written permission of The
 * Codehaus. "groovy" is a registered trademark of The Codehaus.
 *  5. Due credit should be given to The Codehaus - http://groovy.codehaus.org/
 * 
 * THIS SOFTWARE IS PROVIDED BY THE CODEHAUS AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE CODEHAUS OR ITS CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 *  
 */

package org.codehaus.groovy.syntax.parser;

import groovy.lang.GroovyObject;
import groovy.lang.MissingClassException;
import groovy.lang.MissingPropertyException;

import java.io.ByteArrayInputStream;

import org.codehaus.groovy.classgen.TestSupport;
import org.codehaus.groovy.control.CompilationFailedException;

/**
 * @author <a href="mailto:james@coredevelopers.net">James Strachan</a>
 * @version $Revision$
 */
public class CompilerErrorTest extends TestSupport {

    public void testUnknownClassCatch() throws Exception {
        MissingClassException e =
            assertCompileFailed_WithMCE(
                "class UnknownClass {\n"
                    + "    void main(args) {\n"
                    + "        try {\n"
                    + "            println('Hello World!')\n"
                    + "        }\n"
                    + "        catch (UnknownException e) {\n"
                    + "            println('This will never happen')\n"
                    + "        }\n"
                    + "    }\n"
                    + "}\n");

        assertEquals("UnknownException", e.getType());
    }

    public void testUnknownClassInNew() throws Exception {
        MissingClassException e =
            assertCompileFailed_WithMCE(
                "class UnknownClass {\n" + "    void main(args) {\n" + "        def x = new UnknownThingy()\n" + "    }\n" + "}\n");
        assertEquals("UnknownThingy", e.getType());
    }

    public void testUnknownClassInAssignment() throws Exception {
        GroovyObject object =
            assertCompileWorks(
                "class UnknownClass {\n" + "    void main(args) {\n" + "        def x = UnknownThingy\n" + "    }\n" + "}\n");

        try {
            object.invokeMethod("main", new String[] {});
            fail("Should have thrown exception due to unknown property");
        }
        catch (MissingPropertyException e) {
            assertEquals("UnknownThingy", e.getProperty());
        }
    }


    /** TODO non-terminated strings or GStrings lead to an undless loop and to an OutOfMemoryError */
    public void testUnterminatedConstantGString() throws Exception {
        //assertCompileFailed( "println \"d" );
    }

    /** TODO non-terminated strings or GStrings lead to an undless loop and to an OutOfMemoryError */
    public void testUnterminatedGString() throws Exception {
        //assertCompileFailed( "println \"${1+2\"\nprintln \"c\"" );
    }





    protected GroovyObject assertCompileWorks(String code) throws Exception {
        Class type =
            loader.parseClass(new ByteArrayInputStream(code.getBytes()), "ValidClass_" + getMethodName() + ".groovy");
        return (GroovyObject) type.newInstance();
    }

    protected MissingClassException assertCompileFailed_WithMCE(String code) throws Exception {
        try {
            assertCompileWorks(code);

            fail("Should have thrown an exception");
        }
        catch( CompilationFailedException e ) {
            Exception cause = e.getUnit().getException(0);
            if( cause instanceof MissingClassException ) {
                System.out.println("Worked, threw: " + cause);
                //e.printStackTrace();
                return (MissingClassException)cause;
            }
            throw e;
        }
        return null;
    }

    protected CompilationFailedException assertCompileFailed(String code) throws Exception {
        try {
            assertCompileWorks(code);
 
            fail("Should have thrown an exception");
        }
        catch( CompilationFailedException e ) {
            return e;
        }

        return null;
    }

}
