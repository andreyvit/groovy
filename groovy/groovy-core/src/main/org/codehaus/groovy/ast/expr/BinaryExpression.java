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
package org.codehaus.groovy.ast.expr;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.Variable;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.syntax.Types;

/**
 * Represents two expressions and an operation
 * 
 * @author <a href="mailto:james@coredevelopers.net">James Strachan</a>
 * @version $Revision$
 */
public class BinaryExpression extends Expression {
    
    private Expression leftExpression;
    private Expression rightExpression;
    private Token operation;
    
    public BinaryExpression(Expression leftExpression,
                            Token operation,
                            Expression rightExpression) {
        this.leftExpression = leftExpression;
        this.operation = operation;
        this.rightExpression = rightExpression;
    }

    public String toString() {
        return super.toString() +"[" + leftExpression + operation + rightExpression + "]";
    }

    public void visit(GroovyCodeVisitor visitor) {
        visitor.visitBinaryExpression(this);
    }

    public Expression transformExpression(ExpressionTransformer transformer) {
        Expression ret = new BinaryExpression(transformer.transform(leftExpression), operation, transformer.transform(rightExpression));
        ret.setSourcePosition(this);
        return ret;
    }

    public Expression getLeftExpression() {
        return leftExpression;
    }

    public void setLeftExpression(Expression leftExpression) {
        this.leftExpression = leftExpression;
    }

    public void setRightExpression(Expression rightExpression) {
        this.rightExpression = rightExpression;
    }

    public Token getOperation() {
        return operation;
    }

    public Expression getRightExpression() {
        return rightExpression;
    }

    public String getText() {
        if (operation.getType() == Types.LEFT_SQUARE_BRACKET) {
            return leftExpression.getText() + "[" + rightExpression.getText() + "]";
        }
        return "(" + leftExpression.getText() + " " + operation.getText() + " " + rightExpression.getText() + ")";
    }
    
    
   /**
    *  Creates an assignment expression in which the specified expression
    *  is written into the specified variable name.   
    */
    
    public static BinaryExpression newAssignmentExpression( Variable variable, Expression rhs ) {
    	VariableExpression lhs = new VariableExpression( variable );
    	Token         operator = Token.newPlaceholder( Types.ASSIGN );
    
    	return new BinaryExpression( lhs, operator, rhs );
    }


    /**
     *  Creates variable initialization expression in which the specified expression
     *  is written into the specified variable name.   
     */
     
     public static BinaryExpression newInitializationExpression( String variable, ClassNode type, Expression rhs ) {
     	VariableExpression lhs = new VariableExpression( variable );
     
     	if( type != null ) {
     	    lhs.setType(type);
     	}
     
     	Token operator = Token.newPlaceholder( Types.ASSIGN );
     
        return new BinaryExpression( lhs, operator, rhs );
     }

}
