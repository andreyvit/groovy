import groovy.inspect.InspectorTest;
import groovy.lang.*;
import groovy.security.SecurityTest;
import groovy.security.SignedJarTest;
import groovy.servlet.GroovyServletTest;
import groovy.text.TemplateTest;
import groovy.text.XmlTemplateEngineTest;
import groovy.tree.NodePrinterTest;
import groovy.util.EvalTest;
import groovy.util.MBeanTest;
import groovy.xml.XmlTest;
import groovy.xml.FactorySupportTest;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * All Java Unit tests in the 'groovy' dir
 */

public class AllGroovyJavaTestsSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(InspectorTest.class);
        suite.addTestSuite(GroovyShellTest.class);
        suite.addTestSuite(GStringTest.class);
        suite.addTestSuite(IntRangeTest.class);
        suite.addTestSuite(MetaClassTest.class);
        suite.addTestSuite(RangeTest.class);
        suite.addTestSuite(ScriptIntegerDivideTest.class);
        suite.addTestSuite(ScriptPrintTest.class);
        suite.addTestSuite(ScriptTest.class);
        suite.addTestSuite(SequenceTest.class);
        suite.addTestSuite(TupleTest.class);
        suite.addTestSuite(SecurityTest.class);
        suite.addTestSuite(SignedJarTest.class);
        suite.addTestSuite(GroovyServletTest.class);
        suite.addTestSuite(TemplateTest.class);
        suite.addTestSuite(XmlTemplateEngineTest.class);
        suite.addTestSuite(NodePrinterTest.class);
        suite.addTestSuite(EvalTest.class);
        suite.addTestSuite(MBeanTest.class);
        suite.addTestSuite(XmlTest.class);
        suite.addTestSuite(FactorySupportTest.class);

        return suite;
    }
}
