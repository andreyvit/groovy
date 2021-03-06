/**
 *
 * Copyright 2005 Jeremy Rayner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **/
package org.codehaus.groovy.antlr.treewalker;

import groovy.util.GroovyTestCase;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;

import org.codehaus.groovy.antlr.AntlrASTProcessor;
import org.codehaus.groovy.antlr.SourceBuffer;
import org.codehaus.groovy.antlr.UnicodeEscapingReader;
import org.codehaus.groovy.antlr.parser.GroovyLexer;
import org.codehaus.groovy.antlr.parser.GroovyRecognizer;

import antlr.collections.AST;

/**
 * Testcases for the antlr AST visitor that prints groovy source code.
 *
 * @author <a href="mailto:groovy@ross-rayner.com">Jeremy Rayner</a>
 * @version $Revision$
 */
public class SourcePrinterTest extends GroovyTestCase {

	public void testAbstract() throws Exception {
		assertEquals("public abstract class Foo {}", pretty("public abstract class Foo{}"));
	}
	
    public void testAnnotation() throws Exception{
        assertEquals("@Crimson foo", pretty("@Crimson foo"));
        assertEquals("@Override int hashCode() {return 0}", pretty("@Override int hashCode() {return 0}"));
    }
    
    public void testAnnotations() throws Exception{
    	assertEquals("@Important @Blue package foo.bar",pretty("@Important @Blue package foo.bar"));
    }

    public void testAnnotationArrayInit() throws Exception{
    	// obsolete java syntax
    }
    
    public void testAnnotationDef() throws Exception{
    	// todo - 17 July 2006 - test fine, however this parses but causes error in AntlrParserPlugin
        assertEquals("public @interface Foo{}", pretty("public @interface Foo{}"));
    }
    
    public void testAnnotationFieldDef() throws Exception{
    	assertEquals("public @interface Foo{int bar() default 123}", pretty("public @interface Foo{int bar() default 123}"));
    }
    
    public void testAnnotationMemberValuePair() throws Exception{
    	assertEquals("@Prime(value = 17) int foo",pretty("@Prime(value=17) int foo"));
    	assertEquals("@Blue(v = 3, v = 5) int bar",pretty("@Blue(v = 3, v = 5) int bar"));
    }
    
    public void testArrayDeclarator() throws Exception {
    	assertEquals("int[] primes = new int[5]", pretty("int[] primes = new int[5]"));
    }
    
    public void testAssign() throws Exception {
        assertEquals("a = 12", pretty("a=12"));
    }
    
    public void testBand() throws Exception {
    	assertEquals("def x = 1 & 2", pretty("def x=1&2"));
    }

    public void testBandAssign() throws Exception {
    	assertEquals("x &= 2", pretty("x&=2"));
    }
    
    public void testBnot() throws Exception {
    	assertEquals("def z = ~123", pretty("def z = ~123"));
    }
    
    public void testBor() throws Exception {
    	assertEquals("def y = 1 | 2", pretty("def y = 1 | 2"));
    }
    
    public void testBorAssign() throws Exception {
    	assertEquals("y |= 2", pretty("y|=2"));
    }

    public void testBsr() throws Exception {
    	// unsigned right shift
    	assertEquals("def q = 1 >>> 2", pretty("def q = 1 >>> 2"));
    }

    public void testBsrAssign() throws Exception {
    	assertEquals("y >>>= 2", pretty("y>>>=2"));
    }
    
    public void testBxor() throws Exception {
    	assertEquals("def y = true ^ false", pretty("def y = true ^ false"));
    }
    
    public void testBxorAssign() throws Exception {
    	assertEquals("y ^= false", pretty("y^=false"));
    }

    public void testCaseGroup() throws Exception {
        assertEquals("switch (foo) {case bar:x = 1}", pretty("switch(foo){case bar:x=1}"));
    }
    
    public void testClassDef() throws Exception {
        assertEquals("class Foo {def bar}", pretty("class Foo{def bar}"));
    }

    public void testClosedBlock() throws Exception{
        assertEquals("[1, 2, 3].each {println it}", pretty("[1,2,3].each{println it}"));
        assertEquals("def x = foo.bar(mooky){ x, y -> wibble(y, x)}", pretty("def x = foo.bar(mooky) {x,y-> wibble(y,x)}"));
        // todo: above is not quite the spacing I would expect, but good enough for now...
    }
    
    public void testCompareTo() throws Exception{
    	assertEquals("1 <=> 2", pretty("1<=>2"));
    }
    
    public void testCtorCall() throws Exception{
    	assertEquals("class Foo {Foo(int x) {this()}}",pretty("class Foo{Foo(int x) {this()}}"));
    	assertEquals("class Foo {Foo( x) {this()}}",pretty("class Foo{Foo(x) {this()}}"));
        // todo: above is not quite the spacing I would expect, but good enough for now...
    }
    
