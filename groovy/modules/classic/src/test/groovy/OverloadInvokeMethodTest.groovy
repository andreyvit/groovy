/**
 * @version $Revision$
 */
class OverloadInvokeMethodTest extends GroovyTestCase {
    
    void testBug() {
        value = foo(123)
        assert value == 246
    }

    /**
     * Lets overload the invokeMethod() mechanism to provide an alias
     * to an existing method
     */
    invokeMethod(String name, Object args) {
        try {
            return metaClass.invokeMethod(this, name, args)
        }
        catch (MissingMethodException e) {
            if (name == 'foo') {
                return metaClass.invokeMethod(this, 'bar', args)
            }
            else {
                throw e
            }
        }
    }
    
    bar(param) {
        return param * 2
    }

}