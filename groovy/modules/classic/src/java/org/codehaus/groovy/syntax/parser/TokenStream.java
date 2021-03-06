package org.codehaus.groovy.syntax.parser;

import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.syntax.ReadException;
import org.codehaus.groovy.syntax.SyntaxException;

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


/**
 *  Provides a stream of lexer tokens for use by (primarily) the parsing
 *  systems.
 */

public interface TokenStream
{
    // ----------------------------------------------------------------------
    //     Interface
    // ----------------------------------------------------------------------

    /** 
     *  Looks-ahead to the next token.
     *
     *  <p>
     *  This method is equivalent to <code>la(1)</code>.
     *  </p>
     *
     *  @see #la(int)
     *
     *  @return The next token or null if no more tokens
     *          available.
     *
     *  @throws org.codehaus.groovy.syntax.ReadException If an error occurs attempting to lookahead
     *          a token.
     */

    Token la() throws ReadException, SyntaxException;


    /** 
     *  Looks-ahead to the <code>k</code><i>th</i> token.
     *
     *  @param k Number of token to look ahead.
     *
     *  @return the <code>k</code><i>th</i> token or null if no
     *          more tokens available.
     *
     *  @throws ReadException If an error occurs attempting to lookahead
     *          a token.
     */

    Token la(int k) throws ReadException, SyntaxException;


    /** 
     *  Consumes the next token.
     *
     *  @param type The token type.
     *
     *  @return The consumed token or null if no more tokens
     *          available.
     *
     *  @throws ReadException If an error occurs attempting to consume
     *          a token.
     */

    Token consume(int type) throws ReadException, SyntaxException;


    /**
     *  Returns a description of the source location (typically a file path).
     */

    String getSourceLocator();


    /**
     * Checkpoints a point in the stream that we can go back to
     */

    void checkpoint();


    /**
     * Restores to the previous checkpoint
     */

    void restore();


    /**
     * Returns true if the stream is out of tokens, possibly
     * ignoring trailing whitespace.
     */

    boolean atEnd( boolean ignoringWhitespace );



    /**
     * Returns true if the stream is out of tokens.  
     */

    boolean atEnd( );

}
