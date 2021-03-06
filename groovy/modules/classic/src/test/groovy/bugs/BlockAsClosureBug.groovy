/**
 * @version $Revision$
 */
class BlockAsClosureBug extends GroovyTestCase {
    
   void testBug() {
        c = 0 
        
        block: { 
            c = 9 
        } 

        println(c) 
        
        assert c == 9
    }
    
    void testStaticBug() {
        main(null)		
    }
    
    void testNonVoidMethod() {
        foo()		
    }
    
    static void main(args) {
        c = 0 
        
        block: {
            c = 9 
        }

        println(c) 
        
        assert c == 9
    }
    
    foo() {
        c = 0 
        
        block: { 
            c = 9 
        } 
        println(c) 
        
        assert c == 9
        return 5
    }
   }
