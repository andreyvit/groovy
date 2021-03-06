class DefaultParamClosureTest extends GroovyTestCase {

    void testDefaultParameters() {
    /** @todo get default parameters working for closures 
		doSomething = { | a, b = 'defB', c = 'defC' |
			println "Called with a: ${a}, b ${b}, c ${c}"
			
			return a + "-" + b + "-" + c
		}
	
    	value = doSomething("X", "Y", "Z")
    	assert value == "X-Y-Z"
    	
    	value = doSomething("X", "Y")
    	assert value == "X-Y-defC"
    	
    	value = doSomething("X")
    	assert value == "X-defB-defC"
    	
    	shouldFail { doSomething() }
     */
    }

    void testDefaultTypedParameters() {
		/** @todo parser to handle typed parameters 
		
		doTypedSomething = { | String a = 'defA', String b = 'defB', String c = 'defC' |
			println "Called typed method with a: ${a}, b ${b}, c ${c}"
			
			return a + "-" + b + "-" + c
		}
	
    	value = doTypedSomething("X", "Y", "Z")
    	assert value == "X-Y-Z"
    	
    	value = doTypedSomething("X", "Y")
    	assert value == "X-Y-defC"
    	
    	value = doTypedSomething("X")
    	assert value == "X-defB-defC"
    	
    	value = doTypedSomething()
    	assert value == "defA-defB-defC"
	*/
    }

}