    public void testCtorIdent() throws Exception {
        assertEquals("class Foo {private Foo() {}}", pretty("class Foo {private Foo() {}}"));
    }
    
    public void testDec() throws Exception {
    	assertEquals("--b", pretty("--b"));
    }

    public void testDiv() throws Exception {
    	assertEquals("1 / 2", pretty("1/2"));
    }
    
    public void testDivAssign() throws Exception {
    	assertEquals("x /= 2", pretty("x/=2"));
    }
    
    public void testDot() throws Exception {
    	assertEquals("java.util.Date d = new java.util.Date()", pretty("java.util.Date d = new java.util.Date()"));
    	assertEquals("class Foo extends java.util.Date {}", pretty("class Foo extends java.util.Date {}"));
    	assertEquals("foo.bar.mooky()", pretty("foo.bar.mooky()"));
    	assertEquals("package foo.bar", pretty("package foo.bar"));
    	assertEquals("import java.util.Date", pretty("import java.util.Date"));
    	assertEquals("import java.io.*", pretty("import java.io.*"));
    	assertEquals("@foo.Bar mooky", pretty("@foo.Bar mooky"));
    	assertEquals("def foo() throws bar.MookyException{}", pretty("def foo() throws bar.MookyException{}"));
    	assertEquals("def x = \"${foo.bar}\"", pretty("def x = \"${foo.bar}\""));
    }
    
    public void testDynamicMember() throws Exception{
    	assertEquals("foo.(bar)", pretty("foo.(bar)"));
    	assertEquals("foo.\"${bar}\"", pretty("foo.\"${bar}\""));
    }
    
    public void testElist() throws Exception {
    	assertEquals("println 2 + 2", pretty("println 2 + 2"));
    	assertEquals("for (i = 0, j = 2 ; i < 10 ; i++, j--){print i}", pretty("for (i = 0,j = 2;i < 10; i++, j--) {print i}"));
    	assertEquals("foo()", pretty("foo()")); // empty ELIST
    	assertEquals("foo(bar, mooky)", pretty("foo( bar , mooky )"));
    }

    public void testEnumConstantDef() throws Exception {
    	assertEquals("enum Coin {PENNY(1), DIME(10), QUARTER(25)}", pretty("enum Coin {PENNY(1), DIME(10), QUARTER(25)}"));
    }

    public void testEnumDef() throws Exception {
    	assertEquals("enum Season {WINTER, SPRING, SUMMER, AUTUMN}", pretty("enum Season{WINTER,SPRING,SUMMER,AUTUMN}"));
    	//todo: more strange spacing in following line
    	assertEquals("enum Operation {ADDITION{double eval( x,  y) {return x + y}}}",pretty("enum Operation {ADDITION {double eval(x,y) {return x + y}}}"));    	
    }
    
    public void testEqual() throws Exception {
        assertEquals("a == b", pretty("a==b"));
    }

    public void testEsc_FAILS() throws Exception { if (notYetImplemented()) return;
    	// dquote-tab-dquote
    	assertEquals("println \"\\\"\t\\\"\"", pretty("println \"\\\"\t\\\"\""));
    }
    
    public void testExponent() throws Exception {
    	assertEquals("println 1.2e-10", pretty("println 1.2e-10"));
    }
    
    public void testExpr_FAILS() throws Exception { if (notYetImplemented()) return;
    	assertEquals("System.out.println(x)", pretty("System.out.println(x)"));
    	assertEquals("return f", pretty("return f"));
        assertEquals("foo(bar);mooky(bar)", pretty("foo(bar);mooky(bar)"));
    }

    public void testExtendsClause() throws Exception {
        assertEquals("class Foo extends Bar {}", pretty("class Foo extends Bar {}"));
        assertEquals("interface Wibble extends Mooky{}", pretty("interface Wibble extends Mooky {}"));
        //todo spacing is odd, c.f. last space in class vs interface above
    }
    
    public void testFinal() throws Exception {
    	assertEquals("public final int getX() {return 0}", pretty("public final int getX() {return 0}"));
    }
    public void testForCondition() throws Exception {
    	assertEquals("for (i = 0 ; i < 10 ; i++){println i}", pretty("for (i=0;i<10;i++) {println i}"));
    }
    
    // testForInit() covered by testForCondition()
    
    public void testForInIterable() throws Exception {
        assertEquals("for (i in [1, 2]) {}", pretty("for (i in [1,2]) {}"));
    }

    // testForIterator() covered by testForCondition()
    
    public void testGe() throws Exception {
    	assertEquals("if (60 >= 70) {}", pretty("if (60>=70) {}"));
    }
    
    public void testGt() throws Exception {
        assertEquals("if (2070 > 354) {}", pretty("if (2070 > 354) {}"));
    }

