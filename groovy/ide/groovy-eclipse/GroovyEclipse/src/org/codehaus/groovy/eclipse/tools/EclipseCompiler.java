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
package org.codehaus.groovy.eclipse.tools;

import groovy.lang.CompilerConfig;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.codehaus.groovy.GroovyException;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.CompileUnit;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.classgen.ClassGenerator;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.classgen.Verifier;
import org.codehaus.groovy.syntax.lexer.CharStream;
import org.codehaus.groovy.syntax.parser.Parser;
import org.codehaus.groovy.tools.CompilationFailuresException;
import org.codehaus.groovy.tools.CompilerBugException;
import org.codehaus.groovy.tools.CompilerClassLoader;
import org.codehaus.groovy.tools.ExceptionCollector;
import org.codehaus.groovy.tools.GroovyClass;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.DumpClassVisitor;

/**
*  Controls the compilation process, from source to class generation.
*/

public class EclipseCompiler {
   private Verifier verifier; // Verifies and completes ASTs before byte code generation
   private CompilerClassLoader classLoader; // Our class loader
   private CompilerConfig config = new CompilerConfig(); // compiler configuration
   private boolean verbose = false; // If set, extra output is generated
   private boolean debug = false; // If set, debugging output is generated

   private int maximumParseFailuresPerFile = 10; // Limits the number of parse errors before giving up
   private int maximumFailuresPerCompile = 15; // Limits the number of total errors before giving up

   //---------------------------------------------------------------------------
   // CONSTRUCTORS AND SETTINGS

   /**
    *  Initializes the compiler.
    */

   public EclipseCompiler() {
       this.verifier = new Verifier();
       this.classLoader = new CompilerClassLoader();
   }

   /**
    *  Returns the compiler's class loader.
    */

   protected CompilerClassLoader getClassLoader() {
       return this.classLoader;
   }

   /**
    *  Controls the output verbosity.
    */

   public void setVerbose(boolean verbose) {
       this.verbose = verbose;
   }

   /**
    *  Controls the presence of debugging output.
    */

   public void setDebug(boolean debug) {
       this.debug = debug;
   }

   /**
    *  Adds additional paths to the class loader's search path.
    */

   public void setClasspath(String classpath) throws Exception {
       StringTokenizer paths = new StringTokenizer(classpath, File.pathSeparator);

       while (paths.hasMoreTokens()) {
           getClassLoader().addPath(paths.nextToken());
       }
   }

   public CompilerConfig getConfig() {
       return config;
   }

   public void setConfig(CompilerConfig config) {
       this.config = config;
   }

   //---------------------------------------------------------------------------
   // COMPILATION


