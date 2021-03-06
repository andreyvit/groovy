package groovy.xml

/**
 * This test uses the concise syntax to test the building of 
 * textual markup (XML or HTML) using GroovyMarkup
 */
class MarkupTest extends TestXmlSupport {
    
    void testSmallTree() {
        def b = new MarkupBuilder()
        
        b.root1(a:5, b:7) {
            elem1('hello1')
            elem2('hello2')
            elem3(x:7)
        }
    }
    
    void testTree() {
        def b = new MarkupBuilder()
        
        b.root2(a:5, b:7) {
            elem1('hello1')
            elem2('hello2')
            nestedElem(x:'abc', y:'def') {
                child(z:'def')
                child2()  
            }
            
            nestedElem2(z:'zzz') {
                child(z:'def')
                child2("hello")  
            }
        }
    }

    void testContentAndDataInMarkup() {
        def b = new MarkupBuilder()

        b.a(href:"http://groovy.codehaus.org", "groovy")
    }

    void testMarkupWithColonsAndNamespaces() {
        def mkp = new groovy.xml.MarkupBuilder()

        mkp."ns1:customer-description"{
            "last-name"("Laforge")
            "first-name"{
                first("Guillaume")
                "initial-letters"("A.J.")
            }
        }
    }
    
    void testObjectOperationsInMarkup() {
        def doc = new StreamingMarkupBuilder().bind {
          root {
            (1..3).each {
             item() 
            }
          }
        }
        
        assert doc.toString() == "<root><item/><item/><item/></root>"    
    }
}