package groovy.bugs

class SubscriptOnPrimitiveTypeArrayBug extends TestSupport {
    int[] ia;  // type is not necessary
    int i1;

    void testBug() {
        def array = getIntArray() // this function returns [I, true primitive array
        
        def value = array[2]
        
        assert value == 3
        
        array[2] = 8

        value = array[2]
        assert value == 8
        
        // lets test a range
        def range = array[1..2]
        
        assert range == [2, 8]
    }

    void testGroovyIntArray() {
        int[] ia = [1, 2]
        int[] ia1 = ia; // type is not necessary
        def i1 = ia1[0]
        int i2 = i1
        assert i2 == 1
    }
}