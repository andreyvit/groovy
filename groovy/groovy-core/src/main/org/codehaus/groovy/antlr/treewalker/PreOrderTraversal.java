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
package org.codehaus.groovy.antlr.treewalker;

import antlr.collections.AST;
import org.codehaus.groovy.antlr.GroovySourceAST;

/**
 * A simple preorder traversal over the supplied antlr AST.
 *
 * @author <a href="mailto:groovy@ross-rayner.com">Jeremy Rayner</a>
 * @version $Revision$
 */

public class PreOrderTraversal extends TraversalHelper {
    /**
     * A simple preorder traversal over the supplied antlr AST.
     * @param visitor the Visitor to call for each node visited 
     */
    public PreOrderTraversal(Visitor visitor) {
        super(visitor);
    }

    public AST process(AST t) {
        // process each node in turn
        setUp();
        accept((GroovySourceAST)t);
        acceptSiblings((GroovySourceAST)t);
        tearDown();
        return null;
    }

    public void accept(GroovySourceAST currentNode,boolean ignoreSiblings) {
        openingVisit(currentNode);
        acceptChildren(currentNode);
        closingVisit(currentNode);
    }

    private void acceptSiblings(GroovySourceAST t) {
        GroovySourceAST sibling = (GroovySourceAST)t.getNextSibling();
        while (sibling != null) {
            accept(sibling);
            sibling = (GroovySourceAST)sibling.getNextSibling();
        }
    }
}