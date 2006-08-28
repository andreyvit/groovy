/*
 * Copyright 2005 John G. Wilson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package groovy.util.slurpersupport;

import groovy.lang.Buildable;
import groovy.lang.Closure;
import groovy.lang.DelegatingMetaClass;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.MetaClass;
import groovy.lang.Writable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.codehaus.groovy.runtime.DefaultGroovyMethods;


/**
 * @author John Wilson
 *
 */

public abstract class GPathResult extends GroovyObjectSupport implements Writable, Buildable {
  protected final GPathResult parent;
  protected final String name;
  protected final String namespacePrefix;
  protected final Map namespaceMap = new HashMap();
  protected final Map namespaceTagHints;
 
  /**
   * @param parent
   * @param name
   */
  public GPathResult(final GPathResult parent, final String name, final String namespacePrefix, final Map namespaceTagHints) {
    if (parent == null) {
      // we are the top of the tree
      this.parent = this;
      this.namespaceMap.put("xml", "http://www.w3.org/XML/1998/namespace");  // The XML namespace is always defined
    } else {
      this.parent = parent;
      this.namespaceMap.putAll(parent.namespaceMap);
    }
    this.name = name;
    this.namespacePrefix = namespacePrefix;
    this.namespaceTagHints = namespaceTagHints;
    
    setMetaClass(getMetaClass()); // wrap the standard MetaClass with the delegate
  }

    /* (non-Javadoc)
     * @see groovy.lang.GroovyObjectSupport#setMetaClass(groovy.lang.MetaClass)
     */
    public void setMetaClass(final MetaClass metaClass) {
    final MetaClass newMetaClass = new DelegatingMetaClass(metaClass) {
                                        /* (non-Javadoc)
                                         * @see groovy.lang.DelegatingMetaClass#getAttribute(java.lang.Object, java.lang.String)
                                         */
                                        public Object getAttribute(Object object, String attribute) {
                                            return GPathResult.this.getProperty("@" + attribute);
                                        }
                                    };

        super.setMetaClass(newMetaClass);
    }

public Object getProperty(final String property) {
    if ("..".equals(property)) {
      return parent();
    } else if ("*".equals(property)){
        return children();
    } else if ("**".equals(property)){
        return depthFirst();
    } else if (property.startsWith("@")) {
      if (property.indexOf(":") != -1) {
      final int i = property.indexOf(":");

        return new Attributes(this, "@" + property.substring(i + 1), property.substring(1, i), this.namespaceTagHints);
      } else {
        return new Attributes(this, property, this.namespaceTagHints);
      }
    } else {
      if (property.indexOf(":") != -1) {
      final int i = property.indexOf(":");

        return new NodeChildren(this, property.substring(i + 1), property.substring(0, i), this.namespaceTagHints);
      } else {
        return new NodeChildren(this, property, this.namespaceTagHints);
      }
    }
  }

  public String name() {
    return this.name;
  }

  public GPathResult parent() {
    return this.parent;
  }

  public GPathResult children() {
    return new NodeChildren(this, this.namespaceTagHints);
  }

  public String toString() {
    return text();
  }
  
  public Integer toInteger() {
      return DefaultGroovyMethods.toInteger(text());
  }
  
  public Long toLong() {
      return DefaultGroovyMethods.toLong(text());
  }
  
  public Float toFloat() {
      return DefaultGroovyMethods.toFloat(text());
  }
  
  public Double toDouble() {
      return DefaultGroovyMethods.toDouble(text());
  }
  
  public BigDecimal toBigDecimal() {
      return DefaultGroovyMethods.toBigDecimal(text());
  }
  
  public BigInteger toBigInteger() {
      return DefaultGroovyMethods.toBigInteger(text());
  }
  
  public URL toURL() throws MalformedURLException {
      return DefaultGroovyMethods.toURL(text());
  }
  
  public URI toURI() throws URISyntaxException {
      return DefaultGroovyMethods.toURI(text());
  }
  
  public Boolean toBoolean() {
      return DefaultGroovyMethods.toBoolean(text());
  }
  
  public GPathResult declareNamespace(final Map newNamespaceMapping) {
    this.namespaceMap.putAll(newNamespaceMapping);
    
    return this; 
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals(Object obj) {
    return text().equals(obj.toString());
  }

  public Object getAt(final int index) {
  final Iterator iter = iterator();
  int count = 0;
  
  
    while (iter.hasNext()) {
      if (count++ == index) {
        return iter.next();
      } else {
        iter.next();
      }
    }
    
    throw new ArrayIndexOutOfBoundsException(index);
  }
  
  public Iterator depthFirst() {
      return new Iterator() {
        private final Stack stack = new Stack();
        private Iterator iter = iterator();
        private GPathResult next = getNextByDepth();
      
          public boolean hasNext() {
            return this.next != null;
          }
          
          public Object next() {
            try {
              return this.next;
            } finally {
              this.next = getNextByDepth();
            }
          }
          
          public void remove() {
            throw new UnsupportedOperationException();
          }
          
          private GPathResult getNextByDepth() {
            while (this.iter.hasNext()) {
            final GPathResult node = (GPathResult)this.iter.next();
            
              this.stack.push(node);
              this.stack.push(this.iter);
              this.iter = node.children().iterator();
            }
            
            if (this.stack.empty()) {
              return null;
            } else {
              this.iter = (Iterator)this.stack.pop();
              return (GPathResult)this.stack.pop();
            }
          }
      };
  }
  
  public List list() {
  final Iterator iter = nodeIterator();
  final List result = new LinkedList();
  
    while (iter.hasNext()) {
      result.add(new NodeChild((Node)iter.next(), this.parent, this.namespacePrefix, this.namespaceTagHints));
    }
    
    return result;
  }

  public abstract int size();
  
  public abstract String text();
  
  public abstract GPathResult parents();
  
  public abstract Iterator childNodes();
  
  public abstract Iterator iterator();
  
  public abstract GPathResult find(Closure closure);
  
  public abstract GPathResult findAll(Closure closure);
  
  public abstract Iterator nodeIterator();
}