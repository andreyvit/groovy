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

package gls.ch06.s05.testClasses;


import org.codehaus.groovy.runtime.InvokerHelper;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;

/**
 * @author John Wilson
 *
 */

public class Tt1cgi extends Tt1c implements GroovyObject {

  private MetaClass metaClass = InvokerHelper.getMetaClass(this);
  
  public MetaClass getMetaClass() {
    return this.metaClass;
  }

  public Object getProperty(final String property) {
    return this.metaClass.getProperty(this, property);
  }

  public Object invokeMethod(final String name, final Object args) {
    return this.metaClass.invokeMethod(this, name, args);
  }

  public void setMetaClass(final MetaClass metaClass) {
    this.metaClass = metaClass;
  }

  public void setProperty(final String property, final Object newValue) {
    this.metaClass.setProperty(this, property, newValue);
  }
}
