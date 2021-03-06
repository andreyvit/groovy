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
//  License.

package gant.tools

import org.codehaus.groovy.gant.GantState

/**
 *  A class providing methods for executing processes in all subdirectories of the working directory
 *  for use in Gant scripts.  This is not really a target but a target support method.
 *
 *  @author Russel Winder <russel@russel.org.uk>
 *  @version $Revision$ $Date$
 */
final class Subdirectories {
  private final Binding binding ;
  Subdirectories ( final Binding binding ) { this.binding = binding ; }
  void runSubprocess ( final String command , final File directory ) {
    if ( GantState.verbosity > GantState.NORMAL ) { println "\n============ ${directory} ================" }
    //  If we allowed ourselves Java SE 5.0 then we could use ProcessBuilder but we restrict ourselves to Java 1.4.
    //def process = ( new ProcessBuilder ( [ 'sh' , '-c' , command ] )).directory ( directory ).start ( )

    //  RC-01 cannot deal with null in the first parameter.
    //def process = command.execute ( null , directory )
    def process = command.execute ( [ ] , directory )
    if ( GantState.verbosity > GantState.QUIET ) { process.in.eachLine { line -> println ( line ) } }
    process.waitFor ( )
  }
  void forAllSubdirectoriesRun ( final String command ) {
    ( new File ( '.' ) ).eachDir { directory -> runSubprocess ( command , directory ) }
  }
  void forAllSubdirectoriesAnt ( final String target ) { forAllSubdirectoriesRun ( 'ant ' + target ) }
  void forAllSubdirectoriesGant ( final String target ) { forAllSubdirectoriesRun ( 'gant ' + target ) }  
}
