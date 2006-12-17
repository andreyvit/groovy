/*
 $Id$

 Copyright 2003 (C) James Strachan and Bob Mcwhirter. All Rights Reserved.

 Redistribution and use of this software and associated documentation
 ("Software"), with or without modification, are permitted provided
 that the following conditions are met:

 1. Redistributions of source code must retain copyright
    statements and notices.  Redistributions must also contain a
    copy of this document.

 2. Redistributions in binary form must reproduce the
    above copyright notice, this list of conditions and the
    following disclaimer in the documentation and/or other
    materials provided with the distribution.

 3. The name "groovy" must not be used to endorse or promote
    products derived from this Software without prior written
    permission of The Codehaus.  For written permission,
    please contact info@codehaus.org.

 4. Products derived from this Software may not be called "groovy"
    nor may "groovy" appear in their names without prior written
    permission of The Codehaus. "groovy" is a registered
    trademark of The Codehaus.

 5. Due credit should be given to The Codehaus -
    http://groovy.codehaus.org/

 THIS SOFTWARE IS PROVIDED BY THE CODEHAUS AND CONTRIBUTORS
 ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 THE CODEHAUS OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 OF THE POSSIBILITY OF SUCH DAMAGE.

 */

package groovy.lang;

import java.util.List;

import org.codehaus.groovy.runtime.InvokerHelper;

import groovy.util.GroovyTestCase;

/**
 * Tests the use of the structured Attribute type
 * 
 * @author <a href="mailto:james@coredevelopers.net">James Strachan</a>
 * @version $Revision$
 */
public class SequenceTest extends GroovyTestCase {

    public void testConstruction() {
        Sequence sequence = new Sequence(String.class);
        sequence.add("James");
        sequence.add("Bob");

        assertEquals("Size", 2, sequence.size());
        assertEquals("Element", "James", sequence.get(0));
        assertEquals("Element", "Bob", sequence.get(1));

        // now lets try some methods on each item in the list
        List answer = (List) InvokerHelper.invokeMethod(sequence, "startsWith", new Object[] { "Ja" });
        assertArrayEquals(new Object[] { Boolean.TRUE, Boolean.FALSE }, answer.toArray());

        answer = (List) InvokerHelper.invokeMethod(sequence, "length", null);
        assertArrayEquals(new Object[] { new Integer(5), new Integer(3)}, answer.toArray());
    }

    public void testAddingWrongTypeFails() {
        try {
            Sequence sequence = new Sequence(String.class);
            sequence.add(new Integer(5));

            fail("Should have thrown exception");
        }
        catch (IllegalArgumentException e) {
            System.out.println("Caught: " + e);
        }
    }

    public void testAddingNullFails() {
        try {
            Sequence sequence = new Sequence(String.class);
            sequence.add(null);

            fail("Should have thrown exception");
        }
        catch (NullPointerException e) {
            System.out.println("Caught: " + e);
        }
    }

}