   /**
    *  Compiles a set of <code>{@link CharStream}</code> sources.  Collects exceptions
    *  during processing and throws a <code>{@link CompilationFailuresException}</code>
    *  on error.  Other exceptions are bugs that need to be caught and encapsulated.
    *  returns the compilation unit produced
    * @param generateClasses  if false just genertae AST wioth no java classes 
    */
   public CompileUnit compile(CharStream[] sources, List classes, boolean generateClasses ) throws CompilationFailuresException, CompilerBugException {
       CompilationFailuresException failures = new CompilationFailuresException();

       //
       // First up, get at list of source files for error reporting
       // purposes.

       String[] descriptors = new String[sources.length];

       for (int i = 0; i < sources.length; ++i) {
           descriptors[i] = sources[i].getDescription();
           if (descriptors[i] == null) {
               descriptors[i] = "unknown" + i;
           }
       }

       //
       // Next, parse the sources to ASTs.

       CompileUnit unit = new CompileUnit( getClassLoader(), config );

       for (int i = 0; i < sources.length; ++i) {
           try {

               if( verbose ) {
                   System.out.println("building source [" + descriptors[i] + "]");
               }
               
               ModuleNode ast = parse( sources[i], descriptors[i] );
               unit.addModule(ast);
           }
           catch (ExceptionCollector e) {
               if (!e.isEmpty()) {
                   failures.add(descriptors[i], e);
               }

               if (failures.total() > maximumFailuresPerCompile) {
                   throw failures;
               }
           }
           catch( Exception e ) {
               throw new CompilerBugException( descriptors[i], "parse", e );
           }
           finally {
               try {
                   sources[i].close();
               }
               catch (Exception e) {
               }
           }
       }

       //
       // If there were parse errors, bail out.

       if (!failures.isEmpty()) {
           throw failures;
       }

       if(!generateClasses){
       		// control leaves - fast compile no need to gen classes
       		return unit;
       }
       //
       // Next, compile the ASTs to classes.

       for (Iterator iter = unit.getModules().iterator(); iter.hasNext();) {
           ModuleNode module = (ModuleNode) iter.next();
           try {
               Iterator classNodes = module.getClasses().iterator();
               while (classNodes.hasNext()) {
                   ClassNode classNode = (ClassNode) classNodes.next();

                   if (verbose) {
                       System.out.println("Generating class: " + classNode.getName());
                   }

                   classes.addAll(generateClasses(new GeneratorContext(unit), classNode, module.getDescription()));
               }
           }
           catch (ExceptionCollector e) {
               if (!e.isEmpty()) {
                   failures.add(module.getDescription(), e);
               }
           }
           catch( Exception e ) {
               throw new CompilerBugException( module.getDescription(), "class generation", e );
           }
       }

       if (!failures.isEmpty()) {
           throw failures;
       }

       return unit;
   }



  /**
   *  Parses a <code>CharStream</code> source, producing a Concrete
   *  Syntax Tree (CST).  Lexing and parsing errors will be collected in 
   *  an <code>ExceptionCollector</code>.
   */

   protected ModuleNode parse( CharStream charStream, String descriptor ) throws ExceptionCollector, Exception {
       ModuleNode ast = null;

       if (verbose) {
           System.out.println("Parsing: " + descriptor);
       }

       ExceptionCollector collector = new ExceptionCollector(maximumParseFailuresPerFile);

       try {
           Parser parser = Parser.create( charStream, collector );

           collector.throwUnlessEmpty();
           ast = parser.parse( getClassLoader(), descriptor );
       }
       catch (ExceptionCollector e) {
           collector.merge(e, false);
       }
       catch (GroovyException e) {
           collector.add(e, false);
       }

       collector.throwUnlessEmpty();

       return ast;
   }



  /**
   *  Generates a class from an AST.
   */

   protected ArrayList generateClasses(GeneratorContext context, ClassNode classNode, String descriptor)
       throws Exception {
       ArrayList results = new ArrayList();
       ClassGenerator classGenerator = null;
       //        ExceptionCollector collector

       //
       // First, ensure the AST is in proper shape.

       verifier.visitClass(classNode);

       if (debug) {
           DumpClassVisitor dumpVisitor = new DumpClassVisitor(new PrintWriter(new OutputStreamWriter(System.out)));

           classGenerator = new ClassGenerator(context, dumpVisitor, getClassLoader(), descriptor);
           classGenerator.visitClass(classNode);
       }
       else {
           ClassWriter classWriter = new ClassWriter(true);

           classGenerator = new ClassGenerator(context, classWriter, getClassLoader(), descriptor);
           classGenerator.visitClass(classNode);

           byte[] bytes = classWriter.toByteArray();

           results.add(new GroovyClass(classNode.getName(), bytes));
       }

       LinkedList innerClasses = classGenerator.getInnerClasses();

       while (!innerClasses.isEmpty()) {
           results.addAll(generateClasses(context, (ClassNode) innerClasses.removeFirst(), descriptor));
       }

       return results;
       // return (GroovyClass[]) results.toArray( GroovyClass.EMPTY_ARRAY );
   }
}
