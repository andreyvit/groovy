/**
 *
 * Copyright 2005 Jeremy Rayner
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

/**
 * An interface for processing antlr AST objects
 *
 * @author <a href="mailto:groovy@ross-rayner.com">Jeremy Rayner</a>
 * @version $Revision$
 */
public interface AntlrASTProcessor {
    /**
     * performs some processing on the supplied AST node.
     * @param t the AST node to process.
     * @return possibly returns the AST modified or null, depends on the implementation.
     */
    AST process(AST t);
}