    public void testHexDigit() throws Exception {
        assertEquals("def bar = 0xCaFe", pretty("def bar = 0xCaFe"));    	
    }
    public void testHexDigitInUnicodeEscape_FAILS() throws Exception { if (notYetImplemented()) return;
      assertEquals("def foo = '\\ubabe'", pretty("def foo = '\\ubabe'"));
    }
    
    public void testIdent() throws Exception {
    	// used _everywhere_ , lets assume that the other specific 
    	// testcases include enough ident usage for now.
        assertEquals("foo.bar", pretty("foo.bar"));
    }

    public void testImplementsClause() throws Exception {
        assertEquals("class Foo implements Bar {}", pretty("class Foo implements Bar {}"));
    }

    public void testImplicitParameters() throws Exception {
    	assertEquals("[1, 2, 3].each {println it}", pretty("[1,2,3].each{println it}"));
    }
    
    public void testImport() throws Exception {
        assertEquals("import foo.bar.Wibble", pretty("import foo.bar.Wibble"));
    }
    
    public void testInc() throws Exception {
    	assertEquals("++x",pretty("++x"));
    }

    public void testIndexOp() throws Exception {
        assertEquals("foo.bar()[fred.wilma()]", pretty("foo.bar()[fred.wilma()]"));
    }
    
    public void testInterfaceDef() throws Exception {
    	//todo, the spacing here is... unusual
    	assertEquals("interface Foo{void blah() }", pretty("interface Foo{void blah()}"));
    }

    public void testInstanceInit() throws Exception {
    	assertEquals("class Foo {{x = 1}}", pretty("class Foo {{x=1}}"));
    }

    public void testLabeledArg() throws Exception {
        assertEquals("myMethod(argOne:123, argTwo:123)", pretty("myMethod(argOne:123,argTwo:123)"));
        assertEquals("myMap = [keyOne:123, keyTwo:234]", pretty("myMap = [keyOne:123,keyTwo:234]"));
    }

    public void testLabeledStat() throws Exception {
     	assertEquals("foo:x = 1", pretty("foo:x = 1"));
    }
    
    public void testLand() throws Exception {
        assertEquals("true && false", pretty("true && false"));
    }
    
    public void testLe() throws Exception {
    	assertEquals("if (60 <= 70) {}", pretty("if (60<=70) {}"));
    }    

    public void testListConstructor() throws Exception {
        assertEquals("[a, b]", pretty("[a,b]"));
    }

    public void testLiteralAny() throws Exception {
        assertEquals("any x = 2", pretty("any x = 2"));
    }

    public void testLiteralAs() throws Exception {
        assertEquals("import java.util.Date as MyDate", pretty("import java.util.Date as MyDate"));
        // todo suspicious spacing in the following assertion
        assertEquals("x = 12 as Long ", pretty("x = 12 as Long"));
    }

    public void testLiteralAssert() throws Exception {
        assertEquals("assert a == true", pretty("assert a== true"));
        assertEquals("assert b == true : 99", pretty("assert b==true:99"));
        // todo is ',' deprecated now?
        //assertEquals("assert b == true , 99", pretty("assert b==true,99"));
    }

    public void testLiteralBoolean() throws Exception {
        assertEquals("boolean b = true", pretty("boolean b =true"));
    }
    
    public void testLiteralBreak() throws Exception {
    	assertEquals("for (i in 1..100) {break }", pretty("for (i in 1..100) {break}"));
        assertEquals("switch (foo) {default:break }", pretty("switch(foo){default:break}"));
        assertEquals("def myMethod() {break }", pretty("def myMethod(){break}"));
    	assertEquals("for (i in 1..100) {break 2}", pretty("for (i in 1..100) {break 2}"));
    	//todo should the colon be postfixed to the label?
    	assertEquals("for (i in 1..100) {break label1:}", pretty("for (i in 1..100) {break label1:}"));
    }

    public void testLiteralByte() throws Exception {
        assertEquals("byte b = 1", pretty("byte b=1"));
    }

    public void testLiteralCase() throws Exception {
        assertEquals("switch (foo) {case 1:x = 3}", pretty("switch(foo){case 1:x=3}"));
    }
    
    public void testLiteralCatch() throws Exception {
        assertEquals("try {} catch (Exception e) {}", pretty("try {} catch (Exception e) {}"));
        assertEquals("try {} catch (Exception e1) {} catch (Exception2 e2) {}", pretty("try {} catch (Exception e1) {} catch (Exception2 e2) {}"));
    }

    public void testLiteralChar() throws Exception {
        assertEquals("char c = \"a\"", pretty("char c = \"a\""));
    }

    public void testLiteralClass() throws Exception {
    	assertEquals("public class Foo {int bar}", pretty("public class Foo{int bar}"));
    }
    
