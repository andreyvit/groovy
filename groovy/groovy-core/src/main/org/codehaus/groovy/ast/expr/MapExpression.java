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

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.GroovyCodeVisitor;

/**
 * Represents a map expression [1 : 2, "a" : "b", x : y] which creates a mutable Map
 * 
 * @author <a href="mailto:james@coredevelopers.net">James Strachan</a>
 * @version $Revision$
 */
public class MapExpression extends Expression {
    private List mapEntryExpressions;

    public MapExpression() {
        this(new ArrayList());
    }
    
    public MapExpression(List mapEntryExpressions) {
        this.mapEntryExpressions = mapEntryExpressions;
        //TODO: get the type's of the expressions to specify the
        // map type to Map<X> if possible.
        setType(ClassHelper.MAP_TYPE);
    }
    
    public void addMapEntryExpression(MapEntryExpression expression) {
        mapEntryExpressions.add(expression);
    }
    
    public List getMapEntryExpressions() {
        return mapEntryExpressions;
    }

    public void visit(GroovyCodeVisitor visitor) {
        visitor.visitMapExpression(this);
    }

    public boolean isDynamic() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Expression transformExpression(ExpressionTransformer transformer) {
        Expression ret = new MapExpression(transformExpressions(getMapEntryExpressions(), transformer));
        ret.setSourcePosition(this);
        return ret;        
    }
    
    public String toString() {
        return super.toString() + mapEntryExpressions;
    }

    public String getText() {
        StringBuffer sb = new StringBuffer(32);
        sb.append("[");
        int size = mapEntryExpressions.size();
        MapEntryExpression mapEntryExpression = null;
        if (size > 0) {
            mapEntryExpression = (MapEntryExpression) mapEntryExpressions.get(0);
            sb.append(mapEntryExpression.getKeyExpression().getText() + ":" + mapEntryExpression.getValueExpression().getText());
            for (int i = 1; i < size; i++) {
                mapEntryExpression = (MapEntryExpression) mapEntryExpressions.get(i);
                sb.append(", " + mapEntryExpression.getKeyExpression().getText() + ":" + mapEntryExpression.getValueExpression().getText());
                if (sb.length() > 120 && i < size - 1) {
                    sb.append(", ... ");
                    break;
                }
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public void addMapEntryExpression(Expression keyExpression, Expression valueExpression) {
        addMapEntryExpression(new MapEntryExpression(keyExpression, valueExpression));
    }

}
