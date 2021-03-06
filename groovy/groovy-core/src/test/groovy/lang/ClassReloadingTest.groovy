package groovy.lang

class ClassReloadingTest extends GroovyTestCase {

	public void testRealoding() {
		def file = File.createTempFile("TestReload",".groovy", new File("target"))
		file.deleteOnExit()
		def className = file.name-".groovy"

		def cl = new GroovyClassLoader(this.class.classLoader);
		def currentDir = file.parentFile.absolutePath
		cl.addClasspath(currentDir)
		cl.shouldRecompile = true
			
		
        try {
     		file.write """
    		  class $className {
    		    def hello = "hello"
    		  }
    		  """
    		def groovyClass = cl.loadClass(className,true,false)
    		def object = groovyClass.newInstance()
    		assert "hello"== object.hello

            sleep 1000
    					
    		// change class
    		file.write """
    		  class $className {
    		    def hello = "goodbye"
    		  }
    		  """
    		file.lastModified = System.currentTimeMillis()
    		
    		// reload		
    		groovyClass = cl.loadClass(className,true,false)
    		object  = groovyClass.newInstance()
    		assert "goodbye" == object.hello
    	} finally {
		  file.delete()
		}
	}
}