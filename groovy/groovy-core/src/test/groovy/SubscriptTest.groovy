class SubscriptTest extends GroovyTestCase {

    void testListRange() {
        def list = ['a', 'b', 'c', 'd', 'e']

        def sub = list[2..4]
        assert sub == ['c', 'd', 'e']
        
        sub = list[2...5]
        assert sub == ['c', 'd', 'e']
        
        def value = list[-1]
        assert value == 'e'
        
        sub = list[-4..-2]
        assert sub == ['b', 'c', 'd']
        
        // backwards ranges
        sub = list[-1..-3]
        assert sub == ['e', 'd', 'c']
        
        sub = list[-3..-1]
        assert sub == ['c', 'd', 'e']
        
        sub = list[3..1]
        assert sub == ['d', 'c', 'b']
        
        sub = list[1..-3]
        assert sub == ['b', 'c']
    }
    
    void testObjectRangeRange() {
        def list = 'a'..'e'
        
        def sub = list[2..4]
        assert sub == ['c', 'd', 'e']
        
        def value = list[-1]
        assert value == 'e'
        
        sub = list[-4..-2]
        assert sub == ['b', 'c', 'd']
        
        // backwards ranges
        sub = list[-1..-3]
        assert sub == ['e', 'd', 'c']
        
        sub = list[3..1]
        assert sub == ['d', 'c', 'b']
    }
    
    void testStringArrayRange() {
        String[] list = ['a', 'b', 'c', 'd', 'e']
        
        def sub = list[2..4]
        assert sub == ['c', 'd', 'e']
        
        def value = list[-1]
        assert value == 'e'
        
        sub = list[-4..-2]
        assert sub == ['b', 'c', 'd']
        
        // backwards ranges
        sub = list[-1..-3]
        assert sub == ['e', 'd', 'c']
        
        sub = list[3..1]
        assert sub == ['d', 'c', 'b']
    }
    
    void testIntRangeRange() {
        def list = 10..15
        
        def sub = list[2..4]
        assert sub == [12, 13, 14]
        
        def value = list[-1]
        assert value == 15
        
        sub = list[-4..-2]
        assert sub == [12, 13, 14]
        
        // backwards ranges
        sub = list[-1..-3]
        assert sub == [15, 14, 13]
        
        sub = list[3..1]
        assert sub == [13, 12, 11]
    }
    
    void testIntArrayRange() {
        Integer[] list = [ 10, 11, 12, 13, 14, 15 ]
        
        def sub = list[2..4]
        assert sub == [12, 13, 14]
        
        def value = list[-1]
        assert value == 15
        
        sub = list[-4..-2]
        assert sub == [12, 13, 14]
        
        // backwards ranges
        sub = list[-1..-3]
        assert sub == [15, 14, 13]
        
        sub = list[3..1]
        assert sub == [13, 12, 11]
    }
    
    void testStringSubscript() {
        def text = "nice cheese gromit!"
        
        def x = text[2]
        
        assert x == "c"
        assert x.class == String
        
        def sub = text[5..10]
        assert sub == 'cheese'
        
        sub = text[10..5]
        assert sub == 'eseehc'
        
        sub = text[-2..-7]
        assert sub == 'timorg'
        
        sub = text[1..-3]
        assert sub == "ice cheese gromi"
        
    }

    void testStringPutAtRange(){
        def text = "0123"
    }
    
    void testListSubscriptWithList() {
        def list = ['a', 'b', 'c', 'd', 'e']
        
        def indices = [0, 2, 4]
        def sub = list[indices]
        assert sub == ['a', 'c', 'e']
        
        // verbose but valid
        sub = list[[1, 3]]
        assert sub == ['b', 'd']
     
        // syntax sugar
        sub = list[2, 4]
        assert sub == ['c', 'e']
    }
    
    
    void testListSubscriptWithListAndRange() {
        def list = 100..200

        def sub = list[1, 3, 20..25, 33]
        assert sub == [101, 103, 120, 121, 122, 123, 124, 125, 133]

        // now lets try it on an array
        def array = list.toArray()

        sub = array[1, 3, 20..25, 33]
        assert sub == [101, 103, 120, 121, 122, 123, 124, 125, 133]
    }

    void testStringWithSubscriptList() {

        def text = "nice cheese gromit!"
        
        def sub = text[1, 2, 3, 5..10]
        
        assert sub == "icecheese"
    }
    
    void testSubMap() {
        def map = ['a':123, 'b':456, 'c':789]
        
        def keys = ['b', 'a']
        def sub = map.subMap(keys)
        
        assert sub.size() == 2
        assert sub['a'] == 123
        assert sub['b'] == 456
        assert ! sub.containsKey('c')
    }
    
    void testListWithinAListSyntax() {
        def list = [1, 2, 3, 4..10, 5, 6]
        
        assert list.size() == 6
        def sublist = list[3]
        assert sublist == 4..10
        assert sublist == [4, 5, 6, 7, 8, 9, 10]
    }


    void testBeanProperties() {
        def foo = new Foo()

        foo['name'] = 'Gromit'

        assert foo.name == 'Gromit'

        def value = foo['name']
        assert value == 'Gromit'
    }
}
