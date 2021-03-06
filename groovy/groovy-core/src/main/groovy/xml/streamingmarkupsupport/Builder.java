package groovy.xml.streamingmarkupsupport;
/*

Copyright 2004 (C) John Wilson. All Rights Reserved.

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

import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class Builder extends GroovyObjectSupport {
	protected final Map namespaceMethodMap = new HashMap();
	
	public Builder(final Map namespaceMethodMap) {
	final Iterator keyIterator = namespaceMethodMap.keySet().iterator();
		
		while (keyIterator.hasNext()) {
		final Object key = keyIterator.next();
		final List value = (List)namespaceMethodMap.get(key);
		final Closure dg = ((Closure)value.get(1)).asWritable();
		
			this.namespaceMethodMap.put(key, new Object[]{value.get(0), dg, fettleMethodMap(dg, (Map)value.get(2))});
		}
	}
	
	private static Map fettleMethodMap(final Closure defaultGenerator, final Map methodMap) {
	final Map newMethodMap = new HashMap();
	final Iterator keyIterator = methodMap.keySet().iterator();
		
		while (keyIterator.hasNext()) {
		final Object key = keyIterator.next();
		final Object value = methodMap.get(key);
		
			if ((value instanceof Closure)) {
				newMethodMap.put(key, value);
			} else {
				newMethodMap.put(key, defaultGenerator.curry((Object[])value));
			}
		}
		
		return newMethodMap;
	}
	
	abstract public Object bind(Closure root);
	
	protected static abstract class Built extends GroovyObjectSupport {
	protected final Closure root;
	protected final Map namespaceSpecificTags = new HashMap();
		
		public Built(final Closure root, final Map namespaceTagMap) {
			this.namespaceSpecificTags.putAll(namespaceTagMap);
		
			this.root = (Closure)root.clone();
			
			this.root.setDelegate(this);
		}
	}
}
