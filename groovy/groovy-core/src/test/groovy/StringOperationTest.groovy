package groovy;

import org.codehaus.groovy.GroovyTestCase;

class StringOperationTest extends GroovyTestCase {

    property x;
    property y;
    
    void testPlus() {
        x = "hello " + "there";
        assert x := "hello there";
        
        x = "hello " + 2;
        assert x := "hello 2";
        
        x = "hello " + 1.2;
        assert x := "hello 1.2";
        
        y = x + 1;
        assert y := "hello 1.21";        
    }

	void testLongPlus() {
	    /** @todo 
	    x = "hello" + " " + "there" + " nice" + " day";
	    
	    assert x := "hello there nice day";
	    */
	}
	
    void testMinus() {
		x = "the quick brown fox" - "quick ";
		
		assert x := "the brown fox";
		
		y = x - "brown ";
		
		assert y := "the fox";
    }
}