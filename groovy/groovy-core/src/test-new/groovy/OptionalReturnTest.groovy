class OptionalReturnTest extends GroovyTestCase {

	def y
	
    void testSingleExpression() {
        value = foo()
		
        assert value == 'fooReturn'
    }

    void testLastExpressionIsSimple() {
        value = bar()
        
        assert value == 'barReturn'
    }

    void testLastExpressionIsBooleanExpression() {
        value = foo2()
        
        assert value

        value = foo3()
        
        assert value == false
    }

    void testLastExpressionIsAssignment() {
        value = assign()
        
        assert value == 'assignReturn'
        
        value = assignField()
        
        assert value == 'assignFieldReturn'
    }

    void testLastExpressionIsMethodCall() {
        value = methodCall()
        
        assert value == 'fooReturn'
    }

    void testEmptyExpression() {
        value = nullReturn()
        
        assert value == null
    }

//  now is  a compile time error
//    void testVoidMethod() {
//        value = voidMethod()
//
//        assert value == null
//    }

    void testNonAssignmentLastExpressions() {
        value = lastIsAssert()
        
        assert value == null
    }

    def foo() {
        'fooReturn'
    }	
	
    def bar() {
        x = 'barReturn'
        x
    }
	
    def foo2() {
        x = 'cheese'
        x == 'cheese'
    }
	
    def foo3() {
        x = 'cheese'
        x == 'edam'
    }
	
    def assign() {
        x = 'assignReturn'
    }
	
    def assignField() {
        y = 'assignFieldReturn'
    }
    
    def nullReturn() {
    }

    def lastIsAssert() {
        assert 1 == 1
    }

    def methodCall() {
        foo()
    }
    
    void voidMethod() {
        foo()
    }
}