    public void testLiteralContinue() throws Exception {
    	assertEquals("for (i in 1..100) {continue }", pretty("for (i in 1..100) {continue}"));
    	assertEquals("for (i in 1..100) {continue 2}", pretty("for (i in 1..100) {continue 2}"));
    	//todo should the colon be postfixed to the label?
    	assertEquals("for (i in 1..100) {continue label1:}", pretty("for (i in 1..100) {continue label1:}"));
    	assertEquals("[1, 2, 3].each {continue }", pretty("[1,2,3].each{continue}"));
    }

    public void testLiteralDef() throws Exception {
    	assertEquals("def x = 123", pretty("def x=123"));
    	assertEquals("def myMethod() {return 0}", pretty("def myMethod(){return 0}"));
    	// note: def not needed in parameter declarations, but it is valid
    	//todo: is it ok to strip out 'def' from parameter declarations?
    	assertEquals("def foo( bar) {}", pretty("def foo(def bar){}"));
    }
    
    public void testLiteralDefault() throws Exception {
        assertEquals("switch (foo) {default:x = 2}", pretty("switch(foo){default:x=2}"));
    	assertEquals("public @interface Foo{int bar() default 123}", pretty("public @interface Foo{int bar() default 123}"));
    }
    
    public void testLiteralDouble() throws Exception {
    	assertEquals("double d = 1.0", pretty("double d = 1.0"));
    }
    
    public void testLiteralElse() throws Exception {
    	assertEquals("if (false) {a = 1} else {a = 2}", pretty("if (false) {a=1} else {a=2}"));
    }

    public void testLiteralEnum() throws Exception {
    	assertEquals("enum Season {WINTER, SPRING, SUMMER, AUTUMN}", pretty("enum Season{WINTER,SPRING,SUMMER,AUTUMN}"));
    }
    
    public void testLiteralExtends() throws Exception {
    	assertEquals("class Foo extends java.util.Date {}", pretty("class Foo extends java.util.Date {}"));
        assertEquals("class Foo extends Bar {}", pretty("class Foo extends Bar {}"));
        assertEquals("interface Wibble extends Mooky{}", pretty("interface Wibble extends Mooky {}"));
        //todo spacing is odd, c.f. last space in class vs interface above
    	assertEquals("public boolean process(Set<? extends TypeElement> annotations) {println annotations}", pretty("public boolean process(Set<? extends TypeElement> annotations) {println annotations}"));
    }
    
    public void testLiteralFalse() throws Exception {
        assertEquals("if (false) {}", pretty("if (false) {}"));
    }

    public void testLiteralFinally() throws Exception {
        assertEquals("try {}finally {}", pretty("try {}finally {}"));
    }

    public void testLiteralFloat() throws Exception {
        assertEquals("float x", pretty("float x"));
    }

    public void testLiteralFor() throws Exception {
        assertEquals("for (i in [1, 2, 3]) {}", pretty("for (i in [1,2,3]) {}"));
        // check non-braced single statement
        assertEquals("for (i in 1..100) rotateAntiClockwise()", pretty("for (i in 1..100) rotateAntiClockwise()"));
    }

    public void testLiteralIf() throws Exception {
        assertEquals("if (a == b) return false", pretty("if (a==b) return false"));
        assertEquals("if (a == b) {}", pretty("if (a==b) {}"));
    }

    public void testLiteralImplements() throws Exception {
        assertEquals("class Foo implements Bar {}", pretty("class Foo implements Bar {}"));
        //todo the following is legal Java, but pretty strange...?
        assertEquals("enum EarthSeason implements Season {SPRING}", pretty("enum EarthSeason implements Season{SPRING}"));
    }
    
    public void testLiteralImport() throws Exception {
    	assertEquals("import foo.Bar", pretty("import foo.Bar"));
    	assertEquals("import mooky.*", pretty("import mooky.*"));
    }
    
    public void testLiteralIn() throws Exception {
    	assertEquals("for (i in 1..10) {}", pretty("for (i in 1..10) {}"));
    	assertEquals("if (i in myList) {}", pretty("if (i in myList) {}"));
    }

    public void testLiteralInstanceOf() throws Exception {
        assertEquals("if (a instanceof String) {}", pretty("if (a instanceof String) {}"));
    }
    public void testLiteralInt() throws Exception {
        assertEquals("int a", pretty("int a"));
    }
    
    public void testLiteralInterface() throws Exception {
    	assertEquals("interface Foo{}", pretty("interface Foo{}"));
    }
    public void testLiteralLong() throws Exception {
        assertEquals("long a = 1", pretty("long a = 1"));
    }
    public void testLiteralNative() throws Exception {
        assertEquals("public class R {public native void seek(long pos) }", pretty("public class R{public native void seek(long pos)}"));
        assertEquals("native foo() ", pretty("native foo()"));
    }
    public void testLiteralNew() throws Exception {
        assertEquals("new Foo()", pretty("new Foo()"));
        assertEquals("def x = new int[5]", pretty("def x = new int[5]"));
    }

