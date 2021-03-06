/*
 * $Id$
 *
 * Copyright (c) 2005-2006 The Codehaus - http://groovy.codehaus.org
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

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.LanguageUtil;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.PsiPlainTextFileImpl;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.codehaus.groovy.intellij.psi.GroovyElementTypes;
import org.codehaus.groovy.intellij.psi.GroovyTokenSets;
import org.jetbrains.annotations.NotNull;

public class GroovyParserDefinition implements ParserDefinition {

    private final GroovyLanguageToolsFactory languageToolsFactory;

    public GroovyParserDefinition(GroovyLanguageToolsFactory languageToolsFactory) {
        this.languageToolsFactory = languageToolsFactory;
    }

    @NotNull
    public Lexer createLexer(Project project) {
        return languageToolsFactory.createLexer();
    }

    @NotNull
    public PsiParser createParser(Project project) {
        return languageToolsFactory.createParser();
    }

    public IFileElementType getFileNodeType() {
        return GroovyElementTypes.FILE;
    }

    @NotNull
    public TokenSet getWhitespaceTokens() {
        return GroovyTokenSets.WHITESPACES;
    }

    @NotNull
    public TokenSet getCommentTokens() {
        return GroovyTokenSets.COMMENTS;
    }

    @NotNull
    public PsiElement createElement(ASTNode node) {
        return new ASTWrapperPsiElement(node);
    }

    public PsiFile createFile(FileViewProvider fileViewProvider) {
        // TODO: restore once GroovyFile is functionally usable
//        return new GroovyFile(fileViewProvider, GroovyLanguage.findOrCreate());
        return new PsiPlainTextFileImpl(fileViewProvider);
    }

    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return LanguageUtil.canStickTokensTogetherByLexer(left, right, createLexer(left.getPsi().getProject()), 0);
    }
}
