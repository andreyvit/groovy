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
package org.codehaus.groovy.ast.stmt;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.expr.Expression;

/**
 * Represents a switch (object) { case value: ... case [1, 2, 3]: ...  default: ... } statement in Groovy.
 * 
 * @author <a href="mailto:james@coredevelopers.net">James Strachan</a>
 * @version $Revision$
 */
public class SwitchStatement extends Statement {

    private Expression expression;
    private List caseStatements = new ArrayList();
    private Statement defaultStatement;
    

    public SwitchStatement(Expression expression) {
        this(expression, EmptyStatement.INSTANCE);
    }

    public SwitchStatement(Expression expression, Statement defaultStatement) {
        this.expression = expression;
        this.defaultStatement = defaultStatement;
    }

    public SwitchStatement(Expression expression, List caseStatements, Statement defaultStatement) {
        this.expression = expression;
        this.caseStatements = caseStatements;
        this.defaultStatement = defaultStatement;
    }

    public void visit(GroovyCodeVisitor visitor) {
        visitor.visitSwitch(this);
    }
    
    public List getCaseStatements() {
        return caseStatements;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression e) {
        expression=e;
    }
    
    public Statement getDefaultStatement() {
        return defaultStatement;
    }

    public void setDefaultStatement(Statement defaultStatement) {
        this.defaultStatement = defaultStatement;
    }

    public void addCase(CaseStatement caseStatement) {
        caseStatements.add(caseStatement);
    }

    /**
     * @return the case statement of the given index or null
     */
    public CaseStatement getCaseStatement(int idx) {
        if (idx >= 0 && idx < caseStatements.size()) {
            return (CaseStatement) caseStatements.get(idx);
        }
        return null;
    }
}