    public void testLiteralNull() throws Exception {
        assertEquals("def foo = null", pretty("def foo=null"));
    }

    public void testLiteralPackage() throws Exception {
    	assertEquals("package foo.bar", pretty("package foo.bar"));
    }
    public void testLiteralPrivate() throws Exception{
        assertEquals("private bar", pretty("private bar"));
    }

    public void testLiteralProtected() throws Exception{
        assertEquals("protected mooky", pretty("protected mooky"));
    }

    public void testLiteralPublic() throws Exception{
        assertEquals("public foo", pretty("public foo"));
    }

    public void testLiteralReturn() throws Exception {
        assertEquals("def foo() {return false}", pretty("def  foo() { return false }"));
        assertEquals("void bar() {return }", pretty("void bar() {return}"));
    }

    public void testLiteralShort() throws Exception {
        assertEquals("short a = 1", pretty("short a = 1"));
    }

    public void testLiteralStatic() throws Exception {
        assertEquals("static void foo() {}", pretty("static void foo() {}"));
        //classes, interfaces, class/instance vars and methods
        assertEquals("static int bar = 1", pretty("static int bar = 1"));
        //todo: this should parse... assertEquals("private static <T> void foo(List<T> list){}", pretty("private static <T> void foo(List<T> list){}"));
        assertEquals("class Foo {static {bar = 1}}", pretty("class Foo{static {bar=1}}"));
    }

    public void testLiteralSuper() throws Exception {
    	assertEquals("class Foo {public Foo() {super()}}", pretty("class Foo{public Foo(){super()}}"));
    	// todo will 'super' be allowed in non-parentheses method call styles?
    	assertEquals("class Bar {public Bar() {super 99}}", pretty("class Bar{public Bar(){super 99}}"));
    	assertEquals("class Bar {public Bar() {super(1, 2, 3)}}", pretty("class Bar{public Bar(){super(1,2,3)}}"));
    	assertEquals("println(super.toString())", pretty("println(super.toString())"));
    	//todo: doesn't parse correctly...   assertEquals("class Foo<T super C> {T t}",pretty("class Foo<T super C> {T t}"));
    }
    public void testLiteralSwitch() throws Exception {
        assertEquals("switch (foo) {case bar:x = 2}", pretty("switch(foo){case bar:x=2}"));
    }
        
    public void testLiteralSynchronized() throws Exception {
    	assertEquals("synchronized foo() {}", pretty("synchronized foo(){}"));
    	assertEquals("synchronized (t) {doStuff(t)}", pretty("synchronized (t) {doStuff(t)}"));
    }

    public void testLiteralThis() throws Exception {
    	assertEquals("this",pretty("this"));
    	assertEquals("this 2",pretty("this 2"));
    	assertEquals("this()",pretty("this()"));
    	assertEquals("this(1, 2, 3)",pretty("this(1,2,3)"));
        assertEquals("this.x = this.y", pretty("this.x=this.y"));
    }
    
    public void testLiteralThreadsafe() throws Exception {
    	assertEquals("threadsafe foo() {}", pretty("threadsafe foo() {}"));
    }
    
    public void testLiteralThrow() throws Exception {
        assertEquals("def foo() {if (false) throw new RuntimeException()}", pretty("def foo() {if (false) throw new RuntimeException()}"));
    }
    public void testLiteralThrows() throws Exception {
    	//todo AntlrParserPlugin: Unexpected node type: '.' found when expecting type: an identifier
    	assertEquals("def foo() throws java.io.IOException{}", pretty("def foo() throws java.io.IOException{}"));
    }
    public void testLiteralTransient() throws Exception {
    	assertEquals("transient bar", pretty("transient bar"));
    }
    
    public void testLiteralTrue() throws Exception {
        assertEquals("foo = true", pretty("foo = true"));
    }

    public void testLiteralTry() throws Exception {
        assertEquals("try {} catch (Exception e) {}", pretty("try {} catch (Exception e) {}"));
    }

    public void testLiteralVoid() throws Exception {
        assertEquals("void foo() {}", pretty("void foo(){}"));
    }
    public void testLiteralVolatile() throws Exception {
    	assertEquals("volatile mooky", pretty("volatile mooky"));
    }

    public void testLiteralWhile() throws Exception {
        assertEquals("while (true) {}", pretty("while(true){}"));
    }

    public void testLiteralWith() throws Exception {
        assertEquals("with (myObject) {x = 1}", pretty("with(myObject) {x = 1}"));
    }
    public void testLnot() throws Exception {
        assertEquals("if (!isRaining) {}", pretty("if (!isRaining) {}"));
    }

