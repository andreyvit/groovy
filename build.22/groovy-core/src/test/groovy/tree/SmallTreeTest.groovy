class SmallTreeTest extends GroovyTestCase {
    
    def b

    void testTree() {
        b = NodeBuilder.newInstance()
        
        def root = b.root1( {
            elem1('hello1')
        })
        
        assert root != null
        
        println(root)
    }
}