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

import java.util.ArrayList;
import java.util.List;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.impl.source.tree.FileElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.impl.source.tree.PsiCommentImpl;
import com.intellij.psi.impl.source.tree.PsiErrorElementImpl;
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl;
import com.intellij.psi.impl.source.tree.TreeUtil;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.CharTable;

public class GroovyPsiBuilder implements PsiBuilder {

    private static final Logger LOGGER = Logger.getInstance("#org.codehaus.groovy.intellij.language.GroovyPsiBuilder");

    private final CharSequence originalText;
    private final List tokens = new ArrayList();
    private final ListWithRemovableRange markers = new ListWithRemovableRange();
    private final GroovyLexerAdapter lexer;
    private final boolean charTableNotAvailable;
    private final TokenSet whitespaceTokens;
    private final TokenSet commentTokens;
    private CharTable charTable;
    private int currentTokenIndex;

    public GroovyPsiBuilder(Language language, Project project, CharTable charTable, CharSequence originalText) {
        this.originalText = originalText;

        ParserDefinition parserDefinition = language.getParserDefinition();
        lexer = (GroovyLexerAdapter) parserDefinition.createLexer(project);
        whitespaceTokens = parserDefinition.getWhitespaceTokens();
        commentTokens = parserDefinition.getCommentTokens();

        this.charTable = charTable;
        charTableNotAvailable = charTable == null;

        lexer.bind(this);
    }

    // Lexer support API -----------------------------------------------------------------------------------------------

    public GroovyLexerAdapter getLexer() {
        return lexer;
    }

    public CharSequence getOriginalText() {
        return originalText;
    }

    public void advanceLexer() {
        lexer.advance();
    }

    void addToken(IElementType tokenType, int startOffset, int endOffset, String tokenAsText) {
        tokens.add(new Token(tokenType, startOffset, endOffset, tokenAsText));
        currentTokenIndex++;
    }

    public boolean eof() {
        return getLexer().getTokenType() == null;
    }

    public IElementType getTokenType() {
        Token currentToken = getCurrentToken();
        return currentToken == null ? null : currentToken.getTokenType();
    }

    public int getCurrentOffset() {
        Token currentToken = getCurrentToken();
        return currentToken == null ? getOriginalText().length() : currentToken.startOffset;
    }

    public String getTokenText() {
        return getCurrentToken().getTokenText();
    }

    public Token getCurrentToken() {
        return (Token) (tokens.size() == 0 || eof() ? null : tokens.get(tokens.size() - 1));
    }

    // Marker support API ----------------------------------------------------------------------------------------------

    public Marker mark() {
        StartMarker startMarker = new StartMarker(currentTokenIndex);
        markers.add(startMarker);
        return startMarker;
    }

    private Marker preceed(StartMarker startMarker) {
        int markerIndex = markers.lastIndexOf(startMarker);
        LOGGER.assertTrue(markerIndex >= 0, "Cannot preceed dropped or rolled-back marker");
        StartMarker startMarkerCopy = new StartMarker(startMarker.lexemIndex);
        markers.add(markerIndex, startMarkerCopy);
        return startMarkerCopy;
    }

    public void rollbackTo(Marker marker) {
        currentTokenIndex = ((StartMarker) marker).lexemIndex;
        int markerIndex = markers.lastIndexOf(marker);
        LOGGER.assertTrue(markerIndex >= 0, "The marker must be added before rolled back to.");
        markers.removeRange(markerIndex, markers.size());
    }

    public void drop(Marker marker) {
        boolean markerRemoved = markers.remove(markers.lastIndexOf(marker)) == marker;
        LOGGER.assertTrue(markerRemoved, "The marker must be added before it is dropped.");
    }

    public void done(Marker marker) {
        LOGGER.assertTrue(((StartMarker) marker).doneMarker == null, "Marker already done.");

        int markerIndex = markers.lastIndexOf(marker);
        LOGGER.assertTrue(markerIndex >= 0, "Marker has never been added.");

        for (int i = markers.size() - 1; i > markerIndex; i--) {
            ProductionMarker productionMarker = (ProductionMarker) markers.get(i);
            if (!(productionMarker instanceof Marker)) {
                continue;
            }
            StartMarker startMarker = (StartMarker) productionMarker;
            if (startMarker.doneMarker == null) {
                LOGGER.error("Another not done marker of type [" + startMarker.elementType + "] added after this one. Must be done before this.");
            }
        }

        DoneMarker doneMarker = new DoneMarker((StartMarker) marker, currentTokenIndex);
        ((StartMarker) marker).doneMarker = doneMarker;
        markers.add(doneMarker);
    }

    public void error(String message) {
        if ((ProductionMarker) markers.get(markers.size() - 1) instanceof ErrorItem) {
            return;
        }
        markers.add(new ErrorItem(message, currentTokenIndex));
    }