    public void testLor() throws Exception {
        assertEquals("true || false", pretty("true || false"));
    }
    public void testLparen_FAILS() throws Exception { if (notYetImplemented()) return;
    	assertEquals("for (i in (history.size() - 1)..0) {}", pretty("for (i in (history.size() - 1)..0) {}"));
    }
    public void testLt() throws Exception {
        assertEquals("if (3.4f < 12f) {}", pretty("if (3.4f < 12f) {}"));
    }
    public void testMapConstructor() throws Exception{
        assertEquals("Map foo = [:]", pretty("Map foo = [:]"));
        assertEquals("[a:1, b:2]", pretty("[a:1,b:2]"));
    }

    public void testMemberPointer() throws Exception {
        assertEquals("def x = foo.&bar()", pretty("def x=foo.&bar()"));
    }
    public void testMethodCall() throws Exception {
        assertEquals("foo(bar)", pretty("foo(bar)"));
        assertEquals("[1, 2, 3].each {println it}", pretty("[1,2,3].each{println it}"));
        assertEquals("foo(bar){mooky()}", pretty("foo(bar){mooky()}"));
    }

    public void testMethodDef() throws Exception{
        assertEquals("def foo(int bar, boolean boo) {}", pretty("def foo(int bar,boolean boo) {}"));
    }

    public void testMinus() throws Exception {
        assertEquals("def bar = 4 - foo", pretty("def bar=4-foo"));
    }

    public void testMinusAssign() throws Exception {
        assertEquals("x -= 23", pretty("x -= 23"));
    }

    public void testMod() throws Exception {
        assertEquals("x = 4 % 3", pretty("x = 4 % 3"));
    }

    public void testModifiers() throws Exception {
    	assertEquals("public static transient final native threadsafe synchronized volatile strictfp foo() {}", pretty("public static transient final native threadsafe synchronized volatile strictfp foo() {}"));
    }
    
    public void testModAssign() throws Exception {
        assertEquals("x %= 23", pretty("x %= 23"));
    }

    public void testNotEqual() throws Exception {
        assertEquals("a != b", pretty("a!=b"));
    }

    public void testNumBigDecimal() throws Exception {
    	assertEquals("a = 9.8g", pretty("a  =9.8g"));
    }
    public void testNumBigInt() throws Exception {
    	assertEquals("a = 12g", pretty("a=   12g"));
    }
    
    public void testNumDouble() throws Exception {
        assertEquals("b = 34.4d", pretty("b=34.4d"));
        assertEquals("b = 34.4D", pretty("b=34.4D"));
    }
    public void testNumFloat() throws Exception {
        assertEquals("b = 34.4f", pretty("b=34.4f"));
        assertEquals("b = 34.4F", pretty("b=34.4F"));
    }
    public void testNumInt() throws Exception {
        assertEquals("a = 12", pretty("a=12"));
    }

    public void testNumLong() throws Exception {
    	assertEquals("a = 12l", pretty("a=12l"));    	
    }
    public void testObjblock() throws Exception {
        assertEquals("class Foo {def bar}", pretty("class Foo {def bar}"));
    }
    public void testOptionalDot() throws Exception {
        assertEquals("foo = england.london.kings?.head", pretty("foo = england.london.kings?.head"));
    }

    public void testPackageDef() throws Exception {
        assertEquals("package foo.bar", pretty("package foo.bar"));
    }

    public void testParameterDef() throws Exception {
    	//todo spacing slightly suspect with "foo( bar)"
    	assertEquals("def foo( bar) {}", pretty("def foo(bar){}"));
        assertEquals("def foo(String bar, String mooky) {}", pretty("def foo(String bar, String mooky){}"));
        assertEquals("def c = { x -> println x}", pretty("def c = {x->println x}"));
        assertEquals("def c = { x, y -> println(x + y)}", pretty("def c={x,y->println(x+y)}"));
        assertEquals("def c = {int x,int y -> println(x + y)}", pretty("def c={int x, int y->println(x+y)}"));
    }

    public void testParameters() throws Exception {
        assertEquals("def foo(String bar, String mooky) {}", pretty("def foo(String bar, String mooky){}"));
    }
    public void testPlus() throws Exception {
        assertEquals("a + b", pretty("a+b"));
    }
    
    public void testPlusAssign() throws Exception {
        assertEquals("x += 23", pretty("x += 23"));
    }

    public void testPostDec() throws Exception {
    	assertEquals("a--", pretty("a--"));
    }
    public void testPostInc() throws Exception {
    	assertEquals("a++", pretty("a++"));
    }
    public void testQuestion() throws Exception {
        assertEquals("foo == bar?10:20", pretty("foo==bar?10:20"));
    	assertEquals("public boolean process(Set<? extends B> a) {println a}", pretty("public boolean process(Set<? extends B> a) {println a}"));
    	assertEquals("public boolean process(Set<? extends B, ? super C> a) {println a}", pretty("public boolean process(Set<? extends B, ? super C> a) {println a}"));
    }
    public void testRangeExclusive() throws Exception {
        assertEquals("foo[45..<89]", pretty("foo[45...89]")); // todo remove this '...' usage from groovy.g
        assertEquals("foo[45..<89]", pretty("foo[45 ..< 89]"));
    }
    public void testRangeInclusive() throws Exception {
        assertEquals("foo[bar..12]", pretty("foo[bar .. 12]"));
    }
    public void testRegexpLiteral() throws Exception {
    	assertEquals("println", pretty("println //")); // empty regexp_literal should be treated as single line comment
    }

