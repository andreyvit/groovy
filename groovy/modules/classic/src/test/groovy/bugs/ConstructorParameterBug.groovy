package groovy.bugs

class ConstructorParameterBug extends GroovyTestCase {

    void testMethodWithNativeArray() {
        value = new int[2*2]
        println "${value} of type ${value.class}"
        /** @todo fixme!
    	blah2(value)
    	*/
    }

    blah2(int[] wobble) {
       println(wobble)
    }

}