    public ASTNode getTreeBuilt() {
        StartMarker startMarker = (StartMarker) markers.get(0);

        ASTNode node;
        if (charTableNotAvailable) {
            node = new FileElement(startMarker.elementType);
            charTable = ((FileElement) node).getCharTable();
        } else {
            node = new CompositeElement(startMarker.elementType);
            node.putUserData(CharTable.CHAR_TABLE_KEY, charTable);
        }

        for (int k = 1; k < markers.size() - 1; k++) {
            ProductionMarker productionMarker = (ProductionMarker) markers.get(k);
            if (productionMarker instanceof StartMarker) {
                for ( ;
                     productionMarker.lexemIndex < tokens.size()
                            && whitespaceTokens.isInSet(((Token) tokens.get(productionMarker.lexemIndex)).getTokenType());
                     productionMarker.lexemIndex++);
                continue;
            }

            if (!(productionMarker instanceof DoneMarker) && !(productionMarker instanceof ErrorItem)) {
                continue;
            }

            for (int i1 = ((ProductionMarker) markers.get(k - 1)).lexemIndex;
                 productionMarker.lexemIndex > i1
                        && productionMarker.lexemIndex < tokens.size()
                        && whitespaceTokens.isInSet(((Token) tokens.get(productionMarker.lexemIndex - 1)).getTokenType());
                 productionMarker.lexemIndex--);
        }

        ASTNode nodeCopy = node;
        int numberOfProcessedTokens = 0;
        int previousNumberOfProcessedTokens = -1;
        for (int markerIndex = 1; markerIndex < markers.size(); markerIndex++) {
            ProductionMarker productionMarker = (ProductionMarker) markers.get(markerIndex);
            LOGGER.assertTrue(nodeCopy != null, "Unexpected end of the production");
            int currentLexingIndex = productionMarker.lexemIndex;
            if (productionMarker instanceof StartMarker) {
                StartMarker startMarker2 = (StartMarker) productionMarker;
                numberOfProcessedTokens = attachChildNodes(numberOfProcessedTokens, currentLexingIndex, nodeCopy);
                CompositeElement compositeElement = new CompositeElement(startMarker2.elementType);
                TreeUtil.addChildren((CompositeElement) nodeCopy, compositeElement);
                nodeCopy = compositeElement;
                continue;
            }
            if (productionMarker instanceof DoneMarker) {
                DoneMarker doneMarker = (DoneMarker) productionMarker;
                numberOfProcessedTokens = attachChildNodes(numberOfProcessedTokens, currentLexingIndex, nodeCopy);
                LOGGER.assertTrue(doneMarker.startMarker.elementType == nodeCopy.getElementType());
                nodeCopy = nodeCopy.getTreeParent();
                continue;
            }
            if (!(productionMarker instanceof ErrorItem)) {
                continue;
            }
            numberOfProcessedTokens = attachChildNodes(numberOfProcessedTokens, currentLexingIndex, nodeCopy);
            if (numberOfProcessedTokens != previousNumberOfProcessedTokens) {
                previousNumberOfProcessedTokens = numberOfProcessedTokens;
                PsiErrorElementImpl psiErrorElement = new PsiErrorElementImpl();
                psiErrorElement.setErrorDescription(((ErrorItem) productionMarker).message);
                TreeUtil.addChildren((CompositeElement) nodeCopy, psiErrorElement);
            }
        }

        LOGGER.assertTrue(numberOfProcessedTokens == tokens.size(), "Not all of the tokens inserted to the tree");
        LOGGER.assertTrue(nodeCopy == null, "Unbalanced tree");
        return node;
    }

    private int attachChildNodes(int numberOfProcessedTokens, int lexingIndex, ASTNode astNode) {
        for (lexingIndex = Math.min(lexingIndex, tokens.size()); numberOfProcessedTokens < lexingIndex;) {
            Token token = (Token) tokens.get(numberOfProcessedTokens++);
            TreeUtil.addChildren((CompositeElement) astNode, buildLeafPsiElement(token));
        }

        return numberOfProcessedTokens;
    }

    private LeafPsiElement buildLeafPsiElement(Token token) {
        IElementType elementType = token.getTokenType();
        if (whitespaceTokens.isInSet(elementType)) {
            return new PsiWhiteSpaceImpl(lexer.getBuffer(), token.startOffset, token.endOffset, token.state, charTable);
        }
        if (commentTokens.isInSet(elementType)) {
            return new PsiCommentImpl(elementType, lexer.getBuffer(), token.startOffset, token.endOffset, token.state, charTable);
        }
        return new LeafPsiElement(elementType, lexer.getBuffer(), token.startOffset, token.endOffset, token.state, charTable);
    }

    private static class ListWithRemovableRange extends ArrayList {

        public void removeRange(int fromIndex, int toIndex) {
            super.removeRange(fromIndex, toIndex);
        }
    }

    static class Token {

        private final IElementType tokenType;
        private final int startOffset;
        private final int endOffset;
        private final String text;
        private final int state;

        private Token(IElementType tokenType, int startOffset, int endOffset, String text) {
            this.tokenType = tokenType;
            this.startOffset = startOffset;
            this.endOffset = endOffset;
            this.text = text;
            this.state = 0;
        }

        public IElementType getTokenType() {
            return tokenType;
        }

        public String getTokenText() {
            return text;
        }
    }

    private static class ProductionMarker {

        int lexemIndex;

        public ProductionMarker(int lexemIndex) {
            this.lexemIndex = lexemIndex;
        }
    }

    private class StartMarker extends ProductionMarker implements Marker {

        private IElementType elementType;
        private DoneMarker doneMarker;

        private StartMarker(int lexemIndex) {
            super(lexemIndex);
            doneMarker = null;
        }

        public Marker preceed() {
            return GroovyPsiBuilder.this.preceed(this);
        }

        public void drop() {
            GroovyPsiBuilder.this.drop(this);
        }

        public void rollbackTo() {
            GroovyPsiBuilder.this.rollbackTo(this);
        }

        public void done(IElementType elementType) {
            this.elementType = elementType;
            GroovyPsiBuilder.this.done(this);
        }
    }

    private static class ErrorItem extends ProductionMarker {

        private final String message;

        private ErrorItem(String message, int lexemIndex) {
            super(lexemIndex);
            this.message = message;
        }
    }

    private static class DoneMarker extends ProductionMarker {

        private final StartMarker startMarker;

        private DoneMarker(StartMarker startMarker, int lexemIndex) {
            super(lexemIndex);
            this.startMarker = startMarker;
        }
    }
}