    public void testRegexpLiteral_FAILS() throws Exception { if (notYetImplemented()) return;
		//todo: these fail because regexp_literals are converted into string_literals on the antlr AST
		assertEquals("def x = /./", pretty("def x = /./"));
		assertEquals("def z = /blah\\s/", pretty("def z = /blah\\s/")); // actually: def z = /blah\s/
    }
    
    public void testRegexFind() throws Exception {
    	assertEquals("def m = foo =~ \"bar\"", pretty("def m = foo =~ \"bar\""));
    	assertEquals("if (foo =~ \"bar\") {}", pretty("if (foo=~\"bar\"){}"));
    }
    
    public void testRegexMatch() throws Exception {
    	assertEquals("if (foo ==~ \"bar\") {}", pretty("if (foo==~\"bar\"){}"));
    }
    
    public void testScopeEscape() throws Exception {
    	// todo - 31 July + 14 Dec 2006 - test fine, however this parses but causes error in AntlrParserPlugin
    	assertEquals("println([$x, x, y])", pretty("println([$x, x, y])"));
    }
    
    public void testSelectSlot() throws Exception {
    	assertEquals("def x = foo.@bar", pretty("def x = foo . @ bar"));
    }
    
    public void testSl() throws Exception {
    	assertEquals("foo << 123", pretty("foo << 123"));
    }
    
    public void testSlAssign() throws Exception {
    	assertEquals("foo <<= 123", pretty("foo <<= 123")); // does this operator make any sense?
    }

    public void testSlist() throws Exception {
    	assertEquals("class Foo {private Foo() {println bar}}",pretty("class Foo {private Foo() {println bar}}"));
    	assertEquals("if (true) {foo}", pretty("if (true) {foo}"));
    	assertEquals("def x = foo.{bar}", pretty("def x = foo.{bar}")); // todo - inline open block is great, but it doesn't work as one would expect (yet). (c.f. with)
    	assertEquals("def foo() {l:{x = 2}}", pretty("def foo(){l:{x=2}}")); // slist inside a method body (needed label to distinguish from a closure)
    	assertEquals("switch (f) {case 1:break }", pretty("switch(f){case 1:break}")); // slist inside each case body...
    } 
    
    public void testSpreadArg() throws Exception {
    	assertEquals("myList {*name}", pretty("myList{*name}"));
        assertEquals("\"foo$*bar\"", pretty("\"foo$*bar\"")); // this doesn't work beyond parser (AntlrParserPlugin)
        assertEquals("\"foo${*bar}\"", pretty("\"foo${*bar}\"")); // this doesn't work beyond parser (AntlrParserPlugin)
        assertEquals("f(*[a, b, c])", pretty("f(*[a,b,c])"));
        assertEquals("f(*null)", pretty("f(*null)")); // equiv to f()
    }
    public void testSpreadMapArg() throws Exception {
    	assertEquals("f(*:myMap)", pretty("f(*:myMap)"));
    }
    public void testSr() throws Exception {
    	assertEquals("foo >> 123", pretty("foo >> 123"));
    }
    
    public void testSrAssign() throws Exception {
    	assertEquals("foo >>= 123", pretty("foo >>= 123")); // does this operator make any sense?
    }

    public void testStar() throws Exception {
        assertEquals("import foo.*", pretty("import foo.*"));
        assertEquals("a*b", pretty("a*b"));
    }
    
    public void testStarAssign() throws Exception {
    	assertEquals("foo *= 123", pretty("foo *= 123"));
    }

    public void testStarStar() throws Exception {
        assertEquals("def square = +5**2", pretty("def square=+5**2"));
        assertEquals("def cube = 5**3", pretty("def cube = 5**3"));
    } 
    
    public void testStarStarAssign() throws Exception {
    	assertEquals("cubeMe **= 3", pretty("cubeMe **= 3"));
    }

    public void testStaticImport() throws Exception {
    	assertEquals("import static foo.Bar.mooky", pretty("import static foo.Bar.mooky"));
    	assertEquals("import static foo.Bar.*", pretty("import static foo.Bar.*"));
    }

    public void testStaticInit() throws Exception {
    	assertEquals("class Foo {static {println(1 + 1)}}", pretty("class Foo{static{println(1+1)}}"));
    }
    
    public void testStrictFp() throws Exception {
    	assertEquals("private strictfp flibble = 1.2", pretty("private strictfp flibble = 1.2"));
    }
    
