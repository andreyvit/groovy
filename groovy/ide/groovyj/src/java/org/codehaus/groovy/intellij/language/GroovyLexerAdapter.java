/*
 * $Id$
 *
 * Copyright (c) 2005 The Codehaus - http://groovy.codehaus.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 *
 */


package org.codehaus.groovy.intellij.language;

import java.io.StringReader;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.text.CharArrayUtil;

import org.codehaus.groovy.antlr.UnicodeEscapingReader;
import org.codehaus.groovy.antlr.parser.GroovyTokenTypes;

import org.codehaus.groovy.intellij.language.parser.GroovyLexer;
import org.codehaus.groovy.intellij.psi.GroovyTokenTypeMappings;

import antlr.LexerSharedInputState;
import antlr.Token;
import antlr.TokenStreamException;

public class GroovyLexerAdapter extends LexerBase {

    private GroovyPsiBuilder builder;
    private GroovyLexer adaptee;
    private char[] buffer;
    private int currentTokenStartOffset;
    private int bufferEndOffset;

    private int currentLineStartPosition;
    private Token currentToken;

    void bind(GroovyPsiBuilder builder) {
        this.builder = builder;
        adaptee = new GroovyLexer(builder);
        adaptee.setCaseSensitive(true);
        adaptee.setWhitespaceIncluded(true);
        start(CharArrayUtil.fromSequence(builder.getOriginalText()));
    }

    public void start(char[] buffer) {
        start(buffer, 0, buffer.length);
    }

    public void start(char[] buffer, int startOffset, int endOffset) {
        start(buffer, startOffset, endOffset, 0);
    }

    public void start(char[] buffer, int startOffset, int endOffset, int initialState) {
        this.buffer = buffer;
        currentTokenStartOffset = startOffset;  // index of the first character of the current token relative to the full buffer
        bufferEndOffset = endOffset;            // index of the last character in the full buffer
        currentLineStartPosition = startOffset; // index of the first character on the current line

        String input = new String(buffer, startOffset, endOffset - startOffset);
        UnicodeEscapingReader reader = new UnicodeEscapingReader(new StringReader(input));
        reader.setLexer(adaptee);
        adaptee.setInputState(new LexerSharedInputState(reader));
    }

    public GroovyLexer getAdaptedLexer() {
        return adaptee;
    }

    public char[] getBuffer() {
        return buffer;
    }

    public int getBufferEnd() {
        return bufferEndOffset;
    }

    // Never used - possibly a leftover from JFlex...
    public int getState() {
        return 0;
    }

    public IElementType getTokenType() {
        Token token = currentToken;
        if (token == null || token.getType() == GroovyTokenTypes.EOF) {
            return null;
        }

        return GroovyTokenTypeMappings.getType(token.getType());
    }

    public int getTokenStart() {
        return currentTokenStartOffset;
    }

    public int getTokenEnd() {
        return currentTokenStartOffset + currentToken.getText().length();
    }

    public Token getCurrentToken() {
        return currentToken;
    }

    public void advance() {
        try {
            lexerAdvanced(adaptee.nextToken());
        } catch (TokenStreamException e) {
            throw new RuntimeException(e);
        }
    }

    public void lexerAdvanced(Token nextToken) {
        if (currentToken != null) {
            if (currentToken.getType() == GroovyTokenTypes.NLS) {
                advanceNewLine();
            } else if (currentToken.getType() == GroovyTokenTypes.ML_COMMENT) {
                advanceMultiLineComment();
            }
        }

        currentToken = nextToken;
        // columns start at 1, currentTokenStartOffset starts at 0
        currentTokenStartOffset = currentLineStartPosition + currentToken.getColumn() - 1;

        if (nextToken.getType() != GroovyTokenTypes.EOF) {
            String tokenAsText = new String(getBuffer(), getTokenStart(), getTokenEnd() - getTokenStart());
            builder.addToken(getTokenType(), getTokenStart(), getTokenEnd(), tokenAsText);
        }
    }

    private void advanceNewLine() {
        int lastLineSeparatorIndex = findLastIndexOfALineSeparator(currentToken.getText());
        currentLineStartPosition = currentTokenStartOffset + lastLineSeparatorIndex + 1;
    }

    private void advanceMultiLineComment() {
        // ML_COMMENTS may contain any number of NLS and may end with one
        int lastLineSeparatorIndex = findLastIndexOfALineSeparator(currentToken.getText());
        if (lastLineSeparatorIndex > -1) {
            currentLineStartPosition = currentTokenStartOffset + lastLineSeparatorIndex + 1;
        }
    }

    private int findLastIndexOfALineSeparator(String text) {
        int lastIndexOfALineSeparator = text.lastIndexOf("\r\n");    // Windows
        if (lastIndexOfALineSeparator == -1) {
            lastIndexOfALineSeparator = text.lastIndexOf("\r");      // Macintosh
        }
        if (lastIndexOfALineSeparator == -1) {
            lastIndexOfALineSeparator = text.lastIndexOf("\n");      // Unix
        }
        return lastIndexOfALineSeparator;
    }
}
