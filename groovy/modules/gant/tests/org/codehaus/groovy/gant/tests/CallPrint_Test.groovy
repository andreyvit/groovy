//  Gant -- A Groovy build tool based on scripting Ant tasks
//
//  Copyright © 2006 Russel Winder <russel@russel.org.uk>
//
//  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
//  compliance with the License. You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software distributed under the License is
//  distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
//  implied. See the License for the specific language governing permissions and limitations under the
//  License.ware Foundation, Inc., 51 Franklin St, Fifth Floor,
//  Boston, MA 02110-1301 USA

package org.codehaus.groovy.gant.tests

import gant.Gant

/**
 *  A test to ensure that using standard Groovy functions works.
 *
 *  @author Russel Winder <russel@russel.org.uk>
 *  @version $Revision$ $Date$
 */
final class CallPrint_Test extends GantTestCase {
  void testSystemOutPrintln ( ) {
    System.setIn ( new StringBufferInputStream ( '''
task ( systemOutPrintln : "Do something." ) { System.out.println ( "Hello World" ) }
''' ) )
    Gant.main ( [ '-f' , '-' , 'systemOutPrintln' ] as String[] )
    assertEquals ( '''Hello World
''' , output.toString ( ) ) 
  }
  void testPrintln ( ) {
    System.setIn ( new StringBufferInputStream ( '''
task ( testPrintln : "Do something." ) { println ( "Hello World" ) }
''' ) )
    Gant.main ( [ '-f' , '-' , 'testPrintln' ] as String[] )
    assertEquals ( '''Hello World
''' , output.toString ( ) ) 
  }
  void testMessage ( ) {
    System.setIn ( new StringBufferInputStream ( '''
task ( testMessage : "Do something." ) { message ( 'message' , 'A message.' ) }
''' ) )
    Gant.main ( [ '-f' , '-' , 'testMessage' ] as String[] )
    assertEquals ( '  [message] A message.\n' , output.toString ( ) ) 
  }
}
