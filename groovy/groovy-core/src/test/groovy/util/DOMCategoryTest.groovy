package groovy.util

import groovy.xml.DOMBuilder
import groovy.xml.dom.DOMCategory

class DOMCategoryTest extends GroovyTestCase {
    
    void testDomCategory() {
        def text = """
<characters>
    <character id="1" name="Wallace">
    	<likes>cheese</likes>
    </character>
    <character id="2" name="Gromit">
	    <likes>sleep</likes>
    </character>
</characters>
"""
        def reader = new StringReader(text)
        def doc    = DOMBuilder.parse(reader)
        def root   = doc.documentElement

        def node = root.getElementsByTagName('character').item(0)

        use(DOMCategory){
            assert 2 == root.character.size()
            assert 2 == root.likes.size()
            assert 'character' == node.nodeName
            assert 'character' == node.name()
            assert 'likes'     == node[1].nodeName
            assert node        == node[1].parent()
            assert 'cheese'    == node.likes[0].firstChild.nodeValue
            assert 'cheese'    == node.likes[0].text()
            assert 'Wallace'   == node.'@name'
            assert ['Wallace', 'Gromit'] == root.'character'.'@name'
            assert ['cheese', 'sleep']   == root.likes.iterator().collect{ it.text() }
            assert ['sleep']   == root.likes.iterator().findAll{ it.text().startsWith('s') }.collect{ it.text() }
            assert root.likes.iterator().every{ it.text().contains('ee') }
        }

    }
}
