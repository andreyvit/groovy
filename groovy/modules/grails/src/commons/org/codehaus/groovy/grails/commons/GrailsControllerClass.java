/*
 * Copyright 2004-2005 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
package org.codehaus.groovy.grails.commons;


/**
 * <p>Represents a controller class in Grails.
 * 
 * @author Steven Devijver
 * @since Jul 2, 2005
 */
public interface GrailsControllerClass extends InjectableGrailsClass {

	/**
	 * <p>Gets the list of all possible URI's available in this controller.
	 * 
	 * @return list of all possible URI's
	 */
	public String[] getURIs();
	
	/**
	 * <p>Tests if a controller maps to a given URI.
	 * 
	 * @return true if controller maps to URI
	 */
	public boolean mapsToURI(String uri);
	
	/**
	 * <p>Looks for a string property named &lt;closureName&gt;View and returns
	 * that value as view name or null if not found.
	 * 
	 * @param closureName the name of URI
	 * @return the view name of null if not found
	 */
	public String getViewName(String uri);

	/**
	 * <p>Returns a closure property name for a specific URI or null if the URI does not map to a closure.
	 * 
	 * @param uri the URI of the request
	 * @return the closure property name mapped to the URI or null is no closure was found
	 */
	public String getClosurePropertyName(String uri);

}