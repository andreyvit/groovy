import groovy.bugs.TestSupport

/**
 * @version $Revision$
 */
class IterateOverCustomTypeBug extends TestSupport {
    
    void testBug() {
        object = this
        
        answer = []
        for (i in object) {
            answer << i
        }
        assert answer == ['a', 'b', 'c']
        
        answer = []
        object.each { answer << it }
        assert answer == ['a', 'b', 'c']
    }
}