    public void testStringConstructor() throws Exception{
        assertEquals("def x = \"foo$bar\"", pretty("def x=\"foo$bar\""));
        assertEquals("def y = \"foo${mooky}\"", pretty("def y = \"foo${mooky}\""));
    }

    public void testStringLiteral_FAILS() throws Exception{ if (notYetImplemented()) return;
        assertEquals("\"mooky\"", pretty("\"mooky\""));
        assertEquals("'mooky'", pretty("'mooky'"));
    	assertEquals("def x = '''can go over newline'''", pretty("def x = '''can go over newline'''"));
    	assertEquals("def x = \"\"\"can go over newline\"\"\"", pretty("def x = \"\"\"can go over newline\"\"\""));
    	//todo test newlines inside strings somehow...
    }

    public void testSuperCtorCall() throws Exception{
    	assertEquals("class Foo {Foo(int x) {super(12, 3)}}",pretty("class Foo{Foo(int x) {super(12, 3)}}"));
    	assertEquals("class Foo {Foo( x) {super()}}",pretty("class Foo{Foo(x) {super()}}"));
        // todo: above is not quite the spacing I would expect, but good enough for now...
    	// todo not yet implemented in parser: assertEquals("(new Outer()).super()", pretty("(new Outer()).super()"));
    }
    
    public void testType() throws Exception {
    	assertEquals("def bar", pretty("def bar"));
        assertEquals("public bar", pretty("public bar"));
        assertEquals("public String bar", pretty("public String bar"));
        assertEquals("String bar", pretty("String bar"));
    }

    public void testTypecast() throws Exception {
        assertEquals("foo = (int)bar", pretty("foo = (int)bar"));
        assertEquals("foo = (int[])bar", pretty("foo = (int[])bar"));
        assertEquals("foo = (String)bar", pretty("foo = (String)bar"));
        assertEquals("foo = (String[])bar", pretty("foo = (String[])bar"));
    }

    public void testTypeArguments() throws Exception {
    	assertEquals("void printCollection(Collection<?> c) {}", pretty("void printCollection(Collection<?> c) {}"));
    }
    public void testTypeLowerBounds() throws Exception {
    	assertEquals("void printCollection(Collection<? super X> c) {}", pretty("void printCollection(Collection<? super X> c) {}"));
    }
    public void testTypeUpperBounds() throws Exception {
    	assertEquals("void printCollection(Collection<? extends X> c) {}", pretty("void printCollection(Collection<? extends X> c) {}"));
    }

    public void testTypeParameters() throws Exception {
    	assertEquals("class Foo<T extends C & I> {T t}",pretty("class Foo<T extends C & I> {T t}"));
    }
    public void testUnaryMinus() throws Exception {
        assertEquals("def x = -3", pretty("def x= -3"));
    }
    public void testUnaryPlus() throws Exception {
        assertEquals("def x = +2", pretty("def x= +2"));
    }

    public void testVariableDef() throws Exception {
        assertEquals("def x = 1", pretty("def x = 1"));
        assertEquals("int y = 2", pretty("int y = 2"));
        assertEquals("String y", pretty("String y"));
        assertEquals("def foo() {int b = 9}", pretty("def foo(){int b = 9}"));
    }
    public void testVariableDef_FAILS() throws Exception { if (notYetImplemented()) return;
    	assertEquals("boolean x, y, z = false", pretty("boolean x,y,z = false"));       
    }
    
    public void testVaribleParameterDef() throws Exception {
    	assertEquals("void myMethod(String param1, String ... others) {}", pretty("void myMethod(String param1, String ... others) {}"));
    	assertEquals("void myMethod(final int ... others) {}", pretty("void myMethod(final int ... others) {}"));
    	assertEquals("void myMethod(def ... others) {}", pretty("void myMethod(def ... others) {}"));
    }
    
    public void testWildcardType() throws Exception {
    	assertEquals("public boolean process(Set<? extends TypeElement> annotations) {println annotations}", pretty("public boolean process(Set<? extends TypeElement> annotations) {println annotations}"));
    }

    public String pretty(String input) throws Exception{
        GroovyRecognizer parser = null;
        SourceBuffer sourceBuffer = new SourceBuffer();
        UnicodeEscapingReader unicodeReader = new UnicodeEscapingReader(new StringReader(input),sourceBuffer);
        GroovyLexer lexer = new GroovyLexer(unicodeReader);
        unicodeReader.setLexer(lexer);
        parser = GroovyRecognizer.make(lexer);
        parser.setSourceBuffer(sourceBuffer);

        String[] tokenNames;
        tokenNames = parser.getTokenNames();
        parser.compilationUnit();
        AST ast = parser.getAST();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Visitor visitor = new SourcePrinter(new PrintStream(baos),tokenNames,false);
        AntlrASTProcessor traverser = new SourceCodeTraversal(visitor);

        traverser.process(ast);

        return new String(baos.toByteArray());
    }

}
