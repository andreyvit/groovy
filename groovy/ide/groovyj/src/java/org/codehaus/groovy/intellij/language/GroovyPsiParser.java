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

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;

import org.jetbrains.annotations.NotNull;

import org.codehaus.groovy.intellij.language.parser.GroovyRecognizer;

import antlr.RecognitionException;
import antlr.TokenStreamException;

public class GroovyPsiParser implements PsiParser {

    @NotNull
    public ASTNode parse(IElementType rootElementType, PsiBuilder builder) {
        PsiBuilder.Marker rootMarker = builder.mark();
        try {
            GroovyRecognizer.make((GroovyPsiBuilder) builder).compilationUnit();
        } catch (TokenStreamException e) {
            builder.error(e.toString());
        } catch (RecognitionException e) {
            builder.error(e.toString());
        }
        rootMarker.done(rootElementType);
        return builder.getTreeBuilt();
    }
}
