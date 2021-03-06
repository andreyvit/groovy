/**
 *
 * Copyright 2004 James Strachan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **/
package org.codehaus.groovy.antlr;

import antlr.collections.AST;
import org.codehaus.groovy.syntax.ParserException;

/**
 * Thrown when trying to parse the AST
 *
 * @version $Revision$
 */
public class ASTParserException extends ParserException {
    private AST ast;

    public ASTParserException(ASTRuntimeException e) {
        super(e.getMessage(), e, e.getLine(), e.getColumn());
        this.ast = e.getAst();
    }

    public ASTParserException(String message, ASTRuntimeException e) {
        super(message, e, e.getLine(), e.getColumn());
        this.ast = e.getAst();
    }

    public AST getAst() {
        return ast;
    }
}
