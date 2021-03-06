import org.codehaus.groovy.classgen.TestSupport

/**
 * @version $Revision$
 */
class VariableScopingBug extends TestSupport {
    
    void testBug() {
    	// undeclared variable x
    	
    	shouldFail {
			for (z in 0..2) {
	    		x = makeCollection()
			}
		   	
		   	for (t in 0..3) {
		    	for (y in x) {
		    		println x
		    	}
		   	}
	   	}
	}

    void testVariableReuse() {
		for (z in 0..2) {
    		x = makeCollection()
		}
	   	
	   	for (t in 0..3) {
    	    x = 123
    		println x
	   	}
	}

	protected makeCollection() {
		return [1, 2, 3]
	}   
}